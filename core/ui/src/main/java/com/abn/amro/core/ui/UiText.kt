package com.abn.amro.core.ui

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

sealed interface UiText {

    data class DynamicString(val value: String?) : UiText

    data class StringResource(
        @param:StringRes val resId: Int,
        val formatArgs: List<Any?> = emptyList(),
    ) : UiText

    data class PluralResource(
        @param:PluralsRes val resId: Int,
        val quantity: Int,
        val formatArgs: List<Any?> = emptyList(),
    ) : UiText

    data class LocalizedNumber(val value: Long, val maxFractionDigits: Int = 0) : UiText
    data class LocalizedDecimal(val value: Double, val fractionDigits: Int = 1) : UiText
    data class LocalizedCurrency(val amount: Long, val maxFractionDigits: Int = 0) : UiText
    data class LocalizedDateIso(val isoDate: String?) : UiText
    data class RuntimeMinutes(val minutes: Int?) : UiText
}

@Composable
fun UiText.asString(): String? {
    val configuration = LocalConfiguration.current
    val locale: Locale = configuration.locales[0]

    return when (this) {
        is UiText.DynamicString -> value

        is UiText.StringResource -> {
            val args = resolveArgsComposable(formatArgs)
            stringResource(resId, *args)
        }

        is UiText.PluralResource -> {
            val args = resolveArgsComposable(formatArgs)
            pluralStringResource(resId, quantity, *args)
        }

        is UiText.LocalizedNumber -> formatNumber(value, locale, maxFractionDigits)
        is UiText.LocalizedDecimal -> formatDecimal(value, locale, fractionDigits)
        is UiText.LocalizedCurrency -> formatCurrency(amount, locale, maxFractionDigits)

        is UiText.LocalizedDateIso -> {
            if (isoDate.isNullOrBlank()) null else formatDate(isoDate, locale)
        }

        is UiText.RuntimeMinutes -> {
            if (minutes == null || minutes <= 0) null else {
                val hours = minutes / 60
                val remaining = minutes % 60
                if (hours > 0) {
                    stringResource(R.string.runtime_format_full, hours, remaining)
                } else {
                    stringResource(R.string.runtime_format_minutes, remaining)
                }
            }
        }
    }
}

fun UiText.resolve(context: Context, locale: Locale = Locale.getDefault()): String? {
    val resources = context.resources
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> {
            val args = resolveArgsNonComposable(context, locale, formatArgs)
            context.getString(resId, *args)
        }
        is UiText.PluralResource -> {
            val args = resolveArgsNonComposable(context, locale, formatArgs)
            resources.getQuantityString(resId, quantity, *args)
        }
        is UiText.LocalizedNumber -> formatNumber(value, locale, maxFractionDigits)
        is UiText.LocalizedDecimal -> formatDecimal(value, locale, fractionDigits)
        is UiText.LocalizedCurrency -> formatCurrency(amount, locale, maxFractionDigits)
        is UiText.LocalizedDateIso -> {
            if (isoDate.isNullOrBlank()) null else formatDate(isoDate, locale)
        }
        is UiText.RuntimeMinutes -> {
            if (minutes == null || minutes <= 0) null else {
                val hours = minutes / 60
                val remaining = minutes % 60
                if (hours > 0) {
                    resources.getString(R.string.runtime_format_full, hours, remaining)
                } else {
                    resources.getString(R.string.runtime_format_minutes, remaining)
                }
            }
        }
    }
}

@Composable
private fun resolveArgsComposable(args: List<Any?>): Array<out Any> =
    args.mapNotNull { arg ->
        if (arg is UiText) arg.asString() else arg
    }.toTypedArray()

private fun resolveArgsNonComposable(
    context: Context,
    locale: Locale,
    args: List<Any?>
): Array<out Any> =
    args.mapNotNull { arg ->
        if (arg is UiText) arg.resolve(context, locale) else arg
    }.toTypedArray()

private fun formatNumber(value: Long, locale: Locale, maxDigits: Int) =
    NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = maxDigits }.format(value)

private fun formatDecimal(value: Double, locale: Locale, digits: Int) =
    NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = digits
        minimumFractionDigits = digits
    }.format(value)

private fun formatCurrency(amount: Long, locale: Locale, maxDigits: Int) =
    NumberFormat.getCurrencyInstance(locale).apply { maximumFractionDigits = maxDigits }.format(amount)

private fun formatDate(isoDate: String, locale: Locale): String {
    val date = runCatching { LocalDate.parse(isoDate) }.getOrNull()
    return date?.let {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).format(it)
    } ?: isoDate
}