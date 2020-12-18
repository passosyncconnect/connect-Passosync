package com.pasosync.pasosyncconnect.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.activity_already_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AlreadyRegisterActivity : AppCompatActivity() {
    private val TAG = "AlreadyRegisterActivity"
    lateinit var auth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    lateinit var topAnimation: Animation

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

        setContentView(R.layout.activity_already_register)
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        dialog = builder.create()
        auth= FirebaseAuth.getInstance()
        go_to_register.setOnClickListener {
            Intent(this,LoginActivity::class.java).also {
                startActivity(it)
            }
        }
        forgot_pass.setOnClickListener {
            showRecoverPasswordDialog()
        }
        supportActionBar?.hide()
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        iv_al_login.animation=topAnimation
        val email = al_et_email.text.toString().trim()
        val emailPattern = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+"
        al_et_email.addTextChangedListener {
            if (email.matches(emailPattern.toRegex()) && email.isNotEmpty()) {
                alreadyEmailred.error = "Valid Email Address"
            } else {
                alreadyEmailred.error = null
            }
        }
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
                startActivity(Intent(this@AlreadyRegisterActivity, MainActivity::class.java))
                finish()
            }
        }

        al_signInButton.setOnClickListener {
            if (confirmInput()) {
                dialog.show()
                loginUser()
            }
        }


    }

    private fun showRecoverPasswordDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover Password")
        builder.setIcon(R.mipmap.ic_launcher_round)
        builder.setMessage("You will get an email with a link to reset password")
        var linearLayout = LinearLayout(this)
        var layoutParams = ViewGroup.LayoutParams.MATCH_PARENT

        val email_et = EditText(this)
        email_et.hint = "Enter your registered email                  "
        email_et.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS


        linearLayout.addView(email_et)

        builder.setView(linearLayout)


        builder.setPositiveButton("Send link", DialogInterface.OnClickListener { dialog, which ->

            val email = email_et.text.toString().trim()
            if (email.isEmpty()){
                 Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show()

            }else{
                beginRecovery(email)
            }



        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

            dialog.dismiss()

        })
        builder.create().show()

    }

    private fun beginRecovery(email: String) {

        dialog.show()
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            dialog.dismiss()
            if (it.isSuccessful) {
                Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }


        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun validateEmail(): Boolean {
        val email: String = al_et_email.text.toString().trim()
        return if (email.isEmpty()) {
            alreadyEmailred.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            alreadyEmailred.error = "Please Enter a valid Email"
            return false

        } else {
            alreadyEmailred.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password: String = al_et_pass.text.toString().trim()
        return if (password.isEmpty()) {
            alPass.error = "Field can't be empty"
            dialog.dismiss()
            false
        } else {
            alPass.error = null
            true
        }
    }

    private fun confirmInput(): Boolean {
        if (!validateEmail() or !validatePassword()) {
            return false
        }
        return true
        dialog.show()
    }

    private fun loginUser() {
        val email = al_et_email.text.toString().trim()
        val password = al_et_pass.text.toString().trim()


        if (email.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AlreadyRegisterActivity, e.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
        }
    }




}