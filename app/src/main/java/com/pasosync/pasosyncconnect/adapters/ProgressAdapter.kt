package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.LectureDetails
import com.pasosync.pasosyncconnect.data.ProgressDetails
import kotlinx.android.synthetic.main.progress_item.view.*

class ProgressAdapter(options: FirestoreRecyclerOptions<ProgressDetails>):
    FirestoreRecyclerAdapter<ProgressDetails,ProgressAdapter.ProgressViewHolder>(options) {



    inner class ProgressViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((ProgressDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (ProgressDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        return ProgressViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.progress_item, parent, false
            )
        )
    }
    override fun onBindViewHolder(
        holder: ProgressViewHolder,
        position: Int,
        model: ProgressDetails
    ) {
       holder.itemView.apply {
tv_title_progress_item.text=model?.progressTitle
           tv_readmore_progress_item.text=model?.progressDescription
               Glide.with(context).load(model?.progressImage).placeholder(R.drawable.progressbg).into(iv_progress_item)
           tv_date_progress_item.text=model?.date

           setOnClickListener {
               onItemClickListener?.let {
                   it(model)
               }
           }

       }
    }
}