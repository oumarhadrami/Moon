package com.moondevs.moon.home_screens.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moondevs.moon.R
import com.moondevs.moon.databinding.CartItemBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.cart_database.CartItem
import kotlinx.android.synthetic.main.fragment_shop.view.*

class CartAdapter(val viewModel: ShoppingCartViewModel, val activity: FragmentActivity?) : ListAdapter<CartItem, CartAdapter.ViewHolder>(CartDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)

    }


    inner class ViewHolder constructor(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: CartItem) {
            binding.cart = item
            val currecyText = " MRU"
            binding.cartItemQuantity.text = item.shopItemQuantity.toString()
            binding.cartPriceByQuantity.text = item.shopItemPriceByQuantity.toString() + currecyText



            binding.cartAddMore.setOnClickListener {
                viewModel.incrementQuantity(item)
                binding.cartItemQuantity.text = item.shopItemQuantity.toString()
                binding.cartPriceByQuantity.text = item.shopItemPriceByQuantity.toString() + currecyText
            }


            binding.cartRemoveMore.setOnClickListener {
                viewModel.decerementQuantity(item)
                binding.cartItemQuantity.text = item.shopItemQuantity.toString()
                binding.cartPriceByQuantity.text = item.shopItemPriceByQuantity.toString() + currecyText
            }
            binding.executePendingBindings()
        }

    }

    fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CartItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}



class CartDiffCallback : DiffUtil.ItemCallback<CartItem>(){
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.shopItemId == newItem.shopItemId
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }

}