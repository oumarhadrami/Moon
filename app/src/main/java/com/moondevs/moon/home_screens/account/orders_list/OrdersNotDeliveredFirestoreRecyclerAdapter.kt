package com.moondevs.moon.home_screens.account.orders_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.moondevs.moon.databinding.OrderNotDeliveredItemBinding

class OrdersNotDeliveredFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<OrderNotDeliveredItem>) : FirestoreRecyclerAdapter<OrderNotDeliveredItem, OrdersNotDeliveredFirestoreRecyclerAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, p2: OrderNotDeliveredItem) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder  constructor(val binding: OrderNotDeliveredItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderNotDeliveredItem) {
            /**Binding name, amountTopPay, date, and status of the order to the view*/
            binding.orderNotAmountToPay.text = item.amountToPay.toString()
            binding.orderNotDate.text = item.orderDate
            binding.orderNotShopName.text = item.shopName
            binding.orderNotTotalItemsConunt.text = item.totalItemsCount.toString()



            binding.executePendingBindings()
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OrderNotDeliveredItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}