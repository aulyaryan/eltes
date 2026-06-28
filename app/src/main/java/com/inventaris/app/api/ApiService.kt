package com.inventaris.app.api

import com.inventaris.app.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/index.php")
    suspend fun login(@Query("action") action: String = "login", @Body req: LoginRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun logout(@Query("action") action: String = "logout", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getDashboardStats(@Query("action") action: String = "getDashboardStats", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getAllSystemData(@Query("action") action: String = "getAllSystemData", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getAllInventaris(@Query("action") action: String = "getAllInventaris", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun saveInventaris(@Query("action") action: String = "saveInventaris", @Body req: SaveInventarisRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun updateInventaris(@Query("action") action: String = "updateInventaris", @Body req: SaveInventarisRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun deleteInventaris(@Query("action") action: String = "deleteInventaris", @Body req: DeleteRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getAllUsers(@Query("action") action: String = "getAllUsers", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun saveUser(@Query("action") action: String = "saveUser", @Body req: SaveUserRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun updateUser(@Query("action") action: String = "updateUser", @Body req: SaveUserRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun deleteUser(@Query("action") action: String = "deleteUser", @Body req: DeleteRequest): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getConfig(@Query("action") action: String = "getConfig", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun saveConfig(@Query("action") action: String = "saveConfig", @Body req: Map<String, Any>): Response<ApiResponse>

    @POST("api/index.php")
    suspend fun getAllLogs(@Query("action") action: String = "getAllLogs", @Body body: Map<String, Any> = emptyMap()): Response<ApiResponse>

    @Multipart
    @POST("api/index.php")
    suspend fun uploadLogo(@Query("action") action: String = "uploadLogo", @Part image: MultipartBody.Part): Response<ApiResponse>
}
