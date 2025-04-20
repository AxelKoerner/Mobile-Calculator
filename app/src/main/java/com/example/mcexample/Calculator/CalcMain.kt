package com.example.mcexample

import android.content.Context
import java.io.File
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp

class CalcMain : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // We created this McBaseLayout in an extra file to reuse it among different activities
            // Using string resources instead of hardcoded strings in the app is good style for
            //  Android programming.
            McBaseLayout(title = stringResource(R.string.app_name)) {
                    innerPadding ->
                // Our content is defined in its own Composable function.
                CalcContent(Modifier.padding(innerPadding))
            }
        }
    }

    // Showcase lifecycle functions
    override fun onPause() {
        super.onPause()
    }
}

@Composable
fun CalcContent(modifier: Modifier) {
    val context = LocalContext.current

    val buttons = listOf(
        listOf("7", "8", "9", "C"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "/"),
        listOf("0", "+", "-", "="),
        listOf("(", ")")
    )
    var input by remember { mutableStateOf("") }

    val history = remember{ mutableListOf<String>() }

    var resetInput by remember { mutableStateOf(false)}

    // Showcase Lifecycle events
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        Toast.makeText(context, "OnCreate", Toast.LENGTH_SHORT).show()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        Toast.makeText(context, "OnResume", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(4f)
            .padding(20.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            ),
        ) {
            Text(text = input, fontSize = 28.sp)
        }
        Column(
            modifier = Modifier
                .weight(4f)
                .padding(vertical = 10.dp, horizontal = 20.dp),
        ) {
            for (row in buttons) {
                Row() {
                    for (button in row) {
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                when (button) {
                                    "C" -> {
                                        input = ""
                                    }

                                    "=" -> {
                                        if (resetInput) {
                                            input = "";
                                            resetInput = false
                                        } else if (input.isNotEmpty()) {
                                            resetInput = true
                                            try {
                                                history += input
                                                val result = calculateResult(input, context)
                                                history += result
                                                input = "= $result"
                                            } catch (e: ArithmeticException) {
                                                Toast.makeText(
                                                    context,
                                                    e.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                input = "Error"
                                            }
                                        }
                                    }

                                    ")" -> {
                                        //only allow closing parentheses if there is an opening parentheses
                                        if (!areParenthesesBalanced(input)) {
                                            input += button
                                        }
                                    }

                                    else -> {
                                        if (resetInput) input = ""; resetInput = false
                                        //prevents the user from entering two operators consecutively.
                                        //stops Inputs like "++", "**", "*+".
                                        if (!(isOperator(button) && input.isNotEmpty() && charIsOperator(
                                                input.last()
                                            ))
                                        ) {
                                            input += button
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(3f)
                        ) {
                            Text(text = button)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier
            .weight(1f)
            .padding(vertical = 5.dp, horizontal = 20.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                showHistory(history)
                input = history.joinToString("\n") //TODO remove
            }) {
                Text("Show History")
            }
            Button(onClick = {
                exportHistory(history)
            }) {
                Text("Export History")
            }
        }
    }

}
fun areParenthesesBalanced(input: String): Boolean {
    var count = 0
    for (char in input) {
        if (char == '(') count++
        if (char == ')') count--
        if (count < 0) return false
    }
    return count == 0
}

fun isOperator(character: String): Boolean {
    return character in arrayOf("+", "-", "*", "/");
}

fun charIsOperator(char: Char): Boolean {
    return char in arrayOf('+', '-', '*', '/')
}

fun charIsParentheses(char: Char): Boolean {
    return char in arrayOf('(', ')')
}

fun calculateResult(userInput: String, context: Context): String {
    var result = userInput

    if (!areParenthesesBalanced(result)){
        Toast.makeText(context, "Invalid Input: parentheses error", Toast.LENGTH_SHORT).show()
        return "Invalid Input: " + result
    }

    val tokens = tokenizeResult(result)

    if (tokens.isNotEmpty()){
        if (tokens.size < 3){
            Toast.makeText(context, "Invalid Input: To few arguments error", Toast.LENGTH_SHORT).show()
            return "Invalid Input: " + result
        }else{
            //expression should not begin or end with an operator
            if (isOperator(tokens.first()) || isOperator((tokens.last()))){
                Toast.makeText(context, "Invalid Input: Missing argument error", Toast.LENGTH_SHORT).show()
                return "Invalid Input: " + result
            }
        }
        // every parentheses should have and operator connecting it with other tokens
        for (i in tokens.indices) {
            if (tokens[i] == "(" && i != 0){
                if (!isOperator(tokens[i-1])){
                    Toast.makeText(context, "Invalid Input: Missing operator error", Toast.LENGTH_SHORT).show()
                    return "Invalid Input: " + result
                }
            }
            if (tokens[i] == ")" && i != tokens.size-1){
                if (!isOperator(tokens[i+1])){
                    Toast.makeText(context, "Invalid Input: Missing operator error", Toast.LENGTH_SHORT).show()
                    return "Invalid Input: " + result
                }
            }
        }
    }
    result = calculateExpression(tokens)

    return result
}

fun calculateExpression(tokens: List<String>): String {
    val mutableTokens = tokens.toMutableList()
    while (mutableTokens.contains("(")) {
        var openIndex = -1
        var closeIndex = -1

        // get most inner parentheses
        for (i in mutableTokens.indices) {
            if (mutableTokens[i] == "(") {
                openIndex = i
            } else if (mutableTokens[i] == ")") {
                closeIndex = i
                break
            }
        }

        //recursive call with tokens in parentheses
        if ((closeIndex - openIndex) > 1){
            val innerTokens = mutableTokens.subList(openIndex + 1, closeIndex)
            val result = calculateExpression(innerTokens.toList())

            //replace parentheses with result
            for (j in 0..(closeIndex - openIndex)) {
                mutableTokens.removeAt(openIndex)
            }
            mutableTokens.add(openIndex, result)
        }
        //Parentheses are empty
        else {
            throw ArithmeticException("Empty parentheses")
        }

    }
    //evaluate * and / first
    while (mutableTokens.contains("*") || mutableTokens.contains("/")) {
        for (i in mutableTokens.indices) {
            val token = mutableTokens[i]
            if (token == "*" || token == "/") {
                val arg1 = mutableTokens[i - 1].toDouble()
                val arg2 = mutableTokens[i + 1].toDouble()
                if (token == "/" && arg2.toInt() == 0){
                    throw ArithmeticException("Division by zero")
                }
                val result = if (token == "*") arg1 * arg2 else arg1 / arg2

                //delete old tokens and add new result to list
                mutableTokens.removeAt(i + 1)
                mutableTokens.removeAt(i)
                mutableTokens[i - 1] = result.toString()

                break
            }
        }
    }
    //evaluate + and -
    while (mutableTokens.contains("+") || mutableTokens.contains("-")) {
        for (i in mutableTokens.indices) {
            val token = mutableTokens[i]
            if (token == "+" || token == "-") {
                val arg1 = mutableTokens[i - 1].toDouble()
                val arg2 = mutableTokens[i + 1].toDouble()
                val result = if (token == "+") arg1 + arg2 else arg1 - arg2

                mutableTokens.removeAt(i + 1)
                mutableTokens.removeAt(i)
                mutableTokens[i - 1] = result.toString()

                break
            }
        }
    }
    return mutableTokens.first()
}


fun tokenizeResult(userInput: String): List<String> {
    val tokens = mutableListOf<String>()
    var currentNumber = ""

    for (char in userInput) {
        when {
            char.isDigit() -> {
                currentNumber += char
            }

            charIsOperator(char) || charIsParentheses(char) -> {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber)
                    currentNumber = ""
                }
                tokens.add(char.toString())
            }
        }
    }

    if (currentNumber.isNotEmpty()) {
        tokens.add(currentNumber)
    }

    return tokens

}

fun showHistory(history: List<String>) {
    for((index, result) in history.withIndex()) {
        println("Result $index: $result")
    }
}
//https://how.dev/answers/how-to-write-to-a-file-in-kotlin
fun exportHistory(history: List<String>) {
    val historyFile = File("history.txt")
    for(result in history) {
        historyFile.writeText(result)
    }
}