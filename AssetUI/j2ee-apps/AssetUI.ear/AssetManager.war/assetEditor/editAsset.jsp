<%--
  Asset editor container panel for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAsset.jsp#3 $$Change: 654746 $
  @updated $DateTime: 2011/06/27 09:01:12 $$Author: ikalachy $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="c_rt"     uri="http://java.sun.com/jstl/core_rt"                 %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>

  <c:set var="debug" value="false"/>

  <%-- Get the current left tab --%>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>

  <%-- Get the assetEditor for the current left tab --%>
  <c:if test="${not empty currentView}">
    <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
    <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
  </c:if>

  <%-- if multiEditTab, set multi edit mode and operation --%>
  <c:set scope="request" var="atgIsMultiEditView" value="${false}"/>
  <c:if test="${currentView eq 'multiEdit'}">
    <c:set scope="request" var="atgIsMultiEditView" value="${multiEditSessionInfo.multiEditMode ne MODE_SINGLE}"/>
    <c:set var="multiEditTab" value="true"/>
    <c:set var="multiEditMode" value="${multiEditSessionInfo.multiEditMode}" scope="request"/>
    <c:set var="multiEditOperation" value="${multiEditSessionInfo.multiEditOperation}" scope="request"/>
    <c:set var="multiEditPropertyGroupIndex" value="${multiEditSessionInfo.multiEditPropertyGroupIndex}" scope="request"/>
  </c:if>

  <dspel:importbean var="previewContext" bean="/atg/userprofiling/preview/PreviewContext"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title></title>

      <dspel:include page="/components/head.jsp"/>

      <dspel:include page="/scripts/applyToAllMenu.jsp"/>
      <dspel:include page="/scripts/derivedPropertyMenu.jsp"/>

      <script type="text/javascript"
              src="<c:out value='${config.contextRoot}'/>/scripts/collectionEditor.js">
      </script>

      <c:if test="${not empty previewContext}">
        <script type="text/javascript"
                src="<c:out value='${config.webuiRoot}'/>/dijit/previewLauncher/previewPicker.js">
        </script>

        <dspel:link href="${config.webuiRoot}/dijit/previewLauncher/templates/previewPicker.css"
                    rel="stylesheet"
                    type="text/css"
                    media="all"/>
      </c:if>

      <script type="text/javascript">
        // Register our event handlers with the Dojo framework.
        dojo.connect(window, "onload", handleOnLoad);
        dojo.connect(window, "onresize", handleOnResize);

        // A variable indicating if the window has loaded.
        var assetEditorLoaded = false;

        // The preview picker dialog.
        var dlgProg = null;

        handlePreLoad();

        // Handle the window onload event.
        function handleOnLoad() {
          parent.stopWait();
          parent.hideRightLoadingIcon();
          //parent.hideLeftLoadingIcon();
          fireOnLoad();
          layoutAssetEditor();
          assetEditorLoaded = true;
          handlePostLoad();
          <c:choose>
            <c:when test="${param.keepContext == 'true' && assetEditor.assetContext.dirty}">
              setModifiedFlag(true);
            </c:when>
            <c:otherwise>
              setModifiedFlag(false);
            </c:otherwise>
          </c:choose>

          // these parameters were passed in after the save confirm dialog
          <c:if test="${param.finishSaveConfirmFunction != null}">
            if (parent.<c:out value="${param.finishSaveConfirmFunction}"/>)
              parent.<c:out value="${param.finishSaveConfirmFunction}"/>('<c:out value="${param.finishSaveConfirmParam}"/>');
            else <c:out value="${param.finishSaveConfirmFunction}"/>('<c:out value="${param.finishSaveConfirmParam}"/>');
          </c:if>
          <c:if test="${param.setParentURL != null}">
            var theURL = unescape("<c:out value='${param.setParentURL}'/>");
            theURL = theURL.replace(/&amp;/g, "&");
            parent.document.location = theURL;
          </c:if>

          // We do not want to refresh the left pane during a step operation
          // because we don't want to reorder items. (124385)
          <dspel:importbean var="refresh" bean="/atg/web/assetmanager/RefreshComponent"/>
          <c:choose>
            <c:when test="${refresh.refreshFlag && multiEditMode != MODE_MULTI && multiEditOperation != OPERATION_STEP && multiEditMode != MODE_CONFIRM}">
            if (parent.refresh) {
              parent.refresh();
            }
            </c:when>
            <c:otherwise>
              parent.hideLeftLoadingIcon();
            </c:otherwise>
          </c:choose>

          <c:if test="${not empty previewContext}">
            <c:set target="${previewContext}" property="previewURL" value="${null}"/>
            <c:set target="${previewContext}" property="previewUserId" value="${null}"/>
            <c:set target="${previewContext}" property="previewSiteId" value="${null}"/>

            // preview setup
            dojo.require("dijit._Widget");
            dojo.require("dijit._Templated");

            dlgProg = new parent.atg.widget.previewPicker({
                            toggle:"fade",
                            toggleDuration:250,
                            pickerForm:dojo.byId('previewLauncher')
                            }, dojo.byId("previewContent"));

            // only connect the dojo elements if there is an asset on the page, otherwise clicking anywhere in
            // the frame will display the preview dialog
            registerPreviewHandlers();

            // executing this will force the preview buttons to be displayed if there is a user and url data
            checkForPreviewUrls();

            dojo.addOnUnload(function(){
              // Cleaning up after ourselves
              if(typeof dlgProg != "undefined"){
                dlgProg.destroy();
              }
            });


          </c:if>
        }

        // Handle the window onresize event.
        function handleOnResize() {
          layoutAssetEditor();
        }

      </script>

    </head>

    <body id="framePage" class="<c:out value='${config.dojoThemeClass}'/>">

      <%-- Get the configuration component for the current task --%>
      <c:set var="managerConfig" value="${sessionInfo.taskConfiguration}"/>

      <pws:getCurrentProject var="projectContext"/>

      <c:set var="project" value="${projectContext.project}"/>

      <%-- edit or view mode, depending on project status --%>
      <c:choose>
        <c:when test="${empty project or project.editable}">
          <c:set var="mode" value="AssetManager.edit"/>
          <c:set var="editable" value="true"/>
        </c:when>
        <c:otherwise>
          <c:set var="mode" value="AssetManager.view"/>
          <c:set var="editable" value="false"/>
        </c:otherwise>
      </c:choose>

      <%-- initialize the atgCurrentAssetURI variable for this request --%>
      <c:set scope="request" var="atgCurrentAssetURI" value=""/>

      <%-- the assetURI may have been passed in as a parameter --%>
      <dspel:getvalueof var="paramAssetURI" param="assetURI"/>

