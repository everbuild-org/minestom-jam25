# Minestom Game Jam Submission

Team:

- Wi1helm
- \_CreepyX_
- TheNico24
- Bloeckchengrafik
- JustAlittleWolf
- p3sto

## Setup Instructions

Import into intellij, start using the `gradle run` task. Create a jar using the `gradle jar` task.

## Documentation for the base setup

There are three important modules: App, Resources and Utils.

- App contains the primary application logic written for the game jam
- Resources contain the definitions for the resource pack.
- Utils contain the core implementation "Orion" and its minestom platform.

## Orion (Core Modules)

The following modules are part of Orion:

### Configuration

By creating objects that inherit from `ConfigurationNamespace`, you can create
configuration files by using an intutitive API, see the following example:

```kotlin
object SharedPropertyConfig : ConfigurationNamespace("orion") {
    private val defaultEnvironment = loadDefaultEnvironment()
    val tolgeeHost by string("tolgee.host", "https://translate.everbuild.org")
    val bcpEnabled by boolean("bcp.enabled", false)
}
```

All configuration files can be configured through a K8S provider, when installed, environment variables (ex:
CEL_ORION_TOLGEE_HOST), java system properties and
.properties files (ex: `orion.properties` in the server directory containing `bcp.enabled=true`)

You can access these config values through the use of the dot operator like any other field.

### Chat

Orion automatically manages the chat through LuckPerms. During the jam, LuckPerms will be autoconfigured
to add special prefixes to all team members. See the
list [here](./utils/src/main/kotlin/org/everbuild/celestia/orion/platform/minestom/luckperms/MinestomLuckPermsProvider.kt).

All chat messages are stored and can be queried/deleted for moderation purposes.

Chat messages will also be monitored for swear words, those get censored. We might want to remove that for the jam.

### Database Management

Database management works through [Ktorm](https://www.ktorm.org/). This ORM is configured to point to a local sqlite
database during the jam.

To create new tables, you need to do two things: Write
the [ktorm table definition](https://www.ktorm.org/en/entities-and-column-binding.html) and
by defining a migration. This can be done using .sql files in the `migrations` resource directory. You'll need to call
them using the
`Migrator` singleton. See the orion package for a few examples on how to do this.

### Friends

Friend management will not be available during the jam.

### Menu

Orion provides a menu implementation through the use of a kotlin dsl. See this example:

```kotlin
class MenuClassName(player: Player) : Menu(player, "[PLACEHOLDER]", 6) {
    init {
        item(9 * 2 + 4)
            .material(Material.EMERALD)
            .name("PROCEED")
            .then {
                // execute your click action
                player.closeInventory()
            }
    }
}
```

You can open the menu using `MenuClassName(player).open()`.

### Online time

Online time management will not be available during the jam.

### Resource pack data

You can query certain resource pack data through the use of the OrionPacks class. See the class for details on what is
stored.

### Scoreboard

The app can implement the `ScoreBoardCallback` interface and set it using
`ScoreBoardController.scoreBoardCallback = ...`
to implement a scoreboard.

### Translation

Translation will be available within limits during the jam. New strings should not be translated for time reasons except
if we have massive amounts of time left.

### Utilities

Orion contains a few utilities. See the corresponding package for details.

### KStom

We also include some KStom utilities in the `platform.minestom.api` package

### Coins

There is a coin system that can be accessed using the `OrionPlayer` which can be obtained using the
`player.orion` extension function. For a small introduction, see
the [Coin command](utils/src/main/kotlin/org/everbuild/celestia/orion/platform/minestom/command/CoinCommand.kt)

### Commands

The command dsl is best illustrated using a small example:

```kotlin
object BalCommand : Kommand("bal") {
    init {
        // What should happen when just the command is executed?
        default { _, _ ->
            if (!player.hasPermission("orion.command.coins.bal.self")) {
                player.sendTranslated(SharedTranslations.noPermissions)
                return@default
            }

            player.sendTranslated("orion.command.coins.bal") {
                it.replace("coins", player.orion.coins)
            }
        }

        // Define arguments to reuse later
        val argPlayer = Arg.orionPlayer("player")

        // When the player has the permission... (optional)
        requiresPermission("orion.command.coins.bal.others") {
            // they can execute this block with the following arguments:
            executes(argPlayer) {
                sendBal(player, argPlayer())
            }

            // This could also be written like:
            command {
                args += argPlayer
                executes {
                    sendBal(player, argPlayer())
                }
            }

            // The latter way is sometimes better suitable for reusing arguments
        }
    }
}
```

## Resource Pack

In the `resources` module, there is a `data` submodule and a source root. The sources here are
used to generate/serve the resource pack when applicable. The `data` is used to generate the pack.

Add source files into the `data/resources` directory and build it into the final pack using the source root.
Follow the provided examples here and be sure to list the resource lists/resources in a top-level .kt file for them to
be registered.

You can access resource pack data like identifiers or strings from the `utils` and `app` module.
