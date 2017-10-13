<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="example" uri="http://java.freehep.org/example" %>
<%@page isELIgnored="false" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Example: Display Tree and Plots</title>
        <base target="_self">
        <link rel="stylesheet" href="<c:url value='css/screen.css' />" type="text/css" media="screen, print" />
    </head>
    
    <body style="margin-left:74px; margin-right:74px;">
        <div style="padding:0px;border-color: #a2d7c8; border-style: solid; border-width: 1px; clear: both;">
            <h3>
                Top Page with Two Frames: display_tree_plots.jsp
            </h3><br>
            <example:code path="display_tree_plots.jsp" var="source" />
            <pre>
<c:out  value="${source}" escapeXml="true"/>
            </pre>
        </div>
        <br><br>
        
        <div style="padding:0px;border-color: #a2d7c8; border-style: solid; border-width: 1px; clear: both;">
            <h3>
                Tree Page (left frame): tree_page.jsp
            </h3><br>
            <example:code path="tree_page.jsp" var="source" />
            <pre>
<c:out  value="${source}" escapeXml="true"/>
            </pre>
        </div>
        <br><br>
        
        <div style="padding:0px;border-color: #a2d7c8; border-style: solid; border-width: 1px; clear: both;">
            <h3>
                Plot Page (right frame): plot_page.jsp; "optional" blocks add extra functionality and can be removed
            </h3><br>
            <example:code path="plot_page.jsp" var="source" />
            <pre>
<c:out  value="${source}" escapeXml="true"/>
            </pre>
        </div>
        
    </body>
</html>
