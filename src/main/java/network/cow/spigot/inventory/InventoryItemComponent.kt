package network.cow.spigot.inventory

import java.awt.Point

/**
 * @author Benedikt Wüller
 */
data class InventoryItemComponent(val point: Point, val item: InventoryItem) : InventoryComponent {

    override fun getItems(): Map<Point, InventoryItem> = mapOf(point to item)

}
