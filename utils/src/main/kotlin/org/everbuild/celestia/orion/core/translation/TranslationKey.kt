package org.everbuild.celestia.orion.core.translation

data class TranslationKey(
    val keyId: Int,
    val keyDescription: String,
    val keyName: String,
    val keyNamespace: String,
    val keyTags: List<KeyTag>,
    val translations: Map<String, Translation>
)