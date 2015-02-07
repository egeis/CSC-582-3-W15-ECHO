package main.java.com.echo;

import main.java.com.echo.network.Packet;
import main.java.com.echo.network.PacketHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.echo.network.Conn;

/**
 *
 * @author Richard Coan
 */
public class Node {
    private static Logger LOGGER;
                
    private ServerSocket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean shutdown = false;
    
    private Integer parent = null;
    private Integer id = null;
    private int port = -1;
    private String host = "";
    private Boolean isInit = false;
    
    private static Map<Integer,Conn.Child> children = new HashMap<Integer,Conn.Child>();
    private static Map<Integer,Boolean> replies = new HashMap<Integer,Boolean>();
    
//    public static String TIMER_HOST = "localhost";
//    public static int TIMER_PORT = 1212;
            
    private Node()
    {        
//        /**Get an ID**/
//        try ( 
//            Socket socket = new Socket(TIMER_HOST, TIMER_PORT)) {
//            output = new ObjectOutputStream(socket.getOutputStream());
//            output.writeInt(1);
//            output.flush();
//            
//            input = new ObjectInputStream(socket.getInputStream());
//            id = input.readInt();
//            socket.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        acceptMessages();
    }
    
    private void acceptMessages()
    {
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        while(true)
        {
            try (Socket socket = server.accept()) {
                input = new ObjectInputStream(socket.getInputStream());
                Packet p = (Packet) input.readObject();
                this.parsePacket(p);
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } 
            
            if(shutdown) break;
        }      
        
        try {
            server.close();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }
    
     /**
     * Sends a packet of data to the initiator.
     * @param p the packet.
     * @param the type of send, 0 for parent, 1 for children (excluding parent)
     * 2 for all.
     */
    private void sendPacket(Packet p, int type)
    {        
        try {
            for(Entry<Integer,Conn.Child> entry : children.entrySet()) 
            {
                Conn.Child c = entry.getValue();
                Integer key = entry.getKey();
                
                if(key == (Integer) this.parent && type == 1) continue;
                else if((key != (Integer) this.parent && type == 0)) continue;
                
                Socket out = new Socket(c.host, c.port);    
                output = new ObjectOutputStream(out.getOutputStream()); 
                output.writeObject(p);
                output.flush();
                out.close();
            }
        } catch (IOException ex) {
           LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private void parsePacket(Packet p)
    {        
        switch(p.type) 
        {
            case PacketHelper.MESSAGE:
                if(parent == null)
                {
                    parent = p.id;
                    Packet send = PacketHelper.getPacket(PacketHelper.MESSAGE, id, id);
                    sendPacket(send,1);
                } else {
                    
                }
                break;
            case PacketHelper.INIT_INITIATOR:
                isInit = true;
                Packet send = PacketHelper.getPacket(PacketHelper.MESSAGE, id, id);
                sendPacket(send,2);
                break;
            case PacketHelper.INIT_SHUTDOWN:
                shutdown = true;
                break;
            default:
                //Do Nothing.
        }
    }
    
    public static void main(String[] args)
    {
        int port = 0;
        int id = 0;
        String host = "";
        
        if(args.length > 2 )
        {
            //Setup Port and ID.
            String parts[] = args[0].split(":");
            id = Integer.parseInt(parts[0]);
            host = parts[1];
            port = Integer.parseInt(parts[2]);
            
            LOGGER = Logger.getLogger(Node.class.getName()+port);
            
            //Get Adjacent List
            for(int i = 1; i < args.length; i++)
            {
                parts = args[i].split(":");
                                
                Conn.Child c = new Conn.Child();
                c.host = parts[1];
                c.port = Integer.parseInt(parts[2]);
                
                children.put(Integer.parseInt(parts[0]), c);
            }
        } 
        else 
        {
            LOGGER.log(Level.SEVERE, "Invalid Argument Length.");
            System.exit(1);     //Something Went Wrong...
        }
        
        Node node = new Node();
        System.out.println("Exiting...");
    }
}