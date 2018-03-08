<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="tree" uri="http://java.freehep.org/tree-taglib" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link type="text/css" rel="stylesheet" href="<c:url value="tabs.css"/>">
    </head>
    <body>

    <%
    org.freehep.webutil.tree.DefaultTreeNode root = new org.freehep.webutil.tree.DefaultTreeNode("root");
    root.createNodeAtPath("A");
    root.createNodeAtPath("A/a");
    root.createNodeAtPath("/A/B");
    root.createNodeAtPath("/A/B/b/");
    root.createNodeAtPath("/A/B/c");
    root.createNodeAtPath("/1/2/3");
    root.createNodeAtPath("/1/2/4");
    root.createNodeAtPath("/1/2/5/");
    root.createNodeAtPath("/1/3");
    session.setAttribute("root", root);
    %>
    
    <tree:tree model="${root}"/>

    </body>
</html>
