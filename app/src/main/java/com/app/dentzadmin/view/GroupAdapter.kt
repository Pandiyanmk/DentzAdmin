package com.app.dentzadmin.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.Group
import org.greenrobot.eventbus.EventBus


class GroupAdapter(
    val ctx: Context,
    private val mList: List<Group>,
    private val maxSelect: Int,
    private val groupName: String
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

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


        var getCount: List<Group> = mList.filter { it.status == 1 }

        holder.checkBox.isChecked = ItemsViewModel.status == 2 || ItemsViewModel.status == 1

        if (groupName.contains(ItemsViewModel.name)) {
            holder.name.text = "${ItemsViewModel.name}\n${ctx.getString(R.string.sent)}"
            holder.name.setTextColor(Color.LTGRAY)
        } else {
            holder.name.text = ItemsViewModel.name
        }

        holder.fulllay.setOnClickListener {
            if (ItemsViewModel.status != 2) {
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
                        Toast.makeText(
                            ctx, ctx.getString(R.string.limit_exceed), Toast.LENGTH_SHORT
                        ).show()
                    }
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
