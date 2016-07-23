package net.laby.daemon.task;

import lombok.Getter;
import net.laby.daemon.AvailablePorts;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketExitServer;
import net.laby.protocol.packet.PacketStartServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class ServerStartTask implements Runnable {

    private String type;
    private UUID uuid;
    @Getter
    private Process process;

    public ServerStartTask( String type ) {
        this.type = type;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void run() {
        // Getting available port
        int port = AvailablePorts.getAvailablePort();

        // Generating server-folder for this server
        File serverFolder = new File( JabyDaemon.getInstance().getServerFolder(), type + "-" + uuid );

        // Copying server-template to server
        try {
            FileUtils.copyDirectory( new File( JabyDaemon.getInstance().getServerTemplateFolder(), type ), serverFolder );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Getting server-start script
        File serverStartScript = new File( serverFolder, JabyDaemon.getInstance().getStartScriptName() );

        // Starting with ProcessBuilder & Process
        ProcessBuilder processBuilder = new ProcessBuilder( serverStartScript.getAbsolutePath(), "--port", String.valueOf( port ) );

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

        // Waiting for exit
        try {
            this.process.waitFor();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        // Removing folder
        try {
            FileUtils.deleteDirectory( serverFolder );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Sending packet
        JabyBootstrap.getClientHandler().sendPacket( new PacketExitServer( uuid, type ) );

        // Log message
        System.out.println( "[Jaby] Server " + uuid.toString() + " exited!" );
    }

    public UUID getUuid() {
        return uuid;
    }
}
