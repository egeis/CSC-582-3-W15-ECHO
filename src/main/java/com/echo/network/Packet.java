package main.java.com.echo.network;

import java.io.Serializable;

public class Packet implements Serializable {
    public int type = 0;
    public Integer id = null;
    public Integer wave = null;
    public Integer parent = null;
    
    @Override
    public String toString()
    {
        return "[Packet:"+type+" id:"+id+" wave:"+wave+"]";
    }    
}