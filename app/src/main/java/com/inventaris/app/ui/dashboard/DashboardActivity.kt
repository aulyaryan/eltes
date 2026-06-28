package com.inventaris.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.R
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.api.SessionManager
import com.inventaris.app.databinding.ActivityDashboardBinding
import com.inventaris.app.model.DashboardStats
import com.inventaris.app.ui.inventaris.InventarisActivity
import com.inventaris.app.ui.logs.LogsActivity
import com.inventaris.app.ui.login.LoginActivity
import com.inventaris.app.ui.settings.SettingsActivity
import com.inventaris.app.ui.users.UsersActivity
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var b: ActivityDashboardBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(this, b.drawer, b.toolbar, R.string.nav_open, R.string.nav_close)
        b.drawer.addDrawerListener(toggle); toggle.syncState()
        b.navView.setNavigationItemSelectedListener { item ->
            b.drawer.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_inventaris -> startActivity(Intent(this, InventarisActivity::class.java))
                R.id.nav_users -> startActivity(Intent(this, UsersActivity::class.java))
                R.id.nav_logs -> startActivity(Intent(this, LogsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_logout -> Utils.confirm(this, "Logout", "Yakin?") {
                    scope.launch { try { withContext(Dispatchers.IO) { RetrofitClient.getApi().logout() } } catch (_: Exception) {} }
                    SessionManager.logout(); startActivity(Intent(this, LoginActivity::class.java)); finish()
                }
            }; true
        }
        b.cardInventaris.setOnClickListener { startActivity(Intent(this, InventarisActivity::class.java)) }
        b.cardUsers.setOnClickListener { startActivity(Intent(this, UsersActivity::class.java)) }
        b.cardLogs.setOnClickListener { startActivity(Intent(this, LogsActivity::class.java)) }
        b.swipe.setOnRefreshListener { load() }
        load()
    }

    private fun load() {
        b.swipe.isRefreshing = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getDashboardStats() }
                if (r.isSuccessful) {
                    val stats = r.body()?.let { Utils.parseObject<DashboardStats>(it) }
                    if (stats != null) {
                        b.tvTotalInventaris.text = "${stats.totalInventaris}"
                        b.tvTotalKategori.text = "${stats.totalKategori}"
                        b.tvTotalLokasi.text = "${stats.totalLokasi}"
                        b.tvTotalPengguna.text = "${stats.totalPengguna}"
                        b.tvTotalBaik.text = "${stats.totalAsetBaik}"
                        b.tvTotalRusak.text = "${stats.totalAsetRusak}"
                        b.content.visibility = View.VISIBLE
                    }
                }
            } catch (_: Exception) {}
            finally { b.swipe.isRefreshing = false }
        }
    }

    override fun onBackPressed() {
        if (b.drawer.isDrawerOpen(GravityCompat.START)) b.drawer.closeDrawer(GravityCompat.START)
        else Utils.confirm(this, "Keluar", "Tutup aplikasi?") { finishAffinity() }
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
