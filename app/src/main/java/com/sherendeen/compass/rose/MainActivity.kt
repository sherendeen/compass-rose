package com.sherendeen.compass.rose

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

import com.sherendeen.compass.rose.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(LocatyService.KEY_ON_SENSOR_CHANGED_ACTION)
        )

    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 1
            val direction = intent.getStringExtra(LocatyService.KEY_DIRECTION)
            val angle = intent.getDoubleExtra(LocatyService.KEY_ANGLE, 0.0)
            val angleWithDirection = "$angle  $direction"
            binding.directionTextView.text = angleWithDirection
            // 2
            binding.compassImageView.rotation = angle.toFloat() * -1
        }
    }


    override fun onResume() {
        super.onResume()

        startForegroundServiceForSensors(false)
    }

    private fun startForegroundServiceForSensors(background: Boolean) {

        val locatyIntent = Intent(this, LocatyService::class.java)
        locatyIntent.putExtra(LocatyService.KEY_BACKGROUND, background)

        ContextCompat.startForegroundService(this, locatyIntent)

    }

    override fun onPause() {
        super.onPause()
        startForegroundServiceForSensors(true)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

}