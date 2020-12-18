package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.CoachListAdapter
import com.pasosync.pasosyncconnect.adapters.FreeCoachListAdapter
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.coaches_list.*
import kotlinx.android.synthetic.main.coaches_list.rv_coach_list
import kotlinx.android.synthetic.main.coaches_list.view.*
import kotlinx.android.synthetic.main.free_coaches_list.*

class FreeCoachesList:Fragment(R.layout.free_coaches_list) {
    private val TAG = "FreeCoachesList"
    lateinit var coachListAdapter: CoachListAdapter
    private val db = FirebaseFirestore.getInstance()
    lateinit var auth: FirebaseAuth
    val user = FirebaseAuth.getInstance().currentUser
    private val coachDetailsRef =
        db.collection("UserDetails").document(user?.email.toString())
            .collection("FreeSubscribedCoach")
    var coachList = arrayListOf<ExploreCoachList>()
   // lateinit var freeCoachListAdapter: FreeCoachListAdapter
    private fun setUpRecyclerView() = rv_fragment_freeCoaches.apply {
        Log.d(TAG, "setUpRecyclerView: ${rv_fragment_freeCoaches.size}")
        val query = coachDetailsRef
        val options = FirestoreRecyclerOptions.Builder<ExploreCoachList>()
            .setQuery(query, ExploreCoachList::class.java)
            .build()
        coachListAdapter = CoachListAdapter(options)
        adapter = coachListAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onStart() {
        super.onStart()
        coachListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        coachListAdapter.stopListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        auth = FirebaseAuth.getInstance()
        (activity as AppCompatActivity).supportActionBar?.title = " "
//        rv_fragment_freeCoaches.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//        }
      //  showData()
        coachListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_freeCoachesList_to_freeLectureList,
                bundle
            )
        }
    }

//    private fun showData() {
//         val coachDetailsRef =
//            db.collection("CoachDetails")
//            .get().addOnSuccessListener {
//                for (document in it.documents) {
//                    val coachDetails =ExploreCoachList(
//                        document.getString("coachName"),
//                       "","",document.getString("coachAbout")
//                        ,document.getString("coachProfilePicUri"),""
//                    )
//
//                    coachList.add(coachDetails)
//                    Log.d(TAG, "showData: ${coachList.size}")
//                }
//                freeCoachListAdapter = FreeCoachListAdapter(coachList)
//                rv_fragment_freeCoaches.adapter = freeCoachListAdapter
////                dialog.dismiss()
//                    freeCoachListAdapter.setOnItemClickListener {
//                        val bundle = Bundle().apply {
//                            putSerializable("article", it)
//                        }
//                        findNavController().navigate(
//                            R.id.action_freeCoachesList_to_freeLectureList,
//                            bundle
//                        )
//                    }
//
//
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
////                dialog.dismiss()
//
//            }
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.free_coaches_list, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}