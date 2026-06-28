package com.inventaris.app.ui.inventaris

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inventaris.app.databinding.ItemInventarisBinding
import com.inventaris.app.model.Inventaris

class InventarisAdapter(
    private var items: List<Inventaris>,
    private val onClick: (Inventaris) -> Unit,
    private val onDelete: (Inventaris) -> Unit
) : RecyclerView.Adapter<InventarisAdapter.VH>() {
    fun update(d: List<Inventaris>) { items = d; notifyDataSetChanged() }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemInventarisBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, i: Int) = h.bind(items[i])
    override fun getItemCount() = items.size
    inner class VH(private val b: ItemInventarisBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Inventaris) {
            b.tvKode.text = item.kodeBarang; b.tvNama.text = item.namaBarang
            b.tvKategori.text = item.kategori; b.tvLokasi.text = item.lokasi
            b.tvKondisi.text = item.kondisi; b.tvJumlah.text = "Jml: ${item.jumlah}"
            b.tvTahunBulan.text = "${item.tahun} ${item.bulan}"
            if (!item.qrCode.isNullOrEmpty()) Glide.with(b.root.context).load(item.qrCode).into(b.ivQr)
            b.root.setOnClickListener { onClick(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
