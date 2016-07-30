package net.laby.ship;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Class created by LabyStudio
 */
public class ArmorStandBlock {

    private ArmorStand armorStand;
    private Material blockMaterial;
    private byte blockDataId;

    private Location blockAtShipLocation;

    public ArmorStandBlock(Location blockAtShopLocation, Material material, int data) {
        this.armorStand = ( ArmorStand ) blockAtShopLocation.getWorld().spawnEntity(  new Location(blockAtShopLocation.getWorld(), 0, 0, 0), EntityType.ARMOR_STAND );
        this.armorStand.setVisible( false );
        this.armorStand.setGravity( false );
        this.armorStand.setHelmet( new ItemStack( material, 1, (byte) data ) );

        this.blockMaterial = material;
        this.blockDataId = (byte) data;

        this.blockAtShipLocation = blockAtShopLocation;
    }

    public Location getBlockAtShipLocation( ) {
        return blockAtShipLocation;
    }

    public ArmorStand getArmorStand( ) {
        return armorStand;
    }

    public Material getBlockMaterial( ) {
        return blockMaterial;
    }

    public byte getBlockDataId( ) {
        return blockDataId;
    }

    public void setBlockMaterial( Material blockMaterial ) {
        this.blockMaterial = blockMaterial;
    }


    public void setBlockDataId( byte blockDataId ) {
        this.blockDataId = blockDataId;
    }

    public void updatePosition(Location location) {
        this.armorStand.teleport( location.add( this.blockAtShipLocation ) );
    }

    public void setVector(Vector vector) {
        this.armorStand.setVelocity( vector );
    }
}
