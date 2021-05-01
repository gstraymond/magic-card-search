package fr.gstraymond.android

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fr.gstraymond.R
import fr.gstraymond.android.adapter.WishlistAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.*
import java.io.BufferedReader
import java.io.InputStreamReader


class WishListActivity : CustomActivity(R.layout.activity_wishlist) {

    companion object {
        fun getIntent(context: Context) =
                Intent(context, WishListActivity::class.java)

        private const val DIR_PICKER_CODE = 3002
        private const val FILE_PICKER_CODE = 3003
    }

    private val emptyTextView by lazy { find<View>(R.id.wishlist_empty_text) }
    private val export by lazy { find<TextView>(R.id.toolbar_export) }
    private val import by lazy { find<TextView>(R.id.toolbar_import) }

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

        export.setOnClickListener { startDirPicker() }
        import.setOnClickListener { createImportDialog() }
    }

    private fun createImportDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.wishlist_import_title))
                .setMessage(getString(R.string.wishlist_import_message))
                .setPositiveButton(getString(R.string.wishlist_import_ok)) { _, _ -> openFilePicker() }
                .setNegativeButton(getString(R.string.wishlist_import_cancel)) { _, _ -> }
                .show()
    }

    private fun startDirPicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "wishlist.txt")
        }
        startActivityForResult(intent, DIR_PICKER_CODE)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            //type = "application/octet-stream"
            type = "text/plain"
        }

        startActivityForResult(intent, FILE_PICKER_CODE)
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

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.data?.let { path ->
            when (requestCode) {
                DIR_PICKER_CODE -> {
                    val exportPath = app().wishlistManager.export(path, contentResolver, this)
                    val message = String.format(resources.getString(R.string.deck_exported), "Wishlist", exportPath)
                    Snackbar.make(find(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                }
                FILE_PICKER_CODE -> startActivity {
                    val stream = contentResolver.openInputStream(path)
                    val r = BufferedReader(InputStreamReader(stream))
                    val total = StringBuilder()
                    var line: String?
                    while (r.readLine().also { line = it } != null) {
                        total.append(line).append('\n')
                    }
                    DeckImportProgressActivity.getIntent(this, total.toString(), null, true)
                }
            }
        }
    }
}