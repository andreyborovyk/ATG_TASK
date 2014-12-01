<%--
  Viewer for map assets in diff/merge mode
  @param  mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Display each value in the map using the appropriate component viewer --%>    
  <dspel:getvalueof var="values" bean="${propertyView.formHandlerProperty}"/>
  <c:forEach var="value" items="${values}">
    <c:out value="${value.key}"/>:
    <c:set scope="request" var="componentValue" value="${value.value}"/>
    <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
    <br/>
  </c:forEach>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/mapViewer.jsp#2 $$Change: 651448 $--%>
