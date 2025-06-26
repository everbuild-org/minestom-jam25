
package org.everbuild.celestia.orion.core.translation

import java.util.HashMap

@Suppress("PropertyName")
data class PagedLanguageResponse(
    val _embedded: LanguageResponse,
    val _links: HashMap<String, HashMap<String, String>>,
    val page: HashMap<String, Int>
)