<%@page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<html>

<head>
<meta http-equiv="Content-Language" content="en-us">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 5.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>The FreeHEP Java Library</title>
</head>

<body>

      <h2>Download</h2>
      <p>For each release you can download:</p>
      <ul>
        <li>freehep-xxx -- The binaries (jar files)</li>
        <li>freehep-xxx-doc -- The documentation (including javadoc) (also 
        <a href="documentation.jsp">available online</a>)</li>
        <li>freehep-xxx-src -- The source code (also available 
        <a href="cvs.thtml">via CVS</a> or
        <a href="/jcvslet/JCVSlet/list/freehep/freehep">online</a>)</li>
      </ul>
      <p>The documentation for each release includes a status.html file which 
      summarizes the status of each major package within the release.</p>
      <p>All files are available as both .zip (primarily for windows users) and 
      .tar.gz (primarily for unix users).</p>
      <p>Currently available releases:</p>

      <ul>
<c:import url="conf/releases.xml" var="xml"/>
<x:parse var="releases" xml="${xml}" systemId="conf/releases.xml" />
<x:forEach select="$releases//release">
   <li>
   <a href="ftp://ftp.slac.stanford.edu/software/freehep/release/<x:out select=".//@dir"/>">Release <x:out select=".//@version"/></a> <x:out select=".//@date"/> -- 
   <a href="/doc/release/<x:out select=".//@dir"/>/doc/api/status.html">status</a></li>
</x:forEach>
</ul>
      <p>If you are a new user of FreeHEP, consider being added to our <a href="users.thtml">users</a>
      page.</p>
</body>

</html>