<%--
  Page fragment for browsing the contents of a targeter.

  @param  model   An ExpressionModel component containing the targeter contents.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ee"
    uri="http://www.atg.com/taglibs/expreditor_rt"%>

<dspel:page>

  <dspel:getvalueof var="expressionModelPath" param="model"/>
  <dspel:importbean var="model" bean="${expressionModelPath}"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <ee:isMultisiteMode var="multisiteMode"/>
  <ee:isMultisiteEnabled var="multisiteContent" expressionModel="${model}"/>
  
  
  <c:choose>
    <c:when test="${multisiteMode}">
      <%-- Render default rulesets  --%>   
      <dspel:include page="browseTargeterRulesetGroup.jsp">
        <dspel:param name="model" value="${model}"/>
        <dspel:param name="rulesetGroup" value="${model.defaultRulesetGroup}"/>
        <dspel:param name="override" value="${false}"/>
        <dspel:param name="multisiteMode" value="${multisiteMode}"/>
      </dspel:include>
      
      <%-- Render override rulesets  --%>
      <c:forEach var="rulesetGroup" items="${model.overrideRulesetGroups}" varStatus="groupLoop">
        <%-- Render a separator --%>
        <%-- todo: move style to CSS --%>
        <div style="width:90%; height:10px;"></div>
        <dspel:include page="browseTargeterRulesetGroup.jsp">
          <dspel:param name="model" value="${model}"/>
          <dspel:param name="rulesetGroup" value="${rulesetGroup}"/>
          <dspel:param name="rulesetGroupIndex" value="${groupLoop.index}"/>
          <dspel:param name="override" value="${true}"/>
          <dspel:param name="multisiteMode" value="${multisiteMode}"/>
        </dspel:include>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <c:forEach var="ruleset" items="${model.defaultRulesetGroup.rulesets}" varStatus="rulesetLoop">
        
        <%-- Render a separator between any rulesets after the first one. --%>
        <c:if test="${rulesetLoop.index > 0}">
          <div class="ruleSetSeperator">
            <span><fmt:message key="targeterExpressionPanel.rulesetDivider"/></span>
          </div>
        </c:if>
        
        <dspel:include page="browseTargeterRuleset.jsp">
          <dspel:param name="ruleset" value="${ruleset}"/>
        </dspel:include>
      </c:forEach>
    </c:otherwise>
  </c:choose>
  
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterPanel.jsp#2 $$Change: 651448 $--%>
