package fr.gstraymond.android

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType.EDITABLE
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.adapter.DeckDetailFragmentPagerAdapter
import fr.gstraymond.models.Deck
import fr.gstraymond.utils.*
import net.rdrei.android.dirchooser.DirectoryChooserActivity
import net.rdrei.android.dirchooser.DirectoryChooserActivity.*
import net.rdrei.android.dirchooser.DirectoryChooserConfig


class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

    companion object {
        val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }

        private val REQUEST_STORAGE_CODE = 2000
        private val DIR_PICKER_CODE = 2001
    }

    private lateinit var deck: Deck

    private val deckTitle by lazy { find<TextView>(R.id.toolbar_text) }
    private val delete by lazy { find<TextView>(R.id.toolbar_delete) }
    private val export by lazy { find<TextView>(R.id.toolbar_export) }
    private val refresh by lazy { find<TextView>(R.id.toolbar_refresh) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.getStringExtra(DECK_EXTRA)
        deck = app().deckList.getByUid(deckId)!!

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        deckTitle.apply {
            text = deck.name
            setOnClickListener { createTitleDialog() }
        }

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = DeckDetailFragmentPagerAdapter(supportFragmentManager)

        find<TabLayout>(R.id.sliding_tabs).setupWithViewPager(viewPager)

        delete.setOnClickListener { createDeleteDialog() }
        export.setOnClickListener {
            if (!hasPerms(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)) {
                requestPerms(REQUEST_STORAGE_CODE, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
            } else {
                startDirPicker()
            }
        }

        refresh.setOnClickListener { createRefreshDialog() }
    }

    private fun startDirPicker() {
        val chooserIntent = Intent(this, DirectoryChooserActivity::class.java).apply {
            val config = DirectoryChooserConfig.builder()
                    .newDirectoryName("decks")
                    .allowNewDirectoryNameModification(true)
                    .build()

            putExtra(EXTRA_CONFIG, config)
        }

        startActivityForResult(chooserIntent, DIR_PICKER_CODE)
    }

    private fun createTitleDialog() {
        val view = inflate(R.layout.activity_deck_detail_title)
        val editText = view.find<EditText>(R.id.deck_detail_title)
        editText.setText(deck.name, EDITABLE)
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    updateDeckName(editText.text.toString())
                })
                .setNegativeButton(android.R.string.cancel, { _, _ -> })
                .create()
                .show()
    }

    private fun createDeleteDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.deckdetails_delete_title))
                .setPositiveButton(getString(R.string.deckdetails_delete_ok)) { _, _ ->
                    app().deckManager.delete(deck)
                    Tracker.addRemoveDeck(added = false)
                    finish()
                }
                .setNegativeButton(getString(R.string.deckdetails_delete_cancel)) { _, _ -> }
                .show()
    }

    private fun createRefreshDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.deckdetails_refresh_title))
                .setMessage(getString(R.string.deckdetails_refresh_message))
                .setPositiveButton(getString(R.string.deckdetails_refresh_ok)) { _, _ ->
                    startActivity {
                        val deckList = app().deckManager.export(deck).joinToString("\n")
                        DeckImportProgressActivity.getIntentForDeckList(this, deckList)
                    }
                    finish()
                    app().deckManager.delete(deck)
                }
                .setNegativeButton(getString(R.string.deckdetails_refresh_cancel)) { _, _ -> }
                .show()
    }

    private fun updateDeckName(deckName: String) {
        deck = deck.copy(name = deckName)
        app().deckList.update(deck)
        deckTitle.text = deckName
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_CODE -> if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startDirPicker()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            DIR_PICKER_CODE -> when (resultCode) {
                RESULT_CODE_DIR_SELECTED -> {
                    val path = data!!.getStringExtra(RESULT_SELECTED_DIR)
                    val exportPath = app().deckManager.export(deck, path)
                    val rootView = findViewById(android.R.id.content)
                    val message = String.format(resources.getString(R.string.deck_exported), deck.name, exportPath)
                    Snackbar.make(rootView, message, LENGTH_LONG).show()
                }
            }
        }
    }
}
