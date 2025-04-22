package com.example.mcexample

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.*
import android.location.*
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.math.sqrt

class SensorService : Service(), SensorEventListener, LocationListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    private var gyroscopeThreshold = 5f
    private var updatePeriod = 10000L

    private var timer: Timer? = null
    private var lastLocation: Location? = null
    private var lastGyro: FloatArray? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updatePeriod = intent?.getLongExtra("period", 10000L) ?: 10000L
        gyroscopeThreshold = intent?.getFloatExtra("threshold", 5f) ?: 5f

        startForegroundServiceWithNotification()
        registerSensors()
        startLocationUpdates()
        startTimer()

        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        val channel = NotificationChannel(
            "sensor_channel",
            "Sensor Überwachung",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, "sensor_channel")
            .setContentTitle("Sensor läuft")
            .setContentText("Überwacht Sensoren...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    private fun registerSensors() {
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private fun startTimer() {
        runnable = object : Runnable {
            override fun run() {
                checkGyroscopeThreshold()
                handler.postDelayed(this, updatePeriod)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    private fun checkGyroscopeThreshold() {
        lastGyro?.let {
            val magnitude = sqrt(it[0]*it[0] + it[1]*it[1] + it[2]*it[2])
            if (magnitude > gyroscopeThreshold) {
                sendThresholdBroadcast("Gyroskop überschreitet Schwelle: $magnitude")
            }
        }
    }

    private fun sendThresholdBroadcast(message: String) {
        val intent = Intent("com.example.THRESHOLD_EXCEEDED").setClassName(
            packageName,
            "com.example.mcexample.SensorBroadcastReceiver"
        )
        intent.putExtra("message", message)
        sendBroadcast(intent)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            lastGyro = event.values
        }
    }

    override fun onLocationChanged(location: Location) {
        lastLocation?.let {
            val distance = location.distanceTo(it)
            if (distance > gyroscopeThreshold) {
                sendThresholdBroadcast("GPS Bewegung: ${distance}m")
            }
        }
        lastLocation = location
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null
}