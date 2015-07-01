package com.yoavst.quickapps.barcode

import android.content.ClipData
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.QCircleDialog
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.barcode.tools.QRCodeReaderView
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.qCircleToast
import kotlin.properties.Delegates

/**
 * Created by yoavst.
 */
public class CBarcodeActivity : QCircleActivity(), QRCodeReaderView.OnQRCodeReadListener {
    var results: Result? = null
    val scannerView by Delegates.lazy { QRCodeReaderView(this) }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this, ButtonTheme.DARK, true))
        (getMainLayout().getParent().getParent() as RelativeLayout).addView(scannerView, 0)
        scannerView.setOnQRCodeReadListener(this)
        setContentView(template.getView())
    }

    override fun onQRCodeRead(result: Result) {
        scannerView.hide()
        scannerView.getCameraManager().stopPreview()
        QCircleDialog.Builder()
                .setMode(QCircleDialog.DialogMode.YesNo)
                .setTitle(getString(R.string.scanned_successfully))
                .setText(ResultBarcodeActivity.display(result, this))
                .setPositiveButtonText(getString(android.R.string.ok))
                .setNegativeButtonText(getString(android.R.string.copy))
                .setNegativeButtonListener { v ->
                    clipboardManager().setPrimaryClip(ClipData.newPlainText("Scanned Barcode", result.getText() ?: ""))
                    qCircleToast(R.string.copied)
                    restart()
                }
                .setPositiveButtonListener { v -> restart() }
                .create()
                .show(this, template)
        scannerView.getCameraManager().stopPreview()
        results = result
    }

    override fun onResume() {
        super<QCircleActivity>.onResume()
        scannerView.getCameraManager().startPreview()
    }

    override fun onPause() {
        super<QCircleActivity>.onPause()
        scannerView.getCameraManager().stopPreview()
    }

    override fun onDestroy() {
        super<QCircleActivity>.onDestroy()
        System.gc()
    }

    fun restart() {
        results = null
        scannerView.setOnQRCodeReadListener(this)
        scannerView.show()
        scannerView.getCameraManager().startPreview()

    }

    override fun cameraNotFound() {
        QCircleDialog.Builder()
                .setMode(QCircleDialog.DialogMode.Error)
                .setTitle(getString(R.string.error))
                .setText(getString(R.string.camera_not_found))
                .create()
                .show(this, template)
    }

    override fun QRCodeNotFoundOnCamImage() {
    }

    override fun getIntentToShow(): Intent? {
        if (results == null) return null
        else {
            val bundle = Bundle()
            bundle.putString(Intent.EXTRA_SUBJECT, Gson().toJson(results))
            return intent<ResultBarcodeActivity>(bundle)
        }
    }

}