package com.inventaris.app.ui.inventaris

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.R
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.api.SessionManager
import com.inventaris.app.databinding.ActivityInventarisBinding
import com.inventaris.app.model.Inventaris
import com.inventaris.app.model.DeleteRequest
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class InventarisActivity : AppCompatActivity() {
    private lateinit var b: ActivityInventarisBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var all: List<Inventaris> = emptyList()
    private val adp = InventarisAdapter(emptyList(),
        { i -> startActivity(Intent(this, DetailInventarisActivity::class.java).putExtra("id", i.id)) },
        { i -> Utils.confirm(this, "Hapus", "Hapus ${i.namaBarang}?") {
            scope.launch {
                try { withContext(Dispatchers.IO) { RetrofitClient.getApi().deleteInventaris(req = DeleteRequest(i.id)) }; load()
                } catch (_: Exception) {}
            }
        }}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityInventarisBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.rv.layoutManager = LinearLayoutManager(this); b.rv.adapter = adp
        b.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(t: String?): Boolean { filter(t ?: ""); return true }
        })
        b.swipe.setOnRefreshListener { load() }
        b.fab.setOnClickListener { startActivity(Intent(this, FormInventarisActivity::class.java)) }
        b.btnExportPdf.setOnClickListener { exportPdf() }
        b.btnExportExcel.setOnClickListener { exportExcel() }
        // Handle filter from QR scan
        val filterKode = intent.getStringExtra("filter_kode")
        if (!filterKode.isNullOrEmpty()) {
            b.search.setQuery(filterKode, true)
        }
        load()
    }

    override fun onResume() { super.onResume(); load() }

    private fun load() {
        b.swipe.isRefreshing = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getAllInventaris() }
                if (r.isSuccessful) {
                    all = r.body()?.let { Utils.parseList<Inventaris>(it) } ?: emptyList()
                    adp.update(all); b.tvEmpty.visibility = if (all.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) { Snackbar.make(b.root, "Gagal: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show() }
            finally { b.swipe.isRefreshing = false }
        }
    }

    private fun filter(q: String) {
        adp.update(if (q.isEmpty()) all else all.filter {
            it.namaBarang.contains(q, true) || it.kodeBarang.contains(q, true) || it.kategori.contains(q, true)
        })
    }

    private fun exportPdf() {
        b.btnExportPdf.isEnabled = false
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().generatePdf() }
                if (r.isSuccessful && r.body()?.success == true) {
                    val html = r.body()?.html
                    if (html != null) {
                        startActivity(Intent(this@InventarisActivity, PdfViewerActivity::class.java).putExtra("html", html))
                    } else {
                        Snackbar.make(b.root, "Gagal memproses laporan", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(b.root, r.body()?.message ?: "Gagal generate PDF", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Snackbar.make(b.root, "Gagal: ${e.localizedMessage ?: "?"}", Snackbar.LENGTH_LONG).show()
            } finally { b.btnExportPdf.isEnabled = true }
        }
    }

    private fun exportExcel() {
        b.btnExportExcel.isEnabled = false
        scope.launch {
            try {
                val url = "${RetrofitClient.getBaseUrl()}api/index.php?action=exportExcel"
                val sessionId = SessionManager.getSessionId()
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Session-Id", sessionId)
                    .post(okhttp3.RequestBody.create(null, "{}"))
                    .build()

                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        val file = File(cacheDir, "inventaris_export.xlsx")
                        withContext(Dispatchers.IO) {
                            FileOutputStream(file).use { fos ->
                                body.byteStream().use { it.copyTo(fos) }
                            }
                        }
                        val uri = FileProvider.getUriForFile(this@InventarisActivity,
                            "${packageName}.fileprovider", file)
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(Intent.createChooser(intent, "Buka dengan"))
                    }
                } else {
                    Snackbar.make(b.root, "Gagal download Excel", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Snackbar.make(b.root, "Gagal: ${e.localizedMessage ?: "?"}", Snackbar.LENGTH_LONG).show()
            } finally { b.btnExportExcel.isEnabled = true }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
