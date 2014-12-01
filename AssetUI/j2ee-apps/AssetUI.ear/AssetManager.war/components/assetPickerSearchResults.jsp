<%--
  Asset Picker Search results panel in asset manager UI.

  @param assetInfo   Client path to nucleus AssetInfo context object.
                     Source: client/container

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchResults.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>
  <%--
    Declare any debugging variables
    --%>
  <c:set var="debug" value="${false}"/>

  <%-- Unpack all page parameters and set the page-level or request-level parameters --%>
  <dspel:getvalueof var="assetInfoPath" param="assetInfo"/>

  <%-- set up the asset picker's assetInfo stuff --%>
  <c:if test="${empty assetInfoPath}">
    <c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
  </c:if>

  <%-- import the required beans --%>
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="assetBrowser" bean="/atg/web/assetmanager/list/AssetPickerSearchAssetBrowser"/>

  <%-- Clear the checked nodes every time we load the page.  
       This means that when you change asset picker tabs (e.g. search to browse to search)
       you will lose the state of the checked items. --%> 
  <c:set var="assetPickerTreeComponentName" value="${config.assetPickerTreePath}"/>
  <c:if test="${not empty assetPickerTreeComponentName}">
    <dspel:setvalue bean="${assetPickerTreeComponentName}.checkedNodeIds" value="${null}"/>
  </c:if>
  
  <%-- Get the assetInfo component --%>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

  <%-- If there is a tree view in another tab, we want to set it up here so that
       the form handler knows what tree to operate on if it needs a tree. --%> 
  <c:if test="${ not empty assetInfo.attributes.treeComponent }">
    <dspel:importbean var="sourceTree"
          bean="${ assetInfo.attributes.treeComponent }"/>
    <c:if test="${not empty sourceTree and not empty assetPickerTreeComponentName}">
      <dspel:setvalue bean="${assetPickerTreeComponentName}.globalTree" value="${sourceTree.globalTree}"/>
    </c:if>
  </c:if>

 
  <%--  Set the resource bundle  --%>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get the value of the isAllowMultiSelect property, default to false --%>
  <c:set var="isAllowMultiSelect" value="false"/>
  <c:if test="${ !empty assetInfo.attributes.isAllowMultiSelect }">
    <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
  </c:if>

  <script type="text/javascript" >
    //
    // initialize the assetpicker checkboxes javascript object.
    atg.assetpicker.checkboxes.initialize({
      allowMultiSelect:    <c:out value="${isAllowMultiSelect}"/>,
      checkboxElementName: "resultSetItemCheckbox"
    });
  </script>

  <table id="assetListSubHeader" style="display: none;">
    <tr>
      <td id="assetListSubHeaderLeft">
      </td>
    </tr>
  </table>

  <c:url var="listURL" context="${config.contextRoot}" value="/components/assetPickerSearchResultsList.jsp">
    <c:param name="allowMultiSelect" value="${isAllowMultiSelect}"/>
  </c:url>

  <dspel:iframe id="scrollContainer" name="scrollContainer" frameborder="0" src="${listURL}" style="border: none;"/>

  <dspel:include page="/components/listPagingControl.jsp">
    <dspel:param name="currentPageIndex" value="${1}"/>
    <dspel:param name="itemsPerPage"     value="${assetBrowser.itemsPerPage}"/>
    <dspel:param name="resultSetSize"    value="${assetBrowser.assetCount}"/>
    <dspel:param name="maxResultSetSize" value="${assetBrowser.maxResultSetSize}"/>
    <dspel:param name="listURL"          value="${listURL}"/>
  </dspel:include>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchResults.jsp#2 $$Change: 651448 $ --%>
