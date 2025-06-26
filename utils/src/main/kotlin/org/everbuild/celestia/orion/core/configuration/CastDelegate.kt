package org.everbuild.celestia.orion.core.configuration

import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.core.util.Vec3d
import org.everbuild.celestia.orion.core.util.Vec3f
import org.everbuild.celestia.orion.core.util.Vec3i
import kotlin.reflect.KProperty

class CastDelegate<T>(
    delegateBuilder: ((T) -> String) -> DelegationStack,
    private val toT: (String) -> T,
    private val fromT: (T) -> String
) {
    private val delegate = delegateBuilder(fromT)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return toT(delegate.getValue(thisRef, property))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegate.setValue(thisRef, property, fromT(value))
    }

    companion object {
        fun boolean(delegate: ((Boolean) -> String) -> DelegationStack): CastDelegate<Boolean> {
            val truthyValues = setOf("true", "yes", "t", "y", "ja", "j")
            return CastDelegate(delegate, { truthyValues.contains(it.lowercase()) }, { it.toString() })
        }

        fun integer(delegate: ((Int) -> String) -> DelegationStack): CastDelegate<Int> {
            return CastDelegate(delegate, { it.toIntOrNull() ?: 0 }, { it.toString() })
        }

        fun float(delegate: ((Float) -> String) -> DelegationStack): CastDelegate<Float> {
            return CastDelegate(delegate, { it.toFloatOrNull() ?: 0f }, { it.toString() })
        }

        fun double(delegate: ((Double) -> String) -> DelegationStack): CastDelegate<Double> {
            return CastDelegate(delegate, { it.toDoubleOrNull() ?: 0.0 }, { it.toString() })
        }

        fun vec3i(delegate: ((Vec3i) -> String) -> DelegationStack): CastDelegate<Vec3i> {
            return CastDelegate(delegate, {
                val split = it.split(" ")
                if (split.size != 3) return@CastDelegate Vec3i(0, 0, 0)
                val x = split[0].toIntOrNull() ?: return@CastDelegate Vec3i(0, 0, 0)
                val y = split[1].toIntOrNull() ?: return@CastDelegate Vec3i(0, 0, 0)
                val z = split[2].toIntOrNull() ?: return@CastDelegate Vec3i(0, 0, 0)
                return@CastDelegate Vec3i(x, y, z)
            }, {
                "${it.x} ${it.y} ${it.z}"
            })
        }

        fun vec3f(delegate: ((Vec3f) -> String) -> DelegationStack): CastDelegate<Vec3f> {
            return CastDelegate(delegate, {
                val split = it.split(" ")
                if (split.size != 3) return@CastDelegate Vec3f(0f, 0f, 0f)
                val x = split[0].toFloatOrNull() ?: return@CastDelegate Vec3f(0f, 0f, 0f)
                val y = split[1].toFloatOrNull() ?: return@CastDelegate Vec3f(0f, 0f, 0f)
                val z = split[2].toFloatOrNull() ?: return@CastDelegate Vec3f(0f, 0f, 0f)
                return@CastDelegate Vec3f(x, y, z)
            }, { "${it.x} ${it.y} ${it.z}" })
        }

        fun vec3d(delegate: ((Vec3d) -> String) -> DelegationStack): CastDelegate<Vec3d> {
            return CastDelegate(delegate, {
                val split = it.split(" ")
                if (split.size != 3) return@CastDelegate Vec3d(0.0, 0.0, 0.0)
                val x = split[0].toDoubleOrNull() ?: return@CastDelegate Vec3d(0.0, 0.0, 0.0)
                val y = split[1].toDoubleOrNull() ?: return@CastDelegate Vec3d(0.0, 0.0, 0.0)
                val z = split[2].toDoubleOrNull() ?: return@CastDelegate Vec3d(0.0, 0.0, 0.0)
                return@CastDelegate Vec3d(x, y, z)
            }, { "${it.x} ${it.y} ${it.z}" })
        }

        fun fullPosition(delegate: ((FullPosition) -> String) -> DelegationStack): CastDelegate<FullPosition> {
            return CastDelegate(delegate, {
                val split = it.split(" ")
                if (split.size != 5) return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                val x = split[0].toDoubleOrNull() ?: return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                val y = split[1].toDoubleOrNull() ?: return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                val z = split[2].toDoubleOrNull() ?: return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                val yaw = split[3].toFloatOrNull() ?: return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                val pitch = split[4].toFloatOrNull() ?: return@CastDelegate FullPosition(0.0, 0.0, 0.0, 0f, 0f)
                return@CastDelegate FullPosition(x, y, z, yaw, pitch)
            }, { "${it.x} ${it.y} ${it.z} ${it.yaw} ${it.pitch}" })
        }
    }
}