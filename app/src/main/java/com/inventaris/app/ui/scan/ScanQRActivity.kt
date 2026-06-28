package com.inventaris.app.ui.scan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.inventaris.app.databinding.ActivityScanBinding
import com.inventaris.app.ui.inventaris.InventarisActivity

class ScanQRActivity : AppCompatActivity() {
    private lateinit var b: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScanBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scan QR Code"

        b.btnScanCamera.setOnClickListener {
            IntentIntegrator(this)
                .setDesiredBarcodeFormats("QR_CODE")
                .setPrompt("Arahkan kamera ke QR Code")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(false)
                .setOrientationLocked(true)
                .initiateScan()
        }

        b.btnSearchByCode.setOnClickListener {
            val code = b.etManualCode.text.toString().trim()
            if (code.isEmpty()) {
                b.etManualCode.error = "Masukkan kode barang"
                return@setOnClickListener
            }
            searchItemByCode(code)
        }
    }

    private fun searchItemByCode(code: String) {
        // Buka InventarisActivity dengan filter kode
        val intent = Intent(this, InventarisActivity::class.java)
        intent.putExtra("filter_kode", code)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scannedCode = result.contents
            Toast.makeText(this, "Hasil scan: $scannedCode", Toast.LENGTH_SHORT).show()
            searchItemByCode(scannedCode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
