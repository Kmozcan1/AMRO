package com.abn.amro.core.ui

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText

    class PluralResource(
        @param:PluralsRes val resId: Int,
        val quantity: Int,
        vararg val args: Any
    ) : UiText

    data class LocalizedCurrency(val amount: Double) : UiText

    data class LocalizedDate(val rawDate: String) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
            is PluralResource -> pluralStringResource(resId, quantity, *args)
            is LocalizedCurrency -> {
                val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                format.maximumFractionDigits = 0
                format.format(amount)
            }
            is LocalizedDate -> formatDate(rawDate)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
            is PluralResource -> context.resources.getQuantityString(resId, quantity, *args)
            is LocalizedCurrency -> {
                val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
                format.maximumFractionDigits = 0
                format.format(amount)
            }
            is LocalizedDate -> formatDate(rawDate)
        }
    }

    private fun formatDate(rawDate: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
            val date = LocalDate.parse(rawDate, inputFormatter)

            val outputFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())

            date.format(outputFormatter)
        } catch (e: Exception) {
            rawDate
        }
    }
}