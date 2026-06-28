package com.inventaris.app.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.api.SessionManager
import com.inventaris.app.databinding.ActivitySettingsBinding
import com.inventaris.app.ui.login.LoginActivity
import com.inventaris.app.utils.Utils
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.etApiUrl.setText(RetrofitClient.getBaseUrl())
        b.btnSaveApi.setOnClickListener {
            val url = b.etApiUrl.text.toString().trim()
            if (url.isNotEmpty()) { RetrofitClient.updateBaseUrl(url); Snackbar.make(b.root, "URL API diupdate, login ulang", Snackbar.LENGTH_LONG).show() }
        }
        b.btnLogout.setOnClickListener {
            Utils.confirm(this, "Logout", "Yakin?") {
                scope.launch { try { withContext(Dispatchers.IO) { RetrofitClient.getApi().logout() } } catch (_: Exception) {} }
                SessionManager.logout(); startActivity(Intent(this, LoginActivity::class.java)); finish()
            }
        }
        b.tvUserInfo.text = "${SessionManager.nama()} (${SessionManager.username()})"
        b.tvUserLevel.text = SessionManager.level()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
