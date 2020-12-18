package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.GameScoreAdapter
import com.pasosync.pasosyncconnect.data.GameScoreDetails
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_practice.*

private const val TAG = "PracticeFragment"
class PracticeFragment : Fragment() {
    lateinit var gameScoreAdapter: GameScoreAdapter
    private val db = FirebaseFirestore.getInstance()
    val user= FirebaseAuth.getInstance().currentUser
    private val userGameScoreCollectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "PracticeScoreCardDetails"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun setUpRecyclerView() = rvPracticeScoreCard.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvPracticeScoreCard.size}")
        val query = userGameScoreCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<GameScoreDetails>()
            .setQuery(query, GameScoreDetails::class.java)
            .build()
        gameScoreAdapter = GameScoreAdapter(options)
        adapter = gameScoreAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_practice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        gameScoreAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("score", it)
            }
            findNavController().navigate(
                R.id.action_yourProgressFragment_to_scoreCardDetails2,
                bundle
            )


        }
    scoreFab.setOnClickListener {
         findNavController().navigate(R.id.action_yourProgressFragment_to_addGameScore)

    }
        videoFab.setOnClickListener {
findNavController().navigate(R.id.action_yourProgressFragment_to_gameProgressVideoUpload)
        }


    }
    override fun onStart() {
        super.onStart()
        gameScoreAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        gameScoreAdapter.stopListening()
    }

}