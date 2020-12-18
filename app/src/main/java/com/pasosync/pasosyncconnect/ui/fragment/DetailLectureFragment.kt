package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
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
import kotlinx.android.synthetic.main.lecture_details.*

class DetailLectureFragment : Fragment(R.layout.lecture_details) {
    private val TAG = "DetailLectureFragment"
    val args: DetailLectureFragmentArgs by navArgs()
    lateinit var mediaController: MediaController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.lecture_details, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaController = MediaController(requireContext())
        (activity as AppCompatActivity).supportActionBar?.title = " "
        lecture_details_tv_name.text = args.lectureDetails.lectureName
        lecture_details_tv_description.text = args.lectureDetails.lectureContent
        val videoUrl = args.lectureDetails.lectureVideoUrl?.toUri()
        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            video_lecture.visibility=View.VISIBLE
            video_lecture.setVideoURI(videoUrl)
            video_lecture.setMediaController(mediaController)
            mediaController.setAnchorView(video_lecture)
            mediaController.setMediaPlayer(video_lecture)
            this.video_lecture.setMediaController(mediaController)
            video_lecture.requestFocus()
            video_lecture.start()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}