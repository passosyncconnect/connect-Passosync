package com.pasosync.pasosyncconnect.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.activity_otp.*


class OtpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mCurrentUser: FirebaseUser? = null

    private var mAuthVerificationId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth!!.currentUser

            mAuthVerificationId = intent.getStringExtra("AuthCredentials");




verify_btn.setOnClickListener {
    val otp: String = otp_text_view.text.toString()

    if (otp.isEmpty()) {
        otp_form_feedback.visibility = View.VISIBLE
        otp_form_feedback.text = "Please fill in the form and try again."
    } else {
        otp_progress_bar.visibility = View.VISIBLE
        verify_btn.isEnabled = false
        val credential = mAuthVerificationId?.let { it1 -> PhoneAuthProvider.getCredential(it1, otp) }
        if (credential != null) {
            signInWithPhoneAuthCredential(credential)
        }
    }
}

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@OtpActivity
            ) { task ->
                if (task.isSuccessful) {
                    sendUserToHome()
                    // ...
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        otp_form_feedback.visibility = View.VISIBLE
                        otp_form_feedback.text = "There was an error verifying OTP"
                    }
                }
                otp_progress_bar.visibility = View.INVISIBLE
                verify_btn.isEnabled = true
            }
    }


    override fun onStart() {
        super.onStart()
        if (mCurrentUser != null) {
            sendUserToHome()
        }
    }
    fun sendUserToHome() {
        val homeIntent = Intent(this@OtpActivity, MainActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
        finish()
    }
}