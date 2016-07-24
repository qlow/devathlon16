package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.packet.PacketCopyMode;

/**
 * Class created by qlow | Jan
 */
public class CopyModeHandler {

    public static void handle( PacketCopyMode copyMode, ChannelHandlerContext ctx ) {
        String type = copyMode.getType();

        if ( copyMode.isCopyMode() && !JabyDaemon.getInstance().getCopyTypes().contains( type ) ) {
            JabyDaemon.getInstance().getCopyTypes().add( type );
        } else if ( !copyMode.isCopyMode() && JabyDaemon.getInstance().getCopyTypes().contains( type ) ) {
            JabyDaemon.getInstance().getCopyTypes().remove( type );
        }
    }

}
