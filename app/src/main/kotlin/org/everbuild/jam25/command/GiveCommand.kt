package org.everbuild.jam25.command

import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated
import org.everbuild.jam25.item.api.ItemLoader

object GiveCommand : Kommand("give") {
    init {
        permission = "averium.command.give"
        default { _, _ ->
            player.sendTranslated("averium.command.give.usage")
        }
        val itemType = Arg.itemstack("item")
        val amount = Arg.int("amount")
        val allItems = ItemLoader.all()
        val targetPlayer = Arg.player("target")
        command {
            args += targetPlayer
            command {
                args += itemType
                executes {
                    args[targetPlayer].inventory.addItemStack(args[itemType])
                    player.sendTranslated("averium.command.give.success") {
                        it.replace("player", args[targetPlayer].name)
                        it.replace("amount", "1")
                        it.replace("itemtype", args[itemType].material().name())
                    }
                }
                command {
                    args += amount
                    executes {
                        args[targetPlayer].inventory.addItemStack(args[itemType].withAmount(args[amount]))
                        player.sendTranslated("averium.command.give.success") {
                            it.replace("player", args[targetPlayer].name)
                            it.replace("amount", args[amount].toString())
                            it.replace("itemtype", args[itemType].material().name())
                        }
                    }
                }
            }

            for (item in allItems) {
                command {
                    args += Arg.literal(item.id.asString())
                    executes {
                        args[targetPlayer].inventory.addItemStack(item.createNewStack(1))
                        player.sendTranslated("averium.command.give.success") {
                            it.replace("player", args[targetPlayer].name)
                            it.replace("amount", "1")
                            it.replace("itemtype", item.id.asString())
                        }
                    }
                    executes(amount) {
                        args[targetPlayer].inventory.addItemStack(item.createNewStack(args[amount]))
                        player.sendTranslated("averium.command.give.success") {
                            it.replace("player", args[targetPlayer].name)
                            it.replace("amount", args[amount].toString())
                            it.replace("itemtype", item.id.asString())
                        }
                    }
                }
            }
        }
    }
}