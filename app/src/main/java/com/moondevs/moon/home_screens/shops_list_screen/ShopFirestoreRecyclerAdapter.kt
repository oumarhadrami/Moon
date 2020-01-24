package com.moondevs.moon.home_screens.shops_list_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.moondevs.moon.R
import com.moondevs.moon.databinding.ShopItemBinding
import java.math.BigDecimal
import java.math.RoundingMode

class ShopFirestoreRecyclerAdapter(
    options: FirestoreRecyclerOptions<Shop>,
    val context: Context?
) : FirestoreRecyclerAdapter<Shop, ShopFirestoreRecyclerAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, p2: Shop) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder  constructor(val binding: ShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Shop) {
            /**Binding name, rating, distance, and image of the shop to the view*/
            binding.shopName.text = item.shopName
            binding.rating.text = BigDecimal(item.shopRating!!).setScale(1, RoundingMode.HALF_EVEN).toString()
            binding.distance.text = item.shopDistance
            Glide.with(binding.shopImage.context)
                .load(item.shopImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(binding.shopImage)

            if (item.numberOfRatings!! == 1.0)
                binding.numOfRating.text = item.numberOfRatings!!.toInt().toString() + " " + context!!.resources.getString(R.string.rating)
            else
                binding.numOfRating.text = item.numberOfRatings!!.toInt().toString() + " " + context!!.resources.getString(R.string.ratings)




            /**handling the click of the shop event*/
            binding.shopContainer.setOnClickListener {
                it.findNavController().navigate(ShopsListFragmentDirections.actionShopsListFragmentToShopFragment(""+snapshots.getSnapshot(adapterPosition).reference.path, item.shopName, item.shopImage, snapshots.getSnapshot(adapterPosition).reference.id))
            }


            binding.executePendingBindings()
        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ShopItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}


