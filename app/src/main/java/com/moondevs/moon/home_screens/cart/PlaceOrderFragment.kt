package com.moondevs.moon.home_screens.cart


import android.app.Application
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference

import com.moondevs.moon.R
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressViewModel
import com.moondevs.moon.home_screens.account.adresses.addresses_database.Address
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentPlaceOrderBinding
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrder
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import com.moondevs.moon.util.FirestoreUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class PlaceOrderFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var orderDoc : DocumentReference
    @Volatile private var areFieldsStored : Boolean = false
    @Volatile private var isOrderPlaced : Boolean = false
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var args : PlaceOrderFragmentArgs
    private lateinit var instructions : String
    private lateinit var deliveryFee : String
    private var deliveryFeeInt = 0
    private lateinit var shopName : String
    @Volatile private var totalAmountInt : Int = 0
    @Volatile private var totalItemsCount : Int = 0
    @Volatile private lateinit var currentAddress : Address
    @Volatile private var itemsHashMap : HashMap<String,HashMap<String, Any>> = hashMapOf()
    @Volatile private var shopId : String = ""
    @Volatile private var currentAddressHashMap : HashMap<String, Any> = hashMapOf()
    private lateinit var currentDateandTime : String



    private lateinit var binding : FragmentPlaceOrderBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_place_order,container,false)

        /**initialize ShoppingCartViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)

        /**Initialize AddressViewModel*/
        val addressViewModelFactory =
            AddressViewModelFactory(
                application
            )
        addressViewModel = ViewModelProviders.of(activity!!, addressViewModelFactory).get(
            AddressViewModel::class.java)


        /**Initialize the AppBar and BottomNavView*/
        appBar = activity!!.findViewById(R.id.appbar)
        bottomNav = activity!!.findViewById(R.id.nav_view)

        /**fetch instructions froms arguments bundle*/
        args = PlaceOrderFragmentArgs.fromBundle(arguments!!)
        instructions = args.instructions
        deliveryFee = args.deliveryFee
        shopName = args.shopName


        /**Add the total amount, delivery fee, totalItemsCount, overall total amount, address, Items, date to the order document reference*/
        deliveryFeeInt = Integer.parseInt(deliveryFee.dropLastWhile { it.isLetter() }.trim())

        viewModel.totalAmount.observe(viewLifecycleOwner, Observer { totalAmount ->
            totalAmountInt = totalAmount
        })
        viewModel.allItemsCount.observe(viewLifecycleOwner, Observer { totalItemsCountInt ->
            totalItemsCount = totalItemsCountInt
        })


        viewModel.allCartItems.observe(viewLifecycleOwner, Observer {cartItems->
            for ((index, item) in cartItems.withIndex()) {
                itemsHashMap[index.toString()] = hashMapOf(
                    "shopItemId" to item.shopItemId,
                    "shopItemName" to item.shopItemName,
                    "shopItemPrice" to item.shopItemPrice,
                    "shopItemQuantity" to item.shopItemQuantity,
                    "shopName" to item.shopName,
                    "shopItemImage" to item.shopItemImage,
                    "shopImage" to item.shopImage,
                    "shopItemPriceByQuantity" to item.shopItemPriceByQuantity,
                    "shopRef" to item.shopRef,
                    "shopId" to item.shopId
                )
                shopId = item.shopId
            }
            storeAddress()
        })
        Timber.i("\n\n\nLATER  :: $itemsHashMap")

        /**Get time from device*/
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        currentDateandTime = formatter.format(Date())



        return binding.root
    }

    private fun storeAddress() {
        addressViewModel.viewModelScope.launch {
            currentAddress = addressViewModel.getSelectedAddress()
            currentAddressHashMap = hashMapOf(
                "Name" to currentAddress.Name,
                "PhoneNumber" to currentAddress.PhoneNumber,
                "Latitude" to currentAddress.Latitude,
                "Longitude" to currentAddress.Longitude
            )
        placeOrder()
        }
    }


    /**Method handling the placing of the order
     * Add Fields to the database*/
    private fun placeOrder() {
        appBar.visibility = View.GONE
        bottomNav.visibility = View.GONE
        binding.orderPlacingScreen.visibility = View.VISIBLE

        //Document Reference in the firestore database to the order being placed
        orderDoc = FirestoreUtil.firestoreInstance
            .collection("CurrentOrders")
            .document()


        //store in firestore database
        orderDoc.set(hashMapOf(
            "totalAmount" to totalAmountInt,
            "deliveryFee" to deliveryFeeInt,
            "amountToPay" to  totalAmountInt + deliveryFeeInt,
            "totalItemsCount" to totalItemsCount,
            "Instructions" to instructions,
            "shopName" to shopName,
            "shopId" to shopId,
            "orderDate" to currentDateandTime,
            "shopRating" to 0,
            "deliveryExperienceRating" to 0,
            "isOrderPlaced" to true,
            "isOrderAccepted" to false,
            "isOrderAssigned" to false,
            "isShopReached" to false,
            "isOrderCollected" to false,
            "isOrderDelivered" to false,
            "isOrderCancelled" to false,
            "isOrderRated" to false,
            "Items" to itemsHashMap,
            "address" to currentAddressHashMap,
            "UID" to FirebaseAuth.getInstance().currentUser!!.uid,
            "orderId" to orderDoc.id,
            "deliveryAgent" to ""
        )).addOnSuccessListener {
            isOrderPlaced = true
            viewModel.viewModelScope.launch {
                viewModel.insertCurrentOrder(
                    CurrentOrder(
                        Id = orderDoc.id,
                        orderDoc = orderDoc.path,
                        shopName = shopName,
                        amountToPay = totalAmountInt + deliveryFeeInt,
                        totalItemsCount = totalItemsCount,
                        orderDate = currentDateandTime)
                )
            }
            confirmPlacingOrder()

        }
            .addOnFailureListener { areFieldsStored = false }

        }


    /**Checking if the number of items stores matches the size of the cart rows
     * Saving the reference to the document of this order in the location database
     * Navigate to Live-Tracking once the order has been placed successfuly*/
    private fun confirmPlacingOrder() {

        if (isOrderPlaced){
            binding.orderPlacingScreen.visibility = View.GONE
            binding.orderPlacedScreen.visibility = View.VISIBLE
            Handler().postDelayed({
                viewModel.viewModelScope.launch {
                    viewModel.emptyCart()
                }
                findNavController().navigate(PlaceOrderFragmentDirections.actionPlaceOrderFragmentToLiveTrackingFragment(orderDoc.path, orderDoc.id, currentDateandTime,totalItemsCount.toString(),(totalAmountInt+deliveryFeeInt).toString()))
            }, 2000)
        }
    }


}
