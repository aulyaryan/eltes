package com.inventaris.app.ui.inventaris

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.R
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityInventarisBinding
import com.inventaris.app.model.Inventaris
import com.inventaris.app.model.DeleteRequest
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

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

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
