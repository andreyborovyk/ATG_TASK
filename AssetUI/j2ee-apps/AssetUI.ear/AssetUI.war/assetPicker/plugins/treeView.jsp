<%--
  Display a browsable tree of assets for a given browse mode.

  @param assetInfo
    Client path to nucleus AssetInfo context object. 
    Source: client/container

  @param clientView
    Constant which determines the client URL (see: atg.web.assetpicker.Constants
    Source: client/container
    
  @param treeComponent
    Nucleus path to the TreeState component which represents the tree displayed
    in this treeView.jsp. This component is reponsible for feeding the tree jsp
    pages (included from WebUI) the appropriate data it needs to build the tree
    view.
    Source: container/view mapping

  Javascript methods implemented:
    getSelectedAssets()
      Returns an array of of associative arrays, each consisting of the
      following keys and values:
        id          - the asset's ID
        displayName - the asset's display name
        uri         - the asset's version manager URI

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/treeView.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"           %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"            %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"       %>

<!-- Begin treeView.jsp -->

<dspel:page>

  <%@ include file="/assetPicker/assetPickerConstants.jsp" %>

  <c:set var="debug" value="false"/>
  
  <dspel:importbean var="assetUIConfig" bean="/atg/assetui/Configuration"/>
  <dspel:importbean var="webUIConfig"   bean="/atg/web/Configuration"/>
  <dspel:importbean var="bizUIConfig"   bean="/atg/bizui/Configuration"/>
  <fmt:setBundle    var="assetuiBundle" basename="${assetUIConfig.resourceBundle}"/>
  
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

  
  <c:set var="assetBrowserTreeComponentName" value="/atg/web/assetpicker/browse/AssetPickerTreeState"/>
  <dspel:importbean var="assetPickerTree"    bean="${assetBrowserTreeComponentName}"/>

  <c:choose>
    <c:when test="${ ! empty assetInfo.attributes.treeComponent }">
      <dspel:importbean var="sourceTree" 
                        bean="${ assetInfo.attributes.treeComponent }"/> 
    </c:when>
    <c:otherwise>
      <dspel:importbean var="sourceTree" 
                        bean="${ param.treeComponent }"/> 
    </c:otherwise>
  </c:choose>

  <%-- set up the AssetBrowserTreeState component based on the treeComponent passed in --%> 
  <c:set target="${assetPickerTree}" property="globalTree" value="${sourceTree.globalTree}"/>
  <c:set target="${assetPickerTree}" property="selectedNodeId" value="${sourceTree.selectedNodeId}"/>
  <c:set target="${assetPickerTree}" property="openNodeIds" value="${sourceTree.openNodeIds}"/>
  <c:set target="${assetPickerTree}" property="checkedNodeIdsString" value="${null}"/>

  <%--
  <c:set var="treeComponent" value="${ param.treeComponent }"/>
  <dspel:importbean var="treeState" bean="${treeComponent}"/>
  --%>
  
  <c:choose>
    <c:when test="${ 'pick' != assetInfo.attributes.browserMode }">
      <c:set target="${assetPickerTree}" property="unsetProjectContext" value="true" />
    </c:when>
    <c:otherwise>
      <c:set target="${assetPickerTree}" property="unsetProjectContext" value="false" />
    </c:otherwise>
  </c:choose>

  <%-- reset TreeState to default --%>
  <c:set var="resetToDefault"  value="${assetPickerTree.resetToDefault}"/>

  <%-- get the itemType to be selectable in the tree view --%>
  <c:set var="itemType" value="${assetInfo.attributes.itemType}"/>
  
  <%-- make the selected itemType selectable in the tree --%>
  <c:set target="${assetPickerTree}" property="addCheckableType" value="${itemType}"/>
  <c:set target="${assetPickerTree}" property="addSelectableType" value="${itemType}"/>

  <%-- determines radio button vs. checkbox --%>
  <c:if test="${ ! empty assetInfo.attributes.isAllowMultiSelect }">
    <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
  </c:if>
  
  <%-- calling client URL --%>
  <c:choose>
    <c:when test="${ param.clientView eq ASSET_PICKER_IFRAME }">
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PICKER_IFRAME_URL }"/> 
    </c:when>
    <c:when test="${ param.clientView eq BROWSE_TAB }">
      <c:set var="clientURL" value="${ BROWSE_TAB_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PORTLET_URL }"/> 
    </c:when>
    <c:otherwise>
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PICKER_IFRAME_URL }"/> 
    </c:otherwise>
  </c:choose>

  <%-- assetURI is appended via Javascript --%>
  <c:choose>
    <c:when test="${ !empty param.bccContext }">
      <c:url var="assetDetailURL" context="${param.bccContext}" value="${ assetURL }">
      <c:choose>
        <c:when test="${ param.isCondensed }">
          <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
        </c:when>
       <c:otherwise>
          <c:param name="assetView" value="${ ASSET_DETAILS }"/>
        </c:otherwise>
      </c:choose>
      <c:param name="assetInfo"       value="${assetInfoPath}"/>
      <c:param name="clearContext"    value="true"/>
      <c:param name="clearAttributes" value="true"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url var="assetDetailURL" value="${ assetURL }">
      <c:choose>
        <c:when test="${ param.isCondensed }">
          <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
        </c:when>
        <c:otherwise>
          <c:param name="assetView" value="${ ASSET_DETAILS }"/>
        </c:otherwise>
      </c:choose>
      <c:param name="assetInfo"       value="${assetInfoPath}"/>
      <c:param name="clearContext"    value="true"/>
      <c:param name="clearAttributes" value="true"/>
      </c:url>
    </c:otherwise>
  </c:choose>




  <%-- this is a dummy form used only to redirect the asset picker to the asset details URL --%>
  <dspel:form id="assetDetailsForm" name="assetDetailsForm" action="${ assetDetailURL }" method="post">
  </dspel:form>

  <script type="text/javascript" > 
    //
    // checkedItems - array of checkedItem objects
    //
    var checkedItems = new Array();

    // 
    // this is invoked when a tree node is checked or unchecked
    //
    function checkNode(path, info) {
      // if the node is checked, we are unchecking it, so search for the item
      var newCheckedItems = new Array(0);
      var found = false;
      
      for ( var i=0; i<checkedItems.length; i++ ) {
        if ( checkedItems[i].uri == info.URI ) {
          found = true;
        }
        else {
          newCheckedItems[newCheckedItems.length] = checkedItems[i];
        }
      }
      
      if ( found ) {
        checkedItems = newCheckedItems;
        return;
      }
      
      var selectedAsset = getAssetType();

      // item was not found among the checked nodes. i guess we are checking now. 
      var checkedItem = new Object();
      checkedItem.uri = info.URI;
      
      if ( selectedAsset.typeCode == "file" ) {
        checkedItem.id          = info.itemPath;
        checkedItem.displayName = info.itemPath;
      }
      else {
        checkedItem.id          = info.id;
        checkedItem.displayName = info.label;
      }

      <c:choose>
        <c:when test="${ isAllowMultiSelect }">
          checkedItems[checkedItems.length] = checkedItem;
        </c:when>
        <c:otherwise>
          checkedItems[0] = checkedItem;
        </c:otherwise>
      </c:choose>
    }

    //
    // Returns an array of associative arrays, each consisting of the
    // following keys and values:
    //   id          - the selected assets' ID
    //   displayName - the selected assets' display name
    //   uri         - the selected assets' version manager URI
    //
    function getSelectedAssets() {
      return checkedItems;
    } // getSelectedAssets
    
    //
    // Redirects the asset picker to the asset details page based on
    // the selected asset
    //
    function displayAssetDetails( dummy, info ) {
      var clickedItem = new Object();
      clickedItem.uri         = info.URI;

      var assetDetailsForm = document.assetDetailsForm;

      // append the assetURI to the action URL
      var actionURL        = assetDetailsForm.action;
      actionURL            = actionURL + "&assetURI=" + clickedItem.uri;

      assetDetailsForm.action = actionURL;
      assetDetailsForm.submit();
    }
  </script>
  
  <div id="assetBrowserContentBody">

    <div id="assetListHeader">
      <div id="assetListHeaderRightRefresh">
        <c:if test="${param.showRefresh ne null}">
          <span class="label"><a href='javascript:refreshCurrentView();' >Refresh results</a></span>
        </c:if>
      </div>
      <div id="assetListHeaderLeft">
        <fmt:message key='browsing-for' bundle='${assetuiBundle}' /> <strong><c:out value="${itemType}"/></strong>
      </div>
    </div>
  
    <form id="treeBrowseForm" name="treeBrowseForm" action="#">
      <c:url var="treeURL" context="/AssetManager" value="/components/amTreeFrame.jsp">
      <%-- <c:url var="treeURL" context="${webUIConfig.contextRoot}" value="/tree/treeFrame.jsp"> --%>
        <c:param name="styleSheet"         value="${assetUIConfig.contextRoot}${assetUIConfig.styleSheet}"/>
        <c:param name="treeComponent"      value="${assetBrowserTreeComponentName}"/>
        <c:param name="onSelect"           value="displayAssetDetails"/>
        <c:param name="onSelectProperties" value="URI"/>
        <c:param name="nodeIconRoot"       value="${assetUIConfig.contextRoot}"/>
        <c:param name="onCheck"            value="checkNode"/>
        <c:param name="onUncheck"          value="checkNode"/>
        <c:param name="onCheckProperties"  value="id,label,URI,itemPath"/>
        <c:choose>
          <c:when test="${ isAllowMultiSelect }">
            <c:param name="selectorControl" value="checkbox"/>
          </c:when>
          <c:otherwise>
            <c:param name="selectorControl" value="radio"/>
          </c:otherwise>
        </c:choose>
      </c:url>
      <dspel:iframe id="scrollContainer" src="${treeURL}" style="border: none"/>
    </form>

    <table id="scrollFooter">
      <tr>
        <td id="footerCount">
          <div id="footerCountRight"></div>
          <div id="footerCountLeft"></div>
        </td>
      </tr>
    </table>
  </div>
  
</dspel:page>

<!-- End treeView.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/treeView.jsp#2 $$Change: 651448 $--%>
