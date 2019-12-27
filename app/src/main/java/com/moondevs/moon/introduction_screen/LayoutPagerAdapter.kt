package com.moondevs.moon.introduction_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.moondevs.moon.R

class LayoutPagerAdapter(val layoutInflater: LayoutInflater): PagerAdapter() {

    val layouts: Array<Int> = arrayOf(
        R.layout.intro_layout_1, R.layout.intro_layout_2,
        R.layout.intro_layout_3)


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any =
        LayoutInflater.from(viewGroup.context).inflate(layouts[position], viewGroup, false).also {
            viewGroup.addView(it)
        }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}