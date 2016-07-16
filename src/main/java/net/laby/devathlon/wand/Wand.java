package net.laby.devathlon.wand;

import net.laby.devathlon.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Class created by qlow | Jan
 * Represents a wand
 */
public abstract class Wand {

    private Player player;
    private long lastInteract;
    private boolean rightClicking;

    private ItemStack item;

    private Material wandMaterial;
    private short wandData;

    private String name;
    private String[] lore;

    /**
     * Constructor of a wand
     *
     * @param player       player that uses the wand
     * @param wandMaterial item's material
     * @param wandData     item's data
     * @param name         item's name
     * @param lore         item's lore
     */
    public Wand( Player player, Material wandMaterial, int wandData, String name, String... lore ) {
        this.player = player;

        this.wandMaterial = wandMaterial;
        this.wandData = ( short ) wandData;

        this.name = name;
        this.lore = lore;

        this.item = toItem();
    }

    public Wand( Material wandMaterial, byte wandData, String name, String... lore ) {
        this( null, wandMaterial, wandData, name, lore );
    }

    /**
     * Converts the arguments (given in the constructor) to an item
     *
     * @return ItemStack with  wand's name, lore, material, data
     */
    private ItemStack toItem() {
        // Creating item
        ItemStack item = new ItemStack( wandMaterial, 1, wandData );

        // Modifying ItemMeta
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName( this.name );
        itemMeta.setLore( Arrays.asList( this.lore ) );

        // Setting ItemMeta
        item.setItemMeta( itemMeta );

        // Adding enchantment-glow effect
        item = ItemUtils.addGlow( item );

        return item;
    }

    /**
     * Called when the player switches to the item
     */
    public void onEnable() {
    }

    /**
     * Called when the player switches to an other item
     */
    public void onDisable() {
    }

    /**
     * Called when the wand gets right-clicked
     */
    public void onRightClick() {
    }

    /**
     * Called when the wand gets left-clicked
     */
    public void onLeftClick() {
    }

    /**
     * Called when the player releases his right mouse button
     */
    public void onRightClickRelease() {
    }

    /**
     * Called every tick
     */
    public void onTick() {
    }

    /**
     * Name of the wand
     *
     * @return wand's item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Lore of the wand-item
     *
     * @return wand's item's lore
     */
    public String[] getLore() {
        return lore;
    }

    /**
     * The material of the wand
     *
     * @return wand's item's material
     */
    public Material getWandMaterial() {
        return wandMaterial;
    }

    /**
     * The material-data of the wand
     *
     * @return wand's item's data
     */
    public short getWandData() {
        return wandData;
    }

    /**
     * The wand's item
     *
     * @return item initialized in {@link Wand#toItem()}
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Player that uses the wand
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Timestamp of the time the player interacted with the wand the last time
     *
     * @return timestamp in long
     */
    public long getLastInteract() {
        return lastInteract;
    }

    public void setLastInteract( long lastInteract ) {
        this.lastInteract = lastInteract;
    }

    /**
     * State of right-clicking
     *
     * @return true if the player is rightclicking the wand
     */
    public boolean isRightClicking() {
        return rightClicking;
    }

    public void setRightClicking( boolean rightClicking ) {
        this.rightClicking = rightClicking;
    }
}