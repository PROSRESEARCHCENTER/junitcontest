<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%@attribute name="example" required="false"%>
<%@attribute name="viewsource" required="false"%>


<% 
String rootDataURI = application.getRealPath("/WEB-INF/aida_examples_jsp/data/pawdemo.root");
session.setAttribute( "rootDataURI" , rootDataURI );
%>

<html>
<head>
    <title>The &lt;aida:*&gt; tag library</title>
    <link rel="stylesheet" href="<c:url value='/css/screen.css' />" type="text/css" media="screen, print" />
</head>

<body>

<div id="header">
    <h1>The &lt;aida:*&gt; tag library <small>1.3</small></h1>
    
    <ul>
        <li>
            <a href="index.jsp" target="_top">Examples</a>
        </li>
    </ul>
    
</div>
<div id="body">