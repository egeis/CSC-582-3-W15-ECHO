/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echo;

import com.echo.network.Packet;
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
public class TimerNode {
    private static Logger LOGGER;
                
    private static ServerSocket server;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;
        
    
    public static void main(String[] args)
    {
        LOGGER = Logger.getLogger(Node.class.getName());
        int counter = 0;
        
        try {
            server = new ServerSocket(1212);
            
            while(true)
            {
                Socket socket = server.accept();
                input = new ObjectInputStream(socket.getInputStream());
                int send = input.readInt();
                
                output = new ObjectOutputStream(socket.getOutputStream()); 
                output.writeInt(++counter);
                
                socket.close();
                
                if(send == -1) break;   //Unreachable
            }
                        
            server.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}