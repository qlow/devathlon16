package net.laby.protocol;

import net.laby.protocol.packet.PacketAvailableTypes;
import net.laby.protocol.packet.PacketChangeMaxRam;
import net.laby.protocol.packet.PacketCopyMode;
import net.laby.protocol.packet.PacketDisconnect;
import net.laby.protocol.packet.PacketExitServer;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketLoginSuccessful;
import net.laby.protocol.packet.PacketMultiServer;
import net.laby.protocol.packet.PacketPowerUsage;
import net.laby.protocol.packet.PacketRequestServer;
import net.laby.protocol.packet.PacketRequestShutdown;
import net.laby.protocol.packet.PacketServerDone;
import net.laby.protocol.packet.PacketStartServer;
import net.laby.protocol.packet.PacketUpdateDaemons;
import net.laby.protocol.packet.PacketUpdateType;
import net.laby.protocol.packet.PacketUpdateTypes;

/**
 * Protocol used for getting packets by id & getting ids by packets
 * Class created by qlow | Jan
 */
public enum Protocol {

    LOGIN( 0x01, PacketLogin.class ),
    DISCONNECT( 0x02, PacketDisconnect.class ),
    AVAILABLE_TYPES( 0x03, PacketAvailableTypes.class ),
    LOGIN_SUCCESSFUL( 0x04, PacketLoginSuccessful.class ),
    POWER_USAGE( 0x05, PacketPowerUsage.class ),
    START_SERVER( 0x06, PacketStartServer.class ),
    EXIT_SERVER( 0x07, PacketExitServer.class ),
    REQUEST_SERVER( 0x08, PacketRequestServer.class ),
    REQUEST_SHUTDOWN( 0x09, PacketRequestShutdown.class ),
    MULTI_SERVER( 0x0A, PacketMultiServer.class ),
    SERVER_DONE( 0x0B, PacketServerDone.class ),
    COPY_MODE( 0x0C, PacketCopyMode.class ),
    UPDATE_DAEMONS( 0x0D, PacketUpdateDaemons.class ),
    UPDATE_TYPES( 0x0E, PacketUpdateTypes.class ),
    CHANGE_MAX_RAM( 0x0F, PacketChangeMaxRam.class ),
    UPDATE_TYPE( 0x10, PacketUpdateType.class );

    private int packetId;
    private Class<? extends Packet> packetClass;

    /**
     * @param packetId    packet's id
     * @param packetClass packet's class
     */
    Protocol( int packetId, Class<? extends Packet> packetClass ) {
        this.packetId = packetId;
        this.packetClass = packetClass;
    }

    /**
     * Packet's id
     *
     * @return packet's id
     */
    public int getPacketId() {
        return packetId;
    }

    /**
     * Packet's class
     *
     * @return packet's class
     */
    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    /**
     * Returns the packet-id by the given packet-class
     *
     * @param packetClass wanted packet's class
     * @return packet's id, -1 if the packet-id wasn't found
     */
    public static int getPacketIdByClass( Class<? extends Packet> packetClass ) {

        // Iterating through all values in Protocol
        for ( Protocol protocol : Protocol.values() ) {
            // Checking for the right packet-class
            if ( protocol.getPacketClass() != packetClass )
                continue;

            return protocol.getPacketId();
        }

        return -1;
    }

    /**
     * Returns the packet-class by the given packet-id
     *
     * @param packetId wanted packet's id
     * @return packet's class, null if the packet-class wasn't found
     */
    public static Class<? extends Packet> getPacketClassById( int packetId ) {
        // Iterating through all values in Protocol
        for ( Protocol protocol : Protocol.values() ) {
            // Checking for the right packet-id
            if ( protocol.getPacketId() != packetId )
                continue;

            return protocol.getPacketClass();
        }

        return null;
    }

}
