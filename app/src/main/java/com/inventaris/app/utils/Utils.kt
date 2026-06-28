package com.inventaris.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inventaris.app.model.ApiResponse

object Utils {
    private val gson = Gson()

    fun hasNetwork(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    inline fun <reified T> parseList(resp: ApiResponse): List<T> = try {
        gson.fromJson(gson.toJson(resp.data), object : TypeToken<List<T>>() {}.type) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    inline fun <reified T> parseObject(resp: ApiResponse): T? = try {
        gson.fromJson(gson.toJson(resp.data), T::class.java)
    } catch (_: Exception) { null }

    fun confirm(ctx: Context, title: String, msg: String, cb: () -> Unit) {
        MaterialAlertDialogBuilder(ctx).setTitle(title).setMessage(msg)
            .setPositiveButton("Ya") { _, _ -> cb() }.setNegativeButton("Batal", null).show()
    }
}
