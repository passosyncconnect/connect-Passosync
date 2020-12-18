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
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.ProgressAdapter
import com.pasosync.pasosyncconnect.adapters.ViewPagerAdapter
import com.pasosync.pasosyncconnect.data.ProgressDetails
import kotlinx.android.synthetic.main.fragment_your_progress.*

class YourProgressFragment:Fragment(R.layout.fragment_your_progress) {
    private  val TAG = "YourProgressFragment"
//    lateinit var progressAdapter: ProgressAdapter
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val progressDetailsRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "ProgressList"
        )
//    private fun setUpRecyclerView() = rv_fragment_your_progress.apply {
//        Log.d(TAG, "setUpRecyclerView: ${rv_fragment_your_progress.size}")
//        val query = progressDetailsRef.orderBy("timestamp",Query.Direction.DESCENDING)
//        val options = FirestoreRecyclerOptions.Builder<ProgressDetails>()
//            .setQuery(query, ProgressDetails::class.java)
//            .build()
//        progressAdapter = ProgressAdapter(options)
//        adapter = progressAdapter
//        setHasFixedSize(true)
//        layoutManager = LinearLayoutManager(requireContext())
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_your_progress, container, false)
        setHasOptionsMenu(true)

        return v
    }



    override fun onStart() {
        super.onStart()
      //  progressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
       // progressAdapter.stopListening()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabs()



      //  setUpRecyclerView()
        (activity as AppCompatActivity).supportActionBar?.title = " "

//val fm=parentFragmentManager
//        val dialog=DialogFragmentCoachList()
//        progressAdapter.setOnItemClickListener {
//            dialog.show(fm,"new_dialog")
//        }


//        fab_progress.setOnClickListener {
//            findNavController().navigate(R.id.action_yourProgressFragment_to_addYourProgressFragment)
//        }

    }

    private fun setUpTabs() {
val tabAdapter= ViewPagerAdapter(childFragmentManager)
        tabAdapter.addFragment(GameFragment(),"Game")
        tabAdapter.addFragment(PracticeFragment(),"Practice")
viewPager2.adapter=tabAdapter
        tabsProgress.setupWithViewPager(viewPager2)
        tabsProgress.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_explore_24)
        tabsProgress.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_headset_mic_24)
    }

}