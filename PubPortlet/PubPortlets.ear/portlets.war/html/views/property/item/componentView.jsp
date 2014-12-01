<%--
  Default read-only view for arrays
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/componentView.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>

<c:if test="${! empty componentObject }">
  <c:set var="displayName" value="${componentObject.itemDisplayName}"/>
  <c:set var="asset" value="${componentObject}"/>
</c:if>

<c:if test="${ empty asset }">
  <dspel:getvalueof var="asset"
    bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}"/>
</c:if>

<c:if test="${ empty displayName }">
  <dspel:getvalueof var="displayName"
    bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}.itemDisplayName"/>
</c:if>

<c:if test="${ empty displayName }">
  <c:set var="componentItemDescriptor" value="${mpv.componentRepositoryItemDescriptor}"/>
  <dspel:getvalueof var="displayName"
    bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}.${componentItemDescriptor.itemDisplayNameProperty}"/>
</c:if>

<pws:createVersionManagerURI object="${asset}" var="assetURI"/>

<a href='#' onclick='pushContext( "<c:out value='${assetURI}' escapeXml="true"/>" );return false;'>
  <c:out value="${displayName}"/>
</a>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/componentView.jsp#2 $$Change: 651448 $--%>

