package org.everbuild.asorda.resources.data.api.multipart

import org.everbuild.asorda.resources.data.api.item.ItemResource

class MultipartItemResource(private val items: List<ItemResource>, private val width: Int, private val height: Int) {
    fun item(x: Int, y: Int) = items[y * width + x]
    fun forEach(action: (x: Int, y: Int, item: ItemResource) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                action(x, y, items[y * width + x])
            }
        }
    }
}