<%--
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseSiteFilter.jsp#2 $$Change: 651448 $
--%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ee"
    uri="http://www.atg.com/taglibs/expreditor_rt"%>
<%@ taglib prefix="web-ui"
    uri="http://www.atg.com/taglibs/web-ui_rt"%>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramRule" param="rule"/>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Render the header and any toolbar buttons --%>
  <div class="panelHeader">
    <h5><fmt:message key="renderGroupRuleset.asSeenOn" /></h5>
  </div>
  
  <%-- Render the div and table that enclose each Include or Exclude rule --%>
  <div class="panelContent expressionBlocks">
    <table>
      <tr>
        <td colspan="2" style="border:none;">
          <web-ui:renderExpression var="expression"
              expression="${paramRule.expression}"/>
          <c:out value="${expression}"/>
        </td>
      </tr>
    </table>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/targeting/browseSiteFilter.jsp#2 $$Change: 651448 $--%>