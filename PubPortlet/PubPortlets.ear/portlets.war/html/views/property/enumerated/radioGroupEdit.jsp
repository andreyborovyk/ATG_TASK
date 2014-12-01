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

  <%-- Show optional pre-label --%>
  <c:if test="${!empty labelBeforeField}">
    <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
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

  <%-- Iterate over each of the options in the enumeration descriptor --%>
  <c:forEach items="${mpv.propertyDescriptor.enumeratedValues}" var="enumValue" varStatus="enumStatus">
    <label class="formLabelNoBorderGray">

    <%-- Check if this item should be the currently selected item --%>
    <c:choose>
      <c:when test="${mpv.propertyDescriptor.useCodeForValue}">
        <dspel:droplet name="/atg/dynamo/droplet/Switch">
          <dspel:param bean="${mpv.formHandlerProperty}" name="value"/>
          <dspel:oparam name="${mpv.propertyDescriptor.enumeratedCodes[enumStatus.index]}">
            <dspel:input type="radio" value="${enumValue}" iclass="radio" bean="${mpv.formHandlerProperty}"
                         id="propertyValue_${mpv.uniqueId}_${enumValue}"
                         disabled="${pvIsDisabled}" checked="true" />
          </dspel:oparam>
          <dspel:oparam name="default">
            <dspel:input type="radio" value="${enumValue}" iclass="radio" bean="${mpv.formHandlerProperty}"
                         id="propertyValue_${mpv.uniqueId}_${enumValue}"
                         disabled="${pvIsDisabled}"/>
          </dspel:oparam>
        </dspel:droplet>
      </c:when>
      <c:otherwise>
        <dspel:droplet name="/atg/dynamo/droplet/Switch">
          <dspel:param bean="${mpv.formHandlerProperty}" name="value"/>
          <dspel:oparam name="${enumValue}">
            <dspel:input type="radio" value="${enumValue}" iclass="radio" bean="${mpv.formHandlerProperty}"
                         id="propertyValue_${mpv.uniqueId}_${enumValue}"
                         checked="true" />
          </dspel:oparam>
          <dspel:oparam name="default">
            <dspel:input type="radio" value="${enumValue}" iclass="radio"
                         id="propertyValue_${mpv.uniqueId}_${enumValue}"
                         bean="${mpv.formHandlerProperty}" />
          </dspel:oparam>
        </dspel:droplet>
      </c:otherwise>
    </c:choose>
    <c:out value="${enumValue}" escapeXml="false"/>
    </label>

    <c:if test="${!enumStatus.last}">
      <br />
    </c:if>
  </c:forEach>

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



<%-- Every property editor should declare 'get' and 'set' functions
     so that they work with the "form saver" that enabled form values
     to be retained without having to hit "Save Changes"  --%>
<script language="JavaScript">

  function get_<c:out value="${mpv.uniqueId}"/> ()
  {
    var fieldId = '';

    <c:forEach items="${mpv.propertyDescriptor.enumeratedValues}" var="enumValue" varStatus="enumStatus">
      fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_<c:out value="${enumValue}"/>';
      if ( document.getElementById(fieldId).checked) {
        return '<c:out value="${enumValue}"/>';
      }
    </c:forEach>

    return '';
  }

  function set_<c:out value="${mpv.uniqueId}"/> ( value)
  {
    var fieldId = '';

    <c:forEach items="${mpv.propertyDescriptor.enumeratedValues}" var="enumValue" varStatus="enumStatus">
      fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_<c:out value="${enumValue}"/>';
      document.getElementById(fieldId).checked = false;
    </c:forEach>

    if ( value != '') {
      fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>_' + value;
      document.getElementById(fieldId).checked = true;
    }
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${mpv.uniqueId}"/>');

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/enumerated/radioGroupEdit.jsp#2 $$Change: 651448 $--%>

