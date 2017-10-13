<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql" %>

<sql:setDataSource var="isoc_glast_cal" url="jdbc:oracle:thin:@slac-oracle.slac.stanford.edu:1521:SLACPROD" driver="oracle.jdbc.driver.OracleDriver" user="GLAST_CAL" password="9square#" />
<sql:query var="qry" dataSource="${isoc_glast_cal}">
SELECT 
  usecs0,
  (sumv / numValues) as mean
FROM v2hkSTATVALfields
WHERE
  IdTlm = 1037
AND
    usecs0 >= 1098403200
AND
    usecs0 < 1198489600
ORDER BY usecs0 ASC
</sql:query>

<aida:tuple var="tuple" query="${qry}" />
<aida:dataPointSet var="xyPlot" tuple="${tuple}" xaxisColumn="USECS0" yaxisColumn="MEAN" />
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
