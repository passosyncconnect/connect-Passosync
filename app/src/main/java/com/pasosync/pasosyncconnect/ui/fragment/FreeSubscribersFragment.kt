package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pasosync.pasosyncconnect.R

class FreeSubscribersFragment:Fragment(R.layout.fragment_free_subscribers) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = " "
    }




}