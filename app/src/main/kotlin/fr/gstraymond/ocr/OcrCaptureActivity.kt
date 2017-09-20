package fr.gstraymond.ocr

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.text.TextRecognizer
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CardDetailActivity
import fr.gstraymond.android.CustomActivity
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ocr.ui.camera.CameraSource
import fr.gstraymond.ocr.ui.camera.CameraSourcePreview
import fr.gstraymond.ocr.ui.camera.GraphicOverlay
import fr.gstraymond.ui.adapter.DeckDetailCardViews
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.startActivity
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class OcrCaptureActivity : CustomActivity(R.layout.ocr_capture), OcrDetectorProcessor.CardDetector {

    private var mCameraSource: CameraSource? = null
    private var mPreview: CameraSourcePreview? = null
    private var mGraphicOverlay: GraphicOverlay<OcrGraphic>? = null

    companion object {
        private val RC_HANDLE_GMS = 9001
        val AUTO_FOCUS = "AutoFocus"
        val USE_FLASH = "UseFlash"
        val DECK_ID = "DeckId"

        fun getIntent(context: Context,
                      autoFocus: Boolean,
                      useFlash: Boolean,
                      deckId: String? = null): Intent =
                Intent(context, OcrCaptureActivity::class.java).apply {
                    putExtra(AUTO_FOCUS, autoFocus)
                    putExtra(USE_FLASH, useFlash)
                    putExtra(DECK_ID, deckId)
                }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPreview = find<CameraSourcePreview>(R.id.preview)
        mGraphicOverlay = find<GraphicOverlay<OcrGraphic>>(R.id.graphicOverlay)

        // read parameters from the intent used to launch the activity.
        val autoFocus = intent.getBooleanExtra(AUTO_FOCUS, false)
        val useFlash = intent.getBooleanExtra(USE_FLASH, false)

        createCameraSource(autoFocus, useFlash)

        Snackbar.make(mGraphicOverlay!!, "Place card under camera, at least card title and type",
                Snackbar.LENGTH_INDEFINITE)
                .show()
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.

     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        textRecognizer.setProcessor(OcrDetectorProcessor(mGraphicOverlay!!, this, app().searchService))

        if (!textRecognizer.isOperational) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            log.w("Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowStorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                log.w(getString(R.string.low_storage_error))
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_AUTO)
                .setFocusMode(if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else Camera.Parameters.FOCUS_MODE_AUTO)
                .build()
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (mPreview != null) {
            mPreview!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mPreview != null) {
            mPreview!!.release()
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // Check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                mPreview!!.start(mCameraSource!!, mGraphicOverlay!!)
            } catch (e: IOException) {
                log.e("Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }

        }
    }

    private val log = Log(javaClass)

    private var pause = AtomicBoolean(false)

    override fun onCardDetected(card: Card) {
        if (!pause.compareAndSet(false, true)) return

        intent.getStringExtra(DECK_ID)?.let { deckId ->
            runOnUiThread {
                createScanDialog(deckId, card)
            }
        } ?: startActivity {
            finish()
            CardDetailActivity.getIntent(this@OcrCaptureActivity, card)
        }
    }

    private fun addCardToDeck(deckId: String, card: Card) {
        val cardList = app().cardListBuilder.build(deckId.toInt())
        val newDeckLine = DeckLine(card, Date().time, 1, false)
        if (cardList.contains(newDeckLine)) {
            val deckLine = cardList.getByUid(newDeckLine.id())!!
            cardList.update(deckLine.copy(mult = deckLine.mult + 1))
        } else {
            cardList.addOrRemove(newDeckLine)
        }
    }

    private val cardViews by lazy { DeckDetailCardViews(this) }

    private fun createScanDialog(deckId: String, card: Card) {
        val view = inflate(R.layout.activity_ocr_scan)
        val continueScan = view.find<CheckBox>(R.id.ocr_scan_continue_checkbox)
        cardViews.display(view, card, 0)
        AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Add", { _, _ ->
                    addCardToDeck(deckId, card)
                    finishScan(continueScan.isChecked)
                })
                .setNegativeButton("Discard", { _, _ ->
                    finishScan(continueScan.isChecked)
                })
                .create()
                .show()
    }

    private fun finishScan(continueScan: Boolean) {
        if (!continueScan) finish()
        else pause.set(false)
    }

    override fun isPaused() = pause.get()
}
