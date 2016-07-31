package net.laby.game;

import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_10_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Holograms
{
    public static ArrayList<Entity> holograms = new ArrayList<Entity>();
    public static HashMap<UUID, Integer> fakeHolograms = new HashMap<UUID, Integer>();
    public static HashMap<String, Integer> hashHolograms = new HashMap<String, Integer>();

    public static ArmorStand spawnHologram( Location loc, String title)
    {
        ArmorStand armorStand = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(title);
        for (Entity all : armorStand.getWorld().getEntities()) {
            if ((all.getLocation().getX() == loc.getX()) && (all.getLocation().getY() == loc.getY()) && (all.getLocation().getZ() == loc.getZ()) &&
                    (all.getType() != null) && (all.getType() == EntityType.ARMOR_STAND) && (all.getEntityId() != armorStand.getEntityId())) {
                all.remove();
            }
        }
        holograms.add(armorStand);

        return armorStand;
    }

    public static void sendToPlayer(Player p, Location loc, String name)
    {
        if (fakeHolograms.containsKey(p.getUniqueId()))
        {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { ((Integer)fakeHolograms.get(p.getUniqueId())).intValue() });
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            fakeHolograms.remove(p.getUniqueId());
        }
        PacketPlayOutSpawnEntityLiving create = null;
        try
        {
            WorldServer world = ((CraftWorld )p.getWorld()).getHandle();
            EntityArmorStand armor = new EntityArmorStand(world);
            armor.setCustomName(name);
            armor.setInvisible(true);
            armor.setNoGravity(true);
            armor.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0.0F, 0.0F);
            armor.setCustomNameVisible(true);
            create = new PacketPlayOutSpawnEntityLiving(armor);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(create);
            fakeHolograms.put(p.getUniqueId(), armor.getId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendToAll(Location loc, String name, String hash)
    {
        if (hashHolograms.containsKey(hash))
        {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { ((Integer)hashHolograms.get(hash)).intValue() });
            for (Player all : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer)all).getHandle().playerConnection.sendPacket(packet);
            }
            hashHolograms.remove(hash);
        }
        PacketPlayOutSpawnEntityLiving create = null;
        try
        {
            WorldServer world = ((CraftWorld)loc.getWorld()).getHandle();
            EntityArmorStand armor = new EntityArmorStand(world);
            armor.setCustomName(name);
            armor.setInvisible(true);
            armor.setNoGravity(true);
            armor.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0.0F, 0.0F);
            armor.setCustomNameVisible(true);
            create = new PacketPlayOutSpawnEntityLiving(armor);
            for (Player all : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer )all).getHandle().playerConnection.sendPacket(create);
            }
            hashHolograms.put(hash, armor.getId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sendToPlayerUnsafe(Player p, Location loc, String name)
    {
        PacketPlayOutSpawnEntityLiving create = null;
        try
        {
            WorldServer world = ((CraftWorld)p.getWorld()).getHandle();
            EntityArmorStand armor = new EntityArmorStand(world);
            armor.setCustomName(name);
            armor.setInvisible(true);
            armor.setNoGravity(true);
            armor.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0.0F, 0.0F);
            armor.setCustomNameVisible(true);
            create = new PacketPlayOutSpawnEntityLiving(armor);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(create);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void disable()
    {
        for (Entity all : holograms) {
            if (all != null) {
                all.remove();
            }
        }
        for (UUID uuid : fakeHolograms.keySet())
        {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
            {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { ((Integer)fakeHolograms.get(p.getUniqueId())).intValue() });
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            }
        }
        fakeHolograms.clear();
    }
}
