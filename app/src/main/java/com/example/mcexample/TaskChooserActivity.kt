package com.example.mcexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class TaskChooserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            McBaseLayout(title = stringResource(R.string.app_name)) { innerPadding ->
                Greeting(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            val intent = Intent(context, CalcMain::class.java)
            // Optional: Put some data using intent.putExtra(...)
            context.startActivity(intent)
            Toast.makeText(context, "Open calc", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Open " + stringResource(R.string.title_activity_calc))
        }
        Button(onClick = {
            val intent = Intent(context, SensorActivity::class.java)
            // Optional: Put some data using intent.putExtra(...)
            context.startActivity(intent)
            Toast.makeText(context, "open sensor", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Open " + stringResource(R.string.title_activity_sensors))
        }
    }
}