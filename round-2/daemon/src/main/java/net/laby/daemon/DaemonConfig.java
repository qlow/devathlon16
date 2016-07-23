package net.laby.daemon;

import lombok.Getter;
import lombok.Setter;

/**
 * Class created by qlow | Jan
 */
public class DaemonConfig {

    @Getter
    @Setter
    private String address = "127.0.0.1";

    @Getter
    @Setter
    private int port = 1337;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private byte maxRamUsage = 60;

}