<%--
      <c:if test="${debug}">
        [editAsset] paramAssetURI = <c:out value="${paramAssetURI}"/><br />
      </c:if>
--%>

      <%--
           Push/Pop/Clear Breadcrumb Context Stack
       --%>

      <c:if test="${ not empty param.pushContext }">
        <c:set target="${assetEditor.assetContext}" property="transientAsset" value=""/>
        <c:set target="${assetEditor.assetContext}" property="transientAssetFormHandlerPath" value="${null}"/>
        <c:if test="${not empty param.currentListIndex}">
          <c:set target="${assetEditor.assetContext}" property="currentListIndex" value="${param.currentListIndex}"/>
          <c:set target="${assetEditor.assetContext}" property="currentListIndexPropertyName" value="${param.currentListIndexPropertyName}"/>
        </c:if>
        <c:set target="${assetEditor}" property="pushContext" value="true"/>
      </c:if>

      <c:if test="${ not empty param.popContext }">
        <c:if test="${not empty assetEditor.assetContext.listInsertIndex}">
          <c:set scope="request" var="listInsertIndex" value="${assetEditor.assetContext.listInsertIndex}"/>
          <c:set target="${assetEditor.assetContext}" property="listInsertIndex" value=""/>
          <c:set scope="request" var="listInsertPropertyName" value="${assetEditor.assetContext.listInsertPropertyName}"/>
          <c:set target="${assetEditor.assetContext}" property="listInsertPropertyName" value=""/>
        </c:if>
        <c:set target="${assetEditor}" property="popContext" value="${param.popContext}"/>
        <c:if test="${not empty assetEditor.assetContext.currentListIndex}">
          <c:set scope="request" var="currentListIndex" value="${assetEditor.assetContext.currentListIndex}"/>
          <c:set target="${assetEditor.assetContext}" property="currentListIndex" value=""/>
          <c:set scope="request" var="currentListIndexPropertyName" value="${assetEditor.assetContext.currentListIndexPropertyName}"/>
          <c:set target="${assetEditor.assetContext}" property="currentListIndexPropertyName" value=""/>
        </c:if>
      </c:if>

      <c:if test="${ 'true' == param.clearContext }">
        <c:set target="${assetEditor}" property="clearContext" value="true"/>
      </c:if>


      <c:set var="transient" value="false"/>

      <%--
            Set 'atgCurrentAssetURI'

        If the assetURI parameter is set, use this first.  This will be the case
        when selecting an item from the tree, clicking on a linked asset,
        or after updating/saving the page

        If the parameter isn't set, get the assetURI out of the context stack. This
        will be the case when navigating here from the left hand tab or using the breadcrumb.

        If no parameter or context stack, try the context's transient item. This will
        happen after creating an item from the tree or linked property
      --%>

      <c:set var="assetURIFromContext" value="false"/>
      <c:choose>
        <%-- Tree or Link or Right Prop Tab or Save --%>
        <c:when test="${ ! empty paramAssetURI}">
          <c:if test="${ paramAssetURI != 'null'}">
            <c:set scope="request" var="atgCurrentAssetURI" value="${paramAssetURI}"/>
          </c:if>
<%--
          <c:if test="${debug}">
            [editAsset] TREE or LINK or PROPTAB or SAVE use param: <c:out value="${requestScope.atgCurrentAssetURI}"/><br />
          </c:if>
--%>
        </c:when>
        <%-- Post Create --%>
        <c:when test="${ ! empty param.assetURIPath}">
          <dspel:getvalueof scope="request" var="atgCurrentAssetURI" bean="${param.assetURIPath}"/>
        </c:when>
        <%-- Create  --%>
        <c:when test="${not empty assetEditor.assetContext.transientAsset}">
          <c:set var="thisAsset" value="${assetEditor.assetContext.transientAsset}"/>
          <c:set var="item" value="${thisAsset.asset}"/>
          <c:set var="newAssetURI" value="${thisAsset.uri}"/>
          <c:set var="parentAssetURI" value="${assetEditor.assetContext.parentAssetURI}"/>
          <c:set var="transient" value="true"/>
<%--
          <c:if test="${debug}">
            [editAsset] CREATE use transient item: <c:out value="${newAssetURI}"/><br/>
          </c:if>
--%>
        </c:when>
        <%-- Breadcrumb or left tab--%>
        <c:when test="${not empty assetEditor.assetContext.assetURI}">
          <c:set var="assetURIFromContext" value="true"/>
          <c:set scope="request" var="atgCurrentAssetURI" value="${assetEditor.assetContext.assetURI}"/>
<%--
          <c:if test="${debug}">
            [editAsset] BREADCRUMB OR LEFT TAB use context: <c:out value="${requestScope.atgCurrentAssetURI}"/><br />
          </c:if>
