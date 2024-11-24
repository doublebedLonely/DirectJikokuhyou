package com.example.directjikokuhyou

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.directjikokuhyou.utils.BitmapGenerator
import com.example.directjikokuhyou.utils.loadTrainTimes
import com.example.directjikokuhyou.utils.getNextTwoDirectTrains
import com.example.directjikokuhyou.utils.getWidgetContainerMaxWidth
import com.example.directjikokuhyou.utils.BitmapUtils
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val bitmapGenerator = BitmapGenerator(context) // BitmapGeneratorを初期化

        // 複数のウィジェットがある場合に対応
        for (appWidgetId in appWidgetIds) {
            // ウィジェットレイアウトを取得
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // ボタンのクリックイベントを設定
            val intent = Intent(context, WidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_image_container, pendingIntent)

            // 現在の時刻を取得し、widget_textに設定
            val currentTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            views.setTextViewText(R.id.widget_text, "更新時刻: $currentTime")

            // `widget_image_container`の最大幅を取得
            val containerMaxWidth = getWidgetContainerMaxWidth(context, appWidgetManager, appWidgetId)

            val trainTimeList = loadTrainTimes(context)
            val trainTimePair = getNextTwoDirectTrains(trainTimeList)

            // Bitmapを作成
            val bitmap = bitmapGenerator.generate(
                text = trainTimePair.first?.time ?: "直通なし",
                fontResId = R.font.pixelfont,
                textSize = 50f,
                textColor = Color.WHITE
            )

            // Bitmapを作成
            val bitmapExp = when (trainTimePair.first?.color){
                "b" -> bitmapGenerator.generate(
                "  ",
                R.font.pixelfont, // フォントリソース
                50f, // テキストサイズ
                Color.CYAN
                )

                "r" -> bitmapGenerator.generate(
                    " 急行",
                    R.font.pixelfont, // フォントリソース
                    50f, // テキストサイズ
                    Color.RED
                )

                else -> bitmapGenerator.generate(
                    "",
                    R.font.pixelfont, // フォントリソース
                    50f, // テキストサイズ
                    Color.RED
                )
                }

            // Bitmapを作成
            val bitmapDest = bitmapGenerator.generate(
                "新宿",
                R.font.pixelfont, // フォントリソース
                50f, // テキストサイズ
                Color.parseColor("#FFA500") // 明るいオレンジ
            )

            // `widget_image`はそのまま設定
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // 各Bitmapの幅を取得
            val totalWidth = bitmap.width + bitmapExp.width + bitmapDest.width

            // 幅の合計が`containerMaxWidth`を超える場合にリサイズ
            if (totalWidth > containerMaxWidth) {
                val scaleFactor = containerMaxWidth.toFloat() / totalWidth
                val adjustedExpWidth = (bitmapExp.width * scaleFactor).toInt()
                val adjustedDestWidth = (bitmapDest.width * scaleFactor).toInt()

                // 幅のみリサイズ（高さは固定）
                val resizedBitmapExp = BitmapUtils.resizeWidth(bitmapExp, adjustedExpWidth)
                val resizedBitmapDest = BitmapUtils.resizeWidth(bitmapDest, adjustedDestWidth)

                // リサイズ後のBitmapを設定
                views.setImageViewBitmap(R.id.widget_image_exp, resizedBitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest, resizedBitmapDest)
            } else {
                // 幅が問題なければそのまま設定
                views.setImageViewBitmap(R.id.widget_image_exp, bitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest, bitmapDest)
            }

            // ウィジェットを更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}