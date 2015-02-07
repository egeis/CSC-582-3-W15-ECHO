package main.java.com.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 *
 * @author Richard Coan
 */
public class Launch {

    private static DataInputStream input;
    private static DataOutputStream output;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String path = Launch.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, "UTF-8");
         
        int count_port = 1214;
        String nodes = "nodes.txt";
        
        if(path.charAt(0) == '/')
        {
            StringBuilder sb = new StringBuilder(path);
            sb.deleteCharAt(0);
            path = sb.toString();
        }
        
        if(args.length > 0 && args.length < 3)
        {
            count_port = Integer.parseInt(args[0]);
            nodes = args[1];
        } else if(args.length > 0 && args.length < 2)  {
            nodes = args[0];
        } else {
            ClassLoader classLoader = Launch.class.getClassLoader();
            File file = new File(classLoader.getResource("file/nodes.txt").getFile());
            
            System.exit(1);
        }
        
        System.out.println("Jar Location:"+path);
        
        ArrayList<Process> processes = new ArrayList<Process>();
        
        Process CounterServer;
        boolean ready = false;
        
        try {
            System.out.println("Starting Counter Server");
            CounterServer = Runtime.getRuntime().exec("java -cp "+path+" com.echo.CounterNode " + count_port);
        } catch (IOException ex) {
            ex.printStackTrace();        
        } 
        
        System.out.println("Starting: Sending for ID's");
                
        for(int i = 0; i < 10; i++)
        {
            try {
                Socket socket = new Socket("localhost", count_port);
                output = new DataOutputStream (socket.getOutputStream()); 
                input = new DataInputStream(socket.getInputStream());
                
                output.writeInt(2);
                output.flush();                                
                int id = input.readInt();
                
                
                
                output.close();
                input.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        //Shut down the Counter Server.
        System.out.println("Shutting down counter server.");
        try {
            Socket socket = new Socket("localhost", count_port);
            output = new DataOutputStream(socket.getOutputStream()); 
            output.writeInt(-1);
            output.flush();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();       
        }
        
        //Holds the Launcher from exiting, to debug counter server status.
        System.out.println("Press any key to exit.");
        try {
            System.in.read();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
