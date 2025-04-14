package com.example.mcexample

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // We created this McBaseLayout in an extra file to reuse it among different activities
            McBaseLayout(title = stringResource(R.string.title_activity_second)) {
                innerPadding ->
                // Our content is defined in its own Composable function.
                SecondContent(Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun SecondContent(modifier: Modifier) {
    Column(modifier=modifier) {
        val context = LocalContext.current
        var color by remember { mutableStateOf(Color(Random.nextInt())) }

        Button(colors = ButtonDefaults.buttonColors(containerColor = color), onClick = {
            color = Color(Random.nextInt())
        }) {
            Text("Random Button")
        }

        var service: IExampleService? by remember { mutableStateOf(null) }
        val connection = remember {
            object : ServiceConnection {
                // Called when the connection with the service is established.
                override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                    // Following the preceding example for an AIDL interface,
                    // this gets an instance of the IRemoteInterface, which we can use to call on the service.
                    service = IExampleService.Stub.asInterface(binder)
                }

                // Called when the connection with the service disconnects unexpectedly.
                override fun onServiceDisconnected(className: ComponentName) {
                    service = null
                }
            }
        }

        // Unbind from service when activity is stopped.
        // Service persists as long as at least one activity is bound to the service.
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            context.unbindService(connection)
            service = null
        }

        Button(onClick = {
            if (service == null) {
                val intent = Intent(context, ExampleService::class.java)
                context.bindService(intent, connection, BIND_AUTO_CREATE)
            } else {
                context.unbindService(connection)
                service = null
            }
        }) {
            Text(if(service == null) "Bind Service" else "Unbind Service")
        }

        // The UI will freeze during this task
        Button(enabled = service != null, onClick = {
            service?.shortWork(3)
        }) {
            Text("Start short running task")
        }

        var complexTaskResult by remember { mutableStateOf<Int?>(null) }

        val coroutineScope = rememberCoroutineScope()

        // This calculation will not freeze the UI
        Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                // Imagine some heavy IO code here
                complexTaskResult = complexCalculation(3)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Result is $complexTaskResult", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Complex Calculation")
        }

        complexTaskResult?.also { result ->
            Text("Result is $result")
        }
    }
}

suspend fun complexCalculation(a: Int): Int = withContext(Dispatchers.Default) {
    // Heavy CPU work
    Thread.sleep(a * 1000L)
    return@withContext Random.nextInt(100)
}
