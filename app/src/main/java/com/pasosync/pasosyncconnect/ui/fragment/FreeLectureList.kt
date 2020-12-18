package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.SpinnerAdapter
import com.pasosync.pasosyncconnect.adapters.SubsAdapter
import com.pasosync.pasosyncconnect.data.LectureDetails
import com.pasosync.pasosyncconnect.data.SpinnerItem
import com.pasosync.pasosyncconnect.data.Types
import kotlinx.android.synthetic.main.free_lecture_list.*
import kotlinx.android.synthetic.main.lecture_list.*
import kotlinx.android.synthetic.main.lecture_list_subscribed.*

class FreeLectureList:Fragment(R.layout.free_lecture_list), AdapterView.OnItemSelectedListener {
    private  val TAG = "FreeLectureList"
    var spinnerText = ""
    var lectureList = arrayListOf<LectureDetails>()
    lateinit var subsAdapter: SubsAdapter
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()
//    val args:FreeLectureListArgs by navArgs()
val args:FreeLectureListArgs by navArgs()

    private fun setUpCustomSpinner() {
        val adapter = SpinnerAdapter(requireContext(), Types.list!!)

        free_spinner.adapter=adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d(TAG, "onViewCreated: ${args.free.coachEmail}")
        Log.d(TAG, "onViewCreated: ${args.article.coachName}")
        Log.d(TAG, "onViewCreated: ${args.article.coachEmail}")
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder = AlertDialog.Builder(requireContext())

        builder.setCancelable(false) // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)
        free_spinner.visibility=View.VISIBLE
        dialog = builder.create()
        rv_free_lecture_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        setUpCustomSpinner()
        free_spinner.onItemSelectedListener = this

//        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.search, android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        free_spinner.adapter = adapter

        showData()




    }


    private fun showData() {
        dialog.show()

        Log.d(TAG, "showData: $spinnerText")
      //  Log.d(TAG, "email: ${lecture.coachEmail}")
        val lectureRef = db.collection("CoachLectureList")
            .document(args.article.coachEmail).collection("FreeLecture").orderBy("date",Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val lectureDetails = LectureDetails(
                        document.getString("lectureName"),
                        document.getString("lectureContent"),
                        document.getString("lectureImageUrl"),
                        document.getString("lectureVideoUrl"),
                        document.getString("lecturePdfUrl"),
                        document.getString("date"),document.getString("seacrh")
                    )
                    lectureList.add(lectureDetails)

                }
                subsAdapter = SubsAdapter(lectureList)
                rv_free_lecture_list.adapter = subsAdapter
                dialog.dismiss()
                subsAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lec", it)
                    }
                    findNavController()
                        .navigate(
                            R.id.action_freeLectureList_to_freeLectureDetails,
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
        val v: View = inflater.inflate(R.layout.free_lecture_list, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    private fun searchData(s: String) {
        dialog.show()
        lectureList.clear()
        Log.d(TAG, "searchData: $spinnerText")
        val lecture = args.article
        val lectureRef = db.collection("CoachLectureList")
            .document(lecture.coachEmail.toString()).collection("FreeLecture")
            .whereEqualTo("seacrh", s).get()
            .addOnSuccessListener {
                for (document in it.documents) {

                    dialog.dismiss()
                    val lectureDetails = LectureDetails(
                        document.getString("lectureName"),
                        document.getString("lectureContent"),
                        document.getString("lectureImageUrl"),
                        document.getString("lectureVideoUrl"),
                        document.getString("lecturePdfUrl"),
                        document.getString("date"),document.getString("seacrh")
                    )
                    Log.d(TAG, "searchData: ${document.getString("lectureName")}")
                    lectureList.add(lectureDetails)
                }
                subsAdapter = SubsAdapter(lectureList)
                rv_free_lecture_list.adapter = subsAdapter
                dialog.dismiss()
                subsAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lec", it)

                    }
                    findNavController()
                        .navigate(
                            R.id.action_freeLectureList_to_freeLectureDetails,
                            bundle
                        )
                    lectureList.clear()
                }
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent?.getItemAtPosition(position)?.equals("Choose to filter Lecture")!!){


        }else{
            val text = parent!!.getItemAtPosition(position).toString()
//            Toast.makeText(parent.context, text, Toast.LENGTH_SHORT).show()
            val clickedItem: SpinnerItem = parent.getItemAtPosition(position) as SpinnerItem
            val clickedCountryName: String =clickedItem.TypeName


            spinnerText = clickedCountryName
            searchData(spinnerText)
           // free_spinner.visibility=View.GONE
            Log.d(TAG, "onItemSelected: $spinnerText")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}