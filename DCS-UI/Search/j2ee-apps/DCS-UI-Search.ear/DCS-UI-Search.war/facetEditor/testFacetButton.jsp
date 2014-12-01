<%--
  Asset editor page fragment for the Facet asset type.
  This fragment adds a button to the button bar at the bottom of the right pane
  which includes, by default, the Save, Review Changes, Preview, etc buttons. 

  This fragment adds a button to navigate directly to the search test area. 

  The context and uri are configured into the viewmapping data as attributes on the 
  itemMapping "additionalButtonFragment" and "additionalButtonFragmentContext".  This 
  page is then included by editAsset.jsp depending on those two attributes.     

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/testFacetButton.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="assetMgrConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="config" bean="/atg/commerce/search/web/Configuration"/>
  <c:set var="sessionInfo" value="${assetMgrConfig.sessionInfo}"/>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
  <dspel:importbean var="assetEditor" bean="${assetEditorPath}" />
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- render the button only when we have a non-transient asset --%>
  <c:if test="${empty assetEditor.assetContext.transientAsset}">

    <%-- render the button which invokes the script below --%>
    <a href="javascript:jumpToTestSearch()" id="testSearchButtonLink"
       class="button"
       title="<fmt:message key='testFacetButton.title'/>" ><span><fmt:message key="testFacetButton.label"/></span></a>
  

    <script type="text/javascript">

      // Navigate to the SearchTest view, with nothing preloaded.
      // Before doing the navigation, prompt the user to save any unsaved data.  
      function jumpToTestSearch() {
        <c:url var="linkedURI" context="/AssetManager" value="/assetManager.jsp">
          <c:param name="browseView" value="searchTests"/>
          <c:choose>
            <c:when test="${not empty requestScope.categoryId}">
              <c:param name="extraParamName" value="categoryId"/>
              <c:param name="extraParamValue" value="${requestScope.categoryId}"/>
            </c:when>
            <c:otherwise>
              <c:param name="extraParamName" value="isTestUndeployed"/>
              <c:param name="extraParamValue" value="true"/>
            </c:otherwise>
          </c:choose>
        </c:url>
        var parentURL = "<c:out value='${linkedURI}' escapeXml='false'/>";
        parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, parentURL);
      }
    </script>

  </c:if><%-- if not transient --%>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/testFacetButton.jsp#2 $$Change: 651448 $--%>
