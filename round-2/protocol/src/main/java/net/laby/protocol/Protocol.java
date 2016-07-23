package net.laby.protocol;

/**
 * Protocol used for getting packets by id & getting ids by packets
 * Class created by qlow | Jan
 */
public enum Protocol {

    TEST( 0x01, Packet.class );

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
