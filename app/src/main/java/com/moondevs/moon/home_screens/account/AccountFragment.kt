package com.moondevs.moon.home_screens.account


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.moondevs.moon.introduction_screen.MainActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding : FragmentAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false)
        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
        }

        accountViewModel.text.observe(this, Observer {
            binding.textAccount.text = it
        })

        // Log out with firebase
        return binding.root
    }


}
