package me.mio.microgiveaways.completers

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MicroGiveawaysTabs : TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (command.name.equals("microgiveaway", ignoreCase = true)) {
            if (args.size == 1) {
                val completions = mutableListOf<String>()
                completions.add("start")
                completions.add("join")
                completions.add("help")
                completions.add("reload")
                return completions
            }
        }
        return null
    }
}
