package com.abn.amro.core.ui

import android.content.Context
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class UiTextResolveTest {

    @Test
    fun `LocalizedCurrency formats using provided locale (not default)`() {
        val context = mockk<Context>(relaxed = true)
        val locale = Locale.US

        val expected = NumberFormat.getCurrencyInstance(locale).apply {
            maximumFractionDigits = 0
        }.format(160_000_000L)

        val actual = UiText.LocalizedCurrency(amount = 160_000_000L, maxFractionDigits = 0)
            .resolve(context, locale)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `LocalizedDecimal formats with fixed fraction digits using provided locale`() {
        val context = mockk<Context>(relaxed = true)
        val locale = Locale.GERMANY // different decimal separator

        val expected = NumberFormat.getNumberInstance(locale).apply {
            maximumFractionDigits = 1
            minimumFractionDigits = 1
        }.format(8.8)

        val actual = UiText.LocalizedDecimal(value = 8.8, fractionDigits = 1)
            .resolve(context, locale)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `LocalizedDateIso returns null for blank and returns formatted date for valid iso`() {
        val context = mockk<Context>(relaxed = true)
        val locale = Locale.UK

        assertThat(UiText.LocalizedDateIso("  ").resolve(context, locale)).isNull()

        val iso = "2010-07-16"
        val expected = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale)
            .format(LocalDate.parse(iso))

        val actual = UiText.LocalizedDateIso(iso).resolve(context, locale)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `LocalizedDateIso returns original string if parse fails`() {
        val context = mockk<Context>(relaxed = true)
        val locale = Locale.US

        val bad = "not-a-date"
        val actual = UiText.LocalizedDateIso(bad).resolve(context, locale)

        assertThat(actual).isEqualTo(bad)
    }

    @Test
    fun `RuntimeMinutes returns null for null or non-positive`() {
        val context = mockk<Context>(relaxed = true)
        val locale = Locale.US

        assertThat(UiText.RuntimeMinutes(null).resolve(context, locale)).isNull()
        assertThat(UiText.RuntimeMinutes(0).resolve(context, locale)).isNull()
        assertThat(UiText.RuntimeMinutes(-5).resolve(context, locale)).isNull()
    }

    @Test
    fun `RuntimeMinutes uses minutes-only resource when under 60`() {
        val context = mockk<Context>()
        val resources = mockk<Resources>()

        every { context.resources } returns resources
        every { resources.getString(R.string.runtime_format_minutes, 59) } returns "59m"

        val actual = UiText.RuntimeMinutes(59).resolve(context, Locale.US)

        assertThat(actual).isEqualTo("59m")
        verify(exactly = 1) { resources.getString(R.string.runtime_format_minutes, 59) }
    }

    @Test
    fun `RuntimeMinutes uses full resource when 60 or more`() {
        val context = mockk<Context>()
        val resources = mockk<Resources>()

        every { context.resources } returns resources
        every { resources.getString(R.string.runtime_format_full, 2, 28) } returns "2h 28m"

        val actual = UiText.RuntimeMinutes(148).resolve(context, Locale.US)

        assertThat(actual).isEqualTo("2h 28m")
        verify(exactly = 1) { resources.getString(R.string.runtime_format_full, 2, 28) }
    }

    @Test
    fun `PluralResource resolves nested UiText args and passes resolved value to resources`() {
        val context = mockk<Context>()
        val resources = mockk<Resources>()
        every { context.resources } returns resources

        val locale = Locale.US
        val voteCount = 20_000
        val formatted = NumberFormat.getNumberInstance(locale).apply {
            maximumFractionDigits = 0
        }.format(voteCount.toLong())

        every {
            resources.getQuantityString(
                R.plurals.votes_count,
                voteCount,
                formatted
            )
        } returns "$formatted votes"

        val text = UiText.PluralResource(
            resId = R.plurals.votes_count,
            quantity = voteCount,
            formatArgs = listOf(UiText.LocalizedNumber(voteCount.toLong()))
        )

        val actual = text.resolve(context, locale)

        assertThat(actual).isEqualTo("$formatted votes")
        verify(exactly = 1) {
            resources.getQuantityString(R.plurals.votes_count, voteCount, formatted)
        }
    }
}
