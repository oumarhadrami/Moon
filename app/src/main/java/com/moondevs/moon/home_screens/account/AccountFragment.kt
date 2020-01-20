package com.moondevs.moon.home_screens.account


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.moondevs.moon.introduction_screen.MainActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentAccountBinding
import com.moondevs.moon.home_screens.account.orders_list.Order
import com.moondevs.moon.home_screens.account.orders_list.OrdersFirestoreRecyclerAdapter
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber

class AccountFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : OrdersFirestoreRecyclerAdapter
    private lateinit var viewModel: AccountViewModel
    private lateinit var binding : FragmentAccountBinding
    private lateinit var ordersRef : Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        /**Initialize binding, viewmodel, and firebase auth*/
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account,container,false)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        auth = FirebaseAuth.getInstance()


        /**Log out with firebase*/
        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
        }

        /**Initializing the collection for all orders according the user and adapter to the recyclerview*/
        ordersRef = FirestoreUtil.firestoreInstance.collection("Orders").document(auth.currentUser!!.uid).collection("Orders").orderBy("orderDate",Query.Direction.DESCENDING)
        Timber.i("$ordersRef")
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(ordersRef, Order::class.java).build()
        adapter = OrdersFirestoreRecyclerAdapter(options)
        Timber.i("adapter assigned")
        binding.ordersList.adapter = adapter



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
