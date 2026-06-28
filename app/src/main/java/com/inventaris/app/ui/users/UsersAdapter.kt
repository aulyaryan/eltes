package com.inventaris.app.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inventaris.app.databinding.ItemUserBinding
import com.inventaris.app.model.User

class UsersAdapter(
    private var items: List<User>,
    private val onClick: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.VH>() {
    fun update(d: List<User>) { items = d; notifyDataSetChanged() }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(ItemUserBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, i: Int) = h.bind(items[i])
    override fun getItemCount() = items.size
    inner class VH(private val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: User) {
            b.tvUsername.text = item.username; b.tvNama.text = item.nama; b.tvLevel.text = item.role
            b.root.setOnClickListener { onClick(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
