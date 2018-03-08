<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="example" uri="http://java.freehep.org/example" %>

<t:header example="${param.example}"/>

<c:if test="${ ! empty param.example }">
    <c:set var="thisExampleURL" value="${param.example}" scope="session" />
</c:if>

<example:example name="${thisExampleURL}" url="${thisExampleURL}" codetabname="source" exectabname="plot" />

</div>
</body>
</html>