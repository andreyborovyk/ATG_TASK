<%--
  An expression editor for Button instances.

  @param  expression  The expression to be rendered
  @param  container   The ID of the container for this expression editor
  @param  editorId    A unique identifier for this expression editor

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/buttonEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
  <dspel:getvalueof var="paramContainer"  param="container"/>
  <dspel:getvalueof var="paramEditorId"   param="editorId"/>

  <%-- Render a div or span tag to wrap this expresssion.  Each expression can
       be rendered inside a grouping such as a div or span tag.  You can specify
       the grouping type for a given construct using the 'groupingType' attribute
       of a grammar construct.  By default that grouping will be given an id
       that is the same as the expression construct's name.  If you would like
       to specify a different id for the grouping, you can do so by declaring
       the 'groupingId' attribute inside the grammar construct you  wish to
       modify.  Likewise, If you would like to specify a 'class' attribute value
       for the grouping, you can do so by declaring the 'groupingClass' attribute
       inside the grammar construct you wish to modify.  --%>

  <ee:getConstructAttribute var="groupingType"
                            expression="${paramExpression}"
                            attribute="groupingType"/>
  <%-- debug value for groupingType
  <c:out value="button.groupingType=["/> <c:out value="${groupingType}]"/>
   --%>
  <c:if test="${not empty groupingType}">
  
    <%-- Construct the ID attribute string, using either the specified ID or
         the name of the expression's construct, if specified --%>
    <ee:getConstructAttribute var="groupingId"
                              expression="${paramExpression}"
                              attribute="groupingId"/>
    <c:choose>
      <c:when test="${not empty groupingId}">
        <c:set var="groupingId" value="id='${groupingId}'"/>
      </c:when>
      <c:when test="${not empty paramExpression.construct.name}">
        <c:set var="groupingId" value="id='${paramExpression.construct.name}'"/>
      </c:when>
    </c:choose>

    <%-- Construct the class attribute string, if specified --%>
    <ee:getConstructAttribute var="groupingClass"
                              expression="${paramExpression}"
                              attribute="groupingClass"/>
    <c:if test="${not empty groupingClass}">
      <c:set var="groupingClass" value="class='${groupingClass}'"/>
    </c:if>

    <%-- Construct the style attribute string, if specified --%>
    <ee:getConstructAttribute var="groupingStyle"
                              expression="${paramExpression}"
                              attribute="groupingStyle"/>
    <c:if test="${not empty groupingStyle}">
      <c:set var="groupingStyle" value="style='${groupingStyle}'"/>
    </c:if>

    <%-- Render the start of the group element, including any specified attributes --%>
    <c:out value="<!--  Rendering starting group element - buttonEditor.jsp -->" escapeXml="false"/>
    <c:out value="<${groupingType}" escapeXml="false"/>
    <c:out value="${groupingId}" escapeXml="false"/>
    <c:out value="${groupingClass}" escapeXml="false"/>
    <c:out value="${groupingStyle}>" escapeXml="false"/>

  </c:if>

  <%-- Determine the text to be displayed in the link
       This is the description of the button --%>
  <c:choose>
    <c:when test="${not empty paramExpression.description}">
      <c:set var="buttonLabel" value="${paramExpression.description}"/>
    </c:when>
    <c:otherwise>
      <c:set var="buttonLabel" value="${null}"/>
    </c:otherwise>
  </c:choose>

  <%-- Construct the class attribute string, if specified --%>
  <ee:getConstructAttribute var="buttonClass"
                            expression="${paramExpression}"
                            attribute="buttonClass" />
  <c:choose>
    <c:when test="${not empty buttonClass}">
      <c:set var="buttonClass" value="class='${buttonClass} expreditorControl'" />
    </c:when>
    <c:otherwise>
      <c:set var="buttonClass" value="class='terminalLabel expreditorControl'" />
    </c:otherwise>
  </c:choose>

  <%-- Construct the style attribute string, if specified --%>
  <ee:getConstructAttribute var="buttonStyle"
                            expression="${paramExpression}"
                            attribute="buttonStyle" />
  <c:if test="${not empty buttonStyle}">
    <c:set var="buttonStyle" value="style='${buttonStyle}'" />
  </c:if>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix"  value="${paramEditorId}${paramExpression.identifier}"/>
  <c:set var="buttonId" value="button_${idSuffix}"/>
  
  <%-- Render the link --%>
  <a id="<c:out value='${buttonId}'/>"
     title="<c:out value='${buttonText}'/>"
     <c:out value="${buttonClass}" escapeXml="false"/> 
     <c:out value="${buttonStyle}" escapeXml="false"/>
     href="javascript:atg.expreditor.onButtonClick({containerId: '<c:out value="${paramContainer}"/>',
                                                    editorId:    '<c:out value="${paramEditorId}"/>',
                                                    terminalId:  '<c:out value="${paramExpression.identifier}"/>'})"
  ><c:out value="${buttonLabel}"/></a>
  
  <%-- Close out the group element, if one was specified --%>
  <c:if test="${not empty groupingType}">
    <c:out value="<!--  Close out the group element, if one was specified - buttonEditor.jsp -->" escapeXml="false"/>
    <c:out value="</${groupingType}>" escapeXml="false"/>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/buttonEditor.jsp#2 $$Change: 651448 $ --%>
