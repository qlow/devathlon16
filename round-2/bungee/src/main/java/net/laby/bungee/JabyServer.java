package net.laby.bungee;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.laby.protocol.JabyChannel;

import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
@Data
@AllArgsConstructor
public class JabyServer {

    private UUID uuid;
    private String type;

    private String address;
    private int port;

    private JabyChannel channel;

}
