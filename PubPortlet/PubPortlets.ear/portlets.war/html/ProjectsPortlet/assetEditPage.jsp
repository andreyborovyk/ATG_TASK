<%--
  Asset Editor Page

  @param   assetURI           the publishing asset's URI or empty

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>
<!-- Begin assetEditPage.jsp -->

<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>
<%@ page import="atg.security.*" %>
<%@ page import="atg.versionmanager.*" %>
<%@ page import="atg.repository.*" %>
<%@ page import="atg.epub.PublishingConfiguration" %>
<%@ page import="atg.epub.pws.taglib.GetAssetTag" %>
<%@ page import="atg.epub.pws.taglib.GetAssetTag.Results" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<dspel:page>
<%-- Load scripts to support asset viewing/editing --%>
<script language="JavaScript" src="/atg/templates/page/html/scripts/asset_view_functions.js" type="text/javascript"></script>
<script language="JavaScript" src="/WebUI/dijit/previewLauncher/previewPicker.js" type="text/javascript"></script>

<link rel="stylesheet" href="/WebUI/dijit/previewLauncher/templates/previewPicker.css" type="text/css" media="all" />

<%@ include file="projectConstants.jspf" %>

<c:set var="displayPreviewButton" value="${(! empty sessionScope.previewUrl) && (! empty sessionScope.previewUserId) && (! empty sessionScope.previewSiteId)}"/>

<dspel:importbean var="previewContext" bean="/atg/userprofiling/preview/PreviewContext"/>

<c:choose>
  <c:when test="${not empty previewContext}">
    <c:set target="${previewContext}" property="previewURL" value="${null}"/>
    <c:set target="${previewContext}" property="previewUserId" value="${null}"/>
    <c:set target="${previewContext}" property="previewSiteId" value="${null}"/>
  
    <script type="text/javascript" charset="utf-8">
      var djConfig = { 
        isDebug: true,
        preventBackButtonFix: true,
        disableFlashStorage: true,
        usePlainJson: true,
        usesApplets: true,
        parseOnLoad: true
      };
  
      var dlgProg = null;
  
      dojo.require("dijit._Widget");
      dojo.require("dijit._Templated");
      dojo.require("dojo.parser");  // scan page for widgets and instantiate them
  
      // Register the Messaging widget namespace
      dojo.registerModulePath("atg.widget.previewPicker", "/WebUI/dijit/previewLauncher/previewPicker");
      dojo.require("atg.widget.previewPicker");
  
      // Hook the contextHelp creation to the page load
  
      console.log("Calling dojo.addOnLoad()");
  
      dojo.addOnLoad(function(){
  
        dlgProg = new atg.widget.previewPicker({
            toggle:"fade",
            toggleDuration:250,
            pickerForm:dojo.byId('previewLauncher')
            }, dojo.byId("previewContent"));
  
        dojo.connect(dojo.byId("previewAsButton"), "onclick", function(e){
          e.preventDefault();
          initiateUsersAndUrlRequests();
          dlgProg.show();
        });
  
        dojo.connect(dojo.byId("previewButton"), "onclick", function(e){
          e.preventDefault();
          dlgProg.serverLaunchCheck();
        });
  
        // executing this will force the preview buttons to be displayed if there is a user and url data
        console.log("Calling checkForPreviewUrls()");
        checkForPreviewUrls();
  
      });
    </script>
  </c:when>
  <c:otherwise>
    <script type="text/javascript" charset="utf-8">
      var djConfig = {
        usesApplets: true,
        parseOnLoad: true
      };
      dojo.require("dojo.parser");
    </script>
  </c:otherwise>
</c:choose>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<portlet:defineObjects/>

<%--
  Note that a session scoped assetURI is set by the browse
  tab.
--%>
<c:set var="paramAssetURI" value="${param.assetURI}"/>
<c:set var="assetURI" value="" scope="request"/>

<%-- Get PubPortlet Configuration bean --%>
<c:set var="configurationPath" value="/atg/epub/portlet/Configuration" />
<dspel:importbean var="config" bean="${configurationPath}"/>

<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

<pws:getCurrentProject var="projectContext"/>

<c:set var="project" value="${projectContext.project}"/>

