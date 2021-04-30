package network.cow.spigot.inventory

import java.awt.Point

/**
 * @author Benedikt Wüller
 */

fun Point.isInBounds(from: Point, to: Point) : Boolean {
    if (this.x < from.x || this.x > to.x) return false
    if (this.y < from.y || this.y >= to.y) return false
    return true
}
