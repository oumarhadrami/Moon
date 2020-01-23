package com.moondevs.moon.home_screens.account.favorites


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager

import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentFavoritesBinding


class FavoritesFragment : Fragment() {

    private lateinit var binding : FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)

        /**initialize FavoriteShopsViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = FavoritesViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(FavoritesViewModel::class.java)


        /**linking adapter to the recyclerview*/
        val adapter = FavoriteShopAdapter()
        binding.favoriteShopsList.adapter = adapter
        val manager = GridLayoutManager(activity, 2)
        binding.favoriteShopsList.layoutManager = manager
        viewModel.allfavoriteShops.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })


        return binding.root
    }


}
