<%--
  A page fragment that renders either a drop-down list box containing the set of
  sibling terminals for a TerminalExpression, or an expression editor for the
  TerminalExpression itself.

  This page expects the following parameters:
    expression - The Expression instance to be rendered

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/terminalExpressionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"            %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>

  <ee:createTerminalChoice var="terminalChoice" expression="${paramExpression}"/>

  <c:choose>
    <c:when test="${terminalChoice.nonTrivial && not empty terminalChoice.terminals && terminalChoice.terminals.size > 1}">

      <%-- Generate our choice control --%>

      <%-- TBD --%>
      <%-- This does not currently support a literal as a leading terminal in a
           choice.  The ACC places the literal editor in the same location as
           the choice, but HTML isn't that expressive.  As a work-around, I
           think we will have to have the 'Type a string...' as a token followed
           by a literal, rather than having just a literal with a description of
           'Type a string...' --%>

      <select id="select_<c:out value='${paramExpression.identifier}'/>"
              onchange="choiceExprUpdated('select_<c:out value="${paramExpression.identifier}"/>',
                                          '<c:out value="${paramExpression.identifier}"/>')">

        <%-- Iterate over the terminals contained in the TerminalChoice's TerminalSet --%>
        <c:forEach var="optionTerminalExpression"
                   items="${terminalChoice.terminals.terminals}"
                   varStatus="varStatus">

          <c:set var="selected" value=""/>
          <c:set var="terminalText" value="${optionTerminalExpression.description}"/>

          <c:if test="${optionTerminalExpression == paramExpression}">
            <c:set var="selected" value="selected"/>
            <%-- If the expression has an editor-text attribute, then the value
                 of that text should be displayed if the terminal is selected. --%>
            <c:if test="${not empty optionTerminalExpression.text}">
              <c:set var="terminalText" value="${optionTerminalExpression.text}"/>
            </c:if>
          </c:if>

          <option value="<c:out value='${varStatus.index}'/>" <c:out value="${selected}"/>>
            <c:out value="${terminalText}"/>
          </option>

        </c:forEach>
      </select>

    </c:when>
    <c:otherwise>

      <%-- A choice is trivial if it contains only one terminal choice.  In this
           case, we should render the token or literal editor --%>
      <ee:getEditor var="editor" expression="${paramExpression}"/>

      <c:choose>
        <c:when test="${empty editor}">

          <%-- No JSP editor for the terminal expression, just render the value
               as text --%>
          <c:out value="${paramExpression.value}"/>

        </c:when>
        <c:otherwise>

          <%-- Include the JSP page to use for rendering the terminal expression
               if it is not a significant leading terminal of a parent
               sub-expression.  A parameter named 'renderTerminal' with a value
               of 'true' will be passed to this page as an indicator that it
               should render its terminal value or editor.  Note: This should be
               re-factored once we support JSP2.0 and there is a concept similar
               to JHTML OPARAMs --%>

          <dspel:include otherContext="${editor.contextRoot}" page="${editor.page}">
            <dspel:param name="expression" value="${paramExpression}"/>
            <dspel:param name="renderTerminal" value="true"/>
          </dspel:include>

        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/terminalExpressionEditor.jsp#2 $$Change: 651448 $ --%>
