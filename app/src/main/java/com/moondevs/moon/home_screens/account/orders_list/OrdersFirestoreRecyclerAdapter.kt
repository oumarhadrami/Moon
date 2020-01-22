package com.moondevs.moon.home_screens.account.orders_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.moondevs.moon.databinding.OrderItemBinding
import com.moondevs.moon.home_screens.account.AccountFragmentDirections
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.cart_database.CartItem
import com.moondevs.moon.util.FirestoreUtil
import kotlinx.coroutines.launch
import timber.log.Timber

class OrdersFirestoreRecyclerAdapter(
    options: FirestoreRecyclerOptions<Order>,
    val shoppingCartViewModel: ShoppingCartViewModel
) : FirestoreRecyclerAdapter<Order, OrdersFirestoreRecyclerAdapter.ViewHolder>(options) {
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
            /**Binding name, amountTopPay, date, and totalItemCount to the view*/
            binding.orderAmountToPay.text = item.amountToPay.toString()
            binding.orderDate.text = item.orderDate
            binding.orderShopName.text = item.shopName
            binding.orderTotalItemsConunt.text = item.totalItemsCount.toString()

            binding.orderReorder.setOnClickListener {

                FirestoreUtil.firestoreInstance.document(snapshots.getSnapshot(adapterPosition).reference.path)
                    .get()
                    .addOnSuccessListener {document->
                        shoppingCartViewModel.viewModelScope.launch {
                            shoppingCartViewModel.emptyCart()
                        val items = document["Items"] as Map<String, Any>
                        for ((key, value) in items) {
                            val shopItem = value as Map<String, Any>
                            shoppingCartViewModel.insert(
                                CartItem(shopItemId = shopItem["shopItemId"].toString(),
                                        shopName = shopItem["shopName"].toString(),
                                        shopImage = shopItem["shopImage"].toString(),
                                        shopItemName = shopItem["shopItemName"].toString(),
                                        shopItemPrice = shopItem["shopItemPrice"].toString(),
                                        shopItemQuantity = Integer.parseInt(shopItem["shopItemQuantity"].toString()),
                                        shopItemPriceByQuantity = Integer.parseInt(shopItem["shopItemPriceByQuantity"].toString()),
                                        shopRef = shopItem["shopRef"].toString(),
                                        shopId = shopItem["shopId"].toString()
                                    )
                            )
                            Timber.i("Well: $value")
                        }
                    }
                }
                it.findNavController().navigate(AccountFragmentDirections.actionNavigationAccountToNavigationCart())
            }


            binding.executePendingBindings()
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}