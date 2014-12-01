<%--
  Page fragment that displays a read-only view of a single Show or Hide rule
  inside of a targeter. Targeter rules contain 4 distinct sets of conditions
  related to People, Content, Time, and Environment.

  @param  rulesetIndex  The index of the ruleset that contains the rule.
  @param  rule          The rule being rendered.
  @param  ruleType      The type of rule (either accept or reject).
  @param  ruleIndex     The index of the rule being rendered.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRule.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="web-ui"
    uri="http://www.atg.com/taglibs/web-ui_rt"%>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramRule" param="rule"/>

  <c:choose>
    <c:when test="${paramRule.isAcceptRule}">
      <fmt:message var="ruleTypeHeader" key="renderTargeterRuleset.showRule"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="ruleTypeHeader" key="renderTargeterRuleset.hideRule"/>
    </c:otherwise>
  </c:choose>

  <%-- Render the header --%>
  <div class="panelHeader">
    <h5><c:out value="${ruleTypeHeader}"/></h5>
  </div>

  <%-- Render each of the conditions being used to edit this rule --%>
  <div class="panelContent expressionBlocks ruleSetContent">
    <table width="100%" class="ruleSet">

      <%-- Render the content conditions --%>
      <tr>
        <td class="ruleSetLabel" nowrap="nowrap">
          <fmt:message key="renderTargeterRule.ContentConditionTitle"/>
        </td>

        <td class="ruleSetExpression">
          <table>
            <tr>
              <td colspan="2">
                <web-ui:renderExpression var="expression" expression="${paramRule.contentSubExpression}"/>
                <c:out value="${expression}"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>

      <%-- Render the people conditions --%>
      <tr>
        <td class="ruleSetLabel" nowrap="nowrap">
          <fmt:message key="renderTargeterRule.PeopleConditionTitle"/>
        </td>

        <td class="ruleSetExpression">
          <table>
            <tr>
              <td colspan="2">
                <web-ui:renderExpression var="expression" expression="${paramRule.peopleSubExpression}"/>
                <c:out value="${expression}"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>

      <%-- Render the time conditions --%>
      <tr>
        <td class="ruleSetLabel" nowrap="nowrap">
          <fmt:message key="renderTargeterRule.TimeConditionTitle"/>
        </td>

        <td class="ruleSetExpression">
          <table>
            <tr>
              <td colspan="2">
                <web-ui:renderExpression var="expression" expression="${paramRule.timeSubExpression}"/>
                <c:out value="${expression}"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>

      <%-- Render the environment conditions --%>
      <tr>
        <td class="ruleSetLabel last" nowrap="nowrap">
          <fmt:message key="renderTargeterRule.EnvironmentConditionTitle"/>
        </td>

        <td class="ruleSetExpression last">
          <table>
            <tr>
              <td colspan="2">
                <web-ui:renderExpression var="expression" expression="${paramRule.environmentSubExpression}"/>
                <c:out value="${expression}"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>

    </table>
  </div>
  
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRule.jsp#2 $$Change: 651448 $--%>
