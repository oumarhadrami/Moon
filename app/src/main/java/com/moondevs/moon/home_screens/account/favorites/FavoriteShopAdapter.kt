package com.moondevs.moon.home_screens.account.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FavoriteShopItemBinding
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShop
import timber.log.Timber

class FavoriteShopAdapter : ListAdapter<FavoriteShop, FavoriteShopAdapter.ViewHolder>(FavoriteShopDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)

    }

    inner class ViewHolder constructor(val binding: FavoriteShopItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: FavoriteShop) {
            Timber.i("Inside adapter")
            //binding the name of the item to view
            binding.favoriteShop = item
            Glide.with(binding.favoriteShopImage.context)
                .load(item.shopImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(binding.favoriteShopImage)


            binding.executePendingBindings()
        }

    }

    fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FavoriteShopItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

}



class FavoriteShopDiffCallback : DiffUtil.ItemCallback<FavoriteShop>(){
    override fun areItemsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem.shopId == newItem.shopId
    }

    override fun areContentsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem == newItem
    }
}