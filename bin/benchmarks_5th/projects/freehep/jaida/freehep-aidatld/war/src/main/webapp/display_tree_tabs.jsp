<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="tab" uri="http://java.freehep.org/tabs-taglib" %>
<%@page isELIgnored="false" %>

<t:header/>

         <tab:tabs href="display_tree.jsp" 
                  name="displayTree" param="tab" 
                  scope="page">
            <tab:tab name="source" value="display_tree_code.jsp" target="_top">
            </tab:tab>
            <tab:tab name="plot" value="display_tree_plots.jsp" target="_top">
            </tab:tab>
        </tab:tabs>


  </body>
</html>
