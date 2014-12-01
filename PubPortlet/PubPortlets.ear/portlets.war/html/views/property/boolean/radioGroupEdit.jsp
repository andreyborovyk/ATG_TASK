<%--
  Default edit view for int data
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ page import="java.io.*,java.util.*" %>
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

  <%-- Set the value of the property to the default if it's not already set --%>
  <dspel:droplet name="/atg/dynamo/droplet/Switch">
    <dspel:param bean="${mpv.formHandlerProperty}" name="value"/>
    <dspel:oparam name="unset">
      <c:if test="${!empty defaultValue}">
        <dspel:setvalue bean="${mpv.formHandlerProperty}" value="${defaultValue}"/>
      </c:if>
    </dspel:oparam>
  </dspel:droplet>

  <%-- Show the actual field --%>
  <label class="formLabelNoBorderGray">
  <dspel:input type="radio" value="true" iclass="radio" disabled="${pvIsDisabled}"
               id="propertyValue_${mpv.uniqueId}_true"
               bean="${mpv.formHandlerProperty}"/>true
  </label>
  <label class="formLabelNoBorderGray">
  <dspel:input type="radio" value="false" iclass="radio" disabled="${pvIsDisabled}"
               id="propertyValue_${mpv.uniqueId}_false"
               bean="${mpv.formHandlerProperty}"/>false
  </label>

  <%-- Show option trailing text --%>
  <c:if test="${!empty textBelowField}">
    <br />
    <c:out value="${textBelowField}" escapeXml="false"/>
  </c:if>

  <%-- Close out field --%>
  </td>

</tr>
</table>


<%-- Every property editor should declare 'get' and 'set' functions
     so that they work with the "form saver" that enabled form values
     to be retained without having to hit "Save Changes"  --%>
<script language="JavaScript">

  function get_<c:out value="${mpv.uniqueId}"/> ()
  {
    var fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_true';
    if ( document.getElementById(fieldId).checked) {
      return "true";
    }
    fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_false';
    if ( document.getElementById(fieldId).checked) {
      return "false";
    }
    return "";
  }

  function set_<c:out value="${mpv.uniqueId}"/> ( value)
  {
    var fieldIdT = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_true';
    var fieldIdF = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_false';
    if ( value == "true") {
      document.getElementById(fieldIdT).checked = true;
      document.getElementById(fieldIdF).checked = false;
    } else if ( value == "false") {
      document.getElementById(fieldIdF).checked = true;
      document.getElementById(fieldIdT).checked = false;
    } else {
      document.getElementById(fieldIdF).checked = false;
      document.getElementById(fieldIdT).checked = false;
    }
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${mpv.uniqueId}"/>');

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/boolean/radioGroupEdit.jsp#2 $$Change: 651448 $--%>
