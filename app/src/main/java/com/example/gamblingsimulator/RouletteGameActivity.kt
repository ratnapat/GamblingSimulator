package com.example.gamblingsimulator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gamblingsimulator.databinding.ActivityRouletteGameBinding
import kotlin.random.Random


class RouletteGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRouletteGameBinding
    var you = Player(1000, 0)
    var sectors = mutableListOf<Sector>()
    var isBetRed = false
    var isBetBlack = false
    var win = false
    var currentRotation = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRouletteGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.money.text = "Money: $${you.money}"

        initializeSectors()

        betOnRed()
        betOnBlack()
        roullete()
    }

    fun initializeSectors() {
        val sectorNames = listOf(
            "0", "28", "9", "26", "30", "11", "7", "20", "32", "17", "5", "22", "34", "15",
            "3", "24", "36", "13", "1", "00", "27", "10", "25", "29", "12", "8", "19", "31",
            "18", "6", "21", "33", "16", "4", "23", "35", "14", "2"
        )
        val redNumbers = listOf(
            9.0, 30.0, 7.0, 32.0, 5.0, 34.0, 3.0, 36.0, 1.0, 27.0, 25.0, 12.0, 19.0, 18.0,
            21.0, 16.0, 23.0, 14.0
        )
        for (i in 0 until sectorNames.size) {
            val name = sectorNames[i]
            val number = name.toDouble()
            val isRed = redNumbers.contains(number)
            val sectorSize = 360.0 / 38.0
            val sectorStartAngle = i * sectorSize
            sectors.add(Sector(name, sectorStartAngle, isRed))
        }
    }

    fun betOnRed(){
        binding.redMainButton.setOnClickListener{
            isBetRed = true
            Toast.makeText(this, "You've chosen red", Toast.LENGTH_SHORT).show()
            binding.money.text = "Money: $${you.money}"
        }
    }

    fun betOnBlack(){
        binding.blackMainButton.setOnClickListener{
            isBetBlack = true
            Toast.makeText(this, "You've chosen black", Toast.LENGTH_SHORT).show()
            binding.money.text = "Money: $${you.money}"
        }
    }

    // https://developer.android.com/reference/android/view/animation/RotateAnimation
    fun roullete(){
        binding.playMainButton.setOnClickListener{
            var randomValue = Random.nextInt(0, 38).toDouble()
            var sector = sectors[randomValue.toInt()]
            var time = Random.nextInt(4000, 6000)
            var rotation = (currentRotation + 2160 + randomValue / 38.0 * 360).toFloat()
            binding.imageMainAdobe.rotation = (5 * 360 + randomValue / 38 * 360).toFloat()
            val anim = RotateAnimation(
                currentRotation, rotation,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
            )
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.repeatCount = 0
            anim.duration = time.toLong()
            binding.imageMainAdobe.setAnimation(anim)
            binding.imageMainAdobe.startAnimation(anim)
            binding.imageMainAdobe.rotation = rotation
            currentRotation = rotation % 360
            binding.buttonMainReset.setOnClickListener()
            {
                currentRotation = (0).toFloat()
                binding.imageMainAdobe.rotation = currentRotation
                isBetBlack = false
                isBetRed = false
            }
            binding.root.postDelayed({ checkWinCondition(sector) }, time.toLong()) }
        }


    fun checkWinCondition(sector: Sector) {
        win = false

        if (isBetRed && sector.isRed) win = true
        else if (isBetBlack && !sector.isRed && sector.name != "0" && sector.name != "00") win = true

        if (win) {
            Toast.makeText(this, "You win!", Toast.LENGTH_SHORT).show()
            you.money += 200
        } else {
            Toast.makeText(this, "You lose!", Toast.LENGTH_SHORT).show()
            you.money -= 200
        }
        binding.money.text = "Money: $${you.money}"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.simulator_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.menu_simulator_return -> {
                val currentActivity = this
                val gameIntent = Intent(currentActivity, SimulatorStartActivity::class.java)
                currentActivity.startActivity(gameIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}