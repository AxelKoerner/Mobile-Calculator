package com.example.mcexample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExampleService : Service() {
    private var calculations = 0

    private val binder = object : IExampleService.Stub() {
        override fun shortWork(seconds: Int) {
            Thread.sleep(seconds * 1000L)
            Toast.makeText(this@ExampleService, "Short work finished (${calculations++})", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(this, "Bound Service", Toast.LENGTH_SHORT).show();
        return binder
    }
}