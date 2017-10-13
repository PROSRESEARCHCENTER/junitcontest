<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@page isELIgnored="false" %>

<c:set var="ref" value="${param.example}"/>
<c:set var="plotPerPage" value="4"/>

<!-- 
    Create AIDA Tree from Root file. 
    rootDataURI - name of the root file, defined elsewhere.
-->
<aida:tree storeName="${rootDataURI}" storeType="root"/>

<!-- Get data from the AIDA Tree and put into "aidaObjects" variable -->
<aida:objects storeName="${rootDataURI}" var="aidaObjects" path="/PHI" />

<!-- Simplest use of plotset tag -->
<aida:plotset maxplots="${plotPerPage}" plots="${aidaObjects}">
    <aida:plotsetbar url="${ref}"/><br>
</aida:plotset>
