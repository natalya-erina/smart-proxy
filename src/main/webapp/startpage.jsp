<%-- 
    Document   : startpage
    Created on : Jul 10, 2016, 3:09:49 PM
    Author     : Наталья
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Request</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style.css" />
    </head>
    <body>
        <form action="ProxyServer" method="GET" name="form" id="form">
            <input type="text" placeholder="Request URL" name="url" id="request-url"/>
            <input type="radio" name="request-type" value="get" checked="checked" id="get"/> GET
            <input type="radio" name="request-type" value="post" id="post"/> POST
            <input type="radio" name="request-type" value="put" id="put"/> PUT
            <input type="radio" name="request-type" value="delete" id="delete"/> DELETE
            <h3>Headers</h3>
            <input type="text" name="country" value="Preferred-Country" readonly id="country"/>
            <input type="text" name="country-value" placeholder="Header value" id="country-value"/><br>
            <input type="text" name="type" value="Type" readonly id="type"/>
            <input type="text" name="type-value" placeholder="Header value" id="type-value"/><br>
            <input type="submit" value="Send" name="button" id="btn-submit"/>
        </form>
    </body>
</html>
