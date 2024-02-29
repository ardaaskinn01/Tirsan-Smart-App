package com.tirsankardan.tirsanuygulama

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val sharedPreferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val action = LoginFragmentDirections.actionLoginToAnasayfa()
            findNavController(R.id.navigation_graph).navigate(action)
            supportFragmentManager.popBackStack()
        }
    }
}