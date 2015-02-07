package main.java.com.echo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import main.java.com.echo.network.Conn;
import main.java.com.echo.network.Packet;
import main.java.com.echo.network.PacketHelper;

/**
 *
 * @author Richard Coan
 */
public class Launch {
    
    /**
     * @param args the command line arguments
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String path = Launch.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, "UTF-8");
         
        System.out.println("Jar Location:"+path);
        
        int count_port = 1213;
        String nodes = "nodes.json";
        
        if(path.charAt(0) == '/')
            path = new StringBuilder(path).deleteCharAt(0).toString();
        
        if(args.length > 0 && args.length < 3)
        {
            count_port = Integer.parseInt(args[0]);
            nodes = args[1];
        } else if(args.length > 0 && args.length < 2)  {
            nodes = args[0];
        } else {
            nodes = "main/resources/"+nodes;    //Default
        }
        
        /*Load Topology*/
        System.out.println("Loading Nodes Configuration.");
        
        ClassLoader classLoader = Launch.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(nodes);
               
        JsonReader rdr = Json.createReader(is);       
        JsonObject obj = rdr.readObject();
        JsonArray results = obj.getJsonArray("nodes");
        
        Map<Integer, Conn> conns = new HashMap<Integer, Conn>();
        Map<Integer, Integer[]> adjacent = new HashMap<Integer, Integer[]>();
              
        Integer largest = null;
        
        for (JsonObject result : results.getValuesAs(JsonObject.class)) {
            Conn c = new Conn();
            c.host = result.getString("host");
            c.id = result.getInt("id");
            c.port = result.getInt("port");
            
            JsonArray nbs = result.getJsonArray("adj");
            
            Integer[] a = new Integer[nbs.size()];
            for(int i = 0; i < nbs.size(); i++)
            {
                a[i] = nbs.getInt(i);
            }
            
            adjacent.put(c.id, a);            
            conns.put(c.id, c);
                    
            if(largest == null)
                largest = c.id;
            else
                if(c.id > largest) largest = c.id;
        }
        
        /*Add Children*/
        for (Conn c : conns.values())
        {
            Integer[] ports = adjacent.get(c.id);
            
            for(int i = 0 ; i < ports.length; i++)
            {
                Conn.Child d = new Conn.Child();
                d.host = conns.get( ports[i] ).host;
                d.port = conns.get( ports[i] ).port;
                c.adj.put(ports[i],d);
            }
                
            System.out.println(conns.get(c.id).toString());
        }
        
        System.out.println("Candidate Initiator:"+largest);
                       
//        "id" : 1,
//        "host" : "localhost",
//        "port" : 1215,
//        "adj" : [2,4]
               
//        Process CounterServer;
//        
//        try {
//            System.out.println("Starting Counter Server");
//            CounterServer = Runtime.getRuntime().exec("java -cp "+path+" main.java.com.echo.CounterNode " + count_port);
//            System.out.println("Created:"+CounterServer.isAlive());
//        } catch (IOException ex) {
//            ex.printStackTrace();        
//        } 
               
        ArrayList<Process> processes = new ArrayList<Process>();
        for(Entry<Integer, Conn> entry : conns.entrySet())
        {
            Conn c = entry.getValue();
            try {
                System.out.println("Starting Node:"+c.id);
                
                StringBuilder sb = new StringBuilder();
                sb.append(c.id+":"+c.host+":"+c.port+" ");
                
                for(Entry<Integer, Conn.Child> d : c.adj.entrySet())
                {
                    Conn.Child child = d.getValue();
                    sb.append(d.getKey()+":"+child.host+":"+child.port+" ");
                }
                
                Process last = Runtime.getRuntime().exec("java -cp "+path+" main.java.com.echo.Node "+sb.toString() );
                processes.add(last);
                System.out.println("Created:"+last.isAlive());
            } catch (IOException ex) {
                Logger.getLogger(Launch.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        /*Shut down*/
//        System.out.println("Shutting down counter server.");
//        try {
//            Socket socket = new Socket("localhost", count_port);
//            DataOutputStream os = new DataOutputStream(socket.getOutputStream()); 
//            os.writeInt(-1);
//            os.flush();
//            os.close();
//            socket.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();       
//        }
        
        for(Entry<Integer, Conn> entry : conns.entrySet())
        {
            Conn c = entry.getValue();
            Socket socket;
            try {
                socket = new Socket(c.host, c.port);
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream()); 
                Packet p = PacketHelper.getPacket(PacketHelper.INIT_SHUTDOWN, 0, 0);
                os.writeObject(p);
                os.flush();
                os.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Launch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        
        //Holds the Launcher from exiting, to debug counter server status.
        System.out.println("Press any key to exit.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(Launch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}   
