<%--

  params:
      shouldRenderCheckboxes
      shouldSort
      showAssetStatusInProject
      showMultiEditStatus
      filterString
      emptyListMessage

  This page renders a list of assets in sessionInfo.assets according to the params passed in.
  list component.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/list.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="listPageNumber" param="listPageNumber"/>
  <dspel:getvalueof var="shouldRenderCheckboxes" param="shouldRenderCheckboxes"/>
  <dspel:getvalueof var="shouldSort" param="shouldSort"/>
  <dspel:getvalueof var="showAssetStatusInProject" param="showAssetStatusInProject"/>
  <dspel:getvalueof var="showMultiEditStatus" param="showMultiEditStatus"/>
  <dspel:getvalueof var="filterStringParam" param="filterString"/>
  <dspel:getvalueof var="updatePager" param="updatePager"/>
  <dspel:getvalueof var="resetList" param="resetList"/>
  <dspel:getvalueof var="emptyListMessage" param="emptyListMessage"/>

  <c:set var="formHandlerPath" value="/atg/web/assetmanager/action/ListActionFormHandler"/>
  <dspel:importbean var="preferences"
                    bean="/atg/web/assetmanager/Preferences"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>
  <c:set var="assetBrowser" value="${sessionInfo.currentAssetBrowser}"/>

  <%-- If resetList=1 that means that the data in the list could be stale.   
       We need to reset the data without disrupting the current page number. --%>
  <c:if test="${not empty resetList}">
    <c:set var="currentPageIndex" value="${assetBrowser.currentPageIndex}"/>
    <web-ui:invoke var="nothing"
                   bean="${assetBrowser}"
                   method="reset"/>
    <c:set target="${assetBrowser}" property="currentPageIndex" value="${currentPageIndex}"/>
  </c:if>

  <%-- If requested, update the page number in the current asset browser --%>
  <c:if test="${not empty listPageNumber}">
    <c:set target="${assetBrowser}" property="currentPageIndex" value="${listPageNumber - 1}"/>
  </c:if>

  <%-- If requested, update the session info's filter string for the current view --%>
  <c:if test="${filterStringParam ne null}">
    <c:set target="${sessionInfo}" property="currentFilterString" value="${filterStringParam}"/>
  </c:if>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- We want to get the asset being viewed in the right pane so we can preselect it.
       To do so, get the assetEditor for the current tab --%>
  <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>

  <pws:getCurrentProject var="projectContext"/>
  <c:set var="project" value="${projectContext.project}"/>

  <%-- Get all of the assets for the current page.  If the asset count or page
       number changes as a result of a cache refresh, indicate that we also need
       to update the paging controls. --%>
  <c:set var="assetCount" value="${assetBrowser.assetCount}"/>
  <c:set var="currentPageIndex" value="${assetBrowser.currentPageIndex}"/>
  <c:set var="assets" value="${assetBrowser.currentPageAssets}"/>
  <c:if test="${assetBrowser.currentPageIndex != currentPageIndex or
                assetBrowser.assetCount != assetCount}">
    <c:set var="updatePager" value="true"/>
  </c:if>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <script type="text/javascript">
          function atgListLoaded() {
            parent.stopWait();
            parent.hideLeftLoadingIcon();
          }
        </script>

      <title></title>
      <dspel:include page="/components/head.jsp"/>
    </head>

    <body style="background: #fff" onload="atgListLoaded()">

      <%-- loading icon --%>
      <div id="screen"></div>

        <c:choose>
          <c:when test="${assetCount <= 0 and not empty emptyListMessage}">
            <div style="text-align: left">
              <c:out value="${emptyListMessage}"/>
            </div>
          </c:when>
          <c:otherwise>
      <table id="assetListContentTable" cellpadding="0" cellspacing="0">

        <%-- Loop through all of the assets on the current page --%>
        <c:forEach var="assetWrapper" items="${assets}">

          <%-- Get some information about the state of the WorkingVersion --%>
          <web-ui:isAssignableFrom var="isVersionedAssetWrapper"
            className="${assetWrapper.class.name}" instanceOfClassName="atg.service.asset.VersionedAssetWrapper"/>
          <c:set var="added" value="false"/>
          <c:set var="deleted" value="false"/>
          <c:set var="upToDate" value="true"/>
          <c:if test="${isVersionedAssetWrapper}">
            <c:set var="added" value="${assetWrapper.added}"/>
            <c:set var="deleted" value="${assetWrapper.deleted}"/>
            <c:set var="upToDate" value="${assetWrapper.upToDate}"/>
          </c:if>

          <c:set var="assetURI" value="${assetWrapper.uri}"/>

          <%-- Highlight table row if in conflict --%>
          <c:choose>
            <c:when test="${not upToDate and showAssetStatusInProject}">
              <tr style="background: #ffd283">
            </c:when>
            <c:otherwise>
              <tr>
            </c:otherwise>
          </c:choose>

          <%-- multi edit status --%>
          <c:if test="${showMultiEditStatus}">
            <c:set var="successItems" value="${multiEditSessionInfo.multiEditSuccessList}"/>
            <dspel:contains var="multiEditSuccess"
                            values="${successItems}"
                            object="${assetURI}"/>
            <c:set var="errorItems" value="${multiEditSessionInfo.multiEditErrorList}"/>
            <dspel:contains var="multiEditError"
                            values="${errorItems}"
                            object="${assetURI}"/>
            <c:set var="stoppedItems" value="${multiEditSessionInfo.multiEditStoppedList}"/>
            <dspel:contains var="multiEditStopped"
                            values="${stoppedItems}"
                            object="${assetURI}"/>
            <c:choose>
              <c:when test="${multiEditSuccess}">
                <td class="success"> </td>
              </c:when>
              <c:when test="${multiEditError}">
                <td class="error"> </td>
              </c:when>
              <c:when test="${multiEditStopped}">
                <td class="stopped"> </td>
              </c:when>
              <c:otherwise>
                <td class="emptySelectCell"> </td>
              </c:otherwise>
            </c:choose>
          </c:if>

          <td class="checkboxCell">
            <c:if test="${shouldRenderCheckboxes}">
            <web-ui:invoke var="isItemChecked"
                           bean="${assetBrowser}"
                           method="isItemChecked">
              <web-ui:parameter value="${assetURI}"/>
            </web-ui:invoke>

              <c:choose>
                <c:when test="${isItemChecked}">
                  <c:set var="checkedAttribute" value="checked"/>
                </c:when>
                <c:otherwise>
                  <c:set var="checkedAttribute" value=""/>
                </c:otherwise>
              </c:choose>
              <c:set var="checkboxFunction" value="atg.assetmanager.checkboxes.checkItem('${assetURI}')"/>
              <input id="checkbox_<c:out value='${assetURI}'/>"
                     type="checkbox" name="assetCheckboxElement"
                     bean="<c:out value='${formHandlerPath}'/>.assets"
                     value="<c:out value='${assetURI}'/>"
                     onclick="parent.<c:out value='${checkboxFunction}' escapeXml='false'/>"
                     <c:out value="${checkedAttribute}"/>/>
            </c:if>
          </td>
          <c:choose>
            <c:when test="${assetURI eq assetEditor.assetContext.assetURI}">
              <c:set var="selectedStyle" value="selected"/>
            </c:when>
            <c:otherwise>
              <c:set var="selectedStyle" value=""/>
            </c:otherwise>
          </c:choose>

          <%-- Find any custom styles that need to be applied to this table cell --%>
          <web-ui:invoke var="customStyle"
                         bean="${assetBrowser.styleFinder}"
                         method="getStyle">
            <web-ui:parameter value="${assetWrapper}"/>
            <web-ui:parameter value="StyleFinder_listCellClass"/>
          </web-ui:invoke>

          <td class="nameCell <c:out value='${selectedStyle}'/> <c:out value='${customStyle}'/>"
              id="<c:out value='nameCell_${assetURI}'/>">

            <%-- Determine the asset icon --%>
            <web-ui:getIcon var="iconUrl"
                            container="${assetWrapper.containerPath}"
                            type="${assetWrapper.type}"
                            assetFamily="RepositoryItem"/>

            <%-- Link to asset details unless asset is deleted, then just show name --%>
            <c:set var="tooltip" value=""/>
            <c:if test="${preferences.showIdToolTips}">
              <c:set var="tooltip" value="${assetWrapper.id}"/>
            </c:if>
            <c:choose>
              <c:when test="${deleted and showAssetStatusInProject}">
                <a id="<c:out value='nameLink_${assetURI}'/>"
                   class="deletedAsset"
                   title="<c:out value='${tooltip}'/>">
                <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
                <del><c:out value="${assetWrapper.displayName}"/></del>
              </c:when>
              <c:when test="${added and showAssetStatusInProject}">
                <a id="<c:out value='nameLink_${assetURI}'/>"
                   class="<c:out value='${selectedStyle}'/>"
                   onclick="<c:out value='parent.editAsset("${assetURI}")'/>"
                   title="<c:out value='${tooltip}'/>">
                <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
                  <c:out value="${assetWrapper.displayName}"/> <b><fmt:message key="assetList.newAsset"/></b>
                </a>
              </c:when>
              <c:otherwise>
                <a id="<c:out value='nameLink_${assetURI}'/>"
                   class="<c:out value='${selectedStyle}'/>"
                   onclick="<c:out value='parent.editAsset("${assetURI}")'/>"
                   title="<c:out value='${tooltip}'/>">
                <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
                  <c:out value="${assetWrapper.displayName}"/>
                </a>
              </c:otherwise>
            </c:choose>
          </td></tr>
        </c:forEach>
      </table>
          </c:otherwise>
        </c:choose>

      <%-- If requested, update the list paging controls in our parent window --%>
      <c:if test="${not empty updatePager}">
        <script type="text/javascript">
          if (parent.listPager && parent.listPager.updatePager)
            parent.listPager.updatePager(<c:out value="${assetBrowser.assetCount}"/>, <c:out value="${assetBrowser.currentPageIndex}"/>);
        </script>      
      </c:if>

      <script type="text/javascript">
        if (parent.selectFirstAsset) {
          parent.editFirstAsset();
          parent.selectFirstAsset = false;
        }
        if (parent.clearSelectedAsset) {
          parent.clearCurrentSelection();
          parent.clearSelectedAsset = false;
        }

      </script>

    </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/list.jsp#2 $$Change: 651448 $ --%>
