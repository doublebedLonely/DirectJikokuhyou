package com.example.directjikokuhyou.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

class BitmapGenerator(private val context: Context) {

    fun generate(
        text: String,
        fontResId: Int,
        textSize: Float,
        textColor: Int,
        backgroundColor: Int = android.graphics.Color.parseColor("#101010")
    ): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textAlign = Paint.Align.LEFT
            this.textSize = textSize
            typeface = Typeface.create(context.resources.getFont(fontResId), Typeface.NORMAL)
        }

        val textWidth = paint.measureText(text).toInt()
        val textHeight = (paint.descent() - paint.ascent()).toInt()

        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor)
        canvas.drawText(text, 0f, -paint.ascent(), paint)

        return bitmap
    }
}