import khoury.*
import kotlin.math.*

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
    // Checks if a given tag is present in the question's tags
    
    fun format(): String {
        return "$question|$answer|${tags.joinToString(",")}"
    }
    // Formats the question, answer, and tags into a single string with a specific format.
}

val q1 = TaggedQuestion("What is the capital of Massachusetts?", "Boston", listOf("easy", "Geography"))

val q2 = TaggedQuestion("What is the square root of 16?", "4", listOf("easy", "Math"))

val q3 = TaggedQuestion("What year was the Declaration of Independence signed?", "1776", listOf("medium", "History"))

// Step 2
// Converts a formatted string to an object TaggedQuestion
fun stringToTaggedQuestion(str: String): TaggedQuestion {
    val parts = str.split("|")
    val question = parts[0]
    val answer = parts[1]
    val tags = parts[2].split(",").map { it.trim() }
    return TaggedQuestion(question, answer, tags)
}

// Reads a list of TaggedQuestion from a file, converting each line using stringToTaggedQuestion.
fun readTaggedQuestionBank(path: String): List<TaggedQuestion> {
    return if (fileExists(path)) {
        fileReadAsList(path).map { stringToTaggedQuestion(it) }
    } else {
        emptyList()
    }
}

// step 3
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


class ListBasedQuestionBank(private val questions: List<TaggedQuestion>, private val currentState: QuestionBankState = QuestionBankState.QUESTIONING, private val index: Int = 0): IQuestionBank{
  override fun getState(): QuestionBankState = currentState // returns correct state


  override fun getText():String?{ // returns question or answer
    if(currentState==QuestionBankState.COMPLETED)
      return null
    else if (currentState==QuestionBankState.QUESTIONING)
      return questions[index].Q
    else
      return questions[index].A
    }
  }
  
  override fun getSize():Int = questions.size // returns number of questions


  override fun show():IQuestionBank{ // transitions from question to answer
    return if(currentState == QuestionBankState.QUESTIONING){
      ListBasedQuestionBank(questions, QuestionBankState.ANSWERING, index)
    }else this
  } 


  override fun next(correct: Boolean): IQuestionBank{ // proccess answer and moves to next question
    return if(currentState == QuestionBankState.ANSWERING){
      if(correct){/// got it correct
        if(index +1>=questions.size){
          ListBasedQuestionBank(questions, QuestionBankState.COMPLETED)
        }else{
          val updatedQuestions = questions.toMutableList()
          updatedQuestions.removeAt(index)
          ListBasedQuestionBank(updatedQuestions, QuestionBankState.QUESTIONING, index)
        }
      }else{//got it wrong
        val updatedQuestions = questions.toMutableList()
        updatedQuestions.add(updatedQuestions.removeAt(index))// adds question to end to retry
        ListBasedQuestionBank(updatedQuestions, QuestionBankState.QUESTIONING, index)
      }
    }else this
  }
}

class AutoGeneratedQuestionBank(private val genFunc:(Int) -> TaggedQuestion, private val sequence: List<Int>, private val currentState: QuestionBankState = if(sequence.isEmpty()) QuestionBankState.COMPLETED else QuestionBankState.QUESTIONING): IQuestionBank{
  override fun getState(): QuestionBankState = currentState // returns current state


  override fun getText():String?{ // returns question or answer
    if(currentState==QuestionBankState.COMPLETED)
      return null
    else if (currentState==QuestionBankState.QUESTIONING)
      return genFunc(sequence[0]).Q
    else
      return genFunc(sequence[0]).A

  }


  override fun getSize(): Int = sequence.size // returns number of questioned 

  
  fun next(correct: Boolean): IQuestionBank = // process answer and move to next question
    takeIf { currentState == QuestionBankState.ANSWERING }?.let { 
        processNextQuestion(correct)
    } ?: this

private fun processNextQuestion(correct: Boolean): IQuestionBank { // updates the sequence of questions based on answer
    val updatedSequence = sequence.drop(1)
    
    return when {
        updatedSequence.isNotEmpty() -> 
            AutoGeneratedQuestionBank(genFunc, updatedSequence, QuestionBankState.QUESTIONING)
        else -> 
            AutoGeneratedQuestionBank(genFunc, emptyList(), QuestionBankState.COMPLETED)
    }
}


  fun next(correct: Boolean): IQuestionBank = // process response
    takeIf { currentState == QuestionBankState.ANSWERING }?.let { 
        processNextQuestion(correct)
    } ?: this

private fun processNextQuestion(correct: Boolean): IQuestionBank {
    val updatedSequence = sequence.drop(1)
    
    return when {
        updatedSequence.isNotEmpty() -> 
            AutoGeneratedQuestionBank(genFunc, updatedSequence, QuestionBankState.QUESTIONING)
        else -> 
            AutoGeneratedQuestionBank(genFunc, emptyList(), QuestionBankState.COMPLETED)
    }
}


