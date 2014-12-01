<%--
  Page fragment that renders a single Search rule.

  @param  model       An ExpressionModel component containing the expression to
                      be edited.
  @param  searchRule  An SearchConfigRule bean
  @param  container   The ID of the page element that contains this fragment.
                      This allows the element to be reloaded when the user
                      interacts with the controls in this editor.
  @param  mode        Showing mode 
  @param  index       The index of the rule being rendered.

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigRuleRender.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor"            %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="paramModelPath" param="model"/>
  <dspel:getvalueof var="paramRule" param="searchRule"/>
  <dspel:getvalueof var="paramContainer" param="container"/>
  <dspel:getvalueof var="paramMode" param="mode"/>
  <dspel:getvalueof var="paramIndex" param="index"/>

  <c:choose>
    <c:when test="${paramIndex % 2 != 0}">
      <tr class="atg_altRow">
    </c:when>
    <c:otherwise>
      <tr>
    </c:otherwise>
  </c:choose>
    <td class="atg_smerch_orderCell">
      <c:choose>
        <c:when test="${paramMode == 'main' && requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
          <dspel:input id="order_${paramIndex}" type="text" bean="${paramModelPath}.ruleList[${paramIndex}].index" size="4" onkeypress="return searchConfig_onPositionFieldKeyPress(event)" onchange="return searchConfig_onPositionFieldChange(event)" />
        </c:when>
        <c:otherwise>
          <c:out value="${paramRule.index}"/>
        </c:otherwise>
      </c:choose>
    </td>
    <td class="atg_iconCell">
      <dspel:getvalueof var="checkVal" bean="${paramModelPath}.ruleList[${paramIndex}].enabled"/>
      <c:choose>
        <c:when test="${requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
          <dspel:input type="checkbox" bean="${paramModelPath}.ruleList[${paramIndex}].enabled" onclick="markAssetModified()"/>
        </c:when>
        <c:when test="${checkVal}">
          <input type="checkbox" disabled="disabled" checked="checked"/>
        </c:when>
        <c:otherwise>
          <input type="checkbox" disabled="disabled"/>
        </c:otherwise>
      </c:choose>
    </td>
    <td><%-- class="expreditorContainer" --%>
      <ee:createTargetingExpressionId var="editorId"
                                      rulesetIndex="${paramIndex}"
                                      ruleType="query"
                                      ruleIndex="0"
                                      conditionType="content"/>
      <c:set var="containerId" value="${editorId}${paramContainer}"/>
      <div id="<c:out value='${containerId}'/>" class="expreditorPanel">
        <dspel:include page="inlineExpressionPanel.jsp">
          <dspel:param name="model" value="${paramModelPath}"/>
          <dspel:param name="container" value="${containerId}"/>
          <dspel:param name="editorId" value="${editorId}"/>
          <dspel:param name="mode" value="${paramMode}"/>
        </dspel:include>
      </div>
      <ee:createTargetingExpressionId var="editorId"
                                      rulesetIndex="${paramIndex}"
                                      ruleType="action"
                                      ruleIndex="0"
                                      conditionType="content"/>
      <c:set var="containerId" value="${editorId}${paramContainer}"/>
      <div id="<c:out value='${containerId}'/>" class="expreditorPanel">
        <dspel:include page="inlineExpressionPanel.jsp">
          <dspel:param name="model" value="${paramModelPath}"/>
          <dspel:param name="container" value="${containerId}"/>
          <dspel:param name="editorId" value="${editorId}"/>
          <dspel:param name="mode" value="${paramMode}"/>
        </dspel:include>
      </div>
    </td>
    <td class="atg_iconCell">
      <c:choose>
        <c:when test="${paramMode == 'main' && requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
          <a href="#" onclick="document.getElementById('currIndex').value='<c:out value="${paramIndex}"/>'; document.getElementById('removeRule').click(); return false;" title="<fmt:message key='searchConfigRuleRender.removeRuleTooltip'/>"><img src="../images/icon_propertyDelete.gif"/></a>
        </c:when>
        <c:otherwise>
          &nbsp;
        </c:otherwise>
      </c:choose>
    </td>
  </tr>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigRuleRender.jsp#2 $$Change: 651448 $--%>
