package net.laby.daemon.task;

import lombok.Getter;
import net.laby.daemon.AvailablePorts;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketExitServer;
import net.laby.protocol.packet.PacketServerDone;
import net.laby.protocol.packet.PacketStartServer;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class ServerStartTask implements Runnable {

    @Getter
    private String type;
    private UUID uuid;
    @Getter
    private Process process;
    @Getter
    private int port;

    private boolean isDone;

    public ServerStartTask( String type ) {
        this.type = type;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void run() {
        // Getting available port
        this.port = AvailablePorts.getAvailablePort();

        // Generating server-folder for this server
        File serverFolder = new File( JabyDaemon.getInstance().getServerFolder(), type + "-" + uuid );

        // Template-folder
        File templateFolder = new File( JabyDaemon.getInstance().getServerTemplateFolder(), type );

        // Copying server-template to server
        copyDirectoryWithPermissions( templateFolder, serverFolder );

        // Starting with ProcessBuilder & Process
        ProcessBuilder processBuilder = new ProcessBuilder( JabyDaemon.getInstance().getStartScriptName(), "-p", String.valueOf( port ) );
        processBuilder.directory( serverFolder );

        try {
            this.process = processBuilder.start();
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }

        // Adding server to started servers
        JabyDaemon.getInstance().getStartedServers().put( uuid, this );

        // Sending packet
        JabyBootstrap.getClientHandler().sendPacket( new PacketStartServer( this.type, this.uuid, port ) );

        // Log message
        System.out.println( "[Jaby] Started " + type + " with port " + port + " (" + uuid.toString() + ")" );

        // Log messages for the server
        JabyBootstrap.getExecutorService().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

                    String line;

                    while ( (line = reader.readLine()) != null ) {
                        if ( isDone )
                            continue;

                        if ( !line.contains( "Done" ) || !line.contains( "For help, type \"help\" or" ) )
                            continue;

                        isDone = true;
                        JabyBootstrap.getClientHandler().sendPacket( new PacketServerDone( type, uuid ) );
                        System.out.println( "[Jaby] Server " + type.toString() + " (" + uuid.toString() + ") is done!" );
                    }
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        } );

        // Waiting for exit
        try {
            this.process.waitFor();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        if ( JabyDaemon.getInstance().getCopyTypes().contains( type ) ) {
            try {
                FileUtils.deleteDirectory( templateFolder );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            copyDirectoryWithPermissions( serverFolder, templateFolder );
        }

        // Removing folder
        try {
            FileUtils.deleteDirectory( serverFolder );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Sending packet
        JabyBootstrap.getClientHandler().sendPacket( new PacketExitServer( uuid, type, port ) );

        // Removing server from list
        JabyDaemon.getInstance().getStartedServers().remove( this.uuid );

        // Log message
        System.out.println( "[Jaby] Server " + uuid.toString() + " exited!" );
    }

    public UUID getUuid() {
        return uuid;
    }

    private static void copyDirectoryWithPermissions( File src, File dest ) {
        if ( !dest.exists() ) {
            dest.mkdirs();
        }

        for ( File srcFiles : src.listFiles() ) {
            if ( srcFiles.isDirectory() ) {
                copyDirectoryWithPermissions( srcFiles, new File( dest, srcFiles.getName() ) );
                continue;
            }

            try {
                Files.copy( srcFiles.toPath(), new File( dest, srcFiles.getName() ).toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

}
