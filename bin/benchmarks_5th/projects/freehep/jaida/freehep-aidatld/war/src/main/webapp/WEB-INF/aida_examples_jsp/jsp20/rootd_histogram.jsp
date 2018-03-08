<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 


<c:set var="scheme" value="root" />
<c:set var="storeType" value="xroot" />
<c:set var="authority" value="glast01.slac.stanford.edu:1095" />
<c:set var="path" value="pawdemo.root" />
<c:set var="histPath" value="/h110" />

<c:set var="rootDataURI" value="${scheme}://${authority}${path}" />
<aida:tree var="histos" storeName="${rootDataURI}" storeType="${storeType}" options="scheme=anonymous">
    <aida:include name="${histPath}" />
</aida:tree>

<aida:plotter>
    <aida:region>
        <aida:plot var="${histos[0]}" />
    </aida:region>
</aida:plotter>
