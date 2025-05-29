package com.example.blackjack

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blackjack.databinding.ActivityBlackjackGameBinding
import com.example.blackjack.databinding.ActivityBlackjackStartBinding
import kotlinx.coroutines.NonCancellable.start
import kotlin.random.Random

class BlackJackGameActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBlackjackGameBinding
    var dealer = Player(1000000, 0)
    var you = Player(1000, 0)
    var deckToNumberDefault = HashMap<Int, Card>()
    var deckToNumber = HashMap<Int, Card>()
    var deckDefault = mutableListOf<Card>()
    var deck = mutableListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBlackjackGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        deck.add(Card(2, "Clubs", "two_of_clubs.png" ))
        deck.add(Card(2, "Diamonds", "two_of_diamonds.png" ))
        deck.add(Card(2, "Hearts", "two_of_hearts.png" ))
        deck.add(Card(2, "Spades", "two_of_spades.png" ))
        deck.add(Card(3, "Clubs", "three_of_clubs.png" ))
        deck.add(Card(3, "Diamonds", "three_of_diamonds.png" ))
        deck.add(Card(3, "Hearts", "three_of_hearts.png" ))
        deck.add(Card(3, "Spades", "three_of_spades.png" ))
        deck.add(Card(4, "Clubs", "four_of_clubs.png" ))
        deck.add(Card(4, "Diamonds", "four_of_diamonds.png" ))
        deck.add(Card(4, "Hearts", "four_of_hearts.png" ))
        deck.add(Card(4, "Spades", "four_of_spades.png" ))
        deck.add(Card(5, "Clubs", "five_of_clubs.png" ))
        deck.add(Card(5, "Diamonds", "five_of_diamonds.png" ))
        deck.add(Card(5, "Hearts", "five_of_hearts.png" ))
        deck.add(Card(5, "Spades", "five_of_spades.png" ))
        deck.add(Card(6, "Clubs", "six_of_clubs.png" ))
        deck.add(Card(6, "Diamonds", "six_of_diamonds.png" ))
        deck.add(Card(6, "Hearts", "six_of_hearts.png" ))
        deck.add(Card(6, "Spades", "six_of_spades.png" ))
        deck.add(Card(7, "Clubs", "seven_of_clubs.png" ))
        deck.add(Card(7, "Diamonds", "seven_of_diamonds.png" ))
        deck.add(Card(7, "Hearts", "seven_of_hearts.png" ))
        deck.add(Card(7, "Spades", "seven_of_spades.png" ))
        deck.add(Card(8, "Clubs", "eight_of_clubs.png" ))
        deck.add(Card(8, "Diamonds", "eight_of_diamonds.png" ))
        deck.add(Card(8, "Hearts", "eight_of_hearts.png" ))
        deck.add(Card(8, "Spades", "eight_of_spades.png" ))
        deck.add(Card(9, "Clubs", "nine_of_clubs.png" ))
        deck.add(Card(9, "Diamonds", "nine_of_diamonds.png" ))
        deck.add(Card(9, "Hearts", "nine_of_hearts.png" ))
        deck.add(Card(9, "Spades", "nine_of_spades.png" ))
        deck.add(Card(10, "Clubs", "ten_of_clubs.png" ))
        deck.add(Card(10, "Diamonds", "ten_of_diamonds.png" ))
        deck.add(Card(10, "Hearts", "ten_of_hearts.png" ))
        deck.add(Card(10, "Spades", "ten_of_spades.png" ))
        deck.add(Card(10, "Clubs", "jack_of_clubs.png" ))
        deck.add(Card(10, "Diamonds", "jack_of_diamonds.png" ))
        deck.add(Card(10, "Hearts", "jack_of_hearts.png" ))
        deck.add(Card(10, "Spades", "jack_of_spades.png" ))
        deck.add(Card(10, "Clubs", "queen_of_clubs.png" ))
        deck.add(Card(10, "Diamonds", "queen_of_diamonds.png" ))
        deck.add(Card(10, "Hearts", "queen_of_hearts.png" ))
        deck.add(Card(10, "Spades", "queen_of_spades.png" ))
        deck.add(Card(10, "Clubs", "king_of_clubs.png" ))
        deck.add(Card(10, "Diamonds", "king_of_diamonds.png" ))
        deck.add(Card(10, "Hearts", "king_of_hearts.png" ))
        deck.add(Card(10, "Spades", "king_of_spades.png" ))
        deck.add(Card(1, "Clubs", "ace_of_clubs.png" ))
        deck.add(Card(1, "Diamonds", "ace_of_diamonds.png" ))
        deck.add(Card(1, "Hearts", "ace_of_hearts.png" ))
        deck.add(Card(1, "Spades", "ace_of_spades.png" ))

        for (i in 0.. 51) {
            deckToNumber[i] = deck[i]
        }

        deckDefault = deck
        deckToNumberDefault = deckToNumber

        startGame()
    }

    fun startGame() {
        deck = deckDefault
        deckToNumber = deckToNumberDefault

        var num1 = Random.nextInt(0, deckToNumber.size)
        dealer.value += deckToNumber[num1]!!.value
        deckToNumber.remove(num1)
        binding.textViewGameDealerValue.text = dealer.value.toString()
        var num2 = Random.nextInt(0, deckToNumber.size)
        you.value += deckToNumber[num2]!!.value
        deckToNumber.remove(num2)

        var num3 = Random.nextInt(0, deckToNumber.size)
        you.value += deckToNumber[num3]!!.value
        deckToNumber.remove(num3)
        binding.textViewGamePlayerValue.text = you.value.toString()

        binding.buttonGameHit.setOnClickListener {
            hit()
        }

        binding.buttonGameStand.setOnClickListener {
            stand()
        }

        binding.buttonGameReset.setOnClickListener{
            dealer.value = 0
            binding.textViewGameDealerValue.text = "0"
            you.value = 0
            binding.textViewGamePlayerValue.text = "0"
            startGame()
        }
    }

    fun hit() {
        var num1 = Random.nextInt(0, deckToNumber.size)
        you.value += deckToNumber[num1]!!.value
        deckToNumber.remove(num1)
        if (you.value > 21) {
            gameOver()
        }
        binding.textViewGamePlayerValue.text = you.value.toString()
    }

    fun stand() {
        if (dealer.value > 16) {
            gameOver()
        }
        while(dealer.value <= 16) {
            var num1 = Random.nextInt(0, deckToNumber.size)
            dealer.value += deckToNumber[num1]!!.value
            deckToNumber.remove(num1)
            binding.textViewGameDealerValue.text = dealer.value.toString()
            stand()
        }
    }

    fun gameOver() {
        if (you.value > 21) {
            Toast.makeText(this, "YOU BUSTED", Toast.LENGTH_LONG).show()
        }
        else if (dealer.value > 21) {
            Toast.makeText(this, "YOU WON", Toast.LENGTH_LONG).show()
        }
        else if (dealer.value == you.value) {
            Toast.makeText(this, "PUSH", Toast.LENGTH_LONG).show()
        }
        else if (dealer.value < you.value) {
            Toast.makeText(this, "YOU WON", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "YOU LOST", Toast.LENGTH_LONG).show()
        }
    }

}