package net.laby.daemon;

import io.netty.bootstrap.Bootstrap;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import net.laby.daemon.task.PowerUsageTask;
import net.laby.daemon.utils.ConfigManager;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketLogin;

import java.io.File;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class JabyDaemon {

    @Getter
    private static JabyDaemon instance;

    @Getter
    private boolean connected;

    @Getter
    private ConfigManager<DaemonConfig> configManager;
    @Getter
    private File serverTemplateFolder;
    @Getter
    private File serverFolder;
    @Getter
    private String startScriptName;

    public JabyDaemon() {
        instance = this;

        init();
    }

    /**
     * Inits the daemon
     */
    private void init() {
        this.configManager = new ConfigManager<>( new File( "config.json" ), DaemonConfig.class );
        this.serverTemplateFolder = new File( getConfig().getServerTemplateFolder() );
        this.serverFolder = new File( getConfig().getServerFolder() );
        this.startScriptName = getConfig().getStartScriptName() + (PlatformDependent.isWindows() ? ".bat" : ".sh");

        if ( !serverTemplateFolder.exists() ) {
            serverTemplateFolder.mkdirs();
        }

        // Creating new server-folder if it doesn't exist
        if ( !serverFolder.exists() ) {
            serverFolder.mkdirs();
        }

        connect();
    }

    /**
     * Connects to the server set in the config
     */
    private void connect() {
        JabyBootstrap.runClientBootstrap( getConfig().getAddress(), getConfig().getPort(), getConfig().getMaxRamUsage(), getConfig().getPassword(),
                PacketLogin.ClientType.DAEMON, new Consumer<Bootstrap>() {
                    @Override
                    public void accept( Bootstrap bootstrap ) {
                        if ( bootstrap == null ) {
                            JabyDaemon.this.connected = false;
                            System.err.println( "[Jaby] Failed connecting to " + getConfig().getAddress() + ":" + getConfig().getPort() + "!" );
                            System.out.println( "[Jaby] Attempting connecting again in 10 seconds..." );

                            try {
                                Thread.sleep( 10000L );
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
                            }

                            connect();
                            return;
                        }

                        JabyDaemon.this.connected = true;
                        System.out.println( "[Jaby] Connected to " + getConfig().getAddress() + ":" + getConfig().getPort() + "!" );

                        JabyBootstrap.getExecutorService().submit( new PowerUsageTask() );
                    }
                } );
    }

    public DaemonConfig getConfig() {
        return configManager.getSettings();
    }

    public static void main( String[] args ) {
        new JabyDaemon();
    }

}
