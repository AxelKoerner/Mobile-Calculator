package com.example.mcexample

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    val scope = rememberCoroutineScope()

    val buttons = listOf(
        listOf("7", "8", "9", "C"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "/"),
        listOf("0", "+", "-", "=")
    )
    var input by remember { mutableStateOf("") }

    val history = mutableListOf<String>()

    // Showcase Lifecycle events
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        Toast.makeText(context, "OnCreate", Toast.LENGTH_SHORT).show()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        Toast.makeText(context, "OnResume", Toast.LENGTH_SHORT).show()
    }

    Column(modifier = modifier) {
        Row(modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(20.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            Text(text = input, fontSize = 28.sp)
        }
        Column(modifier = modifier) {

            for (row in buttons) {
                Row() {
                    for(button in row) {
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                when(button) {
                                    "C" -> {
                                        input = ""
                                        println(input)
                                    }
                                    "=" -> {
                                        history += calculateResult(input)
                                        input = ""
                                    }
                                    else -> {
                                        input += button
                                        println(input)
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(3f)
                        ) {
                            Text(text = button)
                        }
                    }
                }

            }
        }

        Row(modifier = modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                showHistory(history)
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

fun calculateResult(userInput: String): String {
    var result = userInput
    println("result")
    return result
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