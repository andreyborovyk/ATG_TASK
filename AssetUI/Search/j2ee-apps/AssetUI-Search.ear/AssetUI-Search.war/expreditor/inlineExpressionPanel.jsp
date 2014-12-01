<%--
  Page fragment that displays an expression editor.

  @param  model       An ExpressionModel component containing the expression to
                      be edited.
  @param  container   The ID of the page element that contains this fragment.
                      This allows the element to be reloaded when the user
                      interacts with the controls in this editor.
  @param  editorId    A unique identifier for this expression editor (required
                      only when the page contains more than one expression editor)
  @param  operation   An optional operation to be performed on the expression
                      prior to rendering it.  Valid values are "updateChoice",
                      "updateLiteral", and "updateToken".
  @param  terminalId  The ID of the sub-expression on which the given operation
                      is to be performed.
  @param  value       The value to be assigned to the sub-expression on which
                      the given operation is to be performed.
  
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/inlineExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="ee"     uri="http://www.atg.com/taglibs/expreditor"            %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="paramEditorId" param="editorId"/>
  <dspel:getvalueof var="paramMode" param="mode"/>
  <dspel:getvalueof var="paramModel" param="model"/>
  <dspel:getvalueof var="paramContainer" param="container"/>

  <c:choose>
    <c:when test="${empty paramMode}">
      <c:set var="paramMode" value="main"/>
    </c:when>
    <c:when test="${paramMode == 'main' && requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
      <c:set var="paramMode" value="main"/>
    </c:when>
    <c:otherwise>
      <c:set var="paramMode" value="base"/>
    </c:otherwise>
  </c:choose>

  <%-- Unless we are rendering as part of an Ajax request, register this
       expression editor --%>
  <c:if test="${empty param.isAjax}">  
    <c:url var="panelUrl" context="${config.contextRoot}" value="/expreditor/inlineExpressionPanel.jsp">
      <c:param name="model"     value="${paramModel}"/>
      <c:param name="container" value="${paramContainer}"/>
      <c:param name="isAjax"    value="1"/>
    </c:url>
    <c:url var="controllerUrl" context="${config.contextRoot}" value="/expreditor/inlineExpressionPanel.jsp">
      <c:param name="model"       value="${paramModel}"/>
      <c:param name="doNotRender" value="1"/>
    </c:url>
    <script type="text/javascript">
      atg.expreditor.registerExpressionEditor("<c:out value='${paramContainer}'/>",
        {
          panelUrl:      "<c:out value='${panelUrl}' escapeXml='false'/>",
          controllerUrl: "<c:out value='${controllerUrl}' escapeXml='false'/>",
          callback:      markAssetModified
        }
      );
    </script>
  </c:if>
       
  <%-- Perform the requested operation (if any) on the expression --%>
  <dspel:importbean var="model" bean="${paramModel}"/>
  <ee:parseTargetingExpressionId var="info" encodedId="${paramEditorId}"/>
  <c:set var="rule" value="${model.ruleList[info.rulesetIndex]}"/>
  <c:choose>
    <c:when test="${info.ruleType == 'query'}">
      <c:set var="expression" value="${rule.queryExpression}"/>
    </c:when>
    <c:when test="${info.ruleType == 'action'}">
      <c:set var="expression" value="${rule.actionExpression}"/>
    </c:when>
  </c:choose>

  <c:if test="${not empty param.operation}">
    <ee:getTerminalEditors var="editors" expression="${expression}"/>
    <c:forEach var="editor" items="${editors}">
      <c:if test="${editor.expression.identifier == param.terminalId}">
        <c:set var="expr" value="${editor.expression}"/>
      </c:if>
    </c:forEach>
    <c:choose>
      <c:when test="${param.operation == 'updateChoice'}">
        <ee:setChoiceIndex expression="${expr}"
                           value="${param.value}"/>
      </c:when>
      <c:when test="${param.operation == 'updateLiteral'}">
        <ee:setLiteralValue expression="${expr}"
                            value="${param.value}"/>
      </c:when>
      <c:when test="${param.operation == 'updateToken'}">
        <ee:setMutableTokenValue expression="${expr}"
                                 value="${param.value}"/>
      </c:when>
    </c:choose>
  </c:if>

  <%-- Finally, render the expression editor --%>
  <c:if test="${empty param.doNotRender}">
    <c:choose>
      <c:when test="${paramMode == 'main'}">
        <ee:getTerminalEditors var="editors" expression="${expression}"/>
        <c:forEach var="editor" items="${editors}">
          <dspel:include otherContext="${editor.contextRoot}" page="${editor.page}">
            <dspel:param name="expression" value="${editor.expression}"/>
            <dspel:param name="editorId" value="${paramEditorId}"/>
          </dspel:include>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <%-- Render the expression. --%>
        <web-ui:renderExpression var="expressionText" expression="${expression}"/>
        <c:out value="${expressionText}"/>
      </c:otherwise>
    </c:choose>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/inlineExpressionPanel.jsp#2 $$Change: 651448 $--%>
