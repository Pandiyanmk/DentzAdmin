package com.app.dentzadmin.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.GroupNames
import com.app.dentzadmin.data.model.SendMessage


class AdminSentAdapter(val ctx: Context, private val mList: List<SendMessage>) :
    RecyclerView.Adapter<AdminSentAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reports_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.name.text = ItemsViewModel.content

        val parts = ItemsViewModel.groups
        val layoutManager = GridLayoutManager(ctx, 2)
        holder.groupList!!.layoutManager = layoutManager
        val adapter = GroupSentAdapter(ctx, parts)
        holder.groupList!!.adapter = adapter
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val groupList: RecyclerView = itemView.findViewById(R.id.groupList)
    }
}
