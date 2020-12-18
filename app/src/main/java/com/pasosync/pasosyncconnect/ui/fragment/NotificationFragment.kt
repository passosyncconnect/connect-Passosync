package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.NotificationListAdapter
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.fargment_notifiaction.*

class NotificationFragment : Fragment(R.layout.fargment_notifiaction) {
    private val TAG = "NotificationFragment"
    private val db = FirebaseFirestore.getInstance()
   var notificationListAdapter: NotificationListAdapter? = null
    val user = FirebaseAuth.getInstance().currentUser
    private val subscribedCoachCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())
            .collection("subscribedCoach")
    var email: String? = null
    var name:String?=null
    var query = subscribedCoachCollectionRef.get()
    override fun onStart() {
        super.onStart()
        notificationListAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        notificationListAdapter?.stopListening()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fargment_notifiaction, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = " "
        val querySnapshot = subscribedCoachCollectionRef.get().addOnSuccessListener {
            for (document in it.documents) {
                var docemail = document.getString("coachEmail")
                email = docemail
                Log.d(TAG, "setup: $email")
                val notifyCollectionRef =
                    email?.let {
                        Log.d(TAG, "onViewCreated: $email")
                        db.collection("Notification")
                            .document("virsehwag@gmail.com").collection(
                                "NotificationLecture"
                            )
                    }
                val query = notifyCollectionRef
                var options = query?.let {
                    FirestoreRecyclerOptions.Builder<ExploreCoachList>()
                        .setQuery(it, ExploreCoachList::class.java).build()
                }
                notificationListAdapter = options?.let {
                    Log.d(TAG, "note:$ ${it.hashCode()}")
                    Log.d(TAG, "onViewCreated: $email")
                    NotificationListAdapter(it)
                }!!
                notification_recyclerview.apply {
                    adapter = notificationListAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                }


            }
            Log.d(TAG, "outside:$email ")
            name=email

        }
        Log.d(TAG, "name:$name ")



//        notification_recyclerview.apply {
//            //  getEmail()
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val querySnapshot=subscribedCoachCollectionRef.get().await()
//                    for(document in querySnapshot.documents){
//                        var docemail=document.getString("coachEmail")
//                        email=docemail
//                        Log.d(TAG, "setup: $email")
//                                val notifyCollectionRef =
//                                    email?.let {
//                                        Log.d(TAG, "onViewCreated: $email")
//                                        db.collection("Notification")
//                                            .document(it).collection(
//                                                "NotificationLecture"
//                                            )
//                                    }
//
//                        withContext(Dispatchers.Main){
//                            val query = notifyCollectionRef
//                            var options = query?.let {
//                                FirestoreRecyclerOptions.Builder<ExploreCoachList>()
//                                    .setQuery(it, ExploreCoachList::class.java).build()
//                            }
//                            notificationListAdapter = options?.let {
//                                Log.d(TAG, "note:$ ${it.hashCode()}")
//                                Log.d(TAG, "onViewCreated: $email")
//                                NotificationListAdapter(it) }!!
//
//                            adapter = notificationListAdapter
//                            setHasFixedSize(true)
//                            layoutManager = LinearLayoutManager(requireContext())
//                        }
//
//                    }
//                }catch (e:Exception){
//                    Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
//                }
//                Log.d(TAG, "emailfromdetup: $email")
//            }
//
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}