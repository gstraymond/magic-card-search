package fr.gstraymond.android

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.nbsp.materialfilepicker.ui.FilePickerActivity.RESULT_FILE_PATH
import fr.gstraymond.R
import fr.gstraymond.impex.DeckImporterTask
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.show
import java.net.URL


class DeckImporterActivity : CustomActivity(R.layout.activity_deck_importer) {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckImporterActivity::class.java)

        private val REQUEST_STORAGE_CODE = 1000
        private val FILE_PICKER_CODE = 1001
    }

    private val form by lazy { find<View>(R.id.deck_importer_form) }
    private val process by lazy { find<View>(R.id.deck_importer_process) }
    private val button by lazy { find<Button>(R.id.deck_importer_button) }
    private val logView by lazy { find<TextView>(R.id.deck_importer_log) }
    private val progressBar by lazy { find<ProgressBar>(R.id.deck_importer_progress_bar) }

    private val log = Log(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Deck importer" // FIXME getString(R.string.wishlist_title)
        }

        button.setOnClickListener { _ ->
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), REQUEST_STORAGE_CODE)
            } else {
                openFilePicker()
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(this, FilePickerActivity::class.java)
        startActivityForResult(intent, FILE_PICKER_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_CODE -> if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openFilePicker()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FILE_PICKER_CODE -> if (resultCode == Activity.RESULT_OK) {
                form.hide()
                process.show()
                val url = "file://${data.getStringExtra(RESULT_FILE_PATH)}"
                log.d("onActivityResult: $url")
                logView.text = "Importing $url"
                DeckImporterTask(
                        contentResolver,
                        app().deckResolver,
                        app().cardListBuilder,
                        app().deckList,
                        Process(logView, progressBar, this)
                ).execute(URL(url))
            }
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
