package org.everbuild.celestia.orion.core.configuration

import org.everbuild.celestia.orion.core.util.*

abstract class ConfigurationNamespace(private val namespace: String, forceLocal: Boolean = false) : OrionDependency {
    private val configurationProviderStack: List<ConfigurationProvider> = run {
        val providers = mutableListOf<ConfigurationProvider>()
        val defaults = DefaultProvider()

        providers.add(SystemPropertyProvider(namespace))
        providers.add(PropertyProvider(namespace))
        providers.add(EnvironmentVariableProvider(namespace))
        providers.add(defaults)
        providers
    }

    protected fun string(key: String, default: String): DelegationStack {
        setDefault(key, default)
        return DelegationStack(configurationProviderStack.map { it.getDelegation(key) })
    }

    protected fun boolean(key: String, default: Boolean) = CastDelegate.boolean { string(key, it(default)) }
    protected fun integer(key: String, default: Int) = CastDelegate.integer { string(key, it(default)) }
    protected fun float(key: String, default: Float) = CastDelegate.float { string(key, it(default)) }
    protected fun double(key: String, default: Double) = CastDelegate.double { string(key, it(default)) }
    protected fun vec3i(key: String, default: Vec3i) = CastDelegate.vec3i { string(key, it(default)) }
    protected fun vec3f(key: String, default: Vec3f) = CastDelegate.vec3f { string(key, it(default)) }
    protected fun vec3d(key: String, default: Vec3d) = CastDelegate.vec3d { string(key, it(default)) }
    protected fun pos(key: String, default: FullPosition) = CastDelegate.fullPosition { string(key, it(default)) }

    private fun setDefault(key: String, value: String) {
        configurationProviderStack.forEach { it.setDefault(key, value) }
    }
}