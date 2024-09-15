package com.example.firstapppsicology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class firstApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_app)

        val btnBoys = findViewById<Button>(R.id.btnBoys)
        btnBoys.setOnClickListener {
            val intent = Intent(this, activityBoys::class.java)
            startActivity(intent)
        }

        val btnAdults = findViewById<Button>(R.id.btnAdults)
        btnAdults.setOnClickListener {
            val intent = Intent(this, activityAdults::class.java)
            startActivity(intent)
        }
    }
}