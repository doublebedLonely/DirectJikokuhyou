package com.example.directjikokuhyou.utils

import android.graphics.Bitmap

object BitmapUtils {
    fun resizeWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
        val originalHeight = bitmap.height
        return Bitmap.createScaledBitmap(bitmap, newWidth, originalHeight, true)
    }
}