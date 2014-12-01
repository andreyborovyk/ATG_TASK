<%--
  Page fragment that displays the contents of a segment or content group.

  @param  model          An ExpressionModel component containing the expression to
                         be edited.
  @param  container      The ID of the page element that contains this fragment.
                         This allows the element to be reloaded when the user
                         interacts with the controls in this editor.
  @param  operation      An optional operation to be performed on the expression
                         prior to rendering it.  Valid values are "updateChoice",
                         "updateLiteral", "updateToken", "addAcceptRule",
                         "addRejectRule", "deleteAcceptRule", "deleteRejectRule"
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

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/groupExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"              %>
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
    <c:url var="panelUrl" context="${config.contextRoot}" value="/expreditor/targeting/groupExpressionPanel.jsp">
      <c:param name="model"     value="${paramModel}"/>
      <c:param name="container" value="${paramContainer}"/>
      <c:param name="isAjax"    value="1"/>
    </c:url>
    <c:url var="controllerUrl" context="${config.contextRoot}" value="/expreditor/targeting/groupExpressionController.jsp">
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
    <dspel:include page="groupExpressionController.jsp"/>
  </c:if>

  <%-- Render the rulesets. --%>
  <dspel:importbean var="model" bean="${paramModel}"/>
  
  <ee:isMultisiteMode var="multisiteMode"/>
  <ee:isMultisiteEnabled var="multisiteContent" expressionModel="${model}"/>

  <c:set var="siteFilterAvailable" value="${not model.siteFilterUnavailable and multisiteContent and multisiteMode}"/>
  
  <c:forEach var="ruleset" items="${model.defaultRulesetGroup.rulesets}" varStatus="rulesetLoop">
    <dspel:include page="renderGroupRuleset.jsp">
      <dspel:param name="ruleset" value="${ruleset}"/>
      <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
      <dspel:param name="multisiteMode" value="${multisiteMode}"/>
      <dspel:param name="overrideRuleset" value="${false}"/>
      <dspel:param name="siteFilterAvailable" value="${siteFilterAvailable}"/>
    </dspel:include>
  </c:forEach>
  
  <c:if test="${multisiteMode}">
    <c:forEach var="rulesetGroup" items="${model.overrideRulesetGroups}" varStatus="rulesetLoop">
      <c:forEach var="ruleset" items="${rulesetGroup.rulesets}">
        <dspel:include page="renderGroupRuleset.jsp">
          <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
          <dspel:param name="ruleset" value="${ruleset}"/>
          <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
          <dspel:param name="multisiteMode" value="${multisiteMode}"/>
          <dspel:param name="overrideRuleset" value="${true}"/>
          <dspel:param name="siteFilterAvailable" value="${siteFilterAvailable}"/>
        </dspel:include>
      </c:forEach>
    </c:forEach>
  
    <input type="button" class="small bottom"
           value="<fmt:message key='groupExpressionPanel.addOverrideRule'/>"
           onclick="atg.expreditor.performTargetingOperation({containerId: '<c:out value="${paramContainer}"/>',
                                                              operation:   'addOverrideRuleset'})"/>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/groupExpressionPanel.jsp#2 $$Change: 651448 $--%>
