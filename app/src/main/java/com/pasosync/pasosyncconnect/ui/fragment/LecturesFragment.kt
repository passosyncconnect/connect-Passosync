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
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.adapters.LectureListAdapter
import com.pasosync.pasosyncconnect.adapters.SpinnerAdapter
import com.pasosync.pasosyncconnect.adapters.SubsAdapter
import com.pasosync.pasosyncconnect.data.LectureDetails
import com.pasosync.pasosyncconnect.data.SpinnerItem
import com.pasosync.pasosyncconnect.data.Types
import kotlinx.android.synthetic.main.free_lecture_list.*
import kotlinx.android.synthetic.main.lecture_list.*
import kotlinx.android.synthetic.main.lecture_list_subscribed.*

class LecturesFragment : Fragment(R.layout.lecture_list),AdapterView.OnItemSelectedListener {
    private val TAG = "LecturesFragment"
    var spinnerText = ""
    // lateinit var lectureListAdapter: LectureListAdapter
    lateinit var subsAdapter: SubsAdapter
    var lectureList = arrayListOf<LectureDetails>()
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    val args: LecturesFragmentArgs by navArgs()
    private val db = FirebaseFirestore.getInstance()
    private fun setUpRecyclerView() = rv_lecture_list.apply {
        Log.d(TAG, "setUpRecyclerView: ${rv_lecture_list.size}")
        val lecture = args.lecture
        Log.d(TAG, "email: ${lecture.coachEmail}")

        val lectureRef = db.collection("CoachLectureList")
            .document(lecture.coachEmail.toString()).collection("PaidLecture")
        val query = lectureRef
        val options = query?.let {
            Log.d(TAG, "Query: ${query.toString()}")
            FirestoreRecyclerOptions.Builder<LectureDetails>()
                .setQuery(it, LectureDetails::class.java)
                .build()
        }

        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.lecture_list, container, false)
        setHasOptionsMenu(true)

        return v
    }

    private fun setUpCustomSpinner() {
        val adapter = SpinnerAdapter(requireContext(), Types.list!!)

        spinner.adapter=adapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${rv_lecture_list.size}")

        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        spinner.visibility=View.VISIBLE
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        rv_lecture_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
//        btn_list_search.setOnClickListener {
//            val s = et_search_view.text.trim().toString()
//            searchData(s)
//        }
//        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.search, android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter

        setUpCustomSpinner()
        spinner.onItemSelectedListener = this

        showData()


        //  setUpRecyclerView()


    }

    private fun searchData(s: String) {
        dialog.show()
        val lecture = args.lecture
        val lectureRef = db.collection("CoachLectureList")
            .document(lecture.coachEmail.toString()).collection("PaidLecture")
            .whereEqualTo("seacrh",s).get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    lectureList.clear()
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
                rv_lecture_list.adapter = subsAdapter
                dialog.dismiss()
                subsAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lectureDetails", it)
                    }
                    findNavController()
                        .navigate(
                            R.id.action_lecturesFragment_to_detailLectureFragment,
                            bundle
                        )
                    lectureList.clear()
                }
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun showData() {
        dialog.show()
        val lecture = args.lecture
        Log.d(TAG, "email: ${lecture.coachEmail}")

        val lectureRef = db.collection("CoachLectureList")
            .document(lecture.coachEmail.toString()).collection("PaidLecture").orderBy("date",Query.Direction.DESCENDING)
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
                rv_lecture_list.adapter = subsAdapter
                dialog.dismiss()
                subsAdapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("lectureDetails", it)
                    }
                    findNavController().navigate(
                        R.id.action_lecturesFragment_to_detailLectureFragment,
                        bundle
                    )
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
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
//            spinner.visibility=View.GONE
            Log.d(TAG, "onItemSelected: $spinnerText")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}