--%>
        </c:when>
        <%-- Blank Page --%>
        <c:otherwise>
<%--
          <c:if test="${debug}">
            [editAsset] no assetURI or transient item <br/>
          </c:if>
--%>
        </c:otherwise>
      </c:choose>

      <c:if test="${not empty previewContext}">
        <%-- need this javascript here after the mode and assetURI has been determined --%>
        <script type="text/javascript" charset="utf-8">
          var previewButtonConnection = null;
          var previewAsButtonConnection = null;

          function registerPreviewHandlers() {
            <c:if test="${not empty requestScope.atgCurrentAssetURI}">
              var previewAsButton = dojo.byId("previewAsButton");
              var previewButton = dojo.byId("previewButton");
              if (previewAsButton && previewButton) {
                previewAsButtonConnection = dojo.connect(previewAsButton, "onclick", function(e){
                  // work around an issue where sometimes the button is disabled, but still clickable.
                  // possibly a dojo issue
                  if (previewAsButton.className == "button disabled")
                    return;
                  e.preventDefault();
                  initiateUsersAndUrlRequests();
                  dlgProg.show();
                });
                previewButtonConnection = dojo.connect(previewButton, "onclick", function(e){
                  // work around an issue where sometimes the button is disabled, but still clickable.
                  // possibly a dojo issue
                  if (previewButton.className == "button disabled")
                    return;
                  e.preventDefault();
                  dlgProg.serverLaunchCheck();
                });
              }
            </c:if>
          }

          function initiateUsersAndUrlRequests() {
            <c:if test="${not empty requestScope.atgCurrentAssetURI}">
              if (dlgProg.users == null)
                dlgProg.getPreviewUsers();
              if (dlgProg.sites == null)
                dlgProg.getPreviewSites();
              if (dlgProg.urls == null)
                dlgProg.getPreviewURLs('<c:out value="${mode}"/>', "AssetManager.view", "AssetManager",
                                        escape('<c:out value="${atgCurrentAssetURI}"/>'));
            </c:if>
          }
        </script>
      </c:if>

      <%--
          Set 'item'

          Get the repository item for the assetURI.
          If there is a transient item, this var was set in assetURI section above.
       --%>
      <c:if test="${ ! empty requestScope.atgCurrentAssetURI }">
        <%-- amitj: Fix for secured assets --%>
        <c:catch var="exception">
          <asset-ui:resolveAsset var="thisAsset" uri="${requestScope.atgCurrentAssetURI}"/>
        </c:catch>

        <%-- if this is create then the item was already set to the transient item --%>
        <c:if test="${empty item}">
          <c:set var="item" value="${thisAsset.asset}"/>
        </c:if>

        <%-- Is this a versioned item? --%>
        <c:set var="isVersioned" value="false"/>
        <c:if test="${not empty item}">
          <web-ui:isAssignableFrom var="isVersioned"
             className="${item.repository.class.name}"
             instanceOfClassName="atg.adapter.version.RepositoryVersionContainer"/>
        </c:if>

        <%-- <c:set var="fileitem" value="${thisAsset.virtualFile}"/> --%>
        <c:choose>
          <c:when test="${assetURIFromContext}">
            <%-- This situation means that we were previously viewing this item but it was
                 deleted out from under us.  No error should display in this situation. --%>
          </c:when>
          <c:when test="${not empty exception}">
            <web-ui:getSecurityExceptionMessage var="errorMsg" exception="${exception}"/>
          </c:when>
          <c:when test="${empty thisAsset}">
            <fmt:message var="errorMsg" key="assetEditor.unresolvedItem.error">
              <fmt:param value="${requestScope.atgCurrentAssetURI}"/>
            </fmt:message>
          </c:when>
        </c:choose>
        <c:if test="${not empty errorMsg}">
          <script type="text/javascript">
            parent.messages.addError("<c:out value='${errorMsg}'/>");
          </script>
        </c:if>
      </c:if>

      <c:choose>
        <c:when test="${not empty thisAsset}">
          <c:set var="itemDescriptorName" value="${thisAsset.type}"/>
          <c:set var="repositoryPath" value="${thisAsset.containerPath}"/>
          <%-- The getAssetAccess tag does not always get the secured item out
               of the repository.  In that case, it sets all of the permissions to false. --%>
          <c:if test="${not transient}">
            <%--
               get the ACL for the item
             --%>
            <asset-ui:getAssetAccess item="${item}" var="itemAccess"/>
            <c:if test="${! itemAccess.write}">
              <c:set var="mode" value="AssetManager.view"/>
              <c:set var="editable" value="false"/>
            </c:if>
            <%--
              if user lacks read access, pretend we weren't able to retrieve the item
            --%>
            <c:if test="${! itemAccess.read}">
              <c:set var="readable" value="false"/>
              <c:set var="thisAsset" value="${null}"/>
              <fmt:message var="itemAccess_read_errorMsg" key="assetEditor.unresolvedItem.error">
                <fmt:param value="${requestScope.atgCurrentAssetURI}"/>
              </fmt:message>
              <script type="text/javascript">
                parent.messages.addError("<c:out value='${itemAccess_read_errorMsg}'/>");
              </script>
            </c:if>
          </c:if>

        </c:when>
       <c:when test="${multiEditMode == MODE_MULTI}">
          <c:set var="thisAsset" value="${multiEditSessionInfo.firstAsset}"/>
          <c:set var="itemDescriptorName" value="${thisAsset.type}"/>
          <c:set var="repositoryPath" value="${thisAsset.containerPath}"/>
        </c:when>
      </c:choose>

      <c:if test="${not empty previewContext}">
        <script type="text/javascript" charset="utf-8">
          function checkForPreviewUrls() {
            <c:if test="${not empty requestScope.atgCurrentAssetURI}">
              dlgProg.checkForPreviewURLs('<c:out value="${mode}"/>', 'AssetManager.view', "AssetManager",
                        escape('<c:out value="${atgCurrentAssetURI}"/>'));
            </c:if>
          }
        </script>
      </c:if>

      <%--
          Set 'parentItem'

          Get the repository item for the parent assetURI (if there is one)
       --%>

      <c:set var="parentItem" value=""/>
      <c:if test="${ ! empty parentAssetURI}">
        <asset-ui:resolveAsset var="parentAsset" uri="${parentAssetURI}"/>
        <c:set var="parentItem" value="${parentAsset.asset}"/>
      </c:if>

      <%-- Display any asset-creation errors --%>
      <c:if test="${not empty assetEditor.assetContext.transientAssetFormHandlerPath}">
        <dspel:importbean var="createFormHandler"
                          bean="${assetEditor.assetContext.transientAssetFormHandlerPath}"/>
        <script type="text/javascript">
          <c:if test="${not empty createFormHandler.formExceptions}">
            <c:forEach items="${createFormHandler.formExceptions}" var="exc">
              parent.messages.addError("<c:out value='${exc.message}'/>");
            </c:forEach>
          </c:if>
        </script>
      </c:if>

      <c:choose>

        <c:when test="${ empty thisAsset && multiEditMode != MODE_MULTI}"> <%-- blank page --%>
          <div id="rightPaneHeader">
          </div>
          <c:if test="${not empty fileitem}">
            <p><center><fmt:message key="assetEditor.fileAssetMessage"/></center></p>
          </c:if>
