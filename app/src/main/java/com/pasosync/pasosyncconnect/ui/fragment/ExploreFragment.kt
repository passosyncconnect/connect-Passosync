package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.ExploreCoachListAdapter
import com.pasosync.pasosyncconnect.adapters.NewsAdapter
import com.pasosync.pasosyncconnect.data.ExploreCoachList
import com.pasosync.pasosyncconnect.other.Others.REQUEST_CODE_WRITING
import com.pasosync.pasosyncconnect.other.Permissions
import com.pasosync.pasosyncconnect.other.Resource
import com.pasosync.pasosyncconnect.ui.MainActivity
import com.pasosync.pasosyncconnect.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class ExploreFragment : Fragment(R.layout.fragment_explore),EasyPermissions.PermissionCallbacks {
    private val TAG = "ExploreFragment"
    lateinit var viewModel: MainViewModels
    lateinit var newsAdapter: NewsAdapter
    lateinit var exploreCoachListAdapter: ExploreCoachListAdapter
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsRef =
        db.collection("CoachDetails")
    private val subscribedCoachCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())
            .collection("subscribedCoach")
    var email:String?=null

    private fun getEmail()= CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot=subscribedCoachCollectionRef.get().await()
            for(document in querySnapshot.documents){
                var docemail=document.getString("coachEmail")
                email=docemail
                Log.d(TAG, "getEmail: $email")
            }
        }catch (e:Exception){
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()
        }
    }





    private fun setUpRecyclerView() = rv_coach_list_explore.apply {
        Log.d(TAG, "setUpRecyclerView: ${rv_coach_list_explore.size}")
        val query = coachDetailsRef
        val options = FirestoreRecyclerOptions.Builder<ExploreCoachList>()
            .setQuery(query, ExploreCoachList::class.java)
            .build()
        exploreCoachListAdapter = ExploreCoachListAdapter(options)
        adapter = exploreCoachListAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_explore, container, false)
        setHasOptionsMenu(true)

        return v
    }
    override fun onStart() {
        super.onStart()
        exploreCoachListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        exploreCoachListAdapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = user?.uid

        Log.d(TAG, "onViewCreated: $id")
        getEmail()
        requestPermissions()
        viewModel = (activity as MainActivity).viewmodel
        setUpRecyclerViewNews()
        setUpRecyclerView()
        (activity as AppCompatActivity).supportActionBar?.title = " "
        viewModel.cricketNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
        exploreCoachListAdapter.setOnItemClickListener {
val bundle=Bundle().apply {
    putSerializable("coach",it)
}
            findNavController().navigate(R.id.action_exploreFragment_to_coachDescriptionFragment,bundle)



        }

        newsAdapter.setOnItemClickListener {
            Log.e("Nee=w=", "getting click")
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_exploreFragment_to_articleFragment, bundle
            )

        }

        card_subscribe_now.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_coachesListFragment)
        }
        card_add_progress.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_addYourProgressFragment)

        }
        card_coach_near_you.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_freeCoachesList)
        }
        card_consult_a_coach.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_coachConsultFragment)
        }
    }

    private fun setUpRecyclerViewNews() = rv_news.apply {
        newsAdapter = NewsAdapter()
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun requestPermissions() {
        if (Permissions.hasWritingPermissions(requireContext())) {
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "These permissions are must to use this App",
                REQUEST_CODE_WRITING,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}