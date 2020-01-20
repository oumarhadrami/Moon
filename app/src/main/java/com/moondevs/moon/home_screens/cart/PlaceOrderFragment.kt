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
import com.moondevs.moon.address_screens.AddressViewModel
import com.moondevs.moon.address_screens.addresses_database.Address
import com.moondevs.moon.address_screens.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentPlaceOrderBinding
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrder
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import com.moondevs.moon.util.FirestoreUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class PlaceOrderFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var orderDoc : DocumentReference
    @Volatile private var itemsStoredSize : Int = 0
    @Volatile private var isAddressStored : Boolean = false
    @Volatile private var areItemsStored : Boolean = false
    @Volatile private var areFieldsStored : Boolean = false
    @Volatile private var isOrderPlaced : Boolean = false
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var args : PlaceOrderFragmentArgs
    private lateinit var instructions : String
    private lateinit var deliveryFee : String


    private lateinit var binding : FragmentPlaceOrderBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_place_order,container,false)

        /**initialize ShoppingCartViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)

        /**Initialize AddressViewModel*/
        val addressViewModelFactory = AddressViewModelFactory(application)
        addressViewModel = ViewModelProviders.of(activity!!, addressViewModelFactory).get(AddressViewModel::class.java)


        /**Initialize the AppBar and BottomNavView*/
        appBar = activity!!.findViewById(R.id.appbar)
        bottomNav = activity!!.findViewById(R.id.nav_view)

        /**fetch instructions froms arguments bundle*/
        args = PlaceOrderFragmentArgs.fromBundle(arguments!!)
        instructions = args.instructions
        deliveryFee = args.deliveryFee
        placeOrder()


        return binding.root
    }




    /**Method handling the placing of the order
     * Add Fields to the database*/
    private fun placeOrder() {
        appBar.visibility = View.GONE
        bottomNav.visibility = View.GONE
        binding.orderPlacingScreen.visibility = View.VISIBLE
        /**Get time from device*/
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDateandTime = formatter.format(Date())
        //Document Reference in the firestore database to the order being placed
        orderDoc = FirestoreUtil.firestoreInstance
            .collection("Orders")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .collection("Orders")
            .document()





        /**Add the total amount, delivery fee, and overall total amount of the items ordered to the order document reference*/

        val deliveryFeeInt = Integer.parseInt(deliveryFee.dropLastWhile { it.isLetter() }.trim())
        viewModel.totalAmount.observe(viewLifecycleOwner, Observer { totalAmount ->
            viewModel.allItemsCount.observe(viewLifecycleOwner, Observer {
                var totalItemsCount = it

                orderDoc.set(hashMapOf(
                    "TotalAmount" to totalAmount,
                    "DeliveryFee" to deliveryFeeInt,
                    "amountToPay" to  totalAmount + deliveryFeeInt,
                    "TotalItemsCount" to totalItemsCount,
                    "Instructions" to instructions,
                    "orderDate" to currentDateandTime,
                    "isOrderAccepted" to false,
                    "isOrderAssigned" to false,
                    "isOrderCollected" to false,
                    "isOrderDelivered" to false
                )).addOnSuccessListener {
                    areFieldsStored = true
                    this.storeAddress(areFieldsStored)
                }
                    .addOnFailureListener { areFieldsStored = false }
            })
        })








    }

    /**Add the address of this order to the order document reference*/
    private fun storeAddress(areFieldsStored: Boolean) {
        if (areFieldsStored) {
            Timber.i("Fields Stored")
            addressViewModel.viewModelScope.launch {
                val addressDoc = orderDoc.collection("Address").document()
                val currentAddress: Address = addressViewModel.getLastAddedAddress()
                val addressHashMap = hashMapOf(
                    "Name" to currentAddress.Name,
                    "PhoneNumber" to currentAddress.PhoneNumber,
                    "Latitude" to currentAddress.Latitude,
                    "Longitude" to currentAddress.Longitude
                )
                addressDoc.set(addressHashMap).addOnSuccessListener {

                    isAddressStored = true
                    storeItems(isAddressStored)
                }
                    .addOnFailureListener { isAddressStored = false }
            }
        }
    }

    /**Add All Items from the cart to the items-collection in the order */
    private fun storeItems(isAddressStored: Boolean) {
        if (isAddressStored) {
            Timber.i("Address Stored")
            viewModel.allCartItems.observe(viewLifecycleOwner, Observer { cartItems ->
                for (item in cartItems) {
                    val cartItem = hashMapOf(
                        "shopItemId" to item.shopItemId,
                        "shopItemName" to item.shopItemName,
                        "shopItemPrice" to item.shopItemPrice,
                        "shopItemQuantity" to item.shopItemQuantity,
                        "shopName" to item.shopName,
                        "shopImage" to item.shopImage,
                        "shopItemPriceByQuantity" to item.shopItemPriceByQuantity
                    )
                    val orderItemDoc = orderDoc.collection("Items").document()
                    orderItemDoc.set(cartItem).addOnSuccessListener {
                        itemsStoredSize += 1
                        confirmPlacingOrder(itemsStoredSize)
                    }
                        .addOnFailureListener { }
                }

            })
        }
    }

    /**Checking if the number of items stores matches the size of the cart rows
     * Saving the reference to the document of this order in the location database
     * Navigate to Live-Tracking once the order has been placed successfuly*/
    private fun confirmPlacingOrder(itemsStoredSize: Int) {
        viewModel.numberOfUniqueItems.observe(viewLifecycleOwner, Observer { numberOfUniqueItems ->
            areItemsStored = itemsStoredSize == numberOfUniqueItems
            if (areItemsStored){
                isOrderPlaced = true
                viewModel.viewModelScope.launch {
                    viewModel.insertCurrentOrder(getCurrentOrder())
                }}

            if (isOrderPlaced){
                binding.orderPlacingScreen.visibility = View.GONE
                binding.orderPlacedScreen.visibility = View.VISIBLE
                Timber.i("Items Stored")
                Timber.i("Order Placed")
                Handler().postDelayed({
                    viewModel.viewModelScope.launch {
                        viewModel.emptyCart()
                        Timber.i("${viewModel.getLastAddedOrder()}")
                    }
                    findNavController().navigate(PlaceOrderFragmentDirections.actionPlaceOrderFragmentToLiveTrackingFragment())
                }, 2000)
            }
        })

    }

    /**Store the orderDoc and Navigate to Live-Tracking*/

    private fun getCurrentOrder() : CurrentOrder {
        return CurrentOrder(orderDoc = orderDoc.path)
    }


}
