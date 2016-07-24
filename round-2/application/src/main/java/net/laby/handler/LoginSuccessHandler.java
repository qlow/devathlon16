package net.laby.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.laby.application.Application;
import net.laby.application.TableControlFrame;
import net.laby.protocol.packet.PacketLoginSuccessful;

/**
 * Class created by LabyStudio
 */
public class LoginSuccessHandler {

    public static void handle( PacketLoginSuccessful successful, ChannelHandlerContext ctx ) {

        ctx.channel().closeFuture().addListener( new ChannelFutureListener() {
            @Override
            public void operationComplete( ChannelFuture future ) throws Exception {
                Application.getInstance().getMainGUI().setVisible( true );
                Application.getInstance().getControlTable().dispose();
            }
        } );

        Application.getInstance().getMainGUI().setVisible( false );
        Application.getInstance().setControlTable(TableControlFrame.open());
    }

}