    return if(updatedSequence.isEmpty()){
      AutoGeneratedQuestionBank(genFunc, updatedSequence, QuestionBankState.COMPLETED)
    }else{
      AutoGeneratedQuestionBank(genFunc, updatedSequence, QuestionBankState.QUESTIONING)
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

fun demonstrateChooseMenu() { // displays menu and handles the user's inputs 
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

// Step 5

val dataset: List<LabeledExample<String, Boolean>> = // provides data set of labeled responses 
listOf(
/* Some positive examples */
LabeledExample("yes", true),
LabeledExample("y", true),
LabeledExample("indeed", true),
LabeledExample("aye", true),
LabeledExample("oh yes", true),
LabeledExample("affirmative", true),
LabeledExample("roger", true),
LabeledExample("uh huh", true),
LabeledExample("true", true),
/* Some negative examples */
LabeledExample("no", false),
LabeledExample("n", false),
LabeledExample("nope", false),
LabeledExample("negative", false),
LabeledExample("nay", false),
LabeledExample("negatory", false),
LabeledExample("uh uh", false),
LabeledExample("absolutely not", false),
LabeledExample("false", false),
)

//5.1 - Find Closest - finds top item in list

data class LabeledExample<E, L>(val example: E, val label: L)

fun <T> topK(itemList: List<T>, metrFun: (T, T)-> Int, k: Int): List<T> 
{
    return itemList.sortedByAscending{metrFun(it.first.example, it.second)}.drop(itemList.size-k)
    return itemList.map{it -> (it.first, metrFun(it.first.example, it.second))}.drop(itemList.size-1)
}


//5.2 - Levenshtein Distance - Calculates the Levenshtein distance between two strings.

fun levenshteinDistance(word1: String, word2: String): Int{
    var count = 0
    val diff = abs(word2.length - word1.length)
    count += diff

    val little

    if(word1.length>word2.length)
        little = word2
    else
        little = word1
    
    for(i in 0..little.length-1)
    {
        if(word2[i]!=word1[i])
            count++
    }
    return count
}

//5.3 - k Nearest Neighbor

typealias DistanceFunction<T> = (T, T) -> Int
data class ResultWithVotes<L>(val label: L, val votes: Int)
/**
* Uses k-NN to predict the label for a supplied query
* given a labelled data set and a distance function.
*/
fun <E, L> nnLabel(query: E, dataset: List<LabeledExample<E, L>>, distFn: DistanceFunction<E>, k: Int): ResultWithVotes<L> 
{
    val modData = dataset.map{i -> pair(i, query)}
    val topK = topK(modData, ::levenshteinDistance, 3)
    val countList = topK.groupingBy{it.first.label}.eachCount()

    var max = 0
    for(i in countList)
    {
        if(i.value>max)
            max = i.value
    }
    val finalLabel = countList.entries.find{it.value==maxVotes}
    return ResultWithVotes(finalLabel.key, max)


}
// uses dataset to classify query if no dataset is fast 
fun classifier(query: String, dataset: List<LabeledExample<String,Boolean>>):ResultWithVotes<Boolean?>{
  for(i in dataset){
    if(query == i.example){
      return ResultWithVotes(i.label, 1)
    }
  }


  return nnLabel(query, dataset, ::levenshteinDistance, 3)

// Step 6
//6.1
data class StudyQuestionBankResult(
    val questions: Int,
    val attempts: Int
)

fun studyQuestionBank( // manages study session using react console
    questionBank: List<TaggedQuestion>,
    classifier: (String) -> ResultWithVotes<Boolean>
): StudyQuestionBankResult {
    var attempts = 0
    var correctAnswers = 0
    val totalQuestions = questionBank.size
    var currentIndex = 0

    val initialState = QuestionBankState.QUESTIONING

    fun stateToText(state: QuestionBankState): String {
        return if (state == QuestionBankState.QUESTIONING) {
            questionBank[currentIndex].question
        } else {
            "Correct ((y)es/(n)o)?"
        }
    }

    fun nextState(state: QuestionBankState, input: String): QuestionBankState {
        if (state == QuestionBankState.QUESTIONING) {
            println(questionBank[currentIndex].answer)
            return QuestionBankState.ANSWERING
        } else {
            val result = classifier(input)
            attempts++
            if (result.label) {
                correctAnswers++
                currentIndex++
                if (currentIndex == totalQuestions) {
                    return QuestionBankState.DONE
                }
            }
            return QuestionBankState.QUESTIONING
        }
    }

    val finalState = reactConsole(initialState, ::stateToText, ::nextState) { it == QuestionBankState.DONE }

    return StudyQuestionBankResult(totalQuestions, attempts)
}

// 6.2
fun study() { // manages overall process such as selecting banks, classifiers, and starting session
    val questionBanks = listOf(
        NamedMenuOption("Read from file", "Read from file"),
        NamedMenuOption("AutoGeneratedQuestionBank", "AutoGeneratedQuestionBank"),
        NamedMenuOption("Filtered by tag", "Filtered by tag")
    )

    val sentimentClassifiers = listOf(
        NamedMenuOption("Naive classifier", "Naive classifier"),
        NamedMenuOption("Advanced classifier", "Advanced classifier")
    )

    while (true) {
        val selectedBankOption = chooseMenu(questionBanks) ?: break

        val questionBank = when (selectedBankOption.option) {
            "Read from file" -> readTaggedQuestionBank("questions.txt")
            "AutoGeneratedQuestionBank" -> generateAutoGeneratedQuestionBank()
            "Filtered by tag" -> readTaggedQuestionBank("questions.txt").filter { it.taggedAs("hard") }
            else -> emptyList()
        }

        val selectedClassifierOption = chooseMenu(sentimentClassifiers) ?: break

        val classifier = when (selectedClassifierOption.option) {
            "Naive classifier" -> { input: String -> ResultWithVotes(input.startsWith("y", ignoreCase = true), 1) }
            "Advanced classifier" -> { input: String -> classifier(input, dataset) }
            else -> { _: String -> ResultWithVotes(false, 1) }
        }

        val result = studyQuestionBank(questionBank, classifier)
        println("Bye. Questions: ${result.questions}, Attempts: ${result.attempts}")
    }
}

// generates an autogenerated bank 
fun generateAutoGeneratedQuestionBank(): List<TaggedQuestion> {
    return listOf(
        TaggedQuestion("What is the capital of France?", "Paris", listOf("Geography")),
        TaggedQuestion("What is 11*7?", "77", listOf("Math"))
    )
}