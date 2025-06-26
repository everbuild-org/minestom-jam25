package org.everbuild.celestia.orion.core.database.bank

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HashUtil {
    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedhash = digest.digest(
            pin.toByteArray(StandardCharsets.UTF_8)
        )

        return "512$" + encodedhash.joinToString("") { "%02x".format(it) }
    }

    fun checkPin(pin: String, hash: String): Boolean {
        return hashPin(pin) == hash
    }
}