package org.everbuild.jam25.item.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ItemDataComponent(
    val namespace: String = "averium",
    val id: String
)
