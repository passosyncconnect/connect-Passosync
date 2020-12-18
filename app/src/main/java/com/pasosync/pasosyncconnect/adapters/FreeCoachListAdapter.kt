package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.coach_list_item.view.*

class FreeCoachListAdapter(var mList:List<ExploreCoachList>)
    :RecyclerView.Adapter<FreeCoachListAdapter.FreeCoachListViewHolder>() {

    inner class FreeCoachListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)


    private var onItemClickListener: ((ExploreCoachList) -> Unit)? = null
    fun setOnItemClickListener(listener: (ExploreCoachList) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeCoachListViewHolder {
        return FreeCoachListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.coach_list_item,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: FreeCoachListViewHolder, position: Int) {
        holder.itemView.apply {
            coach_list_tv_name.text=mList[position].coachName
            coach_list_tv_description.text=mList[position].coachAbout
            Glide.with(context).load(mList[position].coachProfilePicUri).into(iv_images)
            setOnClickListener {
                onItemClickListener?.let {
                    it(mList[position])
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}