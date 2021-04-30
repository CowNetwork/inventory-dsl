package network.cow.spigot.inventory

import com.google.common.base.Preconditions
import java.awt.Dimension
import java.awt.Point

/**
 * @author Benedikt Wüller
 */
open class InventorySection(val from: Point, val to: Point) : ItemContainer, InventoryComponent {

    // TODO: slot out of bounds checks

    private val components = mutableListOf<InventoryComponent>()

    val width = (to.x - from.x) + 1
    val height = (to.y - from.y) + 1

    fun section(section: InventorySection) {
        if (!this.isInBounds(section.from)) throw IllegalArgumentException("The 'from' position (${section.from}) is out of bounds.")
        if (!this.isInBounds(section.to)) throw IllegalArgumentException("The 'to' position (${section.to}) is out of bounds.")

        this.components.add(section)
    }

    fun section(from: Point, to: Point, init: InventorySection.() -> Unit) {
        if (!this.isInBounds(from)) throw IllegalArgumentException("The 'from' position ($from) is out of bounds.")
        if (!this.isInBounds(to)) throw IllegalArgumentException("The 'to' position ($to) is out of bounds.")

        val section = InventorySection(from, to)
        section.init()
        this.components.add(section)
    }

    fun section(from: Point, dimensions: Dimension, init: InventorySection.() -> Unit) {
        this.section(from, Point(from.x + dimensions.width - 1, from.y + dimensions.height - 1), init)
    }

    fun border(init: InventorySection.() -> Unit) {
        this.firstRow(init)
        this.lastRow(init)
        this.firstColumn(init)
        this.lastColumn(init)
    }

    fun row(row: Int, init: InventorySection.() -> Unit) = this.section(Point(0, row), Point(this.to.x, row), init)

    fun rowFromLast(row: Int, init: InventorySection.() -> Unit) = this.row(this.to.y - row, init)

    fun firstRow(init: InventorySection.() -> Unit) = this.row(0, init)

    fun lastRow(init: InventorySection.() -> Unit) = this.rowFromLast(0, init)

    fun column(column: Int, init: InventorySection.() -> Unit) = this.section(Point(column, 0), Point(column, this.to.y), init)

    fun columnFromLast(column: Int, init: InventorySection.() -> Unit) = this.column(this.to.x - column, init)

    fun firstColumn(init: InventorySection.() -> Unit) = this.column(0, init)

    fun lastColumn(init: InventorySection.() -> Unit) = this.columnFromLast(0, init)

    override fun item(slot: Int, item: InventoryItem) : InventoryItem {
        if (!this.isInBounds(slot)) throw IllegalArgumentException("The slot ($slot) is out of bounds.")
        this.components.add(InventoryItemComponent(this.getPosition(slot), item))
        return item
    }

    override fun itemFromLast(slot: Int, item: InventoryItem) = this.item(this.getSlot(this.width - 1, this.height - 1) - slot, item)

    override fun fill(item: InventoryItem) : InventoryItem {
        for (y in 0 until this.height) {
            for (x in 0 until this.width) {
                this.item(this.getSlot(x, y), item)
            }
        }
        return item
    }

    fun getSlot(x: Int, y: Int) = y * this.width + x

    fun getSlot(position: Point) = this.getSlot(position.x, position.y)

    fun getPosition(slot: Int) = Point(
        slot % this.width,
        slot / this.width
    )

    fun isInBounds(slot: Int) : Boolean {
        if (slot < 0) return false
        if (slot > this.getSlot(this.width - 1, this.height - 1)) return false
        return true
    }

    fun isInBounds(point: Point) : Boolean {
        if (point.x < 0 || point.x >= this.width) return false
        if (point.y < 0 || point.y >= this.height) return false
        return true
    }

    fun getSubSection(from: Point, to: Point) : InventorySection {
        val section = InventorySection(from, to)
        this.getItems().forEach { entry ->
            val point = entry.key
            point.isInBounds(from, to)
            section.item(this.getSlot(point), entry.value)
        }
        return section
    }

    override fun getItems(): Map<Point, InventoryItem> {
        val items = mutableMapOf<Point, InventoryItem>()
        this.components.forEach { component ->
            items.putAll(component.getItems().mapKeys { entry ->
                val point = entry.key
                return@mapKeys when (component) {
                    is InventorySection -> Point(
                        component.from.x + point.x,
                        component.from.y + point.y
                    )
                    else -> point
                }
            })
        }
        return items
    }

}
