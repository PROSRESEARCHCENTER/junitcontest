<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 


<jsp:useBean id="examplePlots" class="hep.aida.example.ExamplePlots" />
<c:set var="histogram" value="${examplePlots.histogram}" scope="request" />

<aida:plotter var="histogram"/>
