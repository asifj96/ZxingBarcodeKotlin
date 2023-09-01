package com.zxing.zxingbarcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

class BarcodeScannerDialog(private val itemClickListener: ItemClickListener) : DialogFragment() {

    companion object {
        const val TAG = "BarcodeScannerDialog"
    }

    private var scannerView: CodeScannerView? = null
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        isCancelable = true
        return inflater.inflate(R.layout.barcode_scanner_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scannerView = view.findViewById(R.id.scannerView)

        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            initScanner()
        }

    }

    private fun initScanner() {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView!!)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS // all_format
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.decodeCallback = DecodeCallback { result ->
            activity.runOnUiThread {
                itemClickListener.onItemClick(result.text.toString())
            }
            if (dialog?.isShowing!!) {
                dialog?.dismiss()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity.runOnUiThread {
                Toast.makeText(
                    activity, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG
                ).show()
            }
        }
        scannerView!!.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when (isGranted) {
            true -> {
                Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT).show()
                println("Permission has been granted by user")
                initScanner()
            }

            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(action: String)
    }
}