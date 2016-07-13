/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author Наталья
 */

public class ProxyInfo {
    
    private String ip;
    private int port;
    private String country;
    private String type;
    
    public ProxyInfo(String ip, int port, String country, String type) {
        this.ip = ip;
        this.port = port;
        this.country = country;
        this.type = type;
    }
    
    public ProxyInfo() {}
    
    public String getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getCountry() {
        return country;
    }
    
    public String getType() {
        return type;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public DBObject toDBObject() {
        DBObject document = new BasicDBObject();
        document.put("ip", ip);
        document.put("port", port);
        document.put("country", country);
        document.put("type", type);
        return document;
    }
    
    public static ProxyInfo fromDBObject(DBObject document) {
        ProxyInfo proxy = new ProxyInfo();
        proxy.ip = (String) document.get("ip");
        proxy.port = (Integer) document.get("port");
        proxy.country = (String) document.get("country");
        proxy.type = (String) document.get("type");
        return proxy;
    }
    
    public boolean isAvailable () {
        Socket s = null;
        try
        {
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 100000);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if(s != null)
                try {s.close();}
                catch(Exception e){}
        }
    }
    
    @Override
    public String toString() {
        return "{\"ip\":\"" + ip + "\", \"port\":\"" + port + "\", \"country\":\"" + country + "\", \"type\":\"" + type +"\"}";
    }
}
