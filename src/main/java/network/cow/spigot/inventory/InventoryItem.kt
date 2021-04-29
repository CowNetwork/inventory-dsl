package network.cow.spigot.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Benedikt Wüller
 */
data class InventoryItem(var itemStack: ItemStack = ItemStack(Material.AIR), var action: ((InventoryMenu, Player) -> Boolean)? = null) {

    infix fun withStack(itemStack: ItemStack) : InventoryItem {
        this.itemStack = itemStack
        return this
    }

    infix fun withAction(action: ((InventoryMenu, Player) -> Boolean)?) : InventoryItem {
        this.action = action
        return this
    }

    fun withoutAction() : InventoryItem {
        this.action = null
        return this
    }

}

infix fun ItemStack.withAction(action: ((InventoryMenu, Player) -> Boolean)?) = InventoryItem(this, action)

fun ItemStack.withoutAction() = InventoryItem(this)
