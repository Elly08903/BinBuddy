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

class AdminNavActivity : AppCompatActivity() {
    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    private var pickedImageUri: Uri? = null
    private var onImagePicked: ((Uri) -> Unit)? = null

    // register once
    private val pickImage = registerForActivityResult(OpenDocument()) { uri: Uri? ->
        if (uri != null) {

            // persist read permission
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }

            onImagePicked?.invoke(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        if (session.isGuest()) {
            finish()
            return
        }

        lifecycleScope.launch {
            val isAdmin = withContext(Dispatchers.IO) {
                session.getLoggedInUser()?.isAdmin == true
            }
            if (!isAdmin) {
                finish()
                return@launch
            }

            setContentView(R.layout.admin_navigation)

            recyclerView = findViewById(R.id.itemsRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@AdminNavActivity)

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

            val swipeToDelete = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                    val pos = vh.adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return
                    val item = adapter.getItemAt(pos)

                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Delete item?")
                        .setMessage("Remove “${item.title}” from inventory?")
                        .setPositiveButton("Delete") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) { db.itemDao().delete(item) }
                                adapter.removeAt(pos)
                            }
                        }
                        .setNegativeButton("Cancel") { d, _ ->
                            d.dismiss()
                            adapter.notifyItemChanged(pos)
                        }
                        .setOnCancelListener { adapter.notifyItemChanged(pos) }
                        .show()
                }
            }
            ItemTouchHelper(swipeToDelete).attachToRecyclerView(recyclerView)

            val searchView = findViewById<SearchView>(R.id.itemsSearchView)
            launch {
                val items = withContext(Dispatchers.IO) { db.itemDao().getAllItems() }
                adapter.updateList(items)
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    val raw = newText.orEmpty()
                    val pattern = "%$raw%"
                    lifecycleScope.launch {
                        val results = withContext(Dispatchers.IO) {
                            if (raw.isBlank()) db.itemDao().getAllItems()
                            else db.itemDao().searchItems(pattern)
                        }
                        adapter.updateList(results)
                    }
                    return true
                }
            })

            val addProduct = findViewById<Button>(R.id.addProduct)
            addProduct.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)

                val nameInput = dialogView.findViewById<EditText>(R.id.editItemName)
                val descInput = dialogView.findViewById<EditText>(R.id.editItemDescription)
                val priceInput = dialogView.findViewById<EditText>(R.id.editItemPrice)
                val locationInput = dialogView.findViewById<EditText>(R.id.editItemLocation)
                val spStore = dialogView.findViewById<Spinner>(R.id.spStore)
                val ivPreview = dialogView.findViewById<ImageView>(R.id.ivPreview)
                val btnPickImage = dialogView.findViewById<Button>(R.id.btnPickImage)

                pickedImageUri = null
                ivPreview.setImageDrawable(null)

                onImagePicked = { uri ->
                    pickedImageUri = uri
                    ivPreview.setImageURI(uri)
                }

                btnPickImage.setOnClickListener {
                    pickImage.launch(arrayOf("image/*"))
                }

                lifecycleScope.launch {
                    val stores = withContext(Dispatchers.IO) { db.storeDao().getAllStores() }
                    val names = stores.map { it.name }
                    spStore.adapter = ArrayAdapter(
                        this@AdminNavActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        names
                    )

                    AlertDialog.Builder(this@AdminNavActivity)
                        .setTitle("Add Item")
                        .setView(dialogView)
                        .setPositiveButton("Add") { _, _ ->
                            val selectedIdx = spStore.selectedItemPosition
                            val selectedStore =
                                stores.getOrNull(selectedIdx)?.id ?: return@setPositiveButton

                            val newItem = ItemEntity(
                                title = nameInput.text.toString(),
                                location = locationInput.text.toString(),
                                cost = "$${priceInput.text}",
                                description = descInput.text.toString(),
                                storeId = selectedStore,
                                imageId = 0,
                                imageUri = pickedImageUri?.toString()
                            )

                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) { db.itemDao().insert(newItem) }
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