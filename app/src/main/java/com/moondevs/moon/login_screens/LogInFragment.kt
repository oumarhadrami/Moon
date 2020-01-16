package com.moondevs.moon.login_screens


import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {
    private lateinit var binding : FragmentLogInBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_in,container,false)

        /**Making empty textview size of statusBar*/
        val params = ConstraintLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getStatusBarHeight()
        )
        binding.statusbarTextviewInLogin.layoutParams = params

        /**make keyboard appear*/
        binding.phoneNumberLogin.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

        /**Navigate to Intro Fragment*/
        binding.closeLogin.setOnClickListener {
            it.findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToIntroFragment())
        }


        /**Navigate to VerifyPhoneNumber fragment with phone number as argument*/
        binding.login.setOnClickListener {
            val numberFromEditText = binding.phoneNumberLoginTextfield.text.toString().trim()
            if(numberFromEditText.isEmpty() || numberFromEditText.length < 8){
                binding.phoneNumberLogin.error = "please enter the 8-digit phone number!"
                binding.phoneNumberLogin.requestFocus()
                return@setOnClickListener
            }
            else binding.phoneNumberLogin.error = null

            val phoneNumber = "+222$numberFromEditText"
            it.findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToVerifyNumberFragment(phoneNumber))
        }


        return binding.root
    }


    private fun getStatusBarHeight(): Int {
        val height: Int
        val myResources: Resources = resources
        val idStatusBarHeight: Int =
            myResources.getIdentifier("status_bar_height", "dimen", "android")
        height = if (idStatusBarHeight > 0) {
            resources.getDimensionPixelSize(idStatusBarHeight)
        } else {
            0
        }
        return height
    }

}
