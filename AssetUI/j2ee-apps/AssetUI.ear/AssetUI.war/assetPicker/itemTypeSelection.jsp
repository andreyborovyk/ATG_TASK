<%--
  AssetBrowser Container

  The following are AssetBrowser parameters which are configured by the 
  calling parent page:

  @param assetPickerTitle
    Optional parameter that allows an asset picker client to define
    the asset picker's title

  @param assetPickerHeader
    Optional parameter that allows an asset picker client to define
    the asset picker's header  
  
  @param clearAttributes
    "true" - To clear assetInfo attributes on asset picker invocation.
    
    "false" - Do not clear assetInfo attributes on asset picker invocation.
              Default

  @param mapMode
    "pick" -  To be passed into the mode attribute of the biz:getItemMapping
              call in assetPickerContainer.jsp to determin the view mapping
              mode
              Default

  @param browserMode - required
    Determines if the AssetBrowser is adding assets to a project
    or if its adding assets as references to existing assets.

    "project" - For adding assets to current project.Default
    
    "pick" - For asset picker mode

  @param dialogType
    Determines the buttons to display at the bottom of the frame.

    "okCancel"     - The buttons are "OK" and "Cancel"
    null (default) - The buttons are "Add", "Delete" (if browserMode is
                     "project"), and "Close"

  @param styleSheet
    URL of alternative style sheet (optional)

  @param headIncludeFile
    The file to be included in the header of asset picker.
    If required specify the context root
    via headIncludeContextRoot. (optional)

  @param headIncludeContextRoot
    The root context of the head include file.
    Used only if headIncludeFile is specified. (optional)

  @param isAllowMultiSelect
    Determines whether the search results allows selection of just one
    or multiple assets. 
    
    "true" - allow multiple assets to be selected. Default
    
    "false" - allow only a single asset to be selected
    
  @param createMode - required
    "redirect" - to create new asset and redirect user to 
    the session scoped parameter 'assetBrowserCreateURL'
    
    "nested" to push current asset on stack and create new
    asset on same page
    
    "none" to disallow creation of assets. Default

  @param closeAction
    Determines what to do with the AssetBrowser and parent window

    "hide" - to hide asset browser
    
    "refresh" - to hide asset browser and refresh parent window. Default

  @param typeCode
    "repository" - Default
    
    "file" - 

  @param assetInfoPath
    Path to AssetInfo nucleus component
    
  @param onSelect
    The JavaScript method name to call when one or mare assets are
    selected. Note that this is the name of the method, not the method
    object. Default is null.
    Used primarily in item reference property editors
    
  @param onHide
    The JavaScript method name to call when the asset picker becomes hidden.
    Used only when closeAction is "hide".  Note that this is the name of the
    method, not the method object.  Default is null.
    Used primarily in item reference property editors
  
  @param onHideData
    An optional string to be passed to the onHide function.

  @param defaultView
    Name of the default view (or tab) to make initially visible. For 
    example, a picker client interested in...
    
  @param formHandlerPath
    Nucleus path to form handler to use to handle asset choices.
    Used instead of a JavaScript callback method

  @param formHandlerAction
    A parameter specified in the asset picker invoking Javascript which
    specifies an optional value for the client defined form handler's
    assetAction property.
  
  @param callerData
    Used by unsupplied assets.

  @param hideTabs
    Used for the Search Merch "Save As" style picker.  True here will hide the Browse and Search tabs.

  @param extraFieldId
    Used for adding an extra text field to the picker.  Used for the Search Merch "Save As" style picker for the name.

  @param extraFieldLabel
    The label for the extraFieldId text field.  Used only if extraFieldId is defined, and the label  
    will be defaulted with that id value if no lable is supplied.

  @param extraFieldValueRequired
    Determines whether the extra field value is required. Used only if extraFieldId is defined. 
    If the value is required a JavaScript validation will be generated on picker. The validation will prevent
    submit with blank extra field and show alert with message defined in extraFieldRequiredMessage attribute.
    "true" - extra field value is required
    "false" - extra field value is optional

  @param extraFieldRequiredMessage
    Alert message which is shown if field value is required and leaved blank.  
    Used only if extraFieldId is defined and field value is required. 

  This page uses the following session scoped variables (whicn are found
  in /atg/web/ATGSession):

  assetBrowserCreateURL  
    A URL to which to redirect the parent page
    (parent.window.location.href) when an asset is created by this
    browser. Only relevant if createMode is 'redirect'. Default
    is empty. 


  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/itemTypeSelection.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="java.io.*,java.util.*,java.util.*" contentType="text/html;charset=UTF-8" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin itemTypeSelection.jsp -->

<dspel:page>

