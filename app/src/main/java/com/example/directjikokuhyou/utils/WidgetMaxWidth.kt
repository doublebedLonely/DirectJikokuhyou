package com.example.directjikokuhyou.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue

fun getWidgetMaxWidth(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): Int {
    // ウィジェットのサイズオプションを取得
    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)

    // 最小または最大の幅を取得（dp単位）
    val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
    val maxWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)

    // 必要に応じて最大幅を選択（ここでは最小幅を使用）
    val maxWidthDpSelected = if (maxWidthDp > 0) maxWidthDp else minWidthDp

    // dpからpxへの変換
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxWidthDpSelected.toFloat(), displayMetrics).toInt()
}


fun getWidgetContainerMaxWidth(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    paddingDp: Int = 16
): Int {
    // ウィジェットのサイズオプションを取得
    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)

    // ウィジェット全体の最小幅を取得（dp単位）
    val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)

    // ウィジェット全体の幅から左右のpaddingを引いた値を計算
    val containerWidthDp = minWidthDp - paddingDp * 2

    // dpからpxへの変換
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, containerWidthDp.toFloat(), displayMetrics).toInt()
}

fun resizeBitmapWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
    // 元の高さを取得
    val originalHeight = bitmap.height

    // デバッグ: 元のサイズ確認
    println("Original Width: ${bitmap.width}, Height: $originalHeight, New Width: $newWidth")

    // 幅を新しいサイズに設定し、高さはそのまま
    return Bitmap.createScaledBitmap(bitmap, newWidth, originalHeight, true)
}