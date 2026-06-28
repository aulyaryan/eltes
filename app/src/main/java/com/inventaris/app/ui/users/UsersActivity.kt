package com.inventaris.app.ui.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityUsersBinding
import com.inventaris.app.model.User
import com.inventaris.app.model.DeleteRequest
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class UsersActivity : AppCompatActivity() {
    private lateinit var b: ActivityUsersBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val adp = UsersAdapter(emptyList(),
        { u -> startActivity(Intent(this, FormUserActivity::class.java).apply {
            putExtra("id", u.id); putExtra("u", u.username); putExtra("n", u.nama); putExtra("l", u.role)
        }}},
        { u -> Utils.confirm(this, "Hapus", "Hapus ${u.username}?") {
            scope.launch {
                try { withContext(Dispatchers.IO) { RetrofitClient.getApi().deleteUser(req = DeleteRequest(u.id)) } } catch (_: Exception) {}
                load()
            }
        }}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.rv.layoutManager = LinearLayoutManager(this); b.rv.adapter = adp
        b.swipe.setOnRefreshListener { load() }
        b.fab.setOnClickListener { startActivity(Intent(this, FormUserActivity::class.java)) }
        load()
    }

    override fun onResume() { super.onResume(); load() }

    private fun load() {
        b.swipe.isRefreshing = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getAllUsers() }
                val list = r.body()?.let { Utils.parseList<User>(it) } ?: emptyList()
                adp.update(list); b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) { Snackbar.make(b.root, "Gagal: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show() }
            finally { b.swipe.isRefreshing = false }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
