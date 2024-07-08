package com.example.madcamp_week2

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(private val listener: OnShakeListener) : SensorEventListener {

    interface OnShakeListener {
        fun onShake()
    }

    private var shakeThreshold = 2.7f
    private var shakeTimeStamp: Long = 0
    private var shakeTimeInterval = 500

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

            if (gForce > shakeThreshold) {
                val now = System.currentTimeMillis()
                if (shakeTimeStamp + shakeTimeInterval > now) {
                    return
                }
                shakeTimeStamp = now
                listener.onShake()
            }
        }
    }
}
