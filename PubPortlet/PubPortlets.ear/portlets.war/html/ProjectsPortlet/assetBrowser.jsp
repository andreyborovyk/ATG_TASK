<%--
  Initial Asset Browser Page

  @param browserMode - required

    "project" - For adding assets to current project
    "pick" - For asset picker mode

  @param createMode - required

    Set to 'redirect' to create new asset and redirect user to 
    'assetBrowserCreateURL', set to 'iframe' to allow nested asset 
    creation, set to 'none' to disallow creation of assets.

  @param selectMode

    Determines whether the search results allows selection of just one
    or multiple assets. Should be one of ["single" | "multi" | "none"]
    where "none" disables selecting of existing assets. Default is 
    "multi".

  assetBrowserCreateURL  

    A URL to which to redirect the parent page
    (parent.window.location.href) when an asset is created by this
    browser. Only relevant if createMode is 'redirect'. Default
    is empty.

  @param closeAction

    "hide" to hide asset browser, or "refresh" to hide asset browser
    and refresh parent window. Default is "hide".

  This page (and assetBrowserSearchResults.jsp) uses the following
  session scoped variables:

  assetBrowserProject

    The project to add selected assets to (not currently used).
    Default is projectContext.project in sessionScope.

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetBrowser.jsp#3 $$Change: 654744 $
  @updated $DateTime: 2011/06/27 09:00:33 $$Author: ikalachy $
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin assetBrowser.jsp -->
<dspel:page>
<c:catch var="ex">

  <style>
    @import url("<c:url context="${initParam['atg.bizui.ContextPath']}" value="/templates/style/css/style.jsp" />");
  </style>

  <%-- set to true to see lots of debugging information --%>
  <c:set var="debug" value="false" scope="request"/>

  <c:set var="assetSearchHandlerPath" 
    value="/atg/epub/servlet/AssetBrowserSearchFormHandler"/>
  <dspel:importbean bean="${assetSearchHandlerPath}"
    var="assetSearchHandler"/>

  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath" 
        value="/atg/epub/servlet/ProcessAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <script language="JavaScript" type="text/javascript" 
    src="<c:url value='/html/scripts/search.jsp'><c:param name="searchHandlerPath" value="${assetSearchHandlerPath}"/></c:url>">
  </script>

  <%-- project assets --%>
  <pws:getCurrentProject var="projectContext"/>
<%-- This is not currently used... may be used in a future version.
  <c:set target="${assetInfo.attributes}" property="projectAssets"
    value="${projectContext.project.assets}"/>
