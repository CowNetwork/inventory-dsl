package network.cow.spigot.inventory

import net.kyori.adventure.text.Component
import network.cow.spigot.extensions.state.clearState
import network.cow.spigot.extensions.state.getState
import network.cow.spigot.extensions.state.setState
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Dimension
import java.awt.Point

/**
 * @author Benedikt Wüller
 */
open class InventoryMenu {

    companion object {
        const val STATE_KEY_CURRENT_INVENTORY = "current_inventory"
    }

    var parent: InventoryMenu? = null

    val inventory: Inventory
    val player: Player get() = this.inventory.holder!! as Player
    val items = mutableMapOf<Point, InventoryItem>()

    val width: Int get() = this.inventory.getWidth()
    val height: Int get() = this.inventory.getHeight()

    var isSilent: Boolean = false

    constructor(owner: Player, type: InventoryType, title: Component, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = Bukkit.createInventory(owner, type, title)
        this.init(init)
    }

    constructor(owner: Player, type: InventoryType, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = Bukkit.createInventory(owner, type)
        this.init(init)
    }

    constructor(owner: Player, size: Int, title: Component, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = Bukkit.createInventory(owner, size, title)
        this.init(init)
    }

    constructor(owner: Player, size: Int, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = Bukkit.createInventory(owner, size)
        this.init(init)
    }

    constructor(owner: Player, inventory: Inventory, title: Component, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = when (inventory.type) {
            InventoryType.CHEST -> Bukkit.createInventory(owner, inventory.size, title)
            else -> Bukkit.createInventory(owner, inventory.size, title)
        }
        this.init(init, inventory)
    }

    constructor(owner: Player, inventory: Inventory, init: (InventorySection.() -> Unit)? = null) {
        this.inventory = when (inventory.type) {
            InventoryType.CHEST -> Bukkit.createInventory(owner, inventory.size)
            else -> Bukkit.createInventory(owner, inventory.size)
        }
        this.init(init, inventory)
    }

    constructor(owner: Player, view: InventoryView, title: Component, init: (InventorySection.() -> Unit)? = null) : this(owner, view.topInventory, title, init)

    constructor(owner: Player, view: InventoryView, init: (InventorySection.() -> Unit)? = null) : this(owner, view.topInventory, view.title(), init)

    private fun init(init: (InventorySection.() -> Unit)? = null, inventory: Inventory? = null) {
        inventory?.let {
            this.update {
                repeat(inventory.size) { index ->
                    val itemStack = inventory.getItem(index) ?: return@repeat
                    item(index) { itemStack }
                }
            }
        }

        init?.let { this.update(init) }
    }

    fun update(init: InventorySection.() -> Unit) {
        val section = InventorySection(Point(0, 0), Point(this.width - 1, this.height - 1))
        section.init()

        val items = section.getItems()
        this.items.putAll(items)

        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(InventoryDslPlugin::class.java), Runnable {
            items.forEach { entry ->
                val slot = entry.key.y * this.width + entry.key.x
                this.inventory.setItem(slot, entry.value.itemStack)
            }
        })
    }

    fun getSection(from: Point, to: Point) : InventorySection {
        if (!this.isInBounds(from)) throw IllegalArgumentException("The 'from' position ($from) is out of bounds.")
        if (!this.isInBounds(to)) throw IllegalArgumentException("The 'to' position ($to) is out of bounds.")

        val section = InventorySection(from, to)
        this.items.forEach { entry ->
            val point = entry.key
            point.isInBounds(from, to)
            section.item(this.getSlot(point), entry.value)
        }
        return section
    }

    fun getSection(from: Point, dimensions: Dimension) = this.getSection(from, Point(from.x + dimensions.width - 1, from.y + dimensions.height - 1))

    fun open(flushHistory: Boolean = true) {
        if (flushHistory) {
            this.player.clearState(InventoryDslPlugin::class.java, STATE_KEY_CURRENT_INVENTORY)
            this.clearHistory()
        }

        this.parent = this.player.getState(InventoryDslPlugin::class.java, STATE_KEY_CURRENT_INVENTORY)
        this.player.setState(InventoryDslPlugin::class.java, STATE_KEY_CURRENT_INVENTORY, this)

        if (this.player.openInventory == this.inventory) return
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(InventoryDslPlugin::class.java), Runnable {
            this.player.openInventory(this.inventory)
        })
    }

    fun close() {
        if (this.player.openInventory.topInventory != this.inventory) return
        this.player.clearState(InventoryDslPlugin::class.java, STATE_KEY_CURRENT_INVENTORY)
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(InventoryDslPlugin::class.java), Runnable {
            this.player.closeInventory()
        })
    }

    fun click(slot: Int) {
        val point = Point(
            slot % this.width,
            slot / this.width
        )

        val item = this.items[point] ?: return
        val action = item.action ?: return

        val result = action(this, this.player)
        if (this.isSilent) return

        when {
            result -> this.player.playSound(this.player.location, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F)
            else -> this.player.playSound(this.player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
        }
    }

    fun clearHistory() {
        this.parent?.clearHistory()
        this.parent = null
    }

    fun silent(silent: Boolean = true) {
        this.isSilent = silent
    }

    fun getSlot(x: Int, y: Int) = y * this.width + x

    fun getSlot(position: Point) = this.getSlot(position.x, position.y)

    fun getPosition(slot: Int) = Point(
        slot % this.width,
        slot / this.width
    )

    fun isInBounds(point: Point) : Boolean {
        if (point.x < 0 || point.x >= this.width) return false
        if (point.y < 0 || point.y >= this.height) return false
        return true
    }

}
