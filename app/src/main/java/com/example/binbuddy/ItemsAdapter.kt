package com.example.binbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.binbuddy.data.ItemEntity

class ItemAdapter(
    private var items: List<ItemEntity>,
    private val onItemClick: (ItemEntity) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.itemName)
        private val locationText: TextView = view.findViewById(R.id.itemLocation)  // add this to your layout
        private val costText: TextView = view.findViewById(R.id.itemCost)          // and this
        private val descriptionText: TextView = view.findViewById(R.id.itemDescription)

        fun bind(item: ItemEntity) {
            titleText.text = item.title
            locationText.text = item.location
            costText.text = item.cost
            descriptionText.text = item.description
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ItemEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
