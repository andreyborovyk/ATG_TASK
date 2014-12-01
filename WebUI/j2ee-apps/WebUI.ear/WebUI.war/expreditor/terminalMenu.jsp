<%--
  A page fragment that renders an initially invisible "popup menu" containing
  a TerminalExpression and all of the "sibling" TerminalExpressions that can
  be substituted in its place.  If the TerminalExpression is a LiteralExpression,
  then there will also be a text field at the top of the menu for changing the
  value of the LiteralExpression.

  @param  expression      The expression to be rendered
  @param  terminalChoice  The TerminalChoice object containing terminal information
  @param  menuId          The ID to be used for the menu element
  @param  literalId       The ID to be used for the input field, if any

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/terminalMenu.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramTerminalChoice" param="terminalChoice"/>
  <dspel:getvalueof var="paramMenuId"         param="menuId"/>

  <%-- We need a way to make the menu scroll when it contains too many items to
       display simultaneously.  For now, we'll use a fixed-size div with overflow=auto
       if there are more than 15 items. --%>
  <c:choose>
    <c:when test="${paramTerminalChoice.terminals.size > 15}">
      <c:set var="menuClass" value="terminalMenuScrolling"/>
    </c:when>
    <c:otherwise>
      <c:set var="menuClass" value="terminalMenu"/>
    </c:otherwise>
  </c:choose>

  <%-- Create an initially invisible popup menu --%>
  <div id="<c:out value='${paramMenuId}'/>"
       class="<c:out value='${menuClass}'/>"
       style="display: none">
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/terminalMenu.jsp#2 $$Change: 651448 $ --%>
