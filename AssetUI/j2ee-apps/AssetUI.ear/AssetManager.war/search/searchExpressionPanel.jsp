<%--
  The root of the search expression editor.

  @param  operation      An optional operation to be performed on the search form
                         prior to rendering it. Valid values are "changeItemType".
                         This is set by the AJAX call.

  @param  value          The value to be used for the expression editor on which
                         the given operation is to be performed.
                         This is set by the AJAX call.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%-- set the debug flag --%>
  <c:set var="debug" value="${false}"/>

  <c:set var="expressionModelPath" value="/atg/web/assetmanager/search/TargetingExpressionService"/>
  <dspel:importbean var="expressionModel"
                    bean="${expressionModelPath}"/>
  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- if set to true the expression would be rendered.
    This may be set to false if current item type cannot
    be decoded as it may not exists or is wrongly
    specified in taskConfiguration.xml --%>
  <c:set var="includeExpression" value="true"/>

  <%-- check if the operation passed in is type changeItemType,
    if so update the expression context. --%>
  <c:if test="${param.operation == 'changeItemType'}">
    <%-- reset the expression model --%>
    <web-ui:invoke bean="${expressionModel}" method="reset"/>

    <%-- store the current item type in the session --%>
    <c:set target="${sessionInfo}" property="currentSearchItemType" value="${param.value}"/>

    <%-- Set the item types and property configuration file into the context for
         the searchExpressionPanel --%>
    <web-ui:decodeRepositoryItemType var="currentItemTypeInfo" encodedRepositoryItemType="${sessionInfo.currentSearchItemType}"/>

    <c:if test="${not empty currentItemTypeInfo}">
      <%-- Set the item types and property configuration file into the context for
           the searchExpressionPanel --%>
      <dspel:getvalueof var="expressionContext"
                        bean="/atg/web/assetmanager/search/TargetingExpressionContext"/>
      <c:set target="${expressionContext}"
             property="itemRepository"
             value="${currentItemTypeInfo.repositoryPath}"/>
      <c:set target="${expressionContext}"
             property="itemType"
             value="${currentItemTypeInfo.repositoryItemType}"/>
      <c:set target="${expressionContext}"
             property="targetingPropertyConfigurationFile"
             value="${requestScope.tabConfig.views['form'].propertyConfigurationFile}"/>
    </c:if><!--  non empty currentItemTypeInfo -->

    <c:if test="${empty currentItemTypeInfo}">
      <%-- do not include the expression --%>
      <c:set var="includeExpression" value="false"/>
      <span>
        <fmt:message key="searchForm.noItemTypeSelected"/>
      </span>
    </c:if><!--  empty currentItemTypeInfo -->
  </c:if>

  <c:if test="${includeExpression}">
    <div id="searchExprEditorPanel">
      <dspel:include page="/expreditor/inlineExpressionPanel.jsp"
                      otherContext="${config.webuiWebAppName}">
        <dspel:param name="model" value="${expressionModelPath}"/>
        <dspel:param name="container" value="searchExprEditorPanel"/>
        <dspel:param name="enableContainerTags" value="${true}"/>
      </dspel:include>
    </div>
  </c:if><!-- includeExpression = true -->

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchExpressionPanel.jsp#2 $$Change: 651448 $ --%>
