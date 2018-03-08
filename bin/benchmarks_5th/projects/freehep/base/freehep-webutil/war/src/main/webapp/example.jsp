<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%@taglib prefix="example" uri="http://java.freehep.org/example" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

    
    <c:set var="sampleURL" value="sample1.jsp"/>    
    <example:example name="sample1" url="${sampleURL}"/>
    
    <example:example name="sample2" url="${sampleURL}" codetabname="code here" exectabname="exec tab here" />
    
    </body>
</html>
