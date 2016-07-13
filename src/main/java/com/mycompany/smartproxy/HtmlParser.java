/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smartproxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Наталья
 */

public class HtmlParser {
    
    public static List<ProxyInfo> parse(String url) throws IOException { 
        List<ProxyInfo> proxies = new ArrayList<>();
        for (int i = 1; ; i++) {
            Document htmlPage = Jsoup.connect(url+"?page="+i).timeout(100000).get();
            Element table = htmlPage.getElementById("theProxyList");
            if (table == null)
                break;
            Elements trs = htmlPage.select("#theProxyList tbody tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                String ip = tds.get(1).text().trim();
                int port = Integer.parseInt(tds.get(2).text().trim());
                String country = tds.get(3).text().trim();
                country = country.substring(country.indexOf('(')+1, country.indexOf(')'));
                String type = tds.get(5).text().trim().toLowerCase();
                ProxyInfo proxy = new ProxyInfo(ip, port, country, type);
                proxies.add(proxy);
            }
        }
        return proxies;
    }
}
