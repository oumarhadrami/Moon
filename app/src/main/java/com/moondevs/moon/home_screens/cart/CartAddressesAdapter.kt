package com.moondevs.moon.home_screens.cart


import android.content.Context
import com.moondevs.moon.home_screens.account.adresses.addresses_database.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moondevs.moon.R
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressViewModel
import com.moondevs.moon.databinding.AddressItemInCartBinding
import kotlinx.coroutines.launch

class CartAddressesAdapter(val context: Context, val viewModel: AddressViewModel) : ListAdapter<Address, CartAddressesAdapter.ViewHolder>(AddressDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)

    }

    inner class ViewHolder constructor(val binding: AddressItemInCartBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Address) {
            binding.addressListNameInCart.text = item.Name
            binding.addressListNumberInCart.text = item.PhoneNumber
            binding.addressListLatitudeInCart.text = item.Latitude
            binding.addressListLongitudeInCart.text = item.Longitude
            binding.addressSelectedInCart.isChecked = item.isThisTheSelectedAddress

            
            binding.addressContainerCardInCart.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.updateSelectedAddress(item)
                }
            }
            binding.addressSelectedInCart.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.updateSelectedAddress(item)
                }
            }


            binding.executePendingBindings()
        }


        /**Method called when there are items from another shop*/
        private fun showDialog(addressId: Long) {
            MaterialAlertDialogBuilder(context, R.style.AlerDialogStyle)
                .setTitle(R.string.delete_Address)
                .setMessage(R.string.are_you_sure_you_want_to_delete_this_address)
                .setPositiveButton(R.string.yes) { _, _ ->
                    deleteAddress(addressId)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        private fun deleteAddress(addressId: Long) {
            viewModel.viewModelScope.launch {
                viewModel.deleteAddress(addressId)
            }
        }


    }

    fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AddressItemInCartBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}



class AddressDiffCallback : DiffUtil.ItemCallback<Address>(){
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.addressId == newItem.addressId
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }
}