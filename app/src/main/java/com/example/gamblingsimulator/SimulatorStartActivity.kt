package com.example.gamblingsimulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gamblingsimulator.databinding.ActivitySimulatorStartBinding

class SimulatorStartActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySimulatorStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySimulatorStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonStartBlackjack.setOnClickListener {
            val currentActivity = this@SimulatorStartActivity
            val gameIntent = Intent(currentActivity, BlackjackGameActivity::class.java)
            currentActivity.startActivity(gameIntent)
        }

        binding.buttonStartSlots.setOnClickListener {
            val currentActivity = this@SimulatorStartActivity
            val gameIntent = Intent(currentActivity, SlotsGameActivity::class.java)
            currentActivity.startActivity(gameIntent)
        }
        binding.buttonStartRoulette.setOnClickListener {
            val currentActivity = this@SimulatorStartActivity
            val gameIntent = Intent(currentActivity, RouletteGameActivity::class.java)
            currentActivity.startActivity(gameIntent)
        }
    }
}