<%--
          <c:if test="${debug}"> EMPTY item <br></c:if>
--%>
        </c:when>

        <c:otherwise> <%-- not a blank page --%>

          <%-- loading icon --%>
          <div id="screen"></div>


          <%--
             Initialize the assetContext
          --%>

          <c:set target="${assetEditor.assetContext}" property="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
          <c:if test="${not empty thisAsset}">
            <c:set target="${assetEditor.assetContext}" property="displayName" value="${thisAsset.displayName}"/>
          </c:if>
          <c:if test="${not transient}">
            <c:set target="${assetEditor.assetContext}" property="transientAsset" value=""/>
            <c:set target="${assetEditor.assetContext}" property="transientAssetFormHandlerPath" value="${null}"/>
            <c:set target="${assetEditor.assetContext}" property="parentAssetURI" value=""/>
            <c:set target="${assetEditor.assetContext}" property="parentLinkPropertyName" value=""/>
            <c:set target="${assetEditor.assetContext}" property="parentLinkPropertyKey" value=""/>
            <c:set target="${assetEditor.assetContext}" property="parentLinkPropertyIndex" value=""/>
          </c:if>

          <%-- See if we are drilled down into a versioned asset from a non-versioned
               one.  If so, the asset must be displayed as read-only. --%>
          <c:if test="${project == null && assetEditor.drilledFromUnversionedToVersioned}">
            <c:set var="mode" value="AssetManager.view"/>
            <c:set var="editable" value="false"/>
          </c:if>

          <%--
               Item Mapping
           --%>

          <asset-ui:getItemMappingInfo itemDescriptorName="${itemDescriptorName}"
                                       repositoryPath="${repositoryPath}"
                                       mode="${mode}"
                                       taskConfig="${managerConfig}"
                                       var="imapInfo"/>

          <c:catch var="exception">
            <biz:getItemMapping itemPath="${repositoryPath}"
                                itemName="${itemDescriptorName}"
                                var="imap"
                                mode="${imapInfo.mode}"
                                readOnlyMode="AssetManager.view"
                                showExpert="true"
                                displayId="true"
                                transient="${transient}"
                                mappingName="${imapInfo.name}"/>

            <c:if test="${item != null && not transient}">
              <%-- Check whether the asset is secured or not.--%>
              <asset-ui:isAssetSecured var="isSecured" item="${item}"/>
              <c:set var="checkSecured" value="${isSecured}"/>

              <%-- Get the access rights for the current asset--%>
              <asset-ui:getAssetAccess item="${item}" var="itemAccess"/>

              <%-- If the asset is secured and the user has got read_acl rights on that asset,
                then creates item view mapping for the security tab and add it to the existing ones. --%>
              <c:if test="${checkSecured && itemAccess.readAcl && empty imap.attributes.omitSecurityTab}">
               <asset-ui:getSecuredViewMapping var="imap" mappedItem="${imap}" />
              </c:if>
            </c:if>

            <%-- Filter out all unused tabs --%>
            <web-ui:invoke var="imap"
                           componentPath="/atg/web/assetmanager/editor/FilteredItemMappingFactory"
                           method="getFilteredItemMapping">
              <web-ui:parameter value="${imap}"/>
            </web-ui:invoke>

          </c:catch>
          <c:if test="${not empty exception}">
            <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
          </c:if>

<%--
          <c:if test="${debug && empty imap}">
            [editAsset]return empty imap <c:out value="${itemDescriptorName} ${repositoryPath}"/>
          </c:if>
--%>

          <%--
               Set ".value"

               If the form handler defines a value dictionary, set its name in the mapping.
               This is used to get bean names for property editor input fields.
            --%>
          <c:if test="${ ! empty imap.formHandler.attributes.atgFormValueDict }">
            <c:set target="${imap}" property="valueDictionaryName"
                   value="${imap.formHandler.attributes.atgFormValueDict.value}"/>
          </c:if>

          <%--
               Set 'formHandlerPath' and import the bean.
               The form handler is taken from the standard viewmappings, not the property group mappings.
               Therefore must fetch and import bean here before fetching the property group mappings.
          --%>

          <%-- Import form handler specified in mapping --%>
          <c:set var="formHandlerPath" value="${imap.formHandler.path}" scope="request"/>
          <dspel:importbean var="formHandler" bean="${formHandlerPath}" scope="request"/>

