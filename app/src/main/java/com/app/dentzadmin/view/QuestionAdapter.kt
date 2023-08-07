package com.app.dentzadmin.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.QuestionRead
import org.greenrobot.eventbus.EventBus


class QuestionAdapter(
    val ctx: Context, private val mList: List<QuestionRead>, val content: String
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.questions_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sharedPreference = ctx.getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val isAnswered = sharedPreference.getString("isAnswered", "")
        val ItemsViewModel = mList[position]
        holder.name.text = ItemsViewModel.question
        if (isAnswered.equals("")) {
            holder.name.setOnClickListener {
                val isAnswered = sharedPreference.getString("isAnswered", "")
                if (isAnswered.equals("")) {
                    EventBus.getDefault().post(ItemsViewModel.id)
                    var editor = sharedPreference.edit()
                    editor.putString("isAnswered", ItemsViewModel.id)
                    editor.commit()
                    notifyDataSetChanged()
                }
            }
        } else {
            if (isAnswered == ItemsViewModel.id) {
                holder.name.setTextColor(Color.parseColor("#ffffff"))
            } else {
                holder.name.setTextColor(Color.parseColor("#736F6F"))
            }

            val sdk = android.os.Build.VERSION.SDK_INT
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.fulllay.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        ctx, R.drawable.selectedcurvedcorner
                    )
                )
            } else {
                holder.fulllay.background =
                    ContextCompat.getDrawable(ctx, R.drawable.selectedcurvedcorner)
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
        val fulllay: LinearLayout = itemView.findViewById(R.id.fulllay)
    }
}

