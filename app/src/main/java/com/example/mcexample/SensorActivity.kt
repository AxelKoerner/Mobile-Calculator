package com.example.mcexample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class SensorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorScreen()
        }
    }
}

@Composable
fun SensorScreen() {
    val context = LocalContext.current
    var period by remember { mutableStateOf("10000") }
    var threshold by remember { mutableStateOf("5.0") }

    Column(Modifier.padding(16.dp)) {
        Text("Gyroskop & GPS Sensorwerte", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = period,
            onValueChange = { period = it },
            label = { Text("Periodendauer (ms)") }
        )
        OutlinedTextField(
            value = threshold,
            onValueChange = { threshold = it },
            label = { Text("Threshold") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(context, SensorService::class.java).apply {
                putExtra("period", period.toLongOrNull() ?: 10000L)
                putExtra("threshold", threshold.toFloatOrNull() ?: 5f)
            }
            ContextCompat.startForegroundService(context, intent)
        }) {
            Text("Service starten")
        }
    }
}