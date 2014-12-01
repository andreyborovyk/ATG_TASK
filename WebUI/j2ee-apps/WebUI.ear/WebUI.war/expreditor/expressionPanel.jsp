<%--
  The root of the expression editor.

  This page expects the following parameters:
    expressionModel - The ExpressionModel component that holds the expression tree

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/expressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"            %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramExpressionModel" param="expressionModel"/>

  <%-- For compatibility with ACO, we also allow the expressionModel to reside
       in the request scope. --%>
  <c:if test="${empty paramExpressionModel}">
    <c:set var="paramExpressionModel" value="${requestScope.expressionModel}"/>
  </c:if>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/expreditor.js">
  </script>

  <%-- Get the form handler associated with the expression --%>
  <ee:getFormHandler var="formHandler" expression="${paramExpressionModel.rootExpression}"/>

  <dspel:getvalueof var="thisPage" bean="/OriginatingRequest.requestURIwithQueryString"/>

  <%-- This is the form that actually gets submitted.  All
       controls in the expression editor perform javascript
       callbacks that will then modify this form and submit it --%>
  <dspel:form id="expressionSubmitForm"
              action="${thisPage}"
              method="post">

    <%-- These hidden inputs are modified by functions in expreditor.js --%>
    <dspel:input id="terminalIdentifier" type="hidden" value=""
                 bean="${formHandler}.terminalIdentifier"/>
    <dspel:input id="terminalChoiceIndex" type="hidden" value=""
                 bean="${formHandler}.terminalChoiceIndex"/>
    <dspel:input id="literalTextValue" type="hidden" value=""
                 bean="${formHandler}.literalTextValue"/>
    <dspel:input id="mutableTokenValue" type="hidden" value=""
                 bean="${formHandler}.mutableTokenValue"/>

    <%-- TBD --%>
    <%-- Need to submit the ExpressionModel to use as a path string so that we
         can eventually have multiple expression models in use at the same time.
         This will allow us to do scenario editor type setups w/ multiple
         expression trees. --%>

    <%-- These hidden submit buttons are invoked by functions in expreditor.js --%>    
    <dspel:input style="display:none" id="submitLiteral"
                 bean="${formHandler}.updateLiteral"
                 type="submit" priority="-10"/>
    <dspel:input style="display:none" id="submitToken"
                 bean="${formHandler}.updateMutableToken"
                 type="submit" priority="-10"/>
    <dspel:input style="display:none" id="submitChoose"
                 bean="${formHandler}.updateChoice"
                 type="submit" priority="-10"/>
    <dspel:input style="display:none" id="submitButton"
                 bean="${formHandler}.buttonClicked"
                 type="submit" priority="-10"/>
  </dspel:form>

  <%-- This form will contain all expression editor controls.  Those controls
       will perform javascript callbacks that will update the above form and
       submit it. --%>
  <form id="expressionForm">
    <dspel:include page="renderExpression.jsp">
      <dspel:param name="expression" value="${paramExpressionModel.rootExpression}"/>
    </dspel:include>
  </form>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/expressionPanel.jsp#2 $$Change: 651448 $ --%>
