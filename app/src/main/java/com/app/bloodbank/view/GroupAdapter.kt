package com.app.bloodbank.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.bloodbank.R
import com.app.bloodbank.data.model.Group
import org.greenrobot.eventbus.EventBus


class GroupAdapter(val ctx: Context, private val mList: List<Group>, private val maxSelect: Int) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.name.text = ItemsViewModel.name

        var getCount: List<Group> = mList.filter { it.status == 1 }

        holder.checkBox.isChecked = ItemsViewModel.status == 1

        holder.fulllay.setOnClickListener {
            if (getCount.size <= maxSelect) {
                var statusResult = 0
                if (ItemsViewModel.status == 0) {
                    statusResult = 1
                }
                EventBus.getDefault().post("$position,$statusResult")
            } else {
                if (ItemsViewModel.status == 1) {
                    EventBus.getDefault().post("$position,0")
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.limit_exceed), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val fulllay: LinearLayout = itemView.findViewById(R.id.fulllay)
    }
}
