package network.cow.spigot.inventory

import net.kyori.adventure.text.Component
import network.cow.messages.adventure.comp
import network.cow.messages.adventure.formatToComponent
import network.cow.messages.adventure.highlight
import network.cow.messages.adventure.info
import network.cow.spigot.extensions.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import java.awt.Dimension
import java.awt.Point
import kotlin.math.ceil

/**
 * @author Benedikt Wüller
 */
abstract class PagedInventoryMenu : InventoryMenu {

    companion object {
        private val DEFAULT_BORDER_ITEM = ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(Component.empty()).build()
    }

    private val itemCount: Int

    val pageSize: Int = this.inventory.size - (this.width * 2) - (this.height - 2) * 2
    val pageCount: Int get() = ceil(this.itemCount / this.pageSize.toDouble()).toInt()

    var currentPageIndex: Int = 0
        set(value) {
            val cappedValue = maxOf(minOf(value, this.pageCount - 1), 0)
            if (field == cappedValue) return
            field = cappedValue
            this.drawPage(cappedValue)
        }

    constructor(owner: Player, size: Int, title: Component, init: (InventorySection.() -> Unit)? = null) : super(owner, size, title, init)
    constructor(owner: Player, size: Int, init: (InventorySection.() -> Unit)? = null) : super(owner, size, init)

    init {
        this.update {
            val borderItem = getBorderItem()
            border { fill(borderItem) }
        }
        this.itemCount = this.getItemCount()
        this.drawPage(this.currentPageIndex)
    }

    private fun drawPage(index: Int) {
        this.update {
            section(Point(1, 1), Dimension(this.width - 2, this.height - 2))  {
                clear()

                val indexFrom = index * pageSize
                val indexTo = minOf(indexFrom + pageSize, itemCount) - 1

                (indexFrom..indexTo).forEach { i ->
                    this@section.item(i - indexFrom, getItem(player, i))
                }
            }

            // TODO: use translations
            lastRow {
                item(3) { ItemBuilder(Material.ARROW).name("Previous Page".info()).build() } withAction { _, _ ->
                    currentPageIndex--
                    index != currentPageIndex
                }

                item(4, ItemBuilder(Material.MAP).name("Page %1\$s/%2\$s".formatToComponent(
                    (index + 1).toString().highlight(),
                    pageCount.toString().comp()
                ).info()).build())

                item(5) { ItemBuilder(Material.ARROW).name("Next Page".info()).build() } withAction { _, _ ->
                    currentPageIndex++
                    index != currentPageIndex
                }
            }
        }
    }

    fun updateItem(index: Int) {
        val page = index / pageSize
        if (page != this.currentPageIndex) return
        this.update {
            section(Point(1, 1), Dimension(this.width - 2, this.height - 2)) {
                val relativeIndex = index % pageSize
                item(relativeIndex, getItem(player, index))
            }
        }
    }

    protected abstract fun getItem(player: Player, index: Int) : InventoryItem

    protected abstract fun getItemCount() : Int

    open fun getBorderItem() = DEFAULT_BORDER_ITEM

}
