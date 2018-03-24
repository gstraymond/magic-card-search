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
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.BufferType.EDITABLE
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.android.adapter.DeckDetailFragmentPagerAdapter
import fr.gstraymond.biz.Formats
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckCard
import fr.gstraymond.utils.*
import net.rdrei.android.dirchooser.DirectoryChooserActivity
import net.rdrei.android.dirchooser.DirectoryChooserActivity.*
import net.rdrei.android.dirchooser.DirectoryChooserConfig


class DeckDetailActivity : CustomActivity(R.layout.activity_deck_detail) {

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
    private lateinit var deck: Deck
    private lateinit var pagerAdapter: DeckDetailFragmentPagerAdapter

    private val deckTitle by lazy { find<TextView>(R.id.toolbar_text) }
    private val delete by lazy { find<TextView>(R.id.toolbar_delete) }
    private val export by lazy { find<TextView>(R.id.toolbar_export) }
    private val refresh by lazy { find<TextView>(R.id.toolbar_refresh) }
    private val tabLayout by lazy { find<TabLayout>(R.id.sliding_tabs) }
    private val formatChooser by lazy { find<Spinner>(R.id.format_chooser) }

    private val perms = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null) // avoid fragment pager adapter to restore old fragment

        deckId = intent.getStringExtra(DECK_EXTRA)
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
        pagerAdapter = DeckDetailFragmentPagerAdapter(supportFragmentManager, this).apply {
            deckCardCallback = this@DeckDetailActivity.deckCardCallback
        }
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)

        delete.setOnClickListener { createDeleteDialog() }
        export.setOnClickListener {
            if (!hasPerms(*perms)) {
                requestPerms(REQUEST_STORAGE_CODE, *perms)
            } else {
                startDirPicker()
            }
        }

        refresh.setOnClickListener { createRefreshDialog() }
        setTabsText()

        formatChooser.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(getString(R.string.select_format)) + Formats.ordered
        )

        deck.maybeFormat?.apply {
            formatChooser.setSelection(Formats.ordered.indexOf(this) + 1, false)
        }
        
        formatChooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) = Unit

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val maybeFormat = when(position) {
                    0 -> null
                    else -> Formats.ordered[position - 1]
                }
                deck = deck.copy(maybeFormat = maybeFormat)
                app().deckList.update(deck)
                pagerAdapter.formatCallback.formatChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setTabsText()
    }

    private val deckCardCallback = object : DeckCardCallback {
        override fun multChanged(from: DeckCardCallback.FROM, position: Int) = setTabsText()

        override fun cardClick(deckCard: DeckCard) {}
    }

    private fun setTabsText() {
        app().deckList.getByUid(deckId)?.apply {
            tabLayout.getTabAt(0)?.text = String.format(getString(R.string.deck_tab_cards), deckSize)
            tabLayout.getTabAt(1)?.text = String.format(getString(R.string.deck_tab_sideboard), sideboardSize)
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
            setText(deck.name, EDITABLE)
            post {
                setSelection(deck.name.length)
                requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
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
                        DeckImportProgressActivity.getIntentForDeckList(this, deckList, deck.maybeFormat)
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

    interface FormatCallback {
        fun formatChanged()
    }
}
