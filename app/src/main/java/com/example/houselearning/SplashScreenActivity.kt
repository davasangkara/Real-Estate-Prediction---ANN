package com.example.houselearning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val secondActbutton = findViewById<Button>(R.id.button)
        secondActbutton.setOnClickListener {
            val Intent = Intent(this,CardViewActivity::class.java)
            startActivity(Intent)


        }
    }
}