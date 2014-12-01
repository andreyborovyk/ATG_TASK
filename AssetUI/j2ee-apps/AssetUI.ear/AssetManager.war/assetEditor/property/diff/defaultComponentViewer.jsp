<%--
  Default component viewer for assets in diff/merge mode
  @param  componentValue  The current component value
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Display the component value --%>
  <c:out value="${requestScope.componentValue}"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/defaultComponentViewer.jsp#2 $$Change: 651448 $--%>
