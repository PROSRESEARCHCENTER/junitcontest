<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 


<jsp:useBean id="examplePlots" class="hep.aida.example.ExamplePlots" />
<c:set var="dps2D" value="${examplePlots.dps2D}" scope="request" />

<aida:plotter>
	<aida:region>
		<aida:plot var="${examplePlots.dps2D}" />
                <aida:plot var="dps2D" />
	</aida:region>
</aida:plotter>
