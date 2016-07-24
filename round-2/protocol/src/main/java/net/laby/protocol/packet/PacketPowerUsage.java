package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the daemon sends the RAM usage (every 2 seconds)
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketPowerUsage extends Packet {

    @Getter
    private int currentRamUsage;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.currentRamUsage = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeInt( currentRamUsage );
    }

}
