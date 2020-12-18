package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.SubsAdapter
import com.pasosync.pasosyncconnect.data.AllLectureDetails
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.LectureDetails
import com.pasosync.pasosyncconnect.data.UserDetails
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.lecture_list_subscribed.*
import kotlinx.android.synthetic.main.purchase_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PurchaseFragment : Fragment(R.layout.purchase_layout) {
    private val TAG = "PurchaseFragment"
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    var lectureList = arrayListOf<LectureDetails>()
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    var profile_user_pic: String? = null
    var subs_count:Long=0
    var free_count:Long=0
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())

    val args: PurchaseFragmentArgs by navArgs()
    // val email=args.lecture.coachEmail


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.purchase_layout, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ${subs_count}")

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.layout_payment_dialog)
        dialog = builder.create()
        (activity as AppCompatActivity).supportActionBar?.title = " "

        Log.d(TAG, "onViewCreated: ${subs_count}")
        Log.d(TAG, "onViewCreated: ${args.lecture.coachType}")
        Log.d(TAG, "onViewCreated: ${args.lecture.coachExperience}")

        btnBuy.setOnClickListener {
            dialog.show()
            val splash: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(4000)
                        val bundle = Bundle().apply {
                            val name = args.lecture.coachName
                            val about = args.lecture.coachAbout
                            val mobile = args.lecture.coachMobile
                            val image = args.lecture.coachProfilePicUri
                            val video = args.lecture.coachIntroVideoUri
                            val email = args.lecture.coachEmail
                            val type=args.lecture.coachType
                            val experience=args.lecture.coachExperience

                            val subscriberCountRef=db.collection("CoachSubscriberCount")
                                .document(email).get().addOnSuccessListener {
                                    val count=it.get("title")
                                    Log.d(TAG, "purc: ${count}")
                                    subs_count= count  as Long
                                    Log.d(TAG, "pursubs: ${subs_count}")

                                    val count_s: HashMap<String, Any> = HashMap()
                                    count_s["title"]=subs_count+1
                                    db.collection("CoachSubscriberCount").document(email)
                                        .set(count_s)


                                }

                            val exploreCoachList =
                                ExploreCoachList(name, email, mobile, about, image, video,subs_count,0L,experience,type)
                            putSerializable("lecture", exploreCoachList)
                            val subscribedCoachCollectionRef =
                                db.collection("UserDetails").document(user?.email.toString())
                                    .collection("subscribedCoach")
                            val data = subscribedCoachCollectionRef.add(exploreCoachList)
                            showData()
                            retrieveUserDetails()


                            Log.d(TAG, "bundle:${args.lecture.coachEmail}")
                        }
                        findNavController().navigate(
                            R.id.action_purchaseFragment_to_lecturesFragment,
                            bundle
                        )
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Subscription Successful",
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {

                    }


                }

            }
            splash.start()


        }




    }

    private fun getSubscriberCount(){
        try {
            val emailForCount=args.lecture.coachEmail
            val subscriberCountRef=db.collection("CoachSubscriberCount")
                .document(emailForCount).get().addOnSuccessListener {
                 val count=it.get("title")
                    Log.d(TAG, "getSubscriberCount: ${count}")
                    subs_count= count as Long
                    Log.d(TAG, "getSubscriberCount: ${subs_count}")


                }



        }catch (e:Exception){
             Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }

    }

    private fun showData() {
        val lecture = args.lecture
        Log.d(TAG, "email: ${lecture.coachEmail}")
        val lectureRef = db.collection("CoachLectureList")
            .document(lecture.coachEmail.toString()).collection("PaidLecture")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val lectureDetails = AllLectureDetails(
                        document.getString("lectureName"),
                        document.getString("lectureContent"),
                        document.getString("lectureImageUrl"),
                        document.getString("lectureVideoUrl"),
                        document.getString("lecturePdfUrl"),
                        document.getString("date"),
                        document.getString("seacrh"),
                        document.getString("type"),args.lecture.coachName,args.lecture.coachProfilePicUri,args.lecture.coachEmail
                    )
                    val newref=db.collection("UserDetails").document(user?.email.toString()).collection("alllecture")
                    newref.add(lectureDetails).addOnSuccessListener {

                    }

                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

    }

    private fun retrieveUserDetails() = CoroutineScope(Dispatchers.IO).launch {
        Log.d(TAG, "retrieveUserDetails: ${args.lecture.coachEmail}")
        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_age: String? = null
            var profilepic:String?=null
            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val age = document.getString("userAge")
                val email = document.getString("userEmail")
                val mobile = document.getString("userMobile")
                val profileName = document.getString("userName")
                  val profileUserPic = document.getString("userPicUri")
                profile_name = profileName
                Log.d(TAG, "retrieveUserDetails: $profile_name")
                profile_age = age
                profile_mobile = mobile
                profile_email = user?.email
                profilepic=profileUserPic
                val userDetails=UserDetails(profile_name,email,profile_mobile,profile_age,profilepic)
                val userDetailsToCoach = db.collection("CoachLectureList")
                    .document(args.lecture.coachEmail).collection("subscribedUserDetails")
                val userdata = userDetailsToCoach.add(userDetails)
            }.await()
            withContext(Dispatchers.Main) {
                 Toast.makeText(requireContext(),"Success",Toast.LENGTH_SHORT).show()
            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}