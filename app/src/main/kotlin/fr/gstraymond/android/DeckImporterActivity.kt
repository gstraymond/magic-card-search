package fr.gstraymond.android

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.storage.StorageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import fr.gstraymond.R
import fr.gstraymond.impex.DeckImporterTask


class DeckImporterActivity : CustomActivity(R.layout.activity_deck_importer) {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckImporterActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Deck importer" // FIXME getString(R.string.wishlist_title)
        }

        val form = findViewById(R.id.deck_importer_form)
        val process = findViewById(R.id.deck_importer_process)

        val editText = findViewById(R.id.deck_importer_url) as EditText
        val button = findViewById(R.id.deck_importer_button) as Button
        val log = findViewById(R.id.deck_importer_log) as TextView
        val progressBar = findViewById(R.id.deck_importer_progress_bar) as ProgressBar

        if (intent.action == Intent.ACTION_SEND) {
            editText.setText(intent.getStringExtra(Intent.EXTRA_TEXT), TextView.BufferType.EDITABLE)
        }

        if (intent.action == Intent.ACTION_VIEW) {
            editText.setText(intent.dataString, TextView.BufferType.EDITABLE)
        }

        button.setOnClickListener { _ ->
            /*form.hide()
            process.show()
            val url = URL(editText.text.toString())
            log.text = "Importing $url"
            DeckImporterTask(
                    contentResolver,
                    app().deckResolver,
                    app().cardListBuilder,
                    app().deckList,
                    Process(log, progressBar, this)
            ).execute(url)
            */
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 2)
            }

            val intent = Intent(this, FilePickerActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }
}

class Process(val textView: TextView,
              val progressBar: ProgressBar,
              val activity: Activity) : DeckImporterTask.ImporterProcess {

    private val log = Log(this)

    var nbCards = -1
    var progress = 0

    override fun readUrl(nbCards: Int, result: Boolean) {
        textView.text = "READ URL ${if (result) "OK" else "KO"}"
        progressBar.isIndeterminate = false
        this.nbCards = nbCards
    }

    override fun cardImported(card: String, result: Boolean) {
        textView.text = "card $card ${if (result) "imported" else "not imported"}"
        if (!result) log.w("Not imported: $card")
        progressBar.progress = 100 * ++progress / nbCards
    }

    override fun finished() {
        textView.text = "finished"
        activity.finish()
    }
}
