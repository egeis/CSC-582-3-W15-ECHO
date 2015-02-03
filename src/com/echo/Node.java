package com.echo;

import com.echo.network.Packet;
import com.echo.network.PacketHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private Node(int port)
    {        
        try {
            server = new ServerSocket(port);
            
            while(true)
            {
                Packet p = getMessage();
                if(shutdown) break;
            }
                        
            server.close();
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private Packet getMessage() throws IOException, ClassNotFoundException
    {
        Socket socket = server.accept();
        input = new ObjectInputStream(socket.getInputStream());
        Packet p = (Packet) input.readObject();
        this.parsePacket(p);                
        socket.close();
        
        return p;
    }
    
    private void parsePacket(Packet p)
    {        
        switch(p.type) 
        {
            case PacketHelper.TYPE_BROADCAST:
                break;
            case PacketHelper.TYPE_REPLY:
                break;
            case PacketHelper.TYPE_SHUTDOWN:
                shutdown = true;
                break;
            default:
                       
        }
    }
    
    public static void main(String[] args)
    {
        int port = 0;
        int id = 0;
        
        if(args.length > 0 && args.length > 3)
        {
            port = Integer.parseInt(args[0]);
            id = Integer.parseInt(args[1]);
        } 
        else 
        {
            System.exit(1);
        }
        
        LOGGER = Logger.getLogger(Node.class.getName()+port);
        Node node = new Node(port);
    }
}