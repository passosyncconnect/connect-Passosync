package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.notification_layout.view.*

class NotificationListAdapter(options: FirestoreRecyclerOptions<ExploreCoachList>) :
    FirestoreRecyclerAdapter<ExploreCoachList, NotificationListAdapter.NotificationListViewHolder>(options) {
    inner class NotificationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private var onItemClickListener: ((ExploreCoachList) -> Unit)? = null
    fun setOnItemClickListener(listener: (ExploreCoachList) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_layout, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: NotificationListViewHolder,
        position: Int,
        model: ExploreCoachList
    ) {
        holder.itemView.apply {
            notify_text.text="${model?.coachEmail} added a New Lecture"
            setOnClickListener {
                onItemClickListener?.let {
                   it(model)
                }
            }
        }

    }

}