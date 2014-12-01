<%--
  Default property viewer for timestamp values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:getvalueof var="timestamp" bean="${mpv.formHandlerProperty}"/>

  <c:choose>
    <c:when test="${ empty mpv.attributes.pattern }">
      <fmt:formatDate value="${timestamp}" 
        type="${mpv.attributes.type}" 
        dateStyle="${mpv.attributes.dateStyle}"
        timeStyle="${mpv.attributes.timeStyle}"
        timeZone="${mpv.attributes.timeZone}"/>
    </c:when>
    <c:otherwise>
      <fmt:formatDate value="${timestamp}" 
        pattern="${mpv.attributes.pattern}"
        timeZone="${mpv.attributes.timeZone}"/>
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampViewer.jsp#2 $$Change: 651448 $--%>
