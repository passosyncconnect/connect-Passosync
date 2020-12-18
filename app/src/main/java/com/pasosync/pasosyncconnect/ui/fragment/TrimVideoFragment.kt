package com.pasosync.pasosyncconnect.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.fragment_trim_video.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds


private const val TAG = "TrimVideoFragment"
class TrimVideoFragment:Fragment(R.layout.fragment_trim_video) {
    val args:TrimVideoFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_trim_video, container, false)
        setHasOptionsMenu(true)
        return v
    }

    @ExperimentalTime
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path=args.trim.videoUri
        val uri=Uri.parse(path)
        Log.d(TAG, "onViewCreated: $uri")
        Log.d(TAG, "onViewCreated: $path")
        trimVideoPlayer.setVideoURI(uri)

        textViewss.setOnClickListener {
            trimVideoPlayer.currentPosition.seconds
            Log.d(TAG, "onViewCreated: ${trimVideoPlayer.currentPosition.seconds}")
        }
        trimVideoPlayer.start()


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}