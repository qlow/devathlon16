package net.laby.daemon.task;

import net.laby.daemon.AvailablePorts;
import net.laby.daemon.JabyDaemon;
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
    private Process process;

    public ServerStartTask( String type ) {
        this.type = type;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void run() {
        int port = AvailablePorts.getAvailablePort();
        File serverFolder = new File( JabyDaemon.getInstance().getServerFolder(), type + "-" + uuid );

        try {
            FileUtils.copyDirectory( new File( JabyDaemon.getInstance().getServerTemplateFolder(), type ), serverFolder );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        File serverStartScript = new File( serverFolder, JabyDaemon.getInstance().getStartScriptName() );

        ProcessBuilder processBuilder = new ProcessBuilder( serverStartScript.getAbsolutePath(), "--port", String.valueOf( port ) );

        try {
            this.process = processBuilder.start();
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }

        System.out.println("[Jaby] Started " + type + " with port " + port + " (" + uuid.toString() + ")");

        try {
            this.process.waitFor();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        System.out.println("[Jaby] Server " + uuid.toString() + " exited!");
    }

    public UUID getUuid() {
        return uuid;
    }
}
