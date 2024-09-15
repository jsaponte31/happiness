package com.example.firstapppsicology

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.os.Handler
import android.os.Looper
import android.app.AlertDialog
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.GridLayout
import kotlin.random.Random

class activityBoys : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var timerTextView: TextView
    private val emojis = mutableListOf<ImageButton>()
    private var timer: CountDownTimer? = null
    private lateinit var emotionViewPager : ViewPager2
    private val emotions = listOf("ALEGRIA", "TRISTEZA", "MIEDO", "ENFADADO", "SORPRESA", "ASCO")
    private var currentEmotion = ""
    private var timeRemaining = 60000L  // 60 segundos
    private val updateTimeInterval = 1000L  // Intervalo de actualización de 1 segundo
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            timeRemaining -= updateTimeInterval
            updateTimerDisplay()  // Actualiza la UI con el tiempo restante

            if (timeRemaining <= 0) {
                endGame()
                return
            }

            handler.postDelayed(this, updateTimeInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boys)

        timerTextView = findViewById(R.id.timerTextView)
        gridLayout = findViewById(R.id.gridLayout)
        emotionViewPager = findViewById(R.id.emotionViewPager)
        emotionViewPager.adapter = EmotionAdapter(emotions)
        emotionViewPager.isUserInputEnabled = false

        initEmojis()
        startGame()
    }

    private fun initEmojis() {
        val emotionDrawables = mapOf(
            "alegria" to listOf(R.drawable.alegria1, R.drawable.alegria2, R.drawable.alegria3, R.drawable.alegria4, R.drawable.alegria5),
            "tristeza" to listOf(R.drawable.tristeza1, R.drawable.tristeza2, R.drawable.tristeza3, R.drawable.tristeza4, R.drawable.tristeza5),
            "miedo" to listOf(R.drawable.miedo1, R.drawable.miedo2, R.drawable.miedo3, R.drawable.miedo4, R.drawable.miedo5),
            "sorpresa" to listOf(R.drawable.sorpresa1, R.drawable.sorpresa2, R.drawable.sorpresa3, R.drawable.sorpresa4, R.drawable.sorpresa5),
            "asco" to listOf(R.drawable.asco1, R.drawable.asco2, R.drawable.asco3, R.drawable.asco4, R.drawable.asco5),
            "enfadado" to listOf(R.drawable.enfadado1, R.drawable.enfadado2, R.drawable.enfadado3, R.drawable.enfadado4, R.drawable.enfadado5)
        )

        var totalEmojiCount = 0

        val emojiSizeInPixels = resources.getDimensionPixelSize(R.dimen.emoji_size)

        emotionDrawables.forEach { (emotion, drawables) ->
            drawables.forEach { drawableId ->
                val rowIndex = totalEmojiCount % gridLayout.rowCount
                val colIndex = totalEmojiCount % gridLayout.columnCount

                val imageButton = ImageButton(this)
                imageButton.setImageResource(drawableId)  // Cambio aquí
                imageButton.tag = emotion
                imageButton.layoutParams = GridLayout.LayoutParams().apply {
                    width = emojiSizeInPixels
                    height = emojiSizeInPixels
                    setMargins(15,15,15,15)  // Ajusta el valor "8" para cambiar el espacio entre emojis
                    rowSpec = GridLayout.spec(rowIndex)
                    columnSpec = GridLayout.spec(colIndex)
                }
                imageButton.background = null  // Esto quitará el fondo predeterminado del ImageButton
                imageButton.setOnClickListener { handleEmojiClick(it as ImageButton) }
                imageButton.scaleType = ImageView.ScaleType.FIT_CENTER
                imageButton.setPadding(0, 0, 0, 0)  // Quita el relleno del ImageButton

                emojis.add(imageButton)
                gridLayout.addView(imageButton)

                totalEmojiCount++
            }
        }
    }


    private fun startGame() {
        nextEmotion()

        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val totalSeconds = timeRemaining / 1000
                timerTextView.text = totalSeconds.toString()
            }

            override fun onFinish() {
                endGame()
            }
        }
        timer?.start()
    }

    private fun startOrRestartTimer() {
        handler.removeCallbacks(runnable)  // Detiene cualquier actualización anterior
        handler.post(runnable)  // Inicia la actualización
    }

    private fun updateTimerDisplay() {
        val secondsRemaining = timeRemaining / 1000
        timerTextView.text = "$secondsRemaining"
    }

    private fun handleEmojiClick(emoji: ImageButton) {
        val emojiTag = emoji.tag.toString().toLowerCase()
        val currentEmotionInLowerCase = currentEmotion.toLowerCase()

        if (emojiTag == currentEmotionInLowerCase) {
            emojis.remove(emoji)
            emoji.visibility = View.GONE
            nextEmotion()
        }

        startOrRestartTimer()
    }


    private fun nextEmotion() {
        val availableEmotions = emotions.filter { hasAvailableEmojisForEmotion(it.toLowerCase()) }

        // Si no hay más emociones disponibles, termina el juego
        if (availableEmotions.isEmpty()) {
            endGame()
            return
        }

        currentEmotion = availableEmotions.random()
        val emotionPosition = emotions.indexOf(currentEmotion)
        emotionViewPager.setCurrentItem(emotionPosition, true)
    }

    private fun hasAvailableEmojisForEmotion(emotion: String): Boolean {
        return emojis.any { it.tag == emotion }
    }

    private fun endGame() {
        timer?.cancel()

        val touchedEmojis = 30 - emojis.size
        val resultText: String
        val starResource: Int

        when (touchedEmojis) {
            in 0..10 -> {
                resultText = "CALIFICACION: ¡MALA!\n$touchedEmojis de 30 emojis"
                starResource = R.drawable.star_one
            }
            in 11..20 -> {
                resultText = "CALIFICACION: ¡BUENA!\n$touchedEmojis de 30 emojis"
                starResource = R.drawable.star_two
            }
            else -> {
                resultText = "CALIFICACION: ¡EXCELENTE!\n$touchedEmojis de 30 emojis"
                starResource = R.drawable.star_three
            }
        }

        showEndGameDialog(resultText, starResource)
    }

    private fun showEndGameDialog(resultText: String, starResource: Int) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_end_game, null)

        val resultTextView: TextView = dialogView.findViewById(R.id.resultTextView)
        val starImageView: ImageView = dialogView.findViewById(R.id.starImageView)

        resultTextView.text = resultText
        starImageView.setImageResource(starResource)

        builder.setView(dialogView)
        builder.setPositiveButton("OK") { _, _ ->
            val intent = Intent(this, firstApp::class.java)
            startActivity(intent)
        }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    class EmotionFragment : Fragment() {
        companion object {
            private const val ARG_EMOTION = "emotion"

            fun newInstance(emotion: String): EmotionFragment {
                val fragment = EmotionFragment()
                val args = Bundle()
                args.putString(ARG_EMOTION, emotion)
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val view = TextView(context)
            view.textSize = 24f
            view.text = arguments?.getString(ARG_EMOTION)
            return view
        }
    }

    inner class EmotionAdapter(private val emotions: List<String>) : FragmentStateAdapter(this@activityBoys) {
        override fun getItemCount(): Int = emotions.size
        override fun createFragment(position: Int): Fragment = EmotionFragment.newInstance(emotions[position])
    }
}
