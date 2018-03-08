<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%@attribute name="path" required="true"%>
<%@attribute name="var" required="true"%>

<c:set var="outputSourceVar" value="${var}" scope="session" />
<c:set var="inputSourcePath" value="${path}" scope="session" />


<% 
    String output = (String)session.getAttribute("outputSourceVar");
    String filePath = (String)session.getAttribute("inputSourcePath");
    String fullPath = application.getRealPath(filePath);
    
    StringBuffer contents = new StringBuffer();
    java.io.BufferedReader input = null;
    try {
      input = new java.io.BufferedReader( new java.io.FileReader( new java.io.File(fullPath ) ) );
      String line = null;
      boolean addEol = false;
      while (( line = input.readLine()) != null){
          if ( addEol )
            contents.append(System.getProperty("line.separator"));              
          if ( !addEol )
              addEol = true;
        contents.append(line);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        if (input!= null) {
          input.close();
        }
      }
      catch (java.io.IOException ex) {
        ex.printStackTrace();
      }
    }
        
    session.setAttribute( output , contents.toString() );
%>
