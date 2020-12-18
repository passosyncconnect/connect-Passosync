package com.pasosync.pasosyncconnect.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.coach_list_item.view.*

class CoachListAdapter(options: FirestoreRecyclerOptions<ExploreCoachList>):
FirestoreRecyclerAdapter<ExploreCoachList, CoachListAdapter.CoachListViewHolder>(options) {

    private val db = FirebaseFirestore.getInstance()

    inner class CoachListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((ExploreCoachList) -> Unit)? = null
    fun setOnItemClickListener(listener: (ExploreCoachList) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachListViewHolder {
      return CoachListViewHolder(
          LayoutInflater.from(parent.context).inflate(
              R.layout.coach_list_item, parent, false
          )
      )
    }

    override fun onBindViewHolder(
        holder: CoachListViewHolder,
        position: Int,
        model: ExploreCoachList
    ) {
        holder.itemView.apply {
            Glide.with(context).load(model?.coachProfilePicUri).centerCrop().into(iv_images)
            coach_list_tv_name.text=model?.coachName
            coach_list_tv_description.text=model?.coachAbout
            coach_experience.text=model?.coachExperience+" yrs"
            tv_speciality_coach_list.text=model?.coachType

            try{

                val subscriberCountRef=db.collection("CoachSubscriberCount")
                    .document(model?.coachEmail).get().addOnSuccessListener {
                        val count=it.get("title")
                       subscriber_count.text=count.toString()



                    }

                val freeSubscriberCountRef=db.collection("FreeCoachSubscriberCount")
                    .document(model?.coachEmail).get().addOnSuccessListener {
                        val freeCount=it.get("free")
                        tv_follower_count.text=freeCount.toString()

                    }


            }catch (e:Exception){
                Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
            }


            setOnClickListener {
                onItemClickListener?.let {
                    it(model)
                }
            }

        }
    }
}