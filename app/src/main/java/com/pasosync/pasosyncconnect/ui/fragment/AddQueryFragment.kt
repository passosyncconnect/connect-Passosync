package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.add_query_layout.*


private const val TAG = "AddQueryFragment"
class AddQueryFragment:Fragment(R.layout.add_query_layout) {

    val args:AddQueryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.add_query_layout, container, false)
        setHasOptionsMenu(true)

        return v
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        heading.text="Adding a Query at ${args.query.timestamp}"


        action_ok.setOnClickListener {
            findNavController().navigate(R.id.action_addQueryFragment_to_videoProgressDetails)
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}