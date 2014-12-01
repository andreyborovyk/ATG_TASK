<%--
  Project tab container panel for Asset Manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/project/projectTab.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <c:set var="sessionInfoName" value="/atg/web/assetmanager/SessionInfo"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="${sessionInfoName}"/>

  <c:set var="formHandlerPath" value="/atg/web/assetmanager/action/ListActionFormHandler"/>
  <dspel:importbean var="formHandler"
                    bean="${formHandlerPath}"/>

  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>

  <c:set target="${sessionInfo}" property="assetEditorViewID" value="project"/>

  <c:set var="assetBrowser" value="${sessionInfo.currentAssetBrowser}"/>
  <c:set target="${assetBrowser}" property="formHandlerPath" value="${formHandlerPath}"/>

  <%-- Reset the asset caches on the ProjectAssetBrowser --%>
  <web-ui:invoke bean="${assetBrowser}" method="reset"/>
  
  <%-- If necessary, set the conflict mode on the ProjectAssetBrowser --%>
  <c:if test="${not empty param.conflictsOnly}">
    <c:set target="${assetBrowser}" property="includeConflictingAssetsOnly" value="${param.conflictsOnly eq '1'}"/>
  </c:if>

  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>

  <%@ include file="/components/formStatus.jspf" %>

  <table id="contentTable" cellpadding="0" cellspacing="0">
    <tbody>
      <tr>
        <td id="leftPane">

          <%-- Get the current project --%>
          <pws:getCurrentProject var="projectContext"/>
          <c:set var="project" value="${projectContext.project}"/>
          <c:if test="${project ne null}">

            <c:url var="actionURL" value="${requestScope.managerConfig.page}"/>
            <dspel:form name="listActionForm" action="${actionURL}" method="post">

              <script type="text/javascript">

                var currentConflictMode;
                function saveConflictMode() {
                  currentConflictMode = document.getElementById("conflictModeSelector").value;
                }
                function setConflictMode() {
                  var selectInput = document.getElementById("conflictModeSelector");
                  var newMode = selectInput.value;
                  // set it back to the old value in case user cancels the save confirm dialog or there is an error
                  selectInput.value = currentConflictMode;
                  <c:url var="selectURL" value="${requestScope.managerConfig.page}"/>
                  atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame("<c:out value='${selectURL}'/>?conflictsOnly=" + newMode);
                }
              </script>

              <div id="assetListHeader">
                <div id="assetListHeaderRight">
                </div>
                <div id="assetListHeaderLeft">
                  <span class="subHeaderText">
                    <label for="conflictModeSelector">
                      <fmt:message key="projectTab.show"/>:
                    </label>
                    <c:if test="${assetBrowser.includeConflictingAssetsOnly}">
                      <c:set var="conflictingAssetsOnly" value="selected"/>
                    </c:if>
                    <select id="conflictModeSelector" name="conflictModeSelector" onfocus="saveConflictMode()" onchange="setConflictMode()">
                      <option value="0">
                        <fmt:message key="projectTab.allAssets"/>
                      </option>
                      <option value="1" <c:out value="${conflictingAssetsOnly}"/>>
                        <fmt:message key="projectTab.conflictingAssetsOnly"/>
                      </option>
                    </select>
                  </span>
                </div>
              </div>

              <%-- Determine which operations are allowed --%>
              <c:if test="${project.editable}">
                <dspel:contains var="allowRemoveFromProject"
                                values="${requestScope.tabConfig.operations}"
                                object="removeFromProject"/>
                <dspel:contains var="allowAddToMultiEdit"
                                values="${requestScope.tabConfig.operations}"
                                object="addToMultiEdit"/>
                <dspel:contains var="allowCreate"
                                values="${requestScope.tabConfig.operations}"
                                object="create"/>
                <dspel:contains var="allowExport"
                                values="${requestScope.tabConfig.operations}"
                                object="export"/>
              </c:if>

              <%-- Render the button toolbar --%>
              <dspel:include page="/components/toolbar.jsp">
                <dspel:param name="formHandlerPath"        value="${formHandlerPath}"/>
                <dspel:param name="allowRemoveFromProject" value="${allowRemoveFromProject}"/>
                <dspel:param name="allowAddToMultiEdit"    value="${allowAddToMultiEdit}"/>
                <dspel:param name="allowCheckAll"          value="${true}"/>
                <dspel:param name="allowCreate"            value="${allowCreate}"/>
                <dspel:param name="allowExport"            value="${allowExport}"/>
              </dspel:include>

              <%-- Render the list of assets --%>
              <dspel:include page="/components/pageableList.jsp">
                <dspel:param name="shouldSort"               value="true"/>
                <dspel:param name="showAssetStatusInProject" value="true"/>
                <dspel:param name="shouldRenderCheckboxes"   value="${project.editable}"/>
                <dspel:param name="scrollContainerHeightKey" value="projectTabScrollContainer"/>
              </dspel:include>

            </dspel:form>

          </c:if>  <%-- project is not null --%>

          <c:if test="${projectContext.project eq null}">
            <fmt:message key="projectTab.noCurrentProject"/>
          </c:if>

        </td>

        <%-- Display the asset editor in the right-pane iframe. --%>
        <td id="rightPane">

          <dspel:include page="/components/resizeHandler.jsp">
            <dspel:param name="resizableElementId" value="rightPaneIframe"/>
            <dspel:param name="heightKey"          value="rightPane"/>
          </dspel:include>

          <dspel:iframe id="rightPaneIframe"
                        name="rightPaneIframe"
                        iclass="assetEditorFrame"
                        frameborder="0"
                        allowtransparency="true"
                        scrolling="no"
                        src="${assetEditorURL}"
                        style="${requestScope.resizableStyle}"/>
        </td>
      </tr>
    </tbody>
  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/project/projectTab.jsp#2 $$Change: 651448 $ --%>
