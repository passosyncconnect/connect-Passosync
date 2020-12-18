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
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.data.LectureDetails
import kotlinx.android.synthetic.main.subscribed_lecture_details.*

class SubscribedLectureDetails:Fragment(R.layout.subscribed_lecture_details) {
    private  val TAG = "SubscribedLectureDetail"
    val args:SubscribedLectureDetailsArgs by navArgs()
    lateinit var mediaController: MediaController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.lec.lecturePdfUrl}")
        mediaController = MediaController(requireContext())

        (activity as AppCompatActivity).supportActionBar?.title = " "
        subs_lecture_details_tv_name.text = args.lec.lectureName
        subs_lecture_details_tv_description.text = args.lec.lectureContent
        val videoUrl = args.lec.lectureVideoUrl?.toUri()
        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            subs_video_lecture.visibility=View.VISIBLE
            subs_video_lecture.setVideoURI(videoUrl)
            subs_video_lecture.setMediaController(mediaController)
            mediaController.setAnchorView(subs_video_lecture)
            mediaController.setMediaPlayer(subs_video_lecture)
            this.subs_video_lecture.setMediaController(mediaController)
            subs_video_lecture.requestFocus()
            subs_video_lecture.start()
        }
        subs_btn_open_pdf.setOnClickListener {
            val bundle = Bundle().apply {
                val name = args.lec.lectureName
                val about = args.lec.lectureContent
                val date = args.lec.date
                val video = args.lec.lectureVideoUrl
                val pic = args.lec.lectureImageUrl
                val pdf = args.lec.lecturePdfUrl
               val lectureDetails=LectureDetails(name,about,pic,video,pdf,date)
                putSerializable("lec", lectureDetails)
                Log.d(TAG, "onViewCreated: ${args.lec.lecturePdfUrl}")
            }
//            findNavController().navigate(R.id.action_coachDescriptionFragment_to_lecturesFragment,bundle)
            findNavController().navigate(
                R.id.action_subscribedLectureDetails_to_pdfFragment,
                bundle
            )


        }


    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.subscribed_lecture_details, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}