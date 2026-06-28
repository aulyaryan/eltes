package com.inventaris.app.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: Any? = null,
    @SerializedName("user") val user: Any? = null,
    @SerializedName("stats") val stats: Any? = null,
    @SerializedName("html") val html: String? = null
)

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String? = null,
    @SerializedName("nama") val nama: String = "",
    @SerializedName("role") val role: String = "",
    @SerializedName("sessionId") val sessionId: String = "",
    @SerializedName("lastLogin") val lastLogin: String? = null
)

data class Inventaris(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("kodeBarang") val kodeBarang: String = "",
    @SerializedName("namaBarang") val namaBarang: String = "",
    @SerializedName("kategori") val kategori: String = "",
    @SerializedName("lokasi") val lokasi: String = "",
    @SerializedName("kondisi") val kondisi: String = "",
    @SerializedName("jumlah") val jumlah: Int = 1,
    @SerializedName("tahun") val tahun: String = "",
    @SerializedName("bulan") val bulan: String = "",
    @SerializedName("foto") val foto: String? = null,
    @SerializedName("qrCode") val qrCode: String? = null,
    @SerializedName("updatedDate") val updatedDate: String? = null
)

data class SaveInventarisRequest(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("kodeBarang") val kodeBarang: String,
    @SerializedName("namaBarang") val namaBarang: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("lokasi") val lokasi: String,
    @SerializedName("kondisi") val kondisi: String,
    @SerializedName("jumlah") val jumlah: Int,
    @SerializedName("tahun") val tahun: String,
    @SerializedName("bulan") val bulan: String
)

data class SaveUserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("nama") val nama: String,
    @SerializedName("role") val role: String
)

data class DeleteRequest(@SerializedName("id") val id: Int)

data class DashboardStats(
    @SerializedName("totalBarang") val totalBarang: Int = 0,
    @SerializedName("barangBaik") val barangBaik: Int = 0,
    @SerializedName("barangRusak") val barangRusak: Int = 0,
    @SerializedName("barangHilang") val barangHilang: Int = 0
)

data class LogAktivitas(
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("user") val user: String = "",
    @SerializedName("action") val action: String = "",
    @SerializedName("details") val details: String = ""
)
