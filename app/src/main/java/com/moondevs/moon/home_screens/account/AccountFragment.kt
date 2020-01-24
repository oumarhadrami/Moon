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
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
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
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : OrdersFirestoreRecyclerAdapter
    private lateinit var binding : FragmentAccountBinding
    private lateinit var pastOrdersRef : Query
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /**Initialize binding, viewmodel, and firebase auth*/
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false)
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

        binding.favoritesContainer.setOnClickListener {
            it.findNavController().navigate(AccountFragmentDirections.actionNavigationAccountToFavoritesFragment())
        }

        binding.manageAddressesContainer.setOnClickListener {
            it.findNavController().navigate(AccountFragmentDirections.actionNavigationAccountToAddressListFragment())
        }


        /**Make recyclerview invisible and progressbar visible until data has been retrieved*/
        FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid).collection("PastOrders").orderBy("orderDate",Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null ) {
                binding.ordersList.visibility = View.VISIBLE
                binding.progressBarOrders.visibility = View.GONE
            }
        }


        /**Initializing the collection for all orders delivered according the user and adapter to the recyclerview*/
        pastOrdersRef = FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid).collection("PastOrders").orderBy("orderDate",Query.Direction.DESCENDING).limit(4)
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(pastOrdersRef, Order::class.java).build()
        adapter = OrdersFirestoreRecyclerAdapter(options, shoppingCartViewModel)
        binding.ordersList.adapter = adapter

        /**linking current orders adapter to the recyclerview*/
        val currentOrdersAdapter = CurrentOrdersAdapter(shoppingCartViewModel,activity)
        binding.currentOrdersList.adapter = currentOrdersAdapter
        shoppingCartViewModel.currentOrders.observe(viewLifecycleOwner, Observer {
            it?.let {
                currentOrdersAdapter.submitList(it)
            }
        })


        FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid)
            .collection("CurrentOrders").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error $e")
                    return@addSnapshotListener
                }

                for (document in snapshots!!.documents) {
                    if (document.getBoolean("isOrderDelivered")!!) {
                        moveDocumentToPastOrdersCollection(document.id)
                        break
                    }
                }

            }



        return binding.root
    }

    private fun moveDocumentToPastOrdersCollection(id: String) {
        val oldDoc = FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid)
            .collection("CurrentOrders").document(id)
        val newDoc = FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid)
            .collection("PastOrders").document(id)
        oldDoc.get().addOnCompleteListener {task->
            if (task.isSuccessful){
                val document = task.result
                if (document != null){
                    newDoc.set(document.data!!).addOnSuccessListener {
                        oldDoc.delete()
                        shoppingCartViewModel.viewModelScope.launch {
                            shoppingCartViewModel.deleteCurrentOrder(oldDoc.id)
                        }
                    }
                }

            }

        }
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
