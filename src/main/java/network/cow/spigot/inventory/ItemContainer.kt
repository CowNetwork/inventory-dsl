package network.cow.spigot.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author Benedikt Wüller
 */
interface ItemContainer {

    fun item(slot: Int, item: InventoryItem) : InventoryItem

    fun item(slot: Int, item: ItemStack) = this.item(slot, item.withoutAction())

    fun item(slot: Int, provider: () -> ItemStack) = this.item(slot, provider())

    fun item(slot: Int) : InventoryItem = this.item(slot, InventoryItem())

    fun itemFromLast(slot: Int, item: InventoryItem) : InventoryItem

    fun itemFromLast(slot: Int, item: ItemStack) : InventoryItem = this.itemFromLast(slot, item.withoutAction())

    fun itemFromLast(slot: Int, provider: () -> ItemStack) = this.itemFromLast(slot, provider())

    fun itemFromLast(slot: Int) : InventoryItem = this.itemFromLast(slot, InventoryItem())

    fun empty(slot: Int) {
        this.item(slot) { ItemStack(Material.AIR) }
    }

    fun fill(item: InventoryItem) : InventoryItem

    fun fill(item: ItemStack) : InventoryItem = this.fill(item.withoutAction())

    fun fill(provider: () -> ItemStack) = this.fill(provider())

    fun fill() : InventoryItem = this.fill(InventoryItem())

    fun clear() {
        this.fill { ItemStack(Material.AIR) }
    }

}
