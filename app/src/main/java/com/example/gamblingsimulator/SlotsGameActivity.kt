package com.example.gamblingsimulator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gamblingsimulator.databinding.ActivitySlotsGameBinding
import kotlin.random.Random

class SlotsGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySlotsGameBinding
    private var listOfFruits = mutableListOf<Fruit>()
    private var player = Player(1000, 0)
    private var cost = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySlotsGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        generate()
        binding.textViewMainTotal.text = "Money: $${player.money}"
        binding.buttonGameSpin.setOnClickListener {
            player.money -= cost
            startGame()
        }
    }

    fun generate(){
        listOfFruits.add(Fruit("Banana", "banana"))
        listOfFruits.add(Fruit("Strawberry", "strawberry"))
        listOfFruits.add(Fruit("Kiwi", "kiwi"))
        listOfFruits.add(Fruit("Blueberry", "blueberry"))
        listOfFruits.add(Fruit("Orange", "orange"))
        listOfFruits.add(Fruit("Grape", "grape"))
        listOfFruits.add(Fruit("Cherry", "cherry"))
        binding.imageViewGameSlot1.setImageDrawable(resources.getDrawable(R.drawable.cherry))
        binding.imageViewGameSlot2.setImageDrawable(resources.getDrawable(R.drawable.cherry))
        binding.imageViewGameSlot3.setImageDrawable(resources.getDrawable(R.drawable.cherry))
    }

    // https://stackoverflow.com/questions/9481334/how-to-replace-r-drawable-somestring
    fun startGame(){
        binding.buttonGameSpin.isEnabled = false
        binding.buttonGameSpin.postDelayed({binding.buttonGameSpin.isEnabled = true}, 2000)
        val slot1 = Random.nextInt(0, 7)
        val slot2 = Random.nextInt(0, 7)
        val slot3 = Random.nextInt(0, 7)
        var resId = resources.getIdentifier(listOfFruits[slot1].image, "drawable", packageName)
        binding.imageViewGameSlot1.setImageResource(resId)
        var resId2 = resources.getIdentifier(listOfFruits[slot2].image, "drawable", packageName)
        binding.imageViewGameSlot2.setImageResource(resId2)
        var resId3 = resources.getIdentifier(listOfFruits[slot3].image, "drawable", packageName)
        binding.imageViewGameSlot3.setImageResource(resId3)
        checkWin(slot1, slot2, slot3)
    }

    fun checkWin(num1 : Int, num2: Int, num3 : Int){
        if((listOfFruits[num1].image == listOfFruits[num2].image) && (listOfFruits[num1].image == listOfFruits[num3].image) && (listOfFruits[num2].image == listOfFruits[num3].image)) {
            Toast.makeText(this, "YOU WON : 10x!!!", Toast.LENGTH_SHORT).show()
            player.money += (cost * 10)
            binding.textViewMainTotal.text = "Money: $${player.money}"
        }
        else if((listOfFruits[num1].image == listOfFruits[num2].image) || (listOfFruits[num1].image == listOfFruits[num3].image) || (listOfFruits[num2].image == listOfFruits[num3].image)){
            Toast.makeText(this, "YOU WON : 3x!!!", Toast.LENGTH_SHORT).show()
            player.money += (cost * 3)
            binding.textViewMainTotal.text = "Money: $${player.money}"
        }
        else{
            Toast.makeText(this, "YOU LOST", Toast.LENGTH_SHORT).show()
            binding.textViewMainTotal.text = "Money: $${player.money}"
        }
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