<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib prefix="tab" uri="http://java.freehep.org/tabs-taglib" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link type="text/css" rel="stylesheet" href="<c:url value="tabs.css"/>">
    </head>
    <body>

    <h2> This is to test that only the selected tab body is executed </h2>
      <tab:tabs name="ProcessTabs" param="process">
         <tab:tab name="A">A<c:set var="last" value="A"/></tab:tab>
         <tab:tab name="B">B<c:set var="last" value="B"/></tab:tab>
       </tab:tabs>
       <p> The value here : <b>${last}</b> should be identical to the selected tab's body. </p>

    <tab:tabs name="basic" param="basicTab">
        <tab:tab name="tab1" value="1" >
        <br>
        <p align="center">
        <b>body</b> of tab tab1
        </p>
        </tab:tab>
        <tab:tab name="ciao" value="2">body of tab ciao</tab:tab>
    </tab:tabs>
<br>
    <tab:tabs name="mamma" position="bottom">
        <tab:tab name="tab1" >
        <br>
        <p align="center">
        <b>body</b> of tab tab1
        </p>
        </tab:tab>
        <tab:tab name="ciao" >body of tab ciao</tab:tab>
    </tab:tabs>
   <br> 

    <tab:tabs name="red" color="red" selectedColor="yellow">
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
    </tab:tabs>
    
    <br>

    <tab:tabs name="pippo" usestylesheet="true" id="tabs" var="pippoSelectedTab" scope="session">
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
        <tab:tab name="home" href="index.jsp" />
    </tab:tabs>
    
    <br>

    <tab:tabs name="pippo2" showline="true" >
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
        <tab:tab name="home" href="index.jsp" />
    </tab:tabs>
    
    <br>
    <br>

    <tab:tabs name="pippo5" showline="true" position="bottom">
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
        <tab:tab name="home" href="index.jsp" />
    </tab:tabs>
    
    <br>
    <br>

    <tab:tabs name="pippo3"  var="selectedPippo">
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
        <tab:tab name="home" href="index.jsp" />
    </tab:tabs>
    <tab:tabBody>
    <p>
        This is the body of the tab selected ${selectedPippo}
        </p>
    </tab:tabBody>

    
    
    <br>

    <tab:tabBody>
    <p>
        This is the body of the tab selected ${selectedPippo4}
        </p>
    </tab:tabBody>
    <tab:tabs name="pippo4"  var="selectedPippo4" position="bottom">
        <tab:tab name="tab1" />
        <tab:tab name="ciao" />
        <tab:tab name="home" href="index.jsp" />
    </tab:tabs>
    

<br>
    

<tab:tabs name="test">
         <c:forEach var="i" begin="1" end="10">
            <tab:tab name="Tab ${i}">Content of tab ${i}</tab:tab>
         </c:forEach>
</tab:tabs>
    


<a href="http://localhost:8080/freehep-webutil/tabs.jsp">self</a>
    
    
    <img width="150" height="150" src="/freehep-webutil/tabCornerServlet.jsp?type=0"/>
    
    </body>
</html>
