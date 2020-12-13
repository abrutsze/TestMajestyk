package com.majestykapps.arch.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.snackbar.Snackbar


fun View.showSnackBarWithRetryButton(message: String, retryEvent: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction("RETRY") {
            retryEvent.invoke()
        }.show()
}
fun View.showSnackBarMessage(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
      .show()
}

@Suppress("DEPRECATION")
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    } else {

        connectivityManager.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
    }
}