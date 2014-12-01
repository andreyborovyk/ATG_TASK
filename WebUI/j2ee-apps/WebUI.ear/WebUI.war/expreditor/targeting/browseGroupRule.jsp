<%--
  Page fragment that displays a read-only view of a single Include or Exclude
  rule inside of a segment or content group.

  @param  rulesetIndex  The index of the ruleset that contains the rule.
  @param  rule          The rule being rendered.
  @param  ruleType      The type of rule (either accept or reject).
  @param  ruleIndex     The index of the rule being rendered.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupRule.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"                %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramRulesetIndex" param="rulesetIndex"/>
  <dspel:getvalueof var="paramRule" param="rule"/>
  <dspel:getvalueof var="paramRuleType" param="ruleType"/>
  <dspel:getvalueof var="paramRuleIndex" param="ruleIndex"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:choose>
    <c:when test="${paramRule.isAcceptRule}">
      <fmt:message var="ruleTypeHeader" key="renderGroupRuleset.includeRule"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="ruleTypeHeader" key="renderGroupRuleset.excludeRule"/>
    </c:otherwise>
  </c:choose>

  <%-- Titlebar --%>
  <div class="panelHeader">
    <h5><c:out value="${ruleTypeHeader}"/></h5>
  </div>

  <%-- Render the div and table that enclose each Include or Exclude rule --%>
  <div class="panelContent expressionBlocks">
    <table>
      <tr>
        <td colspan="2" style="border:none;">
          <%-- Render the expression. --%>
          <web-ui:renderExpression var="expression"
                                   expression="${paramRule.contentSubExpression}"/>
          <c:out value="${expression}"/>
        </td>
      </tr>
    </table>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupRule.jsp#2 $$Change: 651448 $--%>
