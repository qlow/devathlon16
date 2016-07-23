package net.laby.protocol;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.laby.protocol.packet.PacketLogin;

/**
 * Class created by qlow | Jan
 */
public class JabyChannel {

    @Getter
    private Channel channel;

    @Getter
    private PacketLogin.ClientType clientType;

    @Getter
    private byte maxRamUsage;

    @Getter
    private byte maxCpuUsage;

    @Getter
    @Setter
    private byte currentRamUsage;

    @Getter
    @Setter
    private String[] availableTypes;

    public JabyChannel( Channel channel, PacketLogin.ClientType clientType, byte maxRamUsage ) {
        this.channel = channel;
        this.clientType = clientType;
        this.maxRamUsage = maxRamUsage;
    }

}
