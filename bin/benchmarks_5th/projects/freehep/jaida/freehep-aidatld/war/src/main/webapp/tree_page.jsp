<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@page isELIgnored="false" %>

<html>
    <head>
        <title>AIDA Tree Frame</title>
        
        <!-- 
            All references clicked on this page will be displayed in "plotFrame" frame
         -->
        <base target="plotFrame">
    </head>
    <body>
        
        <!-- 
            If file name is empty.
            rootDataURI - name of the root file, defined elsewhere.
         -->
        <c:if test="${empty rootDataURI}">
            <h3>
                <b>Tree Frame</b>
            </h3>    
        </c:if>
        
        <!-- Do anything only if file name is non-empty -->
        <c:if test="${!empty rootDataURI}">
            
            <!-- Create AIDA Tree from Root file -->
            <aida:tree 
                storeName="${rootDataURI}" storeType="root">
            </aida:tree>
            
            <!-- Display created AIDA Tree -->
            <aida:displaytree 
                leafHref="plot_page.jsp?plotHref=%p" 
                folderHref="plot_page.jsp?plotHref=%p" 
                showFolderHrefForNodesWithLeavesOnly="true"
                rootLabel="Demo ROOT Data" 
                showItemCount="true" 
                rootVisible="false" 
                storeName="${rootDataURI}">
            </aida:displaytree>

        </c:if>
    </body>
</html>
