<%--
  An expression editor for TagSetExpression instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/enumSetEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"               %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Determine the text to be displayed in the link that pops up the input menu.
       This is the current value of the expression, or an underline if no value exists --%>
  <c:choose>
    <c:when test="${not empty paramExpression.value}">
      <c:set var="valueText" value="${paramExpression.text}"/>
    </c:when>
    <c:otherwise>
      <fmt:message var="valueText" key="expressionEditor.blankString"/>
    </c:otherwise>
  </c:choose>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix"       value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="literalId"      value="literal_${idSuffix}"/>
  <c:set var="labelId"        value="terminalLabel_${idSuffix}"/>
  <c:set var="menuId"         value="menu_${idSuffix}"/>
  <c:set var="checkboxPrefix" value="checkbox_${idSuffix}"/>

  <%-- Render the link --%>
  <a id="<c:out value='${labelId}'/>"
     class="enumTerminalLabel terminalLabel expreditorControl"
     hidefocus="true"
     href="javascript:atg.expreditor.displayTerminalMenu({containerId: '<c:out value="${paramContainer}"/>',
                                                          editorId:    '<c:out value="${paramEditorId}"/>',
                                                          terminalId:  '<c:out value="${paramExpression.identifier}"/>',
                                                          menuId:      '<c:out value="${menuId}"/>',
                                                          anchorId:    '<c:out value="${labelId}"/>',
                                                          textFieldId: '<c:out value="${literalId}"/>',
                                                          isEnum:      true})"
  ><c:out value="${valueText}"/></a>

  <%-- We need a way to make the menu scroll when it contains too many items to
       display simultaneously.  For now, we'll use a fixed-size div with overflow=auto
       if there are more than 15 items. --%>
  <c:set var="tags" value="${paramExpression.tags}"/>
  <dspel:test var="info" value="${tags}"/>
  <c:choose>
    <c:when test="${info.size > 15}">
      <c:set var="menuClass" value="terminalMenuScrolling"/>
    </c:when>
    <c:otherwise>
      <c:set var="menuClass" value="terminalMenu"/>
    </c:otherwise>
  </c:choose>

  <%-- Create an initially invisible popup menu --%>
  <div id="<c:out value='${menuId}'/>"
       class="<c:out value='${menuClass}'/>"
       style="display: none">

    <%-- Include a hidden input for transferring the value --%>
    <div>
      <input type="hidden"
             id="<c:out value='${literalId}'/>"
             value="<c:out value='${paramExpression.value}'/>">
    </div>

    <%-- Include a button to pop down the menu --%>
    <div class="enumTerminalMenuHeader">
      <fmt:message var="title" key="expressionEditor.popupClose"/>
      <dspel:img page="${config.imageRoot}/icon_popupClose.gif"
                 onclick="atg.expreditor.popdownMenu()"
                 alt="${title}" border="0"/>
    </div>

    <table class="enumTerminalMenuBody" border="0" cellpadding="2" cellspacing="0">

      <%-- Iterate over the tags --%>
      <c:forEach var="tag" items="${tags}" varStatus="loop">
        <tr>
          <td nowrap>
            <c:set var="checkboxId" value="${checkboxPrefix}${loop.index}"/>
            <input type="checkbox"
                   id="<c:out value='${checkboxId}'/>"
                   onclick="atg.expreditor.onEnumCheckboxInput()">
            <label class="terminalMenuLabel" for="<c:out value='${checkboxId}'/>"><c:out value="${tag}"/></label>
          </td>
        </tr>
      </c:forEach>
    </table>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/enumSetEditor.jsp#2 $$Change: 651448 $ --%>
