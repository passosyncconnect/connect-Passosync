package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.VideoViewPagerAdapter
import com.pasosync.pasosyncconnect.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_video_progress.*
import kotlinx.android.synthetic.main.fragment_your_progress.*


private const val TAG = "ProgressVideoFragment"
class ProgressVideoFragment:Fragment(R.layout.fragment_video_progress) {
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()






    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_video_progress, container, false)
        setHasOptionsMenu(true)

        return v
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = " "
setUpTabs()


    }

    private fun setUpTabs() {
        val tabAdapter= VideoViewPagerAdapter(childFragmentManager)
        tabAdapter.addFragment(GameVideoFragment(),"Game")
        tabAdapter.addFragment(PracticeVideoFragment(),"Practice")
        viewPager2Video.adapter=tabAdapter
        tabsProgressVideo.setupWithViewPager(viewPager2Video)
        tabsProgressVideo.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_ondemand_video_24)
        tabsProgressVideo.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_ondemand_video_24)
    }


}