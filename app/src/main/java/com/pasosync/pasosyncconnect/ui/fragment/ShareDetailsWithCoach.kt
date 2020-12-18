package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.DialogAdapter
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import kotlinx.android.synthetic.main.coach_list_dialog.view.*


private const val TAG = "ShareDetailsWithCoach"
class ShareDetailsWithCoach:Fragment(R.layout.coach_list_dialog) {

    val user = FirebaseAuth.getInstance().currentUser

    var coachList = arrayListOf<ExploreCoachList>()
    lateinit var dialogAdapter: DialogAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.coach_list_dialog, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}