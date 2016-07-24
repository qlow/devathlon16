package net.laby.bungee;

import lombok.Getter;
import lombok.Setter;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketRequestServer;
import net.laby.protocol.packet.PacketRequestShutdown;
import net.md_5.bungee.api.event.LoginEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class ServerType {

    @Getter
    private static List<ServerType> serverTypes = new ArrayList<>();

    @Getter
    private String type;

    @Getter
    private LinkedHashMap<UUID, JabyServer> servers = new LinkedHashMap<>();

    @Getter
    @Setter
    private int serverAmount;

    @Getter
    @Setter
    private boolean standby;

    @Getter
    @Setter
    private String motd;

    @Getter
    @Setter
    private int secondsUntilStop;

    @Getter
    @Setter
    private List<String> addresses = new ArrayList<>();

    @Getter
    @Setter
    private boolean copyServerContent;

    @Getter
    @Setter
    private Map<UUID, LoginEvent> joiningPlayers = new HashMap<>();

    /**
     * Constructs a new server-type
     *
     * @param type             type's name
     * @param standby          state whether the type is in standby-mode
     * @param serverAmount     amount of the servers should be started with this type
     * @param motd             MOTD of this server-type
     * @param secondsUntilStop Time in seconds the server stops after it reaches 0 players
     * @param addresses        Addresses of the server-type
     */
    public ServerType( String type, boolean standby, int serverAmount, String motd, int secondsUntilStop,
                       List<String> addresses, boolean copyServerContent ) {
        this.type = type;
        this.standby = standby;
        this.serverAmount = serverAmount;
        this.motd = motd;
        this.secondsUntilStop = secondsUntilStop;
        this.addresses = addresses;
        this.copyServerContent = copyServerContent;
    }

    /**
     * State whether one server is started
     *
     * @return true if one server is started
     */
    public boolean isStarted() {
        return servers.size() > 0;
    }

    /**
     * Starts the required servers if there are servers required
     */
    public boolean startServers() {
        // Checking for standby-mode
        if ( standby )
            return false;

        // Calculating required servers
        int requiredServers = serverAmount - servers.size();

        // Returning if there is no server required
        if ( requiredServers < 1 )
            return true;

        // List of JabyChannels
        List<JabyChannel> jabyChannels = new ArrayList<>();
        jabyChannels.addAll( JabyBootstrap.getChannels().values() );

        // Sorting JabyChannels by usage
        jabyChannels.sort( new Comparator<JabyChannel>() {
            @Override
            public int compare( JabyChannel o1, JabyChannel o2 ) {
                return (( Integer ) (( int ) o1.getCurrentRamUsage())).compareTo( ( int ) o2.getCurrentRamUsage() );
            }
        } );

        // Iterating through all channels
        for ( JabyChannel jabyChannel : jabyChannels ) {
            // Checking for wrong type
            if ( jabyChannel.getClientType() != PacketLogin.ClientType.DAEMON )
                continue;

            // Checking for available types from the channel
            if ( !Arrays.asList( jabyChannel.getAvailableTypes() ).contains( this.type ) )
                continue;

            // Checking if the current RAM is over the threshold
            if ( jabyChannel.getMaxRamUsage() <= jabyChannel.getCurrentRamUsage() )
                continue;

            // Sending request-packet
            jabyChannel.getChannel().writeAndFlush( new PacketRequestServer( this.type, requiredServers ) );

            // Log message
            System.out.println( "[Jaby] Starting " + requiredServers + " servers of type " + type );
            return true;
        }

        // Log message if there couldn't start any instance of this type
        System.err.println( "[Jaby] Didn't find a free instance for type " + type );
        return false;
    }

    /**
     * Shutdowns all servers
     */
    public void shutdown() {
        for ( UUID server : servers.keySet() ) {
            servers.get( server ).getChannel().getChannel().writeAndFlush( new PacketRequestShutdown( server ) );
        }

        servers.clear();
    }

    /**
     * Gets a server-type by the name
     *
     * @param typeName name of the wanted server-type
     * @return ServerType object with the given name
     */
    public static ServerType getByName( String typeName ) {
        for ( ServerType serverType : serverTypes ) {
            if ( !serverType.getType().equalsIgnoreCase( typeName ) )
                continue;

            return serverType;
        }

        return null;
    }

    /**
     * Gets a server-type by the address
     *
     * @param address one of the address of the wanted server-type
     * @return ServerType object with the given address
     */
    public static ServerType getByAddress( String address ) {
        for ( ServerType serverType : serverTypes ) {
            if ( !serverType.getAddresses().contains( address.toLowerCase() ) )
                continue;

            return serverType;
        }

        return null;
    }

}
