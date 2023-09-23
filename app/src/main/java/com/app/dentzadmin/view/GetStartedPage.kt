package com.app.dentzadmin.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.app.dentzadmin.R
import java.util.Locale


class GetStartedPage : BaseActivity() {
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* Hiding ToolBar */
        supportActionBar?.hide()
        updateLanguage()
        val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreference.getInt("isLoggedIn", 0)
        val isLoggedInType = sharedPreference.getString("isLoggedInType", "")
        if (isLoggedIn == 1 && isLoggedInType.equals("admin")) {
            val moveToAboutPage = Intent(this, HomePage::class.java)
            startActivity(moveToAboutPage)
            finish()
        } else if (isLoggedIn == 1 && isLoggedInType.equals("user")) {
            val moveToAboutPage = Intent(this, UserHomePage::class.java)
            startActivity(moveToAboutPage)
            finish()
        } else {
            setContentView(R.layout.launch_page)
            Handler().postDelayed({
                val moveToAboutPage = Intent(this, LoginPage::class.java)
                startActivity(moveToAboutPage)
                finish()
            }, 5000)
        }

    }
}