package com.inventaris.app.api

import com.inventaris.app.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SessionCookieJar : CookieJar {
    private val cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cks: List<Cookie>) {
        synchronized(cookies) { cookies.addAll(cks) }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        synchronized(cookies) { return cookies.filter { it.matches(url) }.toList() }
    }
}

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
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .cookieJar(SessionCookieJar())
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
