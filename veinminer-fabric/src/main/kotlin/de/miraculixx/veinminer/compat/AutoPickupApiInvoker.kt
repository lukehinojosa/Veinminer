package de.miraculixx.veinminer.compat

import com.lukehinojosa.autopickup.AutoPickupApi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

/**
 * This class isolates the direct calls to the AutoPickup API.
 * It should ONLY be referenced if FabricLoader.getInstance().isModLoaded("auto-pickup") is true.
 * This prevents a NoClassDefFoundError if AutoPickup is not installed.
 */
object AutoPickupApiInvoker {
    fun tryPickup(player: Player, drops: List<ItemStack>): List<ItemStack> {
        // This is a direct, "unsafe" call that is protected by a runtime check.
        return AutoPickupApi.tryPickup(player, drops)
    }
}