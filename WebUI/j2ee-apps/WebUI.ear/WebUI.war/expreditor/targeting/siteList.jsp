<%--
  Page fragment that renders a site list expression.

  @param  ruleType            The type of rule (either accept or reject).
  @param  rulesetGroup        The ruleset group being edited.
  @param  rulesetGroupIndex   The index of the ruleset group being edited.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/siteList.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="ee"
    uri="http://www.atg.com/taglibs/expreditor_rt"%>

<dspel:page>
  
  <dspel:getvalueof var="rulesetType" param="rulesetType"/>
  <dspel:getvalueof var="rulesetGroup" param="rulesetGroup"/>
  <dspel:getvalueof var="rulesetGroupIndex" param="rulesetGroupIndex"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <h5 style="padding-left: 10px; padding-right: 10px;"><fmt:message key="siteList.executesOn"/></h5>
  
  <%-- Create an ID for the expression editor containing info about this rule --%>
  <ee:createTargetingExpressionId var="exprid"
      rulesetGroupType="override"
      rulesetGroupIndex="${rulesetGroupIndex}"
      rulesetIndex="${0}"
      ruleType="siteList"
      ruleIndex="${0}"
      conditionType="content"/>
  
  <%-- Render the condition being used to edit this rule --%>
  <ee:getTerminalEditors var="editors" expression="${rulesetGroup.siteList.expression}"/>
  <c:forEach var="editor" items="${editors}">
    <dspel:include otherContext="${editor.contextRoot}" page="${editor.page}">
      <dspel:param name="expression" value="${editor.expression}"/>
      <dspel:param name="editorId" value="${exprid}"/>
    </dspel:include>
  </c:forEach>
</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/siteList.jsp#2 $$Change: 651448 $--%>