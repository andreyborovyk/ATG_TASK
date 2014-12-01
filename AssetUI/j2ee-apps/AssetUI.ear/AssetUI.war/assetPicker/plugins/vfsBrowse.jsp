<%--
  Display a browsable tree of assets for a given browse mode.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/vfsBrowse.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"           %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"            %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"       %>

<!-- Begin vfsBrowse.jsp -->

<dspel:page>

  <c:set var="debug" value="false"/>
  
  <dspel:importbean var="assetUIConfig"
    bean="/atg/assetui/Configuration"/>
  <dspel:importbean var="webUIConfig"
    bean="/atg/web/Configuration"/>

  <%-- Get the AssetPickerFormHandler --%>
  <c:set var="assetPickerFormHandlerPath" 
     value="/atg/web/assetpicker/servlet/AssetPickerFormHandler"/>
  <dspel:importbean bean="${assetPickerFormHandlerPath}"
    var="assetPickerFormHandler"/>
  
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

  <c:set var="treeComponent" value="${ param.treeComponent }"/>
  <dspel:importbean var="treeState" bean="${treeComponent}"/>

  <c:set var="itemType" value="${assetPickerFormHandler.itemType}"/>
  
  <c:set target="${treeState}" property="addCheckableType" value="${itemType}"/>

  <c:if test="${ ! empty assetInfo.attributes.isAllowMultiSelect }">
    <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
  </c:if>

  <script type="text/javascript" > 
  
    function getSelectedAssets() {
      var selectedResults = new Array(0);
      return selectedResults;
    } // getSelectedAssets
    
    function hello() {
      alert("hello. program me");
    }
    
  </script>
  
  <div id="assetBrowserContentBody">

    <div id="assetListHeader">
      <div id="assetListHeaderRight"></div>
      <div id="assetListHeaderLeft"></div>
    </div>
  
    <form id="treeBrowseForm" name="treeBrowseForm" action="#">
      <c:url var="treeURL" context="/AssetManager" value="/components/amTreeFrame.jsp">
      <%-- <c:url var="treeURL" context="${webUIConfig.contextRoot}" value="/tree/treeFrame.jsp"> --%>
        <c:param name="styleSheet"       value="${assetUIConfig.contextRoot}${assetUIConfig.styleSheet}"/>
        <c:param name="treeComponent"    value="${treeComponent}"/>
        <c:param name="onSelect"         value="hello"/>
        <c:param name="onSelectProperty" value="URI"/>
        <c:param name="nodeIconRoot"     value="${assetUIConfig.contextRoot}${assetUIConfig.imageRoot}"/>
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
  
  <%-- <c:set target="${treeState}" property="removeCheckableType" value="${itemType}"/> --%>
</dspel:page>

<!-- End vfsBrowse.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/vfsBrowse.jsp#2 $$Change: 651448 $--%>
