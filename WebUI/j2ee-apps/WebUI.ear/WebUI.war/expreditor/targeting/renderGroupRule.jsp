<%--
  Page fragment that renders a single Include or Exclude rule inside of a
  segment or content group.

  @param  rulesetIndex    The index of the ruleset that contains the rule.
  @param  rule            The rule being rendered.
  @param  ruleType        The type of rule (either accept or reject).
  @param  ruleIndex       The index of the rule being rendered.
  @param  container       The ID of the container for this expression editor.
  @param  overrideRuleset Defines rendering mode, true for rulesets (targeters), 
                          false for rules (content groups / user segments).

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderGroupRule.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramRulesetIndex" param="rulesetIndex"/>
  <dspel:getvalueof var="paramRule" param="rule"/>
  <dspel:getvalueof var="paramRuleType" param="ruleType"/>
  <dspel:getvalueof var="paramRuleIndex" param="ruleIndex"/>
  <dspel:getvalueof var="paramContainer" param="container"/>
  <dspel:getvalueof var="overrideRuleset" param="overrideRuleset"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Determine the operation to be invoked to delete this rule. --%>
  <c:choose>
    <c:when test="${overrideRuleset}">
      <c:if test="${paramRuleType eq 'accept'}">
        <c:set var="operation" value="deleteOverrideAcceptRule"/>
      </c:if>
      <c:if test="${paramRuleType eq 'reject'}">
        <c:set var="operation" value="deleteOverrideRejectRule"/>
      </c:if>
      <c:set var="rulesetType" value="override"/>
    </c:when>
    <c:otherwise>
      <c:if test="${paramRuleType eq 'accept'}">
        <c:set var="operation" value="deleteAcceptRule"/>
      </c:if>
      <c:if test="${paramRuleType eq 'reject'}">
        <c:set var="operation" value="deleteRejectRule"/>
      </c:if>
      <c:set var="rulesetType" value="default"/>
    </c:otherwise>
  </c:choose>
  

  <c:choose>
    <c:when test="${paramRule.isAcceptRule}">
      <fmt:message var="ruleTypeHeader" key="renderGroupRuleset.includeRule"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="ruleTypeHeader" key="renderGroupRuleset.excludeRule"/>
    </c:otherwise>
  </c:choose>

  <%-- Render the header and any toolbar buttons --%>
  <div class="panelHeader">
    <h5><c:out value="${ruleTypeHeader}"/></h5>
    <div class="actions">
      <a class="iconPanelClose"
         title="<fmt:message key="renderGroupRule.deleteRule"/>"
         href="javascript:atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                                    rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                                    ruleIndex:    '<c:out value="${paramRuleIndex}"/>',
                                                                    operation:    '<c:out value="${operation}"/>'})"></a>

      <a href="#" class="iconPanelMinimize rulesTrigger"
         title="<fmt:message key="renderGroupRule.hideRule"/>"
         onclick="atg.expreditor.rulesDisplay(this,
                                             '<fmt:message key="renderGroupRule.hideRule"/>',
                                             '<fmt:message key="renderGroupRule.showRule"/>');"></a>
    </div>
  </div>

  <%-- Render the div and table that enclose each Include or Exclude rule --%>
  <div class="panelContent expressionBlocks">
    <table>
      <tr>
        <td colspan="2" style="border:none;">

          <%-- Create an ID for the expression editor containing info about this rule --%>
          <ee:createTargetingExpressionId var="editorId"
                                          rulesetType="${rulesetType}"
                                          rulesetIndex="${paramRulesetIndex}"
                                          ruleType="${paramRuleType}"
                                          ruleIndex="${paramRuleIndex}"
                                          conditionType="content"/>

          <%-- Render the condition being used to edit this rule --%>
          <ee:getTerminalEditors var="editors" expression="${paramRule.contentSubExpression}"/>
          <c:forEach var="editor" items="${editors}">
            <dspel:include otherContext="${editor.contextRoot}" page="${editor.page}">
              <dspel:param name="expression" value="${editor.expression}"/>
              <dspel:param name="editorId" value="${editorId}"/>
            </dspel:include>
          </c:forEach>

        </td>
      </tr>
    </table>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderGroupRule.jsp#2 $$Change: 651448 $--%>
