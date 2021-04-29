package network.cow.spigot.inventory

import network.cow.spigot.extensions.state.clearState
import network.cow.spigot.extensions.state.getState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * @author Benedikt Wüller
 */
class InventoryListener : Listener {

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player !is Player) return

        val currentMenu = player.getState<InventoryMenu>(InventoryDslPlugin::class.java, InventoryMenu.STATE_KEY_CURRENT_INVENTORY) ?: return
        if (event.inventory != currentMenu.inventory) return

        currentMenu.click(event.slot)
        event.isCancelled = true
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        if (player !is Player) return
        player.clearState(InventoryDslPlugin::class.java, InventoryMenu.STATE_KEY_CURRENT_INVENTORY)
    }

}
