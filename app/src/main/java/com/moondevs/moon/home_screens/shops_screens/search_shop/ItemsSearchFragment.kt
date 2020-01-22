package com.moondevs.moon.home_screens.shops_screens.search_shop


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.Query

import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentItemsSearchBinding
import com.moondevs.moon.home_screens.shops_screens.ShopItem
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber


class ItemsSearchFragment : Fragment() {

    private lateinit var binding : FragmentItemsSearchBinding
    private lateinit var args : ItemsSearchFragmentArgs
    private lateinit var searchEditText : TextInputEditText
    private lateinit var shopItemsRef : Query
    private lateinit var adapter : SearchShopsItemsFirestoreRecyclerAdapter
    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var collectionPath : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_search,container,false)

        /**Initializing viewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        /**Fetch arguments*/
        args = ItemsSearchFragmentArgs.fromBundle(arguments!!)
        collectionPath = args.collectionPath


        FirestoreUtil.firestoreInstance.collectionGroup("Items").whereEqualTo("shopItemName", "Jam").get()
            .addOnSuccessListener {
                Timber.i("Here you go!")
                for (document in it.documents)
                Timber.i("Well : $document")
            }
            .addOnFailureListener {
                Timber.i("Failed")
            }

        /**add content for toolbar*/
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        val searchLayout = frameLayout.findViewById<View>(R.id.search_page_layout)
        searchEditText = searchLayout.findViewById(R.id.search_shops_textfield)
        searchEditText.hint = getString(R.string.search_items_of_this_shop)
        frameLayout.visibility = View.VISIBLE
        searchLayout.visibility = View.VISIBLE
        searchEditText.text?.clear()

        /**make keyboard appear*/
        searchEditText.requestFocus()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.i(s.toString())
                shopItemsRef = FirestoreUtil.firestoreInstance.collectionGroup("Items").whereGreaterThanOrEqualTo("shopItemName", s.toString().toLowerCase().capitalize())
                val options = FirestoreRecyclerOptions.Builder<ShopItem>().setQuery(shopItemsRef, ShopItem::class.java).build()
                val manager = GridLayoutManager(activity, 2)
                adapter = SearchShopsItemsFirestoreRecyclerAdapter(options , binding, args.shopName,context, args.shopImage, viewModel,args.shopRef, args.shopId)
                binding.searchItemsList.layoutManager = manager
                binding.searchItemsList.adapter = adapter
                adapter.startListening()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        /**Handling view cart click event*/
        binding.cartInSearch.setOnClickListener {
            it.findNavController().navigate(ItemsSearchFragmentDirections.actionItemsSearchFragmentToNavigationCart())
        }

        /**Making cart view visible depending on total number of items in the cart table*/
        viewModel.allItemsCount.observe(this, Observer {
            if (it == 0)
                binding.cartInSearch.visibility = View.GONE
            else
                binding.cartInSearch.visibility = View.VISIBLE
        })


        return binding.root
    }



    override fun onStart() {
        super.onStart()
        shopItemsRef = FirestoreUtil.firestoreInstance.collection("$collectionPath/Items")
        val options = FirestoreRecyclerOptions.Builder<ShopItem>().setQuery(shopItemsRef, ShopItem::class.java).build()
        val manager = GridLayoutManager(activity, 2)
        adapter = SearchShopsItemsFirestoreRecyclerAdapter(
            options,
            binding,
            args.shopName,
            context,
            args.shopImage,
            viewModel,
            args.shopRef,
            args.shopId
        )
        binding.searchItemsList.layoutManager = manager
        binding.searchItemsList.adapter = adapter
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