<%-- edit or view mode, depending on project status --%>
<c:choose>
  <c:when test="${project.editable}">
    <c:set var="mode" value="edit"/>
  </c:when>
  <c:otherwise>
    <c:set var="mode" value="view"/>
  </c:otherwise>
</c:choose>

<%--
  Get the most recently used form handler from the AssetInfo session
  object.
--%>
<c:set var="currentFormHandler" value="${ assetInfo.context.assetFormHandler }"/>

<pws:getCurrentProject var="projectContext"/>

<c:set var="isRepositoryItem" value="false"/>

<c:choose>
  <%--
    If there is no assetURI attribute in the context, use the
    assetURI parameter
  --%>
  <c:when test="${ ! empty paramAssetURI}">
    <c:set var="assetURI" value="${paramAssetURI}" scope="request"/>
  </c:when>
  <%--
    If the asset URI has been modified by the form handler the
    assetURI attribute in the context will be correct
  --%>
  <c:when test="${ ! empty assetInfo.context.attributes.assetURI }">
    <c:set var="assetURI"
      value="${ assetInfo.context.attributes.assetURI }" scope="request"/>
  </c:when>
</c:choose>

<%-- need this javascript here after the mode has been determined --%>
<script type="text/javascript" charset="utf-8">
  function initiateUsersAndUrlRequests() {
    if (dlgProg.users == null)
      dlgProg.getPreviewUsers();
    if (dlgProg.sites == null)
      dlgProg.getPreviewSites();
    if (dlgProg.urls == null)
      dlgProg.getPreviewURLs('<c:out value="${mode}"/>', "view", "*", escape('<c:out value="${assetURI}"/>'));
  }
  
  function checkForPreviewUrls() {
    <c:if test="${! empty assetURI}">
      dlgProg.checkForPreviewURLs('<c:out value="${mode}"/>', "view", "*", escape('<c:out value="${assetURI}"/>'));
    </c:if>
  }
</script>

<%-- Get the repository item or virtual file --%>
<c:choose>
  <c:when test="${ ! empty assetURI }">
    <c:set var="transient" value="false" scope="request"/>
    <c:set target="${assetInfo.context.attributes}" property="assetURI"
      value="${assetURI}"/>
    <pws:getAsset uri="${assetURI}" var="assetVersion"
      workspaceName="${projectContext.project.workspace}">
      <c:choose>
        <c:when test="${ ! empty assetVersion.workingVersion.virtualFile }">
          <c:set value="${assetVersion.workingVersion.virtualFile}"
            var="item"/>
          <c:set var="isRepositoryItem" value="false"/>
        </c:when>
        <c:when test="${ ! empty assetVersion.workingVersion.repositoryItem}" >
          <c:set value="${assetVersion.workingVersion.repositoryItem}"
            var="item"/>
          <c:set var="isRepositoryItem" value="true"/>
        </c:when>
        <c:when test="${ ! empty assetVersion.asset.mainVersion.virtualFile }">
          <c:set value="${assetVersion.asset.mainVersion.virtualFile}"
            var="item"/>
          <c:set var="isRepositoryItem" value="false"/>
        </c:when>
        <c:when test="${ ! empty assetVersion.asset.mainVersion.repositoryItem }">
          <c:set value="${assetVersion.asset.mainVersion.repositoryItem}"
            var="item"/>
          <c:set var="isRepositoryItem" value="true"/>
        </c:when>
      </c:choose>
    </pws:getAsset>
  </c:when>
  <%--
    If no asset URI parameter was specified, try to use the current
    asset from the context . This situation will occur when a
    new asset has been created. Even when the new asset is saved
    the page won't have an assetURI param. AssetURI are only available
    when the asset is selected from the list of assets in the process
    asset list.
  --%>
  <c:when test="${ ! empty assetInfo.context.attributes.asset }">
    <c:set var="item" value="${assetInfo.context.attributes.asset}"/>
    <c:set var="transient" value="${assetInfo.context.attributes.transient}"
      scope="request"/>
  </c:when>
</c:choose>

<%--
  Update context with current asset information
  Set the current asset URI into the context so that if the form
  handler needs to modify the asset in a way that will change the
  asset URI it has access to this value.
