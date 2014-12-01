<%--
  Page fragment that renders a single ruleset group inside of a targeter.

  @param  model               An ExpressionModel component containing the expression to
                              be edited.
  @param  container           The ID of the container for this expression editor.
  @param  rulesetGroup        The ruleset group being edited.
  @param  rulesetGroupIndex   The index of the ruleset group being edited.
  @param  override            Defines rendering mode, true for rulesets (targeters), 
                              false for rules (content groups / user segments).
  @param  multisiteMode       True if we are in a multisite mode.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRulesetGroup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>
  
  <dspel:getvalueof var="paramModel"            param="model"/>
  <dspel:getvalueof var="paramContainer"        param="container"/>
  <dspel:getvalueof var="rulesetGroup"          param="rulesetGroup"/>
  <dspel:getvalueof var="rulesetGroupIndex"     param="rulesetGroupIndex"/>
  <dspel:getvalueof var="override"              param="override"/>
  <dspel:getvalueof var="multisiteMode"         param="multisiteMode"/>
  <dspel:getvalueof var="multisiteContent"      param="multisiteContent"/>
  
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <dspel:importbean var="model" bean="${paramModel}"/>
  
  <c:choose>
    <c:when test="${override}">
      <c:set var="operation" value="addOverrideRuleset"/>
    </c:when>
    <c:otherwise>
      <c:set var="operation" value="addRuleset"/>
    </c:otherwise>
  </c:choose>  

  <dspel:include page="renderTargeterRulesetGroupHeader.jsp">
    <dspel:param name="container" value="${paramContainer}"/>
    <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
    <dspel:param name="rulesetGroupIndex" value="${rulesetGroupIndex}"/>
    <dspel:param name="override" value="${override}"/>
  </dspel:include>
  
  <div class="containerContent">
    
    <c:forEach var="ruleset" items="${rulesetGroup.rulesets}" varStatus="rulesetLoop">
      
      <%-- Render a separator between any rulesets after the first one. --%>
      <c:if test="${rulesetLoop.index > 0}">
        <div class="ruleSetSeperator">
          <span><fmt:message key="targeterExpressionPanel.rulesetDivider"/></span>
        </div>
      </c:if>
      
      <dspel:include page="renderTargeterRuleset.jsp">
        <dspel:param name="ruleset" value="${ruleset}"/>
        <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
        <dspel:param name="rulesetGroupIndex" value="${rulesetGroupIndex}"/>
        <dspel:param name="override" value="${override}"/>
        <dspel:param name="multisiteMode" value="${multisiteMode}"/>
        <dspel:param name="multisiteContent" value="${multisiteContent}"/>
      </dspel:include>
    </c:forEach>
  
    <input type="button" class="small bottom"
           value="<fmt:message key="targeterExpressionPanel.addRuleSet"/>"
           onclick="atg.expreditor.performTargetingOperation({containerId: '<c:out value="${paramContainer}"/>',
                                                              rulesetGroupIndex: '<c:out value="${rulesetGroupIndex}"/>',
                                                              operation:   '<c:out value="${operation}"/>'})"/>
                                                                
  </div>

</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/renderTargeterRulesetGroup.jsp#2 $$Change: 651448 $--%>