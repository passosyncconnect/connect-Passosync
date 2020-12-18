package com.pasosync.pasosyncconnect.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.activity_phone_login.*
import java.util.concurrent.TimeUnit


class PhoneLoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mCurrentUser: FirebaseUser? = null




    private var mCallbacks: OnVerificationStateChangedCallbacks? = null


//    override fun onStart() {
//        super.onStart()
//        if (mCurrentUser != null) {
//            sendUserToHome()
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth!!.currentUser;

        generate_btn.setOnClickListener {
            val country_code = country_code_text.text.toString()
            val phone_number = phone_number_text.text.toString()

            val complete_phone_number = "+$country_code$phone_number"

            if (country_code.isEmpty() || phone_number.isEmpty()) {
                login_form_feedback.text = "Please fill in the form to continue."
                login_form_feedback.visibility = View.VISIBLE
            } else {
                login_progress_bar.setVisibility(View.VISIBLE)
                generate_btn.isEnabled = false
                mCallbacks?.let { it1 ->
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        complete_phone_number,
                        60,
                        TimeUnit.SECONDS,
                        this@PhoneLoginActivity,
                        it1
                    )
                }
            }
        }

        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                login_form_feedback.text = "Verification Failed, please try again."
                login_form_feedback.visibility = View.VISIBLE
                login_progress_bar.visibility = View.INVISIBLE
                generate_btn.isEnabled = true
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Handler().postDelayed(
                    {
                        val otpIntent = Intent(this@PhoneLoginActivity, OtpActivity::class.java)
                        otpIntent.putExtra("AuthCredentials", s)
//                         Toast.makeText(this@PhoneLoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
                        startActivity(otpIntent)
                    },
                    10000
                )
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this@PhoneLoginActivity
            ) { task ->
                if (task.isSuccessful) {
                    sendUserToHome()
                    // ...
                } else {
                    if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        login_form_feedback.setVisibility(View.VISIBLE)
                        login_form_feedback.setText("There was an error verifying OTP")
                    }
                }
                login_progress_bar.setVisibility(View.INVISIBLE)
                generate_btn.setEnabled(true)
            }
    }
    private fun sendUserToHome() {
        val homeIntent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
        finish()
    }
}