<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<aida:tree storeName="${rootDataURI}" storeType="root"/>
<aida:objects storeName="${rootDataURI}" var="objs" path="/" />

<c:set var="numHistos" value="2"/>

<c:set var="width" value="${1/numHistos}"/>
<c:set var="x" value="0"/>

<aida:plotter>
    <c:forEach var="i" end="${numHistos}" begin="1" step="1" >
        <c:set var="plotObject" value="${objs[i]}"/>
        <aida:region x="${x}" y="0" width="${width}" height="1">
            <aida:plot var="${plotObject}" />
        </aida:region>
        <c:set var="x" value="${x + width}"/>
    </c:forEach>
</aida:plotter>
