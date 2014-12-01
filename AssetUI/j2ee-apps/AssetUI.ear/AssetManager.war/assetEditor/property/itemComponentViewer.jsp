<%--
  Default viewer for RepositoryItem elements of collection properties.

  The following request-scoped variables are expected to be set:
  
  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemComponentViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

<dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<dspel:getvalueof var="asset"
  bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}"/>

  <c:choose>
    <c:when test="${not empty asset}">
    
<dspel:getvalueof var="displayName"
  bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}.itemDisplayName"/>

<c:if test="${ empty displayName }">
  <c:set var="componentItemDescriptor" value="${mpv.componentRepositoryItemDescriptor}"/>
  <dspel:getvalueof var="displayName"
    bean="${mpv.formHandlerProperty}${mpv.componentPropertyName}.${componentItemDescriptor.itemDisplayNameProperty}"/>
</c:if>

<asset-ui:createAssetURI var="assetURI" object="${asset}"/>

<c:url var="linkURL" context="${config.contextRoot}" value="/assetEditor/editAsset.jsp">
    <c:param name="assetURI" value="${assetURI}"/>
    <c:param name="pushContext" value="true"/>
    <c:param name="linkPropertyName" value="${mpv.propertyName}"/>
</c:url>
 

<script type="text/javascript">

  function <c:out value="${mpv.uniqueId}"/>_pushContext( linkURL ) {
    // force a save of the current asset, then move to link URL
    parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkURL,null,null,null,null,true);
  }

</script>

<%-- Display a hyperlink for drilling down into the asset, unless we are in
     Multi-edit mode or drilling down is otherwise prohibited, in which case
     we just display the property value. --%>
<c:choose>
  <c:when test="${requestScope.atgIsMultiEditView or
                  requestScope.mpv.attributes.prohibitDrillDown}">
    <span>
      <c:out value="${displayName}"/>
    </span>
  </c:when>
  <c:otherwise>
    <a href="javascript:<c:out value='${mpv.uniqueId}'/>_pushContext('<c:out value='${linkURL}' escapeXml='false'/>');">
      <c:out value="${displayName}"/>
    </a>
  </c:otherwise>
</c:choose>
    </c:when>
    <c:otherwise>
      <fmt:message key='itemComponentViewer.error.noItem'/>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemComponentViewer.jsp#2 $$Change: 651448 $--%>
