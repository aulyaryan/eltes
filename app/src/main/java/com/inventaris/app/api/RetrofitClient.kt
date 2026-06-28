package com.inventaris.app.api

import com.inventaris.app.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var api: ApiService? = null
    private var baseUrl: String = BuildConfig.API_BASE_URL

    fun init() { build() }

    fun updateBaseUrl(url: String) {
        val u = url.trim().let { if (!it.endsWith("/")) "$it/" else it }
        if (u != baseUrl) { baseUrl = u; build() }
    }

    fun getBaseUrl(): String = baseUrl
    fun getApi(): ApiService = api ?: build().let { api!! }

    private fun build(): ApiService {
        val cm = CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cm))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        return api!!
    }
}
