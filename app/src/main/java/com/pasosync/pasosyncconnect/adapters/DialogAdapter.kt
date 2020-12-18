package com.pasosync.pasosyncconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.coach_list_dialog_item.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogAdapter(var mList: List<ExploreCoachList>) :
    RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    private var db= FirebaseFirestore.getInstance()

    inner class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        return DialogViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coach_list_dialog_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.itemView.apply {
            coach_name.text=mList[position].coachName
            Glide.with(context).load(mList[position].coachProfilePicUri).into(profile_coach)
            send_button.setOnClickListener {


                var shareDetailsCollectionRef=db.collection("CoachNotifications")
                    .document(mList[position].coachEmail).collection("ShareDetailsNotifications")




            }


        }



    }


    private fun shareProgressDetailsToCoach(){
        CoroutineScope(Dispatchers.IO).launch {
            try {




            }catch (e:Exception){

            }


        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}