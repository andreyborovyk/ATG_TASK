<%--
  Standard editor for repository item properties...  which pops up an IFRAME asset browser

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/imageLinkViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui"       %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <c:if test="${mpv.attributes.title != null}">
    <c:set var="pvTitle" value="${mpv.attributes.title}"/>
  </c:if>

  <%--
   Determine the input class of this item..
   --%>
  <c:set var="pvClassAttr" value="class=\"formLabel\""/>
  <c:if test="${mpv.propertyDescriptor.required}">
    <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
  </c:if>

  <c:set var="pvIsDisabled" value="false"/>
  <c:if test="${!mpv.propertyDescriptor.writable}">
    <c:set var="pvIsDisabled" value="true"/>
  </c:if>

  <dspel:getvalueof var="assetId"
    bean="${mpv.formHandlerProperty}.repositoryId"/>

  <c:set var="itemDescriptor" value="${mpv.repositoryItemDescriptor}"/>

  <asset-ui:createAssetURI var="emptyRelatedAssetURI"
    componentName="${itemDescriptor.repository.repositoryName}"
    itemDescriptorName="${itemDescriptor.itemDescriptorName}"
    itemId=""/>

  <script type="text/javascript">

    function <c:out value="${mpv.uniqueId}"/>_pushContext() {
      var propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp" + 
                       "?assetURI=<c:out value='${emptyRelatedAssetURI}'/><c:out value='${assetId}'/>" +
                       "&pushContext=true" +
                       "&linkPropertyName=<c:out value='${mpv.propertyName}'/>";
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(propPage);
    }


  </script>

  <%-- Get the name of the property in this item that is the display name --%>
  <c:set var="displayNameProperty"
     value="${mpv.propertyDescriptor.propertyBeanInfo.itemDisplayNameProperty}"/>


<fieldset class="buttons">
  <legend><c:out value="${pvTitle}"/></legend>

  <table class="formTable">
    <c:choose>
      <c:when test="${not empty assetId}">
        <tr>
          <%-- Display a hyperlink for drilling down into the current asset --%>

          <c:set var="displayNameProperty"
            value="${mpv.propertyDescriptor.propertyBeanInfo.itemDisplayNameProperty}"/>

          <td rowspan="4">
            <dspel:getvalueof id="url" bean="${mpv.formHandlerProperty}.url" />
            <a id='<c:out value="${mpv.uniqueId}"/>Image' onmouseover="status='';return true;"
              href="javascript:<c:out value="${mpv.uniqueId}"/>_pushContext();">
              <img src='<c:out value="${url}"/>' alt="" width="75" height="75" class="mediaImage" />
            </a>
          </td>

        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.name"/>
          </td>
          <td>
            <a id='<c:out value="${mpv.uniqueId}"/>DisplayName' onmouseover="status='';return true;"
               href="javascript:<c:out value="${mpv.uniqueId}"/>_pushContext();">
              <dspel:valueof bean="${mpv.formHandlerProperty}.${displayNameProperty}"/>
            </a>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.type"/>
          </td>
          <td>
            <dspel:getvalueof id="mimeType" bean="${mpv.formHandlerProperty}.mimeType" />
            <c:out value="${mimeType}"/>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.creationDate"/>
          </td>
          <td>
            <dspel:getvalueof id="creationDate" bean="${mpv.formHandlerProperty}.creationDate" />
            <c:out value="${creationDate}"/>
          </td>
        </tr>

      </c:when>
      <c:otherwise>
        <tr>
          <td class="formLabel"><fmt:message key="imageLinkEditor.noImage"/></td>
	</tr>
      </c:otherwise>
    </c:choose>
  </table>

  <table class="fieldsetFooterContainer">

    <tr>
      <td class="fieldsetFooter">
      <!-- Right Pane Footer Action Buttons : Includes right pane footer action buttons -->
        <div class="fieldsetFooterRight">
        </div>
        <div class="fieldsetFooterLeft">
        </div>
      </td>
    </tr>
  </table>

</fieldset>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/imageLinkViewer.jsp#2 $$Change: 651448 $--%>
