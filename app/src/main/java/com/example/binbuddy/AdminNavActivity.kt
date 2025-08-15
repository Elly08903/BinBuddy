package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument

/**
 * AdminNavActivity
 *
 * This activity allows admins to:
 *  - View, search, and delete inventory items
 *  - Add new items (with image and store selection)
 *
 * Customers and guest users do not have access to this screen.
 */
class AdminNavActivity : AppCompatActivity() {

    // Database instance (lazy-loaded)
    private val db by lazy { AppDatabase.getInstance(this) }

    // RecyclerView + adapter for displaying items
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    // Holds the URI of the picked image when adding a new item.
    private var pickedImageUri: Uri? = null

    // Callback function to execute after an image is picked.
    private var onImagePicked: ((Uri) -> Unit)? = null

    /**
     * Activity Result Launcher for picking an image from storage.
     * Uses the OpenDocument contract so the user can select a file from the system picker.
     *
     * After a file is chosen:
     *  - Persist a read permission so the image remains accessible across app restarts.
     *  - Invoke the onImagePicked callback to update the UI.
     */
    private val pickImage = registerForActivityResult(OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                // Take persistent read permission for the selected file.
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Permission already granted or not needed
            }

            // Notify the registered callback with the selected image URI.
            onImagePicked?.invoke(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)

        lifecycleScope.launch {

            // Block non-admin (non business owners) from viewing screen
            val isAdmin = withContext(Dispatchers.IO) {
                session.getLoggedInUser()?.isAdmin == true
            }

            // Close the activity for non-admin users.
            if (!isAdmin) {
                finish()
                return@launch
            }

            // Set the layout for this activity
            setContentView(R.layout.admin_navigation)

            // ------------------------------
            // RecyclerView Setup
            // ------------------------------
            recyclerView = findViewById(R.id.itemsRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@AdminNavActivity)

            // Adapter initialized with empty list; click handler opens item detail activity.
            adapter = ItemAdapter(emptyList()) { selected ->
                Intent(this@AdminNavActivity, ItemDetailActivity::class.java).apply {
                    putExtra("itemId", selected.id)
                    putExtra("itemTitle", selected.title)
                    putExtra("itemLocation", selected.location)
                    putExtra("itemCost", selected.cost)
                    putExtra("itemDescription", selected.description)
                    putExtra("imageId", selected.imageId)
                    putExtra("imageUri", selected.imageUri)
                }.also { startActivity(it) }
            }
            recyclerView.adapter = adapter

            // ------------------------------
            // Swipe-to-Delete Setup
            // ------------------------------
            val swipeToDelete = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                // Disable drag & drop by always returning false.
                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                    // Get swiped item position; abort if invalid.
                    val pos = vh.adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return
                    val item = adapter.getItemAt(pos)

                    // Confirm deletion via AlertDialog.
                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Delete item?")
                        .setMessage("Remove “${item.title}” from inventory?")
                        .setPositiveButton("Delete") { _, _ ->
                            lifecycleScope.launch {
                                // Delete from DB in IO thread.
                                withContext(Dispatchers.IO) { db.itemDao().delete(item) }
                                // Remove from adapter after deletion.
                                adapter.removeAt(pos)
                            }
                        }
                        .setNegativeButton("Cancel") { d, _ ->
                            // Restore item if deletion is cancelled.
                            d.dismiss()
                            adapter.notifyItemChanged(pos)
                        }
                        .setOnCancelListener {
                            // Restore item if dialog is dismissed without choice.
                            adapter.notifyItemChanged(pos) }
                        .show()
                }
            }
            // Attach swipe handler to RecyclerView.
            ItemTouchHelper(swipeToDelete).attachToRecyclerView(recyclerView)

            // ------------------------------
            // Search Functionality Setup
            // ------------------------------
            val searchView = findViewById<SearchView>(R.id.itemsSearchView)

            // Load all items from DB initially.
            launch {
                val items = withContext(Dispatchers.IO) { db.itemDao().getAllItems() }
                adapter.updateList(items)
            }

            // Filter list as user types in search box.
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    val raw = newText.orEmpty()
                    val pattern = "%$raw%"
                    lifecycleScope.launch {
                        val results = withContext(Dispatchers.IO) {
                            // Return all items if search query is blank
                            if (raw.isBlank()) db.itemDao().getAllItems()
                            // Filter items by search query
                            else db.itemDao().searchItems(pattern)
                        }
                        // Update the adapter with filtered results
                        adapter.updateList(results)
                    }
                    return true
                }
            })

            // ------------------------------
            // Add Item Button Setup
            // ------------------------------
            val addProduct = findViewById<Button>(R.id.addProduct)
            addProduct.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)

                // Form fields in the add-item dialog.
                val nameInput = dialogView.findViewById<EditText>(R.id.editItemName)
                val descInput = dialogView.findViewById<EditText>(R.id.editItemDescription)
                val priceInput = dialogView.findViewById<EditText>(R.id.editItemPrice)
                val locationInput = dialogView.findViewById<EditText>(R.id.editItemLocation)
                val spStore = dialogView.findViewById<Spinner>(R.id.spStore)
                val ivPreview = dialogView.findViewById<ImageView>(R.id.ivPreview)
                val btnPickImage = dialogView.findViewById<Button>(R.id.btnPickImage)

                // Clear previous image selection & preview.
                pickedImageUri = null
                ivPreview.setImageDrawable(null)

                // Define callback for when user picks an image.
                onImagePicked = { uri ->
                    pickedImageUri = uri
                    ivPreview.setImageURI(uri)
                }

                // Open system picker when "Pick Image" button is clicked.
                btnPickImage.setOnClickListener {
                    pickImage.launch(arrayOf("image/*"))
                }

                lifecycleScope.launch {
                    // Fetch list of stores from DB and set them in spinner.
                    val stores = withContext(Dispatchers.IO) { db.storeDao().getAllStores() }
                    val names = stores.map { it.name }
                    spStore.adapter = ArrayAdapter(
                        this@AdminNavActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        names
                    )

                    // Show dialog for adding a new item.
                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Add Item")
                        .setView(dialogView)
                        .setPositiveButton("Add") { _, _ ->
                            val selectedIdx = spStore.selectedItemPosition
                            val selectedStore =
                                stores.getOrNull(selectedIdx)?.id ?: return@setPositiveButton

                            // Create new item entity from form fields.
                            val newItem = ItemEntity(
                                title = nameInput.text.toString(),
                                location = locationInput.text.toString(),
                                cost = "$${priceInput.text}", // Prepend dollar sign
                                description = descInput.text.toString(),
                                storeId = selectedStore,
                                imageId = 0, // Not used in current flow.
                                imageUri = pickedImageUri?.toString()
                            )

                            lifecycleScope.launch {
                                // Insert new item into DB.
                                withContext(Dispatchers.IO) { db.itemDao().insert(newItem) }

                                // Reload items & scroll to the newly added one.
                                val updated =
                                    withContext(Dispatchers.IO) { db.itemDao().getAllItems() }
                                adapter.updateList(updated)
                                recyclerView.scrollToPosition(updated.size - 1)
                            }
                        }
                        .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                        .show()
                }
            }
        }
    }
}