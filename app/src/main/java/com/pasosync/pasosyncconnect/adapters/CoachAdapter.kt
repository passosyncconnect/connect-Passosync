package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.coach_list_item.view.*

class CoachAdapter(var mList: List<ExploreCoachList>):
RecyclerView.Adapter<CoachAdapter.CoachViewHolder>(){

    inner class CoachViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachViewHolder {
        return CoachViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coach_list_item,parent,false)
        )


    }

    override fun onBindViewHolder(holder: CoachViewHolder, position: Int) {
        holder.itemView.apply {
            coach_list_tv_name.text=mList[position].coachName

        }



    }

    override fun getItemCount(): Int {
       return mList.size
    }

}