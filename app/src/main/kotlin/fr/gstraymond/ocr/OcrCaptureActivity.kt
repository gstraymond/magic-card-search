package fr.gstraymond.ocr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CardDetailActivity
import fr.gstraymond.android.CustomActivity
import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.*
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.SimpleCardViews
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate
import fr.gstraymond.utils.startActivity
import java.util.concurrent.atomic.AtomicBoolean

class OcrCaptureActivity : CustomActivity(R.layout.ocr_capture), OcrDetectorProcessor.CardDetector {
    private val log = Log(javaClass)

    private lateinit var recognizer: TextRecognizer

    private val previewView by lazy { find<PreviewView>(R.id.viewFinder) }

    private val pause = AtomicBoolean(false)

    companion object {
        private const val RC_HANDLE_GMS = 9001
        private val REQUIRED_PERMISSIONS =
            mutableListOf (Manifest.permission.CAMERA).toTypedArray()

        const val DECK_ID = "DeckId"
        const val BOARD = "Board"

        fun getIntent(context: Context,
                      deckId: String? = null,
                      board: Board = Board.DECK
        ): Intent =
            Intent(context, OcrCaptureActivity::class.java).apply {
                putExtra(DECK_ID, deckId)
                putExtra(BOARD, board.ordinal)
            }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, RC_HANDLE_GMS
            )
        }

        Snackbar.make(previewView, getString(R.string.ocr_hint),
            Snackbar.LENGTH_INDEFINITE)
            .show()
    }

    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this)){ analyze(it) }
        cameraController.bindToLifecycle(this)
        previewView.controller = cameraController
    }

    private fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .continueWithTask(app().cameraExecutor) { visionText ->
                    val completer = TaskCompletionSource<Unit>()
                    OcrDetectorProcessor(
                        previewView,
                        this,
                        app().searchService
                    ).receiveDetections(visionText.result)
                    completer.setResult(Unit)
                    completer.task
                }
                .addOnSuccessListener {
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    log.e("text recognition failed $e", e)
                    imageProxy.close()
                }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_HANDLE_GMS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onTitleDetected(title: String) {
        /*runOnUiThread {
            Snackbar.make(previewView, title,
                Snackbar.LENGTH_SHORT)
                .show()
        }*/
    }

    override fun onCardDetected(card: Card) {
        if (!pause.compareAndSet(false, true)) return

        intent.getStringExtra(DECK_ID)?.let { deckId ->
            runOnUiThread {
                createScanDialog(deckId, card, values()[intent.getIntExtra(BOARD, 0)])
            }
        } ?: run {
            startActivity {
                CardDetailActivity.getIntent(this@OcrCaptureActivity, card)
            }
            finish()
        }
    }

    private fun addCardToDeck(deckId: String,
                              card: Card,
                              board: Board) {
        val cardList = app().cardListBuilder.build(deckId.toInt())
        val newDeckLine = DeckCard(
            card = card,
            counts = when (board) {
                DECK -> DeckCard.Counts(deck = 1, sideboard = 0, maybe = 0)
                SB -> DeckCard.Counts(deck = 0, sideboard = 1, maybe = 0)
                MAYBE -> DeckCard.Counts(deck = 0, sideboard = 0, maybe = 1)
            }
        )
        if (cardList.contains(newDeckLine)) {
            val deckLine = cardList.getByUid(newDeckLine.id())!!
            cardList.update(
                when (board) {
                    DECK -> deckLine.setDeckCount(deckLine.counts.deck + 1)
                    SB -> deckLine.setSBCount(deckLine.counts.sideboard + 1)
                    MAYBE -> deckLine.setMaybeCount(deckLine.counts.maybe + 1)
                }
            )
        } else {
            cardList.addOrRemove(newDeckLine)
        }
    }

    private val cardViews = SimpleCardViews()

    private fun createScanDialog(deckId: String,
                                 card: Card,
                                 board: Board) {
        val view = inflate(R.layout.activity_ocr_scan)
        val continueScan = view.find<CheckBox>(R.id.ocr_scan_continue_checkbox)
        cardViews.display(view, card, 0)
        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                addCardToDeck(deckId, card, board)
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