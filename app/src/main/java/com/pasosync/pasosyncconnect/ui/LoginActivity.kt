package com.pasosync.pasosyncconnect.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.data.CheckedUserEmail
import com.pasosync.pasosyncconnect.data.FreeSubscribedCoachEmail
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    lateinit var auth: FirebaseAuth
    lateinit var topAnimation: Animation

    var freeEmailList = arrayListOf<String>()
    private val db = FirebaseFirestore.getInstance()

    var EXTRA_TEXT = "com.example.application.example.EXTRA_TEXT"
    var EXTRA_NUMBER = "com.example.application.example.EXTRA_NUMBER"
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(mAuthListener)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login)
        builder = AlertDialog.Builder(this)


        builder.setCancelable(false) // if you want user to wait for some process to finish

        freeConfirmInput()
        Log.d(TAG, "onCreate: ${freeConfirmInput()}")
        go_to_login.setOnClickListener {
            Intent(this, AlreadyRegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        dialog.dismiss()

        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        iv_login.animation = topAnimation

        val email = et_email.text.toString().toLowerCase().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        et_email.addTextChangedListener {
            if (email.matches(emailPattern.toRegex()) && email.isNotEmpty()) {
                emailred.error = "Valid Email Address"
            } else {
                emailred.error = null
            }
        }
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        signInButton.setOnClickListener {
            if (confirmInput()) {
                val email = et_email.text.toString().toLowerCase().trim()
                val password = et_pass.text.toString().trim()
                val intent = Intent(this, FirstProfileActivity::class.java)
                intent.putExtra(EXTRA_TEXT, email)
                intent.putExtra(EXTRA_NUMBER, password)
                startActivity(intent)
                dialog.dismiss()
                // registerUser()
            }
            else{
                 Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun freeConfirmInput(): Boolean {
        if (!checkingFree()) {
            return false
        }
        return true
    }




    private fun checkingFree(): Boolean {
        val freeEmail: String? = et_email.text.toString()

        var refer = db.collection("CoachUserEmailList")
            .whereEqualTo("coachEmail", freeEmail)
            .get()
            .addOnSuccessListener {

                for (document in it.documents) {
                    val checkedUserEmail = CheckedUserEmail(
                        document.getString("coachEmail")
                    )
                    freeEmailList.add(document.get("coachEmail") as String)
                    Log.d(TAG, "avoidDuplicateEntries: ${freeEmailList.size}")
                }


            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        return freeEmailList.contains(freeEmail)
        Log.d(TAG, "checkingFree: ${freeEmailList.contains(freeEmail)}")
    }


    private fun validateEmail(): Boolean {
        val email: String = et_email.text.toString().trim()
        return if (email.isEmpty()) {
            emailred.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailred.error = "Please Enter a valid Email"
            return false

        } else {
            emailred.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password: String = et_pass.text.toString().trim()
        return if (password.isEmpty()) {
            pass.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            pass.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validatePassword()) {
            return false
        }
        return true
//        dialog.show()
    }


}




