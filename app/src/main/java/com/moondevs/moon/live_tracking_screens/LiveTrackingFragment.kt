package com.moondevs.moon.live_tracking_screens


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentDeliverLocationBinding


class LiveTrackingFragment : Fragment() {
    private lateinit var binding: FragmentDeliverLocationBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_live_tracking,container,false)

        return binding.root
    }


}
