package com.pasosync.pasosyncconnect.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.ProgressDetails
import com.pasosync.pasosyncconnect.other.Others.REQUEST_CODE_IMAGE_PICK
import com.pasosync.pasosyncconnect.other.Permissions
import kotlinx.android.synthetic.main.add_your_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*


class AddYourProgressFragment : Fragment(R.layout.add_your_progress) {
    private val TAG = "AddYourProgressFragment"
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    var progressImageUri: Uri? = null
    var progressImageUrl: String? = null
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val progressCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())
            .collection("ProgressList")
    val connectUserProgressImage = Firebase.storage.reference.child("UserProgressImage")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        (activity as AppCompatActivity).supportActionBar?.title = " "
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        auth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener {
            /*NO-OP*/
        }

        iv_progress.setOnClickListener {
            picImageFromGallery()
        }

        btn_progress_image.setOnClickListener {

            if (progressImageUri == null) {
                Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadImageToStorage()
            }
        }
        btn_upload_progress.setOnClickListener {
            if (confirmInput() && Permissions.hasWritingPermissions(requireContext())) {
                dialog.show()
                saveProgress()
            } else {
                Toast.makeText(requireContext(), "Fill all Details", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkImage(): Boolean {
        return if (progressImageUri == null) {
            Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun picImageFromGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                progressImageUri = it
//                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
//                    .start(requireContext(),this)
                iv_progress.setImageURI(it)

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.add_your_progress, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun validateTitle(): Boolean {
        val title: String = et_title_progress.text.toString().trim()
        return if (title.isEmpty()) {
            title_add_progress.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            title_add_progress.error = null
            true
        }
    }

    private fun validateDescription(): Boolean {
        val description: String = et_description_progress.text.toString().trim()
        return if (description.isEmpty()) {
            description_add_progress.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            description_add_progress.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateTitle() or !validateDescription()) {
            return false
        }
        return true
        dialog.show()
    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {

            progressImageUri?.let {

                val filename =
                    connectUserProgressImage.child("file" + progressImageUri?.lastPathSegment)


                filename.putFile(it).addOnProgressListener { snapshot ->
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    filename.downloadUrl.addOnSuccessListener {
                        dialog.dismiss()
                        progressImageUrl = it.toString()
                        Log.d(TAG, "uploadImageToStorage: ${it.toString()}")
//                        var title = et_title_progress.text.toString()
//                        var description = et_description_progress.text.toString()
//                        val calendar = Calendar.getInstance()
//                        val currentDate: String =
//                            DateFormat.getDateInstance().format(calendar.time)
//                        val progressDetails =
//                            ProgressDetails(title, description, progressImageUrl!!,currentDate)
//                        saveProgressDetails(progressDetails)
//                        dialog.dismiss()
//                        findNavController().navigate(R.id.action_addYourProgressFragment_to_yourProgressFragment)

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
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProgressDetails(progressDetails: ProgressDetails) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                progressCollectionRef.add(progressDetails).addOnCompleteListener {
                    dialog.dismiss()
                }.await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "data uploaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun saveProgress() = CoroutineScope(Dispatchers.Main).launch {
        try {
            var title = et_title_progress.text.toString()
            var description = et_description_progress.text.toString()
            val calendar = Calendar.getInstance()
            val currentDate: String =
                DateFormat.getDateInstance().format(calendar.time)
            val progressImage = progressImageUrl.toString()

            val progressDetails =
                ProgressDetails(title, description, progressImage, currentDate,System.currentTimeMillis())
//            saveProgressDetails(progressDetails)
            val data=progressCollectionRef.add(progressDetails)
            dialog.dismiss()
            findNavController().navigate(R.id.action_addYourProgressFragment_to_yourProgressFragment)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }


    }
}