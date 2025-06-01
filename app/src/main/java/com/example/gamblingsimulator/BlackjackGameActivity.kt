package com.example.gamblingsimulator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gamblingsimulator.databinding.ActivityBlackjackGameBinding

class BlackjackGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlackjackGameBinding
    var dealer = Player(1000000, 0)
    var you = Player(1000, 0)
    var deckDefault = mutableListOf<Card>()
    var deck = mutableListOf<Card>()
    var currentBet: Int = 0

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

        binding.textViewGameMoney.text = "Money: $${you.money}"

        binding.buttonGameSubmitBet.setOnClickListener {
            val betInput = binding.textViewGameBetAmount.text.toString()
            val betAmount = betInput.toIntOrNull()

            if (betAmount == null || betAmount <= 0) {
                showToast("Please enter a valid bet amount!")
                return@setOnClickListener
            }

            if (betAmount > you.money) {
                showToast("You don't have enough money for that bet!")
                return@setOnClickListener
            }

            currentBet = betAmount
            you.money -= betAmount
            showToast("Bet placed: $currentBet")
            updateUI()

            initializeDeck()
            startGame()
        }

        binding.buttonGameHit.setOnClickListener {
            hit()
        }
        binding.buttonGameStand.setOnClickListener {
            stand()
        }
        binding.buttonGameReset.setOnClickListener {
            resetGame()
        }
    }

    fun initializeDeck() {
        val numbers = mapOf(
            2 to "two", 3 to "three", 4 to "four", 5 to "five", 6 to "six",
            7 to "seven", 8 to "eight", 9 to "nine", 10 to "ten"
        )
        val suits = listOf("clubs", "diamonds", "hearts", "spades")
        deckDefault.clear()
        for (suit in suits) {
            for (value in 2..10) {
                deckDefault.add(Card(value, suit, "${numbers[value]}_of_${suit}.png"))
            }
            deckDefault.add(Card(10, suit, "jack_of_${suit}.png"))
            deckDefault.add(Card(10, suit, "queen_of_${suit}.png"))
            deckDefault.add(Card(10, suit, "king_of_${suit}.png"))
            deckDefault.add(Card(1, suit, "ace_of_${suit}.png"))
        }
    }

    fun startGame() {
        binding.layoutBettingControls.visibility = View.GONE

        binding.buttonGameReset.visibility = View.VISIBLE
        binding.buttonGameStand.visibility = View.VISIBLE
        binding.buttonGameHit.visibility = View.VISIBLE
        binding.layoutDealerCards.visibility = View.VISIBLE
        binding.layoutPlayerCards.visibility = View.VISIBLE
        binding.textViewGameDealer.visibility = View.VISIBLE
        binding.textViewGameDealerValue.visibility = View.VISIBLE
        binding.textViewGamePlayer.visibility = View.VISIBLE
        binding.textViewGamePlayerValue.visibility = View.VISIBLE
        binding.textViewGameMessage.visibility = View.VISIBLE

        deck = deckDefault.toMutableList()
        dealer.value = 0
        you.value = 0

        binding.layoutDealerCards.removeAllViews()
        binding.layoutPlayerCards.removeAllViews()

        updateUI()

        val handler = Handler(Looper.getMainLooper())
        val initialDeal = listOf(
            Pair("dealer", drawCard()),
            Pair("player", drawCard()),
            Pair("player", drawCard())
        )

        var index = 0

        fun dealNextCard() {
            if (index < initialDeal.size) {
                val (who, card) = initialDeal[index]
                if (who == "dealer") {
                    dealer.value += card.value
                    addCardImage(binding.layoutDealerCards, card.image)
                } else {
                    you.value += card.value
                    addCardImage(binding.layoutPlayerCards, card.image)
                }
                updateUI()
                index++
                handler.postDelayed({ dealNextCard() }, 750)
            }
        }
        dealNextCard()
    }

    private fun drawCard(): Card {
        val card = deck.random()
        deck.remove(card)
        return card
    }

    fun hit() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val card = drawCard()
            you.value += card.value
            addCardImage(binding.layoutPlayerCards, card.image)
            updateUI()

            if (you.value > 21) {
                showToast("YOU HAVE BUSTED")
                endGame()
            }
        }, 750)
    }

    fun stand() {
        val handler = Handler(Looper.getMainLooper())
        fun dealerTurn() {
            if (dealer.value <= 16) {
                val card = drawCard()
                dealer.value += card.value
                addCardImage(binding.layoutDealerCards, card.image)
                updateUI()

                handler.postDelayed({ dealerTurn() }, 750)
            } else {
                checkWinner()
            }
        }
        dealerTurn()
    }

    fun addCardImage(layout: LinearLayout, imageName: String) {
        val imageView = ImageView(this)
        val resId = resources.getIdentifier(imageName.removeSuffix(".png"), "drawable", packageName)
        if (resId != 0) {
            imageView.setImageResource(resId)
        }
        val params = LinearLayout.LayoutParams(150, 220)
        params.setMargins(8, 0, 8, 0)
        imageView.layoutParams = params
        imageView.alpha = 0f
        layout.addView(imageView)
        imageView.animate().alpha(1f).setDuration(300).start()
    }

    fun checkWinner() {
        val result = when {
            you.value > 21 -> "YOU BUSTED. Lost $currentBet."
            dealer.value > 21 -> {
                you.money += currentBet * 2
                "DEALER BUSTED. You win ${currentBet * 2}!"
            }
            dealer.value == you.value -> {
                you.money += currentBet
                "PUSH. Bet returned: $currentBet."
            }
            you.value > dealer.value -> {
                you.money += currentBet * 2
                "YOU WIN! You get ${currentBet * 2}!"
            }
            else -> "YOU LOSE. Lost $currentBet."
        }

        showToast(result)
        endGame()
    }

    fun endGame() {
        currentBet = 0
        updateUI()

        // Show a message or keep the final card layout visible for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // After the delay, switch back to betting screen
            binding.layoutBettingControls.visibility = View.VISIBLE

            binding.buttonGameReset.visibility = View.INVISIBLE
            binding.buttonGameStand.visibility = View.INVISIBLE
            binding.buttonGameHit.visibility = View.INVISIBLE
            binding.layoutDealerCards.visibility = View.INVISIBLE
            binding.layoutPlayerCards.visibility = View.INVISIBLE
            binding.textViewGameDealer.visibility = View.INVISIBLE
            binding.textViewGameDealerValue.visibility = View.INVISIBLE
            binding.textViewGamePlayer.visibility = View.INVISIBLE
            binding.textViewGamePlayerValue.visibility = View.INVISIBLE
            binding.textViewGameMessage.visibility = View.INVISIBLE

            // Optionally: clear the card views for next round
            binding.layoutDealerCards.removeAllViews()
            binding.layoutPlayerCards.removeAllViews()
        }, 2000) // 2000 milliseconds = 2 seconds
    }

    fun resetGame() {
        currentBet = 0
        you.value = 0
        dealer.value = 0
        updateUI()
        binding.layoutDealerCards.removeAllViews()
        binding.layoutPlayerCards.removeAllViews()

        binding.layoutBettingControls.visibility = View.VISIBLE

        binding.buttonGameReset.visibility = View.INVISIBLE
        binding.buttonGameStand.visibility = View.INVISIBLE
        binding.buttonGameHit.visibility = View.INVISIBLE
        binding.layoutDealerCards.visibility = View.INVISIBLE
        binding.layoutPlayerCards.visibility = View.INVISIBLE
        binding.textViewGameDealer.visibility = View.INVISIBLE
        binding.textViewGameDealerValue.visibility = View.INVISIBLE
        binding.textViewGamePlayer.visibility = View.INVISIBLE
        binding.textViewGamePlayerValue.visibility = View.INVISIBLE
        binding.textViewGameMessage.visibility = View.INVISIBLE
    }

    fun updateUI() {
        binding.textViewGamePlayerValue.text = you.value.toString()
        binding.textViewGameDealerValue.text = dealer.value.toString()
        binding.textViewGameMoney.text = "Money: $${you.money}"
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
