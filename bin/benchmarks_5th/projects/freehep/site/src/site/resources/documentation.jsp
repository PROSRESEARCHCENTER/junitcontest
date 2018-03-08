<%@page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 5.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
</head>

<body>
<h2>Documentation</h2>
<h3>API
    Documentation (automatically generated)</h3>
<ul>
<c:import url="conf/releases.xml" var="xml"/>
<x:parse var="releases" xml="${xml}" systemId="conf/releases.xml" />
<x:forEach select="$releases//release">
   <li><a href="/doc/release/<x:out select=".//@dir"/>/doc/api/index.html">Release <x:out select=".//@version"/></a></li>
</x:forEach>
  <li><a href="/lib/freehep/api/index.html">Developer</a> (this corresponds to the &quot;head&quot; of the CVS repository)</li>
</ul>
<h3>Package Status (automatically generated)</h3>
<ul>
<x:forEach select="$releases//release">
   <li><a href="/doc/release/<x:out select=".//@dir"/>/doc/api/status.html">Release <x:out select=".//@version"/></a></li>
</x:forEach>
  <li><a href="/lib/freehep/api/status.html">Developer</a> (this corresponds to the &quot;head&quot; of the CVS repository)</li>
</ul>
<h3>Manuals and User Guides</h3>
<ul>
  <li><a href="http://yappi.freehep.org">YaPPI</a> (the XML Particle Property
    Database)
  <li><a href="http://java.freehep.org/lib/freehep/doc/root/index.shtml">Root IO</a>
    - A Java implementation of Root IO
  <li><a href="http://java.freehep.org/lib/freehep/doc/aida/index.html">AIDA</a>
    - Abstract Interfaces for Data Analysis</li>
  <li><a href="http://heprep.freehep.org">HepRep</a> - a Generic Interface
    Definition for HEP Event Display Representables</li>
</ul>
<h2>Presentations</h2>
<ul>
  <li><a href="talks/ISAT201.ppt">Talk on FreeHEP Java library</a> presented at <a href="http://conferences.fnal.gov/acat2000/">ACAT2000</a>.</li>
</ul>
</body>

</html>