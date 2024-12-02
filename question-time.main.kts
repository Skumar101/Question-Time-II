import khoury.*

data class Question(val q: String, val a: String)

data class QuestionBank(val name: String, val questions: List<Question>)

// Hand generated questions
val q1 = Question("When did the Berlin Wall fall?", "1989")
val q2 = Question("What is the chemical symbol for gold?", "Au")
val q3 = Question("How many bones are in the human body?", "206")
val manualBank = QuestionBank("School Questions", listOf(q1, q2, q3))

//Creates a question bank regarding integers cubed until the specified limit
fun cubes(count: Int): QuestionBank{
    val ql = mutableListOf<Question>()
    fun helper(count: Int): Question{
        val que = "What is "+count+" cubed?"
        val ans = count*count*count
        return Question(que, ans.toString())
    }
    for(i in 1..count)
    {
        ql.add(helper(i))
    }
    return QuestionBank("Cubes", ql)
}
//Turns a question into string form
fun questionToString(quest: Question): String
{
    return  quest.q +"|"+quest.a
}
//takes a string and formats it into the data type Question
fun stringToQuestion(str: String): Question{
    val list: List<String> = str.split("|")
    return Question(list[0], list[1])
}

//reads a file given the path and creates a QuestionBank with the questions and answers in the file
fun readQuestionBank(filePath: String): QuestionBank{
    if(fileExists(filePath))
    {
        val fileList = fileReadAsList(filePath)
        val questionList = fileList.map(::stringToQuestion)
        return QuestionBank(filePath.slice(0..filePath.length-5), questionList)
    }
    return QuestionBank("", emptyList())
}

//checks if a response from the user begins with y
fun isCorrect(response: String): Boolean{
    return response.uppercase().startsWith("Y")
}

//Single Question
data class QuestionState(val quest: Question, val count: Int, val isDone: Boolean)

//returns the question
fun stateToText(state: QuestionState): String{
    
    return state.quest.q

}
//asks if user got the question right and if so adds one to the total count, if not then it doesnt
fun transitionState(state: QuestionState, input: String): QuestionState{
    println("Answer: "+state.quest.a)
    println("Did you get it right? (y/n)")
    if(isCorrect(input()))
        return QuestionState(Question(state.quest.q, state.quest.a), state.count+1, true)
    else
        return QuestionState(Question(state.quest.q, state.quest.a), state.count, true)
}

//termination condition
fun terminate(state: QuestionState): Boolean{
    return state.isDone
}
//final message - returns how many questions the user got correct
fun message(state: QuestionState): String{
    return "Wow! You got ${state.count} question(s) correct!"
}

//puts everything together using react console for the single question interface
fun studyQuestion(state:QuestionState){
        reactConsole(
        state,
        ::stateToText,
        ::transitionState,
        ::terminate,
        ::message
    )
}

//Multiple Questions

data class QuestionBankState(val bank: QuestionBank, val qNow: String, val aNow: String, val correct: Int, val isDone: Boolean)

//returns the current question 
fun stateToText2(state: QuestionBankState): String{
    
    return "\n" + state.qNow

}

//adds to the count if user got the question right and fulfills the termination condition if the user is on the last question
fun transition(state: QuestionBankState, input: String): QuestionBankState {
    fun printIt(word: String){println(word)}
    printIt("The answer is "+state.aNow)
    printIt("Did you answer correctly?(y/n)")
    val resp = input()
    if(state.bank.questions.size==1)
    {
        if (isCorrect(resp)) {return QuestionBankState(QuestionBank(state.bank.name, state.bank.questions.drop(1)), 
        "", 
        "", 
        state.correct + 1, 
        true)} 
        else return QuestionBankState(QuestionBank(state.bank.name, state.bank.questions.drop(1)),
        "",
        "",
        state.correct,
        true)
        }
    else{   
        
            
            if (isCorrect(resp)) 
            { // question correct
            return QuestionBankState(
                QuestionBank(state.bank.name, state.bank.questions.drop(1)),
                state.bank.questions[1].q,
                state.bank.questions[1].a,
                state.correct + 1,
                false,
            )
            } // question incorrect
            else return QuestionBankState(
                QuestionBank(state.bank.name, 
                state.bank.questions.drop(1)),
                state.bank.questions[1].q, 
                state.bank.questions[1].a, 
                state.correct, 
                false,
            )
        }
    
}

//termination condition
fun terminate2(state: QuestionBankState): Boolean{
    return state.isDone
}
//returns how many question the user got correct
fun message2(state: QuestionBankState): String{
    return "Wow! You got ${state.correct} question(s) correct!"
}
//puts everything together using react console
fun studyQuestionBank(state:QuestionBankState){
        reactConsole(
        state,
        ::stateToText2,
        ::transition,
        ::terminate2,
        ::message2
    )
}
//gives the user their options for question banks and returns the chosen questionBank
fun chooseBank(bankBank: List<QuestionBank>): QuestionBank{
    println("Welcome to Question Time! You can choose from "+bankBank.size+" question banks")

    for(i in 1..bankBank.size){
        println(i.toString()+". "+bankBank[i-1].name)
    }
    println("Enter your choice: ")
    val choice = input()
    if(isAnInteger(choice))
    {
        if(choice.toInt()>0 && choice.toInt()<bankBank.size+10)
            return bankBank[choice.toInt()-1]
    }
        println("Not a valid choice")
        return chooseBank(bankBank)//recursively loop it until they give a proper response
}

