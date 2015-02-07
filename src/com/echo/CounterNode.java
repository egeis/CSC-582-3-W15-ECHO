package com.echo;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Distributes a unique long value on request.
 * @author Richard Coan
 */
public class CounterNode
{
    private final static Logger LOGGER = Logger.getLogger(Node.class.getName());
                
    private static ServerSocket server;
    private static DataInputStream input;
    private static DataOutputStream output;
    
    public static void main(String[] args)
    {
        long counter = 0;   //Counter
        int id = 0;
        int port = 1212;    //Default Port
        
        //Custom Port
        if(args.length > 0)
            port = Integer.parseInt(args[0]);
        
        //Creates the Server
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        System.out.println(CounterNode.class.getName()+":ready");
        
        while(true)
        { 
            try (Socket socket = server.accept()) {
                input = new DataInputStream(socket.getInputStream());
                int send = input.readInt();
                output = new DataOutputStream(socket.getOutputStream());
                if(send == 1) output.writeLong(++counter);
                else if(send == 2) output.writeInt(++id);
                socket.close();
                if(send == -1) break;                        
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } 
        }
        
        try {
            server.close();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }
}