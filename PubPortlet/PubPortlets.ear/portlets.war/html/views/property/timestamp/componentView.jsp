<%--
  Default view-mode view for component timestamp data, uses JSTL formatDate
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<%-- Show the actual field --%>

<dspel:getvalueof var="timestamp"
  bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}"/>

<c:choose>
  <c:when test="${ empty mpv.componentAttributes.pattern }">
    <fmt:formatDate value="${timestamp}" 
      type="${mpv.componentAttributes.type}" 
      dateStyle="${mpv.componentAttributes.dateStyle}"
      timeStyle="${mpv.componentAttributes.timeStyle}"
      timeZone="${mpv.componentAttributes.timeZone}"/>
  </c:when>
  <c:otherwise>
    <fmt:formatDate value="${timestamp}" 
      pattern="${mpv.componentAttributes.pattern}"
      timeZone="${mpv.componentAttributes.timeZone}"/>
  </c:otherwise>
</c:choose>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/timestamp/componentView.jsp#2 $$Change: 651448 $--%>
