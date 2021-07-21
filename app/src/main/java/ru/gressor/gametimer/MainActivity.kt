package ru.gressor.gametimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.gressor.gametimer.TimerForegroundService.Companion.COMMAND_ID
import ru.gressor.gametimer.TimerForegroundService.Companion.COMMAND_START
import ru.gressor.gametimer.TimerForegroundService.Companion.STARTED_TIMER_TIME_MS
import ru.gressor.gametimer.databinding.ActivityMainBinding
import ru.gressor.gametimer.ui.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainer.id, MainFragment())
                .commit()
        }
    }

    override fun onStop() {
        val startIntent = Intent(this, TimerForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, 0L)
        startService(startIntent)

        super.onStop()
    }
}