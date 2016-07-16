package net.laby.devathlon.wand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class PortalWand extends Wand {

    private Player player;
    private List<BlockState> oldBlocks = new ArrayList<>();

    private static int counter = 1337 * 9;

    public PortalWand( Player player ) {
        super( player, Material.STICK, 0, "§9Portal§7-§cStab", "", "§7-> Mit Rechts & Linksklick kannst du", "  §7Portale setzen, durch die", "  §7man laufen kann.", "" );

        this.player = player;
    }

    public PortalWand() {
        this( null );
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRightClick() {
        makePortal( false );
    }

    @Override
    public void onLeftClick() {
        makePortal( true );
    }

    private void makePortal( boolean left ) {
        List<Block> blocks = player.getLineOfSight( new HashSet<Material>(), 50 );

        for ( Block block : blocks ) {
            Location location = block.getLocation();

            if ( block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER ) {
                player.playEffect( location, Effect.MOBSPAWNER_FLAMES, 0 );
                continue;
            }
        }
    }

    private void sendDefaultBlocks() {
        for ( BlockState oldBlock : oldBlocks ) {
            oldBlock.update( true );
        }
    }

}
