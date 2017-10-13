<%@taglib prefix="example" uri="http://java.freehep.org/example" %>
<%@taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="tab"     uri="http://java.freehep.org/tabs-taglib" %>

<%@ attribute name="url"  required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="codetabname" required="false" %>
<%@ attribute name="exectabname" required="false" %>

<c:set var="codeTab" value="${ empty codetabname ? 'code' : codetabname }"/>
<c:set var="execTab" value="${ empty exectabname ? 'execution' : exectabname }"/>


<tab:tabs name="${name}">
    <tab:tab name="${codeTab}">
<example:code path="${url}" var="source" />
<pre>
<c:out  value="${source}" escapeXml="true"/>
</pre>
    </tab:tab>
    <tab:tab name="${execTab}">
        <c:import  url="${url}" />    
    </tab:tab>
</tab:tabs>





