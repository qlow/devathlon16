package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketPowerUsage extends Packet {

    @Getter
    private byte currentRamUsage;
    @Getter
    private byte currentCpuUsage;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.currentRamUsage = byteBuf.readByte();
        this.currentCpuUsage = byteBuf.readByte();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeByte( currentRamUsage );
        byteBuf.writeByte( currentCpuUsage );
    }

}
