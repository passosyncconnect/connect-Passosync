package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.lecture_list_item.view.*

class LectureListAdapter(options: FirestoreRecyclerOptions<LectureDetails>) :
    FirestoreRecyclerAdapter<LectureDetails, LectureListAdapter.LectureListViewHolder>(options) {

    inner class LectureListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((LectureDetails) -> Unit)? = null
    fun setOnItemClickListener(listener: (LectureDetails) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureListViewHolder {
        return LectureListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.lecture_list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LectureListViewHolder,
        position: Int,
        model: LectureDetails
    ) {
        holder.itemView.apply {
            tv_title_lecture_list_item.text = model?.lectureName
            tv_description_lecture_list_item.text = model?.lectureContent
            tv_date_lecture_list_item.text = model?.date

            Glide.with(context).load(model?.lectureImageUrl)
                .placeholder(R.drawable.ic_baseline_person_pin_24).centerCrop()
                .into(iv_lecture_list_item)
            setOnClickListener {
                onItemClickListener?.let {
                    it(model)
                }
            }



        }

    }
}