<%--
          <c:if test="${debug}">
            [editAsset] formHandlerPath <c:out value="${formHandlerPath}" /><br/> imap.description: <c:out value="${imap.description}"/><br/>
          </c:if>
--%>

          <%--
               Multi Edit Item Mapping
          --%>

          <c:if test="${(multiEditMode == MODE_MULTI || multiEditMode == MODE_CONFIRM) && multiEditPropertyGroupIndex != -1}">
            <asset-ui:getItemMappingInfo itemDescriptorName="${itemDescriptorName}"
                                         repositoryPath="${repositoryPath}"
                                         mode="AssetManager.multiEdit"
                                         taskConfig="${managerConfig}"
                                         var="multiEditImapInfo"/>

            <c:catch var="exception">
              <biz:getItemMapping itemPath="${repositoryPath}"
                                  itemName="${itemDescriptorName}"
                                  var="multiEditImap"
                                  mode="${multiEditImapInfo.mode}"
                                  readOnlyMode="AssetManager.view"
                                  showExpert="true"
                                  displayId="true"
                                  mappingName="${multiEditImapInfo.name}"/>
            </c:catch>
            <c:if test="${not empty exception}">
              <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
            </c:if>

<%--
            <c:if test="${debug && empty imap}">
              [editAsset]return empty multi-edit imap <c:out value="${itemDescriptorName} ${repositoryPath}"/>
            </c:if>
