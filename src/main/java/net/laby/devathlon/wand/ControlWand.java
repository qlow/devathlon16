package net.laby.devathlon.wand;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Class created by LabyStudio
 */
public class ControlWand extends Wand {

    private Random random = new Random();
    private long lastTick = 0;
    private Entity controlledEntity = null;

    public ControlWand( Player player ) {
        super( player, Material.BLAZE_ROD, 0, "Control Wand", "" );
    }

    public ControlWand( ) {
        this( null );
    }

    @Override
    public void onTick( ) {
        if ( controlledEntity != null ) {
            if ( lastTick < System.currentTimeMillis() ) {
                lastTick = System.currentTimeMillis() + 500;

            }
        }
    }

    @Override
    public void onEntityInteract( Entity entity ) {
        controlledEntity = entity;
    }

    @Override
    public void onRightClick( ) {
        
    }

    @Override
    public void onRightClickRelease( ) {
        controlledEntity = null;
    }
}
