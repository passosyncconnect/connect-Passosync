package com.pasosync.pasosyncconnect.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.UserDetails
import kotlinx.android.synthetic.main.activity_first_profile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirstProfileActivity : AppCompatActivity() {
    var EXTRA_TEXT = "com.example.application.example.EXTRA_TEXT"
    var EXTRA_NUMBER = "com.example.application.example.EXTRA_NUMBER"
    private  val TAG = "FirstProfileActivity"
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_first_profile)
        first_update_email_input.isEnabled=false
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val intent = intent
        val text = intent.getStringExtra(EXTRA_TEXT)
        first_update_email.text=text.toEditable()
        first_upload.setOnClickListener {
            if (confirmInput()){
                dialog.show()
                registerUser()
                uploaddata()
            }
        }


    }


   private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun registerUser() {
        val intent = intent
        val text = intent.getStringExtra(EXTRA_TEXT)
        val pass = intent.getStringExtra(EXTRA_NUMBER)
        if (text.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(text, pass).await()
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
        }
    }





    private fun uploaddata() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var name = first_update_name.text.toString()
            var email = first_update_email.text.toString()
            var mobile = first_update_mobile.text.toString()
            var age = first_update_age.text.toString()
            val userDetails = UserDetails(name, email, mobile, age,"")
            saveUserDetails(userDetails)


        } catch (e: Exception) {
            Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveUserDetails(userDetails: UserDetails) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val intent = intent
                val text = intent.getStringExtra(EXTRA_TEXT)
                 val coachDetailsCollectionRef =
                    db.collection("UserDetails").document(text)
                coachDetailsCollectionRef.set(userDetails).addOnCompleteListener {
                    dialog.dismiss()
                }.await()
                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@FirstProfileActivity, "data uploaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FirstProfileActivity, e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }



    private fun validateEmail(): Boolean {
        val email: String = first_update_email_input.editText?.text.toString().trim()
        return if (email.isEmpty()) {
            first_update_email_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            first_update_email_input.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val name: String = first_update_name_input.editText?.text.toString().trim()
        return if (name.isEmpty()) {
            first_update_name_input.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            first_update_name_input.error = null
            true
        }
    }

    private fun validateMobile(): Boolean {
        val mobile: String = first_update_mobile_input.editText?.text.toString().trim()
        return if (mobile.isEmpty() || mobile.length > 10 || mobile.length < 10) {
            first_update_mobile_input.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            first_update_mobile_input.error = null
            true
        }
    }

    private fun validateAge(): Boolean {
        val age: String = first_update_age_input.editText?.text.toString().trim()
        return if (age.isEmpty() || age.length > 2 || age.length < 2) {
            first_update_age_input.error = "invalid input"
            dialog.dismiss()
            false
        } else {
            first_update_age_input.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validateName() or !validateMobile() or !validateAge()) {
            return false
        }
        return true
        dialog.show()
    }




















    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }
}