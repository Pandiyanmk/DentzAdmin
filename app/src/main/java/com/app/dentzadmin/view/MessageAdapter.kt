package com.app.dentzadmin.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.Message
import org.greenrobot.eventbus.EventBus


class MessageAdapter(val ctx: Context, private val mList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.name.text = "${position + 1}"

        if(ItemsViewModel.status == 1) {
            holder.fulllay.background =
                ContextCompat.getDrawable(ctx, R.drawable.curvedcorner)
        } else {
            holder.fulllay.background =
                ContextCompat.getDrawable(ctx, R.drawable.unselectedcurvedcorner)
        }
        holder.fulllay.setOnClickListener {
            EventBus.getDefault().post("message,$position")
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val fulllay: LinearLayout = itemView.findViewById(R.id.fulllay)
    }
}