--%>
<c:set target="${assetInfo.context.attributes}" property="asset"
  value="${item}"/>
<c:set target="${assetInfo.context.attributes}" property="transient"
  value="${transient}"/>

<%-- Uncomment this to get asset URIs for new assets once
     the bug in this tag gets fixed

  <c:if test="${ empty assetURI && ! empty item }">
    <pws:createVersionManagerURI var="assetURI" object="${item}"/>
  </c:if>
--%>

<c:set target="${assetInfo.context.attributes}" property="assetURI"
  value="${assetURI}"/>

<c:if test="${ ! empty item  }">
  <!-- get the mapping for the asset -->
  <dspel:getvalueof var="expert" bean="/atg/userprofiling/Profile.expert"/>
  <%--
    If the item is a file, need to get mapping via path and type to
    support file renaming, which changes the asset's URI
   --%>

  <c:if test="${ ! isRepositoryItem }">
    <%-- disgusting hack to work around funky fileFolder type --%>
    <c:if test="${currentFormHandler.itemType == 'fileFolder' }">
      <c:set target="${currentFormHandler}" property="componentPath"
        value="/atg/epub/file/ConfigFileSystem"/>
    </c:if>
  </c:if>

  <%-- Even if the item is not a secured repository item, you can pass it into this droplet
       and the 'true' oparam will be rendered. This keeps you from having to check if the
       item is secure or not before you use this droplet --%>
  <dspel:droplet name="/atg/repository/secure/HasItemAccess">
    <dspel:param name="secureItem" value="${item}"/>
    <dspel:param name="right" value="write"/>
    <dspel:oparam name="true">
      <%-- Don't change the mode here, it was set at the top of the page to edit or view
           depending on whether project.editable was true or false --%>
    </dspel:oparam>
    <dspel:oparam name="false">
      <c:set var="mode" value="view"/>
    </dspel:oparam>
    <dspel:oparam name="error">
      <c:set var="mode" value="none"/>
      <br/>
      <span style="color: #FF0000">
        <b><dspel:valueof param="errorMessage"/>&nbsp;</b>
      </span>
      <br/>
    </dspel:oparam>
  </dspel:droplet>

  <c:choose>
    <c:when test='${mode == "none"}'>
      <br/>
      <span style="color: #FF0000">
        <b><fmt:message key="no-permissions-to-view-item" bundle="${projectsBundle}"/></b>
      </span>
      <br/>
    </c:when>

    <c:otherwise>
      <c:if test='${mode == "view"}'>
        <br/>
        <span>
          <b><fmt:message key="readonly-permissions-to-view-item" bundle="${projectsBundle}"/></b>
        </span>
        <br/>
      </c:if>
      <biz:getItemMapping var="imap" mode="${mode}" readOnlyMode="view"
        showExpert="${expert}" item="${item}"
        itemName="${currentFormHandler.itemType}"
        itemPath="${currentFormHandler.componentPath}">
        
        <!-- Set the item mapping into a request scoped variable -->
        <c:set value="${imap}" var="imap" scope="request"/>

        <c:set target="${assetInfo.context.attributes}" property="itemType"
          value="${imap.itemName}"/>
        <c:set target="${assetInfo.context.attributes}" property="componentPath"
          value="${imap.itemPath}"/>

        <!-- Import form handler specified in mapping -->
        <dspel:importbean var="formHandler" bean="${imap.formHandler.path}"/>

        <c:set target="${formHandler}" property="assetInfo"
          value="${assetInfo}"/>
        <c:set target="${formHandler}" property="editMode"
          value='${project.editable}'/>

        <!-- Make the form handler EL variable request scoped -->
        <c:set value="${formHandler}" var="formHandler" scope="request"/>

        <c:set target="${formHandler}" property="itemType"
          value="${imap.itemName}"/>
        <c:set target="${formHandler}" property="componentPath"
          value="${imap.itemPath}"/>

        <c:choose>
          <c:when test="${ transient }">
            <c:set target="${formHandler}" property="transientItem" value="${item}"/>
            <c:set target="${assetInfo.context.attributes}" property="transient"
              value="true"/>
          </c:when>
          <c:otherwise>
            <c:set target="${formHandler}" property="item" value="${item}"/>
            <c:set target="${assetInfo.context.attributes}" property="transient"
              value="false"/>
          </c:otherwise>
        </c:choose>

        <c:set target="${assetInfo.context.attributes}" property="asset"
          value="${item}"/>
        <c:set target="${assetInfo.context}" property="assetFormHandler"
          value="${formHandler}"/>

        <!-- Transfer mapping attributes to form handler -->
        <c:forEach items="${imap.formHandler.attributes}" var="attr">
          <c:set target="${formHandler.attributes}"
            property="${attr.key}" value="${attr.value.value}"/>
        </c:forEach>

        <c:set value="${formHandler.assetEditor}" target="${imap}"
          property="itemEditor"/>

        <%--
          If the form handler defines a value dictionary, set its
          name in the mapping
         --%>
        <c:if test="${ ! empty imap.formHandler.attributes.atgFormValueDict }">
          <c:set target="${imap}" property="valueDictionaryName"
            value="${imap.formHandler.attributes.atgFormValueDict.value}"/>
        </c:if>

        <c:if test="${ empty actionURL }">
          <portlet:actionURL var="actionURL"/>
        </c:if>

        <dspel:form id="theAssetForm" formid="theAssetForm" enctype="multipart/form-data"
          action="${actionURL}" method="post">

          <script language="JavaScript">
            function getProjectId() {
              return '<c:out value="${project.id}"/>';
            }

            function submitForm( form ) {
              if ( form.onsubmit )
                form.onsubmit();
              form.submit();
            }
            function getForm() {
              return document.getElementById( "theAssetForm" );
            }
            function submitViewChange( viewNumber ) {
              fireOnSubmit();
              fsOnSubmit( 'theAsset');
              var form = getForm();
              form.elements["<c:out value="${imap.formHandler.path}.view"/>"].value =
                viewNumber;
              form.elements["<c:out value="${imap.formHandler.path}.actionType"/>"].value =
                "setView";
              submitForm( form );
            }
            function saveChanges( options ) {
              fireOnSubmit();
              fsOnSubmit( 'theAsset');
              var form = getForm();
              // default viewNumber is 0
              if ( ! options[ 'viewNumber' ] )
                options.viewNumber = 0;
              // default action is "update"
              if ( ! options[ 'actionType' ] )
                options.actionType = "update";
              form.elements["<c:out value="${imap.formHandler.path}.view"/>"].value =
                options.viewNumber;
              form.elements["<c:out value="${imap.formHandler.path}.actionType"/>"].value =
                options.actionType;
              submitForm( form );
            }
            <c:if test="${ ! empty assetInfo.context.attributes.onSelect }">
              <c:if test="${ ! empty assetInfo.context.attributes.newAssetID }">
                <c:set var="displayNameValue" value="${assetInfo.context.attributes.newAssetDisplayName}"/>
                function updatePickerValue() {
                  var selected = new Object();
                  selected.id = '<c:out value="${assetInfo.context.attributes.newAssetID}"/>';

                  // The reason URLEncoder is used here is because displayNameValue can have line breaks
                  // which would cause trouble using Javascript's encodeURIComponent( String ) method.
                  // Setting the URLEncoder's encoding scheme to UTF-8 makes it compatable with
                  // Javascript's decodeURIComponent( String ) method
                  var encDN = '<%=java.net.URLEncoder.encode(""+pageContext.findAttribute("displayNameValue"), "UTF-8") %>';
                  selected.displayName = decodeJavaURIComponent(encDN);

                  selected.uri = '<c:out value="${assetInfo.context.attributes.newAssetAssetURI}"/>';
                  var data = '<c:out value="${assetInfo.context.attributes.assetPickerCallerData}"/>';
                  window[ '<c:out value="${assetInfo.context.attributes.onSelect}"/>' ]( selected, data );
                }
                registerOnLoad( updatePickerValue );
                <c:set target="${assetInfo.context.attributes}" property="onSelect" value=""/>
                <c:set target="${assetInfo.context.attributes}" property="newAssetID" value=""/>
                <c:set target="${assetInfo.context.attributes}" property="newAssetDisplayName" value=""/>
              </c:if>
            </c:if>

            // these two notification methods keep the iframe in view
            // in legacy browsers (i.e. IE)
            registerOnLoad( function() { init('browser'); resizeAssetBrowser() } );
            registerOnResize( new function() { resizeAssetBrowser(); } );

          </script>

          <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
            bean="${imap.formHandler.path}.assetInfoPath"/>
          <dspel:input type="hidden" value="4" priority="100"
            bean="${imap.formHandler.path}.contextOp"/>
          <dspel:input type="hidden" value="${project.editable}" priority="100"
            bean="${imap.formHandler.path}.editMode"/>

          <dspel:input type="hidden" value="${imap.itemName}"
            bean="${imap.formHandler.path}.itemType"/>
          <dspel:input type="hidden" value="${imap.itemPath}"
            bean="${imap.formHandler.path}.componentPath"/>

          <c:if test="${empty formHandler.view}">
            <c:set target="${formHandler}" var="view" value="0"/>
          </c:if>

          <div class="contentActions">
            <table cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td class="blankSpace">&nbsp;
                </td>
                <td>
                  <h2>
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.view"
                      value="" />
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.actionType"
                      value="none" priority="-1"/>
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.submitAction"
                      value="1" priority="-10"/>
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.actionURL"
                      value="${actionURL}" priority="-10"/>
                    <c:forEach items="${imap.viewMappings}" var="aView" varStatus="vstat">
                      <c:if test="${vstat.first}">
                        <c:set value="${aView}" var="view" scope="request"/>
                      </c:if>
                      <c:choose>
                        <c:when test="${ vstat.index == formHandler.view }">
                          <c:set value="${aView}" var="view" scope="request"/>
                          <c:out value="${view.displayName}"/>
                        </c:when>
                        <c:otherwise>
                          <a href='javascript:submitViewChange( "<c:out value="${vstat.index}"/>" );' onmouseover="status='';return true;">
                            <c:out value="${aView.displayName}"/>
                          </a>
                        </c:otherwise>
                      </c:choose>
                      <c:if test="${ ! vstat.last }">
                        |
                      </c:if>
                    </c:forEach>
                  </h2>
                </td>
                <c:choose>
                  <c:when test="${ ! empty formHandler.formExceptions }">
                    <td class="error rightAlign" width="100%">
                      Errors encountered saving data &nbsp;
                    </td>
                  </c:when>
                  <c:otherwise>
                    <td class="blankSpace" width="100%">&nbsp;
                    </td>
                  </c:otherwise>
                </c:choose>
              </tr>
            </table>
          </div>

          <!--
            <c:out value="Mapping Information --------------------"/>
            <c:out value="  description: ${imap.description}"/>
            <c:out value="     itemPath: ${imap.itemPath}"/>
            <c:out value="     itemName: ${imap.itemName}"/>
            <c:out value="         view: ${view.name}, context root: ${view.contextRoot} URI: ${view.uri}"/>
          -->

          <div id="nonTableContent">
            <c:if test="${ ! empty formHandler.formExceptions }">
              <table>
                <c:forEach items="${formHandler.formExceptions}" var="ex">
                  <c:if test="${ ex.class.name == 'atg.droplet.DropletException'}">
                    <tr>
                      <td class="error">
                        <c:out value="${ex.message}"/>
                      </td>
                    </tr>
                  </c:if>
                </c:forEach>
              </table>
            </c:if>
          </div>

          <dspel:include otherContext="${view.contextRoot}" page="${view.uri}"/>

          <!--
             Render the form used to save the state of the asset view without saving to the repository...
             ...  this is assuming we're reusing the form handler form to FormSave...
          -->
          <dspel:input
            bean="${assetInfoPath}.context.attributes.fs_isSaved" value="true" type="hidden"/>
          <div id="theAssetDiv">
            <c:forEach items="${view.propertyMappings}" var="viewMapEntry">
              <!-- Setup a bogus form field in anticipation of saving the form state -->
              <dspel:input bean="${assetInfoPath}.context.attributes.fs_${viewMapEntry.value.uniqueId}"
                type="hidden"/>

              <!-- Check if the form state was previously saved and restore it -->
              <c:set var="fsPropName" value="fs_${viewMapEntry.value.uniqueId}"/>
              <c:if test="${assetInfo.context.attributes.fs_isSaved == 'true' &&
                            assetInfo.context.attributes[fsPropName] != null}">
                <script language="JavaScript">
                  fsSetPropertyValue( '<c:out value="${viewMapEntry.value.uniqueId}"/>',
                                      '<c:out value="${assetInfo.context.attributes[fsPropName]}" escapeXml="true"/>');
                </script>
              </c:if>
            </c:forEach>
          </div>

        </dspel:form>

        <script type="text/javascript" language="JavaScript">
          fsSetFHPath( '<c:out value="${assetInfoPath}.context.attributes"/>');
        </script>

        <!--
           Render the action buttons
        -->
        <div id='previewContent'></div>
        <div class="contentActions">
          <table cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td width="100%" class="blankSpace">&nbsp;
              </td>

              <form action="" method="POST" id="previewLauncher">
                <input type="hidden" name="userId" value="">
                <input type="hidden" name="url" value="">
                <input type="hidden" name="siteId" value="">

                <td>
                  <a id="previewButton" class="mainContentButton preview buttonImage" href="" title='<fmt:message key="preview-button-title" bundle="${projectsBundle}"/>' onmouseover="status='';return true;">
                    <fmt:message key="preview-button-label" bundle="${projectsBundle}"/>
                  </a>
                </td>
                <td>
                  <a id="previewAsButton" class="mainContentButton preview buttonImage" href="" title='<fmt:message key="preview-as-button-title" bundle="${projectsBundle}"/>' onmouseover="status='';return true;">
                    <fmt:message key="preview-as-button-label" bundle="${projectsBundle}"/>
                  </a>
                </td>

              </form>
             
              <c:if test="${project.editable}">
                <td>
                  <a href='javascript:saveChanges( { viewNumber: "<c:out value="${formHandler.view}"/>" } );'
                    class="mainContentButton save" onmouseover="status='';return true;">
                    <fmt:message key="apply" bundle="${projectsBundle}"/>
                  </a>
                </td>
              </c:if>
              <td class="blankSpace">&nbsp;
              </td>
            </tr>
          </table>
        </div>

        <!--
           Render the form used to save the state of the asset view without saving to the repository...
           This form is only used to FormSave in cases where we're not actually using the standard
             form handler saver form (like the one above).  Ugly, but necessary...
        -->
        <iframe src="javascript:false;" name="formSaverIframe" width="1" height="1" frameborder="0" scrolling="no">
        </iframe>
        <c:url context="${ config.contextRoot }" value='/html/ProjectsPortlet/postInvoke.jsp' var="reloadUrl"/>
        <dspel:form id="formSaverForm" formid="formSaverForm" method="post"
          action="${reloadUrl}">
          <dspel:input
            bean="${assetInfoPath}.context.attributes.fs_isSaved" value="true" type="hidden"/>
          <dspel:input id="fs_collectionPickerItem"
            bean="${assetInfoPath}.context.attributes.fs_collectionPickerItem" type="hidden"/>
          <div id="formSaverDiv">
            <c:forEach items="${view.propertyMappings}" var="viewMapEntry">
              <!-- Setup a bogus form field in anticipation of saving the form state -->
              <dspel:input bean="${assetInfoPath}.context.attributes.fs_${viewMapEntry.value.uniqueId}"
                type="hidden"/>

              <!-- Check if the form state was previously saved and restore it -->
              <c:set var="fsPropName" value="fs_${viewMapEntry.value.uniqueId}"/>

              <c:if test="${ project.editable }">
                <c:if test="${assetInfo.context.attributes.fs_isSaved == 'true' &&
                              assetInfo.context.attributes[fsPropName] != null}">
                  <script language="JavaScript">
                    fsSetPropertyValue( '<c:out value="${viewMapEntry.value.uniqueId}"/>',
                                        '<c:out value="${assetInfo.context.attributes[fsPropName]}" escapeXml="true"/>');
                  </script>
                </c:if>
              </c:if>
            </c:forEach>
          </div>
        </dspel:form>

      </biz:getItemMapping>
    </c:otherwise>
  </c:choose>

</c:if> <!-- ! empty item -->
</dspel:page>
<!-- End assetEditPage.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetEditPage.jsp#3 $$Change: 654744 $--%>
