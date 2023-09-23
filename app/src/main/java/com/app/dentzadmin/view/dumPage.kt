package com.app.dentzadmin.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.app.dentzadmin.R


class dumPage : BaseActivity() {
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        updateLanguage()
        setContentView(R.layout.dummyprofile)

        val textView4 = findViewById<TextView>(R.id.textView4)
        val textView3 = findViewById<TextView>(R.id.textView3)
        val imageButton2 = findViewById<ImageButton>(R.id.imageButton2)
        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        textView4.setOnClickListener {
            val moveToReset = Intent(this, ProfilePage::class.java)
            startActivity(moveToReset)
        }

        imageButton2.setOnClickListener {
            val moveToReset = Intent(this, ProfilePage::class.java)
            startActivity(moveToReset)
        }

        textView3.setOnClickListener {
            val moveToReset = Intent(this, postPage::class.java)
            startActivity(moveToReset)
        }

        imageButton.setOnClickListener {
            val moveToReset = Intent(this, postPage::class.java)
            startActivity(moveToReset)
        }

    }
}