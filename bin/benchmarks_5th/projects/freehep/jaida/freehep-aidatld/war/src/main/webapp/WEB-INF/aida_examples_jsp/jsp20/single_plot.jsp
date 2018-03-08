<%@taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="hep.aida.*" %>

<%
  IAnalysisFactory af = IAnalysisFactory.create();
  ITree tree = af.createTreeFactory().create();
  IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);

  // Create a two dimensional IDataPointSet.
  IDataPointSet dps2D = dpsf.create("dps2D","Two Dimensional IDataPointSet",2);
  // Fill the two dimensional IDataPointSet
  double[] yVals2D = { 0.12, 0.22, 0.35, 0.42, 0.54 , 0.61 };
  double[] yErrP2D = { 0.01, 0.02, 0.03, 0.03, 0.04 , 0.04 };
  double[] yErrM2D = { 0.02, 0.02, 0.02, 0.04, 0.06 , 0.05 };
  double[] xVals2D = { 1.5, 2.6, 3.4, 4.6, 5.5 , 6.4 };
  double[] xErrP2D = { 0.5, 0.5, 0.4, 0.4, 0.5 , 0.5 };
  for ( int i = 0; i<yVals2D.length; i++ ) {
    dps2D.addPoint();
    dps2D.point(i).coordinate(0).setValue( xVals2D[i] );
    dps2D.point(i).coordinate(0).setErrorPlus( xErrP2D[i] );
    dps2D.point(i).coordinate(1).setValue( yVals2D[i] );
    dps2D.point(i).coordinate(1).setErrorPlus( yErrP2D[i] );
    dps2D.point(i).coordinate(1).setErrorMinus( yErrM2D[i] );
  }
  pageContext.setAttribute("dps2D",dps2D,PageContext.REQUEST_SCOPE);
%>

<aida:plotter>
    <aida:region>
        <aida:plot var="${dps2D}"/>
    </aida:region>
</aida:plotter>
