package com.pasosync.pasosyncconnect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.lecture_list_item.view.*

class SubsAdapter(var mList: List<LectureDetails>) :
    RecyclerView.Adapter<SubsAdapter.SubsAdapterViewHolder>() {

    private var onItemClickListener: ((LectureDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (LectureDetails) -> Unit) {
        onItemClickListener = listener
    }


    inner class SubsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubsAdapterViewHolder {
        return SubsAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.lecture_list_item, parent, false
            )

        )
    }

    override fun onBindViewHolder(holder: SubsAdapterViewHolder, position: Int) {
        holder.itemView.apply {
            tv_title_lecture_list_item.text = mList[position].lectureName
            tv_description_lecture_list_item.text = mList[position].lectureContent
            tv_date_lecture_list_item.text = mList[position].date
            tv_lecture_list_item_type.text=mList[position].seacrh
            Glide.with(context).load(mList[position].lectureImageUrl).centerCrop().placeholder(R.drawable.progressbg)
                .into(iv_lecture_list_item)
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