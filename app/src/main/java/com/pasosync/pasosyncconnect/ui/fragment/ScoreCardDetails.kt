package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.free_lecture_details.*
import kotlinx.android.synthetic.main.score_card_details.*

private const val TAG = "ScoreCardDetails"

class ScoreCardDetails : Fragment(R.layout.score_card_details) {

    val args: ScoreCardDetailsArgs by navArgs()
    lateinit var mediaController: MediaController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.score_card_details, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.score.ballPlayed}")
        tvMyTotalScore.text = "${args.score.myTeamScore}(${args.score.totalOver} Overs)"
        tvOpponentTotalScore.text = "${args.score.rivalScore}(${args.score.totalOver} Overs)"
        myRunsData.text = "${args.score.myRuns}(${args.score.ballPlayed} Balls)"
        myWicketTakenData.text = "${args.score.myWicketTaken} wickets"
        itemMyTeamName.text=args.score.myTeamName
        iteOpponentName.text=args.score.rivalTeamName
        myFoursAndSixesData.text = "${args.score.myFours} Fours/${args.score.mySixes} Sixes"
        Glide.with(requireContext()).load(args.score.gameImageUrl)
            .placeholder(R.drawable.progressbg).into(
                ivImage
            )


        mediaController = MediaController(requireContext())
        val videoUrl=args.score.gameVideoUrl?.toUri()

        if (videoUrl == null) {
            Snackbar.make(requireView(), "there is no video", Snackbar.LENGTH_LONG).show()
        } else {
            videoScoreCardDetails.visibility=View.VISIBLE
            videoScoreCardDetails.setVideoURI(videoUrl)
            videoScoreCardDetails.setMediaController(mediaController)
            mediaController.setAnchorView(videoScoreCardDetails)
            mediaController.setMediaPlayer(videoScoreCardDetails)
            this.videoScoreCardDetails.setMediaController(mediaController)
            videoScoreCardDetails.requestFocus()
            videoScoreCardDetails.start()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}