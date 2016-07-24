package net.laby.bungee;

import io.netty.channel.Channel;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketUpdateDaemons;
import net.laby.protocol.packet.PacketUpdateTypes;
import net.laby.protocol.utils.JabyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class ApplicationUpdater {

    public static void updateTypes() {
        int count = ServerType.getServerTypes().size() + 1;

        String[] names = new String[count];
        String[] motds = new String[count];
        int[] startedServers = new int[count];
        int[] maxServers = new int[count];
        boolean[] standby = new boolean[count];

        names[0] = "BungeeCord";
        motds[0] = Jaby.getInstance().getMotdString();
        startedServers[0] = 0;
        maxServers[0] = 0;
        standby[0] = false;

        int i = 1;

        for ( ServerType serverType : ServerType.getServerTypes() ) {
            names[i] = serverType.getType();
            motds[i] = serverType.getMotd();
            startedServers[i] = serverType.getServers().size();
            maxServers[i] = serverType.getServerAmount();
            standby[i] = serverType.isStandby();

            i++;
        }

        PacketUpdateTypes packetUpdateTypes = new PacketUpdateTypes( count, names, motds, startedServers, maxServers, standby );

        for ( Map.Entry<Channel, JabyChannel> connectedChannelEntry : JabyBootstrap.getChannels().entrySet() ) {
            if ( connectedChannelEntry.getValue().getClientType() != PacketLogin.ClientType.APPLICATION )
                continue;

            connectedChannelEntry.getKey().writeAndFlush( packetUpdateTypes );
        }
    }

    public static void updateDaemons() {
        // List of JabyChannels
        List<JabyChannel> jabyChannels = new ArrayList<>();
        jabyChannels.addAll( JabyBootstrap.getChannels().values() );

        List<JabyChannel> daemonChannels = new ArrayList<>();

        for ( JabyChannel channel : jabyChannels ) {
            if ( channel.getClientType() != PacketLogin.ClientType.DAEMON )
                continue;

            daemonChannels.add( channel );
        }

        int count = daemonChannels.size();

        if ( count == 0 )
            return;

        String[] ips = new String[count];
        int[] currentRam = new int[count];
        int[] maxRam = new int[count];
        UUID[] uuids = new UUID[count];

        int i = 0;

        for ( JabyChannel daemon : daemonChannels ) {
            ips[i] = JabyUtils.getHostString( daemon.getChannel().remoteAddress() );
            currentRam[i] = daemon.getCurrentRamUsage();
            maxRam[i] = daemon.getMaxRamUsage();
            uuids[i] = daemon.getUuid();

            i++;
        }

        PacketUpdateDaemons packetUpdateDaemons = new PacketUpdateDaemons( count, ips, uuids, currentRam, maxRam );

        for ( Map.Entry<Channel, JabyChannel> connectedChannelEntry : JabyBootstrap.getChannels().entrySet() ) {
            if ( connectedChannelEntry.getValue().getClientType() != PacketLogin.ClientType.APPLICATION )
                continue;

            connectedChannelEntry.getKey().writeAndFlush( packetUpdateDaemons );
        }
    }
}
