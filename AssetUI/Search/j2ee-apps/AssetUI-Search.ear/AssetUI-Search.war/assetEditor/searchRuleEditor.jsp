<%--
  Edit view for search configuration search rule property 
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/searchRuleEditor.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>  
  
<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>
    
<dspel:page>

  <c:if test="${requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
    <assetui-search:getSearchConfigurationDimensionValues var="dimValMap" searchConfig="${requestScope.formHandler.repositoryItem}"/>
    <dspel:setvalue bean="/atg/commerce/web/browse/CatalogTreeSiteFilter.siteId" value="${dimValMap.site}"/>
  </c:if>

  <c:set var="ruleType" value="${requestScope.mpv.attributes.ruleType}"/>

  <dspel:include page="/expreditor/searchConfigRuleEditor.jsp">
    <c:choose>
      <c:when test="${ruleType == 'exclusion'}">
        <dspel:param name="model" value="${requestScope.formHandler.exclusionExpressionService.absoluteName}"/>
        <dspel:param name="modelBase" value="${requestScope.formHandler.exclusionExpressionServiceBase.absoluteName}"/>
      </c:when>
      <c:when test="${ruleType == 'position'}">
        <dspel:param name="model" value="${requestScope.formHandler.positionExpressionService.absoluteName}"/>
        <dspel:param name="modelBase" value="${requestScope.formHandler.positionExpressionServiceBase.absoluteName}"/>
      </c:when>
      <c:when test="${ruleType == 'redirection'}">
        <dspel:param name="model" value="${requestScope.formHandler.redirectionExpressionService.absoluteName}"/>
        <dspel:param name="modelBase" value="${requestScope.formHandler.redirectionExpressionServiceBase.absoluteName}"/>
      </c:when>
    </c:choose>
    <dspel:param name="container" value="${ruleType}SearchConfigContainer"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/searchRuleEditor.jsp#1 $$Change: 651360 $--%>
