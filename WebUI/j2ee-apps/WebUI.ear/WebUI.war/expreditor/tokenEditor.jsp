<%--
  An expression editor for TokenExpression instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/tokenEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"           %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <%-- Get information about the "sibling choices" for this token --%>
  <ee:createTerminalChoice var="terminalChoice" expression="${paramExpression}"/>

  <c:choose>

    <%-- If sibling choices exist, we need to render the token as a clickable
         link that will pop up a menu containing the available choices. --%>
    <c:when test="${terminalChoice.nonTrivial && not empty terminalChoice.terminals && terminalChoice.terminals.size > 1}">

      <%-- If the token text is marked as "hidden", we render the link as a
           diamond icon instead of using the token text. --%>
      <ee:getConstructAttribute var="hidden"
                                expression="${paramExpression}"
                                attribute="hidden"/>
      <c:choose>
        <c:when test="${not empty hidden}">
          <c:set var="valueText" value="&nbsp;"/>
          <c:set var="valueClass" value="terminalLabelHidden"/>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${not empty paramExpression.text}">
              <c:set var="valueText" value="${paramExpression.text}"/>
            </c:when>
            <c:otherwise>
              <c:set var="valueText" value="${paramExpression.value}"/>
            </c:otherwise>
          </c:choose>
          <c:set var="valueClass" value="tokenTerminalLabel terminalLabel"/>
        </c:otherwise>
      </c:choose>

      <%-- Derive IDs for page elements --%>
      <c:set var="idSuffix" value="${paramEditorId}${paramExpression.identifier}"/>
      <c:set var="labelId" value="terminalLabel_${idSuffix}"/>
      <c:set var="menuId"  value="menu_${idSuffix}"/>

      <%-- Render the link --%>
      <c:set var="currentChoice" value="${0}" />

      <c:set var="choices" value="" />
      <c:forEach var="terminal"
                 items="${terminalChoice.terminals.terminals}"
                 varStatus="loop">
        <c:if test="${loop.index > 0}"><c:set var="choices" value="${choices}, " /></c:if>
        <c:set var="choices"><c:out value="${choices}" escapeXml="false" />'<web-ui:escapeJS value='${terminal.description}'/>'</c:set>
        <c:if test="${terminal == paramExpression}"><c:set var="currentChoice" value="${loop.index}" /></c:if>
      </c:forEach>

      <a id="<c:out value='${labelId}'/>"
         class="<c:out value='${valueClass}'/> expreditorControl"
         hidefocus="true"
         href="#"
         onclick="atg.expreditor.displayTerminalMenu({containerId:        '<c:out value="${paramContainer}"/>',
                                                      editorId:           '<c:out value="${paramEditorId}"/>',
                                                      terminalId:         '<c:out value="${paramExpression.identifier}"/>',
                                                      menuId:             '<c:out value="${menuId}"/>',
                                                      anchorId:           '<c:out value="${labelId}"/>',
                                                      literalId:          null,
                                                      literalValue:       null,
                                                      choices:            [<c:out value="${choices}" escapeXml="false"/>],
                                                      choiceClass:        'terminalMenuLabel',
                                                      currentChoiceClass: 'terminalMenuLabelCurrent',
                                                      currentChoiceIndex: <c:out value="${currentChoice}" />,
                                                      isDynamic:          true 
                                                     });return false;"
      ><c:out value="${valueText}" escapeXml="false"/></a>

      <%-- Include an initially invisible popup menu containing all of the
           available choices --%>
      <dspel:include page="terminalMenu.jsp">
        <dspel:param name="terminalChoice" value="${terminalChoice}"/>
        <dspel:param name="menuId"         value="${menuId}"/>
      </dspel:include>

    </c:when>
    <c:otherwise>

      <%-- For a trivial token, just render the token text --%>
      <c:out value="${paramExpression.text}"/>

    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/tokenEditor.jsp#2 $$Change: 651448 $ --%>
