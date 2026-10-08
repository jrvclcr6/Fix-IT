package com.example.data.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

data class CapturePhotoTarget(
    val path: String,
    val uri: Uri
)

object JournalPhotoStorage {
    private const val DIRECTORY_NAME = "journal_photos"

    fun createCaptureTarget(context: Context): CapturePhotoTarget {
        val file = newPhotoFile(context, "jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return CapturePhotoTarget(path = file.absolutePath, uri = uri)
    }

    fun importPhoto(context: Context, source: Uri): String? {
        val extension = when (context.contentResolver.getType(source)) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val destination = newPhotoFile(context, extension)
        return try {
            context.contentResolver.openInputStream(source)?.use { input ->
                destination.outputStream().use(input::copyTo)
            } ?: error("Unable to open selected image")
            destination.absolutePath
        } catch (_: Exception) {
            destination.delete()
            null
        }
    }

    fun delete(path: String?) {
        if (!path.isNullOrBlank()) {
            File(path).delete()
        }
    }

    private fun newPhotoFile(context: Context, extension: String): File {
        val directory = File(context.filesDir, DIRECTORY_NAME).apply { mkdirs() }
        return File(directory, "journal_${UUID.randomUUID()}.$extension")
    }
}
