<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="hep.aida.*" %>
<%@taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="http://glast-ground.slac.stanford.edu/Commons/css/glastCommons.jsp" rel="stylesheet" type="text/css">
        <title>Plot Memory Test</title>
    </head>
    <body>

        <h1>Plot Memory Test</h1>
        
        
        <%
            java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
        %>
        
        <c:if test="${empty numberOfPlots}">
            <c:set var="numberOfPlots" value="5" scope="session" />
        </c:if>        
        <c:if test="${! empty param.numberOfPlots}">
            <c:set var="numberOfPlots" value="${param.numberOfPlots}" scope="session"/>
        </c:if>
        
        <c:if test="${empty selectedDataType}">
            <c:set var="selectedDataType" value="0" scope="session" />
        </c:if>        
        <c:if test="${! empty param.selectedDataType}">
            <c:set var="selectedDataType" value="${param.selectedDataType}" scope="session"/>
        </c:if>

        <c:if test="${empty maxNumberOfPlots}">
            <c:set var="maxNumberOfPlots" value="5" scope="session" />
        </c:if>        
        <c:if test="${! empty param.maxNumberOfPlots}">
            <c:set var="maxNumberOfPlots" value="${param.maxNumberOfPlots}" scope="session" />
        </c:if>
        <c:set var="aida.max.plots.per.session" value="${maxNumberOfPlots}" scope="session"/>
        
        
        <c:set var="testNames" value="${fn:split('Random,Java Histogram1D,Java Histogram2D,Data Point Set,Root Histogram1D,Root Histogram2D,XRoot Histogram1D,XRoot Histogram2D,XRoot Proxy Histogram1D,XRoot Proxy Histogram2D',',')}"/>
        <c:set var="numberOfTests" value="${fn:length(testNames)-1}" scope="session"/>
        
        <form name="JavaHist1DForm">
            <table cellpadding="5" cellspacing="5" class="filterTable">
                <tr>
                    <th align="left">Number of Plots</th>
                    <td><input type="text" name="numberOfPlots" value="${numberOfPlots}"/></td>
                </tr>   
                <tr>
                    <th align="left">AIDATLD Max Number of Plots</th>
                    <td><input type="text" name="maxNumberOfPlots" value="${maxNumberOfPlots}"/></td>
                </tr>   
                <tr>
                    <th align="left">Select Data</th>
                    <td>
                        <select name="selectedDataType">
                            <c:forEach var="i"  begin="0" end="${fn:length(testNames)-1}" >
                                <option value="${i-1}" <c:if test="${selectedDataType==i-1}">selected</c:if>>${testNames[i]}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="right"> <input type="submit" value="Perform Test" name="PerformTest"> </td>
                </tr>
            </table>
        </form>

        <form name="GCForm">
            <table cellpadding="5" cellspacing="5" class="filterTable">
                <tr>
                    <th align="left">Run Garbage Collettor</th>
                    <td> <input type="submit" value="Garbage Collector" name="RunGC"> </td>
                </tr>
            </table>
        </form>
        
        <form name="InvalidateSessionForm">
            <table cellpadding="5" cellspacing="5" class="filterTable">
                <tr>
                    <th align="left">Invalidate Session</th>
                    <td> <input type="submit" value="Invalidate Session" name="InvalidateSession"> </td>
                </tr>
            </table>
        </form>

        
        <c:if test="${ ! empty param.RunGC }">
            <p>Garbage Collected</p>
            <%
                runtime.gc();
            %>
        </c:if>
        
        
        <c:if test="${ ! empty param.InvalidateSession }">
            <p>Session Invalidated</p>
            <%
                session.invalidate();
            %>
        </c:if>

        
        <%
            long usedMem = (runtime.totalMemory() - runtime.freeMemory())/( (long) 1000000.);
            pageContext.setAttribute("initialMemory",usedMem,PageContext.REQUEST_SCOPE);
        %>
                
        <p>Current Memory: ${initialMemory} </p>

        <c:if test="${ ! empty numberOfPlots && numberOfPlots > 0 && ! empty param.PerformTest}" >
        
            <p>See the memory usage at the <a href="#memoryUsage">bottom</a> of the page</p>
    
            <%-- AIDA factories for java generated objects --%>
            <%
                IAnalysisFactory af = IAnalysisFactory.create();
                ITree tree = af.createTreeFactory().create();
                IHistogramFactory hf = af.createHistogramFactory(tree);
                IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);
                // Create an IDataPointSet.
                IDataPointSet dps = dpsf.create("dps","IDataPointSet",1);
                pageContext.setAttribute("dps",dps,PageContext.REQUEST_SCOPE);
            %>

            <%-- Root Tree for direct opening of file --%>
            <% 
                String rootDataURI = application.getRealPath("/WEB-INF/aida_examples_jsp/data/pawdemo.root");
                session.setAttribute( "rootDataURI" , rootDataURI );
            %>
            
            <%-- XRoot file --%>
            <c:set var="rootFile" value="xroot://datadevlnx01///glast/pawdemo.root" />

            <c:forEach var="i"  begin="1" end="${numberOfPlots}" >
            
                <c:set var="currentDataType" value="${selectedDataType}" scope="session"/>
                <%
                    int dataType = Integer.valueOf((String)session.getAttribute("currentDataType")).intValue();
                    java.util.Random r = new java.util.Random();
                    if ( dataType < 0 )
                        dataType = r.nextInt( ((Long)session.getAttribute("numberOfTests")).intValue() );
                    session.setAttribute("currentDataType",String.valueOf(dataType));
                %>            
            
                <c:choose>
                    <c:when test="${currentDataType == 0 || currentDataType == 1 || currentDataType == 2}">
                        <%
                            dataType = Integer.valueOf((String)session.getAttribute("currentDataType")).intValue();

                            Object dataObj = null;

                            if ( dataType == 0 ) {
                                //Create a 1D Histogram
                                IHistogram1D h1 = hf.createHistogram1D("h","hist 1D",100,-5,5);
                                for ( int i = 0; i < 1000; i++ )
                                    h1.fill( r.nextGaussian() );
                                dataObj = h1;
                            }
                            if ( dataType == 1 ) {
                                //Create a 2D Histogram
                                IHistogram2D h2 = hf.createHistogram2D("h2","hist 2D",100,-5,5,100,-5,5);
                                for ( int i = 0; i < 1000; i++ )
                                    h2.fill( r.nextGaussian(), r.nextGaussian() );
                                dataObj = h2;
                            }
                            if ( dataType == 2 ) {
                                // Create an IDataPointSet.
                                IDataPointSet data = dpsf.create("data","IDataPointSet",1);
                                for ( int i = 0; i < 100; i++ )
                                    data.addPoint().coordinate(0).setValue( r.nextGaussian() );
                                dataObj = data;
                            }

                            pageContext.setAttribute("dataObj",dataObj,PageContext.PAGE_SCOPE);
                        %>
                    </c:when>
                    <c:when test="${currentDataType == 3 || currentDataType == 4}">
                        <aida:tree storeName="${rootDataURI}" storeType="root" />
                        <c:choose>
                            <c:when test="${currentDataType == 3}">
                                <aida:objects var="plotObjList" storeName="${rootDataURI}" path="/h110"/>
                            </c:when>
                            <c:otherwise>
                                <aida:objects var="plotObjList" storeName="${rootDataURI}" path="/h210"/>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="dataObj" value="${plotObjList[0]}"/>
                    </c:when>
                    <c:when test="${currentDataType == 5 || currentDataType == 6}">            
                        <c:set var="xrootdOptions" value="automount=false,scheme=anonymous,useProxies=false"/>
                        <aida:tree storeName="${rootFile}" storeType="xroot" options="${xrootdOptions}" />                        
                        <c:choose>
                            <c:when test="${currentDataType == 5}">
                                <aida:objects var="plotObjList" storeName="${rootFile}" path="/h110"/>
                            </c:when>
                            <c:otherwise>
                                <aida:objects var="plotObjList" storeName="${rootFile}" path="/h210"/>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="dataObj" value="${plotObjList[0]}"/>                        
                    </c:when>
                    <c:when test="${currentDataType == 7 || currentDataType == 8}">
                        <c:set var="xrootdOptions" value="automount=false,scheme=anonymous,useProxies=true"/>
                        <aida:tree storeName="${rootFile}" storeType="xroot" options="${xrootdOptions}" />
                        <c:choose>
                            <c:when test="${currentDataType == 7}">
                                <aida:objects var="plotObjList" storeName="${rootFile}" path="/h110"/>
                            </c:when>
                            <c:otherwise>
                                <aida:objects var="plotObjList" storeName="${rootFile}" path="/h210"/>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="dataObj" value="${plotObjList[0]}"/>
                    </c:when>
                </c:choose>
            
                <h2>Plot ${i} type ${testNames[currentDataType+1]}</h2>

                <aida:plotter>
                    <aida:region>
                        <aida:plot var="${dataObj}"/>
                    </aida:region>
                </aida:plotter>
            
                <%
                    usedMem = runtime.totalMemory() - runtime.freeMemory();
                    IDataPointSet d = (IDataPointSet) pageContext.getAttribute("dps",PageContext.REQUEST_SCOPE);
                    if ( d != null )
                        d.addPoint().coordinate(0).setValue((double) usedMem/ 1000000.);
                %>
            </c:forEach>
            
            <a name="memoryUsage"></a>
            
            <h2>Memory used for ${numberOfPlots} plots.</h2>

            <p>Initial Memory: ${initialMemory} </p>
            
            <aida:plotter>
                <aida:region>
                    <aida:plot var="${dps}"/>
                </aida:region>
            </aida:plotter>
            
        </c:if>

        

    </body>
</html>
