package com.januszc.lab4

import android.content.Context
import android.os.Environment
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.nio.charset.StandardCharsets

object EncryptionUtils {

    private const val FILE_NAME = "tajne.bin"

    fun saveEncryptedData(context: Context, data: String) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val file = File(Environment.getExternalStorageDirectory(), FILE_NAME)
            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileOutput().use { outputStream ->
                outputStream.write(data.toByteArray(StandardCharsets.UTF_8))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadEncryptedData(context: Context): String {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val file = File(Environment.getExternalStorageDirectory(), FILE_NAME)

            if (!file.exists()) {
                return "Plik nie istnieje!"
            }

            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileInput().use { inputStream ->
                inputStream.readBytes().toString(StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Błąd podczas odczytu danych"
        }
    }
}
