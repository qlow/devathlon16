package net.laby.daemon;

import io.netty.bootstrap.Bootstrap;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.Setter;
import net.laby.daemon.handler.DisconnectHandler;
import net.laby.daemon.handler.LoginSuccessfulHanndler;
import net.laby.daemon.handler.ServerRequestHandler;
import net.laby.daemon.handler.ServerShutdownRequestHandler;
import net.laby.daemon.task.PowerUsageTask;
import net.laby.daemon.task.QueueStartTask;
import net.laby.daemon.task.ServerStartTask;
import net.laby.daemon.utils.ConfigManager;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketExitServer;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.utils.JabyUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class JabyDaemon {

    @Getter
    private static JabyDaemon instance;

    @Getter
    @Setter
    private boolean connected;
    @Getter
    private boolean disabling;
    @Getter
    @Setter
    private boolean loggedIn;

    private Bootstrap bootstrap;

    @Getter
    private ConfigManager<DaemonConfig> configManager;
    @Getter
    private File serverTemplateFolder;
    @Getter
    private File serverFolder;
    @Getter
    private String startScriptName;
    @Getter
    private String[] availableTypes;
    @Getter
    private Map<UUID, ServerStartTask> startedServers = new HashMap<>();
    @Getter
    private QueueStartTask queueStartTask;

    public JabyDaemon() {
        instance = this;

        init();
    }

    /**
     * Inits the daemon
     */
    private void init() {
        // Starting new command thread
        JabyBootstrap.getExecutorService().execute( new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner( System.in );

                String readLine;

                while ( (readLine = scanner.nextLine()) != null ) {
                    if ( readLine.equals( "stop" ) ) {
                        disable();
                    }
                }
            }
        } );

        this.configManager = new ConfigManager<>( new File( "config.json" ), DaemonConfig.class );
        this.serverTemplateFolder = new File( getConfig().getServerTemplateFolder() );
        this.serverFolder = new File( getConfig().getServerFolder() );
        this.startScriptName = getConfig().getStartScriptName() + (PlatformDependent.isWindows() ? ".bat" : ".sh");

        if ( !serverTemplateFolder.exists() ) {
            serverTemplateFolder.mkdirs();
        } else {
            List<String> availableTypes = new ArrayList<>();

            for ( File templateFolder : serverTemplateFolder.listFiles() ) {
                if ( !templateFolder.isDirectory() )
                    continue;

                availableTypes.add( templateFolder.getName() );
            }

            this.availableTypes = new String[availableTypes.size()];

            int i = 0;
            for ( String availableType : availableTypes ) {
                this.availableTypes[i] = availableType;

                i++;
            }
        }

        // Creating new server-folder if it doesn't exist
        if ( !serverFolder.exists() ) {
            serverFolder.mkdirs();
        }

        for ( File serverFile : serverFolder.listFiles() ) {
            if ( !serverFile.isDirectory() )
                continue;

            try {
                FileUtils.deleteDirectory( serverFile );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        JabyBootstrap.registerHandler(
                DisconnectHandler.class,
                LoginSuccessfulHanndler.class,
                ServerRequestHandler.class,
                ServerShutdownRequestHandler.class );

        JabyBootstrap.getExecutorService().execute( (queueStartTask = new QueueStartTask()) );

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
            @Override
            public void run() {
                disable();
            }
        } ) );

        connect();
    }

    /**
     * Connects to the server set in the config
     */
    public void connect() {
        JabyBootstrap.runClientBootstrap( getConfig().getAddress(), getConfig().getPort(), getConfig().getMaxRamUsage(), JabyUtils.convertToMd5( getConfig().getPassword() ),
                PacketLogin.ClientType.DAEMON, new Consumer<Bootstrap>() {
                    @Override
                    public void accept( Bootstrap bootstrap ) {
                        JabyDaemon.this.bootstrap = bootstrap;

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

                        JabyBootstrap.getExecutorService().execute( new PowerUsageTask() );
                    }
                } );
    }

    /**
     * Called when the client gets a disconnect-packet
     */
    public void disable() {
        if ( this.disabling )
            return;

        this.disabling = true;
        System.out.println( "[Jaby] Stopping servers (Disabling daemon in 20 seconds)..." );

        for ( Map.Entry<UUID, ServerStartTask> serverEntry : startedServers.entrySet() ) {
            JabyBootstrap.getClientHandler().sendPacket( new PacketExitServer( serverEntry.getKey(),
                    serverEntry.getValue().getType(), serverEntry.getValue().getPort() ) );
            Process process = serverEntry.getValue().getProcess();

            try {
                process.getOutputStream().write( "stop\n".getBytes() );
                process.getOutputStream().flush();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        // Disabling servers after 1 minute
        JabyBootstrap.getExecutorService().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 1000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                JabyDaemon.this.connected = false;

                if ( bootstrap != null ) {
                    bootstrap.group().shutdownGracefully();
                    JabyBootstrap.getClientHandler().getChannel().close();
                }

                try {
                    Thread.sleep( 20000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                for ( Map.Entry<UUID, ServerStartTask> serverEntry : startedServers.entrySet() ) {
                    Process process = serverEntry.getValue().getProcess();

                    process.destroy();
                }

                System.out.println( "[Jaby] Disabled daemon!" );
                System.exit( 0 );
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
