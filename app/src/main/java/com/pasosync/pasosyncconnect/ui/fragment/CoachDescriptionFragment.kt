package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.FreeSubscribedCoachEmail
import com.pasosync.pasosyncconnect.data.SubscribedCoachEmail
import com.pasosync.pasosyncconnect.data.UserDetails
import kotlinx.android.synthetic.main.activity_phone_login.*
import kotlinx.android.synthetic.main.coach_description.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class CoachDescriptionFragment : Fragment(R.layout.coach_description) {
    private val TAG = "CoachDescriptionFragment"
    val args: CoachDescriptionFragmentArgs by navArgs()
    lateinit var mediaController: MediaController
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var emailList = arrayListOf<String>()
    var freeEmailList = arrayListOf<String>()
    lateinit var builder: AlertDialog.Builder
    var free_count: Long = 0
    lateinit var dialog: AlertDialog
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.coach_description, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ${args.coach.coachEmail}")
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        confirmInput()
        freeConfirmInput()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        val coach = args.coach
        mediaController = MediaController(requireContext())
        coach_description_tv_name.text = coach.coachName
        coach_description_tv_description.text = coach.coachAbout
        val videoUrl = coach.coachIntroVideoUri?.toUri()
        Log.d(TAG, "onViewCreated: ${args.coach.coachType}")
        Log.d(TAG, "onViewCreated: ${args.coach.coachExperience}")

        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            video_coach_description.visibility = View.VISIBLE
            video_coach_description.setVideoURI(videoUrl)
            video_coach_description.setMediaController(mediaController)
            mediaController.setAnchorView(video_coach_description)
            mediaController.setMediaPlayer(video_coach_description)
            this.video_coach_description.setMediaController(mediaController)
            video_coach_description.requestFocus()
            video_coach_description.start()
            Log.d(TAG, "videoposition:${video_coach_description.currentPosition} ")
        }
        btn_subs.setOnClickListener {
            val splash: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1000)
                        dialog.show()
                    } catch (e: Exception) {

                    }
                }
            }
            splash.start()
            if (confirmInput()) {
                Snackbar.make(requireView(), "Already Subscribed", Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()

            } else {
                val bundle = Bundle().apply {
                    val name = args.coach.coachName
                    val about = args.coach.coachAbout
                    val email = args.coach.coachEmail
                    val mobile = args.coach.coachMobile
                    val pic = args.coach.coachProfilePicUri
                    val video = args.coach.coachIntroVideoUri
                    val type=args.coach.coachType
                    val experience=args.coach.coachExperience



                    val exploreCoachList = ExploreCoachList(
                        name, email, mobile, about, pic, video, 0L, 0L, experience, type
                    )
                    putSerializable("lecture", exploreCoachList)
                    Log.d(TAG, "onViewCreated: ${args.coach.coachEmail}")
                }
//            findNavController().navigate(R.id.action_coachDescriptionFragment_to_lecturesFragment,bundle)
                findNavController().navigate(
                    R.id.action_coachDescriptionFragment_to_purchaseFragment,
                    bundle
                )
                dialog.dismiss()

            }


        }
        btn_subs_free.setOnClickListener {
            Log.d(TAG, "videoposition:${video_coach_description.currentPosition} ")
            val minutes = video_coach_description.currentPosition.toLong() / 1000 / 60
            val seconds =  video_coach_description.currentPosition.toLong() / 1000 % 60
            Log.d(TAG, "onViewCreated: 0${minutes}:0${seconds}")

            val splash: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1000)
                        dialog.show()
                    } catch (e: Exception) {

                    }
                }
            }
            splash.start()
            if (freeConfirmInput()) {
                Snackbar.make(
                    requireView(),
                    "Already Subscribed for Free Content",
                    Snackbar.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            } else {
                val bundle = Bundle().apply {
                    val name = args.coach.coachName
                    val about = args.coach.coachAbout
                    val email = args.coach.coachEmail
                    val mobile = args.coach.coachMobile
                    val pic = args.coach.coachProfilePicUri
                    val video = args.coach.coachIntroVideoUri
                    val type=args.coach.coachType
                    val experience=args.coach.coachExperience
                    val exploreCoachList = ExploreCoachList(
                        name,
                        email,
                        mobile,
                        about,
                        pic,
                        video,
                        0L,
                        0L,
                        experience,
                        type
                    )
                    putSerializable("article", exploreCoachList)
                    Log.d(TAG, "onViewCreated: ${args.coach.coachEmail}")


                    val subscribedCoachCollectionRef =
                        db.collection("UserDetails").document(user?.email.toString())
                            .collection("FreeSubscribedCoach")
                    val data = subscribedCoachCollectionRef.add(exploreCoachList)
                    val freeSubscriberCount = db.collection("FreeCoachSubscriberCount")
                        .document(email).get().addOnSuccessListener {
                            val count = it.get("free")
                            free_count = count as Long
                            val count_f: HashMap<String, Any> = HashMap()
                            count_f["free"]=free_count+1
                            val freeAddCountRef= db.collection("FreeCoachSubscriberCount")
                                .document(email).set(count_f)

                        }
                    retrieveUserDetails()


                }

                findNavController().navigate(
                    R.id.action_coachDescriptionFragment_to_freeLectureList,
                    bundle
                )

            }
        }

    }

    private fun confirmInput(): Boolean {
        if (!avoidDuplicateEntries()) {
            return false
        }
        return true

    }

    private fun freeConfirmInput(): Boolean {
        if (!checkingFree()) {
            return false
        }
        return true
    }

    private fun checkingFree(): Boolean {
        val freeEmail: String? = ""
        var refer = db.collection("UserDetails").document(user?.email.toString())
            .collection("FreeSubscribedCoach").whereEqualTo("coachEmail", args.coach.coachEmail)
            .get()
            .addOnSuccessListener {
//
                for (document in it.documents) {
                    val subscribedCoachEmail = FreeSubscribedCoachEmail(
                        document.getString("coachEmail")
                    )
                    freeEmailList.add(document.get("coachEmail") as String)
                    Log.d(TAG, "avoidDuplicateEntries: ${freeEmailList.size}")
                }


            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        return freeEmailList.contains(args.coach.coachEmail)
    }


    private fun avoidDuplicateEntries(): Boolean {
        val email: String? = ""
        var refer = db.collection("UserDetails").document(user?.email.toString())
            .collection("subscribedCoach").whereEqualTo("coachEmail", args.coach.coachEmail).get()
            .addOnSuccessListener {
//
                for (document in it.documents) {
                    val subscribedCoachEmail = SubscribedCoachEmail(
                        document.getString("coachEmail")
                    )
                    emailList.add(document.get("coachEmail") as String)
                    Log.d(TAG, "avoidDuplicateEntries: ${emailList.size}")
                }


            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        return emailList.contains(args.coach.coachEmail)


    }

    private fun retrieveUserDetails() = CoroutineScope(Dispatchers.IO).launch {

        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_age: String? = null
            var userpic:String?=null
            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->
                val age = document.getString("userAge")
                val email = document.getString("userEmail")
                val mobile = document.getString("userMobile")
                val profileName = document.getString("userName")
                val profilepic=document.getString("userPicUri")
                //  val profileUserPic = document.getString("userPicUri")
                profile_name = profileName
                Log.d(TAG, "retrieveUserDetails: $profile_name")
                profile_age = age
                profile_mobile = mobile
                profile_email = user?.email
                userpic=profilepic
                val userDetails = UserDetails(
                    profile_name,
                    email,
                    profile_mobile,
                    profile_age,
                    profilepic
                )
                val userDetailsToCoach = db.collection("CoachLectureList")
                    .document(args.coach.coachEmail).collection("FreeSubscribedUsers")
                val userdata = userDetailsToCoach.add(userDetails)
            }.await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
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