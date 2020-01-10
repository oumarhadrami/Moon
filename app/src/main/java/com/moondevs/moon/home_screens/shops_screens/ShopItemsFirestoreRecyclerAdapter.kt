package com.moondevs.moon.home_screens.shops_screens

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentShopBinding
import com.moondevs.moon.databinding.ShopitemItemBinding
import com.moondevs.moon.home_screens.shops_screens.cart_database.CartItem
import kotlinx.coroutines.launch


class ShopItemsFirestoreRecyclerAdapter(
        options: FirestoreRecyclerOptions<ShopItem>,
        val shopFragmentBinding: FragmentShopBinding,
        val shopName: String,
        val context: Context?,
        val shopImage: String,
        val viewModel: ShoppingCartViewModel,
        val shopRef: String
) : FirestoreRecyclerAdapter<ShopItem, ShopItemsFirestoreRecyclerAdapter.ViewHolder>(options) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, p2: ShopItem) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder  constructor(val binding: ShopitemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShopItem) {
            viewModel.viewModelScope.launch {
                    if (viewModel.recordExists(item.shopItemId)) {
                        val cartItem = viewModel.getRecord(item.shopItemId)
                        binding.itemCount.text = cartItem.shopItemQuantity.toString()
                        binding.shopItemName.text = cartItem.shopItemName
                        binding.shopItemPrice.text = cartItem.shopItemPrice
                        binding.add.visibility = View.GONE
                        binding.incdecContainer.visibility = View.VISIBLE
                    } else {
                        binding.itemCount.text = item.shopItemCount.toString()
                        binding.shopItemName.text = item.shopItemName
                        binding.shopItemPrice.text = item.shopItemPrice
                    }
            }

            Glide.with(binding.shopItemImage.context)
                .load(item.shopItemImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(binding.shopItemImage)




            //on add button click
            binding.add.setOnClickListener {

                //check if cart is not empty and that shopname does not match with the one in DB
                val totalItemsCount = (shopFragmentBinding.totalItems.text.toString()).substring(0,1).toInt()
                    viewModel.viewModelScope.launch {
                        if (totalItemsCount > 0 && viewModel.dbDoesNotContainThisShopName(shopName))
                        showDialog()
                        else {
                            //Insert cart item if its quantity is 0
                            if (!viewModel.recordExists(item.shopItemId)) {
                                val cartItem = getCartItem(item)
                                viewModel.insert(cartItem)
                                viewModel.update(
                                    CartItem(
                                        shopItemId = cartItem.shopItemId,
                                        shopItemName = cartItem.shopItemName,
                                        shopItemPrice = cartItem.shopItemPrice,
                                        shopItemQuantity = cartItem.shopItemQuantity.plus(1),
                                        shopName = shopName,
                                        shopItemPriceByQuantity = cartItem.shopItemPriceByQuantity,
                                        shopImage = shopImage,
                                        shopRef = shopRef
                                    )
                                )

                                item.shopItemCount = item.shopItemCount.plus(1)
                                binding.itemCount.text = item.shopItemCount.toString()
                                binding.add.visibility = View.GONE
                                binding.incdecContainer.visibility = View.VISIBLE
                                item.shopItemCount = 0
                            }
                        }
                    }
                }


            // on + button click
            binding.addMore.setOnClickListener {
                viewModel.viewModelScope.launch {
                    val cartItem = viewModel.getRecord(item.shopItemId)
                    viewModel.incrementQuantity(cartItem)
                    binding.itemCount.text = cartItem.shopItemQuantity.toString()
                    //update quantity of the cart item ++
                    viewModel.update(cartItem)
                }
            }


            // on - button click
            binding.removeMore.setOnClickListener {
                viewModel.viewModelScope.launch {
                    val cartItem = viewModel.getRecord(item.shopItemId)
                    if (cartItem.shopItemQuantity >= 1) {
                        binding.add.visibility = View.GONE
                        cartItem.shopItemQuantity = cartItem.shopItemQuantity.minus(1)
                        cartItem.shopItemPriceByQuantity = cartItem.shopItemQuantity * cartItem.shopItemPrice.dropLastWhile { it.isLetter() }.trim().toInt()
                        binding.itemCount.text = cartItem.shopItemQuantity.toString()

                        //update quantity of the cart item --
                        viewModel.update(cartItem)
                    }
                    if (cartItem.shopItemQuantity == 0) {
                        binding.itemCount.text = cartItem.shopItemQuantity.toString()
                        binding.incdecContainer.visibility = View.GONE
                        binding.add.visibility = View.VISIBLE

                        //delete cart item when quantity drops to zero
                        viewModel.deleteCartItem(cartItem)
                    }
                }
            }

            binding.executePendingBindings()
        }

        private fun showDialog() {
            MaterialAlertDialogBuilder(context, R.style.AlerDialogStyle)
                .setTitle(R.string.notice)
                .setMessage(R.string.cart_has_items)
                .setPositiveButton(R.string.empty_cart) { _, _ ->
                    emptyCart()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }


        private fun emptyCart() {
            viewModel.viewModelScope.launch {
                viewModel.emptyCart()
            }
        }

        // get the the cart item current values
        private fun getCartItem(item: ShopItem): CartItem {

            return CartItem(shopItemId = item.shopItemId,
                shopItemName = item.shopItemName,
                shopItemPrice = item.shopItemPrice,
                shopItemQuantity = item.shopItemCount,
                shopName = shopName,
                shopItemPriceByQuantity = item.shopItemPrice.dropLastWhile { it.isLetter() }.trim().toInt(),
                shopImage = shopImage,
                shopRef = shopRef)
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ShopitemItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}


