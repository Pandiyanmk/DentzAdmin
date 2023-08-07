package com.app.dentzadmin.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.GroupNames
import com.app.dentzadmin.data.model.SendMessage
import com.app.dentzadmin.repository.MainRepository
import com.app.dentzadmin.util.CommonUtil
import com.app.dentzadmin.viewModel.CommonViewModel
import com.app.dentzadmin.viewModel.CommonViewModelFactory
import com.google.firebase.database.FirebaseDatabase


class ViewReports : AppCompatActivity() {
    private val cu = CommonUtil()
    private lateinit var aboutPageViewModel: CommonViewModel
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

        aboutPageViewModel = ViewModelProvider(
            this, CommonViewModelFactory(MainRepository())
        )[CommonViewModel::class.java]

        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        /* SetUp Views */
        loading = findViewById(R.id.loading)
        reportList = findViewById(R.id.reportList)

        startFetch()
        aboutPageViewModel.errorMessage.observe(this) { errorMessage ->
            cu.showAlert(errorMessage, this)
            loading!!.visibility = View.GONE

        }

        aboutPageViewModel.getReports.observe(this) { getReports ->
            val arraylistAdminMessage = ArrayList<SendMessage>()
            var messageContent = ""
            val groups = ArrayList<GroupNames>()

            for (i in 0 until getReports.reports.size) {
                messageContent = getReports.reports[i].messageData
                val groupid = getReports.reports[i].groupid
                groups.clear()
                val groupSplitArray = groupid.split(",").toTypedArray()
                for (j in 0 until groupSplitArray!!.size) {
                    val repliedCount = getReports.reports[i].groupCount[j]
                    val groupName = getReports.groupName.filter { it.id == groupSplitArray[j] }
                    val groupRepliedDetails = GroupNames(""+repliedCount.count, groupName[0].name+getString(
                                            R.string.sent_to)+groupName[0].count)
                    groups.add(groupRepliedDetails)
                }
                var list: ArrayList<GroupNames> = groups.clone() as ArrayList<GroupNames>

                val sendMessage = SendMessage(content = messageContent, groups = list)
                arraylistAdminMessage.add(sendMessage)
            }

            adminMessages(arraylistAdminMessage)
            loading!!.visibility = View.GONE

        }
    }

    private fun startFetch() {
        if (cu.isNetworkAvailable(this)) {
            aboutPageViewModel.getReports(this)
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
}