--%>

  <%-- 
    browserMode   

    Don't set browser mode if it doesn't exist in the params
    because it won't if the user comes back to this page from the
    search results
  --%>
  <c:if test="${ ! empty param.browserMode }">
    <c:set target="${assetInfo.attributes}" property="browserMode"
      value="${param.browserMode}"/>
  </c:if>

  <!-- create mode -->
  <c:if test="${ ! empty param.createMode }">
    <c:set target="${assetInfo.attributes}" 
      property="assetBrowserCreateMode" value="${param.createMode}"/>
  </c:if>

  <!-- select mode -->
  <c:if test="${ ! empty param.selectMode }">
    <c:set target="${assetInfo.attributes}" 
      property="assetBrowserSelectMode" value="${param.selectMode}"/>
  </c:if>

  <!-- itemType -->
  <c:if test="${ ! empty param.itemType }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerItemType" value="${param.itemType}"/>
  </c:if>

  <!-- typeCode -->
  <c:if test="${ ! empty param.typeCode }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerTypeCode" value="${param.typeCode}"/>
  </c:if>

  <!-- itemTypeDisplayName -->
  <c:if test="${ ! empty param.itemTypeDisplayName }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerItemTypeDisplayName" 
      value="${param.itemTypeDisplayName}"/>
  </c:if>

  <!-- itemTypeRepositoryName -->
  <c:if test="${ ! empty param.itemTypeRepositoryName }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerItemTypeRepositoryName" 
      value="${param.itemTypeRepositoryName}"/>
  </c:if>

  <!-- onSelect method -->
  <c:if test="${ ! empty param.onSelect }">
    <c:set target="${assetInfo.context.attributes}" 
      property="onSelect" 
      value="${param.onSelect}"/>
  </c:if>

  <!-- closeAction -->
  <c:if test="${ ! empty param.closeAction }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerCloseAction" 
      value="${param.closeAction}"/>
  </c:if>

  <!-- caller defined data -->
  <c:if test="${ ! empty param.callerData }">
    <c:set target="${assetInfo.context.attributes}" 
      property="assetPickerCallerData" 
      value="${param.callerData}"/>
  </c:if>
  <c:if test="${ ! empty param.onHideData }">
    <c:set target="${assetInfo.context.attributes}" 
      property="assetPickerOnHideData" 
      value="${param.onHideData}"/>
  </c:if>

  <%-- clear/initialize asset info attributes --%>
  <c:set target="${assetInfo.attributes}" 
    property="assetPickerURIResults" value="${null}"/>
  <c:set target="${assetInfo.attributes}" 
    property="assetPickerPropertyResults" value="${null}"/>
  <c:set target="${assetInfo.attributes}" 
    property="assetPickerAssetPropertyName" 
    value="${param.propertyName}"/>
  <c:set target="${assetInfo.attributes}" 
    property="assetPickerPropertyName" 
    value="${param.propertyName}"/>

  <c:if test="${debug}">
    <c:set target="${assetSearchHandler}" property="loggingDebug"
      value="${debug}"/>
  </c:if>

  <script language="JavaScript">
    <dspel:include otherContext="${initParam['atg.bizui.ContextPath']}"
      page="/templates/page/html/scripts/scripts.js"/>
  </script>

  <fmt:setBundle var="projectsBundle" 
    basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

  <script language="JavaScript" type="text/javascript">

    var picker = parent.currentPicker;

    var typeFilter;
    if ( picker != undefined )
      typeFilter = picker.typeFilter;

    var config = null;
    if ( parent[ 'getAssetBrowserConfig' ] != undefined )
      config = parent.getAssetBrowserConfig();

    function showAsset( elementID ) {
      if ( document.getElementById(elementID) )
        document.getElementById(elementID).style.display = "block";
    }
    function hideAsset( elementID ) {
      if ( document.getElementById(elementID) )
        document.getElementById(elementID).style.display = "none";
    }
    function submitSearch() {
      setFormElement('searchform','handlerAction',
        <c:out value="${assetSearchHandler.ACTION_SEARCH}"/> );
      submitForm('searchform');
    }
    function submitSaveParentAndCreate() {
      // call the callback to save the form contents before moving to the new asset
      if (true == parent.fsOnSubmit()) {
        parent.postFsOnSubmit = submitSaveParentFinal;
      }
      else {
        parent.postFsOnSubmit = null;
        // Wait a bit for the submit to complete...
        setTimeout( 'submitSaveParentFinal()', 1000);
      }
    }
    function submitSaveParentFinal() {
      // creating asset while editing existing asset, push current context
      var listArray = 
        document.getElementById("assetTypeChoice").value.split("|");
      document.getElementById( listArray[0] + "CreateType" ).value = 
        listArray[ 1 ];
      if ( config != null ) {
        document.getElementById( listArray[0] + "ContextOp" ).value = 
          config.contextOp;
      }
      else {
        document.getElementById( listArray[0] + "ContextOp" ).value = 
          '<c:out value="${assetInfo.CONTEXT_PUSH}"/>';
      }
      var theForm = document.getElementById( listArray[0] + "Create" );
      theForm.submit();
      //document.getElementById( listArray[0] + "Create" ).submit();
    }
    function submitCreateAsset() {
      // get the encoded create type value. The incoding follows
      // this pattern: [repository|file]|/component/path:itemType.
      // The first element is used to choose the appropriate form
      // handler to create the asset, the component path and item
      // type indicate the specific type of repository item or
      // file asset to create
      
      var listArray = 
        document.getElementById("assetTypeChoice").value.split("|");
      document.getElementById( listArray[0] + "CreateType" ).value = 
        listArray[ 1 ];
      document.getElementById( listArray[0] + "AssetDisplayName" ).value = 
        listArray[ 2 ];

      if ( '<c:out value="${assetInfo.attributes.assetBrowserCreateMode}"/>' == 'iframe' ) {
        // Use save the form data of the parent before moving to the new asset.
        submitSaveParentAndCreate();
      }
      else {
        // creating asset from projectAssets.page, start with new context
        document.getElementById( listArray[0]+"ContextOp" ).value = 
          '<c:out value="${assetInfo.CONTEXT_CLEAR}"/>';

        // It's OK to submit now...
        document.getElementById( listArray[0] + "Create" ).submit();
      }
    }

    function closeAfterCreate() {
      var createURL = '<c:out escapeXml="false" 
        value="${assetInfo.attributes.assetBrowserCreateURL}"/>';
      if ( createURL )
        parent.window.location.href = createURL;
      else
        refreshParentWindow();
    }    

    // Look for param that indicates iframe should be closed
    var wndCmd = '<c:out value="${param.wndCmd}"/>';
    if ( "close" == wndCmd )
      closeAfterCreate();

    function setSearchType() {
      // Look for param that indicates iframe should be closed
      var iwantto = '<c:out value="${param.iwantto}"/>';
      if ( 'search' == iwantto )
        showAsset( 'addExisting' );
      if ( 'create' == iwantto )
        showAsset( 'createNew' );
    }

    registerOnLoad( setSearchType );

    // Two dropdown repository/item type view support
    var assetTypes = new Array( 0 );

    // If true, picker type is fileFolder and component
    // paths should be ignored, because fileFolders 
    // exist in all VFS file systems
    var isFolderType = false;
    
    //
    // Initialize the search form asset type choices based on the 
    // specified component path
    //
    // repository - component path
    // form - 
    // url - 
    //
    function populateSearchTypePulldown( componentPath, form, url ) {

      var j = 1;
      var item;
      var listArray = componentPath.split("|");
      compPath = listArray[0];

      document.searchform.assetTypePulldown.options.length = 0;

      for ( var i=0; i<assetTypes.length; i++ ) {
        if ( isFolderType || assetTypes[i].componentPath == compPath ) {
          document.searchform.assetTypePulldown.options.length = j;
          document.searchform.assetTypePulldown.options[j-1] = 
            new Option( assetTypes[i].displayName );
          item = assetTypes[i].componentPath + ":" + assetTypes[i].typeName;
          document.searchform.assetTypePulldown.options[j-1].value = item;
          if (item == document.searchform.repositoryItemType.value)
            document.searchform.assetTypePulldown.options[j-1].selected = true;
          j++;
        }
      }

      setSearchValue( document.searchform.assetTypePulldown.value, form, url );
    }
    
    // 
    // val: encoded path and item type
    // 
    function setSearchValue( val, form, url ) {
      if ( val != null && val != "" ) {
        document.searchform.searchInput.value = val;
        document.searchform.repositoryItemType.value = val;
        for( var ii=0; ii < assetTypes.length; ii++ ) {
          if ( val == assetTypes[ii].encodedType ) {
            document.searchform.sortProperty.value = 
              assetTypes[ii].displayNameProperty;
          }
        }
        changeSearchType(form, url);
      }
    }
    
    //
    // Get an associative array of unique components (repositories
    // or virtual file systems.) The key is the component path, the
    // value is an object with the following properties:
    //
    //   typeCode = 'repository' | 'file'
    //   componentPath = nucleus component path
    //
    function getComponentTypes() {
      var components = new Object();
      for( var ii=0; ii < assetTypes.length; ++ii ) {
        var component = new Object();
        component.path = assetTypes[ii].componentPath;
        component.typeCode = assetTypes[ii].typeCode;
        component.displayName = assetTypes[ii].repositoryName;
        components[ assetTypes[ii].componentPath ] = component;
      }
      return components;
    }

    //
    // Initialize the search component type (repository/VFS)
    // choice pulldown
    //
    // componentID = ID of choice component
    //
    function initializeSearchComponentPulldown( componentID ) {
      var componentChoice = document.getElementById( componentID );
      var componentTypes = getComponentTypes();
      if ( componentChoice.options.length == 0 ) {
        var ii = 0;
        for( var componentKey in componentTypes ) {
          var component = componentTypes[ componentKey ];
          var option = new Option( component.displayName );
          option.value = component.path + "|" + component.typeCode;
          componentChoice.options[ ii ] = option;
          ++ii;
        }
      }
      else {
        for( var ii=0; ii < componentChoice.options.length; ) {
          var key = componentChoice.options[ii].value.split("|");
          if ( ! componentTypes[ key[0] ] ) {
            if ( iewin )
              componentChoice.options.remove( ii );
            else
              componentChoice.options[ii] = null;
          }
          else {
            ii++;
          }
        }
      }
    }

    //
    // Initialize the create form handler's component (repositoy or
    // virtual file system) type pulldown
    //
    function initializeCreateComponentPulldown( componentID ) {
      var componentChoice = document.getElementById( componentID );
      componentChoice.options.length = 0;
      var componentTypes = getComponentTypes();
      var ii = 0;
      for( var componentKey in componentTypes ) {
        var component = componentTypes[ componentKey ];
        var option = new Option( component.displayName );
        option.value = component.path;
        componentChoice.options[ ii ] = option;
        ++ii;
      }
    }

    //
    // Initialize the search types based on the current value
    // of the search form's component path
    //
    // (was: initializeAssetPulldown())
    // 
    function initializeSearchTypePulldown() {
      if ( document.searchform.repositoryPulldown.value != null ) {
        populateSearchTypePulldown( document.searchform.repositoryPulldown.value, 
         '', '' );
      }
    }

    //
    // Populate the asset type choices on the create form
    //
    function populateAssetTypeChoice( compPath ) {
      var j = 1;
      var item;
      document.chooser.assetTypeChoice.options.length = 0;
      for ( var i=0; i<assetTypes.length; i++ ) {
        if ( ! assetTypes[i].createable )
          continue;
        if (assetTypes[i].componentPath == compPath) {
          document.chooser.assetTypeChoice.options.length = j;
          document.chooser.assetTypeChoice.options[j-1] = 
            new Option( assetTypes[i].displayName );
          item = assetTypes[i].typeCode + "|" + assetTypes[i].encodedType + "|" + assetTypes[i].displayName;
          document.chooser.assetTypeChoice.options[j-1].value = item;
          j++;
        }
      }
    }
    
    // 
    // Initialize the asset type choice on the create form handler
    //
    function initializeCreateAssetTypePulldown() {
      if ( document.chooser.assetTypeChoice.value == null ||
        document.chooser.assetTypeChoice.value == "" ) {
        populateAssetTypeChoice( document.chooser.repositoryChoice.value ); 
      }
    }

  </script>

    <%-- unset project context if not in pick mode --%>
    <dspel:setvalue value="${ 'pick' != assetInfo.attributes.browserMode }"
      bean="${assetSearchHandlerPath}.unsetProjectContext"/>

    <!-- project -->
    <c:choose>
      <c:when test="${ ! empty param.project }">
        <c:set var="assetBrowserProject" value="${param.project}"
          scope="session"/>
        <dspel:setvalue bean="/atg/web/ATGSession.assetBrowserProject" value="${param.project}"/>
      </c:when>
      <c:otherwise>
        <c:set var="assetBrowserProject" 
          value="${projectContext.project}" scope="session"/>
        <dspel:setvalue bean="/atg/web/ATGSession.assetBrowserProject" value="${projectContext.project}"/>
      </c:otherwise>
    </c:choose>

