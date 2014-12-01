<%--
  An expression editor for LiteralExpression instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/literalEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"           %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get information about the "sibling choices" for this literal --%>
  <ee:createTerminalChoice var="terminalChoice" expression="${paramExpression}"/>

  <%-- Determine the text to be displayed in the link that pops up the input menu.
       This is the current value of the literal, or an underline if no value exists --%>
  <c:choose>
    <c:when test="${not empty paramExpression.value}">
      <c:set var="valueText" value="${paramExpression.value}"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="valueText" key="expressionEditor.blankString"/>
    </c:otherwise>
  </c:choose>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix"  value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="literalId" value="literal_${idSuffix}"/>
  <c:set var="labelId"   value="terminalLabel_${idSuffix}"/>
  <c:set var="menuId"    value="menu_${idSuffix}"/>

  <%-- Render the link --%>
  <c:set var="currentChoice" value="0" />
  <c:forEach var="terminal"
             items="${terminalChoice.terminals.terminals}"
             varStatus="loop">
    <c:if test="${loop.index > 0}"><c:set var="choices" value="${choices}, " /></c:if>
    <c:set var="choices"><c:out value="${choices}" escapeXml="false" />'<web-ui:escapeJS value='${terminal.description}'/>'</c:set>
    <c:if test="${terminal == paramExpression}"><c:set var="currentChoice" value="${loop.index}" /></c:if>
  </c:forEach>
  <a id="<c:out value='${labelId}'/>"
     class="literalTerminalLabel terminalLabel expreditorControl"
     hidefocus="true"
     href="#"
     onclick="javascript:atg.expreditor.displayTerminalMenu({containerId:        '<c:out value="${paramContainer}"/>',
                                                          editorId:           '<c:out value="${paramEditorId}"/>',
                                                          terminalId:         '<c:out value="${paramExpression.identifier}"/>',
                                                          menuId:             '<c:out value="${menuId}"/>',
                                                          anchorId:           '<c:out value="${labelId}"/>',
                                                          blankValue:         '<fmt:message key="expressionEditor.blankString"/>',
                                                          textFieldId:        '<c:out value="${literalId}"/>',
                                                          literalValue:       '<c:out value="${paramExpression.value}"/>',
                                                          choices:            [<c:out value="${choices}" escapeXml="false" />],
                                                          choiceClass:        'terminalMenuLabel',
                                                          currentChoiceClass: 'terminalMenuLabelCurrent',
                                                          currentChoiceIndex: <c:out value="${currentChoice}" />,
                                                          isDynamic:          true 
                                                         });return false;"
      ><c:out value="${valueText}" escapeXml="false"/></a>

  <%-- Include an initially invisible popup menu containing a text field and
       all of the available choices --%>
  <dspel:include page="terminalMenu.jsp">
    <dspel:param name="terminalChoice" value="${terminalChoice}"/>
    <dspel:param name="menuId"         value="${menuId}"/>
    <dspel:param name="literalId"      value="${literalId}"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/literalEditor.jsp#2 $$Change: 651448 $ --%>
