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
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.VideoProgressAdapter
import com.pasosync.pasosyncconnect.data.VideoProgressData
import kotlinx.android.synthetic.main.fragment_game_video.*
import kotlinx.android.synthetic.main.fragment_practice_video.*

private const val TAG = "PracticeVideoFragment"

class PracticeVideoFragment:Fragment(R.layout.fragment_practice_video) {
    lateinit var videoProgressAdapter: VideoProgressAdapter
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val userPracticeVideoProgressectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "PracticeProgressVideoDetails"
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_practice_video, container, false)
        setHasOptionsMenu(true)

        return v
    }

    private fun setUpRecyclerView() = rvPracticeVideo.apply {
        Log.d(TAG, "setUpRecyclerView: ${rvPracticeVideo.size}")
        val query = userPracticeVideoProgressectionRef
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