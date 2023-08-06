package com.app.dentzadmin.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.apachat.loadingbutton.core.customViews.CircularProgressButton
import com.app.dentzadmin.R
import com.app.dentzadmin.repository.MainRepository
import com.app.dentzadmin.util.CommonUtil
import com.app.dentzadmin.viewModel.CommonViewModel
import com.app.dentzadmin.viewModel.CommonViewModelFactory

class ForgotPage : AppCompatActivity() {
    private lateinit var commonViewModel: CommonViewModel
    private val cu = CommonUtil()
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_page)

        /* Hiding ToolBar */
        supportActionBar?.hide()

        var userId = intent.getStringExtra("userId")

        /* ViewModel Initialization */
        commonViewModel = ViewModelProvider(
            this, CommonViewModelFactory(MainRepository())
        )[CommonViewModel::class.java]

        /* Initializing Views */
        val userid = findViewById<TextView>(R.id.userid)
        val loginButton = findViewById<CircularProgressButton>(R.id.loginButton)
        val sent = findViewById<TextView>(R.id.sent)

        userid.text = userId

        loginButton.setOnClickListener {
            if (userid.text.toString().isEmpty()) {
                displayMessageInAlert(getString(R.string.please_enter_user_id).toUpperCase())
            } else {
                sent.visibility = View.GONE
                loginButton.startAnimation()
                startFetch(userid.text.toString())
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
            if (result.data.isEmpty()) {
                sent.text = getString(R.string.invalid_user_id)
            } else {
                sent.text = getString(R.string.password_is_sent_to_the_registered_mobile_number)
            }
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            sent.visibility = View.VISIBLE
            loginButton.revertAnimation()
        }
    }

    private fun startFetch(userId: String) {
        if (cu.isNetworkAvailable(this)) {
            commonViewModel.getForgotPasswordResponse(this, userId)
        } else {
            displayMessageInAlert(getString(R.string.no_internet).toUpperCase())
        }
    }

    private fun displayMessageInAlert(message: String) {
        cu.showAlert(message, this)
    }
}