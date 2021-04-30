package network.cow.spigot.inventory

import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

/**
 * @author Benedikt Wüller
 */

fun InventoryType.getWidth() : Int {
    return when (this) {
        InventoryType.CHEST -> 9
        InventoryType.DISPENSER -> 3
        InventoryType.DROPPER -> 3
        InventoryType.PLAYER -> 9
        InventoryType.CREATIVE -> 9
        InventoryType.ENDER_CHEST -> 9
        InventoryType.SHULKER_BOX -> 9
        InventoryType.BARREL -> 9
        else -> this.defaultSize
    }
}

fun Inventory.getWidth() : Int = this.type.getWidth()

fun Inventory.getHeight() : Int = this.size / this.getWidth()
