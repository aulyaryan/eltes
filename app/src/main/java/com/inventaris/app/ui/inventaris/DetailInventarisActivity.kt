package com.inventaris.app.ui.inventaris

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityDetailInventarisBinding
import com.inventaris.app.model.Inventaris
import com.inventaris.app.model.DeleteRequest
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class DetailInventarisActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetailInventarisBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var itemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailInventarisBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        itemId = intent.getIntExtra("id", 0)
        if (itemId <= 0) { finish(); return }
        b.btnEdit.setOnClickListener { startActivity(Intent(this, FormInventarisActivity::class.java).putExtra("id", itemId)) }
        b.btnDelete.setOnClickListener { Utils.confirm(this, "Hapus", "Yakin?") { delete() } }
        load()
    }

    private fun load() {
        b.loading.visibility = View.VISIBLE
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getAllInventaris() }
                val item = r.body()?.let { Utils.parseList<Inventaris>(it) }?.find { it.id == itemId }
                if (item != null) {
                    b.tvKode.text = item.kodeBarang; b.tvNama.text = item.namaBarang
                    b.tvKategori.text = item.kategori; b.tvLokasi.text = item.lokasi
                    b.tvKondisi.text = item.kondisi; b.tvJumlah.text = "${item.jumlah}"
                    b.tvTahun.text = item.tahun; b.tvBulan.text = item.bulan
                    b.tvCreated.text = item.updatedDate ?: "-"
                    if (!item.qrCode.isNullOrEmpty()) Glide.with(this@DetailInventarisActivity).load(item.qrCode).into(b.ivQr)
                    b.content.visibility = View.VISIBLE
                }
            } catch (e: Exception) { Snackbar.make(b.root, "Gagal: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show() }
            finally { b.loading.visibility = View.GONE }
        }
    }

    private fun delete() {
        b.loading.visibility = View.VISIBLE
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().deleteInventaris(req = DeleteRequest(itemId)) }
                if (r.body()?.success == true) finish()
            } catch (_: Exception) {}
            finally { b.loading.visibility = View.GONE }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
