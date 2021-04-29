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
        InventoryType.FURNACE -> 3
        InventoryType.WORKBENCH -> 3
        InventoryType.CRAFTING -> 2
        InventoryType.ENCHANTING -> 2
        InventoryType.BREWING -> 5
        InventoryType.PLAYER -> 9
        InventoryType.CREATIVE -> 9
        InventoryType.MERCHANT -> 3
        InventoryType.ENDER_CHEST -> 9
        InventoryType.ANVIL -> 3
        InventoryType.SMITHING -> 3
        InventoryType.BEACON -> 1
        InventoryType.HOPPER -> 5
        InventoryType.SHULKER_BOX -> 9
        InventoryType.BARREL -> 9
        InventoryType.BLAST_FURNACE -> 3
        InventoryType.LECTERN -> 1
        InventoryType.SMOKER -> 3
        InventoryType.LOOM -> 4
        InventoryType.CARTOGRAPHY -> 3
        InventoryType.GRINDSTONE -> 3
        InventoryType.STONECUTTER -> 2
        else -> 1
    }
}

fun Inventory.getWidth() : Int = this.type.getWidth()

fun Inventory.getHeight() : Int = this.size / this.getWidth()
