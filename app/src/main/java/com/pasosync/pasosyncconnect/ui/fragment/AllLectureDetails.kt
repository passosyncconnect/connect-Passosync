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
import kotlinx.android.synthetic.main.all_lecture_details.*
import kotlinx.android.synthetic.main.free_lecture_details.*


private const val TAG = "AllLectureDetails"
class AllLectureDetails:Fragment(R.layout.all_lecture_details) {

val args:AllLectureDetailsArgs by navArgs()
    lateinit var mediaController: MediaController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.lec.coachName}")
        (activity as AppCompatActivity).supportActionBar?.title = " "
        mediaController = MediaController(requireContext())
        all_lecture_details_tv_name.text = args.lec.lectureName
        all_lecture_details_tv_description.text = args.lec.lectureContent
        val videoUrl = args.lec.lectureVideoUrl?.toUri()
        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            all_lecture_video_lecture.visibility=View.VISIBLE
            all_lecture_video_lecture.setVideoURI(videoUrl)
            all_lecture_video_lecture.setMediaController(mediaController)
            mediaController.setAnchorView(all_lecture_video_lecture)
            mediaController.setMediaPlayer(all_lecture_video_lecture)
            this.all_lecture_video_lecture.setMediaController(mediaController)
            all_lecture_video_lecture.requestFocus()
            all_lecture_video_lecture.start()
            all_lecture_video_lecture.currentPosition


        }



    }











    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.all_lecture_details, container, false)
        setHasOptionsMenu(true)
        return v
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}