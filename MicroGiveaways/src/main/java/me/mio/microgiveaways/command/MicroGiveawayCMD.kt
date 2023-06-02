package me.mio.microgiveaways.command

import me.mio.microgiveaways.MicroGiveaways
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import kotlin.random.Random

class MicroGiveawayCMD(private val plugin: MicroGiveaways) : CommandExecutor {
    private lateinit var config: FileConfiguration
    private var giveawayStatus: Boolean = false
    private val joinedPlayers: MutableList<Player> = mutableListOf()

    init { loadConfig() }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (label.equals("microgiveaway", ignoreCase = true)) {
            if (args.isNotEmpty()) {
                when (args[0].toLowerCase()) {
                    "start" -> {
                        if (!giveawayStatus) {
                            if (sender.hasPermission("microgiveaways.host")) {
                                val playerName = sender.name
                                val giveawayName = args.getOrNull(1) ?: "a giveaway"

                                val message = "§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §e$playerName has started a new §6$giveawayName §egiveaway. §6Click here to join!"

                                val clickableMessage = createClickableMessage(message, "/microgiveaway join")
                                Bukkit.getServer().spigot().broadcast(clickableMessage)

                                giveawayStatus = true
                                joinedPlayers.clear()

                                startCountdown()

                            } else {
                                sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cIncorrect permissions.")
                            }
                        } else {
                            sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cA giveaway is already in progress.")
                        }
                    }
                    "join" -> {
                        if (giveawayStatus) {
                            if (sender is Player) {
                                val player = sender as Player
                                if (!joinedPlayers.contains(player)) {
                                    joinedPlayers.add(player)
                                    sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §aYou have joined the giveaway!")
                                } else {
                                    sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cYou have already joined the giveaway.")
                                }
                            } else {
                                sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cOnly players can join the giveaway.")
                            }
                        } else {
                            sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cThere is no giveaway currently running.")
                        }
                    }
                    "help" -> {
                        sender.sendMessage("§6§l MicroGiveaways Help ")
                        sender.sendMessage("§e/microgiveaway start <name> - Start a giveaway")
                        sender.sendMessage("§e/microgiveaway join - Join the giveaway")
                    }
                    "reload" -> {
                        if (sender.hasPermission("microgiveaways.reload")) {
                            reloadConfig()
                            sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §aConfiguration reloaded.")
                        } else {
                            sender.sendMessage("§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cIncorrect permissions.")
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    private fun startCountdown() {
        val waitTime = config.getInt("giveaway.wait_time")
        val waitTicks = waitTime * 20L

        object : BukkitRunnable() {
            override fun run() {
                if (joinedPlayers.isNotEmpty()) {
                    val winner = joinedPlayers[Random.nextInt(joinedPlayers.size)]
                    val winnerName = winner.name

                    val winnerMessage = "§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §6The winner of the giveaway is §e$winnerName!"

                    Bukkit.getServer().broadcastMessage(winnerMessage)

                    giveawayStatus = false
                    joinedPlayers.clear()
                } else {
                    val noParticipantsMessage = "§3ᴍɪᴄʀᴏ §eɢɪᴠᴇᴀᴡᴀʏꜱ §cNo participants joined the giveaway. Giveaway cancelled."

                    Bukkit.getServer().broadcastMessage(noParticipantsMessage)

                    giveawayStatus = false
                    joinedPlayers.clear()
                }
            }
        }.runTaskLater(plugin, waitTicks)
    }


    private fun loadConfig() {
        val configFile = File(plugin.dataFolder, "config.yml")

        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            try {
                configFile.createNewFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                return
            }
            plugin.saveResource("config.yml", false)
        }

        config = YamlConfiguration.loadConfiguration(configFile)
    }

    private fun reloadConfig() { config = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, "config.yml")) }

    private fun createClickableMessage(text: String, command: String): net.md_5.bungee.api.chat.TextComponent {
        val clickableMessage = net.md_5.bungee.api.chat.TextComponent(text)
        val clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, command)
        clickableMessage.clickEvent = clickEvent
        return clickableMessage
    }
}