package fr.gstraymond.android

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.*
import net.rdrei.android.dirchooser.DirectoryChooserActivity
import net.rdrei.android.dirchooser.DirectoryChooserConfig

class WishListActivity : CustomActivity(R.layout.activity_wishlist) {

    companion object {
        fun getIntent(context: Context) =
                Intent(context, WishListActivity::class.java)

        private const val REQUEST_STORAGE_EXPORT_CODE = 3000
        private const val REQUEST_STORAGE_IMPORT_CODE = 3001
        private const val DIR_PICKER_CODE = 3002
        private const val FILE_PICKER_CODE = 3003
    }

    private val emptyTextView by lazy { find<View>(R.id.wishlist_empty_text) }
    private val export by lazy { find<TextView>(R.id.toolbar_export) }
    private val import by lazy { find<TextView>(R.id.toolbar_import) }

    private val perms = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    private val clickCallbacks = object : WishlistAdapter.ClickCallbacks {
        override fun onEmptyList() = emptyTextView.visible()

        override fun cardClicked(card: Card) = startActivity {
            CardDetailActivity.getIntent(this@WishListActivity, card)
        }
    }

    private val wishlistAdapter by lazy { WishlistAdapter(this, app().wishList, clickCallbacks) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.wishlist_title)

        find<RecyclerView>(R.id.wishlist_recyclerview).let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = wishlistAdapter
        }

        export.setOnClickListener {
            if (!hasPerms(*perms)) {
                requestPerms(REQUEST_STORAGE_EXPORT_CODE, *perms)
            } else {
                startDirPicker()
            }
        }

        import.setOnClickListener { createImportDialog() }
    }

    private fun createImportDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.wishlist_import_title))
                .setMessage(getString(R.string.wishlist_import_message))
                .setPositiveButton(getString(R.string.wishlist_import_ok)) { _, _ ->
                    if (!hasPerms(*perms)) {
                        requestPerms(REQUEST_STORAGE_IMPORT_CODE, *perms)
                    } else {
                        openFilePicker()
                    }
                }
                .setNegativeButton(getString(R.string.wishlist_import_cancel)) { _, _ -> }
                .show()
    }

    private fun startDirPicker() {
        val chooserIntent = Intent(this, DirectoryChooserActivity::class.java).apply {
            val config = DirectoryChooserConfig.builder()
                    .newDirectoryName("wishlist")
                    .allowNewDirectoryNameModification(true)
                    .build()

            putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config)
        }

        startActivityForResult(chooserIntent, DIR_PICKER_CODE)
    }

    private fun openFilePicker() {
        MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_CODE)
                .start()
    }

    override fun onResume() {
        super.onResume()
        wishlistAdapter.notifyDataSetChanged()
        if (app().wishList.isEmpty()) {
            emptyTextView.visible()
        } else {
            emptyTextView.gone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            when (requestCode) {
                REQUEST_STORAGE_EXPORT_CODE -> startDirPicker()
                REQUEST_STORAGE_IMPORT_CODE -> openFilePicker()
            }
        }
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            DIR_PICKER_CODE -> when (resultCode) {
                DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED -> {
                    val path = data!!.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR)
                    val exportPath = app().wishlistManager.export(path)
                    val message = String.format(resources.getString(R.string.deck_exported), "Wishlist", exportPath)
                    Snackbar.make(find(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                }
            }
            FILE_PICKER_CODE -> when (resultCode) {
                RESULT_OK -> startActivity {
                    val path = "file://${data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)}"
                    DeckImportProgressActivity.getIntent(this, path, true)
                }
            }
        }
    }
}