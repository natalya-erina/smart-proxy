/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.smartproxy;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Наталья
 */

@Stateless
@ManagedBean
public class DBController {
    @Inject ProxyInfo info;
    private DBCollection collection;
    
    public void initDB(String dbName, String collectionName) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB(dbName);
        collection = db.getCollection(collectionName);
        if (collection == null) {
            collection = db.createCollection(collectionName, null);
        }
    }
    
    public void createDocument() {
        DBObject doc = info.toDBObject();
        collection.insert(doc);
    }
    
    public List<ProxyInfo> getDocuments() {
        List<ProxyInfo> docs = new ArrayList<>();
        
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject dbo = cursor.next();
            docs.add(ProxyInfo.fromDBObject(dbo));            
        }
        return docs;
    }
    
    public List<ProxyInfo> getDocumentsByQuery(DBObject query) {
        DBCursor dBCursor = collection.find(query);
        List<ProxyInfo> docs = new ArrayList<>();
        List<DBObject> objects = dBCursor.toArray();
        for (DBObject obj : objects) {
            docs.add(ProxyInfo.fromDBObject(obj));
        }
        return docs;
    }
    
    public ProxyInfo getOneDocumentByQuery(DBObject query) {
        return ProxyInfo.fromDBObject(collection.findOne(query));
    }
    
    public void insert(ProxyInfo document) {
        collection.insert(document.toDBObject());
    }
    
    public void delete(ProxyInfo document) {
        collection.remove(document.toDBObject());
    }
    
    public void insert(List<ProxyInfo> documents) {
        for (ProxyInfo elem : documents) {
            collection.insert(elem.toDBObject());
        }
    }
    
    public void clearCollection() {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            collection.remove(cursor.next());
        }
    }
    
    public void updateCollection() {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (!ProxyInfo.fromDBObject(doc).isAvailable())
                collection.remove(doc);
        }
    }
}
