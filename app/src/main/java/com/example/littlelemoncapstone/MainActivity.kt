package com.example.littlelemoncapstone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("LittleLemon", MODE_PRIVATE)
        val user = preferences.getString("user", null)

        val intent = if (user == null) {
            Intent(this, WelcomeActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}