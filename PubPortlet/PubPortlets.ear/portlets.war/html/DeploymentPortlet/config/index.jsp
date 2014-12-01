<!-- BEGIN FILE index.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>

<c:choose>
 <c:when test="${addSite && !fromMenu}">
  <%@ include file="config_add_site_tabs.jspf" %>
 </c:when>
 <c:when test="${makeChangesLive && !fromMenu}">
   <dspel:include page="config_make_live.jsp"/>
  </c:when>
 <c:when test="${goToConfigDetailsTabs != null && !fromMenu}">
  <%@ include file="config_details_tabs.jspf" %>
  </c:when><%-- end details--%>
 <c:otherwise>
    <%@ include file="sites.jspf" %>
  </c:otherwise>
</c:choose>

</dspel:page>
<!-- END FILE index.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/index.jsp#2 $$Change: 651448 $--%>
