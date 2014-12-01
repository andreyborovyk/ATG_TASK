<%--
  Default view-mode view for string data
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
<%@ include file="../includes/fieldAttributes.jspf" %>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
  
  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <c:if test="${!empty title}">
    <c:set var="pvTitle" value="${title}"/>
  </c:if>
  <td class="formLabel">
    <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
  </td>

  <td class="formText formPadding">

  <%-- Open paragraph and show the optional lead-in text --%>
  <c:if test="${!empty textAboveField}">
    <c:out value="${textAboveField}" escapeXml="false"/><br />
  </c:if>

  <%-- Show optional pre-label --%>
  <c:if test="${!empty labelBeforeField}">
    <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
  </c:if>

  <%-- Show the actual field --%>
  <dspel:valueof bean="${mpv.formHandlerProperty}"/>&nbsp;

  <%-- Show optional post-label --%>
  <c:if test="${!empty labelAfterField}">
    &nbsp;<c:out value="${labelAfterField}" escapeXml="false"/>
  </c:if>

  <%-- Show option trailing text --%>
  <c:if test="${!empty textBelowField}">
    <br />
    <c:out value="${textBelowField}" escapeXml="false"/>
  </c:if>

  <%-- Close out field --%>
  </td>

</tr>
</table>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/string/simpleView.jsp#2 $$Change: 651448 $--%>
