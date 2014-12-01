<%--
  JSP fragment to render an expression and its children.

  This page expects the following parameters:
    expression - The expression to render

  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/renderExpression.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor_rt"            %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramExpression" param="expression"/>
 
  <%--
  {
    // Debugging code that prints out the expression nodes
    atg.ui.expreditor.Expression e = 
      (atg.ui.expreditor.Expression)pageContext.findAttribute("paramExpression");
    Object name = ((e instanceof atg.ui.expreditor.TerminalExpression) ? ((atg.ui.expreditor.TerminalExpression)e).getDescription() : e.getConstruct().getName()) ;
    String type = null;
    if(e instanceof atg.ui.expreditor.SequenceExpression)
      type = "S";
    if(e instanceof atg.ui.expreditor.ChoiceExpression)
      type = "C";
    if(e instanceof atg.ui.expreditor.TokenExpression)
      type = "T";
    if(e instanceof atg.ui.expreditor.LiteralExpression)
      type = "L";
    atg.nucleus.Nucleus.getGlobalNucleus().logDebug("renderExpression: " + type + " " + e.getIdentifier() + "\t\t" + name);
  } 
  --%>

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
    <c:out value="<${groupingType}" escapeXml="false"/>
    <c:out value="${groupingId}" escapeXml="false"/>
    <c:out value="${groupingClass}" escapeXml="false"/>
    <c:out value="${groupingStyle}>" escapeXml="false"/>

  </c:if>

  <%-- Include the page that renders the current expression --%>  
  <ee:getEditor var="editor" expression="${paramExpression}"/>
  <c:if test="${not empty editor}">
    <dspel:include otherContext="${editor.contextRoot}" page="${editor.page}">
      <dspel:param name="expression" value="${paramExpression}"/>
    </dspel:include>
  </c:if>

  <%-- Close out the group element, if one was specified --%>
  <c:if test="${not empty groupingType}">
    <c:out value="</${groupingType}>" escapeXml="false"/>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/expreditor/renderExpression.jsp#1 $$Change: 651360 $ --%>
