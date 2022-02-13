package com.tinku.qrscanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val requestCodeCameraPermissions = 1
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            askCameraPermission()
        } else {
            setupControls()
        }
    }
    private fun setupControls() {
        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector)
            .setAutoFocusEnabled(true)
            .build()
        cameraSurface.holder.addCallback(surgaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(Manifest.permission.CAMERA),
            requestCodeCameraPermissions
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermissions && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(this@MainActivity, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val surgaceCallBack = object : SurfaceHolder.Callback {
        override fun surfaceCreated(p0: SurfaceHolder) {
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            cameraSource.stop()
        }

        @SuppressLint("MissingPermission")
        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            try {
                cameraSource.start(surfaceHolder)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity,
                    "something e=went wrong - ${e.printStackTrace()}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (detections.detectedItems.isNotEmpty()) {
                val qrCode: SparseArray<Barcode> = detections.detectedItems
                val code = qrCode.valueAt(0)
                scan_ressult.text = code.displayValue
            } else {
                scan_ressult.text = ""
            }
        }
    }
}