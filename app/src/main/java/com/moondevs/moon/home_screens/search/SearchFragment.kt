package com.moondevs.moon.home_screens.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
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
        val searchLayout = frameLayout.findViewById<View>(R.id.search_page_layout)
        val searchEditText = searchLayout.findViewById<TextInputEditText>(R.id.search_shops_textfield)
        frameLayout.visibility = View.VISIBLE
        searchLayout.visibility = View.VISIBLE

        /**make keyboard appear*/
        searchEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

    }

    override fun onStop() {
        super.onStop()
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        val searchLayout = frameLayout.findViewById<View>(R.id.search_page_layout)
        val searchEditText = searchLayout.findViewById<TextInputEditText>(R.id.search_shops_textfield)
        frameLayout.visibility = View.GONE
        searchLayout.visibility = View.GONE

        /**make keyboard disappear */
        searchEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

    }
}