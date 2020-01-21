package com.moondevs.moon.home_screens.shops_list_screen


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.moondevs.moon.util.FirestoreUtil
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentShopsListBinding


class ShopsListFragment : Fragment() {

    private lateinit var binding : FragmentShopsListBinding
    private lateinit var adapter : ShopFirestoreRecyclerAdapter
    private lateinit var shopsRef : Query
    private lateinit var args: ShopsListFragmentArgs


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_shops_list,container,false)


        //get shopType from home fragment
        args = ShopsListFragmentArgs.fromBundle(arguments!!)

        /**Initializing the collection for all shops according the shop type and adapter to the recyclerview*/
        shopsRef = FirestoreUtil.firestoreInstance.collection("Shops/").whereEqualTo("shopType",args.shopType)
        val options = FirestoreRecyclerOptions.Builder<Shop>().setQuery(shopsRef, Shop::class.java).build()
        adapter = ShopFirestoreRecyclerAdapter(options)
        binding.shopsList.adapter = adapter
        binding.lifecycleOwner = this



        return binding.root
    }


    /**Displaying shops type in the toolbar*/
    override fun onStart() {
        super.onStart()
        adapter.startListening()

        //add content for toolbar
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        frameLayout.visibility = View.VISIBLE
        val shopsCategoryLayout = activity!!.findViewById<View>(R.id.shops_categories_layout)
        val shopsCategoryText = shopsCategoryLayout.findViewById<TextView>(R.id.shop_type_in_toolbar)
        shopsCategoryText.text = args.shopType
        shopsCategoryLayout.visibility = View.VISIBLE
    }

    /**Hiding the shops type in toolbar*/
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        val shopsCategoryLayout = activity!!.findViewById<View>(R.id.shops_categories_layout)
        shopsCategoryLayout.visibility = View.GONE

    }

}
