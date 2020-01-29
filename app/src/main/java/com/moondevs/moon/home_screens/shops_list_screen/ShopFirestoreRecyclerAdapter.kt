package com.moondevs.moon.home_screens.shops_list_screen

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.moondevs.moon.R
import com.moondevs.moon.databinding.ShopItemBinding
import java.math.BigDecimal
import java.math.RoundingMode

class ShopFirestoreRecyclerAdapter(
    options: FirestoreRecyclerOptions<Shop>,
    val context: Context?,
    val activity: FragmentActivity
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
            calculateDistanceBetweenShopAndUser(item.shopLatitude,item.shopLongitude)
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

        private fun calculateDistanceBetweenShopAndUser(shopLatitude: String?, shopLongitude: String?){

            val shopLocation = Location("Point A")
            shopLocation.latitude = shopLatitude!!.toDouble()
            shopLocation.longitude = shopLongitude!!.toDouble()

            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    //getting latitude and longitude and animating camera to user's location
                    val customerLocation = Location("Point B")
                    customerLocation.latitude = location!!.latitude
                    customerLocation.longitude = location.longitude
                    val distance = (shopLocation.distanceTo(customerLocation)/1000).toDouble()
                    binding.distance.text = BigDecimal(distance).setScale(1, RoundingMode.HALF_EVEN).toString() + " km"
                }

        }


    }
    private fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ShopItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}


