package com.pasosync.pasosyncconnect.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.QueryAdapter
import com.pasosync.pasosyncconnect.data.QueryData
import com.pasosync.pasosyncconnect.data.QueryDataRV
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_query_layout.*
import kotlinx.android.synthetic.main.all_lecture_details.*
import kotlinx.android.synthetic.main.coach_description.*
import kotlinx.android.synthetic.main.fragment_trim_video.view.*
import kotlinx.android.synthetic.main.video_progress_details_layout.*
import kotlinx.coroutines.withTimeout


private const val TAG = "VideoProgressDetails"

class VideoProgressDetails : Fragment(R.layout.video_progress_details_layout),
    DialogFragmentCoachList.OnInputSelected {

    val args: VideoProgressDetailsArgs by navArgs()
    lateinit var mediaController: MediaController

    var queryList = arrayListOf<QueryDataRV>()
    var timeAtPause = 0L


    override fun sendInput(input: String?, timeStamp: String?, millis: Long?) {
        Log.d(TAG, "sendInput: found Incoming Input:${input} ${timeStamp}")
        queryList.add(QueryDataRV(timeStamp, input, millis))
        progressVideoDetails


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.video_progress_details_layout, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        mediaController = MediaController(requireContext())
        val fm = parentFragmentManager
        val dialog = DialogFragmentCoachList()
        rvQueryList.setOnClickListener {
            Log.d(TAG, "onViewCreated: happening something")
        }

        rvQueryList.layoutManager = LinearLayoutManager(requireContext())


//        val bundle: Bundle? = arguments
//        val time = bundle?.getString("time")
//        val text = bundle?.getString("text")
//        Log.d(TAG, "onViewCreated: ${bundle?.getString("time")}")
//        Log.d(TAG, "onViewCreated: ${bundle?.getString("text")}")
//
//        queryList.add(QueryDataRV(time, text))

        var queryAdapter = QueryAdapter(queryList)
        rvQueryList.adapter = queryAdapter

        queryAdapter.notifyItemInserted(queryList.size)
        Log.d(TAG, "onViewCreated: ${queryList.size}")



        queryAdapter.setOnItemClickListener {
            Log.d(TAG, "adapterclick: ${it}")
            Log.d(TAG, "adapterclick: ${it}")


            progressVideoDetails.seekTo(it.toInt())
            progressVideoDetails.start()

        }


        val videoUrl = args.video.VideoUrlProgress?.toUri()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        progressVideoDetails.visibility = View.VISIBLE
        progressVideoDetails.setVideoURI(videoUrl)
        progressVideoDetails.setMediaController(mediaController)
        mediaController.setAnchorView(progressVideoDetails)
        mediaController.setMediaPlayer(progressVideoDetails)
        this.progressVideoDetails.setMediaController(mediaController)
        progressVideoDetails.requestFocus()
        progressVideoDetails.start()


        addQueryFab.setOnClickListener {
            if (queryList.size == 5) {

                Toast.makeText(
                    requireContext(),
                    "You cannot add More than five query",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                progressVideoDetails.pause()
                dialog.setTargetFragment(navHostFragment, 123)
                dialog.show(fm, "new_dialog")
                val millis = progressVideoDetails.currentPosition.toLong()
                val minutes = progressVideoDetails.currentPosition.toLong() / 1000 / 60
                val seconds = progressVideoDetails.currentPosition.toLong() / 1000 % 60

                if (seconds < 9) {
                    val timestamp = "0${minutes}:0${seconds}"
                    val bundle = Bundle()
                    bundle.putString("key", timestamp)
                    bundle.putLong("mili", millis)
                    dialog.arguments = bundle
                } else {
                    val timestamp = "0${minutes}:${seconds}"
                    val bundle = Bundle()
                    bundle.putString("key", timestamp)
                    bundle.putLong("mili", millis)
                    dialog.arguments = bundle
                }


            }



//            Log.d(TAG, "onViewCreated: 0${minutes}:0${seconds}")
//            val bundle=Bundle().apply {
//                putSerializable("query",QueryData(args.video.VideoUrlProgress,timestamp))
//            }
//            findNavController().navigate(R.id.action_videoProgressDetails_to_addQueryFragment,bundle)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}