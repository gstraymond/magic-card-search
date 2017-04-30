package fr.gstraymond.android

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity.RESULT_FILE_PATH
import fr.gstraymond.R
import fr.gstraymond.utils.find
import fr.gstraymond.utils.hasPerms
import fr.gstraymond.utils.requestPerms
import fr.gstraymond.utils.startActivity

class DeckImporterActivity : CustomActivity(R.layout.activity_deck_importer) {

    companion object {
        fun getIntent(context: Context) = Intent(context, DeckImporterActivity::class.java)

        private val REQUEST_STORAGE_CODE = 1000
        private val FILE_PICKER_CODE = 1001
    }

    private val button by lazy { find<Button>(R.id.deck_importer_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.import_deck)
        }

        button.setOnClickListener { _ ->
            if (!hasPerms(READ_EXTERNAL_STORAGE)) {
                requestPerms(REQUEST_STORAGE_CODE, READ_EXTERNAL_STORAGE)
            } else {
                openFilePicker()
            }
        }
    }

    private fun openFilePicker() {
        MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_CODE)
                .start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_CODE -> if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openFilePicker()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            FILE_PICKER_CODE -> when (resultCode) {
                RESULT_OK -> startActivity {
                    val path = "file://${data!!.getStringExtra(RESULT_FILE_PATH)}"
                    DeckImportProgressActivity.getIntent(this, path)
                }
            }
        }
    }
}