package fr.gstraymond.android

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import fr.gstraymond.R
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity


class DeckPasteActivity : CustomActivity(R.layout.activity_deck_paste) {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckPasteActivity::class.java)
    }

    private val editText by lazy { find<EditText>(R.id.paste_edit_text) }
    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.decks_title)

        find<Button>(R.id.paste_import).setOnClickListener {
            val data = editText.text.toString()
            if (data.isNotBlank()) {
                startActivity {
                    DeckImportProgressActivity.getIntentForDeckList(this, data, null)
                }
                finish()
            }
        }

        find<Button>(R.id.paste_clear).setOnClickListener {
            editText.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()

        clipboardManager.primaryClip?.apply {
            (1..this.itemCount).map {
                getItemAt(it - 1).coerceToText(this@DeckPasteActivity)
            }.find {
                it.isNotBlank()
            }?.apply {
                editText.setText(this)
            }
        }
    }
}