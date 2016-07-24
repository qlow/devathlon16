package net.laby.protocol;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.laby.protocol.packet.PacketLogin;

import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class JabyChannel {

    @Getter
    private Channel channel;

    @Getter
    private PacketLogin.ClientType clientType;

    @Getter
    @Setter
    private int maxRamUsage;

    @Getter
    @Setter
    private int currentRamUsage;

    @Getter
    @Setter
    private String[] availableTypes;

    @Getter
    @Setter
    private UUID uuid;

    public JabyChannel( Channel channel, PacketLogin.ClientType clientType, int maxRamUsage ) {
        this.channel = channel;
        this.clientType = clientType;
        this.maxRamUsage = maxRamUsage;
        this.uuid = UUID.randomUUID();
    }

}
