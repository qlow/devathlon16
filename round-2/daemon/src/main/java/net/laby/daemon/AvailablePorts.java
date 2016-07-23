package net.laby.daemon;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class created by qlow | Jan
 */
public class AvailablePorts {

    // Minimum port can be used
    private final static int MIN_PORT = 40000;

    // Maximum port can be used
    private final static int MAX_PORT = 50000;

    // List of the ports already have been used
    private final static List<Integer> alreadyUsedPorts = new ArrayList<>();

    /**
     * Available port for starting a server-socket
     *
     * @return available port
     */
    public static int getAvailablePort() {
        // Iterating through ports
        for ( int i = MIN_PORT; i < MAX_PORT; i++ ) {
            if ( alreadyUsedPorts.contains( i ) )
                continue;

            try {
                // Opening socket (if there is an exception, the port isn't available)
                ServerSocket serverSocket = new ServerSocket( i );

                // Running thread that removes this port from the used-ports list after 15 minutes
                int finalI = i;
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep( TimeUnit.MINUTES.toMillis( 15 ) );
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }

                        alreadyUsedPorts.remove( finalI );
                    }
                } ).start();

                // Returning available port
                return i;
            } catch ( IOException ex ) {
                continue;
            }
        }

        // I don't think, there is no port available of this 10000
        throw new RuntimeException( "How could this happen? How couldn't there be one port available out of 10000?" );
    }

}
