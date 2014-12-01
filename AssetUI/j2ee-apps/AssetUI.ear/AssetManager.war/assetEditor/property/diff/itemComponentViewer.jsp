<%--
  Component viewer for RepositoryItem assets in diff/merge mode
  @param  mpv             A request scoped, MappedPropertyView item for this view
  @param  componentValue  The current component value
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="value"        value="${requestScope.componentValue}"/>
    
  <%-- Display the component value --%>
  <c:set var="displayName" value="${value.itemDisplayName}"/>
  <c:if test="${empty displayName}">
    <c:set var="displayNameProp"
           value="${propertyView.repositoryItemDescriptor.itemDisplayNameProperty}"/>
    <c:if test="${not empty displayNameProp}">
      <c:set var="displayName" value="${value[displayNameProp]}"/>
    </c:if>
  </c:if>
  <c:out value="${displayName}"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/itemComponentViewer.jsp#2 $$Change: 651448 $--%>
