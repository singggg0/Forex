package com.project.samsin.forex.extension

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.project.samsin.forex.R
import java.math.BigDecimal
import java.text.DecimalFormat

fun BigDecimal?.toFormatDecimal(decimal: Int, defaultText: String = "-"): String {
    if (this == null) return defaultText
    return try {
        val df = DecimalFormat("#,###")
        df.minimumFractionDigits = decimal
        df.minimumIntegerDigits = 1
        df.format(this)
    } catch (e: Exception) {
        defaultText
    }
}

@ColorInt
fun BigDecimal?.getTrendColor(
    context: Context,
    @ColorInt defaultColor: Int = ContextCompat.getColor(context, R.color.white)
): Int {
    if (this == null) return defaultColor

    return when (this.compareTo(BigDecimal.ZERO)) {
        1 -> ContextCompat.getColor(context, R.color.rise_color)
        -1 -> ContextCompat.getColor(context, R.color.drop_color)
        else -> defaultColor
    }
}