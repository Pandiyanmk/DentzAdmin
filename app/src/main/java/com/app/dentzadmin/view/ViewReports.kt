package com.app.dentzadmin.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.MessageSentFromAdminToGroup
import com.app.dentzadmin.data.model.SendData
import com.app.dentzadmin.data.model.SendMessage
import com.app.dentzadmin.util.CommonUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ViewReports : AppCompatActivity() {
    private val cu = CommonUtil()

    private var loading: ProgressBar? = null
    private var reportList: RecyclerView? = null
    lateinit var groupAdapter: GroupAdapter
    lateinit var messageAdapter: MessageAdapter

    var firebaseDatabase: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewreports)

        /* Hiding ToolBar */
        supportActionBar?.hide()

        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        /* SetUp Views */
        loading = findViewById(R.id.loading)
        reportList = findViewById(R.id.reportList)

        startFetch()
    }

    private fun startFetch() {
        if (cu.isNetworkAvailable(this)) {
            getAdminSentMessage()
        } else {
            displayMessageInAlert(getString(R.string.no_internet))
            loading!!.visibility = View.GONE
        }
    }

    private fun displayMessageInAlert(message: String) {
        cu.showAlert(message, this)
    }

    fun adminMessages(adminMessage: ArrayList<SendMessage>) {
        reportList!!.layoutManager = LinearLayoutManager(this)
        val adapter = AdminSentAdapter(this, adminMessage)
        reportList!!.adapter = adapter
    }

    private fun getAdminSentMessage() {
        val arraylistAdminMessage = ArrayList<SendMessage>()
        val pathReference = firebaseDatabase!!.reference.child("AdminSentMessages")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (templateSnapshot in dataSnapshot.children) {
                    val post = templateSnapshot.getValue(MessageSentFromAdminToGroup::class.java)
                    post?.let {
                        val sd = SendData(messageid = it.content, groupid = it.groups)
                        val sendMessage = SendMessage(content = it.content, groups = it.groups)
                        arraylistAdminMessage.add(sendMessage)
                    }
                }
                adminMessages(arraylistAdminMessage)
                loading!!.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                loading!!.visibility = View.GONE
            }
        }
        pathReference.addListenerForSingleValueEvent(postListener)
    }
}