<%--
  Page fragment that renders a single ruleset inside of a targeter.

  @param  ruleset           The ruleset being edited.
  @param  rulesetIndex      The index of the ruleset being edited inside an expression service.
  @param  container         The ID of the container for this expression editor
  @param  rulesetGroupIndex The index of the ruleset group being edited.
  @param  override          Defines rendering mode, true for rulesets (targeters), 
                            false for rules (content groups / user segments).
  @param  multisiteMode     True if we are in a multisite mode.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRuleset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramRuleset" param="ruleset"/>
  <dspel:getvalueof var="paramRulesetIndex" param="rulesetIndex"/>
  <dspel:getvalueof var="paramContainer" param="container"/>
  <dspel:getvalueof var="rulesetGroupIndex" param="rulesetGroupIndex"/>
  <dspel:getvalueof var="override" param="override"/>
  <dspel:getvalueof var="multisiteMode" param="multisiteMode"/>
  <dspel:getvalueof var="multisiteContent" param="multisiteContent"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:choose>
    <c:when test="${override}">
      <c:set var="deleteOperation" value="deleteOverrideRuleset"/>
      <c:set var="moveUpOperation" value="moveOverrideRulesetUp"/>
      <c:set var="moveDownOperation" value="moveOverrideRulesetDown"/>
      <c:set var="showOperation" value="addOverrideAcceptRule"/>
      <c:set var="hideOperation" value="addOverrideRejectRule"/>
      <c:set var="siteFilterOperation" value="addOverrideSiteFilterDefinition"/>
    </c:when>
    <c:otherwise>
      <c:set var="deleteOperation" value="deleteRuleset"/>
      <c:set var="moveUpOperation" value="moveRulesetUp"/>
      <c:set var="moveDownOperation" value="moveRulesetDown"/>
      <c:set var="showOperation" value="addAcceptRule"/>
      <c:set var="hideOperation" value="addRejectRule"/>
      <c:set var="siteFilterOperation" value="addDefaultSiteFilterDefinition"/>
    </c:otherwise>
  </c:choose>
  
  <%-- Toolbar --%>
  <div class="panelHeader">
    <h5><fmt:message key="renderTargeterRuleset.rulesetTitle"/></h5>
    <div class="actions">

      <a href="#" class="iconPanelClose"
         title="<fmt:message key="renderTargeterRuleset.deleteRuleset"/>"
         onclick="atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                            rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                            rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                            operation:    '<c:out value="${deleteOperation}"/>'})"></a>

      <a href="#" class="iconPanelMinimize rulesTrigger"
         title="<fmt:message key="renderTargeterRuleset.hideRuleset"/>"
         onclick="atg.expreditor.rulesDisplay(this,
                                             '<fmt:message key="renderTargeterRuleset.hideRuleset"/>',
                                             '<fmt:message key="renderTargeterRuleset.showRuleset"/>');"></a>

      <a href="#" class="iconPanelMoveDown"
         title="<fmt:message key="renderTargeterRuleset.moveRulesetDown"/>"
         onclick="atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                            rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                            rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                            operation:    '<c:out value="${moveDownOperation}"/>'})"></a>

      <a href="#" class="iconPanelMoveUp"
         title="<fmt:message key="renderTargeterRuleset.moveRulesetUp"/>"
         onclick="atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                            rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                            rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                            operation:    '<c:out value="${moveUpOperation}"/>'})"></a>

      <span> <fmt:message key="renderTargeterRuleset.toolbarTitle"/>
        <c:if test="${multisiteMode and multisiteContent}">
          <input type="button"
              <c:choose>
                <c:when test="${not empty paramRuleset.siteFilter}">
                  class="small disabled"
                  onclick="return false"
                </c:when>
                <c:otherwise>
                  class="small"
                  onclick="atg.expreditor.performTargetingOperation({
                      containerId:  '<c:out value="${paramContainer}"/>',
                      rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                      rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                      operation:    '<c:out value="${siteFilterOperation}"/>'})"
                </c:otherwise>
              </c:choose>
              value="<fmt:message key='renderGroupRuleset.asSeenOn'/>"/>
        </c:if>

        <input type="button" class="small"
               value="<fmt:message key="renderTargeterRuleset.showRule"/>"
               onclick="atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                                  rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                                  rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                                  operation:    '<c:out value="${showOperation}"/>'})"/>

        <input type="button" class="small"
               value="<fmt:message key="renderTargeterRuleset.hideRule"/>"
               onclick="atg.expreditor.performTargetingOperation({containerId:  '<c:out value="${paramContainer}"/>',
                                                                  rulesetIndex: '<c:out value="${paramRulesetIndex}"/>',
                                                                  rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                                  operation:    '<c:out value="${hideOperation}"/>'})"/>
       </span>
    </div>
  </div>

  <%-- Div which contains each of the individual rules inside this ruleset --%>
  <div class="panelContent">
    <c:if test="${multisiteMode and (not (empty paramRuleset.siteFilter))}">
      <dspel:include page="renderSiteFilter.jsp">
        <dspel:param name="rulesetGroupIndex" value="${rulesetGroupIndex}"/>
        <dspel:param name="rulesetIndex" value="${paramRulesetIndex}"/>
        <dspel:param name="rule" value="${paramRuleset.siteFilter}"/>
        <dspel:param name="overrideRuleset" value="${override}"/>
      </dspel:include>
    </c:if>
  
    <c:forEach var="acceptRule" items="${paramRuleset.acceptRules}" varStatus="acceptLoop">
      <dspel:include page="renderTargeterRule.jsp">
        <dspel:param name="rulesetIndex" value="${paramRulesetIndex}"/>
        <dspel:param name="rule" value="${acceptRule}"/>
        <dspel:param name="ruleType" value="accept"/>
        <dspel:param name="ruleIndex" value="${acceptLoop.index}"/>
        <dspel:param name="rulesetGroupIndex" value="${rulesetGroupIndex}"/>
        <dspel:param name="override" value="${override}"/>
      </dspel:include>
    </c:forEach>

    <c:forEach var="rejectRule" items="${paramRuleset.rejectRules}" varStatus="rejectLoop">
      <dspel:include page="renderTargeterRule.jsp">
        <dspel:param name="rulesetIndex" value="${paramRulesetIndex}"/>
        <dspel:param name="rule" value="${rejectRule}"/>
        <dspel:param name="ruleType" value="reject"/>
        <dspel:param name="ruleIndex" value="${rejectLoop.index}"/>
        <dspel:param name="rulesetGroupIndex" value="${rulesetGroupIndex}"/>
        <dspel:param name="override" value="${override}"/>
      </dspel:include>
    </c:forEach>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRuleset.jsp#2 $$Change: 651448 $--%>
