<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<t:header/>

<h2>Examples</h2>

<p>
    The AIDA Tag Library allows you to easily insert live plots into Java Server Pages (.jsp) files, and is
    designed to work in a container supporting JSP 2.0.
</p>

<p>
    The following examples show some of the functionality of the aida
    tags.  These example pages also allow you to view the JSP source,
    so you can see how you might interface with the tags in your own
    application. 
</p>

<c:set var="exampleDir" value="/WEB-INF/aida_examples_jsp/jsp20"/>

<table cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/single_plot.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Simple plot with Java embedded code</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/java_el.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Plot a Java generated histogram stored in EL variable</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/java_overlay.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Overlay two Java generated XY plots</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/region.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Laying out multiple plots with &lt;region&gt;</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/jstl_foreach.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Laying out multiple plots with JSTL &lt;c:forEach&gt;</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/styles.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Using &lt;style&gt; and &lt;attribute&gt;</a>
        </td>	
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/plotset_simple.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Multiple plots - simple, using &lt;aida:plotset&gt;</a>
        </td>
    </tr>
    <tr>
        <td>
            <c:url var="exampleURL" value="/example.jsp" >
                <c:param name="example" value="${exampleDir}/plotset_complex.jsp" />
            </c:url>
            <a href="<c:out value='${exampleURL}' />">Multiple plots - complex, using &lt;aida:plotset&gt;</a>
        </td>
    </tr>
    <tr>
        <td>
            <a href="display_tree.jsp" target="_self">Display Clickable AIDA Tree</a>
        </td>
    </tr>
</table>
