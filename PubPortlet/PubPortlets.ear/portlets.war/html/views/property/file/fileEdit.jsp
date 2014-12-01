<%--
  File upload/download widget. Shows download option if file contents can
  be read.

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
   Determine the input class of this item..
   --%>
  <c:set var="pvClassAttr" value="class=\"formLabel\""/>
  <c:if test="${mpv.propertyDescriptor.required}">
    <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
  </c:if>

  <c:set var="pvIsDisabled" value="false"/>
  <c:if test="${!mpv.propertyDescriptor.writable}">
    <c:set var="pvIsDisabled" value="true"/>
  </c:if>

  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <c:if test="${!empty title}">
    <c:set var="pvTitle" value="${title}"/>
  </c:if>
  <td <c:out value="${pvClassAttr}" escapeXml="false"/> >
    <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
  </td>

  <td>

  <%--
   Display any errors that were caught for this property...
   --%>
  <c:if test="${ ! empty formHandler.propertyExceptions[mpv.propertyName]}">
    <span class="error">
      <c:out value="${formHandler.propertyExceptions[mpv.propertyName].message}"/>
    </span>
    <br />
  </c:if>

  <%-- Open paragraph and show the optional lead-in text --%>
  <c:if test="${!empty textAboveField}">
    <c:out value="${textAboveField}" escapeXml="false"/><br />
  </c:if>

  <%-- Show optional pre-label --%>
  <c:if test="${!empty labelBeforeField}">
    <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
  </c:if>

  <%-- Show the actual field --%>
  <dspel:input type="file" iclass="formElementCustom standardButton" 
               id="propertyValue_${mpv.uniqueId}"
               disabled="${pvIsDisabled}" size="${mpv.attributes.inputFieldSize}"
               maxlength="${mpv.attributes.inputFieldMaxlength}" 
               bean="${mpv.formHandlerProperty}"/>

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

<%-- This property is not form-saveable for security reasons --%>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/fileEdit.jsp#2 $$Change: 651448 $--%>
