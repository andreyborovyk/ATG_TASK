<%--
  Page fragment that displays a read-only view of a single ruleset inside of a
  segment or content group.

  @param  ruleset         The ruleset being edited.
  @param  rulesetIndex    The index of the ruleset being edited inside an expression service.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupRuleset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramRulesetGroup" param="rulesetGroup"/>
  <dspel:getvalueof var="paramRuleset" param="ruleset"/>
  <dspel:getvalueof var="paramRulesetIndex" param="rulesetIndex"/>
  <dspel:getvalueof var="multisiteMode" param="multisiteMode"/>
  <dspel:getvalueof var="overrideRuleset" param="overrideRuleset"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Titlebar --%>
  <c:set var="titleKey" value="renderGroupRuleset.rulesetTitle" />
  <c:if test="${multisiteMode}">
    <c:set var="titleKey" value="renderGroupRuleset.rulesetTitleDefault" />
    <c:if test="${overrideRuleset}">
      <c:set var="titleKey" value="renderGroupRuleset.rulesetTitleOverride" />
    </c:if>
  </c:if>
  
  <fmt:message var="titleMsg" key="${titleKey}">
    <fmt:param value="${index + 1}" />
  </fmt:message>
  
  <div class="panelHeader">
    <c:choose>
      <c:when test="${overrideRuleset}">
        <table>
          <tr>
            <td valign="middle">
              <nobr><h5><c:out value="${titleMsg}"/></h5></nobr>
            </td>
            <td valign="middle" style="width: 100%">
              <table>
                <tr>
                  <td colspan="2" style="border: none;">
                    <dspel:include page="browseSiteList.jsp">
                      <dspel:param name="rulesetGroup" value="${paramRulesetGroup}"/>
                    </dspel:include>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </c:when>
      <c:otherwise>
        <h5><c:out value="${titleMsg}"/></h5>
      </c:otherwise>
    </c:choose>
  </div>

  <%-- Div which contains each of the individual rules inside this ruleset --%>
  <div class="panelContent">
    <c:if test="${multisiteMode and (not (empty paramRuleset.siteFilter))}">
      <dspel:include page="browseSiteFilter.jsp">
        <dspel:param name="rule" value="${paramRuleset.siteFilter}"/>
      </dspel:include>
    </c:if>
    
    <c:forEach var="acceptRule" items="${paramRuleset.acceptRules}" varStatus="acceptLoop">
      <dspel:include page="browseGroupRule.jsp">
        <dspel:param name="rulesetIndex" value="${paramRulesetIndex}"/>
        <dspel:param name="rule" value="${acceptRule}"/>
        <dspel:param name="ruleType" value="accept"/>
        <dspel:param name="ruleIndex" value="${acceptLoop.index}"/>
      </dspel:include>
    </c:forEach>
    
    <c:forEach var="rejectRule" items="${paramRuleset.rejectRules}" varStatus="rejectLoop">
      <dspel:include page="browseGroupRule.jsp">
        <dspel:param name="rulesetIndex" value="${paramRulesetIndex}"/>
        <dspel:param name="rule" value="${rejectRule}"/>
        <dspel:param name="ruleType" value="reject"/>
        <dspel:param name="ruleIndex" value="${rejectLoop.index}"/>
      </dspel:include>
    </c:forEach>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupRuleset.jsp#2 $$Change: 651448 $--%>
