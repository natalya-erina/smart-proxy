/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Наталья
 */
public class ProxyServer extends HttpServlet {

    private DBController controller;
    
    public ProxyServer() throws UnknownHostException {
        controller = new DBController();
    }
    
    private void updateDatabase() throws UnknownHostException, FileNotFoundException, IOException { 
        File file = new File("./lastUpdate.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String str = reader.readLine();
            Date lastUpdate = (new SimpleDateFormat("MM/dd/yyyy")).parse(str);
            if (((new Date()).getTime() - lastUpdate.getTime())/(1000*60*60*24) >= 3) {
                List<ProxyInfo> proxies = HtmlParser.parse("http://foxtools.ru/Proxy");
                controller.clearCollection();
                for (ProxyInfo proxy : proxies) {
                    controller.insert(proxy);
                }
                controller.updateCollection();
                PrintWriter pw = new PrintWriter(file);
                pw.println(new Date());
                pw.close();
            }
        } catch (ParseException ex) {
        }
    }
    
    private String getResult(HttpURLConnection connection) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));    
            String output;
            while ((output = br.readLine()) != null) {
                result +=output;
            }
        } catch (IOException e) {
            result = "<html><body><h2>Cannot connect to requested proxy server</h2></body></html>";
        } 
        return result;
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        controller.initDB("test", "proxies");
        updateDatabase();
        DBObject doc = new BasicDBObject();
        String country = request.getParameter("country-value").toLowerCase();
        if (!country.isEmpty())
            doc.put("country", country);
        String type= request.getParameter("type-value").toLowerCase();
        if (!type.isEmpty())
            doc.put("type", type);
        
        ProxyInfo proxyInfo;
        try {
            proxyInfo = controller.getOneDocumentByQuery(doc);
        } catch (NullPointerException e) {
            String result = "<html><body><h2>Cannot find requested proxy server</h2></body></html>";
            request.setAttribute("message", result);
            request.getRequestDispatcher("response.jsp").forward(request, response);
            return;
        }
       
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.getIp(), proxyInfo.getPort()));
        URL url = new URL(request.getParameter("url"));
        HttpURLConnection conn = (HttpURLConnection)url.openConnection(proxy);
        
        conn.setRequestMethod(request.getParameter("request-type").toUpperCase());  
        
        request.setAttribute("message", getResult(conn));
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
