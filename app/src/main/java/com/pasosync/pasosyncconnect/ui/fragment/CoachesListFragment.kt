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
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.CoachListAdapter
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.coaches_list.rv_coach_list
import kotlinx.android.synthetic.main.coaches_list.view.*

class CoachesListFragment : Fragment(R.layout.coaches_list) {
    private val TAG = "CoachesListFragment"
    lateinit var coachListAdapter: CoachListAdapter
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsRef =
        db.collection("CoachDetails")
    private fun setUpRecyclerView() = rv_coach_list.apply {
        Log.d(TAG, "setUpRecyclerView: ${rv_coach_list.size}")
        val query = coachDetailsRef
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
                putSerializable("coach", it)
            }
            findNavController().navigate(
                R.id.action_coachesListFragment_to_coachDescriptionFragment,
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
        val v: View = inflater.inflate(R.layout.coaches_list, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}