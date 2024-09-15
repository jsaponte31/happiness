package com.example.firstapppsicology

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.os.CountDownTimer
import android.app.AlertDialog
import android.widget.TextView
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.graphics.Typeface

class activityAdults : AppCompatActivity() {
    data class Card(val id: Int, val imageResId: Int)
    private lateinit var timerTextView: TextView
    private var firstCard: ImageView? = null
    private var secondCard: ImageView? = null
    private var firstCardData: Card? = null
    private var secondCardData: Card? = null
    private var timer: CountDownTimer? = null
    private var pairsFound = 0
    private lateinit var gridLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adults)
        timerTextView = findViewById(R.id.timerTextView)
        gridLayout = findViewById(R.id.gridLayout)
        val cards: List<Card> = listOf(
            Card(id = 1, imageResId = R.drawable.alegria),
            Card(id = 1, imageResId = R.drawable.parejaalegria),
            Card(id = 2, imageResId = R.drawable.tristeza),
            Card(id = 2, imageResId = R.drawable.parejatristeza),
            Card(id = 3, imageResId = R.drawable.miedo),
            Card(id = 3, imageResId = R.drawable.parejamiedo),
            Card(id = 4, imageResId = R.drawable.dormido),
            Card(id = 4, imageResId = R.drawable.parejadormido),
            Card(id = 5, imageResId = R.drawable.mentiroso),
            Card(id = 5, imageResId = R.drawable.parejamentiroso),
            Card(id = 6, imageResId = R.drawable.sorpresa),
            Card(id = 6, imageResId = R.drawable.parejasorpresa),
            Card(id = 7, imageResId = R.drawable.asco),
            Card(id = 7, imageResId = R.drawable.parejaasco),
            Card(id = 8, imageResId = R.drawable.frio),
            Card(id = 8, imageResId = R.drawable.parejafrio),
        ).shuffled()

        cards.forEach { card ->
            val imageView = ImageView(this)
            imageView.layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 0
                height = 0
                setMargins(8, 8, 8, 8)
            }
            imageView.setImageResource(R.drawable.back_of_card)
            imageView.setOnClickListener {
                onCardClicked(card, imageView)
            }
            gridLayout.addView(imageView)
        }
        startTimer()
    }

    private fun startTimer() {
        timer = object: CountDownTimer(60000, 1000) { // 60 segundos en total, tickea cada segundo
            override fun onTick(millisUntilFinished: Long) {
                val secondsUntilFinished = millisUntilFinished / 1000
                timerTextView.text = "${secondsUntilFinished}"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun onCardClicked(card: Card, imageView: ImageView) {
        if (firstCard == null) {
            // Muestra la primera carta
            firstCard = imageView
            firstCardData = card
            imageView.setImageResource(card.imageResId)
        } else if (secondCard == null && firstCard != imageView) {
            // Muestra la segunda carta
            secondCard = imageView
            secondCardData = card
            imageView.setImageResource(card.imageResId)

            // Comprueba si las cartas coinciden
            if (firstCardData!!.id == secondCardData!!.id) {
                // Las cartas coinciden, se dejan visibles
                pairsFound++
                firstCard?.tag = "MATCHED"
                secondCard?.tag = "MATCHED"
                firstCard = null
                secondCard = null
                firstCardData = null
                secondCardData = null
                checkEndGame()
            } else {
                // Las cartas no coinciden, se voltean de nuevo después de un breve retraso
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    firstCard?.setImageResource(R.drawable.back_of_card)
                    secondCard?.setImageResource(R.drawable.back_of_card)
                    firstCard = null
                    secondCard = null
                    firstCardData = null
                    secondCardData = null
                }, 1000) // 1 segundo de retraso
            }
        }
    }

    private fun checkEndGame() {
        val allMatched = (0 until gridLayout.childCount).all { index ->
            val imageView = gridLayout.getChildAt(index) as ImageView
            imageView.tag == "MATCHED"
        }

        if (allMatched) {
            endGame()
        }
    }

    fun SpannableStringBuilder.bold(action: SpannableStringBuilder.() -> Unit): SpannableStringBuilder {
        val start = length
        action()
        setSpan(StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return this
    }

    private fun endGame() {
        timer?.cancel()

        val qualification = when (pairsFound) {
            in 1..3 -> "¡MALA!"
            in 4..6 -> "¡BUENA!"
            else -> "¡EXCELENTE!"
        }

        val messageText = SpannableStringBuilder()
            .bold { append("Alegría: ") }
            .append("asociada con la felicidad, el placer y la satisfacción.\n")
            .bold { append("Tristeza: ") }
            .append("se experimenta como una sensación de pérdida, desesperanza o melancolía.\n")
            .bold { append("Miedo: ") }
            .append("relacionada con la percepción de una amenaza o peligro inminente.\n")
            .bold { append("Ira: ") }
            .append("irritación, la furia o la indignación.\n")
            .bold { append("Asco: ") }
            .append("vinculada a la aversión física o psicológica hacia algo desagradable o repugnante.\n")
            .bold { append("Sorpresa: ") }
            .append("se produce ante una situación inesperada o algo novedoso\n\n")
            .append("Has encontrado $pairsFound de 8 parejas. Tu calificación es: $qualification")

        AlertDialog.Builder(this)
            .setTitle("Juego terminado")
            .setMessage(messageText)
            .setPositiveButton("Aceptar") { _, _ ->
                val intent = Intent(this, firstApp::class.java)
                startActivity(intent)
            }
            .show()

    }
}