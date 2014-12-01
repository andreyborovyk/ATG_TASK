<%--
  Browse tab container panel for asset manager UI.
  
  params: 
     browseView    the name of the BrowseViewConfiguration component to switch to

  request scope variables:
     preloadURL    A url that can be set in the assetBrowser.topFragmentPage (see tree.jsp)
                   to be preloaded in to the right pane instead of what the framework would 
                   load by default.

  This page renders a view selector and includes the appropriate page with the appropriate 
  browse component. 

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/browseTab.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="browseView" param="browseView"/>
  <dspel:getvalueof var="assetURI" param="assetURI"/>
  <dspel:getvalueof var="siteId" param="siteId"/>

  <%-- If a browseView parameter was specified, store it as a session variable. --%>
  <c:if test="${not empty param.browseView}">
    <c:set target="${sessionInfo}" property="browseView" value="${param.browseView}"/>
  </c:if>

  <%-- Specify a default value for the browseView variable if necessary. --%>
  <c:if test="${empty sessionInfo.browseView}">
     <c:set target="${sessionInfo}" property="browseView" value="${requestScope.tabConfig.initialView}"/>
  </c:if>

  <c:set target="${sessionInfo}"
         property="assetEditorViewID"
         value="${sessionInfo.browseView}"/>

  <%-- Get the current project (if any) --%>
  <c:set var="projectEditable" value="false"/>
  <pws:getCurrentProject var="projectContext"/>
  <c:if test="${projectContext.project ne null and projectContext.project.editable}">
    <c:set var="projectEditable" value="true"/>
  </c:if>


  <%-- figure out if this page should be readonly --%>
  <c:set var="dataEditable" value="false"/>
  <asset-ui:isDataEditable>
    <c:set var="dataEditable" value="true"/>
  </asset-ui:isDataEditable>


  <table id="contentTable" cellpadding="0" cellspacing="0">
    <tbody>
      <tr>
        <td id="leftPane">
          <%-- Display the correct page for the current view. --%>
          <c:set var="viewConfig" value="${requestScope.tabConfig.views[sessionInfo.browseView]}"/>
          <dspel:include otherContext="${viewConfig.contextRoot}" page="${viewConfig.page}">
            <dspel:param name="projectEditable"  value="${projectEditable}"/>
            <dspel:param name="dataEditable"  value="${dataEditable}"/>
            <dspel:param name="assetURI" value="${assetURI}"/>
            <dspel:param name="siteId" value="${siteId}"/>
          </dspel:include>
        </td>

        <%-- Display the asset editor in the right-pane iframe. --%>
        <td id="rightPane">

          <c:url var="assetEditorURL"
                 value="${requestScope.tabConfig.editorConfiguration.page}">
            <c:if test="${not empty assetURI}">
              <c:param name="assetURI" value="${assetURI}" />
            </c:if>     
          </c:url>       

          <dspel:include page="/components/resizeHandler.jsp">
            <dspel:param name="resizableElementId" value="rightPaneIframe"/>
            <dspel:param name="heightKey"          value="rightPane"/>
          </dspel:include>

          <%-- An included fragment might request that we preload a URL instead of doing
               what the requestScope.tabConfiguration.editorConfiguration wants. --%>
          <c:if test="${not empty requestScope.preloadURL}">
            <c:set var="assetEditorURL" value="${requestScope.preloadURL}"/>
          </c:if>

          <dspel:iframe id="rightPaneIframe"
                        name="rightPaneIframe"
                        iclass="assetEditorFrame"
                        frameborder="0"
                        allowtransparency="true"
                        scrolling="no"
                        src="${assetEditorURL}"
                        style="${requestScope.resizableStyle}"/>
        </td>
      </tr>
    </tbody>
  </table>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/browseTab.jsp#2 $$Change: 651448 $ --%>
