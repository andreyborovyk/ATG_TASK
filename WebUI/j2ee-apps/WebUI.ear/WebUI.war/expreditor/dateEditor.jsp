<%--
  An expression editor for DateExpression instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/dateEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"               %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Convert the current date value into text that will be displayed in the
       link that pops up the current menu. --%>
  <fmt:message var="dateFormat" key="dateEditor.dateFormat"/>
  <c:set var="date" value="${paramExpression.value}"/>
  <fmt:formatDate var="valueText" value="${date}" pattern="${dateFormat}"/>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix"            value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="literalId"           value="literal_${idSuffix}"/>
  <c:set var="labelId"             value="terminalLabel_${idSuffix}"/>
  <c:set var="menuId"              value="menu_${idSuffix}"/>
  <c:set var="calendarContainerId" value="calendarContainer_${idSuffix}"/>

  <%-- Render the link --%>
  <a id="<c:out value='${labelId}'/>"
     class="dateTerminalLabel terminalLabel expreditorControl"
     hidefocus="true"
     href="javascript:atg.expreditor.displayTerminalMenu({containerId:         '<c:out value="${paramContainer}"/>',
                                                          editorId:            '<c:out value="${paramEditorId}"/>',
                                                          terminalId:          '<c:out value="${paramExpression.identifier}"/>',
                                                          menuId:              '<c:out value="${menuId}"/>',
                                                          anchorId:            '<c:out value="${labelId}"/>',
                                                          textFieldId:         '<c:out value="${literalId}"/>',
                                                          calendarContainerId: '<c:out value="${calendarContainerId}"/>',
                                                          dateFormat:          '<c:out value="${dateFormat}"/>',
                                                          firstDay:            <fmt:message key="dateEditor.firstDay"/>,
                                                          date:                new Date(<c:out value="${date.time}"/>)})"
  ><c:out value="${valueText}" escapeXml="false"/></a>

  <%-- Create an initially invisible popup menu --%>
  <div id="<c:out value='${menuId}'/>"
       class="terminalMenu"
       style="display: none">

    <%-- Include a text field for entering the value.
         NOTE: On Firefox, the text field must be enclosed in a div with
         overflow=auto in order for the caret to appear.  We'll set this
         programatically in atg.expreditor.displayTerminalMenu.  We don't
         want to set it here, because using overflow=auto makes a scrollable
         text field almost invisible on IE7.  See PR 142717. --%>
    <div class="dateTerminalMenuHeader">
      <input type="text"
             id="<c:out value='${literalId}'/>"
             value="<c:out value='${valueText}'/>"
             onkeydown="return atg.expreditor.onLiteralKeyDown(event)"
             onkeypress="return atg.expreditor.onLiteralKeyPress(event)"
             onpropertychange="atg.expreditor.onLiteralInput()"
             oninput="atg.expreditor.onLiteralInput()"
             onpaste="atg.expreditor.onLiteralInput()">
      <fmt:message var="title" key="expressionEditor.popupClose"/>
      <dspel:img page="${config.imageRoot}/icon_popupClose.gif"
                 onclick="atg.expreditor.popdownMenu()"
                 alt="${title}" border="0"/>
    </div>

    <%-- Create an empty div that will act as the parent of the calendar widget --%>
    <div id="<c:out value='${calendarContainerId}'/>" class="dateTerminalMenuBody">
    </div>

  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/dateEditor.jsp#2 $$Change: 651448 $ --%>
