package network.cow.spigot.inventory

import java.awt.Point

/**
 * @author Benedikt Wüller
 */
interface InventoryComponent {

    fun getItems(): Map<Point, InventoryItem>

}
