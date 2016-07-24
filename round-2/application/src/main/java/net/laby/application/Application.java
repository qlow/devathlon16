package net.laby.application;

import io.netty.bootstrap.Bootstrap;
import net.laby.handler.DisconnectHandler;
import net.laby.handler.LoginSuccessHandler;
import net.laby.handler.UpdateDaemonsHandler;
import net.laby.handler.UpdateTypesHandler;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketUpdateDaemons;
import net.laby.protocol.packet.PacketUpdateTypes;
import net.laby.utils.Connection;
import net.laby.utils.ConnectionsLoader;
import net.laby.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Application {

    private ConnectionsLoader connectionsLoader = new ConnectionsLoader( new File( "config.json" ) );
    private static Application instance;
    private Bootstrap bootstrap;
    private MainConnectionsFrame mainGUI;
    private TableControlFrame controlTable;

    private PacketUpdateDaemons updateDaemons;
    private PacketUpdateTypes updateTypes;

    public Application() {
        instance = this;
        connectionsLoader.loadConnections();

        mainGUI = MainConnectionsFrame.open();
    }

    public void setControlTable( TableControlFrame cotrolTable ) {
        this.controlTable = cotrolTable;
    }

    public TableControlFrame getControlTable() {
        return controlTable;
    }

    public static Application getInstance() {
        return instance;
    }

    public ConnectionsLoader getConnectionsLoader() {
        return connectionsLoader;
    }

    public MainConnectionsFrame getMainGUI() {
        return mainGUI;
    }

    public static void main( String[] args ) {
        JabyBootstrap.registerHandler(
                DisconnectHandler.class,
                LoginSuccessHandler.class,
                UpdateDaemonsHandler.class,
                UpdateTypesHandler.class );
        new Application();
    }


    public ArrayList<Connection> getConnections() {
        return this.connectionsLoader.getList().getConnections();
    }

    public void disconnect() {
        if ( bootstrap != null ) {
            bootstrap.group().shutdownGracefully();
            JabyBootstrap.getClientHandler().getChannel().close();
        }
    }

    public void connect( Connection connection ) {
        if ( connection == null ) {
            return;
        }
        JabyBootstrap.runClientBootstrap( connection.getAddress(), connection.getPort(), -1, connection.getPassword(),
                PacketLogin.ClientType.APPLICATION, new Consumer<Bootstrap>() {
                    @Override
                    public void accept( Bootstrap bootstrap ) {
                        if ( bootstrap == null ) {
                            Application.this.bootstrap = null;

                            // NOT CONNECTED
                            Utils.showDialog( null, "Connection error", "Cannot connect to " + connection.getAddress(), new ImageIcon( Application.class.getResource( "/assets/connectionError.png" ) ) );
                            return;
                        }

                        Application.this.bootstrap = bootstrap;
                        System.out.println( "[Jaby] Connected to " + connection.getAddress() + ":" + connection.getPort() );
                    }
                } );
    }

    public PacketUpdateDaemons getUpdateDaemons() {
        return updateDaemons;
    }

    public PacketUpdateTypes getUpdateTypes() {
        return updateTypes;
    }

    public void setUpdateDaemons( PacketUpdateDaemons updateDaemons ) {
        this.updateDaemons = updateDaemons;
    }

    public void setUpdateTypes( PacketUpdateTypes updateTypes ) {
        this.updateTypes = updateTypes;
    }
}
