<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page isELIgnored="false" %>

<c:set var="plotsPerPage" value="4"/>
<c:set var="aidaPath" value="/PHI"/>
<c:set var="ref" value="${param.example}"/>

<aida:tree storeName="${rootDataURI}" storeType="root"/>

<aida:objects storeName="${rootDataURI}" var="objs" path="${aidaPath}" />

<!-- Make use of the aida:plotset tag body -->
<aida:plotset statusvar="status" maxplots="${plotsPerPage}" nplots="${fn:length(objs)}">
    
    <!-- 
        Set IPlotterStyle for the plotter: do not show errors 
        and do not fill histogram bars
    -->
    <aida:style>
        <aida:style type="statisticsBox">
            <aida:attribute name="isVisible" value="false"/>
        </aida:style>
        <aida:style type="data">
            <aida:style type="errorBar">
                <aida:attribute name="isVisible" value="false"/>
            </aida:style>
            <aida:style type="fill">
                <aida:attribute name="isVisible" value="false"/>
            </aida:style>
        </aida:style>
    </aida:style>
    
    <!-- Here we display the default navigation bar inside the Table -->
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
    
    <!-- Display even plots with red and odd plots with blue -->
    <c:set var="color" value="red"/>
    <c:set var="shape" value="dot"/>
    <c:if test="${(status.index)%2 == 1}">
        <c:set var="color" value="blue"/>
        <c:set var="shape" value="box"/>
    </c:if>
    
    <aida:region>
        
        <!-- Actually plot data here -->
        <aida:plot var="${objs[status.index]}">                    
            
            <!-- 
                Set IPlotterStyle for the plotter marker and outline.
                Please note that inside the <plot> tag the top-level 
                style is IDataStyle by default.
            -->
            <aida:style>
                <aida:style type="marker">
                    <aida:attribute name="isVisible" value="true"/>
                    <aida:attribute name="color" value="${color}"/>
                    <aida:attribute name="shape" value="${shape}"/>
                    <aida:attribute name="size" value="8"/>
                </aida:style>
                <aida:style type="line">
                    <aida:attribute name="isVisible" value="true"/>
                    <aida:attribute name="color" value="${color}"/>
                </aida:style>
            </aida:style>
            
        </aida:plot>
        
    </aida:region>
    
</aida:plotset>
