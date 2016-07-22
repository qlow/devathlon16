package net.laby.devathlon.utils;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagInt;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Class created by qlow | Jan
 */
public class ItemUtils {

    /**
     * Adds a glow-effect to an item
     * (like enchantment, but without an enchantment)
     * @param is
     * @return
     */
    public static ItemStack addGlow( ItemStack is ) {
        // Adding random enchantment
        is.addUnsafeEnchantment( Enchantment.ARROW_DAMAGE, 1 );

        // Getting nms-stack by bukkit-stack
        net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy( is );

        // Adding Hideflag, so you can't see the enchantment
        NBTTagCompound tag = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        tag.set( "HideFlags", new NBTTagInt( 1 ) );
        nmsStack.setTag( tag );

        // Returning itemstack with glow
        return CraftItemStack.asCraftMirror( nmsStack );
    }

}
