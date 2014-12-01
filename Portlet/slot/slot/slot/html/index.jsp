<%--
  Home page for Slot portlet
  Controls which page to present depending on mode of the portlet.
  
  @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page errorPage="/error.jsp" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"   %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>

<fmt:setBundle var="slotbundle" basename="atg.portlet.slot.slot"/>

<dspel:page>

<portlet:defineObjects/>

<c:choose>
  <c:when test="${renderRequest.portletMode eq 'view'}">
    <jsp:include page="view.jsp"/>
  </c:when>

  <c:when test="${renderRequest.portletMode eq 'help'}">
    <jsp:include page="help.jsp"/>
  </c:when>

  <c:when test="${renderRequest.portletMode eq 'edit_defaults'}">
    <jsp:include page="edit.jsp"/>
  </c:when>

  <c:otherwise>
  </c:otherwise>
</c:choose>

</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/index.jsp#2 $$Change: 651448 $--%>
