<%--
  Pageable list fragment for insertion into pages that have the following characteristics

  taglibs: 
     c
     dsp
     web-ui

  page elements : 
     rightPaneIframe   an iframe in which the selected asset details can be displayed 

  page variables : 
     enableFilter   Whether to enable filtering of the list.
     project        the current project
     config         nucleus component for /atg/web/assetmanager/ConfigurationInfo
     showAssetStatusInProject  whether to show each asset's status in the project
     showMultiEditStatus  whether to show each asset's status in the multiedit
     doUpToDateCheck           whether to force recalculation of list if assets have changed since last rendering
     shouldRenderCheckboxes    whether to show a checkbox next to each item
     emptyListMessage the message to display when the list is empty.
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/pageableList.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <c:set var="debug" value="true"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="showAssetStatusInProject" param="showAssetStatusInProject"/>
  <dspel:getvalueof var="showMultiEditStatus"      param="showMultiEditStatus"/>
  <dspel:getvalueof var="shouldRenderCheckboxes"   param="shouldRenderCheckboxes"/>
  <dspel:getvalueof var="shouldSort"               param="shouldSort"/>
  <dspel:getvalueof var="scrollContainerHeightKey" param="scrollContainerHeightKey"/>
  <dspel:getvalueof var="enableFilter"             param="enableFilter"/>
  <dspel:getvalueof var="emptyListMessage"         param="emptyListMessage"/>

  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>

  <c:set var="sessionInfoName" value="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="sessionInfo" bean="${sessionInfoName}"/>

  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>
  
  <c:set var="listPageNumber" value="0"/>
  <c:if test="${param.listPageNumber ne null}">
    <c:set var="listPageNumber" value="${param.listPageNumber}"/>
  </c:if>

  <%-- We want to get the asset being viewed in the right pane so we can preselect it.  
       To do so, get the assetEditor for the current tab --%>
  <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>

  <%-- include javascript methods for asset selection & highlighting --%>
  <dspel:include page="/components/selectionManagement.jsp">
    <dspel:param name="sessionInfoName" value="${sessionInfoName}"/>
    <dspel:param name="assetEditorURL"  value="${assetEditorURL}"/>
  </dspel:include>

  <%-- Display the list inside a scrolling subwindow --%>
  <c:url var="listURL" value="/components/list.jsp">
    <c:param name="showAssetStatusInProject" value="${showAssetStatusInProject}"/>
    <c:param name="showMultiEditStatus"      value="${showMultiEditStatus}"/>
    <c:param name="shouldRenderCheckboxes"   value="${shouldRenderCheckboxes}"/>
    <c:param name="shouldSort"               value="${shouldSort}"/>
    <c:param name="emptyListMessage"         value="${emptyListMessage}"/>
  </c:url>

  <c:set scope="request" var="resizableStyle" value="border: none;"/>

  <dspel:include page="/components/resizeHandler.jsp">
    <dspel:param name="resizableElementId"      value="scrollContainer"/>
    <dspel:param name="extraVerticalElementIds" value="scrollFooter"/>
    <dspel:param name="heightKey"               value="${scrollContainerHeightKey}"/>
  </dspel:include>

  <c:if test="${enableFilter}">
    <div id="filterBy">
          <span class="filterLabel"><fmt:message key="common.filterByNameLabel"/>:</span> <input name="listfilter" id="filterField" type="text" size="8" value="<c:out value='${sessionInfo.currentFilterString}'/>" onkeypress="return onFilterFieldKeyPress(event)"/>
          <fmt:message var="gotext" key="common.go"/><img src="images/button_filter.gif" class="filterButton" onclick="saveAndDoFilter()" />
    </div>
  </c:if>

  <dspel:iframe id="scrollContainer" name="scrollContainer" frameborder="0" src="${listURL}" style="${requestScope.resizableStyle}"/>

  <%-- list paging --%>
  <dspel:include page="/components/listPagingControl.jsp">
    <dspel:param name="currentPageIndex" value="${sessionInfo.currentAssetBrowser.currentPageIndex + 1}"/>
    <dspel:param name="itemsPerPage"     value="${sessionInfo.currentAssetBrowser.itemsPerPage}"/>
    <dspel:param name="resultSetSize"    value="${sessionInfo.currentAssetBrowser.assetCount}"/>
    <dspel:param name="maxResultSetSize" value="${sessionInfo.currentAssetBrowser.maxResultSetSize}"/>
    <dspel:param name="listURL"          value="${listURL}"/>
  </dspel:include>

  <script type="text/javascript">
    <c:if test="${enableFilter}">

      function onFilterFieldKeyPress(evt) {
        // Determine which key was pressed.
        if (evt == null) evt = event;
        var charCode = evt.charCode;
        if (charCode == null || charCode == 0) charCode = evt.keyCode;
        if (charCode == null) charCode = evt.which;

        if (charCode == 13 || charCode == 3) {
          // Enter key was pressed.
          // Do something with the current contents of the text field.
          saveAndDoFilter();
          return false;
        }
        return true;
      }

      function saveAndDoFilter() {
        var filterValueElement = document.getElementById("filterField");
        var filterValue = encodeURIComponent(filterValueElement.value);
        atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, doFilter, filterValue, "doFilter");
      }

      function doFilter(filterValue) {
        updatePage(true, filterValue);
      }

    </c:if>

    //
    // refresh the page to pick up latest data
    //
    function refresh() {
      updatePage(false, null);
    }

    //
    // Refresh the list pane and the paging controls, updating the filter
    // string if requested.
    //
    function updatePage(includeFilter, filterValue) {
      var listPage = "<c:out value='${listURL}' escapeXml='false'/>&updatePager=1&resetList=1";
      if (includeFilter)
        listPage += "&filterString=" + filterValue;
      var subDocument = document.getElementById("scrollContainer").contentWindow.document;
      subDocument.location = listPage;
      if (filterValue != null) {
        var rightURL = "<c:out value='${assetEditorURL}'/>?assetURI=null&clearContext=true";
        top.window.frames['rightPaneIframe'].location = rightURL;
      }
    }

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/pageableList.jsp#2 $$Change: 651448 $--%>
