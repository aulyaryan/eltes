package com.inventaris.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        // Config management (admin only)
        if (SessionManager.isAdmin()) {
            b.cardConfig.visibility = View.VISIBLE
            loadConfig()
            b.btnSaveConfig.setOnClickListener { saveConfig() }
        }
    }

    private fun loadConfig() {
        b.btnSaveConfig.isEnabled = false
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().getConfig() }
                if (r.isSuccessful && r.body()?.success == true) {
                    // Parse config dari resp.data (LinkedTreeMap)
                    val config = r.body()?.data as? Map<*, *>
                    if (config != null) {
                        b.etInstansi1.setText((config["instansi_baris1"] as? String) ?: "")
                        b.etInstansi2.setText((config["instansi_baris2"] as? String) ?: "")
                        b.etSekolah.setText((config["nama_sekolah"] as? String) ?: "")
                        b.etAlamat.setText((config["alamat"] as? String) ?: "")
                        b.etKontak.setText((config["kontak"] as? String) ?: "")
                        b.etKota.setText((config["kota_surat"] as? String) ?: "")
                        b.etKepsek.setText((config["nama_kepsek"] as? String) ?: "")
                        b.etNipKepsek.setText((config["nip_kepsek"] as? String) ?: "")
                        b.etPetugas.setText((config["nama_petugas"] as? String) ?: "")
                        b.etNipPetugas.setText((config["nip_petugas"] as? String) ?: "")
                        b.etLogoL.setText((config["logo_kiri"] as? String) ?: "")
                        b.etLogoR.setText((config["logo_kanan"] as? String) ?: "")
                    }
                }
            } catch (_: Exception) {}
            finally { b.btnSaveConfig.isEnabled = true }
        }
    }

    private fun saveConfig() {
        b.btnSaveConfig.isEnabled = false
        val data = mapOf(
            "instansi_baris1" to b.etInstansi1.text.toString().trim(),
            "instansi_baris2" to b.etInstansi2.text.toString().trim(),
            "nama_sekolah" to b.etSekolah.text.toString().trim(),
            "alamat" to b.etAlamat.text.toString().trim(),
            "kontak" to b.etKontak.text.toString().trim(),
            "kota_surat" to b.etKota.text.toString().trim(),
            "nama_kepsek" to b.etKepsek.text.toString().trim(),
            "nip_kepsek" to b.etNipKepsek.text.toString().trim(),
            "nama_petugas" to b.etPetugas.text.toString().trim(),
            "nip_petugas" to b.etNipPetugas.text.toString().trim(),
            "logo_kiri" to b.etLogoL.text.toString().trim(),
            "logo_kanan" to b.etLogoR.text.toString().trim(),
        )
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().saveConfig(req = data) }
                if (r.isSuccessful && r.body()?.success == true) {
                    Snackbar.make(b.root, r.body()?.message ?: "Konfigurasi tersimpan", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(b.root, r.body()?.message ?: "Gagal menyimpan", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Snackbar.make(b.root, "Gagal: ${e.localizedMessage ?: "?"}", Snackbar.LENGTH_LONG).show()
            } finally { b.btnSaveConfig.isEnabled = true }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
