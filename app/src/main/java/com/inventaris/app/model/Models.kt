package com.inventaris.app.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: Any? = null,
    @SerializedName("user") val user: Any? = null
)

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String? = null,
    @SerializedName("nama_lengkap") val namaLengkap: String = "",
    @SerializedName("level") val level: String = "",
    @SerializedName("role") val role: String = "",
    @SerializedName("nama") val nama: String = "",
    @SerializedName("sessionId") val sessionId: String = "",
    @SerializedName("created_at") val createdAt: String? = null
)

data class Inventaris(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("kode_barang") val kodeBarang: String = "",
    @SerializedName("nama_barang") val namaBarang: String = "",
    @SerializedName("kategori") val kategori: String = "",
    @SerializedName("lokasi") val lokasi: String = "",
    @SerializedName("kondisi") val kondisi: String = "",
    @SerializedName("jumlah") val jumlah: Int = 1,
    @SerializedName("tahun") val tahun: String = "",
    @SerializedName("bulan") val bulan: String = "",
    @SerializedName("foto") val foto: String? = null,
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class SaveInventarisRequest(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("kode_barang") val kodeBarang: String,
    @SerializedName("nama_barang") val namaBarang: String,
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
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("level") val level: String
)

data class DeleteRequest(@SerializedName("id") val id: Int)

data class DashboardStats(
    @SerializedName("total_inventaris") val totalInventaris: Int = 0,
    @SerializedName("total_kategori") val totalKategori: Int = 0,
    @SerializedName("total_lokasi") val totalLokasi: Int = 0,
    @SerializedName("total_pengguna") val totalPengguna: Int = 0,
    @SerializedName("total_aset_baik") val totalAsetBaik: Int = 0,
    @SerializedName("total_aset_rusak") val totalAsetRusak: Int = 0
)

data class LogAktivitas(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user") val user: String = "",
    @SerializedName("action") val action: String = "",
    @SerializedName("details") val details: String = "",
    @SerializedName("created_at") val createdAt: String? = null
)
