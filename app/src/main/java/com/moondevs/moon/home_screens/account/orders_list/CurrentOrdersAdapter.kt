package com.moondevs.moon.home_screens.account.orders_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moondevs.moon.databinding.CurrentOrderItemBinding
import com.moondevs.moon.home_screens.account.AccountFragmentDirections
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrder

class CurrentOrdersAdapter(val viewModel: ShoppingCartViewModel, val activity: FragmentActivity?) : ListAdapter<CurrentOrder, CurrentOrdersAdapter.ViewHolder>(CurrentOrderDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)

    }


    inner class ViewHolder constructor(val binding: CurrentOrderItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: CurrentOrder) {

            //binding the name of the item to view
            binding.currentOrder = item
            binding.trackCurrentOrder.setOnClickListener {
                it.findNavController().navigate(AccountFragmentDirections.actionNavigationAccountToLiveTrackingFragment(item.orderDoc))
            }
            binding.executePendingBindings()
        }

    }

    fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CurrentOrderItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}



class CurrentOrderDiffCallback : DiffUtil.ItemCallback<CurrentOrder>(){
    override fun areItemsTheSame(oldItem: CurrentOrder, newItem: CurrentOrder): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: CurrentOrder, newItem: CurrentOrder): Boolean {
        return oldItem == newItem
    }


}