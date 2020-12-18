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
import kotlinx.android.synthetic.main.subscribe_now.view.*

class ExploreCoachListAdapter(options: FirestoreRecyclerOptions<ExploreCoachList>):
FirestoreRecyclerAdapter<ExploreCoachList,ExploreCoachListAdapter.ExploreCoachListViewHolder>(options){

    inner class ExploreCoachListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    private var onItemClickListener: ((ExploreCoachList) -> Unit)? = null
    fun setOnItemClickListener(listener: (ExploreCoachList) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreCoachListViewHolder {
        return ExploreCoachListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.subscribe_now, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ExploreCoachListViewHolder,
        position: Int,
        model: ExploreCoachList
    ) {
        holder.itemView.apply {
            tv_explore_coach_list.text=model?.coachName
            Glide.with(context).load(model?.coachProfilePicUri).centerCrop().into(iv_explore_coach_list)
            setOnClickListener {
                onItemClickListener?.let {
                    it(model)
                }
            }

        }
    }
}