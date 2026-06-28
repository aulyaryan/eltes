package com.inventaris.app.ui.users

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.inventaris.app.R
import com.inventaris.app.api.RetrofitClient
import com.inventaris.app.databinding.ActivityFormUserBinding
import com.inventaris.app.model.SaveUserRequest
import kotlinx.coroutines.*

class FormUserActivity : AppCompatActivity() {
    private lateinit var b: ActivityFormUserBinding
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFormUserBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.actLevel.setAdapter(ArrayAdapter.createFromResource(this, R.array.level_array, android.R.layout.simple_dropdown_item_1line))

        editId = intent.getIntExtra("id", 0).takeIf { it > 0 }
        if (editId != null) {
            supportActionBar?.title = "Edit"
            b.etUsername.setText(intent.getStringExtra("u")); b.etNama.setText(intent.getStringExtra("n"))
            b.actLevel.setText(intent.getStringExtra("l") ?: "pengguna", false)
            b.tilPassword.hint = "Password (kosongkan jika tidak diubah)"
            b.btnSave.text = "Update"
        } else { supportActionBar?.title = "Tambah"; b.actLevel.setText("pengguna", false) }
        b.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val u = b.etUsername.text.toString().trim(); val p = b.etPassword.text.toString().trim()
        val n = b.etNama.text.toString().trim(); val l = b.actLevel.text.toString().trim()
        if (u.isEmpty() || n.isEmpty()) { Snackbar.make(b.root, "Username & nama wajib", Snackbar.LENGTH_LONG).show(); return }
        if (editId == null && p.isEmpty()) { Snackbar.make(b.root, "Password wajib untuk baru", Snackbar.LENGTH_LONG).show(); return }

        val pass = if (editId != null && p == "password_tidak_diubah") null else p
        val action = if (editId != null) "updateUser" else "saveUser"

        b.loading.visibility = View.VISIBLE
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { RetrofitClient.getApi().saveUser(action, SaveUserRequest(u, pass, n, l)) }
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
