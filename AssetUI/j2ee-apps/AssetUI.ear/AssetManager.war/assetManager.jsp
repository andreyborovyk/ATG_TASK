<%--
  Main panel for Asset Manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetManager.jsp#3 $$Change: 654746 $
  @updated $DateTime: 2011/06/27 09:01:12 $$Author: ikalachy $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <%@ include file="multiEdit/multiEditConstants.jspf" %>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="assetURI" param="assetURI"/>
  <dspel:getvalueof var="siteId" param="siteId"/>
  
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  

  <c:url var="multiEditIsCompleteURL" value="/multiEdit/multiEditResponse.jsp"/>

  <c:url var="cleanUpImportURL" value="/components/getValue.jsp">
    <c:param name="bean" value="/atg/web/assetmanager/transfer/ImportAsset.endFileImportOperation"/>
  </c:url>


  <%-- Blank page to be used as the default src for all iframes, in order to
       prevent IE confirmation dialog for displaying "unsecure items" when
       running with https.  See bug 128404.  --%>
  <c:url var="blankUrl" value="/blank.html"/>

  <%-- Until we implement a scrolling div for the main panel of reviewChanges.jsp,
       we need to conditionally hide the main body scrollbar. --%>
  <c:set var="bodyId" value="assetManagerBody"/>
  <c:set var="bodyScroll" value="no"/>
  <c:if test="${not empty param.reviewChanges}">
    <c:set var="bodyId" value="assetManagerDiffBody"/>
    <c:set var="bodyScroll" value="yes"/>
  </c:if>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>

      <dspel:include page="components/head.jsp"/>

      <link rel="icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon"/>
      <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon"/>

      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/dropDownMenu.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/fader.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/mainWindow.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/messages.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/resizeHandler.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/quietFormSubmitter.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/saveConfirm.js"></script>
      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/selectionManagement.js"></script>
      <script type="text/javascript" src="<c:out value='${config.webuiRoot}'/>/dijit/previewLauncher/previewPicker.js"></script>

      <dspel:link href="${config.webuiRoot}/dijit/previewLauncher/templates/previewPicker.css"
                  rel="stylesheet"
                  type="text/css"
                  media="all"/>

      <script type="text/javascript">

        dojo.require("dojox.Dialog");
        dojo.require("dijit.form.CheckBox");
        dojo.require("dijit.form.Button");
        dojo.require("dijit.form.FilteringSelect");
        dojo.require("dijit.form.TextBox");
        dojo.require("dojo.parser");  // scan page for widgets and instantiate them


        // Register the Messaging widget namespace
        dojo.registerModulePath("atg.widget.previewPicker", "<c:out value='${config.webuiRoot}'/>/dijit/previewLauncher/previewPicker");
        dojo.require("atg.widget.previewPicker");

        dojo.connect(window, "onload", handleOnLoad);
        dojo.connect(window, "onresize", handleOnResize);

        // Create an object that manages alerts and error messages.
        var messages = new Messages();

        // Create an object that manages resizable window elements.
        var resizeHandler = new ResizeHandler();

        var multiEditIntervalVar = null;

        var multiEditWait = false;

        // Set the current browser window height, initially obtained from the server.
        resizeHandler.currentWindowHeight = "<c:out value='${sessionInfo.dimensions.mainWindow}'/>";

        // Set the URL of the page that will store the dimensions map on the server.
        <c:url var="dimensionsUrl" value="/components/setValue.jsp">
          <c:param name="bean" value="/atg/web/assetmanager/SessionInfo.dimensionsString"/>
        </c:url>
        resizeHandler.dimensionsUrl = "<c:out value='${dimensionsUrl}'/>";

        //----------------------------------------------------------------------------//
        // Handle the window onload event.
        //
        function handleOnLoad() {

          createEventHandlers("<c:out value='${blankUrl}'/>");
          fireOnLoad();
          messages.handleOnLoad();
          resizeHandler.handleOnLoad();
          <%-- Keep session alive when browser is displaying asset manager --%>
          <c:if test="${config.autoRefreshInterval > 0}">
            setInterval("issueRequest('blank.jsp')", <c:out value="${config.autoRefreshInterval * 60000}"/>);
          </c:if>
          atg.assetmanager.quietFormSubmitter.initialize();
		  <%-- fix bug 158949 - adding manually parsing before dijit.byId('assetManagerDialog') --%>
		  dojo.parser.parse(); 
          window.assetManagerDialog = dijit.byId('assetManagerDialog');
        }

        // Handle the window onresize event.
        //
        function handleOnResize() {
          resizeHandler.handleOnResize();
        }


        //----------------------------------------------------------------------------//
        //
        // Confirm that the user wants to create or save the current asset
        //
        function showLogoutConfirmDialog() {
          var frame = parent.document.getElementById("logoutDialogFrame");
          var frameURL = "/AssetManager/logout.jsp";
          frame.src = frameURL;
          dijit.byId("logoutConfirmDialog").show();
        }

        //----------------------------------------------------------------------------//
        //
        // Confirm that the user wants to create or save the current asset
        //
        function hideLogoutConfirmDialog() {
          dijit.byId("logoutConfirmDialog").hide();
        }


        //----------------------------------------------------------------------------//
        //
        // show export dialog
        //
        function showExportDialog() {
          var frame = dijit.byId("exportDialog");
          var frameURL = "/AssetManager/transfer/export.jsp";
          frame.setHref(frameURL);
          dijit.byId("exportDialog").show();
        }


        function hideExportDialog() {
          dijit.byId("exportDialog").hide();
        }

        //----------------------------------------------------------------------------//
        //
        // show import dialog
        //
        function showImportDialog() {
          if (actionsAreLocked()) return; 

          var frame = parent.document.getElementById("importDialogFrame");
          var frameURL = "/AssetManager/transfer/import.jsp";
          frame.src = frameURL;

          var importdialog = dijit.byId("importDialog");
          dojo.connect(importdialog, "hide", importDialogCloseListener);
          importdialog.show();
        }

        // clean up temporary file used by import dialog
        function importDialogCloseListener() {
          issueRequest('<c:out value='${cleanUpImportURL}'/>');
        }

        function hideImportDialog() {
          dijit.byId("importDialog").hide();
        }

        // bug 157786 - make the call to hide the "SaveCreateConfirmDialog"
        // and then finishSaveBeforeLeaveAsset()/finishLeaveAsset() sequential
        // This method is called from SaveCreateConfirmDialog's "confirmSave"
        // and "confirmNoSave" buttons on the dialog.
        // @param pSaveCreate - Set to true if the asset should be saved, other simply
        // finish without saving.
        function hideSaveCreateConfirmDialog(pSaveCreate) {
          // hide the SaveCreateConfirmDialog
          // Hiding this would make the curtain disappear.
          dijit.byId("saveCreateConfirmDialog").hide();

          // These method if required would display another dialog.
          // e.g. When the user clicks on the logout link without saving
          // a modified asset - "LogoutConfirmDialog" to be displayed after
          // the "SaveCreateConfirmDialog" is 'not cancelled'.
          if (pSaveCreate) {
            // leave the edit asset window after saving the current
            // asset.
            atg.assetmanager.saveconfirm.finishSaveBeforeLeaveAsset();
          }
          else {
            // leave the edit asset window without saving the current
            // asset.
            atg.assetmanager.saveconfirm.finishLeaveAsset();
          }
        }

        //----------------------------------------------------------------------------//
        //
        // This method submits a form without using iframes.
        //  node is the input submit button of the form
        //  parent div is the parent node to set the success/error url
        //  uploadFile is true if uploading a file (need to use hidden iframe)
        //
        function submitDojoForm(node, parentDiv, uploadFile) {

          var formNode = dojo.html.getParentByType(node,"FORM");

          // call form's handle method (doesn't work for iframe.send)
          var submitButtonContent={};
          submitButtonContent[node.name]=node.value;

          dojo.require("dojo.io.iframe");

          var params = {
             //handleAs: "html",
             // Name of the Form we want to submit
             form: formNode,

             // calls the handle method on our form
             content: submitButtonContent,

             // Loads this function if everything went ok
             load: function (data) {
                // Put the response into the parent Div
                parentDiv.innerHTML = data;
             },

             // Call this function if an error happened
             error: function (error) {
                alert("error " + error);
                parentDiv.innerHTML = error;
             }
          };
          if (!uploadFile) {
            dojo.xhrPost(params);
          }
          else {
           dojo.io.iframe.send (params);
          }
        }

        //----------------------------------------------------------------------------//
        // this is the response from pinging the server for status of the current multi edit operation
        //
        var evaluateMultiEditAjaxResponse = function(pData, pResponseText) {
          var theresponse = eval("(" + pResponseText + ")");
          var percentComplete = theresponse.value;
          var successCount = theresponse.successCount;
          var errorCount = theresponse.errorCount;
          var noOpCount = theresponse.noOpCount;
          var remainCount = theresponse.remainCount;

          // publish json Topic for clock and progress bar widgets
          obj = {
            'value':percentComplete,
            'state':'Good',
            'message':''
          };
          dojo.publish("/atg/assetManager/multiEdit/progress", [obj]);

          // update the tab clock
          updateMultiEditTabClock(percentComplete);

          // if there is a right pane, update the progress bar page (confirmationWait)
          var rightiframe = window.frames.rightPaneIframe;
          if (rightiframe && rightiframe.updateMultiEditWaitBarPage) {
              rightiframe.window.dojo.publish("/atg/assetManager/multiEdit/progress", [obj]);
              rightiframe.updateMultiEditWaitBarPage(percentComplete, remainCount, successCount, errorCount, noOpCount);
          }

          // if the multi edit just completed, stop pinging & change the mode to confirmation mode
          if (percentComplete == '100') {
            clearInterval(multiEditIntervalVar);
            clearSelectedAsset = true;
            <c:if test="${param.assetManagerTab eq 'multiEdit' || (empty  param.assetManagerTab && currentView eq 'multiEdit')}">
              changeMode('<c:out value="${MODE_CONFIRM}"/>','<c:out value="${OPERATION_APPLY_TO_ALL}"/>','null')
            </c:if>
            messages.addAlert("<fmt:message key='assetManager.multiEditComplete'/>"+"<a href=\"javascript:atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame('/AssetManager/assetManager.jsp?assetManagerTab=multiEdit')\">" + "<fmt:message key='assetManager.multiEditViewDetails'/>" + "</a>");
            setMultiEditWait(false);
          }
          else setMultiEditWait(true);
        }


        //  call above method at set intervals
        //  (started from onLoad of this page and confirmationWait page)
        //
        function startPingingMultiEditProgress() {
          // if the interval wasn't set already set it here
          if (multiEditIntervalVar == null) {
            multiEditIntervalVar = setInterval("issueRequest('<c:out value='${multiEditIsCompleteURL}'/>',null,evaluateMultiEditAjaxResponse)",<c:out value="${config.multiEditAutoRefreshInterval * 1000}"/>);
          }
        }


        function setMultiEditWait(isWait) {
          multiEditWait = isWait;
        }

       atg.assetmanager.saveconfirm.initializeDialogStrings("<fmt:message key='assetEditor.confirm.create'/>","<fmt:message key='assetEditor.confirm.nocreate'/>","<fmt:message key='assetEditor.confirmCreateMessage'/>","<fmt:message key='assetEditor.confirmCreateTitle'/>","<fmt:message key='assetEditor.confirm.save'/>","<fmt:message key='assetEditor.confirm.nosave'/>","<fmt:message key='assetEditor.confirmSaveMessage'/>","<fmt:message key='assetEditor.confirmSaveTitle'/>");

      </script>

      <script type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/exportDownload.js"></script>

    </head>

    <body id="<c:out value='${bodyId}'/>" scroll="<c:out value='${bodyScroll}'/>" class="<c:out value='${config.dojoThemeClass}'/>">

      <%-- Update the session with information about the current project, task,
           and activity.  Note that this will cause the session to reset if
           any of those items have changed. --%>
      <pws:getCurrentProject var="projectContext"/>

      <c:choose>
        <c:when test="${not empty projectContext}">
          <c:set var="project" value="${projectContext.project}"/>
          <c:set var="task" value="${projectContext.task}"/>
          <c:set target="${sessionInfo}" property="currentProjectId" value="${project.id}"/>
          <c:set target="${sessionInfo}" property="currentTaskId" value="${task.taskDescriptor.taskElementId}"/>
          <c:set target="${sessionInfo}" property="currentActivityId" value="${projectContext.activity}"/>
          <c:set target="${sessionInfo}" property="reset" value="reset"/>
          <dspel:importbean var="activityManager"
                            bean="/atg/bizui/activity/ActivityManager"/>
          <asset-ui:findActivity var="activity" 
                                 activityManager="${activityManager}"
                                 activityId="${projectContext.activity}"/>
        </c:when>
        <c:otherwise>
          <c:set var="project" value="${null}"/>
          <c:set var="task" value="${null}"/>
          <c:set var="activity" value="${null}"/>
          <c:set target="${sessionInfo}" property="currentProjectId" value="${null}"/>
          <c:set target="${sessionInfo}" property="currentTaskId" value="${null}"/>
          <c:set target="${sessionInfo}" property="currentActivityId" value="${null}"/>
          <c:set target="${sessionInfo}" property="reset" value="reset"/>
        </c:otherwise>
      </c:choose>


      <%-- If the task configuration is already stored in the session, use it.
           Otherwise, find a configuration manager, and use it to determine the
           configuration.  An alternate configuration manager can be specified
           as a request parameter. --%>
      <c:set scope="request" var="managerConfig" value="${sessionInfo.taskConfiguration}"/>
      <c:if test="${empty requestScope.managerConfig}">
        <c:choose>
          <c:when test="${not empty param.taskConfiguration}">
            <dspel:importbean var="configurationManager" bean="${param.taskConfiguration}"/>
          </c:when>
          <c:otherwise>
            <c:set var="configurationManager" value="${config.configurationManager}"/>
          </c:otherwise>
        </c:choose>
        <asset-ui:getTaskConfiguration scope="request"
                                       var="managerConfig"
                                       config="${configurationManager}"/>
        <c:set target="${sessionInfo}" property="taskConfiguration" value="${requestScope.managerConfig}"/>
      </c:if>


      <c:set var="isFinishedWaiting" value="${sessionInfo.multiEditSessionInfo.multiEditRunnable.multiEditFinished}"/>
      <c:if test="${isFinishedWaiting == 'false'}">
        <script type="text/javascript">
          registerOnLoad(function() { startPingingMultiEditProgress();});
        </script>
      </c:if>

      <c:if test="${not empty requestScope.managerConfig}">

        <%-- Display the common BCC-style header. --%>
        <dspel:include page="components/header.jsp">
          <dspel:param name="projectContextParam" value="${projectContext}"/>
          <dspel:param name="projectParam" value="${project}"/>
          <dspel:param name="taskParam" value="${task}"/>
          <dspel:param name="activityParam" value="${activity}"/>
        </dspel:include>

        <c:set var="managerBundle" value="${requestScope.managerConfig.resourceBundle}"/>
        <c:if test="${empty managerBundle}">
          <c:set var="managerBundle" value="${config.resourceBundle}"/>
        </c:if>
        <fmt:bundle basename="${managerBundle}">

          <div id="contentHeader">
            <%-- Include areas in which alert and error messages
                 can be displayed --%>
            <div id="wait" style="display: none">
              <fmt:message key="assetManager.pleaseWait"/>
            </div>
            <div id="alert" style="display: none">
            </div>
            <div id="error" style="display: none">
            </div>
          </div>

          <%-- If a view parameter was specified, store it as a session variable. --%>
          <c:if test="${not empty param.assetManagerTab}">
            <c:set target="${sessionInfo}" property="assetManagerTab" value="${param.assetManagerTab}"/>
          </c:if>

          <%-- Specify a default value for the assetManagerTab variable if necessary. --%>
          <c:if test="${empty sessionInfo.assetManagerTab}">
            <c:set target="${sessionInfo}" property="assetManagerTab" value="${managerConfig.initialTab}"/>
          </c:if>

          <%-- Get the configuration object for the current tab. --%>
          <c:if test="${not empty sessionInfo.assetManagerTab}">
            <c:set scope="request"
                   var="tabConfig"
                   value="${requestScope.managerConfig.tabs[sessionInfo.assetManagerTab]}"/>
          </c:if>

          <div id="contentWrapper">
            <div id="topNav">

              <script type="text/javascript">
                function tabSelected(url) {

                  if (actionsAreLocked()) return;

                  var rightiframe = window.frames.rightPaneIframe;
                  // check that the page with the javascript function has loaded
                  if (atg.assetmanager.saveconfirm.checkForModifiedAsset) {
                    if (atg.assetmanager.saveconfirm.checkForModifiedAsset()) {
                      atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, url);
                      return;
                    }
                  }
                  document.location = url;
                }
              </script>

              <%-- Display the tab banner --%>
              <c:if test="${not empty sessionInfo.assetManagerTab}">
                <dspel:include page="/components/tabBanner.jsp">
                  <dspel:param name="editable"          value="${empty project or project.editable}"/>
                  <dspel:param name="tabIds"            value="${managerConfig.tabIds}"/>
                  <dspel:param name="selectedTab"       value="${sessionInfo.assetManagerTab}"/>
                  <dspel:param name="destinationPage"   value="${requestScope.managerConfig.page}"/>
                  <dspel:param name="tabParameter"      value="assetManagerTab"/>
                  <dspel:param name="javascriptWrapper" value="tabSelected"/>
                </dspel:include>
              </c:if>

            </div>

            <%-- Display the asset diff page if requested.  Otherwise,
                 include the page fragment for the current tab. --%>
            <c:choose>
              <c:when test="${not empty param.reviewChanges}">
                <dspel:include page="/diff/reviewChanges.jsp"/>
              </c:when>
              <c:otherwise>
                <c:if test="${not empty requestScope.tabConfig}">
                  <dspel:include otherContext="${requestScope.tabConfig.contextRoot}"
                                 page="${requestScope.tabConfig.page}">
                    <dspel:param name="assetURI" value="${assetURI}"/>
                    <dspel:param name="siteId" value="${siteId}"/>
                  </dspel:include>                           
                </c:if>
              </c:otherwise>
            </c:choose>

          </div>

          <%-- Curtain for dialogs --%>
          <div id="curtain" class="curtain"></div>

          <%-- Include an initially invisible iframe for the asset picker --%>
          <div id="browser">
            <div id="browserIframe">
              <iframe frameborder="0"
                      scrolling="no"
                      src="<c:out value='${blankUrl}'/>"
                      id="iFrame">
              </iframe>
            </div>
            <div id="browserBottom">
            </div>
          </div>

          <%-- Include an initially invisible iframe for the confirmation dialog --%>
          <div id="confirm">
            <div id="confirmIframe">
              <iframe frameborder="0"
                      scrolling="no"
                      src="<c:out value='${blankUrl}'/>"
                      id="iFrame2">
              </iframe>
            </div>
            <div id="confirmBottom"></div>
          </div>


          <%-- Include an initially invisible iframe for the volume dialog --%>
          <div id="volumeDialog">
            <div id="volumeDialogIframe">
              <iframe frameborder="0"
                      scrolling="no"
                      src="<c:out value='${blankUrl}'/>"
                      id="volumeDialogFrame"
                      name="volumeDialogFrame">
              </iframe>
            </div>
            <div id="volumeDialogBottom">
            </div>
          </div>

          <%-- Include an initially invisible iframe for the error dialog --%>
          <c:url var="errorDialogUrl" value="/errorDialog.jsp"/>
          <div id="errorDialogContainer">
            <div id="errorDialogFrameContainer">
              <iframe frameborder="0"
                      scrolling="no"
                      id="errorDialogFrame"
                      name="errorDialogFrame"
                      src="<c:out value='${errorDialogUrl}'/>">
              </iframe>
            </div>
            <div id="errorDialogBottom"></div>
          </div>

          <div dojoType="dojox.Dialog"
            id="exportDialog"
            bgColor="white"
            bgOpacity="0.5"
            toggle="fade"
            closeHoverText="<fmt:message key='common.close'/>"
            src="<c:out value='${blankUrl}'/>"
            allowtransparency="true"
            toggleDuration="250"
            title="<fmt:message key='transfer.exportDialog.title'/>"
            style="display:none; width: 900px; max-width: 900px; height: 380px; max-height: 400px;">
          </div>


          <div dojoType="dojox.Dialog"
            id="importDialog"
            bgColor="white"
            closeHoverText="<fmt:message key='common.close'/>"
            bgOpacity="0.5"
            toggle="fade"
            toggleDuration="250"
            title="Import"
            style="display:none; width: 900px; max-width: 900px; height: 380px; max-height: 400px;">
            <div id="importIframe">
              <iframe frameborder="0"
                      scrolling="no"
                      src="<c:out value='${blankUrl}'/>"
                      id="importDialogFrame"
                      name="importDialogFrame">
              </iframe>
            </div>
          </div>

          <div dojoType="dojox.Dialog"
            id="saveCreateConfirmDialog"
            bgColor="white"
            bgOpacity="0.5"
            toggle="fade"
            toggleDuration="250"
            closeHoverText="<fmt:message key='common.close'/>"
            title="<fmt:message key='assetEditor.confirmSaveTitle'/>"
            style="display:none">
            <div id="saveCreateConfirmDialogBody">
              <p id="confirmSaveMessage"><fmt:message key="assetEditor.confirmSaveMessage"/></p>
            </div>
            <div id="saveCreateConfirmDialogFooter">
              <a class="button" id="confirmSave" href="javascript:hideSaveCreateConfirmDialog(true);">
                <span id="confirmSaveButton">
                  <fmt:message key="assetEditor.confirm.save"/>
                </span>
              </a>
                <span id="confirmNoSaveButtonOuter">
              <a class="button" id="confirmNoSave" href="javascript:hideSaveCreateConfirmDialog(false);">
                <span id="confirmNoSaveButton">
                  <fmt:message key="assetEditor.confirm.nosave"/>
                </span>
              </a>
                </span>
              <a class="button" id="confirmCancelSave" href="javascript:dijit.byId('saveCreateConfirmDialog').hide()">
                <span >
                  <fmt:message key="common.cancel"/>
                </span>
              </a>
            </div>
          </div>


          <div dojoType="dojox.Dialog"
            id="logoutConfirmDialog"
            bgColor="white"
            bgOpacity="0.5"
            toggle="fade"
            toggleDuration="250"
            title="<fmt:message key='assetManager.logoutTitle'/>"
            style="display:none">
            <div id="lougoutDialogIframe">
              <iframe frameborder="0"
                      scrolling="no"
                      src="<c:out value='${blankUrl}'/>"
                      allowtransparency="true"
                      id="logoutDialogFrame"
                      name="logoutDialogFrame">
              </iframe>
            </div>
          </div>

          <div dojoType="dojox.Dialog" id="assetManagerDialog" href="#" style="display: none"></div>

        </fmt:bundle>
      </c:if>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetManager.jsp#3 $$Change: 654746 $--%>
