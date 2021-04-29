package network.cow.spigot.inventory

import org.bukkit.entity.Player

/**
 * @author Benedikt Wüller
 */
object Actions {

    fun navigateBack(): (InventoryMenu, Player) -> Boolean = { menu, _ ->
        val parent = menu.parent
        when {
            parent != null -> parent.open(false)
            else -> menu.close()
        }
        true
    }

    fun navigateTo(inventory: InventoryMenu, flushHistory: Boolean = false): (InventoryMenu, Player) -> Boolean = { _, player ->
        if (inventory.player != player) TODO("error")
        inventory.open(flushHistory)
        true
    }

    fun close(): (InventoryMenu, Player) -> Boolean = { menu, _ ->
        menu.close()
        true
    }

}
