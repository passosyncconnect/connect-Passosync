package com.pasosync.pasosyncconnect.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.UserDetails
import com.pasosync.pasosyncconnect.other.Others.REQUEST_CODE_IMAGE_PICK
import com.pasosync.pasosyncconnect.other.Permissions
import com.pasosync.pasosyncconnect.ui.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val TAG = "ProfileFragment"
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var auth: FirebaseAuth
    val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val coachDetailsCollectionRef =
        db.collection("UserDetails").document(user?.email.toString())
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var userPicUri: Uri? = null
    var picurl: String? = null
    val connectUserIntro = Firebase.storage.reference.child("ConnectuserIntroData")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        retrieveUserDetails()
        update_email.isEnabled=false
        (activity as AppCompatActivity).supportActionBar?.title = " "
        mAuthListener = FirebaseAuth.AuthStateListener {
            /*NO-OP*/
        }
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        upload_photo.setOnClickListener {
            if (userPicUri == null) {
                Toast.makeText(requireContext(), "Please Select an Image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                dialog.show()
                uploadImageToStorage()
            }

        }


//        profile_pic.setOnClickListener {
//            picImageFromGallery()
//
//        }
        upload_photo.setOnClickListener {
            uploadImageToStorage()
        }

        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        profile_pic.setOnClickListener {
            picImageFromGallery()
        }
        update_profile.setOnClickListener {
            if (confirmInput() && Permissions.hasWritingPermissions(requireContext())) {
                dialog.show()
                uploaddata()
            } else {
                Toast.makeText(requireContext(), "Fill the details", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun picImageFromGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    private fun retrieveUserDetails() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var profile_name: String? = null
            var profile_email: String? = null
            var profile_mobile: String? = null
            var profile_age: String? = null
            var profile_user_pic: String? = null
            val querySnapshot = coachDetailsCollectionRef.get().addOnSuccessListener { document ->

                val age = document.getString("userAge")
                val email = document.getString("userEmail")
                val mobile = document.getString("userMobile")
                val profileName = document.getString("userName")
                val profileUserPic = document.getString("userPicUri")
                profile_name = profileName
                profile_age = age
                profile_mobile = mobile

                profile_email =user?.email
                profile_user_pic = profileUserPic
//

            }.await()
            withContext(Dispatchers.Main) {
                profile_tv_name?.text = profile_name
                profile_tv_email?.text = profile_email
                update_name.text = profile_name?.toEditable()
                update_email.text = profile_email?.toEditable()
                update_age.text = profile_age?.toEditable()
                update_mobile.text = profile_mobile?.toEditable()
//                if (profile_user_pic == null) {
//                    profile_pic.setImageResource(R.drawable.ic_baseline_person_pin_24)
//                } else {
//                    Glide.with(requireContext()).load(profile_user_pic).into(profile_pic)
//                }
                Glide.with(requireContext()).load(profile_user_pic).placeholder(R.drawable.man).fitCenter()
                    .into(profile_pic)

            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }


    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            userPicUri?.let {

                val filename = connectUserIntro.child("file" + userPicUri?.lastPathSegment)


                filename.putFile(it).addOnProgressListener { snapshot ->
                    dialog.show()

                }.addOnSuccessListener { taskSnapshot ->
                    filename.downloadUrl.addOnSuccessListener {
                        dialog.dismiss()
                        picurl = it.toString()
                        Log.d(TAG, "uploadImageToStorage: ${it.toString()}")
//                        var name = update_name.text.toString()
//                        var email = update_email.text.toString()
//                        var mobile = update_mobile.text.toString()
//                        var age=update_age.text.toString()
//                        val userDetails=UserDetails(name,email,mobile,age,picurl.toString())
//                        saveUserDetails(userDetails)
                        coachDetailsCollectionRef.update("userPicUri",picurl)
                        dialog.dismiss()

                    }
                }.await()
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploaddata() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var name = update_name.text.toString()
            var email = update_email.text.toString()
            var mobile = update_mobile.text.toString()
            var age = update_age.text.toString()
            val userDetails = UserDetails(name, email, mobile, age)
            saveUserDetails(userDetails)


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDetails(userDetails: UserDetails) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                coachDetailsCollectionRef.set(userDetails).addOnCompleteListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                userPicUri = it
                profile_pic.setImageURI(it)
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email: String = update_email_input.editText?.text.toString().trim()
        return if (email.isEmpty()) {
            update_email_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            update_email_input.error = null
            true
        }
    }
    private fun validateAbout(): Boolean {
        val email: String = update_about_input.editText?.text.toString().trim()
        return if (email.isEmpty()) {
            update_about_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            update_about_input.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val name: String = update_name_input.editText?.text.toString().trim()
        return if (name.isEmpty()) {
            update_name_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            update_name_input.error = null
            true
        }
    }

    private fun validateMobile(): Boolean {
        val mobile: String = update_mobile_input.editText?.text.toString().trim()
        return if (mobile.isEmpty() || mobile.length > 10 || mobile.length < 10) {
            update_mobile_input.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            update_mobile_input.error = null
            true
        }
    }

    private fun validateAge(): Boolean {
        val age: String = update_age_input.editText?.text.toString().trim()
        return if (age.isEmpty() || age.length > 2 || age.length < 2) {
            update_age_input.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            update_age_input.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validateName() or !validateMobile() or !validateAge() or !validateAbout()) {
            return false
        }
        return true
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener == null) {
            auth.removeAuthStateListener(mAuthListener)
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}