--%>

            <%-- We want to get the current selected property group from the list of
                 property groups for the type.  We will be getting it by index, so,
                 first we have to sort the list in the same order that the picker was sorted.

                 NOTE: If you change this sorting, please make the same change to the sorting
                 in dropDownIframe.jsp
             --%>
            <c:if test="${not empty multiEditImap.viewMappings}">
              <dspel:sort var="sorter" values="${multiEditImap.viewMappings}">
                <dspel:orderBy property="displayName"/>
              </dspel:sort>
              <c:set var="propertyGroups" value="${sorter.sortedArray}"/>
            </c:if>
            <c:set var="currentPropertyGroup" value="${propertyGroups[multiEditPropertyGroupIndex]}"/>
            <c:set var="view" value="${currentPropertyGroup}" scope="request"/>

            <c:if test="${ ! empty multiEditImap.formHandler.attributes.atgFormValueDict }">
              <c:set target="${multiEditImap}" property="valueDictionaryName"
                     value="${imap.formHandler.attributes.atgFormValueDict.value}"/>
            </c:if>

          </c:if>

          <c:if test="${not empty imap}">

            <%-- Turn off editability if the item mapping is read-only --%>
            <c:if test="${isVersioned && imap.mode == 'AssetManager.view'}">
              <c:set var="editable" value="false"/>
            </c:if>

            <%--
                 Create form URLs
             --%>

            <c:url var="actionURL" value="editAsset.jsp">
              <c:if test="${not empty requestScope.atgCurrentAssetURI}">
                <c:param name="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
              </c:if>
              <c:param name="treeComponent" value="${param.treeComponent}"/>
              <c:param name="keepContext" value="true"/>
            </c:url>

            <c:url var="addAssetURL" value="editAsset.jsp">
              <c:param name="assetURIPath" value="${formHandlerPath}.assetURI"/>
            </c:url>

            <c:set var="cancelSubTypeURL" value="${addAssetURL}" scope="request"/>

            <c:url var="editAssetURL" value="editAsset.jsp">
              <c:param name="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
            </c:url>

            <%--
                BEGIN assetForm
            --%>
            <dspel:form enctype="multipart/form-data" action="${actionURL}" method="post"
                        id="assetForm" formid="viewerForm">

              <%--
                   Input assetContext.assetURI (and parentAssetURI)
              --%>
              <c:choose>
                <c:when test="${not empty newAssetURI}">
                  <dspel:input type="hidden" bean="${assetEditorPath}.assetContext.assetURI"
                               value="${newAssetURI}" />
                </c:when>
                <c:otherwise>
                  <dspel:input type="hidden" bean="${assetEditorPath}.assetContext.assetURI"
                               value="${requestScope.atgCurrentAssetURI}" />
                </c:otherwise>
              </c:choose>
              <dspel:input type="hidden" bean="${assetEditorPath}.assetContext.parentAssetURI"
                           value="${parentAssetURI}" id="parentAssetURIContext"/>

              <%--
                   Status Bar
              --%>

              <dspel:include page="/assetEditor/statusBar.jsp">
                <c:if test="${not empty thisAsset}">
                  <dspel:param name="asset" value="${thisAsset}"/>
                </c:if>
                <c:if test="${not empty parentItem}">
                  <dspel:param name="parentItemDisplayName" value="${parentItem.itemDisplayName}"/>
                </c:if>
                <dspel:param name="transient" value="${transient}"/>
                <c:choose>
                  <c:when test="${not empty param.treeComponent}">
                    <dspel:param name="treeComponent" value="${param.treeComponent}"/>
                  </c:when>
                  <c:otherwise>
                    <c:if test="${not empty assetEditor.assetContext.treeComponentPath}">
                      <dspel:param name="treeComponent" value="${assetEditor.assetContext.treeComponentPath}"/>
                    </c:if>
                  </c:otherwise>
                </c:choose>
                <dspel:param name="imap" value="${imap}"/>
                <dspel:param name="assetIndex" value="${param.assetIndex}"/>
              </dspel:include>

              <c:choose>
                <c:when test="${not empty assetEditor.assetContext.treeComponentPath}">
                  <c:set var="treeComponent" value="${assetEditor.assetContext.treeComponentPath}"/>
                </c:when>
              </c:choose>

              <%--
                   Property Tabs
                   This file sets a request scoped variable called "view"
               --%>

            <div id="assetEditorContainer">

            <div id="subNav">
              <c:if test="${(multiEditMode != MODE_MULTI && multiEditMode != MODE_CONFIRM) || multiEditPropertyGroupIndex == -1}">
                <dspel:include page="/assetEditor/propertyTabs.jsp">
                  <dspel:param name="resetViewNumber" value="${param.resetViewNumber}"/>
                  <dspel:param name="imap" value="${imap}"/>
                  <c:if test="${not empty param.popContext}">
                    <dspel:param name="overrideViewNumber" value="${assetEditor.assetContext.currentViewNumber}"/>
                  </c:if>
                </dspel:include>
              </c:if>
              </div>

              <%-- If we are in multi-edit mode, and there is a currently selected
                   property group, then replace the value of ${requestScope.view}
                   with the itemViewMapping for the property group. --%>
              <c:if test="${(multiEditMode eq MODE_MULTI || multiEditMode eq MODE_CONFIRM) && multiEditPropertyGroupIndex != -1}">
                <%-- Give a warning if the form handler in the multiedit property group doesn't
                     match the one used for this asset type in single edit.  --%>
                <c:set var="fh1" value="${multiEditImap.formHandler.path}"/>
                <c:set var="fh2" value="${requestScope.formHandlerPath}"/>
                <c:if test="${fh1 ne fh2}">
                  <%
                  Object loggingObj = pageContext.getAttribute("multiEditSessionInfo");
                  if (loggingObj != null && loggingObj instanceof atg.nucleus.GenericService) {
                    atg.nucleus.GenericService loggingService = (atg.nucleus.GenericService)loggingObj;
                    if (loggingService.isLoggingWarning())
                      loggingService.logWarning("The form handler path for the multiedit itemMapping does not match the form handler used for single edit: " + pageContext.getAttribute("fh2") + " " + pageContext.getAttribute("fh1")) ;
                  }
                  %>
                </c:if>
              </c:if>

              <!-- Right pane fade-out effect -->
              <%-- <div id="topFade"></div> Block this, since it makes the top of the page unclickable --%>




              <%-- Find out which interfaces the form handler implements --%>

              <web-ui:isAssignableFrom var="isSingleForm" nucleusPath="${formHandlerPath}"
                                       instanceOfClassName="atg.web.assetmanager.editor.SingleAssetRepositoryForm"/>
              <web-ui:isAssignableFrom var="isLinkForm" nucleusPath="${formHandlerPath}"
                                       instanceOfClassName="atg.web.assetmanager.editor.LinkAssetRepositoryForm"/>
              <web-ui:isAssignableFrom var="isTreeForm" nucleusPath="${formHandlerPath}"
                                       instanceOfClassName="atg.web.assetmanager.editor.TreeAssetRepositoryForm"/>
              <web-ui:isAssignableFrom var="isMsgForm" nucleusPath="${formHandlerPath}"
                                       instanceOfClassName="atg.web.assetmanager.action.SuccessMessageForm"/>
              <web-ui:isAssignableFrom var="isFileForm" nucleusPath="${formHandlerPath}"
                                       instanceOfClassName="atg.web.assetmanager.editor.FileAssetFormHandler"/>

              <%--
                   Display Form Messages.
               --%>

              <c:if test="${isMsgForm}">
                <c:if test="${not empty formHandler.successMessage}">
                  <script>
                    parent.messages.addAlert('<c:out value="${formHandler.successMessage}"/>');
                  </script>
                </c:if>
              </c:if>

             <c:if test="${not empty param.treeComponent}">
               <dspel:importbean var="treeState" bean="${param.treeComponent}"/>
               <c:set var="selectedTreeNodeURI" value="${treeState.selectedNode.URI}" scope="request"/>
             </c:if>

              <%--
                  Set 'formHandler' variables

                  Update form handler with the current asset information.
                  If this is only a property tab change, the keepContext parameter is set and
                  these updates aren't necessary (or desired becaues they cause the form to clear)
              --%>

              <c:if test="${ param.keepContext != 'true' }">

                <c:if test="${isSingleForm}">
                  <c:set target="${formHandler}" property="itemDescriptorName" value="${imap.itemName}"/>
                  <c:set target="${formHandler}" property="repositoryPathName" value="${imap.itemPath}"/>
                </c:if>

                <c:choose>
                  <c:when test="${transient}">
                    <c:if test="${isSingleForm}">
                      <c:set target="${formHandler}" property="transientItem" value="${item}"/>
                    </c:if>
                    <c:if test="${not empty parentItem && isLinkForm}">
                      <c:set target="${formHandler}" property="parentAssetURI" value="${parentAssetURI}"/>
                      <c:set target="${formHandler}" property="parentRepositoryPathName" value="${parentItem.repository.absoluteName}"/>
                      <c:set target="${formHandler}" property="parentLinkPropertyName"
                             value="${assetEditor.assetContext.parentLinkPropertyName}"/>
                      <c:set target="${formHandler}" property="parentLinkPropertyKey"
                             value="${assetEditor.assetContext.parentLinkPropertyKey}"/>
                      <c:set target="${formHandler}" property="parentLinkPropertyIndex"
                             value="${assetEditor.assetContext.parentLinkPropertyIndex}"/>
                      <c:set target="${formHandler}" property="assetContext"
                             value="${assetEditor.assetContext}"/>
                    </c:if>
                    <%-- If creating an asset, the form uses the tree to set links --%>
                    <c:if test="${not empty treeComponent && isTreeForm}">
                      <dspel:importbean var="treeState" bean="${treeComponent}"/>
                      <c:set target="${formHandler}" property="treeDefinition" value="${treeState.globalTree.treeDefinition}"/>
                    </c:if>
                  </c:when>
                  <c:when test="${isSingleForm}">
                    <c:set target="${formHandler}" property="repositoryId" value="${null}"/>
                    <c:if test="${not empty item}">
                      <c:set target="${formHandler}" property="repositoryId" value="${item.repositoryId}"/>
                    </c:if>
                  </c:when>
                </c:choose>
              </c:if>


              <%-- if this is multiedit confirm mode, then we need to prepopulate the error values --%>
              <c:if test="${multiEditMode == MODE_CONFIRM}">
                <dspel:importbean var="confirmFormHandler" bean="/atg/web/assetmanager/multiedit/MultiEditConfirmFormHandler"/>
                <c:set target="${confirmFormHandler}" property="singleFormHandler" value="${formHandler}"/>
                <c:set target="${confirmFormHandler}" property="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
                <c:set var="dummy" value="${confirmFormHandler.initErrorValues}"/>
              </c:if>

              <%--
                   Input formHandler variables
               --%>

              <div><%-- hidden inputs should be in a div, otherwise extra space causes bottom buttons to dissapear--%>

              <c:if test="${isLinkForm}">
                <dspel:input type="hidden" bean="${formHandlerPath}.parentAssetURI"
                             value="${parentAssetURI}" id="parentAssetURIInput"/>
              </c:if>

              <c:if test="${isSingleForm}">
                  <dspel:input type="hidden" bean="${formHandlerPath}.updateSuccessURL"
                               value="${editAssetURL}" id="updateSuccessURLField"/>
                  <dspel:input type="hidden" bean="${formHandlerPath}.addSuccessURL"
                               value="${addAssetURL}" id="addSuccessURLField"/>
                  <dspel:input type="hidden" bean="${formHandlerPath}.cancelURL"
                               value="${editAssetURL}"/>

                  <%--  Hidden submit buttons which are clicked from javascript below  --%>
                  <dspel:input type="submit"  style="display:none" id="tabChangeButton"
                               bean="${formHandlerPath}.tabChange" value="TabChange"/>
                  <dspel:input type="submit"  style="display:none" id="saveButton"
                               bean="${formHandlerPath}.update" value="Save"/>
                  <dspel:input type="submit"  style="display:none" id="addButton"
                               bean="${formHandlerPath}.add" value="Add"/>
                  <dspel:input type="submit"  style="display:none" id="cancelButton"
                               bean="${formHandlerPath}.cancel" value="Revert"/>
              </c:if>

              </div>

              <%--
                  Property Sheet
              --%>

              <!-- Right Pane Properties Content : Includes right pane properties content -->
              <%-- <div id="rightPaneContent" style="display:none"> --%>
              <div id="rightPaneContent" >

                <%-- list form handler errors --%>
                <script type="text/javascript">
                  <c:if test="${not empty formHandler.formExceptions}">
                    parent.messages.hide();
                    <c:forEach items="${formHandler.formExceptions}" var="ex">
                      <c:set var="message" value="${ex.message}"/>
                      <%-- Need to use the c_rt library if we want
                           to specify a newline character in a string literal --%>
                      <c_rt:forTokens var="messageLine" items='<%=(String) pageContext.getAttribute("message")%>' delims='<%="\r\n"%>'>
                        parent.messages.addError("<c:out value='${messageLine}'/>");
                      </c_rt:forTokens>
                    </c:forEach>
                  </c:if>
                </script>

                <%-- include the current tab's page --%>
                <c:set scope="request" var="atgCurrentAsset" value="${thisAsset}"/>
                <c:set scope="request" var="atgCurrentParentAsset" value="${parentAsset}"/>
                <c:set scope="request" var="atgIsAssetEditable" value="${editable}"/>
                <c:set scope="request" var="atgItemMapping" value="${imap}"/>
                <c:set scope="request" var="atgItemViewMapping" value="${requestScope.view}"/>
                <dspel:include page="/assetEditor/editAssetView.jsp">
                  <dspel:param name="imap" value="${imap}"/>
                </dspel:include>

              </div>


              <c:set var="hideButtonBar" value="false"/>
              <c:if test="${not empty imap.attributes.hideButtonBar}">
                 <c:set var="buttonBarStyle" value="display:none"/>
              </c:if>
              <%--
                  Bottom Button Bar
              --%>
              <table id="rightPaneScrollFooter" style="<c:out value='${buttonBarStyle}'/>">
                <tr>
                  <td id="rightPaneFooter">

                    <!-- Right Pane Footer Action Buttons -->
                    <div>
                      <div id="rightPaneFooterRight">
                        <c:choose>
                          <c:when test="${editable}">
                            <c:choose>
                              <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_STEP}">
                                <dspel:include page="/multiEdit/stepButtons.jsp"/>
                              </c:when>
                              <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_APPLY_TO_ALL}">
                                <dspel:include page="/multiEdit/applyToAllButtons.jsp">
                                  <dspel:param name="actionURL" value="${actionURL}"/>
                                </dspel:include>
                              </c:when>
                              <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_LIST}">
                                <dspel:include page="/multiEdit/listEditButtons.jsp">
                                  <dspel:param name="actionURL" value="${actionURL}"/>
                                </dspel:include>
                              </c:when>
                              <c:when test="${multiEditMode == MODE_CONFIRM}">
                                <dspel:include page="/multiEdit/confirmButtons.jsp">
                                  <dspel:param name="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
                                </dspel:include>
                              </c:when>
                              <c:otherwise>
                                <dspel:include page="/assetEditor/singleEditButtons.jsp">
                                  <dspel:param name="transient" value="${transient}"/>
                                </dspel:include>
                              </c:otherwise>
                            </c:choose>
                          </c:when>
                          <%-- Show 'Save' and 'Revert' buttons - if user does not have "Write" access but has
                               "Set Access Rights" and is in a project --%>
                          <c:when test="${!editable && item != null && itemAccess.readAcl && itemAccess.writeAcl && project != null}">
                            <dspel:include page="/assetEditor/singleEditButtons.jsp">
                              <dspel:param name="transient" value="${transient}"/>
                            </dspel:include>
                          </c:when>
                          <c:when test="${assetEditor.size > 1}">
                            <a href="javascript:popContextBreadcrumb(true)" id="closeButtonLink"
                               class="button"
                               title="<fmt:message key='assetEditor.close.title'/>"><span><fmt:message
                               key="assetEditor.close"/></span></a>
                          </c:when>
                        </c:choose>
                      </div>
                      <div id="rightPaneFooterLeft">
                        <dspel:input type="checkbox"  bean="${assetEditorPath}.assetContext.dirty" id="assetModifiedCB" style="display:none"/>
                        <c:choose>
                          <c:when test="${param.keepContext == 'true' && assetEditor.assetContext.dirty}" >
                            <fmt:message var="reviewChangesTitle" key='assetEditor.reviewChanges.dirty.title'/>
                            <fmt:message var="previewAsTitle" key='assetEditor.preview.dirty.title'/>
                            <fmt:message var="previewTitle" key='assetEditor.preview.dirty.title'/>
                            <c:set var="disableLink" value="onclick='return false'"/>
                           </c:when>
                          <c:otherwise>
                            <fmt:message var="reviewChangesTitle" key='assetEditor.reviewChanges.title'/>
                            <fmt:message var="previewAsTitle" key='assetEditor.previewAs.title'/>
                            <fmt:message var="previewTitle" key='assetEditor.preview.title'/>
                            <c:set var="disableLink" value=""/>
                          </c:otherwise>
                        </c:choose>

                        <c:if test="${not empty item}">
                          <%-- You can't review changes on a non-ver item --%>
                          <c:if  test="${isVersioned and
                                         not transient and
                                         not empty sessionInfo.currentProjectId and
                                         not (multiEditMode == MODE_MULTI && (multiEditOperation == OPERATION_APPLY_TO_ALL || multiEditOperation == OPERATION_LIST)) and
                                         not isFileForm}">
                            <c:url var="reviewChangesURL" value="/assetManager.jsp">
                              <c:param name="reviewChanges" value="1"/>
                              <c:param name="assetURI" value="${requestScope.atgCurrentAssetURI}"/>
                            </c:url>
                            <a href="<c:out value='${reviewChangesURL}'/>" target="_parent" id="reviewChangesButton" class="button" title="<c:out value='${reviewChangesTitle}'/>" <c:out value='${disableLink}' escapeXml="false"/> >
                              <span>
                                <fmt:message key="assetEditor.reviewChanges"/>
                              </span>
                            </a>
                          </c:if>
                        </c:if>

                        <c:if test="${not empty previewContext and 'true' ne imap.attributes.disablePreview.value}">
                          <%-- Preview button logic --%>
                          <a id="previewAsButton" class="button" href="" title="<c:out value='${previewAsTitle}'/>">
                            <fmt:message key='assetEditor.previewAs'/>
                          </a>
                          <a id="previewButton" class="button" href="" title="<c:out value='${previewTitle}'/>">
                            <fmt:message key='assetEditor.preview'/>
                          </a>
                        </c:if>

                        <c:if test="${not empty imap.attributes.additionalButtonFragment}">
                          <c:set var="buttonFrag" value="${imap.attributes.additionalButtonFragment.value}"/>
                          <c:set var="buttonCxt" value="${imap.attributes.additionalButtonFragmentContext.value}"/>
                          <dspel:include otherContext="${buttonCxt}" page="${buttonFrag}"/>
                        </c:if>

                      </div>
                    </div>

                  </td>
                </tr>
              </table>
            </dspel:form>
          </div>

            <c:if test="${not empty previewContext}">
              <%-- Preview form must be here because it cannot be embedded in main form --%>
              <form action="" method="POST" id="previewLauncher">
                <input type="hidden" name="userId" value="">
                <input type="hidden" name="url" value="">
                <input type="hidden" name="siteId" value="">
              </form>
            </c:if>

             <%--
                Include javascript

                Can't do javascript include because need jsp processing.
                Must be included here, AFTER needed jsp variables are set
            --%>
            <c:catch var="exception">
              <asset-ui:resolveAsset var="selectedAsset" uri="${selectedTreeNodeURI}"/>
            </c:catch>
            <dspel:include page="editAssetJavaScript.jsp">
              <dspel:param name="assetEditorPath" value="${assetEditorPath}"/>
              <dspel:param name="transient" value="${transient}"/>
              <dspel:param name="editable" value="${editable}"/>
              <dspel:param name="formHandlerPath" value="${formHandlerPath}"/>
              <c:if test="${(not empty selectedAsset) and (not empty param.treeComponent)}">
                <dspel:param name="selectedTreeNodeURI" value="${selectedTreeNodeURI}"/>
              </c:if>
            </dspel:include>

          </c:if>  <%-- not empty item mapping --%>

        </c:otherwise> <%-- not empty item (and not multi edit operation) --%>

      </c:choose>
    </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAsset.jsp#3 $$Change: 654746 $ --%>
