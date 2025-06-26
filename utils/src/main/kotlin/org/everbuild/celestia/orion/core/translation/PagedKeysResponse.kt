package org.everbuild.celestia.orion.core.translation

data class PagedKeysResponse(
    val _embedded: TranslationKeys?,
    val page: PaginationData,
    val nextCursor: String?
)