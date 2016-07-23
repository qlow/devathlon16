package net.laby.daemon.task;

import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketPowerUsage;

/**
 * That tasks sends the RAM-utilization every 2 seconds
 * Class created by qlow | Jan
 */
public class PowerUsageTask implements Runnable {

    @Override
    public void run() {
        while ( true ) {
            if(!JabyDaemon.getInstance().isConnected())
                return;

            try {
                Thread.sleep( 2000L );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            JabyBootstrap.getClientHandler().sendPacket( new PacketPowerUsage( getCurrentRamUsage() ) );
        }
    }

    /**
     * Current usage of RAM in percent
     * @return current usage (0 to 100)
     */
    private byte getCurrentRamUsage() {
        com.sun.management.OperatingSystemMXBean bean =
                ( com.sun.management.OperatingSystemMXBean )
                        java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        long total = bean.getTotalPhysicalMemorySize() / 1024;
        long used = total - (bean.getFreePhysicalMemorySize() / 1024);

        return ( byte ) (100D / total * used);
    }

}
