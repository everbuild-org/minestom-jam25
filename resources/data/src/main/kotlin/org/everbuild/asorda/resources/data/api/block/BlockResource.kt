package org.everbuild.asorda.resources.data.api.block

import net.kyori.adventure.key.Key

open class BlockResource(
    val key: Key,
    val stateIds: HashMap<String, BlockStateIdentifier>,
    var defaultStateId: String = "",
    var defaultModel: String = ""
)

sealed class BlockStateIdentifier {
    data class ForeignStateBlockStateIdentifier(
        val key: Key,
        val properties: Map<String, String>
    ) : BlockStateIdentifier()
}
