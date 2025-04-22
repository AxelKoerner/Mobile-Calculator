package com.example.mcexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SensorBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: return
        //the Toast is not being displayed when the App is closed or in the background, atleast on my emulated device.
        // I get this error in "Logcat: Suppressing toast from package com.example.mcexample by user request." Sadly I did not find the solution to displaying these messages
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}