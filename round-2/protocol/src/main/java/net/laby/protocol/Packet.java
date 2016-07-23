package net.laby.protocol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * Class created by qlow | Jan
 */
public abstract class Packet {

    /**
     * Called when the packet should be read from the ByteBuf
     *
     * @param byteBuf ByteBuf the packet should be read from
     */
    public abstract void read( ByteBuf byteBuf );

    /**
     * Called when the packet should be written to the ByteBuf
     *
     * @param byteBuf ByteBuf the packet should be written to
     */
    public abstract void write( ByteBuf byteBuf );

    /**
     * Reads a String from the given ByteBuf
     *
     * @param byteBuf ByteBuf the String should be read from
     * @return string in bytebuf
     */
    public String readString( ByteBuf byteBuf ) {
        int stringLength = byteBuf.readInt();

        byte[] stringBytes = new byte[stringLength];
        byteBuf.readBytes( stringBytes );

        return new String( stringBytes, Charset.forName( "UTF-8" ) );
    }

    /**
     * Writes the given String to the given ByteBuf
     *
     * @param byteBuf     ByteBuf the String should be written to
     * @param writeString String should be written to the given ByteBuf
     */
    public void writeString( ByteBuf byteBuf, String writeString ) {
        byte[] stringBytes = writeString.getBytes( Charset.forName( "UTF-8" ) );

        // Writing length
        byteBuf.writeInt( stringBytes.length );

        // Writing bytes
        byteBuf.writeBytes( stringBytes );
    }

}
