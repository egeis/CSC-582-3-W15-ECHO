package main.java.com.echo.network;

/**
 * Creates the Packet Object to send across nodes.
 * @author Richard Coan
 */
public class PacketHelper {
    
    public static final int MESSAGE              = 1;
    public static final int INIT_SHUTDOWN        = 30;
    public static final int INIT_INITIATOR       = 31;

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