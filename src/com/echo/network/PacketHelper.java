package com.echo.network;

/**
 * Creates the Packet Object to send across nodes.
 * @author Richard Coan
 */
public class PacketHelper {
    
    public static final int REPLY = 1;
    public static final int BROADCAST = 2;
    public static final int WAVE_REPLY = 3;
    public static final int WAVE_BROADCAST = 4;
    
    public static final int SHUTDOWN = 21;

    /**
     * Gets a Packet containing only a message type.
     * @param type of message.
     * @return 
     */
    public static Packet getPacket(int type, int id, int wave)
    {
        Packet p = new Packet();
        p.type = type;
        p.id = id;
        p.wave = wave;
        return p;
    }
}