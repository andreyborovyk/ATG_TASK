<%--
  Page fragment that performs an operation on an expression.

  @param  model       An ExpressionModel component containing the expression to
                      be edited.
  @param  operation   The operation to be performed on the expression prior to
                      rendering it.  Valid values are "updateChoice",
                      "updateLiteral", "updateToken", and "clickButton".
  @param  terminalId  The ID of the sub-expression on which the given operation
                      is to be performed.
  @param  value       The value to be assigned to the sub-expression on which
                      the given operation is to be performed.
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"           %>

<dspel:page>

  <%-- Invoke the specified ExpressionController method --%>
  <c:if test="${not empty param.operation}">
    <c:catch var="exception">
      <web-ui:invoke componentPath="/atg/web/expreditor/ExpressionController" method="${param.operation}">
        <web-ui:parameter value="${param.model}"/>
        <web-ui:parameter value="${param.terminalId}"/>
        <web-ui:parameter type="java.lang.String" value="${param.value}"/>
      </web-ui:invoke>
    </c:catch>
    <%-- TBD: don't just blindly ignore exceptions --%>
    <%-- NOTE, however, that the AssetManager search tab reuses the "operation" parameter! --%>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/expressionController.jsp#2 $$Change: 651448 $--%>
