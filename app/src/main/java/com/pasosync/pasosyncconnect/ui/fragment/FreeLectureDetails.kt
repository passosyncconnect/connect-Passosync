package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.free_lecture_details.*
import kotlinx.android.synthetic.main.subscribed_lecture_details.*

class FreeLectureDetails:Fragment(R.layout.free_lecture_details) {
    private  val TAG = "FreeLectureDetails"
    val args:FreeLectureDetailsArgs by navArgs()
    lateinit var mediaController: MediaController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.lec.lectureName}")
        Log.d(TAG, "onViewCreated: ${args.lec.lectureContent}")
        mediaController = MediaController(requireContext())
        free_lecture_details_tv_name.text = args.lec.lectureName
        free_lecture_details_tv_description.text = args.lec.lectureContent
        val videoUrl = args.lec.lectureVideoUrl?.toUri()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            free_video_lecture.visibility=View.VISIBLE
            free_video_lecture.setVideoURI(videoUrl)
            free_video_lecture.setMediaController(mediaController)
            mediaController.setAnchorView(free_video_lecture)
            mediaController.setMediaPlayer(free_video_lecture)
            this.free_video_lecture.setMediaController(mediaController)
            free_video_lecture.requestFocus()
            free_video_lecture.start()
        }


    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.free_lecture_details, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}