<c:catch var="ex">

  <dspel:importbean var="config" bean="/atg/assetui/Configuration"/>
  <fmt:setBundle var="assetuiBundle" basename="${config.resourceBundle}"/>

  <dspel:importbean var="repoAssetFH" bean="/atg/epub/servlet/RepositoryAssetFormHandler" />
  <c:set var="isFormErrors" value="${repoAssetFH.formError}" />

  <%-- set to true to see lots of debugging information --%>
  <c:set var="debug" value="false" scope="request"/>

  <%-- get ProcessAssetInfo --%>
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
  
  <%-- clearAttributes --%>  
  <c:if test="${ param.clearAttributes eq 'true' }">
    <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
  </c:if>

  <%-- 
    browserMode   

    Don't set browser mode if it doesn't exist in the params
    because it won't if the user comes back to this from the
    search results
  --%>
  <c:if test="${ ! empty param.browserMode }">
    <%-- clear AssetInfo title on asset picker instantiation only --%>
    <c:set target="${assetInfo.attributes}" property="assetPickerTitle" value=""/>
    <c:set target="${assetInfo.attributes}" property="browserMode"
      value="${param.browserMode}"/>
    <c:set var="browserMode" value="${param.browserMode}"/>
  </c:if>

  <%-- dialog type --%>
  <c:if test="${ ! empty param.dialogType }">
    <c:set target="${assetInfo.attributes}" 
      property="dialogType" 
      value="${param.dialogType}"/>
  </c:if>

  <%-- style sheet --%>
  <c:if test="${ ! empty param.styleSheet }">
    <c:set target="${assetInfo.attributes}" 
      property="styleSheet" 
      value="${param.styleSheet}"/>
  </c:if>

  <%-- include file --%>
  <c:if test="${ ! empty param.headIncludeFile }">
    <c:if test="${ ! empty param.headIncludeContextRoot }">
      <c:set target="${assetInfo.attributes}" 
        property="headIncludeContextRoot" 
        value="${param.headIncludeContextRoot}"/>
    </c:if>
    <c:set target="${assetInfo.attributes}" 
      property="headIncludeFile" 
      value="${param.headIncludeFile}"/>
  </c:if>

  <%-- map mode --%>
  <c:if test="${ ! empty param.mapMode }">
    <c:set target="${assetInfo.attributes}" 
      property="mapMode" value="${param.mapMode}"/>
  </c:if>

  <%-- assetPickerTitle --%>
  <c:if test="${ ! empty param.assetPickerTitle }">
    <c:set target="${assetInfo.attributes}" 
      property="assetPickerTitle" value="${param.assetPickerTitle}"/>
  </c:if>

  <%-- assetPickerHeader --%>
  <c:if test="${ ! empty param.assetPickerHeader }">
    <c:set target="${assetInfo.attributes}"
      property="assetPickerHeader" value="${param.assetPickerHeader}"/>
  </c:if>

  <%-- select mode --%>
  <c:if test="${ ! empty param.isAllowMultiSelect }">
    <c:set target="${assetInfo.attributes}" 
      property="isAllowMultiSelect" value="${param.isAllowMultiSelect}"/>
  </c:if>

  <%-- create mode --%>
  <c:if test="${ ! empty param.createMode }">
    <c:set target="${assetInfo.attributes}" 
      property="createMode" value="${param.createMode}"/>
    <c:set var="createMode" value="${param.createMode}"/>      
  </c:if>

  <%-- close action --%>
  <c:if test="${ ! empty param.closeAction }">
    <c:set target="${assetInfo.attributes}" 
      property="closeAction" 
      value="${param.closeAction}"/>
  </c:if>

  <%-- type code --%>
  <c:if test="${ ! empty param.typeCode }">
    <c:set target="${assetInfo.attributes}" 
      property="typeCode" value="${param.typeCode}"/>
  </c:if>

  <%-- on select method --%>
  <c:if test="${ ! empty param.onSelect }">
    <c:set target="${assetInfo.context.attributes}" 
      property="onSelect" 
      value="${param.onSelect}"/>
  </c:if>

  <%-- on hide method --%>
  <c:if test="${ ! empty param.onHide }">
    <c:set target="${assetInfo.context.attributes}" 
      property="onHide" 
      value="${param.onHide}"/>
  </c:if>
 
  <%-- default view --%>
  <c:if test="${ ! empty param.defaultView }">
    <c:set target="${assetInfo.attributes}" 
      property="defaultView" value="${param.defaultView}"/>
  </c:if>
  <c:set var="defaultView" value="${assetInfo.attributes.defaultView}"/>

  <%-- formHandlerPath --%>
  <c:if test="${ ! empty param.formHandler }">
    <c:set target="${assetInfo.attributes}" 
      property="formHandlerPath" value="${param.formHandler}"/>
  </c:if>

  <%-- formHandlerAction --%>
  <c:if test="${ ! empty param.formHandlerAction }">
    <c:set target="${assetInfo.attributes}" 
      property="formHandlerAction" value="${param.formHandlerAction}"/>
  </c:if>

  <%-- caller defined data --%>
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

  <%-- hideTabs --%>
  <%-- if hideTabs is true, then set the tabstyle to display:none --%>
  <c:set target="${assetInfo.attributes}" 
    property="hideTabs" value="false"/>
  <c:if test="${ ! empty param.hideTabs }">
    <c:set target="${assetInfo.attributes}" 
      property="hideTabs" value="${param.hideTabs}"/>
  </c:if>

  <%-- extrafields --%>
  <c:if test="${ ! empty param.extraFieldId }">
    <c:set target="${assetInfo.attributes}" 
      property="extraFieldId" value="${param.extraFieldId}"/>
  </c:if>
  <c:if test="${ ! empty param.extraFieldLabel }">
    <c:set target="${assetInfo.attributes}" 
      property="extraFieldLabel" value="${param.extraFieldLabel}"/>
  </c:if>
  <c:if test="${ ! empty param.extraFieldValueRequired }">
    <c:set target="${assetInfo.attributes}" 
      property="extraFieldValueRequired" value="${param.extraFieldValueRequired}"/>
  </c:if>
  <c:if test="${ ! empty param.extraFieldRequiredMessage}">
    <c:set target="${assetInfo.attributes}" 
      property="extraFieldRequiredMessage" value="${param.extraFieldRequiredMessage}"/>
  </c:if>

  <script language="JavaScript" type="text/javascript">
    //
    // display appropriate div on AssetBrowser startup and
    // populate appropriate select dropdowns
    //
    function loadAssetPicker() {
      fireOnLoad();
      var temp = new Array(0);
      var test = "<c:out value='${param.browserMode}'/>";
      var isFormErrors = "<c:out value='${isFormErrors}'/>";
      
      // populate select dropdowns if assetTypes is > 1
      if ( assetTypes.length > 1 || isFormErrors == "true" ) {
        document.selectionForm.selectedRepository.options.length = 0;
        var j = 1;

        for (var i=0; i < assetTypes.length; i++) {
          if ( temp[ assetTypes[i].componentPath ] != assetTypes[i].componentPath ) {
            document.selectionForm.selectedRepository.options.length = j;
            document.selectionForm.selectedRepository.options[j-1] =
              new Option( assetTypes[i].repositoryName );
            document.selectionForm.selectedRepository.options[j-1].value =
              assetTypes[i].componentPath;
            temp[ assetTypes[i].componentPath ] = assetTypes[i].componentPath;
            j++;
          }
        }
        populateTypePullDown( document.selectionForm.selectedRepository.value );
        show( "assetBrowserContentWrapper" );
      }
      // otherwise, set the componentPath/itemType and go to the
      // search form 
      else if ( assetTypes.length == 1 ){
        
        document.selectionForm.componentPath.value =
          assetTypes[0].componentPath;
        document.selectionForm.itemType.value =
          assetTypes[0].typeName;
        if ( getViewConfiguration() != null )
          document.selectionForm.viewConfiguration.value = 
            getViewConfigurationAsString();
        document.selectionForm.submit();
      }
    }
  
    //
    // Populate the itemType drop down
    //
    function populateTypePullDown( componentPath ) {
    
      var j = 1;
      document.selectionForm.selectedItemType.options.length = 0;
      
      for ( var i=0; i<assetTypes.length; i++) {
        if (assetTypes[i].componentPath == componentPath) {
          document.selectionForm.selectedItemType.options.length = j
          document.selectionForm.selectedItemType.options[j-1] = 
            new Option( assetTypes[i].displayName );
          document.selectionForm.selectedItemType.options[j-1].value = 
            assetTypes[i].typeName;
          j++;
        }
      }
    }

  </script>

    <div id="assetBrowserContentWrapper" style="display: none;">

      <div id="assetListHeader">
        <div id="assetListHeaderRight"></div>
        <div id="assetListHeaderLeft"><fmt:message key="explanation-text" bundle="${assetuiBundle}" /></div>
      </div>

      <div id="scrollContainer">

        <%-- Get the AssetPickerFormHandler --%>
        <c:set var="assetPickerFormHandlerPath" 
           value="/atg/web/assetpicker/servlet/AssetPickerFormHandler"/>
        <dspel:importbean bean="${assetPickerFormHandlerPath}"
          var="assetPickerFormHandler"/>

        <c:url var="assetBrowserURL" value="assetPicker.jsp">
          <c:param name="assetInfo" value="${assetInfoPath}"/>
          <c:param name="apView" value="${ AP_CONTAINER }"/>
        </c:url>

        <%-- Repository/item type selection form --%>
        <dspel:form formid="selectionForm" name="selectionForm" action="${assetBrowserURL}" method="post">
          <table class="formTable">
            <tr>
              <td class="formLabel"><label for="selectedRepository"><fmt:message key="repository" bundle="${assetuiBundle}" /></label></td>
              <td>
                <select name="selectedRepository" id="selectedRepository"
                  onchange="populateTypePullDown(
                    document.selectionForm.selectedRepository[ document.selectionForm.selectedRepository.options.selectedIndex ].value);">
                  <%-- populate options via JavaScript --%>
                </select>
              </td>
            </tr>
            <tr>
              <td class="formLabel"><label for="selectedItemType"><fmt:message key="asset-type" bundle="${assetuiBundle}" /></label></td>
              <td>
                <select name="selectedItemType" id="selectedItemType" onchange="">
                  <%-- populate options via JavaScript --%>
                </select>
              </td>
            </tr>
          </table>

          <div id="formSubmitButtons">
            <c:choose>
              <c:when test="${ 'none' != assetInfo.attributes.createMode }">
                <a href="javascript:submitCreateNewAsset();" class="button" title="<fmt:message key='create-new-button' bundle='${assetuiBundle}' />"><span><fmt:message key="create-new-button" bundle="${assetuiBundle}" /></span></a>&nbsp;<a href="javascript:submitSearchAssets();" class="button" title="<fmt:message key='add-existing-button' bundle='${assetuiBundle}' />"><span><fmt:message key="add-existing-button" bundle="${assetuiBundle}" /></span></a>
              </c:when>
              <c:otherwise>
                <a href="javascript:submitSearchAssets();" class="button" title="<fmt:message key='find-button' bundle='${assetuiBundle}' />"><span><fmt:message key='find-button' bundle='${assetuiBundle}' /></span></a>
              </c:otherwise>
            </c:choose>
          </div>

          <dspel:input type="hidden" priority="1000" value="true"
            bean="${assetPickerFormHandlerPath}.initialize" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.view"
            value="${defaultView}"
            id="view" name="view" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.componentPath"
            id="componentPath" name="componentPath" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.itemType"
            id="itemType" name="itemType" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.viewConfiguration"
            value=""
            id="viewConfiguration" name="viewConfiguration" />
          <dspel:input type="hidden" priority="100"
            bean="${assetPickerFormHandlerPath}.clearSearchResults"
            id="clearSearchResults" name="clearSearchResults" 
            value="true" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.assetInfoPath"
            value="${assetInfoPath}"
            id="assetInfoPath" name="assetInfoPath" />
          <dspel:input type="hidden" priority="-10" value="1"
            bean="${assetPickerFormHandlerPath}.configureAssetInfo"  />
        </dspel:form>

      </div>

      <script language="JavaScript" type="text/javascript">

        var typeCode = null;

        //
        // Create new asset button
        //
        function submitCreateNewAsset() {
          var selectionForm      = document.selectionForm;
          var selectedRepository = selectionForm.selectedRepository.value;
          var selectedItemType   = selectionForm.selectedItemType.value;

          createNewAsset( selectedRepository, selectedItemType );
        }

        //
        // Search existing assets button
        //
        function submitSearchAssets() {

          document.selectionForm.componentPath.value =
            document.selectionForm.selectedRepository.value;
          document.selectionForm.itemType.value =
            document.selectionForm.selectedItemType.value;
          if ( getViewConfiguration() != null )
            document.selectionForm.viewConfiguration.value = 
              getViewConfigurationAsString();

          document.selectionForm.submit();

        }
      </script>

      <table id="scrollFooter">
        <tr>
          <td id="footerCount">
            <div id="assetBrowserFooterCountRight"></div>
            <div id="assetBrowserFooterCountLeft"></div>
          </td>
        </tr>
      </table>

    </div>

    <%-- if we're in pick mode, make two repository drop-down invisible, display this div --%>
    <%-- ...unless there are multiple types that can be selected, then display dropdowns --%>
<%--
    <div name="searchContainer" id="searchContainer" style="display: none;">
      <%@ include file="./searchContainer.jsp" %>
    </div>
--%>

    <div id="assetBrowserFooterRight">
      <%-- <a href="javascript:submitClose();" class="button abTrigger" onclick="parent.showIframe('browser');" title="Cancel">Cancel</a> --%>
    </div>
    <div id="assetBrowserFooterLeft"></div>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in itemTypeSelection.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End itemTypeSelection.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/itemTypeSelection.jsp#2 $$Change: 651448 $--%>
