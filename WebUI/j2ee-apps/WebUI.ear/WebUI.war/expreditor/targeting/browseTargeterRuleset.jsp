<%--
  Page fragment that displays a read-only view of a single ruleset inside of
  a targeter.

  @param  ruleset         The ruleset being edited.
  @param  rulesetIndex    The index of the ruleset being edited inside an expression service.

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRuleset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramRuleset" param="ruleset"/>
  <dspel:getvalueof var="multisiteMode" param="multisiteMode"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <div class="panelHeader">
    <h5><fmt:message key="renderTargeterRuleset.rulesetTitle"/></h5>
  </div>

  <%-- Div which contains each of the individual rules inside this ruleset --%>
  <div class="panelContent">
    <c:if test="${multisiteMode and (not (empty paramRuleset.siteFilter))}">
      <dspel:include page="browseSiteFilter.jsp">
        <dspel:param name="rule" value="${paramRuleset.siteFilter}"/>
      </dspel:include>
    </c:if>
  
    <c:forEach var="acceptRule" items="${paramRuleset.acceptRules}" varStatus="acceptLoop">
      <dspel:include page="browseTargeterRule.jsp">
        <dspel:param name="rule" value="${acceptRule}"/>
      </dspel:include>
    </c:forEach>

    <c:forEach var="rejectRule" items="${paramRuleset.rejectRules}" varStatus="rejectLoop">
      <dspel:include page="browseTargeterRule.jsp">
        <dspel:param name="rule" value="${rejectRule}"/>
      </dspel:include>
    </c:forEach>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseTargeterRuleset.jsp#2 $$Change: 651448 $--%>
