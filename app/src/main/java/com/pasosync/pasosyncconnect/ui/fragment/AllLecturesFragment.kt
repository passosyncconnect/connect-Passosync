package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.AllLectureListAdapter
import com.pasosync.pasosyncconnect.adapters.SpinnerAdapter
import com.pasosync.pasosyncconnect.adapters.SubsAdapter
import com.pasosync.pasosyncconnect.data.AllLectureDetails
import com.pasosync.pasosyncconnect.data.LectureDetails
import com.pasosync.pasosyncconnect.data.SpinnerItem
import com.pasosync.pasosyncconnect.data.Types
import kotlinx.android.synthetic.main.all_lectures_fragment.*
import kotlinx.android.synthetic.main.lecture_list_subscribed.*


private const val TAG = "AllLecturesFragment"

class AllLecturesFragment : Fragment(R.layout.all_lectures_fragment), AdapterView.OnItemSelectedListener {
    var spinnerText = ""
    var lectureList = arrayListOf<AllLectureDetails>()
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var allLectureListAdapter: AllLectureListAdapter
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()


    private fun setUpCustomSpinner() {
        val adapter = SpinnerAdapter(requireContext(), Types.list!!)

        all_spinner.adapter=adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: doing")
        builder = AlertDialog.Builder(requireContext())
        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        rv_all_lecture_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        showData()
        setUpCustomSpinner()
        all_spinner.onItemSelectedListener = this

    }


    private fun showData() {
        dialog.show()
        val lectureRef = db.collection("UserDetails")
            .document(user?.email.toString()).collection("alllecture").orderBy("date",Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val lectureDetails = AllLectureDetails(
                        document.getString("lectureName"),
                        document.getString("lectureContent"),
                        document.getString("lectureImageUrl"),
                        document.getString("lectureVideoUrl"),
                        document.getString("lecturePdfUrl"),
                        document.getString("date"),
                        document.getString("seacrh"),
                        document.getString("type"),
                        document.getString("coachName"),
                        document.getString("coachProfilePicUri"),
                        document.getString("coachEmail")
                    )
                    lectureList.add(lectureDetails)
                }
                allLectureListAdapter = AllLectureListAdapter(lectureList)
                rv_all_lecture_list.adapter = allLectureListAdapter
                dialog.dismiss()
                allLectureListAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lec", it)
                    }
                    findNavController()
                        .navigate(
                            R.id.action_allLecture_to_allLectureDetails,
                            bundle
                        )
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.all_lectures_fragment, container, false)
        setHasOptionsMenu(true)

        return v
    }

    private fun searchData(s:String) {
        dialog.show()
        lectureList.clear()
        val lectureRef = db.collection("UserDetails")
            .document(user?.email.toString()).collection("alllecture").whereEqualTo("seacrh", s)
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val lectureDetails = AllLectureDetails(
                        document.getString("lectureName"),
                        document.getString("lectureContent"),
                        document.getString("lectureImageUrl"),
                        document.getString("lectureVideoUrl"),
                        document.getString("lecturePdfUrl"),
                        document.getString("date"),
                        document.getString("seacrh"),
                        document.getString("type"),
                        document.getString("coachName"),
                        document.getString("coachProfilePicUri"),
                        document.getString("coachEmail")
                    )
                    lectureList.add(lectureDetails)
                }
                allLectureListAdapter = AllLectureListAdapter(lectureList)
                rv_all_lecture_list.adapter = allLectureListAdapter
                dialog.dismiss()
                allLectureListAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lec", it)
                    }
                    findNavController()
                        .navigate(
                            R.id.action_allLecture_to_allLectureDetails,
                            bundle
                        )
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.getItemAtPosition(position)?.equals("Choose to filter Lecture")!!) {
        } else {
            val text = parent!!.getItemAtPosition(position).toString()


//
            val clickedItem: SpinnerItem = parent.getItemAtPosition(position) as SpinnerItem
            val clickedCountryName: String =clickedItem.TypeName

            spinnerText = clickedCountryName
            searchData(spinnerText)
//
            Log.d(TAG, "onItemSelected: $spinnerText")
        }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}