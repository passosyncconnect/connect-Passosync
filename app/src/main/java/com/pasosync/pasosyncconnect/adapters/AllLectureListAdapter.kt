package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.AllLectureDetails
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.all_lecture_list_item.view.*

class AllLectureListAdapter(var mList: List<AllLectureDetails>) :
    RecyclerView.Adapter<AllLectureListAdapter.AllLectureListViewHolder>() {
    private var onItemClickListener: ((AllLectureDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (AllLectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    inner class AllLectureListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllLectureListViewHolder {
        return AllLectureListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.all_lecture_list_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: AllLectureListViewHolder, position: Int) {
        holder.itemView.apply {
            tv_all_title_lecture_list_item.text=mList[position].lectureName
            tv_all_description_lecture_list_item.text=mList[position].lectureContent

            tv_all_date_lecture_list_item.text=mList[position].date
            Glide.with(context).load(mList[position].lectureImageUrl).centerCrop().placeholder(R.drawable.progressbg).into(iv_all_lecture_list_item)
            Glide.with(context).load(mList[position].coachProfilePicUri)
                .into(all_lecture_coach_profile_pic)
            tv_all_lecture_coach_name.text="Created by ${mList[position].coachName}"
            tv_all_lecture_list_item_type.text=mList[position].seacrh
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