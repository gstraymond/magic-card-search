package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import fr.gstraymond.R
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity

class DeckImporterActivity : CustomActivity(R.layout.activity_deck_importer) {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckImporterActivity::class.java)

        private const val FILE_PICKER_CODE = 1001
    }

    private val button by lazy { find<Button>(R.id.deck_importer_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(find(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.import_deck)
        }

        button.setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            //type = "application/octet-stream"
            type = "text/plain"
        }

        startActivityForResult(intent, FILE_PICKER_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FILE_PICKER_CODE ->
                data?.data?.let { path ->
                    startActivity {
                        DeckImportProgressActivity.getIntent(this, path)
                    }
                }
        }
    }
}