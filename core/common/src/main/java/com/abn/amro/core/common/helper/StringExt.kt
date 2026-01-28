package com.abn.amro.core.common.helper

import com.abn.amro.core.common.TmdbConfig
import com.abn.amro.core.common.model.TmdbImageSize


fun String.toTmdbUrl(size: TmdbImageSize): String {
    if (this.isBlank()) return ""

    val base = TmdbConfig.IMAGE_BASE_URL.trimEnd('/')
    val path = this.trimStart('/')
    return "$base/${size.segment}/$path"
}