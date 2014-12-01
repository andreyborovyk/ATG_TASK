<%--
  Default view-mode view for string data
  @param   mpv  A request scoped, MappedPropertyView item for this view

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<%-- Show the actual field --%>
<c:choose>
  <c:when test="${! empty componentObject }">
    <c:out value="${componentObject}"/>
  </c:when>
  <c:otherwise>
    <dspel:valueof
      bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}"/>
  </c:otherwise>
</c:choose>
</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/string/simpleComponentView.jsp#2 $$Change: 651448 $--%>