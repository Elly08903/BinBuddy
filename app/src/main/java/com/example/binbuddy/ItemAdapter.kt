package com.example.binbuddy

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.binbuddy.data.ItemEntity

/**
 * RecyclerView Adapter for displaying a list of items in the app.
 * Handles item binding, click actions, and list updates.
 */
class ItemAdapter(
    // The list of items to display
    private var items: List<ItemEntity>,

    // Callback function that runs when an item is clicked
    private val onItemClick: (ItemEntity) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /**
     * ViewHolder class that holds the references to UI elements for each item.
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // UI references for item details
        private val titleText: TextView = view.findViewById(R.id.itemName)
        private val locationText: TextView = view.findViewById(R.id.itemLocation)
        private val costText: TextView = view.findViewById(R.id.itemCost)
        private val descriptionText: TextView = view.findViewById(R.id.itemDescription)
        private val imageView: ImageView = view.findViewById(R.id.itemImage)

        /**
         * Binds an ItemEntity's data to the ViewHolder's UI elements.
         * Sets text values, image, and click listener.
         */
        fun bind(item: ItemEntity) {
            // Set text fields
            titleText.text = item.title
            locationText.text = item.location
            costText.text = item.cost
            descriptionText.text = item.description

            // Set click listener for the whole item
            itemView.setOnClickListener { onItemClick(item) }

            // Set the image: First try the URI, then the resource id, end with fallback image.
            if (!item.imageUri.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse(item.imageUri) // Parse the stored URI
                    imageView.setImageURI(uri) // Load the image from the URI
                } catch (e: Exception) {
                    // If loading from URI fails, use a fallback image
                    imageView.setImageResource(R.drawable.ic_launcher_foreground) // fallback
                }
            } else if (item.imageId != 0) {
                // If image URI is empty but a drawable ID exists
                imageView.setImageResource(item.imageId)
            } else {
                // No image data available → show fallback image
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }

        }
    }

    /**
     * Creates a new ViewHolder when there is no existing one to reuse.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    /**
     * Binds the data for a specific position in the list to a ViewHolder.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Returns the number of items in the current list.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Replaces the current item list with a new one and refreshes the UI.
     */
    fun updateList(newItems: List<ItemEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * Returns the item at a given position in the list.
     */
    fun getItemAt(position: Int): ItemEntity = items[position]

    /**
     * Removes the item at a given position and updates the UI.
     */
    fun removeAt(position: Int) {
        val m = items.toMutableList()
        m.removeAt(position)
        items = m
        notifyItemRemoved(position)
    }
}
