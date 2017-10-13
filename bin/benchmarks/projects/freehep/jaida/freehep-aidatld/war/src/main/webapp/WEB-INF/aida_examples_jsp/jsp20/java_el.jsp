<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>

<jsp:useBean id="examplePlots" class="hep.aida.example.ExamplePlots" />

<aida:plotter var="${examplePlots.histogram}"/>
