package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.NotifyData
import kotlinx.android.synthetic.main.notification_layout.view.*

class FakeAdapter(
    var todos:List<NotifyData>
):RecyclerView.Adapter<FakeAdapter.FakeViewHolder>() {
    inner class FakeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FakeViewHolder {
        return FakeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: FakeViewHolder, position: Int) {
       holder.itemView.apply {
           notify_text.text="${todos[position].coachEmail} added a new Lecture"

       }
    }

    override fun getItemCount(): Int {
      return todos.size
    }
}