<%--
  Page fragment that displays the contents of a targeter.

  @param  model          An ExpressionModel component containing the expression to
                         be edited.
  @param  container      The ID of the page element that contains this fragment.
                         This allows the element to be reloaded when the user
                         interacts with the controls in this editor.
  @param  operation      An optional operation to be performed on the expression
                         prior to rendering it.  Valid values are "updateChoice",
                         "updateLiteral", "updateToken", "addAcceptRule",
                         "addRejectRule", "deleteAcceptRule", "deleteRejectRule",
                         "addRuleset", "deleteRuleset", "moveRulesetUp",
                         "moveRulesetDown", "addRulesetGroup".
  @param  terminalId     The ID of the sub-expression on which the given operation
                         is to be performed.
  @param  value          The value to be assigned to the sub-expression on which
                         the given operation is to be performed.

  @param  editorId       An encoded id string of the form
                         ruleset_rulesetIndex_ruleType_ruleIndex_conditionType,
                         where rulesetIndex is the index of the ruleset to
                         operate on within the segment or group, ruleType is the
                         type of rule to operate on within the ruleset, ruleIndex
                         is the index of the rule within the ruleset, and
                         conditionType is the type of condition being edited
                         within the rule.
  @param  rulesetIndex   The index of the ruleset to operate on within the
                         segment or group.
  @param  ruleIndex      The index of the rule within the ruleset.
  @param  callback       A JavaScript function to be called when the expression is
                         changed
  @param  callbackData   A string to be passed to the callback function

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/targeterExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramModel"        param="model"/>
  <dspel:getvalueof var="paramContainer"    param="container"/>
  <dspel:getvalueof var="paramCallback"     param="callback"/>
  <dspel:getvalueof var="paramCallbackData" param="callbackData"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- To prevent JavaScript syntax errors, provide a default value for the callback --%>
  <c:if test="${empty paramCallback}">
    <c:set var="paramCallback" value="null"/>
  </c:if>

  <%-- Unless we are rendering as part of an Ajax request, register this
       expression editor --%>
  <c:if test="${empty param.isAjax}">  
    <c:url var="panelUrl" context="${config.contextRoot}" value="/expreditor/targeting/targeterExpressionPanel.jsp">
      <c:param name="model"     value="${paramModel}"/>
      <c:param name="container" value="${paramContainer}"/>
      <c:param name="isAjax"    value="1"/>
    </c:url>
    <c:url var="controllerUrl" context="${config.contextRoot}" value="/expreditor/targeting/targeterExpressionController.jsp">
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
    <dspel:include page="targeterExpressionController.jsp"/>
  </c:if>

  <%-- Render the rulesets. --%>
  <dspel:importbean var="model" bean="${paramModel}"/>
  
  <ee:isMultisiteMode var="multisiteMode"/>
  <ee:isMultisiteEnabled var="multisiteContent" expressionModel="${model}"/>
  
  <c:choose>
    <c:when test="${multisiteMode}">
      <%-- Render default rulesets  --%>   
      <dspel:include page="renderTargeterRulesetGroup.jsp">
        <dspel:param name="model" value="${paramModel}"/>
        <dspel:param name="container" value="${paramContainer}"/>
        <dspel:param name="rulesetGroup" value="${model.defaultRulesetGroup}"/>
        <dspel:param name="override" value="${false}"/>
        <dspel:param name="multisiteMode" value="${multisiteMode}"/>
        <dspel:param name="multisiteContent" value="${multisiteContent}"/>
      </dspel:include>
      
      <%-- Render override rulesets  --%>
      <c:forEach var="rulesetGroup" items="${model.overrideRulesetGroups}" varStatus="groupLoop">
      
        <%-- Render a separator --%>
        <%-- todo: move style to CSS --%>
        <div style="width:90%; height:10px;"></div>
      
        <dspel:include page="renderTargeterRulesetGroup.jsp">
          <dspel:param name="model" value="${paramModel}"/>
          <dspel:param name="container" value="${paramContainer}"/>
          <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
          <dspel:param name="rulesetGroupIndex" value="${groupLoop.index}"/>
          <dspel:param name="override" value="${true}"/>
          <dspel:param name="multisiteMode" value="${multisiteMode}"/>
          <dspel:param name="multisiteContent" value="${multisiteContent}"/>
        </dspel:include>
      </c:forEach>
      
      <input type="button" class="small bottom"
             value="<fmt:message key="targeterExpressionPanel.addOverrideRuleset"/>"
             onclick="atg.expreditor.performTargetingOperation({containerId: '<c:out value="${paramContainer}"/>',
                                                                operation:   'addOverrideRuleset'})"/>
    </c:when>
    
    <c:otherwise>
    
      <c:forEach var="ruleset" items="${model.defaultRulesetGroup.rulesets}" varStatus="rulesetLoop">
    
        <%-- Render a separator between any rulesets after the first one. --%>
        <c:if test="${rulesetLoop.index > 0}">
          <div class="ruleSetSeperator">
            <span><fmt:message key="targeterExpressionPanel.rulesetDivider"/></span>
          </div>
        </c:if>
    
        <dspel:include page="renderTargeterRuleset.jsp">
          <dspel:param name="ruleset" value="${ruleset}"/>
          <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
        </dspel:include>
      </c:forEach>
    
      <input type="button" class="small bottom"
             value="<fmt:message key="targeterExpressionPanel.addRuleSet"/>"
             onclick="atg.expreditor.performTargetingOperation({containerId: '<c:out value="${paramContainer}"/>',
                                                                operation:   'addRuleset'})"/>
    </c:otherwise>
  </c:choose>  

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/targeterExpressionPanel.jsp#2 $$Change: 651448 $--%>
