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

/**
 *
 * @author Наталья
 */
public abstract class AbstractRequestConnectionBuilder {
    public abstract HttpURLConnection createConnection(URL url, Proxy proxy, String json) throws IOException;
}