//comprehensively allows the user to utilize the multiple question react console
fun play() {
    val bankBank = listOf(manualBank, readQuestionBank("Testing.txt"), cubes(3))
    val choice = chooseBank(bankBank)
    val initialState = QuestionBankState(choice, choice.questions[0].q, choice.questions[0].a, 0, false)
    studyQuestionBank(initialState)
}
@EnabledTest
fun testAll() {//tests all the functions to make sure everything is running as intended and errors are catched
    //Cubes
    testSame(cubes(2), QuestionBank("Cubes", listOf(Question(q="What is 1 cubed?", a="1"), Question(q="What is 2 cubed?", a="8"))), "Cubes Test")
    //questionToString
    testSame(questionToString(Question(q="When did the Berlin Wall fall?", a="1989")), "When did the Berlin Wall fall?|1989", "questionToString test")
    //stringToQuestion
    testSame(stringToQuestion("When did the Berlin Wall fall?|1989"), Question(q="When did the Berlin Wall fall?", a="1989"),"stringToQuestion Test")
    //readQuestionBank
    testSame(readQuestionBank("Testing.txt"), QuestionBank("Testing", listOf(Question(q="What are we testing?", a="Files"), Question(q="Why are we testing them?", a="For class"), Question(q="What class is this for?", a="Fundies"))),"readQuestionBank - file")
    testSame(readQuestionBank("blahblahblah.txt"), QuestionBank("", emptyList()),"readQuestionBank - no file")
    //isCorrect
    testSame(isCorrect("yes"),true,"isCorrect True case")
    testSame(isCorrect("no"),false,"isCorrect False case")
    //stateToText - single question
    testSame(stateToText(QuestionState(quest=Question(q="What is 1 cubed?", a="1"), count=0, isDone=false)),"What is 1 cubed?" ,"stateToText - Single Question")
    val banks = listOf(manualBank, readQuestionBank("Testing.txt") ,cubes(3))
    println("Choose a number corresponding to your desired question Bank")
    testSame(//tests hand done questions
        "${chooseBank(banks)}",
        "QuestionBank(name=School Questions, questions=[Question(q=When did the Berlin Wall fall?, a=1989), Question(q=What is the chemical symbol for gold?, a=Au), Question(q=How many bones are in the human body?, a=206)])",
        "Choose bank Function - 1",
    )
    testSame(//tests file questions
        "${chooseBank(banks)}",
        "QuestionBank(name=Testing, questions=[Question(q=What are we testing?, a=Files), Question(q=Why are we testing them?, a=For class), Question(q=What class is this for?, a=Fundies)])",
        "Choose bank Function - 2",
    )
    
    testSame(//tests cube questions
        "${chooseBank(banks)}",
        "QuestionBank(name=Cubes, questions=[Question(q=What is 1 cubed?, a=1), Question(q=What is 2 cubed?, a=8), Question(q=What is 3 cubed?, a=27)])",
        "Choose bank Function - 3",
    )
}
play()
runEnabledTests(this)

// Pt2 of Question Time
// Step 1
data class TaggedQuestion(
    val question: String,
    val answer: String,
    val tags: List<String>
) {
    fun taggedAs(tag: String): Boolean {
        return tags.contains(tag)
    }
    
    fun format(): String {
        return "$question|$answer|${tags.joinToString(",")}"
    }
}

val q1 = TaggedQuestion("What is the capital of Massachusetts?", "Boston", listOf("easy", "Geography"))

val q2 = TaggedQuestion("What is the square root of 16?", "4", listOf("easy", "Math"))

val q3 = TaggedQuestion("What year was the Declaration of Independence signed?", "1776", listOf("medium", "History"))

// Step 2
fun stringToTaggedQuestion(str: String): TaggedQuestion {
    val parts = str.split("|")
    val question = parts[0]
    val answer = parts[1]
    val tags = parts[2].split(",").map { it.trim() }
    return TaggedQuestion(question, answer, tags)
}

fun readTaggedQuestionBank(path: String): List<TaggedQuestion> {
    return if (fileExists(path)) {
        fileReadAsList(path).map { stringToTaggedQuestion(it) }
    } else {
        emptyList()
    }
}

// step 3

//*NEED TO CHANGE UP BC ITS FROM ANOTHER GROUP*

