package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.GameScoreAdapter
import com.pasosync.pasosyncconnect.adapters.VideoProgressAdapter
import com.pasosync.pasosyncconnect.data.GameScoreDetails
import com.pasosync.pasosyncconnect.data.VideoProgressData
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game_video.*

private const val TAG = "GameVideoFragment"

class GameVideoFragment : Fragment(R.layout.fragment_game_video) {
    lateinit var videoProgressAdapter: VideoProgressAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val userGameVideoProgressCollectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "GameProgressVideoDetails"
        )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_game_video, container, false)
        setHasOptionsMenu(true)

        return v
    }

    private fun setUpRecyclerView() = rvGameVideo.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvGameVideo.size}")
        val query = userGameVideoProgressCollectionRef
        val options = FirestoreRecyclerOptions.Builder<VideoProgressData>()
            .setQuery(query, VideoProgressData::class.java)
            .build()
        videoProgressAdapter = VideoProgressAdapter(options)
        adapter = videoProgressAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        videoProgressAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("video",it)
            }
            findNavController().navigate(R.id.action_progressVideoFragment_to_videoProgressDetails,bundle)

        }


    }

    override fun onStart() {
        super.onStart()
        videoProgressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        videoProgressAdapter.stopListening()
    }


}