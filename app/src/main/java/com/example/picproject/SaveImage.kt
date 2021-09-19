package com.example.picproject

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import android.content.Intent
import android.net.Uri


class SaveImage {

    @Throws(IOException::class)
    suspend fun saveImageToStorage(
        context: Context,
        bitmapObject: Bitmap,
        imageTitle: String,
        mimeType: String
    ) {
        val imageOutStream: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val directory = Environment.DIRECTORY_PICTURES + File.separator + "Unsplash"

            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageTitle)
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                directory
            )
            val uri =
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
            imageOutStream = context.contentResolver.openOutputStream(uri!!)

            val intent = Intent(Intent.ACTION_ATTACH_DATA)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(uri, "image/jpeg")
            intent.putExtra("mimeType", "image/jpeg")
            context.startActivity(Intent.createChooser(intent, "Set as:"))

        } else {
            val directory =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "Unsplash")

            if (!directory.exists())
                directory.mkdirs()

            val image = File(directory, imageTitle)
            imageOutStream = FileOutputStream(image)

            val intent = Intent(Intent.ACTION_ATTACH_DATA)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(Uri.fromFile(directory), "image/jpeg")
            intent.putExtra("mimeType", "image/jpeg")
            context.startActivity(Intent.createChooser(intent, "Set as:"))
        }

        try {
            bitmapObject.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
        } finally {
            imageOutStream?.close()
            withContext(Main) {
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}