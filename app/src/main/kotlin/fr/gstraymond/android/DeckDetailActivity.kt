package fr.gstraymond.android

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.BufferType.EDITABLE
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar.make
import com.google.android.material.tabs.TabLayout
import fr.gstraymond.R
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.android.adapter.DeckDetailFragmentPagerAdapter
import fr.gstraymond.biz.ExportFormat.MAGIC_WORKSTATION
import fr.gstraymond.biz.ExportFormat.MTG_ARENA
import fr.gstraymond.biz.Formats
import fr.gstraymond.models.Board
import fr.gstraymond.models.DeckCard
import fr.gstraymond.utils.*
import net.rdrei.android.dirchooser.DirectoryChooserActivity
import net.rdrei.android.dirchooser.DirectoryChooserActivity.*
import net.rdrei.android.dirchooser.DirectoryChooserConfig


class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail), DeckCardCallback {

    companion object {
        const val DECK_EXTRA = "deck"

        fun getIntent(context: Context, deckId: String) =
                Intent(context, DeckDetailActivity::class.java).apply {
                    putExtra(DECK_EXTRA, deckId)
                }

        private const val REQUEST_STORAGE_CODE = 2000
        private const val DIR_PICKER_CODE = 2001
    }

    private lateinit var deckId: String
    private lateinit var pagerAdapter: DeckDetailFragmentPagerAdapter

    private val deckTitle by lazy { find<TextView>(R.id.toolbar_text) }
    private val delete by lazy { find<TextView>(R.id.toolbar_delete) }
    private val export by lazy { find<TextView>(R.id.toolbar_export) }
    private val refresh by lazy { find<TextView>(R.id.toolbar_refresh) }
    private val tabLayout by lazy { find<TabLayout>(R.id.sliding_tabs) }
    private val formatChooser by lazy { find<Spinner>(R.id.format_chooser) }
    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    private val perms = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    private fun deck() = app().deckList.getByUid(deckId)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deckId = intent.getStringExtra(DECK_EXTRA)!!

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        deckTitle.apply {
            text = deck().name
            setOnClickListener { createTitleDialog() }
        }

        val viewPager = find<ViewPager>(R.id.viewpager)
        pagerAdapter = DeckDetailFragmentPagerAdapter(supportFragmentManager, this)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = pagerAdapter.count

        tabLayout.setupWithViewPager(viewPager)

        delete.setOnClickListener { createDeleteDialog() }
        val exportOptions = arrayOf(
                getString(R.string.deck_detail_export_deck_file),
                getString(R.string.deck_detail_export_deck_clipboard)
        )
        export.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.deck_detail_export_deck))
                    .setItems(exportOptions) { _, which ->
                        when (which) {
                            0 -> {
                                if (!hasPerms(*perms)) {
                                    requestPerms(REQUEST_STORAGE_CODE, *perms)
                                } else {
                                    startDirPicker()
                                }
                            }
                            1 -> {
                                val deck = deck()
                                val exportLines = app().deckManager.export(deck, MTG_ARENA)
                                val deckLines = app().cardListBuilder.build(deck.id).filter { it.counts.deck > 0 }.size
                                val message = if (exportLines.size < deckLines) {
                                    getString(R.string.deck_detail_export_deck_clipboard_failed)
                                } else {
                                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Deck", exportLines.joinToString("\n")))
                                    getString(R.string.deck_detail_export_deck_clipboard_completed)
                                }
                                make(find(android.R.id.content), String.format(message, deck.name), LENGTH_LONG).show()
                            }
                        }
                    }
            builder.create().show()

        }

        refresh.setOnClickListener { createRefreshDialog() }
        setTabsText()

        formatChooser.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(getString(R.string.select_format)) + Formats.ordered
        )

        deck().maybeFormat?.apply {
            formatChooser.setSelection(Formats.ordered.indexOf(this) + 1, false)
        }

        formatChooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) = Unit

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val maybeFormat = when (position) {
                    0 -> null
                    else -> Formats.ordered[position - 1]
                }
                val deck = deck().copy(maybeFormat = maybeFormat)
                app().deckList.update(deck)
                pagerAdapter.formatChanged()
                setTabsText()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setTabsText()
    }

    override fun multChanged(from: Board, position: Int) {
        pagerAdapter.onMultChanged(from, position)
        setTabsText()
    }

    override fun cardClick(deckCard: DeckCard) {}

    private fun setTabsText() {
        app().deckList.getByUid(deckId)?.apply {
            val sideboardOrCommander =
                    if (isCommander()) getString(R.string.deck_tab_commander)
                    else getString(R.string.deck_tab_sideboard)
            tabLayout.getTabAt(0)?.text = String.format(getString(R.string.deck_tab_cards), deckSize)
            tabLayout.getTabAt(1)?.text = String.format(sideboardOrCommander, sideboardSize)
            tabLayout.getTabAt(2)?.text = String.format(getString(R.string.deck_tab_maybeboard), maybeboardSize)
        }
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
        val editText = view.find<EditText>(R.id.deck_detail_title).apply {
            val deckName = deck().name
            setText(deckName, EDITABLE)
            post {
                setSelection(deckName.length)
                requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    updateDeckName(editText.text.toString())
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .create()
                .show()
    }

    private fun createDeleteDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.deckdetails_delete_title))
                .setPositiveButton(getString(R.string.deckdetails_delete_ok)) { _, _ ->
                    app().deckManager.delete(deck())
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
                    val deck = deck()
                    startActivity {
                        val deckList = app().deckManager.export(deck, MAGIC_WORKSTATION).joinToString("\n")
                        DeckImportProgressActivity.getIntentForDeckList(this, deckList, deck.maybeFormat)
                    }
                    finish()
                    app().deckManager.delete(deck)
                }
                .setNegativeButton(getString(R.string.deckdetails_refresh_cancel)) { _, _ -> }
                .show()
    }

    private fun updateDeckName(deckName: String) {
        app().deckList.update(deck().copy(name = deckName))
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
                    val path = data!!.getStringExtra(RESULT_SELECTED_DIR)!!
                    val deck = deck()
                    val exportPath = app().deckManager.export(deck, path)
                    val rootView = find<View>(android.R.id.content)
                    val message = String.format(resources.getString(R.string.deck_exported), deck.name, exportPath)
                    make(rootView, message, LENGTH_LONG).show()
                }
            }
        }
    }

    interface FormatCallback {
        fun formatChanged()
    }
}
