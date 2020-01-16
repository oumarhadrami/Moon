package com.moondevs.moon.home_screens.account


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.moondevs.moon.introduction_screen.MainActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding : FragmentAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /**Initialize binding, viewmodel, and firebase auth*/
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        auth = FirebaseAuth.getInstance()


        /**Log out with firebase*/
        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
        }



        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //Make the frameLayout View.GONE
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        frameLayout.visibility = View.GONE
    }



}
