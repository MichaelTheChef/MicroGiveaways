package me.mio.microgiveaways

import me.mio.microgiveaways.command.MicroGiveawayCMD
import me.mio.microgiveaways.completers.MicroGiveawaysTabs
import org.bukkit.plugin.java.JavaPlugin

class MicroGiveaways : JavaPlugin() {
    override fun onEnable() {
        getCommand("microgiveaway")?.setExecutor(MicroGiveawayCMD(this))
        getCommand("microgiveaway")?.tabCompleter = MicroGiveawaysTabs()
    }

    override fun onDisable() {}
}
