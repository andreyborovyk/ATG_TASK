<%--
  Page fragment that renders a ruleset group header for a targeter.

  @param  container           The ID of the container for this expression editor.
  @param  rulesetGroup        The ruleset group being edited.
  @param  rulesetGroupIndex   The index of the ruleset group being edited.
  @param  override            Defines rendering mode, true for rulesets (targeters), 

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRulesetGroupHeader.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>

<dspel:page>

  <dspel:getvalueof var="container" param="container"/>
  <dspel:getvalueof var="rulesetGroup" param="rulesetGroup"/>
  <dspel:getvalueof var="index" param="rulesetGroupIndex"/>
  <dspel:getvalueof var="override" param="override"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <c:choose>
    <c:when test="${override}">
      <c:set var="titleKey" value="renderTargeterRulesetGroup.titleOverride"/>
    </c:when>
    <c:otherwise>
      <c:set var="titleKey" value="renderTargeterRulesetGroup.titleDefault"/>
    </c:otherwise>
  </c:choose>
  
  <fmt:message var="titleMsg" key="${titleKey}">
    <fmt:param value="${index + 1}"/>
  </fmt:message>
  
  <div class="containerHeader" style="min-height: 20px; height: auto">
    <c:choose>
      <c:when test="${override}">
        <table>
          <tr>
            <td valign="top" style="width: 100%">
              <h5 style="white-space: nowrap"><c:out value="${titleMsg}"/></h5>
              <dspel:include page="siteList.jsp">
                <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
                <dspel:param name="rulesetGroupIndex" value="${index}"/>
              </dspel:include>
            </td>
            <td valign="top" style="width: 50px">
              <div style="height: 20px; width: 50px; overflow:hidden;">
                <a href="#" class="iconPanelClose"
                    title="<fmt:message key="renderTargeterRulesetGroup.deleteRulesets"/>"
                    onclick="atg.expreditor.performTargetingOperation({
                            containerId:  '<c:out value="${container}"/>',
                            rulesetGroupIndex: '<c:out value="${index}"/>',
                            operation:    'deleteRulesetGroup'})"></a>
                <a href="#" class="iconPanelMinimize rulesTrigger"
                    title="<fmt:message key="renderTargeterRulesetGroup.hideRulesets"/>"
                    onclick="atg.expreditor.rulesDisplay(
                          this,
                          '<fmt:message key="renderTargeterRuleset.hideRuleset"/>',
                          '<fmt:message key="renderTargeterRuleset.showRuleset"/>');"></a>
              </div>
            </td>
          </tr>
        </table>
      </c:when>
      <c:otherwise>
        <h5><c:out value="${titleMsg}"/></h5>
        <div class="actions">
          <a href="#" class="iconPanelMinimize rulesTrigger"
              title="<fmt:message key="renderTargeterRulesetGroup.hideRulesets"/>"
              onclick="atg.expreditor.rulesDisplay(
                    this,
                    '<fmt:message key="renderTargeterRuleset.hideRuleset"/>',
                    '<fmt:message key="renderTargeterRuleset.showRuleset"/>');"></a>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
  
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRulesetGroupHeader.jsp#2 $$Change: 651448 $--%>