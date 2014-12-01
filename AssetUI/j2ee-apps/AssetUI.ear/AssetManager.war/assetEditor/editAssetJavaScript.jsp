<%--
  The javascript for editAsset.jsp.  It includes jsp so it can't be a .js file.

  The following request-scoped variables are expected to be set:

  @param  multiEditMode True if during the multi edit phase of a multi edit operation
  @param  multiEditOperation name of the multi edit operation

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAssetJavaScript.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"           %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"            %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramAssetEditorPath"     param="assetEditorPath"/>
  <dspel:getvalueof var="paramEditable"            param="editable"/>
  <dspel:getvalueof var="paramFormHandlerPath"     param="formHandlerPath"/>
  <dspel:getvalueof var="paramSelectedTreeNodeURI" param="selectedTreeNodeURI"/>
  <dspel:getvalueof var="paramTransient"           param="transient"/>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="formHandler" bean="${paramFormHandlerPath}"/>
  <dspel:importbean var="assetEditor" bean="${paramAssetEditorPath}"/>

  <script type="text/javascript">

    parent.atg.assetmanager.saveconfirm.initialize(<c:out value="${paramTransient == 'true'}"/>,<c:out value="${paramEditable =='false'}"/>,<c:out value="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_APPLY_TO_ALL}"/>,<c:out value="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_LIST}"/>);


    registerOnModified(function () {
      var previewButton = dojo.byId("previewButton");
      if (previewButton) {
        dojo.disconnect(previewButtonConnection);
        previewButton.onclick = function() { return false; };
        previewButton.title = "Save the asset before previewing";
        previewButton.className = "button disabled";
      }
      
      var previewAsButton = dojo.byId("previewAsButton");
      if (previewAsButton) {
        dojo.disconnect(previewAsButtonConnection);
        previewAsButton.onclick = function() { return false; };
        previewAsButton.title = "Save the asset before previewing";
        previewAsButton.className = "button disabled";
      }

      var reviewChangesButton = document.getElementById("reviewChangesButton");
      if (reviewChangesButton != null ) {
        reviewChangesButton.onclick = function() { return false; };
        reviewChangesButton.title = "Save the asset before reviewing changes";
        reviewChangesButton.className = "button disabled";
      }
    });

    function submitForm() {
      parent.messages.hide();
      fireOnSubmit();

      var form = getForm();
      form.submit();
    }

    function getForm() {
      return document.getElementById("assetForm");
    }

    function handleSelection(assetURI) {
      <c:choose>
        <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_APPLY_TO_ALL}">
         var form = getForm();
         form.action = "editAsset.jsp?assetURI=" + assetURI + "&_DARGS=/AssetManager/assetEditor/editAsset.jsp.viewerForm";
         submitForm();
         return true;
        </c:when>
        <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_LIST}">
          var jumpto = "anchor_" + assetURI;
          document.location.hash = jumpto;
          return true;
        </c:when>
        <c:when test="${multiEditMode == MODE_WAIT}">
          return true;
        </c:when>
        <c:otherwise>
          parent.messages.hide();
          return false;
        </c:otherwise>
      </c:choose>
    }

    function cancelCreate() {
      // when cancelling a linked property create,
      //  pop the breadcrumb and don't create/save
      // otherwise load a blank page
      <c:choose>
        <c:when test="${not empty assetEditor.assetContext.parentLinkPropertyName}">
          popContextBreadcrumb(true);
        </c:when>
        <c:when test="${not empty paramSelectedTreeNodeURI}">
            // if tree, show selected node
            propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp?assetURI=<c:out value='${paramSelectedTreeNodeURI}'/>";
            document.location = propPage;
        </c:when>
        <c:otherwise>
            var selectList = false;
            if (parent.displaySelectedListAssetRightPane) {
              selectList = parent.displaySelectedListAssetRightPane();
            }
            if (!selectList) {
              // display blank page
              var propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp?clearContext=true";
              document.location = propPage;
            }
        </c:otherwise>
      </c:choose>
    }

    function popContextBreadcrumb(popOnceNoSave) {

      if (popOnceNoSave != null)
        numPops = 1;
      else {
        // get the number of pops from the breadcrumb select menu
        var bcrumb = document.getElementById("contextbcrumb");
        if (bcrumb != null)
          numPops = bcrumb.options[bcrumb.selectedIndex].value;
      }
      // pop the context(s)
      // set form action to ASSETURI, POPContext=numPops
      var propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp" +
                     "?popContext=" + numPops;


      // force a save of the current asset, then go to breadcrumb asset
      if (popOnceNoSave != null)
        document.location = propPage;
      else parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(propPage);
    }


     function clickCancelButton() {
       document.getElementById('cancelButton').click();
     }


    function lockRightHandSide() {
      var buttonBar = document.getElementById("rightPaneFooter");
      if (buttonBar) { 
        buttonBar.addEventListener('click', function (e) {
          e.preventDefault();
        },false);
      }
    }

    function unlockRightHandSide() {
      var buttonBar = document.getElementById("rightPaneFooter");
      if (buttonBar) { 
        
      }
    }

    function actionButtonClicked(buttonLabel) {

      if (parent.actionsAreLocked()) return;

      var actionbutton = document.getElementById(buttonLabel);
      if (!actionbutton) return;

      parent.messages.hide();

      <web-ui:isAssignableFrom var="isTreeForm" nucleusPath="${paramFormHandlerPath}"
                               instanceOfClassName="atg.web.assetmanager.editor.TreeAssetRepositoryForm"/>
      <c:if test="${isTreeForm}">

        // if doing a create and there is no parent, warn user.
        var noParentLabel = document.getElementById("noParentLabel");
        if (noParentLabel != null) {
          <c:choose>
            <c:when test="${formHandler.parentRequired}">
              parent.messages.addError("<fmt:message key='assetEditor.createParentRequired'/>");
              return;
            </c:when>
            <c:otherwise>
              <c:if test="${!formHandler.treeDefinition.useRootNodeAsDefaultParent}">
                if (!confirm("<fmt:message key='assetEditor.createNoParent'/>"))
                  return;
              </c:if>
            </c:otherwise>
          </c:choose>
        }
      </c:if>

      fireOnSubmit();

      <c:if test="${multiEditMode != MODE_MULTI && multiEditOperation != OPERATION_APPLY_TO_ALL}">
         parent.startWait(false, true, true);
      </c:if>

      actionbutton.click();

      return;

    }

    function saveAndClose() {
      parent.atg.assetmanager.saveconfirm.mFinishSaveConfirmFunctionName = "popContextBreadcrumb";
      parent.atg.assetmanager.saveconfirm.mRightURL = null;
      parent.atg.assetmanager.saveconfirm.finishSaveBeforeLeaveAsset();
    }

    function saveNextPrevButtonClicked(nextPrevURL) {

     urlInput = document.getElementById("updateSuccessURLField");
     if (urlInput != null)
       urlInput.value = nextPrevURL;
     urlInput = document.getElementById("updateNoChangeURLField");
     if (urlInput != null)
       urlInput.value = nextPrevURL;
     actionButtonClicked("saveButton");

    }

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAssetJavaScript.jsp#2 $$Change: 651448 $--%>