<!-- 
    <c:if test="${debug}">
      Asset Info Path <c:out value="${assetInfoPath}"/>
      <br />
      Create Mode: <c:out value="${assetInfo.attributes.assetBrowserCreateMode}"/>
      <br />
      Create URL: <c:out value="${assetInfo.attributes.assetBrowserCreateURL}"/>
      <br />
      Select Mode: <c:out value="${assetInfo.attributes.assetBrowserSelectMode}"/>
      <br />
      Project: <c:out value="${sessionScope.assetBrowserProject}"/>
      <br />
      Test: <c:out value="${param.test}"/>
      <br />
    </c:if>
-->
    <html>

      <body class="assetBrowser" onload="fireOnLoad();">

        <div id="assetBrowserHeader">
          <h2>
            <fmt:message key="asset-browser-title" bundle="${projectsBundle}"/>
          </h2>
        </div>

        <pws:getVersionedAssetTypes var="results">
          <c:forEach items="${results}" var="result">
            <c:forEach items="${result.types}" var="type">
              <c:choose>
                <c:when test="${result.repository}">
                  <c:set var="typeCode" value="repository"/>
                </c:when>
                <c:when test="${result.fileSystem}">
                  <c:set var="typeCode" value="file"/>
                </c:when>
              </c:choose>
              <script language="JavaScript" type="text/javascript">
                var assetType = new Object();
                assetType.typeName = '<c:out value="${type.typeName}"/>';
                assetType.displayName = '<c:out value="${type.displayName}"/>';
                assetType.typeCode = '<c:out value="${typeCode}"/>';
                assetType.repositoryName = '<c:out value="${result.repositoryName}"/>';
                assetType.componentPath = '<c:out value="${result.componentPath}"/>';
                assetType.encodedType = 
                  assetType.componentPath + ":" + assetType.typeName;
                assetType.displayNameProperty = 'displayName';
                assetType.createable = true;
                if ( typeFilter == undefined || typeFilter(assetType) == true )
                  assetTypes[ assetTypes.length ] = assetType;
              </script>
              <c:if test="${result.repository}">
                <c:if test="${ !empty type.itemDescriptor.itemDisplayNameProperty}">
                  <script language="JavaScript" type="text/javascript">
                    assetType.displayNameProperty = 
                      '<c:out value="${type.itemDescriptor.itemDisplayNameProperty}"/>';
                  </script>
                </c:if>
              </c:if>
            </c:forEach>
          </c:forEach>
        </pws:getVersionedAssetTypes>

        <%-- these are needed so the search form submission works 
             correctly --%>
        <c:url var="searchResultsActionURL" 
          value="/html/ProjectsPortlet/assetBrowserSearchResults.jsp">
          <c:param name="assetInfo" value="${assetInfoPath}"/>
        </c:url>
        <c:url var="searchActionURL" value="/html/ProjectsPortlet/assetBrowser.jsp">
          <c:param name="assetInfo" value="${assetInfoPath}"/>
          <c:param name="iwantto" value="search"/>
        </c:url>

        <div id="assetForm" style="margin-left: -250px; margin-top: 10px;">

          <script language="JavaScript">
            function initBrowseForm( assetType, form, url ) {
              showAsset('addExisting');
              hideAsset('createNew');
              initializeSearchComponentPulldown( 'repositoryPulldown' );
              populateSearchTypePulldown( assetType, form, url );
            }
          </script>

          <form action="#">   
            <table cellpadding="0" width="100%" cellspacing="3" border="0" 
              style="font-size: 1em;">
              <tr>
                <td class="formLabel formLabelRequired">
                  <fmt:message key="i-want-to" bundle="${projectsBundle}"/>
                </td>
                <td>
                  <p>
                  <input type="radio" name="radio_3" value="a" class="radio" id="createRadio"
                    <c:choose>
                      <c:when test="${ 'none' == assetInfo.attributes.assetBrowserCreateMode }">
                        disabled
                      </c:when>
                      <c:when test="${ 'create' == param.iwantto }">
                        checked="true"
                      </c:when>
                    </c:choose>
                    onclick="showAsset('createNew'); hideAsset('addExisting');" />
                  <label for="createRadio">
                    <fmt:message key="create-new-asset" bundle="${projectsBundle}"/>
                    </label>
                  <br />
                  <input type="radio" name="radio_3" value="a" class="radio" id="searchRadio"
                    <c:choose>
                      <c:when test="${ 'search' == param.iwantto }">
                        checked="true"
                      </c:when>
                      <c:when test="${ 'none' == assetInfo.attributes.assetBrowserCreateMode }">
                        checked="true"
                      </c:when>
                    </c:choose>
                    onclick="initBrowseForm(document.searchform.repositoryPulldown.value, 
                      'searchform', '<c:out value="${searchActionURL}"/>' );" />
                    <label for="searchRadio">
                      <c:choose>
                        <c:when test="${ mode == 'pick' }">
                          <fmt:message key="select-existing-asset" bundle="${projectsBundle}"/>
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="browse-existing-assets" bundle="${projectsBundle}"/>
                        </c:otherwise>
                      </c:choose>
                    </label>
                    </label>
                  </p>
                </td>
              </tr>
            </table>
          </form>
      
          <div id="addExisting" style="display: none;">
 
            <dspel:setvalue bean="${assetSearchHandlerPath}.doTextSearch" 
              value="false"/>
            <dspel:setvalue bean="${assetSearchHandlerPath}.doKeywordSearch" 
              value="true"/>
            <dspel:setvalue bean="${assetSearchHandlerPath}.ignoreCase" 
              value="true"/>
            <dspel:setvalue bean="${assetSearchHandlerPath}.allowRefine" 
              value="false"/>
            <dspel:setvalue bean="${assetSearchHandlerPath}.loggingDebug" 
              value="${debug}"/>

            <dspel:form formid="search" name="searchform" id="searchform"
              action="${searchResultsActionURL}" method="post">
              
              <!-- Initial sort on displayName -->
              <dspel:input type="hidden" value="displayName" priority="10"
                 id="sortProperty" name="sortProperty"
                 bean="${assetSearchHandlerPath}.sortProperty"/>

              <!-- Initial sort order is ascending (1) -->
              <dspel:input type="hidden" value="1" priority="10"
                bean="${assetSearchHandlerPath}.sortDirection"/>

              <dspel:input type="hidden" value="${assetSearchHandler.ACTION_SEARCH}" 
                name="handlerAction" id="handlerAction"
                priority="10" bean="${assetSearchHandlerPath}.action"/>

              <dspel:input type="hidden" value="1" priority="-10"
                 bean="${assetSearchHandlerPath}.performAction"/>

              <table cellpadding="0" cellspacing="3" border="0" style="font-size: 1em;">

                <%-- project mode, list all versioned asset types --%>
                <tr>
                  <td class="formLabel formLabelRequired">
                    <fmt:message key="add-search-in-repository" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <dspel:select iclass="formElement"
                      priority="10"
                      id="repositoryPulldown"
                      name="repositoryPulldown"
                      bean="${assetSearchHandlerPath}.componentPath"
                      onchange="
                        populateSearchTypePulldown(
                          document.searchform.repositoryPulldown[ document.searchform.repositoryPulldown.options.selectedIndex ].value, 
                          'searchform', '${searchActionURL}' );">
                      <c:forEach items="${results}" var="result">
                        <c:forEach items="${result.types}" var="type">
                          <c:choose>
                            <c:when test="${result.repository}">
                              <c:set var="typeCode" value="repository"/>
                            </c:when>
                            <c:when test="${result.fileSystem}">
                              <c:set var="typeCode" value="file"/>
                            </c:when>
                          </c:choose>
                        </c:forEach>
                        <dspel:option value="${result.componentPath}|${typeCode}">
                          <c:out value="${result.repositoryName}"/>
                        </dspel:option>
                      </c:forEach>

                    </dspel:select>
                  </td>
                </tr>

                <tr>
                  <td class="formLabel formLabelRequired">
                    <fmt:message key="add-show-assets-of-type" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <dspel:input type="hidden" 
                      bean="${assetSearchHandlerPath}.itemType"
                      id="repositoryItemType"
                      name="repositoryItemType" />
          
                    <dspel:input type="hidden" 
                      bean="${assetSearchHandlerPath}.encodedPathAndItemType"
                      id="searchInput"
                      name="searchInput" />
      
                    <select class="formElement"
                      id="assetTypePulldown"
                      name="assetTypePulldown"
                      priority="10"
                      onchange="setSearchValue(
                        document.searchform.assetTypePulldown[ document.searchform.assetTypePulldown.options.selectedIndex ].value, 
                          'searchform', '<c:out value="${searchActionURL}"/>' );">
                    </select>
         
                    <script language="JavaScript">
                      initializeSearchComponentPulldown( 'repositoryPulldown' );
                      initializeSearchTypePulldown();
                    </script>

                  </td>
                </tr>

                <tr>
                  <td>
                  </td>
                  <td>
                    <div id="hierarchyPane" class="hierarchyPaneClosed">
                      <%@ include file="../AssetsPortlet/hierarchyFrame.jspf" %>
                    </div>
                  </td>
                </tr>

                <%-- show hierarchy pane dif if asset type is a file asset type --%>
                <script class="alternateRowHighlight" type="text/javascript">
                  processHierarchyPane(document.getElementById('repositoryPulldown'));
                </script>

                <tr>
                  <td class="formLabel">
                    <fmt:message key="add-keyword" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <dspel:input iclass="formElement formElementInputText"
                      size="40" type="text" priority="10"
                      bean="${assetSearchHandlerPath}.keywordInput"/>
                  </td>
                </tr>
        
                <tr>
                  <td class="formLabel">
                    <fmt:message key="results-per-page" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <dspel:select bean="${assetSearchHandlerPath}.maxResultsPerPage"
                      iclass="formElementSmallSelect">
                      <dspel:option label="10" value="10">10</dspel:option>
                      <dspel:option label="20" value="20">20</dspel:option>
                      <dspel:option label="50" value="50">50</dspel:option>
                      <dspel:option label="100" value="100">100</dspel:option>
                    </dspel:select>
                    <a href="javascript:submitSearch('searchform');" class="goButton" onmouseover="status='';return true;">
                      <fmt:message key="go-button" bundle="${projectsBundle}"/>
                    </a>
                  </td>
                </tr>
              </table>
            </dspel:form>

          </div> <!-- addExisting -->

          <div id="createNew" style="display: none;">
            <%-- 
              Create Form

              Based on the type of asset chosen to create, this form handler will
              invoke another form handler to create the asset.
            --%>
            <c:url var="formActionURL" value="assetBrowser.jsp">
              <c:param name="assetInfo" value="${assetInfoPath}"/>
              <c:param name="iwantto" value="search"/>
            </c:url>
            
            <form id="chooser" action='<c:out value="${formActionURL}"/>' method="post" 
              formid="chooser" name="chooser">
              <table cellpadding="0" cellspacing="3" border="0" style="font-size: 1em;">

                <tr>
                  <td class="formLabel formLabelRequired">
                    <fmt:message key="create-repository" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <select class="formElement" 
                      id="repositoryChoice"
                      name="repositoryChoice"
                      onchange="populateAssetTypeChoice( 
                        document.chooser.repositoryChoice[document.chooser.repositoryChoice.options.selectedIndex].value );">
                    </select>
                  </td>
                </tr>
                <tr>
                  <td class="formLabel formLabelRequired">
                    <fmt:message key="create-asset-type" bundle="${projectsBundle}"/>
                  </td>
                  <td>
                    <select class="formElement" 
                      id="assetTypeChoice"
                      name="assetTypeChoice">
                    </select>
                    <script language="JavaScript">
                      initializeCreateComponentPulldown( 'repositoryChoice' );
                      initializeCreateAssetTypePulldown();
                    </script>
                  </td>
                </tr>
                <tr>
                  <td colspan="2" class="rightAlign">
                    <a href="javascript:submitCreateAsset();" class="goButton" onmouseover="status='';return true;">
                      <fmt:message key="create-add-to-process" bundle="${projectsBundle}"/>
                    </a>
                  </td>
                </tr>
              </table>
            </form>
          </div> <!-- createNew -->
        </div> <!-- assetForm -->

        <div id="hidden" style="display: none;">

          <dspel:importbean var="repositoryFormHandler" 
            bean="/atg/epub/servlet/RepositoryAssetFormHandler"/>

          <%-- Repository Item Create Form Handler --%>
          <c:url var="createFormAction" value="assetBrowser.jsp">
            <c:param name="assetInfo" value="${assetInfoPath}"/>
            <c:param name="wndCmd" value="close"/>
          </c:url>

          <dspel:form action="${createFormAction}" method="post" 
            formid="repositoryCreate" id="repositoryCreate">
            <dspel:input type="hidden" value="${debug}" priority="100"
              bean="RepositoryAssetFormHandler.loggingDebug"/>
            <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
              bean="RepositoryAssetFormHandler.assetInfoPath"/>
            <dspel:input type="hidden" priority="10" id="repositoryContextOp"
              bean="RepositoryAssetFormHandler.contextOp" value="0"/>
            <dspel:input type="hidden" value="false" priority="10" 
              bean="RepositoryAssetFormHandler.requireIdOnCreate"/>
            <dspel:input type="hidden" priority="20" id="repositoryCreateType"
              bean="RepositoryAssetFormHandler.encodedPathAndItemType"/>
            <dspel:input type="hidden" priority="10" id="repositoryAssetDisplayName"
              bean="RepositoryAssetFormHandler.displayName"/>
            <dspel:input type="hidden" bean="RepositoryAssetFormHandler.createTransient" 
              value="1" priority="-10"/>
          </dspel:form>

          <dspel:importbean var="fileFormHandler" 
            bean="/atg/epub/servlet/FileAssetFormHandler"/>

          <%-- File Create Form Handler --%>
          <dspel:form action="${createFormAction}" method="post" 
            formid="fileCreate" id="fileCreate">
            <dspel:input value="${debug}" priority="100"
              bean="FileAssetFormHandler.loggingDebug"/>
            <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
              bean="FileAssetFormHandler.assetInfoPath"/>
            <dspel:input type="hidden" priority="100" id="fileContextOp"
              bean="FileAssetFormHandler.contextOp" value="0"/>
            <dspel:input type="hidden" priority="10" id="fileCreateType"
              bean="FileAssetFormHandler.encodedPathAndItemType"/>
            <dspel:input type="hidden" priority="10" id="fileAssetDisplayName"
              bean="FileAssetFormHandler.displayName"/>
            <dspel:input type="hidden" bean="FileAssetFormHandler.createTransient" 
              value="1" priority="-10"/>

          </dspel:form>

        </div>

        <div class="okAlt">
          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td width="100%">
                &nbsp;
              </td>
              <td class="buttonImage">
                <a href="javascript:parent.hideIframe('assetBrowser')" class="mainContentButton cancel" onmouseover="status='';return true;">
                  <c:choose>
                    <c:when test="${'pick' == assetInfo.attributes.browserMode}">
                      <fmt:message key="cancel-button" bundle="${projectsBundle}"/>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="done-button" bundle="${projectsBundle}"/>
                    </c:otherwise>
                  </c:choose>
                </a>
              </td>
              <td>&nbsp;</td>
            </tr>
          </table>
        </div>  

      </body>
    </html>

    <c:if test="${ 'none' == assetInfo.attributes.assetBrowserCreateMode }">
      <script language="JavaScript">
        showAsset( 'addExisting' );
        hideAsset( 'createNew' );
      </script>
    </c:if>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in asset browser:");
    tt.printStackTrace();
  }
%>
</dspel:page>
<!-- End assetBrowser.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetBrowser.jsp#3 $$Change: 654744 $--%>
