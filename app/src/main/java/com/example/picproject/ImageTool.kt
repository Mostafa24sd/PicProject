package com.example.picproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.picproject.ui.frgs.TAG
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@RequiresApi(Build.VERSION_CODES.Q)
fun getMediaDirUri(
    context: Context,
    imageTitle: String,
    mimeType: String
): Uri? {

    val directory = Environment.DIRECTORY_PICTURES + File.separator + "Unsplash"

    val values = ContentValues()
    values.put(MediaStore.Images.Media.DISPLAY_NAME, imageTitle)
    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
    values.put(
        MediaStore.Images.Media.RELATIVE_PATH,
        directory
    )

    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )
}

fun getMediaDirFile(
    imageTitle: String,
): File {
    val directory =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "Unsplash")

    if (!directory.exists())
        directory.mkdirs()

    return File(directory, imageTitle)
}

suspend fun saveImg(context: Context, bitmapObject: Bitmap, uri: Uri?, file: File?): Boolean {

    val imageOutStream: OutputStream? = if (uri != null) {
        context.contentResolver.openOutputStream(uri)
    } else {
        FileOutputStream(file)
    }

    try {
        bitmapObject.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
    } catch (e: Exception) {
        Log.d(TAG, "saveImg: ${e.message}")
        return false
    } finally {
        imageOutStream?.close()
    }
    return true
}

fun setWall(context: Context, uri: Uri?) {
    val intent = Intent(Intent.ACTION_ATTACH_DATA)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.setDataAndType(uri, "image/jpeg")
    intent.putExtra("mimeType", "image/jpeg")
    context.startActivity(Intent.createChooser(intent, "Set as:"))
}
