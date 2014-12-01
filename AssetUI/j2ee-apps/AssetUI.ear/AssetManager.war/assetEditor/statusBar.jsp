<%--
  The yellow status bar


  The following request-scoped variables are expected to be set:

  @param  multiEditMode True if during the multi edit phase of a multi edit operation
  @param  multiEditOperation name of the multi edit operation

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/statusBar.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Get the current left tab --%>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>

  <%-- Get the assetEditor for the current left tab --%>
  <c:if test="${not empty currentView}">
    <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
    <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
  </c:if>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="asset" param="asset"/>
  <dspel:getvalueof var="parentItemDisplayName" param="parentItemDisplayName"/>
  <dspel:getvalueof var="transient" param="transient"/>
  <dspel:getvalueof var="treeComponent" param="treeComponent"/>
  <dspel:getvalueof var="imap" param="imap"/>
  <dspel:getvalueof var="assetIndex" param="assetIndex"/>

  <%--
    if the tree was not passed in as a param, look for a treeComponent
  --%>

  <c:if test="${transient}">
    <c:if test="${empty treeComponent}">
      <web-ui:getTree var="treeComponent"
         assetURI="${assetEditor.assetContext.transientAsset.uri}"
         treeRegistry="${sessionInfo.treeRegistryPath}"/>
    </c:if>
  </c:if>

  <%-- get the service named by treeComponent --%>
  <c:if test="${not empty treeComponent}">
    <dspel:importbean var="treeState" bean="${treeComponent}"/>
  </c:if>

  <%-- if the asset index is set as a param, put it in the session info, otherwise, pull the value out of session info --%>
  <c:choose>
    <c:when test="${empty assetIndex}">
      <c:set var="assetIndex" value="${sessionInfo.assetIndex}"/>
      <script type="text/javascript">
        parent.currentSelectedAssetIndex = <c:out value="${assetIndex}"/> - 1;
      </script>
    </c:when>
    <c:otherwise>
      <c:set target="${sessionInfo}" property="assetIndex" value="${assetIndex}"/>
    </c:otherwise>
  </c:choose>

  <%-- asset picker --%>
  <dspel:include page="/components/assetPickerLauncher.jsp">
    <dspel:param name="assetType"            value="${imap.itemName}"/>
    <dspel:param name="isPickingParent"      value="true"/>
    <dspel:param name="launchPickerFunction" value="showAssetPickerForParent"/>
    <dspel:param name="onSelectFunction"     value="onPickerEditSelect"/>
    <dspel:param name="repositoryName"       value="${asset.containerName}"/>
    <dspel:param name="repositoryPath"       value="${asset.containerPath}"/>
  </dspel:include>

  <script type="text/javascript">
    
    function onPickerEditSelect(selected, attributes) {
      var nameDiv = document.getElementById("parentNameDiv");
      nameDiv.innerHTML = selected.displayName;

      var parentAssetURIText = document.getElementById("parentAssetURIInput");
      parentAssetURIText.value = selected.uri;

      var parentAssetURIContext = document.getElementById("parentAssetURIContext");
      parentAssetURIContext.value = selected.uri;
      <c:if test="${treeState != null && treeState.treeDefinition.parentChangeRequiresRefresh}">
        submitForm();
      </c:if>
    }

    function clearParent() {
      var nameDiv = document.getElementById("parentNameDiv");
      if (nameDiv != null)
      nameDiv.innerHTML = "<span id='noParentLabel'><b><font color=red><fmt:message key="assetEditor.noParent"/></font></b></span>";

      var parentAssetURIText = document.getElementById("parentAssetURIInput");
      parentAssetURIText.value = "";

      var parentAssetURIContext = document.getElementById("parentAssetURIContext");
      parentAssetURIContext.value = "";
      <c:if test="${treeState != null && treeState.treeDefinition.parentChangeRequiresRefresh}">
        submitForm();
      </c:if>
    }


  </script>

  <div id="rightPaneHeader">

    <!-- Right Pane Header : Includes right content header -->

    <div id="rightPaneHeaderRight">

      <c:choose>

        <%-- When editing or creating linked item, display 'parentName.linkPropertyName' --%>
        <c:when test="${not empty parentItemDisplayName and not empty assetEditor.assetContext.parentLinkPropertyName}">
          <div><c:out value="${parentItemDisplayName}"/>.<c:out value="${assetEditor.assetContext.parentLinkPropertyName}"/></div>
        </c:when>

        <%-- Don't display anything if creating from a tree that does not allow setParent --%>
        <c:when test="${transient &&
                        not empty treeComponent &&
                        not treeState.treeDefinition.allowSetParent}">
          <script type="text/javascript">
            registerOnLoad(clearParent);
          </script>
        </c:when>

        <%-- When creating from other trees, show 'parentName' and picker --%>
        <c:when test="${not empty treeComponent && transient}">
          <c:set target="${assetEditor.assetContext}" property="treeComponentPath" value="${treeComponent}"/>
          <div id="assetMeta" class="noParent">
            <span id="parentNameDiv">
              <c:choose>
                <c:when test="${not empty parentItemDisplayName}">
                  <c:out value="${parentItemDisplayName}"/>
                </c:when>
                <c:otherwise>
                  <span id="noParentLabel"><b><font color=red><fmt:message key="assetEditor.noParent"/></font></b></span>
                </c:otherwise>
              </c:choose>
            </span>
            <span class="assetMetaActions">
              <a href="javascript:showAssetPickerForParent()"
                 class="buttonSmall" title="<fmt:message key='assetEditor.selectParent.title'/>"><fmt:message key="assetEditor.selectParent"/></a>
              <a href="javascript:clearParent()"
                 class="buttonSmall" title="<fmt:message key='assetEditor.clearParent.title'/>"><fmt:message key="assetEditor.clear"/></a>
            </span>
          </div>
        </c:when>

        <%-- Show ID and Date info unless we are in multi edit mode --%>
        <c:when test="${multiEditMode != 'Multi'}">
          <div id="assetMeta">
            <div class="assetID">
              <span class="metaLabel"><fmt:message key='assetEditor.statusBar.id'/></span>
              <c:out value="${asset.id}"/>
            </div>

            <c:if test="${not empty asset && not empty asset.asset}">
              <dspel:tomap var="theAssetItem" value="${asset.asset}"/>
              <fmt:message key='assetEditor.statusBar.formatDate.type' var="formatType"/>
              <fmt:message key='assetEditor.statusBar.formatDate.pattern' var="formatPattern"/>

              <c:choose>
                <c:when test="${not empty theAssetItem.creationDate}">
                  <div class="assetCreated">
                    <span class="metaLabel"><fmt:message key='assetEditor.statusBar.creationDate'/></span>
                    <fmt:formatDate value="${theAssetItem.creationDate}"
                                    type="${formatType}"
                                    pattern="${formatPattern}"/>
                  </div>
                </c:when>
                <c:when test="${not empty theAssetItem.registrationDate}">
                  <div class="assetCreated">
                    <span class="metaLabel"><fmt:message key='assetEditor.statusBar.registrationDate'/></span>
                    <fmt:formatDate value="${theAssetItem.registrationDate}"
                                    type="${formatType}"
                                    pattern="${formatPattern}"/>
                  </div>
                </c:when>
              </c:choose>
            </c:if>

          </div>
        </c:when>
      </c:choose>

    </div>

    <div id="rightPaneHeaderLeft">

      <c:choose>
        <c:when test="${not empty asset}">
          <c:set var="assetTypeName" value="${asset.typeDisplayName}"/>
        </c:when>
        <c:otherwise>
          <c:set var="assetTypeName" value="${imap.itemName}"/>
        </c:otherwise>
      </c:choose>

      <c:if test="${not empty asset.displayName}">
        <c:set var="itemDisplayName" value="${asset.displayName}"/>
      </c:if>

      <c:set var="assetFamily" value="${asset.assetFamily}"/>
      <c:set var="assetContainerName" value="${asset.containerPath}"/>
      <c:set var="assetContainerType" value="${asset.type}"/>
      <web-ui:getIcon assetFamily="${assetFamily}"
                      container="${assetContainerName}"
                      type="${assetContainerType}"
                      largeIcon="true"
                      var="assetIconUrl"/>

      <div id="assetInfo">

        <c:choose>

          <c:when test="${multiEditMode == 'Multi'}">

            <%-- Multi edit asset icon. --%>
            <c:if test="${not empty assetIconUrl}">
              <dspel:img otherContext="${assetIconUrl.contextRoot}"
                         src="${assetIconUrl.relativeUrl}"
                         iclass="assetIcon"/>
            </c:if>

            <%-- The name of the multiedit property group.  --%>
            <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>
            <c:choose>
              <c:when test="${multiEditSessionInfo.multiEditPropertyGroupIndex == -1}">
                <fmt:message key="multiEdit.allPropertyGroups" var="propertyGroupName"/>
              </c:when>
              <c:otherwise>

                <c:if test="${not empty view.displayName}">
                  <c:set var="bundleName" value="${view.attributes.resourceBundle}"/>
                  <c:choose>
                    <c:when test="${not empty bundleName}">
                      <fmt:setBundle var="resBundle" basename="${bundleName}"/>
                      <fmt:message key="${view.displayName}" bundle="${resBundle}" var="propertyGroupName"/>
                    </c:when>
                    <c:otherwise>
                      <c:set var="propertyGroupName" value="${view.displayName}"/>
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </c:otherwise>
            </c:choose>

            <%-- Display the multiedit operation name and the property group --%>
            <fmt:message var="multiEditOpName" key="assetEditor.multiEdit.operation${multiEditOperation}"/>
            <div class="assetType">
              <fmt:message key="assetEditor.statusBar.multiedit.modeAndPropertyGroup">
                <fmt:param value="${multiEditOpName}"/>
                <fmt:param value="${propertyGroupName}"/>
              </fmt:message>
            </div>

            <%-- Step Operation: Currently Editing: TypeName Asset (n of total) --%>

            <c:if test="${multiEditOperation == 'Step'}">
              <div class="assetName">
                <c:out value="${itemDisplayName}"/> <fmt:message key="assetEditor.statusBar.currentOfN">
                  <fmt:param value="${assetIndex}"/>
                  <fmt:param value="${sessionInfo.multiEditSessionInfo.assetCount}"/>
                </fmt:message>
              </div>
            </c:if>

            <%-- Apply All or List Operation: Currently Editing: total Assets --%>

            <c:if test="${multiEditOperation == 'List' || multiEditOperation == 'ApplyToAll'}">
              <div class="assetName">
                <fmt:message key="assetEditor.statusBar.nAssets">
                  <fmt:param value="${sessionInfo.multiEditSessionInfo.assetCount}"/>
                </fmt:message>
              </div>
            </c:if>

          </c:when>

          <%-- Single Edit Mode:  'Asset Type' 'Asset Name' or 'Breadcrumb' --%>

          <c:otherwise>

            <%-- The Asset Type and Icon --%>
            <c:if test="${not empty assetIconUrl}">
              <dspel:img otherContext="${assetIconUrl.contextRoot}" src="${assetIconUrl.relativeUrl}" iclass="assetIcon"/>
            </c:if>
            <div class="assetType"><c:out value="${assetTypeName}"/></div>

            <%-- The Asset Name or Breadcrumb --%>
            <c:choose>

              <c:when test="${assetEditor.size > 1}">
                <select id="contextbcrumb" onChange="popContextBreadcrumb()">
                  <option value="0"><c:out value="${asset.displayName}"/></option>
                  <c:forEach begin="1" end="${assetEditor.size -1}" var="current" varStatus="status">
                    <option value="<c:out value='${status.index}'/>" >
                      <c:out value="${assetEditor.assetContexts[assetEditor.size - status.index -1].displayName}"/>
                    </option>
                  </c:forEach>
                </select>
              </c:when>

              <c:when test="${not empty itemDisplayName}">
                <div class="assetName"><c:out value="${itemDisplayName}"/></div>
              </c:when>

            </c:choose>
          </c:otherwise>

        </c:choose>
      </div> <%-- assetInfo --%>

    </div> <%-- rightPaneHeaderLeft --%>

  </div> <%-- rightPaneHeader --%>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/statusBar.jsp#2 $$Change: 651448 $--%>
