package com.example.mcexample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult

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
    var thresholdGps by remember { mutableStateOf("5.0") }
    var thresholdGyro by remember { mutableStateOf("3.0") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val serviceIntent = Intent(context, SensorService::class.java).apply {
                putExtra("period", period.toLongOrNull() ?: 10000L)
                putExtra("thresholdGps", thresholdGps.toFloatOrNull() ?: 5f)
                putExtra("thresholdGyro", thresholdGyro.toFloatOrNull() ?: 5f)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            println("Permission denied")
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Gyroskop & GPS Sensorwerte", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = period,
            onValueChange = { period = it },
            label = { Text("Periodendauer (ms)") }
        )
        OutlinedTextField(
            value = thresholdGps,
            onValueChange = { thresholdGps = it },
            label = { Text("Threshold_GPS") }
        )
        OutlinedTextField(
            value = thresholdGyro,
            onValueChange = { thresholdGyro = it },
            label = { Text("Threshold_GYRO") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                val serviceIntent = Intent(context, SensorService::class.java).apply {
                    putExtra("period", period.toLongOrNull() ?: 10000L)
                    putExtra("thresholdGps", thresholdGps.toFloatOrNull() ?: 5f)
                    putExtra("thresholdGyro", thresholdGyro.toFloatOrNull() ?: 5f)
                }
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }) {
            Text("Service starten")
        }
    }
}