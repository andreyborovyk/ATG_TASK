<%--
  An expression editor for TokenExpression instances.

  This page expects the following parameters:
    expression - The Expression instance to be rendered
    renderTerminal - Flag indicating whether to actually render the expression

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/tokenExpressionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramExpression"     param="expression"/>
  <dspel:getvalueof var="paramRenderTerminal" param="renderTerminal"/>

  <c:choose>
    <c:when test="${paramRenderTerminal}">

      <%-- Render our token value --%>
      <c:out value="${paramExpression.text}"/>

    </c:when>
    <c:otherwise>

      <%-- Render a page that may end up recursively including this page again,
           passing the renderTerminal flag.  This should be re-written once we
           support JSP2.0 to use the OPARAM equivalent that allows you to pass
           a template to a child page to render in its context. --%>
      <dspel:include page="terminalExpressionEditor.jsp">
        <dspel:param name="expression" value="${paramExpression}"/>
      </dspel:include>

    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/tokenExpressionEditor.jsp#2 $$Change: 651448 $ --%>
