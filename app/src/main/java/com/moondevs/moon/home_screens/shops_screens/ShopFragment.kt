package com.moondevs.moon.home_screens.shops_screens


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.moondevs.moon.util.FirestoreUtil
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentShopBinding
import com.moondevs.moon.home_screens.account.favorites.FavoritesViewModel
import com.moondevs.moon.home_screens.account.favorites.FavoritesViewModelFactory
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShop
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {
    private lateinit var binding : FragmentShopBinding
    private lateinit var shopItemsRef : Query
    private lateinit var adapter : ShopItemsFirestoreRecyclerAdapter
    private lateinit var args: ShopFragmentArgs
    private lateinit var collectionPath : String
    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var frameLayout : FrameLayout
    private lateinit var shopDetailsInPageLayout : View
    private lateinit var searchButton : ImageButton
    private lateinit var favoriteButton : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_shop,container,false)

        //binding shop details to the views
        args = ShopFragmentArgs.fromBundle(arguments!!)
        collectionPath = args.ref + "/Items"

        /**Initializing viewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /**initialize FavoriteShopsViewModel*/
        val favoritesViewModelFactory = FavoritesViewModelFactory(application)
        favoritesViewModel = ViewModelProviders.of(activity!!, favoritesViewModelFactory).get(FavoritesViewModel::class.java)


        /**Make recyclerview invisible and progressbar visible until data has been retrieved*/
        FirestoreUtil.firestoreInstance.collection(collectionPath).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null ) {
                binding.shopItemsContainter.visibility = View.VISIBLE
                binding.progressBarShop.visibility = View.GONE
            }
        }

        /** shop items reference and recyclerview adapter init*/

        shopItemsRef = FirestoreUtil.firestoreInstance.collection(collectionPath)
        val options = FirestoreRecyclerOptions.Builder<ShopItem>().setQuery(shopItemsRef, ShopItem::class.java).build()
        val manager = GridLayoutManager(activity, 2)
        adapter = ShopItemsFirestoreRecyclerAdapter(options , binding, args.shopName!!,context, args.shopImage!!, viewModel,args.ref, args.shopId)
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


        /**Navigate to search fragment*/
        //add content for toolbar
        frameLayout = activity!!.findViewById(R.id.toolbar_framelayout)
        shopDetailsInPageLayout = activity!!.findViewById(R.id.shop_page_layout)
        searchButton = shopDetailsInPageLayout.findViewById(R.id.search_items)
        searchButton.setOnClickListener {
            findNavController().navigate(ShopFragmentDirections.actionShopFragmentToItemsSearchFragment(collectionPath,  args.shopImage!!, args.shopName!!, args.ref, args.shopId!!))
        }


        /**set heart from/to favorite or unfavorite from the start*/
        changeHeart()
        /**To favorite or unfovorite something*/
        favoriteButton = shopDetailsInPageLayout.findViewById(R.id.set_favorite)
        favoriteButton.setOnClickListener {
           favoriteOrUnfavorite()
        }
        return binding.root
    }

    private fun changeHeart() {
        favoritesViewModel.viewModelScope.launch {
            if (!favoritesViewModel.recordExists(args.shopId))
                favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            else
                favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp)
        }
    }

    private fun favoriteOrUnfavorite() {
        favoritesViewModel.viewModelScope.launch {
            if (!favoritesViewModel.recordExists(args.shopId)) {
                favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp)
                favoritesViewModel.insert(
                    FavoriteShop(
                        shopId = args.shopId,
                        shopRef = args.ref,
                        shopName = args.shopName!!,
                        shopImage = args.shopImage!!
                    )
                )
            }
            else{
                favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                favoritesViewModel.deleteFavoriteShop(args.shopId)
            }
        }
    }

    /**Displaying the shop name in the toolbar*/
    override fun onStart() {
        super.onStart()
        adapter.startListening()


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

