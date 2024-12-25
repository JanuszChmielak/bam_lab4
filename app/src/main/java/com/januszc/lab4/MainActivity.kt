package com.januszc.lab4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import com.januszc.lab4.R
import com.januszc.lab4.EncryptionUtils

class MainActivity : AppCompatActivity() {

    private val REQUEST_MANAGE_STORAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ustawienie widoku układu z XML
        setContentView(R.layout.activity_main)

        // Sprawdzenie uprawnień
        checkPermissions()

        // Powiązanie widoków z układem XML
        val inputField = findViewById<EditText>(R.id.inputField)
        val outputField = findViewById<TextView>(R.id.outputField)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val loadButton = findViewById<Button>(R.id.loadButton)

        // Obsługa przycisków
        saveButton.setOnClickListener {
            val inputData = inputField.text.toString()
            EncryptionUtils.saveEncryptedData(this, inputData)
        }

        loadButton.setOnClickListener {
            val outputData = EncryptionUtils.loadEncryptedData(this)
            outputField.text = outputData
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ - sprawdzanie MANAGE_EXTERNAL_STORAGE
            if (!android.os.Environment.isExternalStorageManager()) {
                requestManageStoragePermission()
            }
        } else {
            // Starsze wersje Androida - standardowe uprawnienia
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (!permissions.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_MANAGE_STORAGE)
            }
        }
    }

    private fun requestManageStoragePermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, REQUEST_MANAGE_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !android.os.Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Aplikacja wymaga dostępu do całej pamięci!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                finish() // Zamknij aplikację, jeśli nie przyznano uprawnień
            }
        }
    }
}
