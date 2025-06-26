package org.everbuild.celestia.orion.platform.minestom.menu

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import org.everbuild.asorda.resources.data.items.GlobalIcons
import org.everbuild.celestia.orion.core.translation.TranslationContext
import org.everbuild.celestia.orion.platform.minestom.util.c

abstract class PagedMenu<T : List<E>, E>(
    player: Player,
    nameI18NKey: String,
    rows: Int,
    val page: Int,
    val search: Boolean = false,
    val back: Boolean = true,
    replacements: (TranslationContext) -> Unit = {}
) : Menu(player, nameI18NKey, rows + 1, replacements) {
    private fun setup() {
        val itemList = getItemList()
        val itemsPerPage = (rows - 1) * 9
        val maxPage = (itemList.size / itemsPerPage) + 1
        val minPage = 1
        val currentPage = page.coerceIn(minPage, maxPage)

        val subList = itemList.subList(
            ((currentPage - 1) * itemsPerPage).coerceAtLeast(0),
            ((currentPage - 1) * itemsPerPage + itemsPerPage.coerceAtMost(itemList.size)).coerceAtMost(itemList.size)
        )
        for ((i, item) in subList.withIndex()) {
            setItem(i, item)
        }

        if (back)
            item(itemsPerPage)
                .material(GlobalIcons.iconBackGray)
                .name("orion.remenu.back")
                .then { back() }

        if (search) {
            item(itemsPerPage + 5)
                .material(GlobalIcons.iconSearch)
                .name("orion.remenu.search")
                .then {
                    openAnvilGui(player) {
                        this.title = player.c("orion.menu.search.title")
                        this.text = player.c("orion.menu.search.text")
                        then {
                            closeAnvil()
                            player.playSound(Sound.sound(Key.key("minecraft", "ui.button.click"), Sound.Source.MASTER, 1.0f, 1.0f))
                            onSearch(it)
                        }
                    }
                }

            item(itemsPerPage + 3)
                .material(GlobalIcons.iconBook)
                .name("orion.menu.current") { it.replaceNumber("page", currentPage) }

            nextItem(itemsPerPage + 7, currentPage)
            previousItem(itemsPerPage + 1, currentPage)
        } else {
            item(itemsPerPage + 4)
                .material(GlobalIcons.iconBook)
                .name("orion.menu.current") { it.replaceNumber("page", currentPage) }

            nextItem(itemsPerPage + 6, currentPage)
            previousItem(itemsPerPage + 2, currentPage)
        }
    }

    private fun nextItem(pos: Int, current: Int) {
        item(pos)
            .material(GlobalIcons.iconRightGray)
            .name("orion.remenu.next")
            .then { openPage(current + 1) }
    }

    private fun previousItem(pos: Int, current: Int) {
        item(pos)
            .material(GlobalIcons.iconLeftGray)
            .name("orion.remenu.previous")
            .then { openPage(current - 1) }
    }

    abstract fun setItem(pos: Int, item: E)
    abstract fun getItemList(): T
    abstract fun openPage(page: Int)
    open fun back() {}
    open fun onSearch(query: String) {}

    override fun open() {
        setup()
        super.open()
    }
}