<%--
  Asset creation page.

  This page is loaded into the right-pane when attempting to create a new asset.
  It does not contain any visible content.  Instead, it includes a form
  connected to an asset creation form handler.  The form is submitted
  immediately after the page is loaded, causing the standard asset editor JSP to
  be loaded into the right-pane.

  @param  containerPath     The name of the container in which the created
                            item will be located
  @param  assetType         The type of asset to create
  @param  treeComponent     A TreeState component used for determining the
                            parent of the new item (does not apply to
                            non-hierarchical assets)
  @param  linkPropertyName  The name of a property of the current right-pane
                            asset in which a reference to the new asset will
                            be stored (does not apply if creating from the
                            left-pane)
  @param  linkPropertyKey   If the linkPropertyName is for a map property, this
                            indicates the map key in which a reference to the
                            new asset will be stored
  @param  linkPropertyIndex If the link property is a list collection, this
                            indicates the index at which the new item should
                            be inserted.
  @param  pushContext       If specified, then the asset-editor breadcrumb stack
                            should be pushed prior to creating the asset
  @param  extraParamName    Optional. If specified, then this will be the name of
                            an additional parameter to be passed to editAsset.jsp
  @param  extraParamValue   Optional. If specified, then this will be the value of
                            an additional parameter to be passed to editAsset.jsp.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/createTransientAsset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Error URL that redisplays the standard asset editor in the right-pane
       using the previously selected asset. --%>
  <c:url var="errorURL" value="editAsset.jsp"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html>
    <head>
      <script type="text/javascript">

        // After the page loads, submit the creation form, unless some errors
        // occurred while rendering, in which case we just display the normal
        // asset editor with the current asset (having displayed errors in
        // the top-level error dialog).
        var pageErrors = false;
        function handleOnLoad() {
          if (pageErrors)
            document.location = "<c:out value='${errorURL}'/>";
          else
            document.getElementById("createAssetButton").click();
          <dspel:importbean var="refresh" bean="/atg/web/assetmanager/RefreshComponent"/>
          <c:if test="${refresh.refreshFlag}">
            if (parent.refresh) {
              parent.refresh();
            }
          </c:if>
        }

      </script>
    </head>
    <body onload="handleOnLoad()">

      <%-- Get the create-mode item mapping for the given asset type --%>
      <c:catch var="exception">
        <asset-ui:getItemMappingInfo var="imapInfo"
                                     repositoryPath="${param.containerPath}"
                                     itemDescriptorName="${param.assetType}"
                                     mode="AssetManager.create"
                                     taskConfig="${sessionInfo.taskConfiguration}"/>
        <biz:getItemMapping var="imap"
                            mappingName="${imapInfo.name}"
                            itemPath="${param.containerPath}"
                            itemName="${param.assetType}"
                            mode="${imapInfo.mode}"/>
      </c:catch>

      <c:choose>
        <c:when test="${not empty exception}">

          <%-- Display error message if no item mapping--%>
          <script type="text/javascript">
            pageErrors = true;
            parent.messages.addError("<fmt:message key='assetEditor.noItemMapping.error'/> <c:out value='${exception.message}'/>");
          </script>

        </c:when>
        <c:otherwise>

          <%-- Get the assetEditor, assetContext, and assetURI for the current tab --%>
          <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[sessionInfo.assetEditorViewID]}"/>
          <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
          <c:set var="assetContext" value="${assetEditor.assetContext}"/>
          <c:set var="assetURI" value="${assetContext.assetURI}"/>

          <%-- If we are pushing the breadcrumb context, clear the transientAsset
               and form handler path in the current assetContext, and push the
               context in the asset editor.  Otherwise, clear the context in the
               asset editor. --%>
          <c:choose>
            <c:when test="${not empty param.pushContext}">
              <c:set target="${assetContext}" property="transientAsset" value="${null}"/>
              <c:set target="${assetContext}" property="transientAssetFormHandlerPath" value="${null}"/>
              <c:set target="${assetEditor}" property="pushContext" value="true"/>
            </c:when>
            <c:otherwise>
              <c:set target="${assetEditor}" property="clearContext" value="true"/>
            </c:otherwise>
          </c:choose>

          <%-- Get the *new* asset context, which was created when we pushed or
               cleared the context. --%>
          <c:set var="assetContext" value="${assetEditor.assetContext}"/>

          <%-- Store the link property name and key (if any) in the asset context.
               The form handler will set the value of this property (or collection
               property element) as a reference to the new asset. For list
               collections we'll also pass the index at which to insert the item. --%>
          <c:set target="${assetContext}" property="parentLinkPropertyName" value="${param.linkPropertyName}"/>
          <c:set target="${assetContext}" property="parentLinkPropertyKey"  value="${param.linkPropertyKey}"/>
          <c:set target="${assetContext}" property="parentLinkPropertyIndex" value="${param.linkPropertyIndex}"/>

          <%-- Store in the assetContext the URI of the "parent" of the new asset.
               This will be used as the asset whose link property value will be
               set as a reference to the newly created asset.

               If this asset is being created from the left pane, and a tree is
               in use by the browse view, then the parent is obtained from the
               selected tree asset.  Note that the parent may be null if there
               is no selection or the selected asset does not qualify as a parent
               for the type of asset being created.

               If this asset is being created from the right pane (indicated by
               a link property name), then the currently displayed asset is used
               as the parent. --%>

          <c:set target="${assetContext}" property="parentAssetURI" value="${null}"/>
          <c:choose>
            <c:when test="${not empty param.treeComponent}">
              <web-ui:getBestParent var="parentNode"
                                    tree="${param.treeComponent}"
                                    childType="${param.assetType}"/>
              <c:if test="${not empty parentNode}">
                <c:set target="${assetContext}" property="parentAssetURI" value="${parentNode.URI}"/>
              </c:if>
            </c:when>
            <c:when test="${not empty param.linkPropertyName}">
              <c:set target="${assetContext}" property="parentAssetURI" value="${assetURI}"/>
            </c:when>
          </c:choose>

          <%-- The action URL for the creation form displays the standard
               asset editor, with the property tab index reset to 0. --%>
          <c:url var="assetEditorURL" value="editAsset.jsp">
            <c:param name="resetViewNumber" value="true"/>
            <c:if test="${not empty param.treeComponent}">
              <c:param name="treeComponent" value="${param.treeComponent}"/>
            </c:if>
          </c:url>

          <%-- The error URL for the creation form displays the standard asset
               editor.  If we pushed the context, then we have to pop it again to
               get the original asset to be displayed.  Otherwise, (i.e. if we
               *cleared* the context above) we have to explicitly reload the
               original asset by specifying its URI. --%>
          <c:url var="createErrorURL" value="editAsset.jsp">
            <c:choose>
              <c:when test="${not empty param.pushContext}">
                <c:param name="popContext" value="1"/>
              </c:when>
              <c:otherwise>
                <c:param name="assetURI" value="${assetURI}"/>
              </c:otherwise>
            </c:choose>
          </c:url>

          <%-- Get the create-mode form handler path.  Store it in the asset
               context so that the asset editor can determine if any exceptions
               occurred when creating the asset. --%>
          <c:set var="formHandlerPath" value="${imap.formHandler.path}"/>
          <c:set target="${assetContext}" property="transientAssetFormHandlerPath" value="${formHandlerPath}"/>

          <%-- Render the form with form-handler data in hidden inputs.  Note
               that the names of the container path and asset type properties
               are obtained from form handler attributes. --%>
          <dspel:form name="creationForm" action="${assetEditorURL}" method="post">

            <c:if test="${not empty imap.formHandler.attributes.containerPathProperty.value}">
              <dspel:input type="hidden" bean="${formHandlerPath}.${imap.formHandler.attributes.containerPathProperty.value}"
                           value="${param.containerPath}"/>
            </c:if>
            <c:if test="${not empty imap.formHandler.attributes.assetTypeProperty.value}">
              <dspel:input type="hidden" bean="${formHandlerPath}.${imap.formHandler.attributes.assetTypeProperty.value}"
                           value="${param.assetType}" />
            </c:if>
            <dspel:input type="hidden" bean="${formHandlerPath}.assetEditorPath"
                         value="${assetEditorPath}" />
            <dspel:input type="hidden" bean="${formHandlerPath}.createErrorURL"
                         value="${createErrorURL}"/>

            <c:if test="${not empty param.extraParamName}">
              <input type="hidden" name="<c:out value='${param.extraParamName}'/>"
                         value="<c:out value='${param.extraParamValue}'/>" />
            </c:if>

            <%-- Hidden submit button that is clicked by the onload handler --%>
            <dspel:input type="submit" id="createAssetButton" style="display:none"
                         bean="${formHandlerPath}.create"/>

          </dspel:form>
        </c:otherwise>
      </c:choose>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/createTransientAsset.jsp#2 $$Change: 651448 $--%>
