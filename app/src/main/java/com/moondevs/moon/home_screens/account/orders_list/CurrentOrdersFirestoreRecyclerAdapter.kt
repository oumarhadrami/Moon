package com.moondevs.moon.home_screens.account.orders_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.moondevs.moon.databinding.CurrentOrderItemBinding

class CurrentOrdersFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<CurrentOrderItem>) : FirestoreRecyclerAdapter<CurrentOrderItem, CurrentOrdersFirestoreRecyclerAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, p2: CurrentOrderItem) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder  constructor(val binding: CurrentOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CurrentOrderItem) {
            /**Binding name, amountTopPay, date, and status of the order to the view*/
            binding.currentOrderAmountToPay.text = item.amountToPay.toString()
            binding.currentOrderDate.text = item.orderDate
            binding.currentOrderShopName.text = item.shopName
            binding.currentOrderTotalItemsConunt.text = item.totalItemsCount.toString()



            binding.executePendingBindings()
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CurrentOrderItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}