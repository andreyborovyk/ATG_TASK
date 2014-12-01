<%--
  Asset picker plugin container page

  @param assetInfo
    Client path to nucleus AssetInfo context object.
    Source: client/container

  Container Javascript methods implemented:
    getAssetType()
      Returns an associative array of the asset type selected in itemTypeSelection.jsp
      consisting of the following keys and values:
        itemType            - asset type
        componentPath       - component path to repository/vfs
        itemDisplayProperty - item's display name property used for sorting
        encodedType         - assetType:componentPath

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/assetPickerContainer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ page import="java.io.*,java.util.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<!-- Begin assetPickerContainer.jsp -->

<dspel:page>

<c:catch var="ex">

  <dspel:importbean var="config" bean="/atg/assetui/Configuration"/>
  <fmt:setBundle var="assetuiBundle" basename="${config.resourceBundle}"/>

  <%@ include file="assetPickerConstants.jsp" %>

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

  <%-- Get relevant goodies from assetInfo --%>

  <%-- browser mode --%>
  <c:if test="${ ! empty assetInfo.attributes.browserMode}">
    <c:set var="browserMode" value="${assetInfo.attributes.browserMode}"/>
  </c:if>

  <%-- dialog type --%>
  <c:if test="${ ! empty assetInfo.attributes.dialogType}">
    <c:set var="dialogType" value="${assetInfo.attributes.dialogType}"/>
  </c:if>

  <%-- map mode --%>
  <c:if test="${ ! empty assetInfo.attributes.mapMode}">
    <c:set var="mapMode" value="${assetInfo.attributes.mapMode}"/>
  </c:if>

  <%-- create mode --%>
  <c:if test="${ ! empty assetInfo.attributes.createMode}">
    <c:set var="createMode" value="${assetInfo.attributes.createMode}"/>
  </c:if>

  <%-- isAllowMultiSelect --%>
  <c:if test="${ ! empty assetInfo.attributes.isAllowMultiSelect }">
    <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
  </c:if>

  <%-- asset component path --%>
  <c:if test="${ ! empty assetInfo.attributes.componentPath}">
    <c:set var="componentPath" value="${assetInfo.attributes.componentPath}"/>
  </c:if>

  <%-- asset item type --%>
  <c:if test="${ ! empty assetInfo.attributes.itemType }">
    <c:set var="itemType" value="${assetInfo.attributes.itemType}"/>
  </c:if>

  <%-- formHandlerPath --%>
  <c:if test="${ ! empty assetInfo.attributes.formHandlerPath }">
    <c:set var="clientDefinedFormHandlerPath" value="${assetInfo.attributes.formHandlerPath}"/>
  </c:if>

  <%-- formHandlerAction --%>
  <c:if test="${ ! empty assetInfo.attributes.formHandlerAction }">
    <c:set var="formHandlerAction" value="${assetInfo.attributes.formHandlerAction}"/>
  </c:if>

  <%-- hideTabs --%>
  <%-- if hideTabs is true, then set the tabstyle to display:none --%>
  <c:set var="tabstyle" value=""/>
  <c:if test="${ assetInfo.attributes.hideTabs }">
    <c:set var="tabstyle" value="display:none"/>
  </c:if>

  <%-- extrafields --%>
  <c:if test="${ ! empty assetInfo.attributes.extraFieldId }">
    <c:set var="extraFieldId" value="${assetInfo.attributes.extraFieldId}"/>
    <c:set var="extraFieldLabel" value="${assetInfo.attributes.extraFieldId}"/>    
    <c:set var="extraFieldValueRequired" value="${assetInfo.attributes.extraFieldValueRequired}"/>    
    <c:set var="extraFieldRequiredMessage" value="${assetInfo.attributes.extraFieldRequiredMessage}"/>    
    <c:if test="${ ! empty assetInfo.attributes.extraFieldLabel }">
      <c:set var="extraFieldLabel" value="${assetInfo.attributes.extraFieldLabel}"/>
    </c:if>
  </c:if>

  <script language="JavaScript">
    //
    // Returns the selected asset type to the plugin
    //
    function getAssetType() {
      var assetType             = new Object();
        assetType.itemType      = "<c:out value='${itemType}'/>";
        assetType.componentPath = "<c:out value='${componentPath}'/>";
        assetType.encodedType   = assetType.componentPath
                                    + ":"
                                    + assetType.itemType;
        assetType.itemDisplayProperty
          = getItemDisplayProperty( assetType.componentPath, assetType.itemType );

        for (var i=0; i<assetTypes.length; i++) {
          if ( assetTypes[i].componentPath == assetType.componentPath
            && assetTypes[i].typeName == assetType.itemType ) {

            assetType.typeCode    = assetTypes[i].typeCode;
            assetType.displayName = assetTypes[i].displayName;
          }
        }

      return assetType;
    }

    //
    // Deprecated
    //
    function getAssetTypes() {
      var itemType = "<c:out value='${itemType}'/>";
      var componentPath = "<c:out value='${componentPath}'/>";
      var encodedType = componentPath + ":" + itemType;

      return encodedType;
    }

    //
    // Testing button
    //
    function testing() {
      var selectedAssets = getSelectedAssets();

      for (var j=0; j<selectedAssets.length; j++) {
        alert( selectedAssets[j].displayName + "; "
          + selectedAssets[j].uri + "; "
          + selectedAssets[j].id);
      }
    }

    // Method called by assetPicker.jsp on page load. Can be used for anything
    // on this page that requires initialization
    function loadAssetPicker() {
       initializeForm();
       fireOnLoad();
    }
  </script>

  <c:if test="${debug}">
    [assetPickerContainer.jsp] itemType=<c:out value="${itemType}"/><br />
    [assetPickerContainer.jsp] componentPath=<c:out value="${componentPath}"/><br />
    [assetPickerContainer.jsp] mapMode=<c:out value="${mapMode}"/><br />
  </c:if>

  <biz:getItemMapping var="imap" itemName="${itemType}" itemPath="${componentPath}" mode="${mapMode}" />

  <c:if test="${debug}">
    [assetPickerContainer.jsp] imap=<c:out value="${imap}"/><br />
  </c:if>

  <div id="includedPage" style="display: none;">
    <c:if test="${ ! empty clientDefinedFormHandlerPath }">
      <dspel:droplet name="/atg/dynamo/droplet/Switch">
        <dspel:param bean="${clientDefinedFormHandlerPath}.formError" name="value"/>
        <dspel:oparam name="true">
          <ul>
          <dspel:droplet name="/atg/dynamo/droplet/ForEach">
            <dspel:param name="array" bean="${clientDefinedFormHandlerPath}.formExceptions"/>
            <dspel:oparam name="output">
              <li><div class="error"><dspel:valueof param="element"/></div>
            </dspel:oparam>
          </dspel:droplet>
          </ul>
        </dspel:oparam>
        <dspel:oparam name="false">
        </dspel:oparam>
      </dspel:droplet>
    </c:if>

    <c:if test="${ !empty imap }">
      <c:set var="viewMappings" value="${imap.viewMappings}"/>

      <c:url var="tabSelectionURL" value="${ ASSET_PICKER_IFRAME_URL }">
        <c:param name="assetInfo" value="${assetInfoPath}"/>
      </c:url>

      <c:url var="backToSelectTypeURL" value="${ ASSET_PICKER_IFRAME_URL }">
        <c:param name="apView"    value="${ AP_ITEM_TYPE_SELECT }"/>
        <c:param name="assetInfo" value="${assetInfoPath}"/>
      </c:url>

      <%-- Get the AssetPickerFormHandler --%>
      <c:set var="assetPickerFormHandlerPath"
         value="/atg/web/assetpicker/servlet/AssetPickerFormHandler"/>
      <dspel:importbean bean="${assetPickerFormHandlerPath}"
        var="assetPickerFormHandler"/>

      <%-- Tab selection form --%>
      <dspel:form formid="tabSelectionForm" name="tabSelectionForm" id="tabSelectionForm" action="${tabSelectionURL}" method="post">

        <%-- extra field --%>
        <c:if test="${ ! empty extraFieldLabel }">
          <div id="assetBrowserField">
            <c:out value="${extraFieldLabel}"/>
            <input type="text" name="<c:out value='${extraFieldId}'/>" id="<c:out value='${extraFieldId}'/>"/>
          </div>
        </c:if>

        <div id="assetBrowserContentTabs" style="<c:out value='${tabstyle}'/>">
          <div id="assetBrowserNav">
            <div id="assetBrowserNavRight">
              <c:if test="${ browserMode != 'pick' }">
                <a href="<c:out value='${ backToSelectTypeURL }'/>">&lt;&lt; <fmt:message key="select-asset-type" bundle="${assetuiBundle}" /></a>
              </c:if>
            </div><!-- id=assetBrowserNavRight -->
            <ul>
              <c:forEach var="viewMapping" items="${viewMappings}" varStatus="i">
                <c:if test="${i.first}">
                  <c:set var="selectedView" value="${viewMapping}" />
                </c:if>
                <%-- Use this view as selected view if this is the only view, or if it is actually selected.
                    This protects against accidentally setting a non-existant view as defaultView.  --%>
                <c:set var="isSelectedView" value="${ (viewMapping.attributes.tabDisplayName eq assetPickerFormHandler.view) or (i.first and i.last) }"/>
                <c:choose>
                  <c:when test="${ isSelectedView }">
                    <c:set var="selectedView" value="${viewMapping}"/>
                    <li class="current">
                      <a href='javascript:submitViewChange( "<c:out value="${viewMapping.attributes.tabDisplayName}"/>" );'>
                        <c:out value="${viewMapping.attributes.tabDisplayName}"/>
                      </a>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li>
                      <a href='javascript:submitViewChange( "<c:out value="${viewMapping.attributes.tabDisplayName}"/>" );'>
                        <c:out value="${viewMapping.attributes.tabDisplayName}"/>
                      </a>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </ul>
          </div><!-- id=assetBrowserNav -->
        </div><!-- id=assetBrowserContentTabs -->
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.view"                value=""
          id="view" name="view" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.componentPath"       value="${componentPath}"
          id="componentPath" name="componentPath" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.itemType"            value="${itemType}"
          id="itemType" name="itemType" />
        <dspel:input type="hidden" priority="-10"
          bean="${assetPickerFormHandlerPath}.configureAssetInfo"  value="1"/>
      </dspel:form><!-- id=tabSelectionForm -->

      <c:url var="itemViewURI" value="${ selectedView.uri }">
        <c:param name="assetInfo" value="${ assetInfoPath }"/>
        <c:param name="isCondensed" value="true"/>
        <c:param name="clientView" value="${ ASSET_PICKER_IFRAME }"/>
        <c:if test="${ ! empty imap.attributes.treeComponent }">
          <c:param name="treeComponent" value="${ imap.attributes.treeComponent.value }"/>
        </c:if>
      </c:url>

      <dspel:include otherContext="${selectedView.contextRoot}" src="${itemViewURI}" />
    </c:if>

    <%-- include the asset browser footer in one single div
         so that resizing of the div above it is easier.
         This div contains the top, left, right part of
         the footer. --%>
    <div id="assetBrowserFooter">

    <div id="assetBrowserFooterTop">
      <c:if test="${ createMode != 'none' }" >
        <div id="multipleCreatableTypes" style="display: none;">
          <fmt:message key="creatable-type" bundle="${assetuiBundle}" />
          <form action="#" id="createForm" name="createForm">
            <select id="encodedPathAndType" name="encodedPathAndType">
              <%-- populated with JavaScript --%>
            </select>
          </form>
          <a href="javascript:submitCreateNewAssetMini();" class="buttonSmall" title="<fmt:message key='create-new-button' bundle='${assetuiBundle}' />">
            <span>
              <fmt:message key='create-new-button' bundle='${assetuiBundle}' />
            </span>
          </a>
        </div>
        <div id="singleCreatableType" style="display: none;">
          <a href="javascript:submitCreateNewAssetMini();" class="buttonSmall" title="<fmt:message key='create-new-button' bundle='${assetuiBundle}' />">
            <span id="singleItemCreateButtonText">
              <fmt:message key='create-new-button' bundle='${assetuiBundle}' />&nbsp;<c:out value="${itemType}" />
            </span>
          </a>
        </div>
      </c:if>
    </div><!-- id=assetBrowserFooterTop -->

    <%-- Button strings depend on dialog type --%>
    <c:choose>
      <c:when test="${dialogType == 'okCancel'}">
        <fmt:message var="addString"   key="ok-button"     bundle="${assetuiBundle}"/>
        <fmt:message var="closeString" key="cancel-button" bundle="${assetuiBundle}"/>
      </c:when>
      <c:otherwise>
        <fmt:message var="addString"   key="add-button"    bundle="${assetuiBundle}"/>
        <fmt:message var="closeString" key="close-button"  bundle="${assetuiBundle}"/>
      </c:otherwise>
    </c:choose>

    <div id="assetBrowserFooterRight">
      <a href="javascript:submitApply();" class="buttonSmall" title="<c:out value='${addString}' />"><span><c:out value='${addString}' /></span></a> 
      <c:if test="${browserMode == 'project'}">
        <a href="javascript:submitDelete();" class="buttonSmall" title="<fmt:message key='delete-button' bundle='${assetuiBundle}' />"><span><fmt:message key='delete-button' bundle='${assetuiBundle}' /></span></a>
      </c:if> <a href="javascript:submitClose();" class="buttonSmall" title="<c:out value='${closeString}' />"><span><c:out value='${closeString}' /></span></a>
    </div>

    <div id="assetBrowserFooterLeft">
      <c:if test="${debug}">
        <a href="javascript:testing();" class="button" title="Test"><span>Test</span></a>
      </c:if>
    </div>

    </div><!-- id=assetBrowserFooter -->

  </div>

  <script language="JavaScript">
    // populate create dropdown
    var k=1;
    var selectedItemType      = "<c:out value='${itemType}' />";
    var selectedComponentPath = "<c:out value='${componentPath}' />";
    var browserMode           = "<c:out value='${browserMode}' />";
    var createMode            = "<c:out value='${createMode}' />";
    var createForm;

    function initializeForm(){
      if ( createMode != "none" ) {
        createForm            = document.createForm;
   
        for (var i=0; i<assetTypes.length; i++) {
          if ( assetTypes[i].createable == "true"
            && browserMode == "pick" ) {
  
            addSelectItem( assetTypes[i], k );
            k++
          }
          else if ( assetTypes[i].createable == "true"
            && assetTypes[i].typeName == selectedItemType
            && assetTypes[i].componentPath == selectedComponentPath
            && browserMode == "project" ) {
  
            addSelectItem( assetTypes[i], k );
            k++;
          }
        }
        if (createForm.encodedPathAndType.options.length == 1) {
          var selectedType = getAssetType();
          if ( selectedType.displayName != null && selectedType.displayName != "" ) {
             var createButtonText = document.getElementById("singleItemCreateButtonText");
             createButtonText.innerHTML  = "<fmt:message key='create-new-button' bundle='${assetuiBundle}' /> " + selectedType.displayName;
          }
          show("singleCreatableType");
        }
        else {
          show("multipleCreatableTypes");
        }
      }
    }

    function addSelectItem( assetType, count ) {
      createForm.encodedPathAndType.options.length = count;
      createForm.encodedPathAndType.options[count-1] =
        new Option( assetType.displayName );
      createForm.encodedPathAndType.options[count-1].value =
        assetType.componentPath
        + "|"
        + assetType.typeName;
    }
  </script>

  <%-- Get the current project --%>
  <pws:getCurrentProject var="projectContext"/>

  <%-- Set the action URL --%>
  <c:url var="projectAction" value="assetPicker.jsp">
    <c:param name="assetInfo" value="${assetInfoPath}"/>
    <c:param name="clearContext" value="y"/>
    <c:param name="clearAttributes" value="y"/>
  </c:url>

  <c:if test="${ ! empty clientDefinedFormHandlerPath }">

    <%-- form --%>
    <dspel:form formid="clientDefinedForm" id="clientDefinedForm" name="clientDefinedForm" method="post"
      action="${projectAction}">

      <dspel:importbean var="clientDefinedFormHandler"
        bean="${clientDefinedFormHandlerPath}"/>
      <dspel:input type="hidden" bean="${clientDefinedFormHandlerPath}.pipeDelimitedAssetURIList"
        name="pipeDelimitedAssetURIList"/>
      <dspel:input type="hidden" bean="${clientDefinedFormHandlerPath}.assetAction"
        name="assetAction" value="1" priority="-1"/>
      <dspel:input type="hidden" bean="${clientDefinedFormHandlerPath}.performAssetAction"
        value="1" priority="-10"/>

      <c:choose>
        <c:when test="${ browserMode == 'project' }">
          <%-- project id --%>
          <dspel:input type="hidden" bean="${clientDefinedFormHandlerPath}.projectId"
            name="projectId" value="${projectContext.project.id}" />
        </c:when>
        <c:otherwise>

        </c:otherwise>
      </c:choose>
    </dspel:form>
  </c:if>

  <script language="JavaScript">
    //
    // Buttons Actions
    //

    //
    // Either invokes form handler if one is defined in
    // AssetBrowser.formHandler, or calls the parent
    // Javascript callback defined in AssetBrowser.onSelect
    //
    function submitApply() {

      var selectedAssets = getSelectedAssets();

      if ( selectedAssets != null && selectedAssets.length > 0 ) {
        var clientDefinedForm = document.clientDefinedForm;

        var assetList = new String();

        for( var i=0; i<selectedAssets.length; i++) {
          assetList += selectedAssets[i].uri + "|";
        }

        // if there's a form handler defined in AssetPicker.formHandler, submit it
        if ( '<c:out value="${clientDefinedFormHandlerPath}"/>' != '' ) {
          clientDefinedForm.pipeDelimitedAssetURIList.value =
            assetList;
          if ( '<c:out value="${formHandlerAction}"/>' != '' ) {
            clientDefinedForm.assetAction.value =
              '<c:out value="${formHandlerAction}"/>';
          }
          clientDefinedForm.submit();
        }
        // otherwise call the callback method defined in AssetPicker.onSelect
        else {
          var callbackContainer;
          if ( picker.callbackContainer ) {
            callbackContainer = picker.assetPickerParentWindow[ picker.callbackContainer ];
          }
          else {
            callbackContainer = picker.assetPickerParentWindow;
          }

          // get the value in the extraField if any and put it in the caller data
          var attrs = "<c:out value='${assetInfo.context.attributes.assetPickerCallerData}'/>";
          <c:if test="${! empty extraFieldId}">
            var extraFieldValue = document.getElementById("<c:out value='${extraFieldId}'/>").value;
            if (extraFieldValue != null) {
              attrs = new Object();
              attrs.<c:out value="${extraFieldId}"/> = extraFieldValue;
            }
            <c:if test="${extraFieldValueRequired}">
              if (extraFieldValue == null || extraFieldValue.replace(/^\s+/g, '') == ''){
                alert('<c:out value="${extraFieldRequiredMessage}"/>');
                return;
              }
            </c:if>
          </c:if>
          // invoke the callback 
          if ( picker.isAllowMultiSelect == "true" ) {
            callbackContainer[ picker.onSelect ]( selectedAssets, attrs );
          }
          else {
            callbackContainer[ picker.onSelect ]( selectedAssets[0], attrs );
          }
          submitClose();
        }
      }
      else {
        alert("<fmt:message key='select-an-asset' bundle='${assetuiBundle}' />");
      }
    }

    //
    // Submit delete asset in project.
    // This should only appear in 'project' mode
    //
    function submitDelete() {

      var selectedAssets = getSelectedAssets();

      if ( selectedAssets != null && selectedAssets.length > 0 ) {
        var clientDefinedForm = document.clientDefinedForm;

        var assetList = new String();

        for( var i=0; i<selectedAssets.length; i++) {
          assetList += selectedAssets[i].uri + "|";
        }

        clientDefinedForm.pipeDelimitedAssetURIList.value =
          assetList;
        clientDefinedForm.assetAction.value =
          '<c:out value="${clientDefinedFormHandler.DEL_ASSET_ACTION}"/>';
        clientDefinedForm.submit();
      }
      else {
        alert("<fmt:message key='select-an-asset' bundle='${assetuiBundle}' />");
      }
    }

    //
    // Submit create new asset from single dropdown
    //
    function submitCreateNewAssetMini() {
      var encodedPathAndType =
        document.createForm.encodedPathAndType.value;

      var typeArray = encodedPathAndType.split("|");

      createNewAsset(typeArray[0], typeArray[1]);
    }

    //
    // Submit view change
    //
    function submitViewChange( selectedTabIndex ) {
      // var tabSelectionForm = document.tabSelectionForm;
      var tabSelectionForm = document.getElementById("tabSelectionForm");
      tabSelectionForm.view.value = selectedTabIndex;
      tabSelectionForm.submit();
    }

    // Display included page
    show("includedPage");
  </script>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in assetPickerContainer.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End assetPickerContainer.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/assetPickerContainer.jsp#2 $$Change: 651448 $--%>

