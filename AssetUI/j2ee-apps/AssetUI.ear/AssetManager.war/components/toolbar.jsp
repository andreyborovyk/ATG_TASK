<%--
 toolbar.jspf

 This fragment renders the toolbar, including those buttons that the container page declares to be allowed.

    page elements:
      dspel:form         this fragment must be included inside a dspel:form element named listActionForm

    page variables:
      treeComponent      nucleus path to the tree component currently showing (if any)
      formHandlerPath    nucleus path to the form handler component
      allowDuplicate     whether to alow duplicate button
      allowDelete        whether to allow delete button
      allowAddToProject
      allowRemoveFromProject
      allowAddToMultiEdit
      allowRemoveFromMultiEdit
      allowLink
      allowMove
      allowUnlink
      allowCheckAll
      allowUncheckAll
      allowCreate
--%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"                %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="formHandlerPath"          param="formHandlerPath"/>
  <dspel:getvalueof var="treeComponent"            param="treeComponent"/>
  <dspel:getvalueof var="tab"                      param="tab"/>
  <dspel:getvalueof var="fullItemType"                 param="itemType"/>

  <dspel:getvalueof var="allowDuplicate"           param="allowDuplicate"/>
  <dspel:getvalueof var="allowDelete"              param="allowDelete"/>
  <dspel:getvalueof var="allowAddToProject"        param="allowAddToProject"/>
  <dspel:getvalueof var="allowRemoveFromProject"   param="allowRemoveFromProject"/>
  <dspel:getvalueof var="allowAddToMultiEdit"      param="allowAddToMultiEdit"/>
  <dspel:getvalueof var="allowRemoveFromMultiEdit" param="allowRemoveFromMultiEdit"/>
  <dspel:getvalueof var="allowLink"                param="allowLink"/>
  <dspel:getvalueof var="allowMove"                param="allowMove"/>
  <dspel:getvalueof var="allowUnlink"              param="allowUnlink"/>
  <dspel:getvalueof var="allowCheckAll"            param="allowCheckAll"/>
  <dspel:getvalueof var="allowUncheckAll"          param="allowUncheckAll"/>
  <dspel:getvalueof var="allowCreate"              param="allowCreate"/>
  <dspel:getvalueof var="allowExport"              param="allowExport"/>
  <dspel:getvalueof var="allowDuplicateAndMove"    param="allowDuplicateAndMove"/>
  <dspel:getvalueof var="allowNewSearch"           param="allowNewSearch"/>
  <dspel:getvalueof var="allowStepEdit"            param="allowStepEdit"/>
  <dspel:getvalueof var="allowApplyToAll"          param="allowApplyToAll"/>
  <dspel:getvalueof var="allowListEdit"            param="allowListEdit"/>
  <dspel:getvalueof var="allowTransformations"     param="allowTransformations"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="toolbarConfiguration" bean="/atg/web/assetmanager/action/ToolbarConfiguration"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="assetPickerTreePath" value="${config.assetPickerTreePath}"/>
  <c:set var="assetPickerTree" value="${config.assetPickerTree}"/>
  <c:set var="modeConfig" value="${requestScope.tabConfig.views[sessionInfo.browseView]}"/>
  <c:set var="assetBrowser" value="${sessionInfo.currentAssetBrowser}"/>

  <%-- trees use their own checkbox management --%>
  <c:set var="checkAllFunction" value="checkAll()"/>
  <c:set var="uncheckAllFunction" value="uncheckAll()"/>
  <c:if test="${treeComponent eq null}">
    <dspel:include page="/components/checkboxManagement.jsp"/>
    <c:set var="checkAllFunction" value="atg.assetmanager.checkboxes.checkAll()"/>
    <c:set var="uncheckAllFunction" value="atg.assetmanager.checkboxes.uncheckAll()"/>
  </c:if>

  <%-- figure out what is the source tree for data to fill the asset picker if we should need to pop it up --%>
  <asset-ui:initAssetPickerData var="pickerData"/>

  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>


  <%-- Used for loading icon.--%>
  <dspel:importbean var="formHandler" bean="${formHandlerPath}"/>
  <c:set var="linkRefresh" value="${formHandler.shouldRefreshOnLink}"/>
  <c:set var="duplicateRefresh" value="${formHandler.shouldRefreshOnDuplicate}"/>
  <c:set var="moveRefresh" value="${formHandler.shouldRefreshOnMove}"/>
  <c:set var="unlinkRefresh" value="${formHandler.shouldRefreshOnUnlink}"/>
  <c:set var="deleteRefresh" value="${formHandler.shouldRefreshOnDelete}"/>
  <c:set var="addToProjectRefresh" value="${formHandler.shouldRefreshOnAddToProject}"/>
  <c:set var="discardFromProjectRefresh" value="${formHandler.shouldRefreshOnDiscardFromProject}"/>
  <c:set var="addToMultiEditRefresh" value="${formHandler.shouldRefreshOnAddToMultiEdit}"/>
  <c:set var="removeFromMultiEditRefresh" value="${formHandler.shouldRefreshOnRemoveFromMultiEdit}"/>

  <c:if test="${tab eq 'search'}">
    <dspel:importbean var="forbiddenOperationRegistry" bean="/atg/web/assetmanager/configuration/ForbiddenOperationRegistry" />

    <c:if test="${!empty forbiddenOperationRegistry}">
    <c:if test="${!empty fullItemType}" >
      <c:forEach items="${forbiddenOperationRegistry.parsedTypeOperationMap[fullItemType]}" var="op">
        <c:if test="${op eq 'duplicate'}"><c:set var="allowDuplicate" value="${false}" /></c:if>
        <c:if test="${op eq 'delete'}"><c:set var="allowDelete" value="${false}" /></c:if>
        <c:if test="${op eq 'addToProject'}"><c:set var="allowAddToProject" value="${false}" /></c:if>
        <c:if test="${op eq 'removeFromProject'}"><c:set var="allowRemoveFromProject" value="${false}" /></c:if>
        <c:if test="${op eq 'addToMultiEdit'}"><c:set var="allowAddToMultiEdit" value="${false}" /></c:if>
        <c:if test="${op eq 'removeFromMultiEdit'}"><c:set var="allowRemoveFromMultiEdit" value="${false}" /></c:if>
        <c:if test="${op eq 'link'}"><c:set var="allowLink" value="${false}" /></c:if>
        <c:if test="${op eq 'move'}"><c:set var="allowMove" value="${false}" /></c:if>
        <c:if test="${op eq 'unlink'}"><c:set var="allowUnlink" value="${false}" /></c:if>
        <c:if test="${op eq 'create'}"><c:set var="allowCreate" value="${false}" /></c:if>
        <c:if test="${op eq 'duplicateAndMove'}"><c:set var="allowDuplicateAndMove" value="${false}" /></c:if>
        <c:if test="${op eq 'stepEdit'}"><c:set var="allowStepEdit" value="${false}" /></c:if>
        <c:if test="${op eq 'applyToAll'}"><c:set var="allowApplyToAll" value="${false}" /></c:if>
        <c:if test="${op eq 'listEdit'}"><c:set var="allowListEdit" value="${false}" /></c:if>
        <c:if test="${op eq 'transformations'}"><c:set var="allowTransformations" value="${false}" /></c:if>
        <c:if test="${op eq 'export'}"><c:set var="allowExport" value="${false}" /></c:if>
      </c:forEach>
    </c:if>
    </c:if>
  </c:if>

  <dspel:input type="hidden" bean="${formHandlerPath}.ajaxSuccessUrl" value="components/json/toolbarResults.jsp"/>
  <dspel:input type="hidden" bean="${formHandlerPath}.ajaxErrorUrl" value="components/json/toolbarResults.jsp"/>

  <table id="leftPaneToolbar">
    <tr>
      <td id="leftPaneToolbarBack">



        <div class="leftPaneToolbarRight">

          <c:if test="${allowCreate || allowDuplicate || allowDuplicateAndMove ||
                        allowMove || allowLink || allowUnlink || allowAddToProject ||
                        allowAddToMultiEdit || allowStepEdit || allowListEdit ||
                        allowApplyToAll || allowTransformations || allowExport ||
                        allowDelete || allowRemoveFromMultiEdit ||
                        allowRemoveFromProject }">

            <div class="toolbarWrapper">
            <div class="toolbarContainer">
            <c:if test="${allowCreate}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="buttonSepBorder newButtonOn iconDD" id="newButton" href="#"
                 accesskey="<fmt:message key='browseTab.accessKey.create'/>"
                 title="<fmt:message key='browseTab.button.create'/>">
              </a>
            </c:if>

            <%-- duplicate button --%>
            <c:if test="${allowDuplicate}">
              <a href="javascript:doNonDestinationAction('duplicate')"
                class="buttonSepBorder duplicate"
                accesskey="<fmt:message key='browseTab.accessKey.launchDuplicate'/>"
                title="<fmt:message key='browseTab.button.launchDuplicate'/>">
              </a>
              <dspel:input id="duplicate" type="submit" style="display:none"
                           bean="${formHandlerPath}.duplicate" value=""
                           iclass="quietformsubmitter"/>

            </c:if>

            <%-- duplicate and move button --%>
            <c:if test="${allowDuplicateAndMove}">
              <a href="javascript:doDestinationAction('duplicateAndMove')"
                class="buttonSepBorder duplicate"
                accesskey="<fmt:message key='browseTab.accessKey.launchDuplicate'/>"
                title="<fmt:message key='browseTab.button.launchDuplicate'/>">
              </a>
              <dspel:input id="duplicateAndMove" type="submit" style="display:none"
                           bean="${formHandlerPath}.duplicateAndMove" value=""
                           iclass="quietformsubmitter"/>

            </c:if>

            <%-- move button --%>
            <c:if test="${allowMove}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="move buttonSepBorder linkDD" id="moveDropDownButton" href="#"
                 accesskey="<fmt:message key='browseTab.accessKey.launchMove'/>"
                 title="<fmt:message key='browseTab.button.launchMove'/>">
              </a>

  <%--
              <input id="moveFromAllAssetPickerButton" type="submit"
                onclick="doDestinationAction('moveFromAll')"
                style="display:none" value=""/>
  --%>

              <dspel:input id="move" type="submit" style="display:none"
                           bean="${formHandlerPath}.move" value=""
                           iclass="quietformsubmitter"/>
              <dspel:input id="moveFromAll" type="submit" style="display:none"
                           bean="${formHandlerPath}.moveFromAll" value=""
                           iclass="quietformsubmitter"/>
            </c:if>

            <%-- link button --%>
            <c:if test="${allowLink}">
              <a href="javascript:doDestinationAction('link')"
                 class="buttonSepBorder linkCopy"
                 accesskey="<fmt:message key='browseTab.accessKey.launchLink'/>"
                 title="<fmt:message key='browseTab.button.launchLink'/>">
              </a>
              <dspel:input id="link" type="submit" style="display:none"
                           bean="${formHandlerPath}.multiLink" value=""
                           iclass="quietformsubmitter"/>

            </c:if>

            <%-- unlink button --%>
            <c:if test="${allowUnlink}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="unlink buttonSepBorder linkDD" id="unlinkDropDownButton" href="#"
                 accesskey="<fmt:message key='browseTab.accessKey.unlink'/>"
                 title="<fmt:message key='browseTab.button.unlink'/>">
              </a>

              <dspel:input id="unlinkButton" type="submit" style="display:none"
                           bean="${formHandlerPath}.unlink" value=""
                           iclass="quietformsubmitter"/>
              <dspel:input id="unlinkFromAllButton" type="submit" style="display:none"
                           bean="${formHandlerPath}.unlinkFromAll" value=""
                           iclass="quietformsubmitter"/>
            </c:if>

            <%-- addToProject button --%>
            <c:if test="${allowAddToProject}">

              <%-- Include the dotted line to the right of the button only if
                   there is an Add to Multi-Edit button. --%>
              <c:choose>
                <c:when test="${allowAddToMultiEdit}">
                  <c:set var="buttonClass" value="buttonSepBorder addToProject"/>
                </c:when>
                <c:otherwise>
                  <c:set var="buttonClass" value="addToProject"/>
                </c:otherwise>
              </c:choose>

              <a href="javascript:doNonDestinationAction('addToProject')"
                 class="<c:out value='${buttonClass}'/>"
                 accesskey="<fmt:message key='browseTab.accessKey.addToProject'/>"
                 title="<fmt:message key='browseTab.button.addToProject'/>">
              </a>
              <dspel:input id="addToProject" type="submit" style="display:none"
                           bean="${formHandlerPath}.addToProject" value=""
                           iclass="quietformsubmitter"/>
            </c:if>

            <%-- addToMultiEdit button --%>
            <c:if test="${allowAddToMultiEdit}">
              <a href="javascript:doNonDestinationAction('addToMultiEdit')"
                 class="addToWorkspace buttonSepBorder"
                 accesskey="<fmt:message key='browseTab.accessKey.addToMultiEdit'/>"
                 title="<fmt:message key='browseTab.button.addToMultiEdit'/>">
              </a>
              <dspel:input id="addToMultiEdit" type="submit" style="display:none"
                          bean="${formHandlerPath}.addToMultiEdit" value=""
                          iclass="quietformsubmitter"/>
            </c:if>

            <%-- Multi Edit Step Edit --%>
            <c:if test="${allowStepEdit}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="stepEdit buttonSepBorder iconDD" id="stepEdit" href="#"
                 accesskey="<fmt:message key='multiEdit.accessKey.stepEdit'/>"
                 title="<fmt:message key='multiEdit.button.stepEdit'/>"></a>
            </c:if>

            <%-- Multi Edit List Edit --%>
            <c:if test="${allowListEdit}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="listEdit buttonSepBorder iconDD" id="listEdit" href="#"
                 accesskey="<fmt:message key='multiEdit.accessKey.listEdit'/>"
                 title="<fmt:message key='multiEdit.button.listEdit'/>"></a>
            </c:if>

            <%-- Multi Edit Apply To All --%>
            <c:if test="${allowApplyToAll}">
              <%-- no href here so that the id will be used for deciding what kind of pull down to show --%>
              <a class="applyToAll buttonSepBorder iconDD" id="applyToAll" href="#"
                 accesskey="<fmt:message key='multiEdit.accessKey.applyToAll'/>"
                 title="<fmt:message key='multiEdit.button.applyToAll'/>"></a>
            </c:if>

            <%-- Multi Edit Transformations --%>
            <c:if test="${allowTransformations}">
              <a class="transform iconDD" id="transformations" href="#"
                 accesskey="<fmt:message key='multiEdit.accessKey.transformations'/>"
                 title="<fmt:message key='multiEdit.button.transformations'/>"></a></div>
            </c:if>

            <%-- export button --%>
            <c:if test="${allowExport}">
              <a id="exportToolbarButton" href="javascript:javascript:doNonDestinationAction('export')"
                class="export"
                accesskey="<fmt:message key='browseTab.accessKey.launchEport'/>"
                title="<fmt:message key='browseTab.button.export'/>">
              </a>
            </c:if>

            <%-- delete button --%>
            <c:if test="${allowDelete}">
                <a href="javascript:doNonDestinationAction('delete')"
                   class="delete"
                   accesskey="<fmt:message key='browseTab.accessKey.launchDelete'/>"
                   title="<fmt:message key='browseTab.button.launchDelete'/>">
                </a>
                <dspel:input id="deleteButton" type="submit" style="display:none"
                             bean="${formHandlerPath}.delete" value=""
                             iclass="quietformsubmitter"/>
            </c:if>

            <%-- removeFromMultiEdit button --%>
            <c:if test="${allowRemoveFromMultiEdit}">
                <a href="javascript:doNonDestinationAction('removeFromMultiEdit')"
                   class="remove"
                   accesskey="<fmt:message key='multiEdit.accessKey.removeFromMultiEdit'/>"
                   title="<fmt:message key='multiEdit.button.removeFromMultiEdit'/>">
                </a>
                <dspel:input id="discardAssets" type="submit" style="display:none"
                             bean="${formHandlerPath}.removeFromMultiEdit"
                             iclass="quietformsubmitter"/>
            </c:if>

            <%-- removeFromProject button --%>
            <c:if test="${allowRemoveFromProject}">
                <a href="javascript:doNonDestinationAction('removeFromProject')"
                   class="remove"
                   accesskey="<fmt:message key='browseTab.accessKey.removeFromProject'/>"
                   title="<fmt:message key='browseTab.button.removeFromProject'/>">
                </a>
                <dspel:input id="discardAssets" type="submit" style="display:none"
                             bean="${formHandlerPath}.discardFromProject"
                             iclass="quietformsubmitter"/>
            </c:if>

            </div>
          </div>
        </c:if>

        </div>

        <div class="leftPaneToolbarLeft">
          <c:if test="${allowCheckAll}">
            <div class="selectAllContainer">
              <input id="checkAllCheckbox" name="checkAllCheckbox" type="checkbox" class="selectAll"
                     title="<fmt:message key='browseTab.button.checkAll'/>" onclick='<c:out value="${checkAllFunction}"/>'/>
          </c:if>
          <c:if test="${allowUncheckAll}">
            <div class="selectAllContainer">
              <input id="uncheckAllCheckbox" name="uncheckAllCheckbox" type="checkbox" class="selectAll"
                     title="<fmt:message key='browseTab.button.uncheckAll'/>" onclick='<c:out value="${uncheckAllFunction}"/>'>
          </c:if>
        </div>
      </td>
    </tr>
  </table>

  <c:url var="deleteConfirmUrl" value="/components/toolbarConfirm.jsp">
    <c:param name="buttonName" value="deleteButton"/>
    <c:param name="confirmationGeneratorName" value="/atg/web/assetmanager/action/DeleteConfirmationGenerator"/>
  </c:url>
  <c:url var="unlinkConfirmUrl" value="/components/unlinkConfirm.jsp">
    <c:param name="buttonName" value="unlinkFromAllButton"/>
  </c:url>
  <c:url var="moveConfirmUrl" value="/components/unlinkConfirm.jsp">
    <c:param name="functionName" value="continueDoDestinationAction"/>
    <c:param name="functionParamValue" value="moveFromAll"/>
  </c:url>
  <c:url var="removeFromProjectConfirmUrl" value="/components/toolbarConfirm.jsp">
    <c:param name="buttonName" value="discardAssets"/>
    <c:param name="confirmationGeneratorName" value="/atg/web/assetmanager/action/RemoveFromProjectConfirmationGenerator"/>
  </c:url>
  <c:url var="aboveThresholdConfirmUrl" value="/components/aboveThresholdConfirm.jsp">
  </c:url>
  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/toolbar.js">
  </script>

  <script type="text/javascript" >

    atg.assetmanager.toolbar.initialize();
    atg.assetmanager.toolbar.assetEditorUrl = "<c:out value='${config.contextRoot}'/><c:out value='${requestScope.tabConfig.editorConfiguration.page}'/>";
    //
    // Page wide js variables
    //

    // the action to invoke when asset picker closes
    var m_toolbarActionButton = "";

    // whether or not an error condition has been found
    var m_actionErrorExists = false;


    function getNumberOfChecks() {
       var numberOfChecks = 0;
       <c:choose>
         <c:when test="${treeComponent eq null}">
           numberOfChecks = atg.assetmanager.checkboxes.numChecks;
         </c:when>
         <c:otherwise>
           numberOfChecks = numChecks;
         </c:otherwise>
       </c:choose>
       return numberOfChecks;
    }

    function getThreshold(pAction) {
      var threshold = 20000;
      if (pAction === "link")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.link}'/>;
      }
      else if (pAction === "unlinkFromAll")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.unlink}'/>;
      }
      else if (pAction === "unlink")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.unlink}'/>;
      }
      else if (pAction === "duplicate")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.duplicate}'/>;
      }
      else if (pAction === "duplicateAndMove")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.duplicateAndMove}'/>;
      }
      else if (pAction === "addToProject")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.addToProject}'/>;
      }
      else if (pAction === "removeFromProject")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.discardFromProject}'/>;
      }
      else if (pAction === "addToMultiEdit")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.addToMultiEdit}'/>;
      }
      else if (pAction === "removeFromMultiEdit")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.removeFromMultiEdit}'/>;
      }
      else if (pAction === "delete")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.delete}'/>;
      }
      else if (pAction === "move")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.move}'/>;
      }
      else if (pAction === "moveFromAll")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.move}'/>;
      }
      else if (pAction === "export")
      {
        threshold = <c:out value='${toolbarConfiguration.actionItemCountThresholds.export}'/>;
      }
      return (threshold);
    }

    function processActionErrors(pAction) {
       // clear out old error and status messages
       messages.hide();

       m_actionErrorExists = false;

       // if no items were checked, then we have nothing to operate on.  throw an error.
       // numChecks is managed separately by trees and lists
       // NOTE: There is a glitch that when there are no checkable items, the numChecks comes out as -1.

       var numberOfChecks = getNumberOfChecks();
       if (numberOfChecks <= 0) {
         var errstring;
         if (pAction == "duplicate")
         {
           errstring = "<fmt:message key='error.duplicate.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "duplicateAndMove")
         {
           errstring = "<fmt:message key='error.duplicate.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "link")
         {
           errstring = "<fmt:message key='error.link.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "move")
         {
           errstring = "<fmt:message key='error.move.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "moveFromAll")
         {
           errstring = "<fmt:message key='error.move.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "unlink")
         {
           errstring = "<fmt:message key='error.unlink.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "unlinkFromAll")
         {
           errstring = "<fmt:message key='error.unlink.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "addToProject")
         {
           errstring = "<fmt:message key='error.addToProject.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "addToMultiEdit")
         {
           errstring = "<fmt:message key='error.addToMultiEdit.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "removeFromProject")
         {
           errstring = "<fmt:message key='error.discardFromProject.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "removeFromMultiEdit")
         {
           errstring = "<fmt:message key='error.discardFromMultiEdit.noAssetSelectedForOperation'/>";
         }
         else if (pAction == "delete")
         {
           errstring = "<fmt:message key='error.delete.noAssetSelectedForOperation'/>";
         }
         else
         {
           errstring = "<fmt:message key='error.general.noAssetSelectedForOperation'/>";
         }

         m_actionErrorExists = true;
         messages.addError(errstring);
       }
    }

    //
    // load waiting icon during toolbar action
    //
    function startWaiting(pAction) {
      var doRefresh = false;
      if (pAction == "link" && <c:out value="${linkRefresh}"/>) {
        doRefresh = true;
      }
      else if ((pAction == "duplicate" || pAction == "duplicateAndMove") && <c:out value="${duplicateRefresh}"/>) {
        doRefresh = true;
      }
      else if ((pAction == "move" || pAction == "moveFromAll") && <c:out value="${moveRefresh}"/> ) {
        doRefresh = true;
      }
      else if ((pAction == "unlink" || pAction == "unlinkFromAll") && <c:out value="${unlinkRefresh}"/> ) {
        doRefresh = true;
      }
      else if (pAction == "delete" && <c:out value="${deleteRefresh}"/> ) {
        doRefresh = true;
      }
      else if (pAction == "addToProject" && <c:out value="${addToProjectRefresh}"/> ) {
        doRefresh = true;
      }
      else if (pAction == "removeFromProject" && <c:out value="${discardFromProjectRefresh}"/> ) {
        doRefresh = true;
      }
      else if (pAction == "addToMultiEdit" && <c:out value="${addToMultiEditRefresh}"/> ) {
        doRefresh = true;
      }
      else if (pAction == "removeFromMultiEdit" && <c:out value="${removeFromMultiEditRefresh}"/> ) {
        doRefresh = true;
      }
      startWait(!doRefresh, doRefresh);
    }

    //
    // show the delete confirmation message
    //
    function launchDelete() {
      var confirmuri = '<c:out value="${deleteConfirmUrl}" escapeXml="false"/>';
      document.getElementById("iFrame2").src = confirmuri;
      showIframe("confirm", true);
    }

    //
    // show the unlink from all confirmation message
    //
    function launchUnlinkFromAll() {
      var confirmuri = '<c:out value="${unlinkConfirmUrl}" escapeXml="false"/>';
      document.getElementById("iFrame2").src = confirmuri;
      showIframe("confirm", true);
    }

    //
    // Export Assets
    //
    function launchExport() {
      parent.showExportDialog();
      hideIframe("confirm");
    }

    //
    // show the move from all confirmation message
    //
    function launchMoveFromAll() {
      // Invoke processActionErrors before doing checkThreshold or save confirmation dialog so
      // that we don't put up all those other dialogs for nothing.
      processActionErrors("move");
      if (m_actionErrorExists)
      {
        return;
      }

      atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.checkThresholdForMoveFromAll, null, "checkThresholdForMoveFromAll");
    }

    function checkThresholdForMoveFromAll() {
      if (checkThreshold("continueLaunchMoveFromAll", "moveFromAll"))
      {
         continueLaunchMoveFromAll();
      }
    }


    //
    // show the move from all confirmation message
    //
    function continueLaunchMoveFromAll() {
      var confirmuri = '<c:out value="${moveConfirmUrl}" escapeXml="false"/>';
      document.getElementById("iFrame2").src = confirmuri;
      showIframe("confirm", true);
    }


    //
    // show the remove from project confirmation message
    //
    function launchRemoveFromProject() {
      var confirmuri = '<c:out value="${removeFromProjectConfirmUrl}" escapeXml="false"/>';
      document.getElementById("iFrame2").src = confirmuri;
      showIframe("confirm", true);
    }

    //
    // do an action that does not require the picker (delete, unlink, etc)
    //
    // first param is 'delete', 'unlink' etc
    // second param is 'all' or 'single' for unlink from all locations or just from selected location
    function doNonDestinationAction(pAction) {
      if (actionsAreLocked()) return;

      // Invoke processActionErrors before doing checkThreshold or save confirmation dialog so
      // that we don't put up all those other dialogs for nothing.
      processActionErrors(pAction);
      if (m_actionErrorExists)
      {
        return;
      }

      // For these operations we do not need to save any outstanding changes.
      // Any asset on right pane will remain in place with edits in still ready to save,
      // for addToProject and addToMultiedit.
      // In the case of removeFromProject, any change you save will get reverted.
      // In the case of delete, it may be another asset being deleted, so still want to prompt.
      // this asset is outta here.
      if (pAction == "addToProject" || 
          pAction == "addToMultiEdit" || 
          pAction == "removeFromProject" ||
          pAction == "export" )
      {
        checkThresholdForNonDestinationAction(pAction);
      }
      else
      {
        atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.checkThresholdForNonDestinationAction, pAction, "checkThresholdForNonDestinationAction");
      }
    }

    function checkThresholdForNonDestinationAction(pAction) {
      if (checkThreshold("continueDoNonDestinationAction", pAction))
      {
        continueDoNonDestinationAction(pAction);
      }
    }

    function continueDoNonDestinationAction(pAction) {

      // click the appropriate button
      if (pAction == "duplicate")
      {
        startWaiting(pAction);
        document.listActionForm.duplicate.click();
      }
      else if (pAction == "unlink")
      {
        startWaiting(pAction);
        document.listActionForm.unlinkButton.click();
      }
      else if (pAction == "unlinkFromAll")
      {
        launchUnlinkFromAll();
      }
      else if (pAction == "addToProject")
      {
        startWaiting(pAction);
        document.listActionForm.addToProject.click();
      }
      else if (pAction == "addToMultiEdit")
      {
        startWaiting(pAction);
        document.listActionForm.addToMultiEdit.click();
      }
      else if (pAction == "removeFromMultiEdit")
      {
        startWaiting(pAction);
        document.getElementById('discardAssets').click();
      }
      else if (pAction == "removeFromProject")
      {
        launchRemoveFromProject();
      }
      else if (pAction == "delete")
      {
        launchDelete();
      }
      else if (pAction == "export")
      {
        launchExport();
      }

    }

    //
    // do an action that requires the asset picker (dup&move, link, move)
    //
    function doDestinationAction(pAction) {
      if (actionsAreLocked()) return;

      // Invoke processActionErrors before doing checkThreshold or save confirmation dialog so
      // that we don't put up all those other dialogs for nothing.
      processActionErrors(pAction);
      if (m_actionErrorExists)
      {
        return;
      }

      atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.checkThresholdForDestinationAction, pAction, "checkThresholdForDestinationAction");
    }

    function checkThresholdForDestinationAction(pAction) {
      if (checkThreshold("continueDoDestinationAction", pAction))
      {
        continueDoDestinationAction(pAction);
      }
    }

    function checkThreshold(pFunction, pAction) {
      var numberOfChecks = getNumberOfChecks();
      var threshold = getThreshold(pAction);
      if (threshold == -1 || numberOfChecks < threshold)
         return true;
      showAboveThresholdDialog(pFunction, pAction);
      return false;
    }

    function showAboveThresholdDialog(pFunction, pAction) {
      var confirmuri = '<c:out value="${aboveThresholdConfirmUrl}" escapeXml="false"/>' + "?function=" + pFunction + "&action=" + pAction;
      document.getElementById("iFrame2").src = confirmuri;
      showIframe("confirm");
    }

    function continueDoDestinationAction(pAction) {
      <c:choose>
        <c:when test="${not empty pickerData}">
          if (pAction == "link")
          {
            showMultiAssetPicker(pAction);
          }
          else
          {
            showAssetPicker(pAction);
          }
          return;
        </c:when>
        <c:otherwise>
          <%-- We do not know enough to pop up the picker. Change a dup&move to just a dup and reject other actions. --%>
          if (pAction == "duplicateAndMove")
          {
            startWaiting(pAction);
            document.listActionForm.duplicateAndMove.click();
          }
          else if (pAction == "link" || pAction == "move")
          {
            messages.addError("<fmt:message key='error.linkMove.unsupportedType'/>");
          }
        </c:otherwise>
      </c:choose>
    }

    function showMultiAssetPicker(pAction) {
      doShowAssetPicker(pAction, "true", "onPickerMultiSelect");
    }
    function showAssetPicker(pAction) {
      doShowAssetPicker(pAction, "false", "onPickerSelect");
    }

    //
    // Invoke Asset Picker iFrame
    //
    function doShowAssetPicker( pAction, pAllowMultiSelect, pOnSelectFunction ) {
      m_toolbarActionButton = pAction;

      // these pickable types will be the possible parent types for current item type
      var pickableTypes = new Array( 0 );

      // for each of the possible parent types, add an "assetType" to the asset picker's list
      <c:forEach var="parenttype" items="${pickerData.types}">
        var assetType = new Object();
        assetType.typeCode            = 'repository';
        assetType.displayName         = '<c:out value="${parenttype}"/>';
        assetType.typeName            = '<c:out value="${parenttype}"/>';
        assetType.repositoryName      = '<c:out value="${pickerData.containerName}"/>';
        assetType.componentPath       = '<c:out value="${pickerData.containerPath}"/>';
        assetType.createable = "false";
        pickableTypes[ pickableTypes.length ] = assetType;
      </c:forEach>
      var viewConfiguration = new Array();
      viewConfiguration.treeComponent = '<c:out value="${pickerData.sourceTreePath}"/>';
      // See changelists 438448 and 438450.
      //viewConfiguration.treeComponent = '<c:out value="${config.assetPickerTreePath}"/>';
      if ("<c:out value='${pickerData.pickableTypesFromTree}'/>".length > 0)
      {
        viewConfiguration.getPickableTypesFromTree = "<c:out value='${pickerData.pickableTypesFromTree}'/>";
      }

      var picker = new AssetBrowser();
      picker.mapMode            = "AssetManager.assetPicker";
      picker.clearAttributes    = "true";
      picker.pickerURL          = '<c:out value="${config.assetPickerRoot}${config.assetPicker}"/>?apView=1&';
      <fmt:message var="assetPickerTitle" key="browseTab.destinationPickerTitle"/>
      <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
      picker.assetPickerTitle   = "<c:out value='${assetPickerTitle}'/>";
      picker.browserMode        = "pick";
      picker.onSelect           = pOnSelectFunction;
      picker.isAllowMultiSelect = pAllowMultiSelect;
      picker.createMode         = "none";
      picker.closeAction        = "hide";
      picker.assetTypes         = pickableTypes;
      picker.viewConfiguration  = viewConfiguration;
      picker.defaultView        = globalAssetPickerDefaultView;
      picker.assetPickerFrameElement = document.getElementById('iFrame');
      picker.assetPickerDivId        = "browser";
      picker.assetPickerParentWindow = window;
      picker.invoke();

    }

    //
    // this is called when an OK is clicked in the asset picker
    //
    function onPickerSelect(checkedItem) {
      //alert("DEBUG: browseTab.jsp: What was picked by the asset picker? " + checkedItem);
      if (checkedItem.uri)
      {
        document.getElementById("pickedAssetURI").value = checkedItem.uri;
      }
      else
      {
        document.getElementById("pickedAssetURI").value = checkedItem;
      }
      // Click the hidden button that submits the form to perform the action.
      document.getElementById(m_toolbarActionButton).click();

      startWaiting(m_toolbarActionButton);

      m_toolbarActionButton = "";

    }

    //
    // this is called when an OK is clicked in the multi-select asset picker
    //
    function onPickerMultiSelect(checkedItems) {
      for (i = 0; i < checkedItems.length; i++) 
      {
        if (checkedItems[i].uri)
        {
          checkedItems[i] = checkedItems[i].uri;
        }
        //alert("checkedItems[i] = " + checkedItems[i])
      }
      document.getElementById("pickedAssetURIs").value = checkedItems;

      // Click the hidden button that submits the form to perform the action.
      document.getElementById(m_toolbarActionButton).click();

      startWaiting(m_toolbarActionButton);

      m_toolbarActionButton = "";
    }

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/toolbar.jsp#2 $$Change: 651448 $ --%>
