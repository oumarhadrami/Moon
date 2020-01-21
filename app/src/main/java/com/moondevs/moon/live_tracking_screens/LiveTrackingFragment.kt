package com.moondevs.moon.live_tracking_screens


import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentLiveTrackingBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import kotlinx.coroutines.launch
import timber.log.Timber


class LiveTrackingFragment : Fragment() {
    private lateinit var binding : FragmentLiveTrackingBinding
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_live_tracking,container,false)

        /**initialize ShoppingCartViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        shoppingCartViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)

        /**visibility of the AppBar and BottomNavView*/
        appBar = activity!!.findViewById(R.id.appbar)
        bottomNav = activity!!.findViewById(R.id.nav_view)
        appBar.visibility = View.GONE
        bottomNav.visibility = View.GONE

        /**Making empty textview size of statusBar*/
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getStatusBarHeight()
        )
        binding.statusbarTextview.layoutParams = params




        /**Navigating back after click the close button*/
        binding.closeTracking.setOnClickListener {
            findNavController().navigate(LiveTrackingFragmentDirections.actionLiveTrackingFragmentToNavigationAccount())
        }

        return binding.root
    }

    /**make appBar and BottomNav visible outside this fragment*/
    override fun onDestroyView() {
        super.onDestroyView()
        appBar.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
    }

    private fun getStatusBarHeight(): Int {
        val height: Int
        val myResources: Resources = resources
        val idStatusBarHeight: Int =
            myResources.getIdentifier("status_bar_height", "dimen", "android")
        height = if (idStatusBarHeight > 0) {
            resources.getDimensionPixelSize(idStatusBarHeight)
        } else {
            0
        }
        return height
    }



}
