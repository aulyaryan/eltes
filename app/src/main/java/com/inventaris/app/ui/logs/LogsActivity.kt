package com.inventaris.app.ui.logs

import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityLogsBinding
import com.inventaris.app.databinding.ItemLogBinding
import com.inventaris.app.model.LogAktivitas
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class LogsAdapter(private var items: List<LogAktivitas>) : RecyclerView.Adapter<LogsAdapter.VH>() {
    fun update(d: List<LogAktivitas>) { items = d; notifyDataSetChanged() }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemLogBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, i: Int) = h.bind(items[i])
    override fun getItemCount() = items.size
    inner class VH(private val b: ItemLogBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(it: LogAktivitas) { b.tvUser.text = it.user; b.tvAction.text = it.action; b.tvDetails.text = it.details; b.tvTime.text = it.createdAt ?: "" }
    }
}

class LogsActivity : AppCompatActivity() {
    private lateinit var b: ActivityLogsBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val adp = LogsAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.rv.layoutManager = LinearLayoutManager(this); b.rv.adapter = adp
        b.swipe.setOnRefreshListener { load() }; load()
    }

    private fun load() {
        b.swipe.isRefreshing = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getAllLogs() }
                val list = r.body()?.let { Utils.parseList<LogAktivitas>(it) } ?: emptyList()
                adp.update(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            } catch (_: Exception) {}
            finally { b.swipe.isRefreshing = false }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
