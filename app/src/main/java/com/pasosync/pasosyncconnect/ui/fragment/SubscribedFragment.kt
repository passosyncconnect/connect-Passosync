package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.fragment_subscribed.*

class SubscribedFragment:Fragment(R.layout.fragment_subscribed) {
    private val TAG = "SubscribedFragment"
    lateinit var coachListAdapter: CoachListAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val subscribedCoachCollectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection("subscribedCoach")

    private fun setUpRecyclerView() = rv_fragment_subscribed.apply {
        Log.d(TAG, "setUpRecyclerView: ${rv_fragment_subscribed.size}")
        val query = subscribedCoachCollectionRef
        val options = FirestoreRecyclerOptions.Builder<ExploreCoachList>()
            .setQuery(query, ExploreCoachList::class.java)
            .build()
        coachListAdapter = CoachListAdapter(options)
        adapter = coachListAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        (activity as AppCompatActivity).supportActionBar?.title = " "
        coachListAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("subs", it)
            }
            findNavController().navigate(
                R.id.action_subscribedFragment_to_subscribedLectureList,
                bundle
            )
        }

    }


    override fun onStart() {
        super.onStart()
        coachListAdapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        coachListAdapter.stopListening()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_subscribed, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}