/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Наталья
 */
@Path("/proxy")
public class ProxyServer {

    private DBController controller;
    
    public ProxyServer() throws UnknownHostException, SchedulerException {
        controller = new DBController();
    }
    
    static {     
        try {
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();
            
            JobDetail job = newJob(DBController.class).build();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever())
                    .build();
            sched.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
        }
    }
    
    public String getResult(HttpURLConnection connection) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));    
            String output;
            while ((output = br.readLine()) != null) {
                result +=output;
            }
            br.close();
            connection.disconnect();
        } catch (IOException e) {
            result = "Cannot connect to requested proxy server";
        } 
        return result;
    }
    
    private Response processRequest (HttpHeaders headers, String json, String method) throws IOException {
        controller.initDB("test", "proxies");
        DBObject doc = new BasicDBObject();
        
        doc.put("country", headers.getHeaderString("country"));
        ProxyInfo proxyInfo;
        try {
            proxyInfo = controller.getOneDocumentByQuery(doc);
        } catch (NullPointerException e) {
            return Response.ok().build();
        }
       
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.getIp(), proxyInfo.getPort()));
        URL url = new URL(headers.getHeaderString("target"));
        ConnectionCreator creator = new ConnectionCreator(url, proxy, json);
        HttpURLConnection conn = creator.getConnection(method);
        conn.getResponseCode();
        String result = getResult(conn);
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processGetRequest(@Context HttpHeaders headers) throws IOException {
        return processRequest(headers, "", "GET");
    }
    
    @POST
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processPostRequest(@Context HttpHeaders headers, String json) throws IOException {
        return processRequest(headers, json, "POST"); 
    }
    
    @PUT
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processPutRequest(@Context HttpHeaders headers, String json) throws IOException {
        return processRequest(headers, json, "PUT"); 
    }
    
    @DELETE
    @Path("/go")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processDeleteRequest(@Context HttpHeaders headers, String json) throws IOException {
        return processRequest(headers, json, "DELETE"); 
    }
}
