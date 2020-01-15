package com.moondevs.moon.splash_screen

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.moondevs.moon.introduction_screen.MainActivity
import com.moondevs.moon.R
import com.moondevs.moon.databinding.ActivitySplashBinding
import com.moondevs.moon.home_screens.HomeActivity
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_splash
        )

        /**Adding the video from raw folder and playing it while the thread is sleeping*/
        val uri = Uri.parse("android.resource://" + this.packageName + "/" + R.raw.splashvideo)
        binding.video.setVideoURI(uri)
        binding.video.start()

        /** initialize firebase auth */
        auth = FirebaseAuth.getInstance()

        /**Thread sleeps for 3 seconds and then naviagte
         * to proper activity depending on whether the user s logged in or not*/
        thread {
            Thread.sleep((3 * 1000).toLong())
            if (auth.currentUser == null)
            {
                startMainActivity()
            }
            else
                startHomeActivity()
        }.priority = Thread.NORM_PRIORITY
    }

    private fun startHomeActivity() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
