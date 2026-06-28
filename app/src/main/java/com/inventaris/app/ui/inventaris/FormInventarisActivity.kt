package com.inventaris.app.ui.inventaris

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.R
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityFormInventarisBinding
import com.inventaris.app.model.Inventaris
import com.inventaris.app.model.SaveInventarisRequest
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class FormInventarisActivity : AppCompatActivity() {
    private lateinit var b: ActivityFormInventarisBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFormInventarisBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.actKategori.setAdapter(ArrayAdapter.createFromResource(this, R.array.kategori_array, android.R.layout.simple_dropdown_item_1line))
        b.actKondisi.setAdapter(ArrayAdapter.createFromResource(this, R.array.kondisi_array, android.R.layout.simple_dropdown_item_1line))
        b.actBulan.setAdapter(ArrayAdapter.createFromResource(this, R.array.bulan_array, android.R.layout.simple_dropdown_item_1line))

        editId = intent.getIntExtra("id", 0).takeIf { it > 0 }
        if (editId != null) {
            supportActionBar?.title = "Edit"; b.btnSave.text = "Update"
            loadItem(editId!!)
        } else {
            supportActionBar?.title = "Tambah"
            b.etKodeBarang.setText("INV-${System.currentTimeMillis().toString().takeLast(6)}")
        }
        b.btnSave.setOnClickListener { save() }
    }

    private fun loadItem(id: Int) {
        b.loading.visibility = View.VISIBLE
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getAllInventaris() }
                val item = r.body()?.let { Utils.parseList<Inventaris>(it) }?.find { it.id == id }
                item?.let {
                    b.etKodeBarang.setText(it.kodeBarang); b.etNamaBarang.setText(it.namaBarang)
                    b.actKategori.setText(it.kategori, false); b.etLokasi.setText(it.lokasi)
                    b.actKondisi.setText(it.kondisi, false); b.etJumlah.setText("${it.jumlah}")
                    b.etTahun.setText(it.tahun); b.actBulan.setText(it.bulan, false)
                }
            } catch (_: Exception) {}
            finally { b.loading.visibility = View.GONE }
        }
    }

    private fun save() {
        val req = SaveInventarisRequest(
            id = editId, kodeBarang = b.etKodeBarang.text.toString().trim(),
            namaBarang = b.etNamaBarang.text.toString().trim(),
            kategori = b.actKategori.text.toString().trim(), lokasi = b.etLokasi.text.toString().trim(),
            kondisi = b.actKondisi.text.toString().trim(), jumlah = (b.etJumlah.text.toString().toIntOrNull() ?: 1),
            tahun = b.etTahun.text.toString().trim(), bulan = b.actBulan.text.toString().trim()
        )
        if (req.kodeBarang.isEmpty() || req.namaBarang.isEmpty()) {
            Snackbar.make(b.root, "Kode & nama barang wajib", Snackbar.LENGTH_LONG).show(); return
        }
        b.loading.visibility = View.VISIBLE
        scope.launch {
            try {
                val action = if (editId != null) "updateInventaris" else "saveInventaris"
                val r = withContext(Dispatchers.IO) {
                    if (editId != null) RetrofitClient.getApi().updateInventaris(action, req)
                    else RetrofitClient.getApi().saveInventaris(action, req)
                }
                if (r.body()?.success == true) finish()
                else Snackbar.make(b.root, r.body()?.message ?: "Gagal", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) { Snackbar.make(b.root, "Gagal: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show() }
            finally { b.loading.visibility = View.GONE }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
