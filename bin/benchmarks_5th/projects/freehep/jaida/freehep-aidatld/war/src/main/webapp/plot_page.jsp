<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@page isELIgnored="false" %>

<head>
    <title>AIDA Plot Frame</title>
    <base target="_self">
</head>
<body>   
    
    <!-- If nothing to plot (empty path) -->
    <c:if test="${empty param.plotHref && empty aidaPath}">
        <h3>
            <b>Plot Frame</b>
        </h3>    
    </c:if>
    
    <!-- Do plotting only if path is non-empty -->
    <c:if test="${!empty param.plotHref || !empty aidaPath}">
        
        <!-- 
            Get AIDA Tree. 
            rootDataURI - name of the root file, defined elsewhere.
         -->
        <aida:tree 
            storeName="${rootDataURI}" storeType="root">
        </aida:tree>
        
        <c:if test="${!empty param.plotHref}">
            <c:set var="aidaPath" value="${param.plotHref}" scope="session"/>
        </c:if>
        <c:set var="ref" value="plot_page.jsp?plotHref=${aidaPath}"/>
        
        
        <!-- Get data from the AIDA Tree and put into "aidaObjects" variable -->
        <aida:objects storeName="${rootDataURI}"
                      path="${aidaPath}"
                      var="aidaObjects">
        </aida:objects>
        
        <h3>
            <b>Path=${aidaPath}</b>
        </h3>    
        
        <!-- Display Plotter Style Editor that controls Errors, Markers, etc. (optional) -->
        <aida:plotterStyleEditor 
            selectorText="Style Editor"
            includeNormalization="false"
            includeComparison="false"
            action="${ref}"
            var="styleVar"
        />
        
        <!-- Create clickable Image Map only for multiple data only (optional) -->
        <c:set var="imgMap" value="false"/>
        <c:if test="${!empty aidaObjects && fn:length(aidaObjects) > 1}">
            <c:set var="imgMap" value="true"/>
        </c:if>
        
        <!-- Plot single or multiple data using "plotset" tag -->
        <aida:plotset statusvar="status" nplots="${fn:length(aidaObjects)}" 
                      createImageMap="${imgMap}" allowDownload="true" format="png">
            
            <!-- Display the default navigation bar inside the Table (optional) -->
            <aida:plotsetbar  var="barVar" url="${ref}">
                <c:if test="${barVar.npages > 1}">
                    <TABLE bgcolor="D0D0D0" border="1">
                        <TR>
                            <TD>
                                &nbsp;${barVar.defaultbar}&nbsp;
                            </TD>
                        </TR>
                    </TABLE><br>
                </c:if>
            </aida:plotsetbar>
            
            <!-- Format plotter style according to the Plotter Style Editor settings (optional) -->
            <c:if test="${!empty styleVar.showPlotStyle && styleVar.showPlotStyle != false}">
                <aida:style>
                    <aida:style type="statisticsBox">
                        <aida:attribute name="isVisible" value="${styleVar.showStatistics}"/>
                    </aida:style>
                    <aida:style type="legendBox">
                        <aida:attribute name="isVisible" value="${styleVar.showLegend}"/>
                    </aida:style>
                    <aida:style type="data">
                        <aida:style type="errorBar">
                            <aida:attribute name="isVisible" value="${styleVar.showError}"/>
                        </aida:style>
                        <c:if test="${!empty styleVar.markerSize && styleVar.markerSize != 'none'}">
                            <aida:style type="marker">
                                <aida:attribute name="isVisible" value="true"/>
                                <aida:attribute name="size" value="${styleVar.markerSize}"/>
                            </aida:style>
                        </c:if>
                    </aida:style>
                </aida:style>
            </c:if>
            
            <aida:region href="${ref}/${aida:objectName(aidaObjects[status.index])}">
                
                <aida:plot var="${aidaObjects[status.index]}">                    
                </aida:plot>
                
            </aida:region>
        </aida:plotset>
        
    </c:if>
    
</body>
</html>
