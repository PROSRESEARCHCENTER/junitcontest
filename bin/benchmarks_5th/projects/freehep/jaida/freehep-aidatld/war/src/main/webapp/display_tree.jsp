<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page isELIgnored="false" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Example: Display Tree and Plots</title>
        <base target="_self">
    </head>

    <c:set var="tabid" value="display_tree_code.jsp#displayTree" scope="page"/>
    <c:if test="${!empty param.tab}">
        <c:set var="tabid" value="${param.tab}" scope="page"/>
    </c:if>

    <FRAMESET rows="180,*" BORDER="0">
        <FRAME SRC="display_tree_tabs.jsp?tab=${tabid}" NAME="tabFrame" scrolling="no" >
        <FRAME NAME="resultFrame" src="${tabid}">
    </FRAMESET>
</html>
