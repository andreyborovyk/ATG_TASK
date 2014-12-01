<%--
  Page fragment that renders a ruleset group header for segment or content group.

  @param  container           The ID of the container for this expression editor.
  @param  rulesetGroup        The ruleset group being edited.
  @param  index               The index of the ruleset being edited inside an expression service.
  @param  multisiteMode       True if we are in a multisite mode.
  @param  override            Defines rendering mode, true for rulesets (targeters), 
                              false for rules (content groups / user segments).
  @param  siteFilterAvailable True if site filer should be displayed.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderGroupRulesetHeader.jsp#2 $$Change: 651448 $
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
  <dspel:getvalueof var="ruleset" param="ruleset"/>
  <dspel:getvalueof var="rulesetGroup" param="rulesetGroup"/>
  <dspel:getvalueof var="index" param="rulesetIndex"/>
  <dspel:getvalueof var="override" param="override"/>
  <dspel:getvalueof var="multisiteMode" param="multisiteMode"/>
  <dspel:getvalueof var="siteFilterAvailable" param="siteFilterAvailable"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <c:set var="titleKey" value="renderGroupRuleset.rulesetTitle" />
  <c:if test="${multisiteMode}">
    <c:set var="titleKey" value="renderGroupRuleset.rulesetTitleDefault" />
    <c:if test="${override}">
      <c:set var="titleKey" value="renderGroupRuleset.rulesetTitleOverride" />
    </c:if>
  </c:if>
  
  <fmt:message var="titleMsg" key="${titleKey}">
    <fmt:param value="${index + 1}" />
  </fmt:message>
  
  <c:choose>
    <c:when test="${multisiteMode and override}">
      <c:set var="siteFilterOperation" value="addOverrideSiteFilterDefinition"/>
      <c:set var="includeOperation" value="addOverrideAcceptRule"/>
      <c:set var="excludeOperation" value="addOverrideRejectRule"/>  
      <c:set var="rulesetType" value="override"/>
    </c:when>
    <c:otherwise>
      <c:set var="siteFilterOperation" value="addDefaultSiteFilterDefinition"/>
      <c:set var="includeOperation" value="addAcceptRule"/>
      <c:set var="excludeOperation" value="addRejectRule"/>
      <c:set var="rulesetType" value="default"/>
    </c:otherwise>
  </c:choose>
  
  <div class="panelHeader">
    <c:choose>
      <c:when test="${override}">
        <table>
          <tr>
            <td valign="middle" style="width: 100%">
              <h5 style="white-space: nowrap"><c:out value="${titleMsg}"/></h5>
              <dspel:include page="siteList.jsp">
                 <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
                 <dspel:param name="rulesetGroupIndex" value="${index}"/>
              </dspel:include>
            </td>
            <td valign="top">
              <c:if test="${override}">
                <a class="iconPanelClose"
                    title="<fmt:message key='renderGroupRuleset.deleteSiteOverrideRule'/>"
                    href="javascript:atg.expreditor.performTargetingOperation({
                            containerId: '<c:out value="${container}"/>',
                            rulesetIndex: '<c:out value="${index}"/>',
                            operation:    'deleteOverrideRuleset'})"></a>
              </c:if>
            </td>
          </tr>
        </table>
      </c:when>
      <c:otherwise>
        <h5><c:out value="${titleMsg}"/></h5>
      </c:otherwise>
    </c:choose>
      
    <div class="actions">
      <fmt:message key="renderGroupRuleset.toolbarTitle"/>
      <c:if test="${siteFilterAvailable}">
        <input type="button"
            <c:choose>
              <c:when test="${not empty ruleset.siteFilter}">
                class="small disabled"
                onclick="return false"
              </c:when>
              <c:otherwise>
                class="small"
                onclick="atg.expreditor.performTargetingOperation({
                    containerId:  '<c:out value="${container}"/>',
                    rulesetIndex: '<c:out value="${index}"/>',
                    operation:    '<c:out value="${siteFilterOperation}"/>'})"
              </c:otherwise>
            </c:choose>
            value="<fmt:message key='renderGroupRuleset.asSeenOn'/>"/>
      </c:if>
      
      <input type="button" class="small"
          value="<fmt:message key='renderGroupRuleset.includeRule'/>"
          onclick="atg.expreditor.performTargetingOperation({
                  containerId:  '<c:out value="${container}"/>',
                  rulesetIndex: '<c:out value="${index}"/>',
                  operation:    '<c:out value="${includeOperation}"/>'})"/>
      <input type="button" class="small"
          value="<fmt:message key='renderGroupRuleset.excludeRule'/>"
          onclick="atg.expreditor.performTargetingOperation({
                  containerId:  '<c:out value="${container}"/>',
                  rulesetIndex: '<c:out value="${index}"/>',
                  operation:    '<c:out value="${excludeOperation}"/>'})"/>
    </div>
  </div>
  
</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderGroupRulesetHeader.jsp#2 $$Change: 651448 $--%>