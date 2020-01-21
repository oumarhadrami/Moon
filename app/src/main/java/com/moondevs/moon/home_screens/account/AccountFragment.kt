package com.moondevs.moon.home_screens.account


import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.moondevs.moon.introduction_screen.MainActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentAccountBinding
import com.moondevs.moon.home_screens.account.orders_list.*
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber

class AccountFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : OrdersFirestoreRecyclerAdapter
    private lateinit var viewModel: AccountViewModel
    private lateinit var binding : FragmentAccountBinding
    private lateinit var pastOrdersRef : Query
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /**Initialize binding, viewmodel, and firebase auth*/
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        /**Initializing shoppingCartViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        shoppingCartViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)


        /**Log out with firebase*/
        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
        }



        /**Initializing the collection for all orders delivered according the user and adapter to the recyclerview*/
        pastOrdersRef = FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid).collection("PastOrders").orderBy("orderDate",Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(pastOrdersRef, Order::class.java).build()
        adapter = OrdersFirestoreRecyclerAdapter(options)
        binding.ordersList.adapter = adapter

        /**linking adapter to the recyclerview*/
        val adapter = CurrentOrdersAdapter(shoppingCartViewModel,activity)
        binding.currentOrdersList.adapter = adapter
        shoppingCartViewModel.currentOrders.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })


        /*FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid)
            .collection("CurrentOrders").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error $e")
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documents) {
                    if (dc.getBoolean("isOrderDelivered")!!) {
                        FirestoreUtil.firestoreInstance.collection("Orders")
                            .document(auth.currentUser!!.uid)
                            .collection("CurrentOrders").document(dc.id).delete()
                        Timber.i(dc.toString())
                    }
                }
            }*/



        return binding.root
    }

    override fun onStart() {
        super.onStart()

        /**Adapter will start listening*/
        adapter.startListening()

        //Make the frameLayout View.GONE
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        frameLayout.visibility = View.GONE


    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}
