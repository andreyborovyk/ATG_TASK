<%--
  Viewer for RepositoryItem assets in diff/merge mode
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
    
  <%-- Display the property value --%>
  <dspel:getvalueof var="displayName"
    bean="${propertyView.formHandlerProperty}.itemDisplayName"/>
  <c:if test="${empty displayName}">
    <dspel:getvalueof var="displayName"
      bean="${propertyView.formHandlerProperty}.${propertyView.repositoryItemDescriptor.itemDisplayNameProperty}"/>
  </c:if>
  <c:out value="${displayName}"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/itemViewer.jsp#2 $$Change: 651448 $--%>
