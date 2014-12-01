<%--
  Asset editor page fragment for the Search Config asset type.
  This fragment adds a button to the button bar at the bottom of the right pane
  which includes, by default, the Save, Review Changes, Preview, etc buttons. 

  This fragment adds a button to navigate directly to the search test area, 
  pre-filling the currently viewed searchConfig into the search test input form. 

  The context and uri are configured into the viewmapping data as attributes on the 
  itemMapping "additionalButtonFragment" and "additionalButtonFragmentContext".  This 
  page is then included by editAsset.jsp depending on those two attributes.     

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/testSearchConfigButton.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="assetMgrConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <c:set var="sessionInfo" value="${assetMgrConfig.sessionInfo}"/>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
  <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- render the button only when we have a non-transient asset --%>
  <c:if test="${empty assetEditor.assetContext.transientAsset}">

    <%-- get the current search config's assetURI --%>
    <c:set var="searchConfigURI" value="${requestScope.atgCurrentAssetURI}"/>

    <%-- render the button which invokes the script below --%>
    <a href="javascript:jumpToTestSearch()" id="testSearchButtonLink"
       class="button"
       title="<fmt:message key='searchTest.testSearchConfig.title'/>" ><span><fmt:message key="searchTest.testSearchConfig"/></span></a>
  

    <%-- Build the right pane URL.  This will be a create new searchTest url with a searchConfig preset --%>
    <%-- Get the create-mode item mapping for SearchTest --%>  
    <c:set var="searchTestContainerPath" value="/atg/search/repository/SearchTestingRepository"/>
    <c:set var="searchTestAssetType" value="searchTest"/>
    <asset-ui:getItemMappingInfo var="searchTestImapInfo"
                                 repositoryPath="${searchTestContainerPath}"
                                 itemDescriptorName="${searchTestAssetType}"
                                 mode="AssetManager.create"
                                 taskConfig="${sessionInfo.taskConfiguration}"/>
    <c:catch var="exception">
      <biz:getItemMapping var="searchTestImap"
                          mappingName="${searchTestImapInfo.name}"
                          itemPath="${searchTestContainerPath}"
                          itemName="${searchTestAssetType}"
                          mode="${searchTestImapInfo.mode}"/>
    </c:catch>
    <c:if test="${not empty exception}">
      <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
    </c:if>
    <%-- Derive a URL for the page that creates a new search test asset --%>
    <c:if test="${not empty searchTestImap}">
      <c:set var="searchTestView" value="${searchTestImap.viewMappings[0]}"/>
      <c:url var="searchTestCreationURL" context="/${searchTestView.contextRoot}" value="${searchTestView.uri}"/>
    </c:if>

    <script type="text/javascript">

      // Navigate to the SearchTest view, with no preloaded Saved Search, but do
      // pre-load the search test inputs with this search config. 
      // Before doing the navigation, prompt the user to save any unsaved data.  
      function jumpToTestSearch() {
        var parentURL = "/AssetManager/assetManager.jsp?browseView=searchTests&&extraParamName=searchConfigId&extraParamValue=<c:out value='${requestScope.formHandler.repositoryItem.repositoryId}'/>";
        parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, parentURL);
      }
    </script>

  </c:if><%-- if not transient --%>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/testSearchConfigButton.jsp#2 $$Change: 651448 $--%>
