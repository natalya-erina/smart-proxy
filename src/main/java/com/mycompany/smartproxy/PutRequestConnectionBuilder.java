/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 *
 * @author Наталья
 */
public class PutRequestConnectionBuilder extends AbstractRequestConnectionBuilder {
    
    @Override
    public HttpURLConnection createConnection(URL url, Proxy proxy, String json) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();
        return conn;
    }
}
