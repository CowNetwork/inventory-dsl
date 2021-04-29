package network.cow.spigot.inventory

import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Benedikt Wüller
 */
class InventoryDslPlugin : JavaPlugin() {

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(InventoryListener(), this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }

}
