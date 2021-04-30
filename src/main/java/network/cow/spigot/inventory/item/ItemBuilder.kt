package network.cow.spigot.inventory.item

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import java.util.Base64
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
open class ItemBuilder(itemStack: ItemStack) {

    private val itemStack = ItemStack(itemStack)

    constructor(material: Material = Material.AIR, amount: Int = 1) : this(ItemStack(material, amount))

    // GENERIC

    fun material(material: Material) = this.apply { this.itemStack.type = material }

    fun amount(amount: Int) = this.apply { this.itemStack.amount = amount }

    fun name(name: Component) = this.meta<ItemMeta> { this.displayName(name) }

    fun lore(vararg lore: Component) = this.meta<ItemMeta> {
        this.lore(lore.toMutableList())
    }

    fun splitComponent(component: Component, length: Int) : List<TextComponent> {
        if (component !is TextComponent) return emptyList()
        return component.content().chunked(length).map {
            Component.text(it, component.style())
        }
    }

    fun flags(vararg flags: ItemFlag) = this.meta<ItemMeta> {
        this.removeItemFlags(*this.itemFlags.toTypedArray())
        this.addItemFlags(*flags)
    }

    fun unbreakable(flag: Boolean) = this.meta<ItemMeta> { this.isUnbreakable = flag }

    fun attribute(attribute: Attribute, modifier: AttributeModifier) = this.meta<ItemMeta> { this.addAttributeModifier(attribute, modifier) }

    fun customModelData(data: Int?) = this.meta<ItemMeta> { this.setCustomModelData(data) }

    fun enchant(enchantment: Enchantment, level: Int = 1) = this.apply {
        when {
            !enchantment.canEnchantItem(this.itemStack) || level > enchantment.maxLevel -> this.itemStack.addUnsafeEnchantment(enchantment, level)
            else -> this.itemStack.addEnchantment(enchantment, level)
        }
    }

    fun glow() = this.enchant(Enchantment.SOUL_SPEED).flags(ItemFlag.HIDE_ENCHANTS)

    // POTION

    fun potion() = this.apply {
        if (this.itemStack.itemMeta !is PotionMeta) {
            this.material(Material.POTION)
        }
    }

    fun potion(type: PotionType, extended: Boolean = false, upgraded: Boolean = false) = this.potion().meta<PotionMeta> {
        val data = PotionData(type, extended, upgraded)
        this.basePotionData = data
    }

    fun potion(effect: PotionEffect, type: PotionType = PotionType.getByEffect(effect.type) ?: PotionType.AWKWARD) = this.potion(type).meta<PotionMeta> {
        this.addCustomEffect(effect, true)
    }

    fun potion(effectType: PotionEffectType, duration: Int, amplifier: Int) = this.potion(PotionEffect(effectType, duration * 20, amplifier - 1))

    // SKULL

    fun skull() = this.apply {
        if (this.itemStack.itemMeta !is SkullMeta) {
            this.material(Material.PLAYER_HEAD)
        }
    }

    fun skull(profile: PlayerProfile) = this.skull().meta<SkullMeta> {
        this.playerProfile = profile
    }

    fun skull(player: Player) = this.skull(player.playerProfile)

    fun skull(offlinePlayer: OfflinePlayer) = this.skull(Bukkit.createProfile(offlinePlayer.uniqueId, offlinePlayer.name))

    fun skullFromTexture(texture: String) = this.apply {
        val profile = Bukkit.createProfile(UUID.randomUUID(), null)
        profile.properties.add(ProfileProperty("textures", texture))
        this.skull(profile)
    }

    fun skullFromTexturUrl(textureUrl: String) = this.apply {
        val json = "{\"textures\":{\"SKIN\":{\"url\":\"$textureUrl\"}}}"
        val base64 = Base64.getEncoder().encodeToString(json.toByteArray())
        this.skullFromTexture(base64)
    }

    fun skullFromTextureId(textureId: String) = this.skullFromTexturUrl("https://textures.minecraft.net/texture/$textureId")

    // FIREWORKS

    fun firework() = this.apply {
        if (this.itemStack.itemMeta !is FireworkMeta) {
            this.material(Material.FIREWORK_ROCKET)
        }
    }

    fun fireworkPower(power: Int) = this.firework().meta<FireworkMeta> { this.power = power }

    fun fireworkEffects(vararg effects: FireworkEffect) = this.firework().meta<FireworkMeta> {
        this.clearEffects()
        this.addEffects(*effects)
    }

    // UTIL

    fun <T : ItemMeta> meta(init: T.() -> Unit) = this.apply {
        val meta = this.itemStack.itemMeta as T
        meta.init()
        this.itemStack.itemMeta = meta
    }

    fun build() = this.itemStack.clone()

    fun clone() = ItemBuilder(this.itemStack.clone())

}
