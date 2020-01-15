package com.moondevs.moon.home_screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding : FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search,container,false)
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //add content for toolbar
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        frameLayout.visibility = View.GONE
    }
}