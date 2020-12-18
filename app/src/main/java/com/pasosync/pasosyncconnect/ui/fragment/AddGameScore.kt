package com.pasosync.pasosyncconnect.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.GameScoreDetails
import com.pasosync.pasosyncconnect.other.Others
import com.pasosync.pasosyncconnect.other.Permissions
import kotlinx.android.synthetic.main.add_score_game.*
import kotlinx.android.synthetic.main.add_your_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*

private const val TAG = "AddGameScore"

class AddGameScore : Fragment(R.layout.add_score_game), DatePickerDialog.OnDateSetListener {
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var gameScoreImageUri: Uri? = null
    var gameScoreVideoUri: Uri? = null
    var urlImageGameScore: String? = null
    var urlVideoGameScore: String? = null
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
var radioText=""
    var date: String? = null

    private val user = FirebaseAuth.getInstance().currentUser
    val userNewGameScoreStorageReference = Firebase.storage.reference.child("UserGameScoreData")
    private val db = FirebaseFirestore.getInstance()
    private val userGameScoreCollectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "GameScoreCardDetails"
        )
    private val userPracticeScoreCollectionRef =
        db.collection("UserDetails").document(user?.email.toString()).collection(
            "PracticeScoreCardDetails"
        )


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()

        var calendar: Calendar = Calendar.getInstance()
        datePickerGame.setOnClickListener {
//            val datePicker: DialogFragment = DatePickerFragment()
//            datePicker.show(childFragmentManager, "date picker")
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), this, year, month, day).show()

            Log.d(TAG, "onViewCreated: $date")
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            var rb = view.findViewById<RadioButton>(checkedId)
            if (rb != null) {
                Log.d(TAG, "onViewCreated: ${rb.text.toString()}")
                radioText = rb.text.toString()
                Log.d(TAG, "onViewCreated:$radioText")
            }
            else{
                radioText="Game"
            }

        }

        imageGameScore.setOnClickListener {
            if (Permissions.hasWritingPermissions(requireContext())) {
                chooseImageForGameScore()


            }
        }
        imageGameVideo.setOnClickListener {
            if (Permissions.hasWritingPermissions(requireContext())) {
                chooseVideoForGameScore()

            }
        }



        btnGameChooseImage.setOnClickListener {
            if (gameScoreImageUri == null) {
                Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadImageToStorage()
            }
        }

        btnGameUploadVideo.setOnClickListener {
            if (gameScoreVideoUri == null) {
                Toast.makeText(requireContext(), "Please Select a video", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadVideoToStorage()
            }
        }

        uploadGameScoreData.setOnClickListener {
            if (confirmInput()) {

                uploadGameScoreDataInFirestore()
            } else {
                Toast.makeText(requireContext(), "Fill all details", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun uploadGameScoreDataInFirestore() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val myTeamName = etGameMyTeamName.text.trim().toString()
            val rivalTeamName = etGameOppositeTeamName.text.trim().toString()
            val myTeamScore = etGameMyTotalScore.text.trim().toString().toInt()
            val oppositeTeamScore = etGameOppositeTotalScore.text.trim().toString().toInt()
            val individualRuns = etMyRuns.text.trim().toString().toInt()
            val myFours = etMyFours.text.trim().toString().toInt()
            val mySixes = etMySixes.text.trim().toString().toInt()
            val wickets = etMyWickets.text.trim().toString().toInt()
            val ballPlayed = etBallPlayed.text.trim().toString().toInt()
            val matchDesc = etGameMatchDescription.text.trim().toString()
            val totalOver=etTotalOver.text.trim().toString().toInt()
            val gameImageUrl = urlImageGameScore?.toString()
            val gameVideoUrl = urlVideoGameScore?.toString()
            val dateofscore = date.toString()
            val timestamp = System.currentTimeMillis()
            val type=radioText.toString()

            val gameScoreDetails = GameScoreDetails(
                myTeamName,
                rivalTeamName,
                myTeamScore,
                oppositeTeamScore,
                individualRuns,
                myFours,
                mySixes,
                wickets,
                ballPlayed,
                matchDesc,
                dateofscore,
                gameImageUrl,
                gameVideoUrl,
                timestamp,totalOver
            )

            if (type == "Game") {
                val data = userGameScoreCollectionRef.add(gameScoreDetails)
                Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_addGameScore_to_yourProgressFragment)


            }else if(type=="Practice"){
                val freedata = userPracticeScoreCollectionRef.add(gameScoreDetails)
                Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_addGameScore_to_yourProgressFragment)



            }
            else {
                Toast.makeText(requireContext(),"Please choose between \n Game and Practice",Toast.LENGTH_SHORT).show()

            }




//            val data = userGameScoreCollectionRef.add(gameScoreDetails)
//            Snackbar.make(requireView(), "Uploaded successfully", Snackbar.LENGTH_SHORT)
//                .show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            gameScoreImageUri?.let {
                val imageFileName = userNewGameScoreStorageReference.child(
                    "Image${System.currentTimeMillis()}"
                )
                imageFileName.putFile(it).addOnProgressListener {
                    dialog.show()


                }.addOnSuccessListener { tasksnapshot ->
                    imageFileName.downloadUrl.addOnSuccessListener {
                        urlImageGameScore = it.toString()
                        Log.d(TAG, "uploadImageToStorage: $urlImageGameScore")
                    }

                }.await()
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Image Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadVideoToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            gameScoreVideoUri?.let {
                val videoFileName = userNewGameScoreStorageReference.child(
                    "Video${System.currentTimeMillis()}"
                )
                videoFileName.putFile(it).addOnProgressListener {
                    dialog.show()


                }.addOnSuccessListener { tasksnapshot ->
                    videoFileName.downloadUrl.addOnSuccessListener {
                        urlVideoGameScore = it.toString()
                        Log.d(TAG, "uploadVideoToStorage: $urlVideoGameScore")
                    }


                }.await()
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Video Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseVideoForGameScore() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Others.PICK_VIDEO)

    }

    private fun chooseImageForGameScore() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, Others.REQUEST_CODE_IMAGE_PICK)
        }
    }


    private fun validateMyTeamName(): Boolean {
        val title: String = etGameMyTeamName.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameMyTeamNameInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameMyTeamNameInput.error = null
            true
        }
    }

    private fun validateOppositeTeamName(): Boolean {
        val title: String = etGameOppositeTeamName.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameOppositeTeamNameInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameOppositeTeamNameInput.error = null
            true
        }
    }

    private fun validateOppositeTeamScore(): Boolean {
        val title: String = etGameOppositeTotalScore.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameOppositeTeamTotalScoreInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameOppositeTeamTotalScoreInput.error = null
            true
        }
    }

    private fun validateTotalOvers(): Boolean {
        val title: String = etTotalOver.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameTotalOverInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameTotalOverInput.error = null
            true
        }
    }

    private fun validateMyTeamScore(): Boolean {
        val title: String = etGameMyTotalScore.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameMyTeamTotalScoreInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameMyTeamTotalScoreInput.error = null
            true
        }
    }

    private fun validateMyIndividualScore(): Boolean {
        val title: String = etMyRuns.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameMyRunsInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameMyRunsInput.error = null
            true
        }
    }

    private fun validateMySixes(): Boolean {
        val title: String = etMySixes.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameNumberOfSixesInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameNumberOfSixesInput.error = null
            true
        }
    }

    private fun validateMyFours(): Boolean {
        val title: String = etMyFours.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameNumberOfFoursInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameNumberOfFoursInput.error = null
            true
        }
    }

    private fun validateBallPlayed(): Boolean {
        val title: String = etBallPlayed.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameBallPlayedInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameBallPlayedInput.error = null
            true
        }
    }

    private fun validateWicketTaken(): Boolean {
        val title: String = etMyWickets.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameMyWicketsInput.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameMyWicketsInput.error = null
            true
        }
    }

    private fun validateMatchDescription(): Boolean {
        val title: String = etGameMatchDescription.text.toString().trim()
        return if (title.isEmpty()) {
            tvGameMatchDescription.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            tvGameMatchDescription.error = null
            true
        }
    }

    private fun validateDate(): Boolean {
        val title: String = tvGameDate.text.toString()
        return if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please choose a date", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            false
        } else {

            true
        }
    }

    private fun confirmInput(): Boolean {

        if (!validateMyTeamName() or !validateOppositeTeamName() or !validateBallPlayed()
            or !validateMatchDescription() or !validateMyFours() or !validateMySixes()
            or !validateMyIndividualScore() or !validateMyTeamScore() or !validateWicketTaken()
            or !validateOppositeTeamScore() or !validateDate() or !validateTotalOvers()
        ) {
            return false
        }
        return true
//        dialog.show()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.add_score_game, container, false)
        setHasOptionsMenu(true)

        return v
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year



        tvGameDate.text = "$savedDay-$savedMonth-$savedYear"
        date = "$savedDay-$savedMonth-$savedYear"
        Log.d(TAG, "onDateSet: $date")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Others.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {

                gameScoreImageUri = it
                imageGameScore.setImageResource(R.drawable.checkedsmall)
                Toast.makeText(
                    requireContext(),
                    "Your Image is ready to upload",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        if (resultCode == Activity.RESULT_OK && requestCode == Others.PICK_VIDEO) {
            data?.data?.let {

                gameScoreVideoUri = it
                imageGameVideo.setImageResource(R.drawable.checkedsmall)
                Toast.makeText(
                    requireContext(),
                    "Your Image is ready to upload",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }


    }

}