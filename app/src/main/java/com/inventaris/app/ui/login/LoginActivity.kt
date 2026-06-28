package com.inventaris.app.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.databinding.ActivityLoginBinding
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.api.SessionManager
import com.inventaris.app.model.LoginRequest
import com.inventaris.app.ui.dashboard.DashboardActivity
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (SessionManager.isLoggedIn()) {
            startActivity(android.content.Intent(this, DashboardActivity::class.java))
            finish(); return
        }
        b.btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        val u = b.etUsername.text.toString().trim()
        val p = b.etPassword.text.toString()
        if (u.isEmpty() || p.isEmpty()) {
            Snackbar.make(b.root, "Isi username dan password", Snackbar.LENGTH_LONG).show(); return
        }
        b.loading.visibility = View.VISIBLE; b.btnLogin.isEnabled = false
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().login(req = LoginRequest(u, p)) }
                if (r.isSuccessful) {
                    val body = r.body()
                    if (body?.status == "success") {
                        val user = com.inventaris.app.utils.Utils.parseObject<com.inventaris.app.model.User>(body)
                        SessionManager.save(user?.username ?: u, user?.namaLengkap ?: u, user?.level ?: "pengguna")
                        startActivity(android.content.Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else Snackbar.make(b.root, body?.message ?: "Login gagal", Snackbar.LENGTH_LONG).show()
                } else Snackbar.make(b.root, "Error ${r.code()}", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                Snackbar.make(b.root, "Koneksi gagal: ${e.localizedMessage ?: "?"}", Snackbar.LENGTH_LONG).show()
            } finally { b.loading.visibility = View.GONE; b.btnLogin.isEnabled = true }
        }
    }

    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
