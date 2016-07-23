package net.laby.daemon;

import io.netty.bootstrap.Bootstrap;
import net.laby.daemon.utils.ConfigManager;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketPowerUsage;

import java.io.File;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class JabyDaemon {

    private static JabyDaemon instance;
    private ConfigManager<DaemonConfig> configManager;

    public JabyDaemon() {
        instance = this;

        init();
    }

    private void init() {
        this.configManager = new ConfigManager<>( new File( "config.json" ), DaemonConfig.class );

        JabyBootstrap.runClientBootstrap( getConfig().getAddress(), getConfig().getPort(), getConfig().getMaxRamUsage(), getConfig().getPassword(),
                PacketLogin.ClientType.DAEMON, new Consumer<Bootstrap>() {
                    @Override
                    public void accept( Bootstrap bootstrap ) {
                        if(bootstrap == null) {
                            System.err.println( "[Jaby] Failed connecting to " + getConfig().getAddress() + ":" + getConfig().getPort() + "!" );
                            System.exit( 0 );
                            return;
                        }

                        System.out.println( "[Jaby] Connected to " + getConfig().getAddress() + ":" + getConfig().getPort() + "!" );

                        JabyBootstrap.getExecutorService().submit( new Runnable() {
                            @Override
                            public void run() {
                                while ( true ) {
                                    try {
                                        Thread.sleep( 2000L );
                                    } catch ( InterruptedException e ) {
                                        e.printStackTrace();
                                    }

                                    JabyBootstrap.getClientHandler().sendPacket( new PacketPowerUsage( getCurrentRamUsage() ) );
                                }
                            }
                        } );
                    }
                } );
    }

    private byte getCurrentRamUsage() {
        com.sun.management.OperatingSystemMXBean bean =
                ( com.sun.management.OperatingSystemMXBean )
                        java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        long total = bean.getTotalPhysicalMemorySize() / 1024;
        long used = total - (bean.getFreePhysicalMemorySize() / 1024);

        return ( byte ) (100D / total * used);
    }

    public DaemonConfig getConfig() {
        return configManager.getSettings();
    }

    public ConfigManager<DaemonConfig> getConfigManager() {
        return configManager;
    }

    public static JabyDaemon getInstance() {
        return instance;
    }

    public static void main( String[] args ) {
        new JabyDaemon();
    }

}
