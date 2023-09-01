package com.zxing.zxingbarcode

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScannerActivity : AppCompatActivity(), BarcodeScannerDialog.ItemClickListener {


    private lateinit var btnScanBarcode: Button
    private lateinit var tvScanResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)
        tvScanResult = findViewById(R.id.tvScanResult)
        btnScanBarcode.setOnClickListener {
            tvScanResult.text = buildString {
                append("")
            }
            showBarcodeScannerDialog()
        }
    }


    private fun showBarcodeScannerDialog() {
        val qrScannerDialog = BarcodeScannerDialog(this)
        qrScannerDialog.isCancelable = true
        qrScannerDialog.show(
            supportFragmentManager, BarcodeScannerDialog.TAG
        )
    }

    override fun onItemClick(data: String) {
        tvScanResult.text = buildString {
            append("Scan Result : ")
            append(data)
        }
    }
}