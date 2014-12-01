<%--
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRulesetGroup.jsp#2 $$Change: 651448 $
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
  
  <dspel:getvalueof var="paramModel"            param="model"/>
  <dspel:getvalueof var="paramRulesetGroup"      param="rulesetGroup"/>
  <dspel:getvalueof var="paramRulesetGroupIndex" param="rulesetGroupIndex"/>
  <dspel:getvalueof var="override"              param="override"/>
  <dspel:getvalueof var="multisiteMode" param="multisiteMode"/>
  
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <dspel:importbean var="model" bean="${paramModel}"/>
  
  <c:choose>
    <c:when test="${override}">
      <c:set var="titleKey" value="renderTargeterRulesetGroup.titleOverride"/>
    </c:when>
    <c:otherwise>
      <c:set var="titleKey" value="renderTargeterRulesetGroup.titleDefault"/>
    </c:otherwise>
  </c:choose>
  
  <fmt:message var="titleMsg" key="${titleKey}">
    <fmt:param value="${paramRulesetGroupIndex + 1}"/>
  </fmt:message>
  
  <div class="containerHeader" style="min-height: 20px; height: auto">
    <c:choose>
      <c:when test="${override}">
        <table>
          <tr>
            <td valign="middle">
              <nobr><h5><c:out value="${titleMsg}"/></h5></nobr>
            </td>
            <td valign="middle" style="width: 100%">
              <dspel:include page="browseSiteList.jsp">
                <dspel:param name="rulesetGroup" value="${paramRulesetGroup}"/>
              </dspel:include>
            </td>
          </tr>
        </table>
      </c:when>
      <c:otherwise>
        <h5><h5><c:out value="${titleMsg}"/></h5></h5>
      </c:otherwise>
    </c:choose>
  </div>
  
  <div class="containerContent">
    
    <c:forEach var="ruleset" items="${paramRulesetGroup.rulesets}" varStatus="rulesetLoop">
      
      <%-- Render a separator between any rulesets after the first one. --%>
      <c:if test="${rulesetLoop.index > 0}">
        <div class="ruleSetSeperator">
          <span><fmt:message key="targeterExpressionPanel.rulesetDivider"/></span>
        </div>
      </c:if>
      
      <dspel:include page="browseTargeterRuleset.jsp">
        <dspel:param name="ruleset" value="${ruleset}"/>
        <dspel:param name="multisiteMode" value="${multisiteMode}"/>
      </dspel:include>
    </c:forEach>
  
  </div>

</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRulesetGroup.jsp#2 $$Change: 651448 $--%>