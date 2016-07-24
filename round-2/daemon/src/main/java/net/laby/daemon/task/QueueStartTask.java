package net.laby.daemon.task;

import lombok.Getter;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class QueueStartTask implements Runnable {

    @Getter
    private List<ServerStartTask> serverQueue = new ArrayList<>();

    @Override
    public void run() {
        while ( true ) {
            if ( JabyDaemon.getInstance().isConnected() ) {
                // Starting servers in queue
                for ( ServerStartTask serverStartTask : serverQueue ) {
                    JabyBootstrap.getExecutorService().execute( serverStartTask );
                }

                serverQueue.clear();
            }

            try {
                Thread.sleep( 1000 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

}
