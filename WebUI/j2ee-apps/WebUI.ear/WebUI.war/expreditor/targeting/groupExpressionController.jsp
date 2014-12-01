<%--
  Page fragment that performs an operation on a segment or content group expression.

  @param  model          An ExpressionModel component containing the expression to
                         be edited.
  @param  operation      An optional operation to be performed on the expression
                         prior to rendering it.  Valid values are "updateChoice",
                         "updateLiteral", "updateToken", "addAcceptRule",
                         "addRejectRule", "deleteAcceptRule", "deleteRejectRule",
                         "addOverrideRuleset", "deleteOverrideRuleset",
                         "addOverrideAcceptRule", "addOverrideRejectRule",
                         "deleteOverrideAcceptRule", "deleteOverrideRejectRule",
                         "addDefaultSiteFilterDefinition", "addOverrideSiteFilterDefinition"
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

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/groupExpressionController.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Perform the requested operation on the expression --%>
  <dspel:importbean var="model" bean="${param.model}"/>
  <c:if test="${not empty param.editorId}">
    <ee:parseTargetingExpressionId var="info" encodedId="${param.editorId}"/>
    <ee:findTargetingExpression var="expression"
                                expressionModel="${model}"
                                terminalId="${param.terminalId}"
                                rulesetGroupType="${info.rulesetGroupType}"
                                rulesetGroupIndex="${info.rulesetGroupIndex}"
                                rulesetType="${info.rulesetType}"
                                rulesetIndex="${info.rulesetIndex}"
                                ruleType="${info.ruleType}"
                                ruleIndex="${info.ruleIndex}"
                                conditionType="${info.conditionType}"/>
  </c:if>
  <c:choose>
    <c:when test="${param.operation == 'updateChoice'}">
      <ee:setChoiceIndex expression="${expression}" value="${param.value}"/>
    </c:when>
    <c:when test="${param.operation == 'updateLiteral'}">
      <ee:setLiteralValue expression="${expression}" value="${param.value}"/>
    </c:when>
    <c:when test="${param.operation == 'updateToken'}">
      <ee:setMutableTokenValue expression="${expression}" value="${param.value}"/>
    </c:when>
    <c:when test="${param.operation == 'addAcceptRule'}">
      <ee:createAcceptRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'addRejectRule'}">
      <ee:createRejectRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteAcceptRule'}">
      <ee:deleteAcceptRule expressionModel="${model}"
                           rulesetIndex="${param.rulesetIndex}"
                           ruleIndex="${param.ruleIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteRejectRule'}">
      <ee:deleteRejectRule expressionModel="${model}"
                           rulesetIndex="${param.rulesetIndex}"
                           ruleIndex="${param.ruleIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'addOverrideRuleset'}">
      <ee:createOverrideRuleset expressionModel="${model}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteOverrideRuleset'}">
      <ee:deleteOverrideRuleset expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'addOverrideAcceptRule'}">
      <ee:createOverrideRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}" acceptRule="${true}"/>
    </c:when>
    <c:when test="${param.operation == 'addOverrideRejectRule'}">
      <ee:createOverrideRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}" acceptRule="${false}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteOverrideAcceptRule'}">
      <ee:deleteOverrideRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"
                             ruleIndex="${param.ruleIndex}" acceptRule="${true}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteOverrideRejectRule'}">
      <ee:deleteOverrideRule expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"
                             ruleIndex="${param.ruleIndex}" acceptRule="${false}"/>
    </c:when>
    
    <c:when test="${param.operation == 'addDefaultSiteFilterDefinition'}">
      <ee:createDefaultSiteFilterDefinition expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteDefaultSiteFilterDefinition'}">
      <ee:deleteDefaultSiteFilterDefinition expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'addOverrideSiteFilterDefinition'}">
      <ee:createOverrideSiteFilterDefinition expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    <c:when test="${param.operation == 'deleteOverrideSiteFilterDefinition'}">
      <ee:deleteOverrideSiteFilterDefinition expressionModel="${model}" rulesetIndex="${param.rulesetIndex}"/>
    </c:when>
    
    
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/groupExpressionController.jsp#2 $$Change: 651448 $--%>