/**
* The bank is either completed,
* showing a question or showing
* an answer
*/
enum class QuestionBankState { COMPLETED, QUESTIONING, ANSWERING }
/**
* Basic functionality of any question bank
*/
interface IQuestionBank {
  /**
  * Returns the state of a question bank.
  */
  fun getState(): QuestionBankState
  /**
  * Returns the currently visible text (or null if completed).
  */
  fun getText(): String?
  /**
  * Returns the number of question-answer pairs.
  * (Size does not change when a question is put
  * to the end of the question bank.)
  */
  fun getSize(): Int
  /**
  * Shifts from question to answer. If not QUESTIONING,
  * returns the same IQuestionBank.
  */
  fun show(): IQuestionBank
  /**
  * Shifts from an answer to the next question (or completion).
  * If the current question was answered correctly, it discards
  * it. Otherwise it cycles the question to the end.
  *
  * If not ANSWERING, returns the same IQuestionBank.
  */
  fun next(correct: Boolean): IQuestionBank
}


class ListBasedQuestionBank(private val questions: List<TaggedQuestion>, private val currentState: QuestionBankState = QuestionBankState.QUESTIONING, private val currIndex: Int = 0): IQuestionBank{
  override fun getState(): QuestionBankState = currentState


  override fun getText():String?{
    return when(currentState){//returns current state
      QuestionBankState.COMPLETED -> null
      QuestionBankState.QUESTIONING -> questions[currIndex].Q
      QuestionBankState.ANSWERING ->  questions[currIndex].A
    }
  }
  
  override fun getSize():Int = questions.size


  override fun show():IQuestionBank{
    return if(currentState == QuestionBankState.QUESTIONING){
      ListBasedQuestionBank(questions, QuestionBankState.ANSWERING, currIndex)
    }else this
  } 


  override fun next(correct: Boolean): IQuestionBank{
    return if(currentState == QuestionBankState.ANSWERING){
      if(correct){/// got it correct
        if(currIndex +1>=questions.size){
          ListBasedQuestionBank(questions, QuestionBankState.COMPLETED)
        }else{
          val updatedQuestions = questions.toMutableList()
          updatedQuestions.removeAt(currIndex)
          ListBasedQuestionBank(updatedQuestions, QuestionBankState.QUESTIONING, currIndex)
        }
      }else{//got it wrong
        val updatedQuestions = questions.toMutableList()
        updatedQuestions.add(updatedQuestions.removeAt(currIndex))// adds question to end to retry
        ListBasedQuestionBank(updatedQuestions, QuestionBankState.QUESTIONING, currIndex)
      }
    }else this
  }
}




class AutoGeneratedQuestionBank(private val generator:(Int) -> TaggedQuestion, private val sequence: List<Int>, private val currentState: QuestionBankState = if(sequence.isEmpty()) QuestionBankState.COMPLETED else QuestionBankState.QUESTIONING): IQuestionBank{
  override fun getState(): QuestionBankState = currentState


  override fun getText():String?{
    return when(currentState){//returns current state
      QuestionBankState.COMPLETED -> null
      QuestionBankState.QUESTIONING -> generator(sequence[0]).Q
      QuestionBankState.ANSWERING ->  generator(sequence[0]).A
    }
  }


  override fun getSize(): Int = sequence.size


  override fun show():IQuestionBank{
    return if(currentState == QuestionBankState.QUESTIONING && sequence.isNotEmpty()){
      AutoGeneratedQuestionBank(generator, sequence, QuestionBankState.ANSWERING)
    }else this
  }


  override fun next(correct: Boolean): IQuestionBank{
    if(currentState != QuestionBankState.ANSWERING ) return this
    
    val updatedSequence = if(correct){
      sequence.drop(1)// remove question
    }else{
      sequence.drop(1) + sequence[0] // moves question to end
    }


    return if(updatedSequence.isEmpty()){
      AutoGeneratedQuestionBank(generator, updatedSequence, QuestionBankState.COMPLETED)
    }else{
      AutoGeneratedQuestionBank(generator, updatedSequence, QuestionBankState.QUESTIONING)
    }
  }
}

// Step 4
interface IMenuOption {
    fun getTitle(): String
}

/**
 * A menu option with a single value and name.
 */
data class NamedMenuOption<T>(
    val option: T, 
    val title: String
) : IMenuOption {
    override fun getTitle(): String = title
}

fun <T : IMenuOption> chooseMenu(options: List<T>): T? {
    while (true) {
        println("Please choose an option:")
        println("0. Quit")
        for ((index, option) in options.withIndex()) {
            println("${index + 1}. ${option.getTitle()}")
        }

        val input = readLine()?.trim() ?: continue
        
        val choice = input.toIntOrNull()
        if (choice == 0) {
            return null
        } else if (choice != null && choice in 1..options.size) {
            return options[choice - 1]
        }
        
        println("Invalid selection. Please try again.")
    }
}

fun demonstrateChooseMenu() {
    val options = listOf(
        NamedMenuOption(1, "Option 1"),
        NamedMenuOption(2, "Option 2"),
        NamedMenuOption(3, "Option 3")
    )
    val selectedOption = chooseMenu(options)
    if (selectedOption != null) {
        println("You selected: ${selectedOption.getTitle()}")
    } else {
        println("You chose to quit.")
    }
}