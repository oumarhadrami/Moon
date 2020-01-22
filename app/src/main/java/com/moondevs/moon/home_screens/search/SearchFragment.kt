package com.moondevs.moon.home_screens.search

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentSearchBinding
import com.moondevs.moon.home_screens.shops_list_screen.Shop
import com.moondevs.moon.util.FirestoreUtil

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding : FragmentSearchBinding
    private lateinit var searchEditText : TextInputEditText
    private lateinit var adapter : SearchShopsFirestoreRecyclerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search,container,false)
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)


        /**add content for toolbar*/
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        val searchLayout = frameLayout.findViewById<View>(R.id.search_page_layout)
        searchEditText = searchLayout.findViewById(R.id.search_shops_textfield)
        frameLayout.visibility = View.VISIBLE
        searchLayout.visibility = View.VISIBLE

        /**make keyboard appear*/
        searchEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val shopsRef = FirestoreUtil.firestoreInstance.collection("Shops").whereEqualTo("shopName",s.toString())
                val options = FirestoreRecyclerOptions.Builder<Shop>().setQuery(shopsRef, Shop::class.java).build()
                adapter = SearchShopsFirestoreRecyclerAdapter(options)
                binding.searchShopsList.adapter = adapter
                adapter.startListening()
            }
        })


        /**list of shoptypes*/


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val shopsRef = FirestoreUtil.firestoreInstance.collection("Shops").limit(4)
        val options = FirestoreRecyclerOptions.Builder<Shop>().setQuery(shopsRef, Shop::class.java).build()
        adapter = SearchShopsFirestoreRecyclerAdapter(options)
        binding.searchShopsList.adapter = adapter
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        val searchLayout = frameLayout.findViewById<View>(R.id.search_page_layout)
        val searchEditText = searchLayout.findViewById<TextInputEditText>(R.id.search_shops_textfield)
        searchLayout.visibility = View.GONE

        /**make keyboard disappear */
        searchEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        adapter.stopListening()

    }
}