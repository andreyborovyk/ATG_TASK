<%--
  Page fragment that displays a read-only view of the contents of a segment
  or content group.

  @param  model          An ExpressionModel component containing the expression.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="ee"
    uri="http://www.atg.com/taglibs/expreditor_rt"%>

<dspel:page>
  
  <dspel:getvalueof var="expressionModelPath" param="model"/>
  <dspel:importbean var="model" bean="${expressionModelPath}"/>
  
  <ee:isMultisiteMode var="multisiteMode"/>
  <ee:isMultisiteEnabled var="multisiteContent" expressionModel="${model}"/>
  
  <%-- Render the rulesets. --%>
  <c:forEach var="ruleset" items="${model.defaultRulesetGroup.rulesets}" varStatus="rulesetLoop">
    <dspel:include page="browseGroupRuleset.jsp">
      <dspel:param name="ruleset" value="${ruleset}"/>
      <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
      <dspel:param name="multisiteMode" value="${multisiteMode}"/>
      <dspel:param name="overrideRuleset" value="${false}"/>
    </dspel:include>
  </c:forEach>
  
  <c:if test="${multisiteMode}">
    <c:forEach var="rulesetGroup" items="${model.overrideRulesetGroups}" varStatus="rulesetLoop">
      <c:forEach var="ruleset" items="${rulesetGroup.rulesets}">
        <dspel:include page="browseGroupRuleset.jsp">
          <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
          <dspel:param name="ruleset" value="${ruleset}"/>
          <dspel:param name="rulesetIndex" value="${rulesetLoop.index}"/>
          <dspel:param name="multisiteMode" value="${multisiteMode}"/>
          <dspel:param name="overrideRuleset" value="${true}"/>
        </dspel:include>
      </c:forEach>
    </c:forEach>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseGroupPanel.jsp#2 $$Change: 651448 $--%>
