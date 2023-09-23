package com.app.dentzadmin.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.app.dentzadmin.R


class postPage : BaseActivity() {
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        updateLanguage()
        setContentView(R.layout.postpage)

    }
}