package com.app.dentzadmin.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.apachat.loadingbutton.core.customViews.CircularProgressButton
import com.app.dentzadmin.R
import com.app.dentzadmin.repository.MainRepository
import com.app.dentzadmin.util.CommonUtil
import com.app.dentzadmin.viewModel.CommonViewModel
import com.app.dentzadmin.viewModel.CommonViewModelFactory

class LoginPage : AppCompatActivity() {
    private lateinit var commonViewModel: CommonViewModel
    private val cu = CommonUtil()
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        /* Hiding ToolBar */
        supportActionBar?.hide()

        /* ViewModel Initialization */
        commonViewModel = ViewModelProvider(
            this, CommonViewModelFactory(MainRepository())
        )[CommonViewModel::class.java]

        /* Initializing Views */
        val userid = findViewById<TextView>(R.id.userid)
        val password = findViewById<TextView>(R.id.password)
        val loginButton = findViewById<CircularProgressButton>(R.id.loginButton)
        val forgotpassword = findViewById<LinearLayout>(R.id.forgotpassword)

        val india = findViewById<LinearLayout>(R.id.india)
        val us = findViewById<LinearLayout>(R.id.us)

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

        forgotpassword.setOnClickListener {
            val moveToReset = Intent(this, ForgotPage::class.java)
            moveToReset.putExtra("userId", userid.text.toString())
            startActivity(moveToReset)
        }

        loginButton.setOnClickListener {
            if (userid.text.toString().isEmpty()) {
                displayMessageInAlert(getString(R.string.please_enter_user_id).toUpperCase())
            } else if (password.text.toString().isEmpty()) {
                displayMessageInAlert(getString(R.string.please_enter_password).toUpperCase())
            } else {
                loginButton.startAnimation()
                startFetch(userid.text.toString(), password.text.toString())
            }
        }
        userid.requestFocus()
        if (userid.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        commonViewModel.errorMessage.observe(this) { errorMessage ->
            cu.showAlert(errorMessage, this)
            loginButton.revertAnimation()
        }

        commonViewModel.responseContent.observe(this) { result ->
            if (result.data.isNotEmpty()) {
                val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putInt("isLoggedIn", 1)
                editor.putString("userId", result.data[0].userid)
                if (result.data[0].type == "user") {
                    val sharedPreferenceFcm = getSharedPreferences("FCMID", Context.MODE_PRIVATE)
                    val fcmToken = sharedPreferenceFcm.getString("Token", "")
                    commonViewModel.sendFcmId(this, result.data[0].userid, fcmToken!!)
                    editor.putString("groupId", result.data[0].groupid)
                    editor.putString("userId", result.data[0].userid)
                    editor.putString("isLoggedInType", "user")
                    editor.commit()
                    val moveToReset = Intent(this, UserHomePage::class.java)
                    startActivity(moveToReset)
                    finish()
                } else {
                    editor.putString("isLoggedInType", "admin")
                    editor.commit()
                    val moveToReset = Intent(this, HomePage::class.java)
                    startActivity(moveToReset)
                    finish()
                }
            } else {
                cu.showAlert(getString(R.string.invalid_login_details), this)
            }

            loginButton.revertAnimation()
        }
    }

    private fun startFetch(userId: String, password: String) {
        if (cu.isNetworkAvailable(this)) {
            commonViewModel.getLoginResponse(this, userId, password)
        } else {
            displayMessageInAlert(getString(R.string.no_internet).toUpperCase())
        }
    }

    private fun displayMessageInAlert(message: String) {
        cu.showAlert(message, this)
    }
}