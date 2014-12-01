<%--
  Default property viewer for RepositoryItem values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui" %>

<dspel:page>

  <c:set var="itemDescriptor" value="${mpv.repositoryItemDescriptor}"/>

  <dspel:getvalueof var="assetID" bean="${mpv.formHandlerProperty}.repositoryId"/>

  <asset-ui:createAssetURI var="assetURI"
    componentName="${itemDescriptor.repository.repositoryName}"
    itemDescriptorName="${itemDescriptor.itemDescriptorName}"
    itemId="${assetID}"/>

  <%-- Create link to asset if referenced asset exists --%>
  <c:if test="${ ! empty assetID }">


  <c:url var="linkURL" value="/assetEditor/editAsset.jsp">
    <c:param name="assetURI" value="${assetURI}"/>
    <c:param name="pushContext" value="true"/>
    <c:param name="linkPropertyName" value="${mpv.propertyName}"/>
  </c:url>
   

  <script type="text/javascript">

    function <c:out value="${mpv.uniqueId}"/>_pushContext( ) {
      // force a save of the current asset, then move to link URL
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset("<c:out value='${linkURL}' escapeXml='false'/>",null,null,null,null,true);
    }

  </script>

      <dspel:getvalueof var="displayName"
         bean="${mpv.formHandlerProperty}.itemDisplayName"/>
      <c:if test="${ empty displayName }">
        <dspel:getvalueof var="displayName"
          bean="${mpv.formHandlerProperty}.${itemDescriptor.itemDisplayNameProperty}"/>
      </c:if>
      <c:if test="${ empty displayName }">
        <dspel:getvalueof var="displayName" bean="${mpv.formHandlerProperty}.repositoryId"/>
      </c:if>

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
        <a href="javascript:<c:out value="${requestScope.mpv.uniqueId}"/>_pushContext();">
          <c:out value="${displayName}"/>
        </a>
      </c:otherwise>
    </c:choose>

  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemViewer.jsp#2 $$Change: 651448 $--%>
