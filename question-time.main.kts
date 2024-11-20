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