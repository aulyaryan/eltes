package com.inventaris.app.ui.inventaris

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.inventaris.app.databinding.ActivityPdfViewerBinding

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var b: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar); supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Laporan PDF"

        val html = intent.getStringExtra("html") ?: "<html><body><p>Gagal memuat laporan</p></body></html>"

        b.webView.apply {
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = false
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
            }
            loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
