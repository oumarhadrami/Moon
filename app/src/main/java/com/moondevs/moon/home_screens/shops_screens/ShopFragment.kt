package com.moondevs.moon.home_screens.shops_screens


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.moondevs.moon.util.FirestoreUtil
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentShopBinding

class ShopFragment : Fragment() {
    private lateinit var binding : FragmentShopBinding
    private lateinit var shopItemsRef : Query
    private lateinit var adapter : ShopItemsFirestoreRecyclerAdapter
    private lateinit var args: ShopFragmentArgs
    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var frameLayout : FrameLayout
    private lateinit var shopDetailsInPageLayout : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_shop,container,false)

        //binding shop details to the views
        args = ShopFragmentArgs.fromBundle(arguments!!)

        //Initializing viewModel
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        /** shop items reference and recyclerview adapter init*/
        val collectionPath = args.ref + "/Items"
        shopItemsRef = FirestoreUtil.firestoreInstance.collection(collectionPath)
        val options = FirestoreRecyclerOptions.Builder<ShopItem>().setQuery(shopItemsRef, ShopItem::class.java).build()
        val manager = GridLayoutManager(activity, 2)
        adapter = ShopItemsFirestoreRecyclerAdapter(options , binding, args.shopName!!,context, args.shopImage!!, viewModel,args.ref)
        binding.itemsList.layoutManager = manager
        binding.itemsList.adapter = adapter



        /**Handling view cart click event*/
        binding.cart.setOnClickListener {
            findNavController().navigate(ShopFragmentDirections.actionShopFragmentToNavigationCart())
        }

        /**Making cart view visible depending on total number of items in the cart table*/
        viewModel.allItemsCount.observe(this, Observer {
            if (it == 0)
                binding.cart.visibility = View.GONE
            else
                binding.cart.visibility = View.VISIBLE
        })



        return binding.root
    }

    /**Displaying the shop name in the toolbar*/
    override fun onStart() {
        super.onStart()
        adapter.startListening()

        //add content for toolbar
        frameLayout = activity!!.findViewById(R.id.toolbar_framelayout)
        shopDetailsInPageLayout = activity!!.findViewById(R.id.shop_page_layout)
        frameLayout.visibility = View.VISIBLE
        shopDetailsInPageLayout.visibility = View.VISIBLE

        val shopsNameText = shopDetailsInPageLayout.findViewById<TextView>(R.id.shop_name_toolbar)
        shopsNameText.text = args.shopName
    }


    /**Hiding the shop name in the toolbar*/
    override fun onStop() {
        super.onStop()
        adapter.stopListening()

        shopDetailsInPageLayout.visibility = View.GONE
    }

}

