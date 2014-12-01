<%--
  Edit view for search configuration search rule property 

  @param  model       An ExpressionModel component containing the expression to
                      be edited.
  @param  modelBase   An base ExpressionModel component containing the expression to
                      be edited.
  @param  container   The ID of the page element that contains this fragment.
                      This allows the element to be reloaded when the user
                      interacts with the controls in this editor.
 
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigRuleEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>  
  
<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
    
<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <dspel:getvalueof var="paramModelPath" param="model"/>
  <dspel:getvalueof var="paramModelBasePath" param="modelBase"/>
  <dspel:getvalueof var="paramContainer" param="container"/>
  
  <c:set var="formHandler" value="${requestScope.formHandler}"/>
  
  <div>

   <%--
    <b>
      <fmt:message key="ruleSetSubHeader"/>
    </b>
    <br /><fmt:message key="ruleSetHelpText"/>
    <br /><br />
    --%>
    <c:set var="containerId" value="${paramContainer}"/>
    <div id="<c:out value='${containerId}'/>" class="atg_smerch_rulesTable">
      <dspel:include page="searchConfigExpressionPanel.jsp">
        <dspel:param name="model" value="${paramModelPath}"/>
        <dspel:param name="container" value="${containerId}"/>
        <dspel:param name="mode" value="main"/>
      </dspel:include>
    </div>
  </div>

  <c:if test="${formHandler.showBaseConfig}">

    <dspel:importbean var="baseConfigModel" bean="${paramModelBasePath}"/>
    <c:if test="${!empty baseConfigModel.ruleList}">
    
    <%-- Display a link to the base search configuration --%>
    <%-- TODO: make this link go to directly to the same tab that we are on. --%>
    <dspel:getvalueof var="bscname" bean="${requestScope.formHandlerPath}.value.baseSearchConfig.displayName"/>
    <dspel:getvalueof var="baseCfgId" bean="${requestScope.formHandlerPath}.value.baseSearchConfig.id"/>
    <c:set var="repName" value="${requestScope.formHandler.repository.repositoryName}"/>
    <asset-ui:createAssetURI var="linkedURI" 
      componentName="${repName}"
      itemDescriptorName="baseSearchConfig"
      itemId="${baseCfgId}"/>
    <b>
      <br /><fmt:message key="rulesInheritedFrom"/>
      <c:set var="currentTabNumber" value="${sessionInfo.propertyTabs['searchConfig']}"/>
      <a id="<c:out value='${linkId}'/>" href="javascript:<c:out value='drillDownFunc("${linkedURI}",${currentTabNumber})'/>">
        <c:out value="${bscname}"/> 
      </a>
    </b>
    <%--
    <br /><fmt:message key="rulesInheritedFromHelpText"/>
    --%>
    <br /><br />
    <div>
    <c:set var="containerId" value="${paramContainer}Base"/>
      <div id="<c:out value='${containerId}'/>">
        <dspel:include page="searchConfigExpressionPanel.jsp">
          <dspel:param name="model" value="${paramModelBasePath}"/>
          <dspel:param name="container" value="${containerId}"/>
          <dspel:param name="mode" value="base"/>
        </dspel:include>
      </div>
    </div>
  </c:if>
  </c:if>
  
  <script type="text/javascript">
    <%-- Function for linking to another asset --%>
    function drillDownFunc(uri, tab) {
      var linkURL = "/AssetManager/assetEditor/editAsset.jsp?pushContext=true&linkPropertyName=baseSearchConfig&assetURI=" + uri;
      if (tab != null)
      {
        linkURL += "&overrideViewNumber=" + tab;
      }
      // Force a save of the current asset, then move to the URL.
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkURL,null,null,null,null,true);
    }
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigRuleEditor.jsp#2 $$Change: 651448 $--%>
