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
  @param  callback    A JavaScript function to be called when the expression is
                      changed
  @param  callbackData  A string to be passed to the callback function
  
  @param  enableContainerTags A boolean to render non-terminal expressions.
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramModel"               param="model"/>
  <dspel:getvalueof var="paramContainer"           param="container"/>
  <dspel:getvalueof var="paramEnableContainerTags" param="enableContainerTags"/>
  <dspel:getvalueof var="paramCallback"            param="callback"/>
  <dspel:getvalueof var="paramCallbackData"        param="callbackData"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>

  <%-- To prevent JavaScript syntax errors, provide a default value for the callback --%>
  <c:if test="${empty paramCallback}">
    <c:set var="paramCallback" value="null"/>
  </c:if>

  <%-- Unless we are rendering as part of an Ajax request, register this
       expression editor --%>
  <c:if test="${empty param.isAjax}">  
    <c:url var="panelUrl" context="${config.contextRoot}" value="/expreditor/inlineExpressionPanel.jsp">
      <c:param name="model"               value="${paramModel}"/>
      <c:param name="container"           value="${paramContainer}"/>
      <c:param name="enableContainerTags" value="${paramEnableContainerTags}"/>
      <c:param name="isAjax"              value="1"/>
    </c:url>
    <c:url var="controllerUrl" context="${config.contextRoot}" value="/expreditor/expressionController.jsp">
      <c:param name="model" value="${paramModel}"/>
    </c:url>
    <script type="text/javascript">
      atg.expreditor.registerExpressionEditor("<c:out value='${paramContainer}'/>",
        {
          panelUrl:      "<c:out value='${panelUrl}' escapeXml='false'/>",
          controllerUrl: "<c:out value='${controllerUrl}' escapeXml='false'/>",
          callback:      <c:out value="${paramCallback}"/>,
          callbackData:  "<c:out value='${paramCallbackData}'/>"
        }
      );
    </script>
  </c:if>

  <%-- Perform the requested operation (if any) on the expression --%>
  <c:if test="${not empty param.operation}">  
    <dspel:include page="expressionController.jsp"/>
  </c:if>

  <c:set var="debug" value="${false}"/>
  
  <%-- Finally, render the expression editor --%>
  <dspel:importbean var="model" bean="${paramModel}"/>
  <ee:getTerminalEditors var="renderings" expression="${model.rootExpression}" enableContainerTags="${paramEnableContainerTags}" debug="${debug}"/>
  <c:forEach var="rendering" items="${renderings}">
    <c:choose>
      <c:when test="${not empty rendering.expression}">
        <c:if test="${debug}">
          <c:out value="<!--  Expr: ${rendering.expression} -->" escapeXml="false"/>
          <c:out value="<!--  Expr.id: ${rendering.expression.identifier} -->" escapeXml="false"/>
          <c:out value="<!--  Expr.page: ${rendering.page} -->" escapeXml="false"/>
        </c:if>
        <dspel:include otherContext="${rendering.contextRoot}" page="${rendering.page}">
          <dspel:param name="expression" value="${rendering.expression}"/>
        </dspel:include>
      </c:when>
      <c:otherwise>
        <c:if test="${not empty rendering.HTMLTag}">
          <c:out value="${rendering.HTMLTag}" escapeXml="false"/>
        </c:if>
      </c:otherwise>
    </c:choose>
  </c:forEach>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/inlineExpressionPanel.jsp#2 $$Change: 651448 $--%>
