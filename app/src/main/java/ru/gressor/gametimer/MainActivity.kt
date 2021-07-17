package ru.gressor.gametimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}