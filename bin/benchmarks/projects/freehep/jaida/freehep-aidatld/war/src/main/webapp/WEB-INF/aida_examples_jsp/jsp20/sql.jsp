<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<sql:setDataSource var="sampleEventDatabase" url="jdbc:hsqldb:." driver="org.hsqldb.jdbcDriver" />
<sql:query var="qry" dataSource="${sampleEventDatabase}">
select
  runNumber,
  eventNumber,
  dateTime,
  energy,
  probability
from
  SampleEvent
</sql:query>

<aida:tuple var="tuple" query="${qry}" />
<aida:datapointset var="xyPlot" tuple="${tuple}" xaxisColumn="DATETIME" yaxisColumn="ENERGY" />
<aida:plotter>
  <aida:region>
    <aida:style>
      <aida:attribute name="showLegend" value="false"/>
      <aida:attribute name="showStatisticsBox" value="false"/>
      <aida:style type="xAxis">
        <aida:attribute name="type" value="date"/>
      </aida:style>
      <aida:style type="data">
        <aida:attribute name="showHistogramBars" value="false"/>
        <aida:attribute name="showErrorBars" value="false"/>
        <aida:attribute name="showDataPoints" value="true"/>
        <aida:attribute name="fillHistogramBars" value="false"/>
        <aida:style type="marker">
          <aida:attribute name="size" value="6"/>
        </aida:style>
      </aida:style>
    </aida:style>
    <aida:plot var="${xyPlot}" />
  </aida:region>
</aida:plotter>
