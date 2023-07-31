package com.app.dentzadmin.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.app.dentzadmin.R


class ProfilePage : AppCompatActivity() {
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.profile_page)

        val logout = findViewById<LinearLayout>(R.id.logout)
        val india = findViewById<LinearLayout>(R.id.india)
        val us = findViewById<LinearLayout>(R.id.us)
        val viewreports = findViewById<LinearLayout>(R.id.viewreports)
        val viewreportscard = findViewById<CardView>(R.id.viewreportscard)

        val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val isLoggedInType = sharedPreference.getString("isLoggedInType", "")

        if (isLoggedInType.equals("admin")) {
            viewreportscard.visibility = View.VISIBLE
        }

        india.setOnClickListener {
            val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("isLanguage", "hi")
            editor.commit()
            val moveToReset = Intent(this, GetStartedPage::class.java)
            moveToReset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(moveToReset)
            finish()
        }

        viewreports.setOnClickListener {
            val moveToReset = Intent(this, ViewReports::class.java)
            startActivity(moveToReset)
        }

        us.setOnClickListener {
            val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("isLanguage", "en")
            editor.commit()
            val moveToReset = Intent(this, GetStartedPage::class.java)
            moveToReset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(moveToReset)
            finish()
        }

        logout.setOnClickListener {
            val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.remove("isAnswered")
            editor.remove("isLoggedIn")
            editor.commit()
            val moveToReset = Intent(this, GetStartedPage::class.java)
            moveToReset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(moveToReset)
            finish()
        }

    }
}