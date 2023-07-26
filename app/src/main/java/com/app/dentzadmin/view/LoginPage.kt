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
                startFetch()
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
            if (result.userId == userid.text.toString() && result.password == password.text.toString()) {
                val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putInt("isLoggedIn", 1)
                editor.commit()
                val moveToReset = Intent(this, HomePage::class.java)
                startActivity(moveToReset)
                finish()
            } else {
                cu.showAlert(getString(R.string.invalid_login_details), this)
            }
            loginButton.revertAnimation()
        }
    }

    private fun startFetch() {
        if (cu.isNetworkAvailable(this)) {
            commonViewModel.getResponseContent(this)
        } else {
            displayMessageInAlert(getString(R.string.no_internet).toUpperCase())
        }
    }

    private fun displayMessageInAlert(message: String) {
        cu.showAlert(message, this)
    }
}