<%--
  Default editor for timestamp elements of collection properties.

  The following request-scoped variables are expected to be set:
  
  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampComponentViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

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
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampComponentViewer.jsp#2 $$Change: 651448 $--%>
