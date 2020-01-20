package com.moondevs.moon.home_screens.account.orders_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.moondevs.moon.databinding.OrderItemBinding
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber

class OrdersFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<Order>) : FirestoreRecyclerAdapter<Order, OrdersFirestoreRecyclerAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, p2: Order) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder  constructor(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Order) {
            /**Binding name, amountTopPay, date, and status of the order to the view*/
            binding.orderAmountToPay.text = item.amountToPay.toString()
            binding.orderDate.text = item.orderDate
            binding.orderShopName.text = item.shopName


            binding.executePendingBindings()
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}