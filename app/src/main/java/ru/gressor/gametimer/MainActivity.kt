package ru.gressor.gametimer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.gressor.gametimer.TimerForegroundService.Companion.COMMAND_ID
import ru.gressor.gametimer.TimerForegroundService.Companion.COMMAND_START
import ru.gressor.gametimer.TimerForegroundService.Companion.TIMER_FROM_ACTIVITY_ID
import ru.gressor.gametimer.TimerForegroundService.Companion.TIMER_FROM_ACTIVITY_NAME
import ru.gressor.gametimer.TimerForegroundService.Companion.TIMER_FROM_ACTIVITY_VALUE
import ru.gressor.gametimer.databinding.ActivityMainBinding
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.ui.MainFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var activeTimer: ActiveTimer? = null

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
        activeTimer?.let {
            val startIntent = Intent(this, TimerForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(TIMER_FROM_ACTIVITY_VALUE, it.ticker.flow.value)
            startIntent.putExtra(TIMER_FROM_ACTIVITY_ID, it.id.toString())
            startIntent.putExtra(TIMER_FROM_ACTIVITY_NAME, it.name)
            startService(startIntent)
        }

        super.onStop()
    }
}