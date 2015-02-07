package com.echo;

import com.echo.network.Packet;
import com.echo.network.PacketHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private int parent = -1;
    private int id = -1;
    private int port = -1;
    
    public static String TIMER_HOST = "localhost";
    public static int TIMER_PORT = 1212;
        
    protected static ArrayList<Conn> adj = new ArrayList<Conn>();
    
    private Node()
    {        
        //Get an ID
        try ( 
            Socket socket = new Socket(TIMER_HOST, TIMER_PORT)) {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeInt(1);
            output.flush();
            
            input = new ObjectInputStream(socket.getInputStream());
            id = input.readInt();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            acceptMessages();
        }
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
     */
    private void sendPacket(Packet p)
    {        
        try {
            for(Conn c : adj) 
            {
                if(c.id == this.parent) continue;
                
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
            case PacketHelper.BROADCAST:
                break;
            case PacketHelper.REPLY:
                break;
            case PacketHelper.WAVE_BROADCAST:
                break;
            case PacketHelper.WAVE_REPLY:
                break;
            case PacketHelper.SHUTDOWN:
                shutdown = true;
                break;
            default:
                
        }
    }
    
    public static void main(String[] args)
    {
        int port = 0;
        int id = 0;
        
        if(args.length > 2 )
        {
            //Setup Port and ID.
            port = Integer.parseInt(args[0]);
            id = Integer.parseInt(args[1]);
            LOGGER = Logger.getLogger(Node.class.getName()+port);
            
            //Get Adjacent List
            for(int i = 2; i < args.length; i++)
            {
                String[] parts = args[i].split(":");
                                
                Conn c = new Conn();
                c.host = parts[0];
                c.port = Integer.parseInt(parts[1]);
                
                adj.add(c);
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
    
    protected static class Conn 
    {
        public String host;
        public int port;
        public int id = 0;
        
        public String toString()
        {
            return "[Host:"+host+", Port:"+port+"]";
        }
    }
}