<%--
  The root of the search expression editor for asset Picker

  @param  operation      An optional operation to be performed on the search form
                         prior to rendering it. Valid values are "changeItemType".
  @param  value          The value to be used related to the operation to be performed.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack all page parameters --%>
  <dspel:getvalueof var="operation" param="operation"/>
  <dspel:getvalueof var="opValue" param="value"/>

  <dspel:importbean var="sessionInfo" bean="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="config"      bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Operations to be performed before rendering anything else - START --%>
  <c:choose>
    <c:when test="${operation == 'changeItemType'}">
      <%-- reset the expression model --%>
      <web-ui:invoke bean="${sessionInfo.assetPickerExpressionService}" method="reset"/>

      <%-- Set the item types and property configuration file into the context for
           the assetPickerSearchExpressionPanel --%>
      <web-ui:decodeRepositoryItemType var="currentItemTypeInfo" encodedRepositoryItemType="${opValue}"/>

      <c:if test="${not empty currentItemTypeInfo}">
        <%-- Set the item types and property configuration file into the context for
             the searchExpressionPanel --%>
        <%--
           Get the current expression context in AssetPickerExpressionService.
           AssetPickerExpressionService is of type MultiTypeTargetingExpressionService which
           stores a copy of the default context per item type key.
           Hence simply resolving the nucleus component would modify
           the default JSP expression context and not the one used for creating a query.
        --%>
        <c:set var="expressionContext" value="${config.sessionInfo.assetPickerExpressionService.currentJspExpressionContext}"/>
        
        <c:set target="${expressionContext}"
               property="itemRepository"
               value="${currentItemTypeInfo.repositoryPath}"/>
        <c:set target="${expressionContext}"
               property="itemType"
               value="${currentItemTypeInfo.repositoryItemType}"/>
  
        <%-- include the expression --%>
        <c:set var="includeExpression" value="true"/>
      </c:if><!--  non empty currentItemTypeInfo -->

      <c:if test="${empty currentItemTypeInfo}">
        <%-- do not include the expression --%>
        <c:set var="includeExpression" value="false"/>
        <span>
          <fmt:message key="searchForm.noItemTypeSelected"/>
        </span>
      </c:if><!--  empty currentItemTypeInfo -->
    </c:when>
    <c:otherwise>
      <c:set var="includeExpression" value="true"/>
    </c:otherwise>
  </c:choose>
  <%-- Operations to be performed before rendering anything else - END --%>
  
  <c:if test="${includeExpression}">
    <div id="assetPickerSearchExprEditorPanel">
      <dspel:include page="/expreditor/inlineExpressionPanel.jsp"
                     otherContext="${config.webuiWebAppName}">
        <dspel:param name="model" value="${sessionInfo.assetPickerExpressionService.absoluteName}"/>
        <dspel:param name="container" value="assetPickerSearchExprEditorPanel"/>
        <dspel:param name="enableContainerTags" value="${true}"/>
      </dspel:include>
    </div>
  </c:if><!-- includeExpression = true -->

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchExpressionPanel.jsp#2 $$Change: 651448 $ --%>
