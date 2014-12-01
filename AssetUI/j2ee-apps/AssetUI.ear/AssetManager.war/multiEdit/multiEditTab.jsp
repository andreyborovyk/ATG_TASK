<%--
  Multi Edit tab container panel for asset manager UI.

  params :
     assetIdx  int. the index of the first item to show in asset list.  if unset will default to 0


  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/multiEditTab.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <c:set var="debug" value="false"/>

  <%@ include file="multiEditConstants.jspf" %>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="multiEditModeParam" param="multiEditMode"/>
  <dspel:getvalueof var="multiEditOperationParam" param="multiEditOperation"/>
  <dspel:getvalueof var="multiEditPropertyGroup" param="multiEditPropertyGroup"/>
  <dspel:getvalueof var="multiEditPropertyGroupIndex" param="multiEditPropertyGroupIndex"/>


  <c:set var="sessionInfoName" value="/atg/web/assetmanager/SessionInfo"/>
  <c:set var="applyFormHandlerPath" value="/atg/web/assetmanager/multiedit/ApplyToAllFormHandler"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>
  <dspel:importbean var="applyFormHandler"
                    bean="${applyFormHandlerPath}"/>

  <%-- it's possible we haven't changed the mode after a wait --%>
  <c:set var="isFinishedWaiting" value="${sessionInfo.multiEditSessionInfo.multiEditRunnable.multiEditFinished}"/>
  <c:if test="${multiEditSessionInfo.multiEditMode eq MODE_WAIT && isFinishedWaiting}">
    <c:set var="multiEditModeParam" value="${MODE_CONFIRM}"/>
  </c:if>

  <%-- NOTE: normally the view is set to a view, but multiedit has no views, so we set the view id to the id of the tab --%>
  <c:set target="${sessionInfo}" property="assetEditorViewID" value="${sessionInfo.assetManagerTab}"/>

  <c:set var="multiEditModePrevious" value="${multiEditSessionInfo.multiEditMode}"/>
  <c:set var="multiEditOperationPrevious" value="${multiEditSessionInfo.multiEditOperation}"/>


  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>

  <%-- If a multiEditOperation parameter was specified, store it as a session variable. --%>
  <c:if test="${not empty multiEditOperationParam}">
    <%-- when a new operation is chosen, clear the current selection, except when new operation is step in which case
         set the selected asset to the start of the list --%>
    <c:choose>
      <c:when test='${multiEditOperationParam eq OPERATION_STEP and multiEditOperationParam ne multiEditOperationPrevious}'>
        <script type="text/javascript">
          selectFirstAsset = true;
        </script>
      </c:when>
      <c:when test='${multiEditModeParam eq MODE_SINGLE and multiEditOperationParam ne multiEditOperationPrevious}'>
        <%-- change of modes to single means we should clean up the multi edit context --%>
        <script type="text/javascript">
          clearSelectedAsset = true;
        </script>
      </c:when>
      <c:when test='${not empty multiEditOperationParam and multiEditOperationParam ne multiEditOperationPrevious}'>
        <script type="text/javascript">
          clearSelectedAsset = true;
        </script>
      </c:when>
      <c:otherwise>
        <%-- do nothing --%>
      </c:otherwise>
    </c:choose>

    <c:set target="${multiEditSessionInfo}" property="multiEditOperation" value="${multiEditOperationParam}"/>
  </c:if>


  <%-- Specify a default value for the multiEditOperation variable if necessary. --%>
  <c:if test="${empty multiEditSessionInfo.multiEditOperation}">
     <c:set target="${multiEditSessionInfo}" property="multiEditOperation" value="${OPERATION_NONE}"/>
  </c:if>

  <%-- If a multiEditMode parameter was specified, store it as a session variable. --%>
  <c:if test="${not empty multiEditModeParam}">
    <c:set target="${multiEditSessionInfo}" property="multiEditMode" value="${multiEditModeParam}"/>
  </c:if>

  <%-- Specify a default value for the multiEditMode variable if necessary. --%>
  <c:if test="${empty multiEditSessionInfo.multiEditMode}">
     <c:set target="${multiEditSessionInfo}" property="multiEditMode" value="${MODE_SINGLE}"/>
  </c:if>


  <%-- If a multiEditPropertyGroupIndex parameter was specified, store it as a session variable. --%>
  <c:if test="${not empty multiEditPropertyGroupIndex && multiEditPropertyGroupIndex != 'null'}">
    <c:set target="${multiEditSessionInfo}" property="multiEditPropertyGroupIndex" value="${multiEditPropertyGroupIndex}"/>
  </c:if>

  <%-- finally, the session scoped variables are all set.  now, gather them up into the page variables --%>
        <c:set var="multiEditOperation" value="${multiEditSessionInfo.multiEditOperation}"/>
        <c:set var="multiEditMode" value="${multiEditSessionInfo.multiEditMode}"/>

  <%-- In confirmation mode, always show the multi-edit status and show the confirm page or wait page if no asset is selected --%>
  <c:choose>
    <c:when test='${multiEditMode eq MODE_CONFIRM}'>
      <c:set var="showMultiEditStatus" value="true"/>
      <c:set var="showConfirmPage" value="false"/>
      <%-- we'd like to show confirm page only if no asset is selected. --%>
      <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>
      <c:if test="${empty assetEditor.assetContext.assetURI}">
         <c:set var="showConfirmPage" value="true"/>
      </c:if>

    </c:when>
    <c:when test="${multiEditMode eq MODE_WAIT}">
      <c:set var="showMultiEditStatus" value="false"/>
      <c:set var="showWaitPage" value="true"/>
    </c:when>
  </c:choose>

  <%-- In Multi mode, if there are no assets, give the user an error message --%>
  <c:if test="${multiEditMode eq MODE_MULTI}">
    <dspel:test var="assetListInfo" value="${sessionInfo.assetURIs}"/>
      <c:if test="${assetListInfo.size eq 0}">
        <script type="text/javascript">
           <fmt:message var="noCheckedAssetsErrorMessage" key="multiEdit.error.noCheckedAssets"/>
           messages.addError('<c:out value="${noCheckedAssetsErrorMessage}"/>');
        </script>
      </c:if>
  </c:if>

  <c:set var="formHandlerPath" value="/atg/web/assetmanager/action/ListActionFormHandler"/>
  <dspel:importbean var="formHandler"
                    bean="${formHandlerPath}"/>



  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>

  <%-- Reset the asset caches on the current asset browser --%>
  <web-ui:invoke bean="${sessionInfo.currentAssetBrowser}" method="reset"/>

  <%@ include file="/components/formStatus.jspf" %>

  <table id="contentTable" cellpadding="0" cellspacing="0">
    <tbody>
      <tr>
        <td id="leftPane">

          <%-- Get the current project and id --%>
          <pws:getCurrentProject var="projectContext"/>
          <c:set var="project" value="${projectContext.project}"/>
          <c:if test="${empty project or project.editable}">

            <c:url var="actionURL" value="${requestScope.managerConfig.page}">
            </c:url>
            <dspel:form name="listActionForm" formid="multiEditAssetsForm" action="${actionURL}" method="post">

              <div id="assetListHeader">
                <div id="assetListHeaderRight">
                </div>
                <div id="assetListHeaderLeft">
                  <%-- don't let them change filter type in the midst of a multi operation --%>
                  <c:choose>
                    <c:when test='${multiEditSessionInfo.multiEditMode ne MODE_SINGLE}'>
                      <c:set var="disableTypeFilter" value="true"/>
                    </c:when>
                    <c:otherwise>
                      <c:set var="filterPickList" value="${sessionInfo.types}"/>
                    </c:otherwise>
                  </c:choose>
                  <%@ include file="/components/filterByType.jspf" %>
                </div>
              </div>

             <%-- In Single mode, we render the usual toolbar, and render checkboxes --%>
             <c:choose>
               <c:when test='${multiEditSessionInfo.multiEditMode eq MODE_SINGLE}'>
                       <%-- set up what operations are allowed --%>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                          object="removeFromMultiEdit"
                                          var="allowRemoveFromMultiEdit"/>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                          object="stepEdit"
                                          var="allowStepEdit"/>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                          object="applyToAll"
                                          var="allowApplyToAll"/>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                          object="listEdit"
                                          var="allowListEdit"/>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                          object="transformations"
                                          var="allowTransformations"/>
                 <dspel:contains values="${requestScope.tabConfig.operations}"
                                 object="export"
                                 var="allowExport"/>



                 <c:set var="allowCheckAll" value="${true}"/>

                 <%-- render the button toolbar --%>
                 <dspel:include page="/components/toolbar.jsp">
                   <dspel:param name="formHandlerPath"           value="${formHandlerPath}"/>
                   <dspel:param name="allowTransformations"      value="${allowTransformations}"/>
                   <dspel:param name="allowApplyToAll"           value="${allowApplyToAll}"/>
                   <dspel:param name="allowListEdit"             value="${allowListEdit}"/>
                   <dspel:param name="allowStepEdit"             value="${allowStepEdit}"/>
                   <dspel:param name="allowCheckAll"             value="${allowCheckAll}"/>
                   <dspel:param name="allowRemoveFromMultiEdit"  value="${allowRemoveFromMultiEdit}"/>
                   <dspel:param name="allowExport"               value="${allowExport}"/>
                 </dspel:include>

               </c:when>
               <c:otherwise>
                 <table id="leftPaneToolbar">
                         <tr>
                           <td id="leftPaneToolbarBack">
                       <c:choose>
                         <c:when test='${multiEditSessionInfo.multiEditOperation eq OPERATION_STEP}'>
                           <fmt:message var="operationName" key="assetEditor.multiEdit.operationStep"/>
                         </c:when>
                         <c:when test='${multiEditSessionInfo.multiEditOperation eq OPERATION_APPLY_TO_ALL}'>
                           <fmt:message var="operationName" key="assetEditor.multiEdit.operationApplyToAll"/>
                         </c:when>
                         <c:when test='${multiEditSessionInfo.multiEditOperation eq OPERATION_LIST}'>
                           <fmt:message var="operationName" key="assetEditor.multiEdit.operationList"/>
                         </c:when>
                       </c:choose>

                       <fmt:message var="exitTitle" key="assetEditor.multiEdit.exitMode">
                         <fmt:param value="${operationName}"/>
                       </fmt:message>

                       <div class="leftPaneToolbarRight">
                         <div class="toolbarWrapper">
                           <div class="toolbarContainer">
                           <a href="javascript:saveBeforeExitMultiEdit()"
                             class="exitEditMode"
                             accesskey="<fmt:message key='multiEdit.accessKey.exit'/>"
                             title="<c:out value='${exitTitle}'/>"></a>
                           </div>
                         </div>
                       </div>
                       <div class="leftPaneToolbarLeft">
                       </div>
                           </td>
                         </tr>
                 </table>
               </c:otherwise>
             </c:choose>


             <%-- render the scrollable, pagable list of assets --%>
             <dspel:include page="/components/pageableList.jsp">
               <dspel:param name="shouldRenderCheckboxes"   value="${multiEditSessionInfo.multiEditMode ne MODE_MULTI and ( multiEditSessionInfo.multiEditMode ne MODE_CONFIRM && multiEditSessionInfo.multiEditMode ne MODE_WAIT)}"/>
               <dspel:param name="shouldSort"               value="true"/>
               <dspel:param name="scrollContainerHeightKey" value="multiEditTabScrollContainer"/>
               <dspel:param name="showMultiEditStatus" value="${showMultiEditStatus}"/>
             </dspel:include>

            </dspel:form>  <%-- Discard Assets form --%>

          </c:if>  <%-- empty project or project.editable --%>

        </td>

        <%-- Display the asset editor in the right-pane iframe. --%>
        <td id="rightPane">
          <dspel:include page="/components/resizeHandler.jsp">
            <dspel:param name="resizableElementId" value="rightPaneIframe"/>
            <dspel:param name="heightKey"          value="rightPane"/>
          </dspel:include>

          <c:set var="rightPanePage" value="${assetEditorURL}"/>
          <dspel:iframe id="rightPaneIframe"
                        name="rightPaneIframe"
                        iclass="assetEditorFrame"
                        frameborder="0"
                        allowtransparency="true"
                        scrolling="no"
                        src="${rightPanePage}"
                        style="${requestScope.resizableStyle}"/>
          <c:choose>
          <c:when test="${showWaitPage}">
            <c:set var="rightPanePage" value="${config.contextRoot}/multiEdit/confirmationWait.jsp"/>
            <script type="text/javascript">
              registerOnLoad(function() {
                rightPaneIframe.location="<c:out value='${rightPanePage}'/>";
                refresh();
              });
            </script>
          </c:when>
          <c:when test="${showConfirmPage}">
            <c:set var="rightPanePage" value="${config.contextRoot}/multiEdit/multiEditConfirmation.jsp"/>
            <script type="text/javascript">
              registerOnLoad(function() {
                rightPaneIframe.location="<c:out value='${rightPanePage}'/>";
                refresh();
              });
            </script>
          </c:when>
        </c:choose>
        </td>
      </tr>
    </tbody>
  </table>

  <%-- initialize the apply to all dropdown menus (setting repositoryId, clears the form) --%>
  <c:if test="${multiEditSessionInfo.multiEditMode eq MODE_SINGLE}">
    <c:set target="${applyFormHandler}" property="clearForm" value="true"/>
  </c:if>



    <script type="text/javascript">

      function showConfirmationPage() {
         parent.clearCurrentSelection();
         <c:set var="rightConfirmPage" value="${config.contextRoot}/multiEdit/multiEditConfirmation.jsp"/>
         rightPaneIframe.location="<c:out value='${rightConfirmPage}'/>";
         refresh();
      }


      function changeMode(mode, operation, propertyGroupIndex) {
        <c:url var="selectURL" value="${requestScope.managerConfig.page}"/>
        var theurl = "<c:out value='${selectURL}'/>?multiEditMode=" + mode
                             + "&multiEditOperation=" + operation
                             + "&multiEditPropertyGroupIndex=" + propertyGroupIndex;

        document.location = theurl;
      }

      function changeToSingleMode() {
        <c:url var="selectURL" value="${requestScope.managerConfig.page}"/>
        var theurl = "<c:out value='${selectURL}'/>?multiEditMode=<c:out value='${MODE_SINGLE}'/>&multiEditOperation=<c:out value='${OPERATION_NONE}'/>";
        document.location = theurl;
      }

   function saveBeforeExitMultiEdit() {

       if (parent.multiEditWait) {
       // uncomment if we want the exit button to stop the multi edit operation
       // <c:url var="stopUrl" value="/components/setValue.jsp">
       //   <c:param name="bean" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditRunnable.multiEditStopped"/>
       //   <c:param name="value" value="true"/>
       // </c:url>
       // issueRequest("<c:out escapeXml='false' value='${stopUrl}'/>");
          return;
       }
       atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.changeToSingleMode, null, "changeToSingleMode");

       //rightPaneIframe.clickCancelButton();
    }

   </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/multiEditTab.jsp#2 $$Change: 651448 $ --%>
