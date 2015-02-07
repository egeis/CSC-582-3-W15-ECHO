package main.java.com.echo.network;

import java.io.Serializable;

public class Packet implements Serializable {
    public int type = 0;
    public int id = 0;
    public int wave = 0;
    
    @Override
    public String toString()
    {
        return "[Packet:"+type+" id:"+id+" wave:"+wave+"]";
    }    
}