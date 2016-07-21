/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Наталья
 */
public class ConnectionCreator {
    private Map<String, AbstractRequestConnectionBuilder> map;
    private URL url;
    private Proxy proxy;
    private String json;
    
    public ConnectionCreator(URL url, Proxy proxy, String json) throws IOException {       
        this.url = url;
        this.proxy = proxy;
        this.json = json;
        map = new HashMap<>();
        map.put("GET", new GetRequestConnectionBuilder());
        map.put("POST", new PostRequestConnectionBuilder());
        map.put("PUT", new PutRequestConnectionBuilder());
        map.put("DELETE", new DeleteRequestConnectionBuilder());
    }
    
    public HttpURLConnection getConnection(String method) throws IOException {
        return (map.get(method.toUpperCase())).createConnection(url, proxy, json);
    }
}
