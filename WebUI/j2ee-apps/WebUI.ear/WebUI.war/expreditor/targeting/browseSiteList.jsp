<%--
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseSiteList.jsp#2 $$Change: 651448 $
--%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="ee"
    uri="http://www.atg.com/taglibs/expreditor_rt"%>
<%@ taglib prefix="web-ui"
    uri="http://www.atg.com/taglibs/web-ui_rt"%>

<dspel:page>
  
  <dspel:getvalueof var="paramRulesetGroup" param="rulesetGroup"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <table>
    <tr>
      <td>
        <h5 style="padding-left: 10px; padding-right: 10px;"><fmt:message key="siteList.executesOn"/></h5>
      </td>
      <td>
        <web-ui:renderExpression var="expression"
            expression="${paramRulesetGroup.siteList.expression}"/>
        <c:out value="${expression}"/>
      </td>
    </tr>
  </table>

</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseSiteList.jsp#2 $$Change: 651448 $--%>