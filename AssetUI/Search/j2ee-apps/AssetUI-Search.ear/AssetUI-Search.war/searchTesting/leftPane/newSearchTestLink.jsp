<%--
  New Search Testing Link that appears between the toolbar and the tree on the left pane.

  params:

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/leftPane/newSearchTestLink.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>

<dspel:page>

  <script type="text/javascript">
    function createSearchTest(url) {
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(url);
    }
  </script>

  <%-- Get the create-mode item mapping for this asset type --%>  
  <c:set var="containerPath" value="/atg/search/repository/SearchTestingRepository"/>
  <c:set var="assetType" value="searchTest"/>
  <asset-ui:getItemMappingInfo var="imapInfo"
                               repositoryPath="${containerPath}"
                               itemDescriptorName="${assetType}"
                               mode="AssetManager.create"
                               taskConfig="${requestScope.managerConfig}"/>

  <c:catch var="exception">
    <biz:getItemMapping var="imap"
                        mappingName="${imapInfo.name}"
                        itemPath="${containerPath}"
                        itemName="${assetType}"
                        mode="${imapInfo.mode}"/>
  </c:catch>
  <c:if test="${not empty exception}">
    <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
  </c:if>
  <%-- Derive a URL for the page that creates a new asset for this type --%>
  <c:if test="${not empty imap}">
    <c:set var="view" value="${imap.viewMappings[0]}"/>
    <c:url var="creationURL" context="/${view.contextRoot}" value="${view.uri}">
      <c:param name="containerPath" value="${containerPath}"/>
      <c:param name="assetType" value="${assetType}"/>
    </c:url>
    <a href="javascript:createSearchTest('<c:out value="${creationURL}"/>')" 
       class="atg_smerch_newSearchTest">New Search Test</a>
  </c:if>


  <%-- begin: Custom Right Pane behavior --%>
  <%-- If no asset is selected, then we want to automatically load a new empty search test
       into the right pane.  Do this by setting the preloadURL to the creation new search 
       test url. --%>
  <%-- If we came from the Test Search button on a searchConfig asset, then we want to preload
       an empty search test in the right pane, just like no asset is selected.  But we also want
       to preload the searchConfigId into the search test.  Do this by passing through the 
       extraParamName and extraParamValue that the Test Search button has sent to us. --%>
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>

  <c:choose>
    <c:when test="${not empty param.extraParamName}">
      <c:url var="creationURL" context="/${view.contextRoot}" value="${view.uri}">
        <c:param name="containerPath" value="${containerPath}"/>
        <c:param name="assetType" value="${assetType}"/>
        <c:param name="extraParamName" value="${param.extraParamName}"/>
        <c:param name="extraParamValue" value="${param.extraParamValue}"/>
      </c:url>
      <c:set scope="request" var="preloadURL" value="${creationURL}"/>
    </c:when> 
    <c:when test="${empty assetEditor.assetContext.assetURI}">
      <c:set scope="request" var="preloadURL" value="${creationURL}"/>
    </c:when>
  </c:choose>
  <%-- end: Custom Right Pane behavior --%>


</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/leftPane/newSearchTestLink.jsp#2 $$Change: 651448 $--%>
