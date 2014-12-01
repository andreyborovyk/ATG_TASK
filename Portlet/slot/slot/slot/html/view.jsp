<%--
  View page for Slot portlet
  Displays content from slot.
  
  @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/view.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="javax.portlet.*" %>
<%@ page import="java.util.*" %>

<%@ page errorPage="/error.jsp"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"   %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>

<fmt:setBundle var="slotbundle" basename="atg.portlet.slot.slot"/>

<portlet:defineObjects/>

<%
  PortletPreferences prefs = renderRequest.getPreferences();
  String presentationPage = prefs.getValue("presentationPage", "defaultPresentation.jsp");
  presentationPage = presentationPage.trim();

  String currentSlotName = prefs.getValue("slotComponent", "");
  currentSlotName = currentSlotName.trim();
  renderRequest.setAttribute("slotName", currentSlotName);

  pageContext.setAttribute("presentationPage", presentationPage);
%>
<dspel:page>
  <c:catch var="includeError">
    <jsp:include page="<%=presentationPage%>"/>
  </c:catch>
  <c:if test="${not empty includeError}">
    <fmt:message key="presentationPageError" bundle="${slotbundle}"/>: <c:out value="${presentationPage}"/>
  </c:if>
</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/view.jsp#2 $$Change: 651448 $--%>
