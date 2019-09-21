package fr.gstraymond.ocr

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.text.TextRecognizer
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CardDetailActivity
import fr.gstraymond.android.CustomActivity
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ocr.ui.camera.CameraSource
import fr.gstraymond.ocr.ui.camera.CameraSourcePreview
import fr.gstraymond.ocr.ui.camera.GraphicOverlay
import fr.gstraymond.ui.adapter.SimpleCardViews
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.startActivity
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class OcrCaptureActivity : CustomActivity(R.layout.ocr_capture), OcrDetectorProcessor.CardDetector {

    private var cameraSource: CameraSource? = null
    private val preview by lazy { find<CameraSourcePreview>(R.id.preview) }
    private val graphicOverlay by lazy { find<GraphicOverlay<OcrGraphic>>(R.id.graphicOverlay) }

    private val log = Log(javaClass)

    private val pause = AtomicBoolean(false)

    companion object {
        private const val RC_HANDLE_GMS = 9001
        const val AUTO_FOCUS = "AutoFocus"
        const val USE_FLASH = "UseFlash"
        const val DECK_ID = "DeckId"
        const val ADD_TO_SIDEBOARD = "AddToSideboard"

        fun getIntent(context: Context,
                      autoFocus: Boolean,
                      useFlash: Boolean,
                      deckId: String? = null,
                      addToSideboard: Boolean = false): Intent =
                Intent(context, OcrCaptureActivity::class.java).apply {
                    putExtra(AUTO_FOCUS, autoFocus)
                    putExtra(USE_FLASH, useFlash)
                    putExtra(DECK_ID, deckId)
                    putExtra(ADD_TO_SIDEBOARD, addToSideboard)
                }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val autoFocus = intent.getBooleanExtra(AUTO_FOCUS, false)
        val useFlash = intent.getBooleanExtra(USE_FLASH, false)

        createCameraSource(autoFocus, useFlash)

        Snackbar.make(graphicOverlay, getString(R.string.ocr_hint),
                Snackbar.LENGTH_INDEFINITE)
                .show()
    }

    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        val textRecognizer = TextRecognizer.Builder(this).build()
        textRecognizer.setProcessor(OcrDetectorProcessor(graphicOverlay, this, app().searchService))

        if (!textRecognizer.isOperational) {
            log.w("Detector dependencies are not yet available.")

            val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowStorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                log.w(getString(R.string.low_storage_error))
            }
        }

        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_AUTO)
                .setFocusMode(if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else Camera.Parameters.FOCUS_MODE_AUTO)
                .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        preview.release()
    }

    override fun onRestart() {
        super.onRestart()
        // ugly hack to be sure everything is correctly when user is coming back
        this@OcrCaptureActivity.recreate()
    }

    private fun startCameraSource() {
        // Check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show()
        }

        cameraSource?.apply {
            try {
                preview.start(this, graphicOverlay)
            } catch (e: IOException) {
                log.e("Unable to start camera source.", e)
                release()
                cameraSource = null
            }
        }
    }

    override fun onCardDetected(card: Card) {
        if (!pause.compareAndSet(false, true)) return

        intent.getStringExtra(DECK_ID)?.let { deckId ->
            runOnUiThread {
                createScanDialog(deckId, card, intent.getBooleanExtra(ADD_TO_SIDEBOARD, false))
            }
        } ?: startActivity {
            CardDetailActivity.getIntent(this@OcrCaptureActivity, card)
        }
    }

    private fun addCardToDeck(deckId: String,
                              card: Card,
                              addToSideboard: Boolean) {
        val cardList = app().cardListBuilder.build(deckId.toInt())
        val newDeckLine = DeckCard(
                card = card,
                counts = if (addToSideboard) DeckCard.Counts(deck = 0, sideboard = 1)
                else DeckCard.Counts(deck = 1, sideboard = 0)
        )
        if (cardList.contains(newDeckLine)) {
            val deckLine = cardList.getByUid(newDeckLine.id())!!
            cardList.update(
                    if (addToSideboard) deckLine.setSBCount(deckLine.counts.sideboard + 1)
                    else deckLine.setDeckCount(deckLine.counts.deck + 1)
            )
        } else {
            cardList.addOrRemove(newDeckLine)
        }
    }

    private val cardViews = SimpleCardViews()

    private fun createScanDialog(deckId: String,
                                 card: Card,
                                 addToSideboard: Boolean) {
        val view = inflate(R.layout.activity_ocr_scan)
        val continueScan = view.find<CheckBox>(R.id.ocr_scan_continue_checkbox)
        cardViews.display(view, card, 0)
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Add") { _, _ ->
                    addCardToDeck(deckId, card, addToSideboard)
                    finishScan(continueScan.isChecked)
                }
                .setNegativeButton("Discard") { _, _ ->
                    finishScan(continueScan.isChecked)
                }
                .create()
                .show()
    }

    private fun finishScan(continueScan: Boolean) {
        if (!continueScan) finish()
        else pause.set(false)
    }

    override fun isPaused() = pause.get()
}
