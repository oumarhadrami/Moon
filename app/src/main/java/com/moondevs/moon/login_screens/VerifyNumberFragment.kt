package com.moondevs.moon.login_screens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.moondevs.moon.home_screens.HomeActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentVerifyNumberBinding
import com.moondevs.moon.util.FirestoreUtil
import java.util.*
import java.util.concurrent.TimeUnit


class VerifyNumberFragment : Fragment() {


    private lateinit var binding: FragmentVerifyNumberBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationId = ""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_number, container, false)

        /**move layout up keyboard*/
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        /**make resend orange*/
        makeResendOrange()

        /**Initialize firebase Auth*/
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode(Locale.getDefault().language)

        /** Get phone number from previous fragment*/
        val args = VerifyNumberFragmentArgs.fromBundle(arguments!!)
        val phoneNumber = args.phoneNumber
        binding.phoneNumberVerification.text = phoneNumber


        /**start the phone verification process*/
        verifyPhoneNumber(phoneNumber)

        /**validate otp Manually*/
        binding.enterOtpButton.setOnClickListener {
            authenticateManually()
        }

        /**resend code*/
        binding.resend.isEnabled = false
        binding.resend.setOnClickListener {
            Snackbar.make(it, "verification is being sent. check your sms inbox", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            resendVerificationCode(phoneNumber)
        }





        return binding.root
    }

    private fun makeResendOrange() {
        val text = "Did not receive the code? Resend OTP"
        val ss = SpannableString(text)
        val orangeResend = ForegroundColorSpan(Color.parseColor("#f8ab2e"))
        ss.setSpan(orangeResend, 26,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.resend.text = ss
    }


    private fun authenticateManually() {

        val enteredCode = binding.otpView.text.toString()
        if (enteredCode.isEmpty()   || enteredCode.length < 6) {
            binding.otpView.error = "Enter the 6-digit code..."
            binding.otpView.requestFocus()
            return
        }
            val credential = PhoneAuthProvider.getCredential(verificationId, enteredCode)
            signIn(credential)

    }


    private fun resendVerificationCode(phoneNumber: String) {
        verificationCallbacks()

        activity?.let {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                it,
                callbacks,
                resendToken
            )
        }

    }




    private fun verifyPhoneNumber(phoneNumber: String) {

        verificationCallbacks()

        activity?.let {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                it,
                callbacks
            )
        }
    }

    private fun verificationCallbacks(){
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                Toast.makeText(activity, "success", Toast.LENGTH_LONG).show()
                binding.otpView.setText(p0.smsCode)
                binding.resend.isEnabled = false
                signIn(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(activity, p0.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                //binding.otpView.setText(p0)
                verificationId = p0
                resendToken = p1
                binding.resend.isEnabled = true

                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "code sent", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun signIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.resend.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(Intent(activity, HomeActivity::class.java))
                }
            }
            else
                Toast.makeText(activity, "Phone number not verified!!",Toast.LENGTH_LONG).show()
        }
    }



}








