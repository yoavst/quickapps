package com.yoavst.quickapps.barcode

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import android.content.Intent
import android.os.Bundle
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.lge.qcircle.template.TemplateTag
import com.google.zxing.Result
import com.lge.qcircle.template.QCircleDialog
import com.yoavst.quickapps.R
import com.mobsandgeeks.ake.clipboardManager
import android.content.ClipData
import com.yoavst.util.qCircleToast
import com.mobsandgeeks.ake.getIntent
import com.google.gson.Gson
import android.view.ViewGroup


public class CBarcodeActivity : QCircleActivity(), ZXingScannerView.ResultHandler {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val scannerView: ZXingScannerView  by Delegates.lazy { ZXingScannerView(this) }
    var results: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.setBackButton()
        template.getLayoutById(TemplateTag.CONTENT_MAIN) addView(scannerView)
        setContentView(template.getView())
    }

    override fun onResume() {
        super<QCircleActivity>.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super<QCircleActivity>.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        QCircleDialog.Builder()
                .setMode(QCircleDialog.DialogMode.YesNo)
                .setTitle(getString(R.string.scanned_successfully))
                .setText(ResultBarcodeActivity.display(rawResult))
                .setPositiveButtonText(getString(android.R.string.ok))
                .setNegativeButtonText(getString(android.R.string.copy))
                .setNegativeButtonListener { v ->
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", rawResult.getText() ?: ""))
                    qCircleToast(R.string.copied)
                    restart()
                }
                .setPositiveButtonListener { v -> restart() }
                .create()
                .show(this, template)
        results = rawResult
    }

    override fun getIntentToShow(): Intent? {
        if (results == null) return null
        else {
            val bundle = Bundle()
            bundle.putString(Intent.EXTRA_SUBJECT, Gson().toJson(results))
            return getIntent<ResultBarcodeActivity>(bundle)
        }
    }

    fun restart() {
        results = null
        scannerView.startCamera()
    }
}