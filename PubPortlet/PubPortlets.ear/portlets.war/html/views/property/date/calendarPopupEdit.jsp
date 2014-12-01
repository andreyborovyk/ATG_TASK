<%--
  Edit view for a date
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
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>

<%-- Derive IDs for page elements --%>
<c:set var="uniqueId"      value="${mpv.uniqueId}"/>
<c:set var="hiddenInputId" value="input_${uniqueId}"/>
<c:set var="iconId"        value="calendarIcon_${uniqueId}"/>
<c:set var="dateInputId"   value="propertyDateValue_${uniqueId}"/>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
  
  <%--
   Determine the input class of this item..
   --%>
  <c:set var="pvClassAttr" value="class=\"formLabel\""/>
  <c:if test="${mpv.propertyDescriptor.required}">
    <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
  </c:if>

  <c:set var="pvIsDisabled" value=""/>
  <c:if test="${!mpv.propertyDescriptor.writable}">
    <c:set var="pvIsDisabled" value='disabled=\"disabled\"'/>
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

  <%-- Determine the hidden input format --%>
  <fmt:message var="fieldDateFormat" bundle="${viewMapBundle}" key="date-format-input"/>
  <c:if test="${mpv.attributes.dateFormat}">
    <c:set var="fieldDateFormat" value="${mpv.attributes.dateFormat}"/>
  </c:if>
  
  <%-- Show the actual field --%>
  <%-- Only show the calendar widget if the field is writable --%>
  <c:if test="${empty pvIsDisabled}">
    <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/calendar/calendar2_off.gif'/>"
         id='<c:out value="${mpv.uniqueId}"/>_anchor'
         onclick="document.getElementById('<c:out value="${dateInputId}"/>').focus()"
         alt="<fmt:message bundle='${viewMapBundle}' key='select-date-text'/>" 
         width="17" height="11" class="imgpadded" border="0"></img>
  </c:if>

  <%-- Use a hidden DSP input for the property value --%>
  <dspel:input type="hidden"
               id="${hiddenInputId}"
               converter="date"
               nullable="true"
               date="${fieldDateFormat}"
               bean="${mpv.formHandlerProperty}"/>

  <%-- Get the current date value as an ISO8601/RFC3339 string, in order to
       initialize the DateTextBox --%>
  <dspel:getvalueof var="propertyValue" bean="${mpv.formHandlerProperty}"/>
  <c:set var="dateValue" value=""/>
  <c:if test="${not empty propertyValue}">
    <fmt:formatDate var="dateValue" value="${propertyValue}" pattern="yyyy-MM-dd"/>
  </c:if>

  <script type="text/javascript">
    dojo.registerModulePath("atg.widget.form", "/AssetManager/dijit/form");
    dojo.require("atg.widget.form.SimpleDateTextBox");
  </script>

  <%-- Display the date --%>
  <input type="text"
         id="<c:out value='${dateInputId}'/>"
         value="<c:out value='${dateValue}'/>"
         class="formElementCustom dateField"
         <c:out value='${pvIsDisabled}'/>
         size="<c:out value='${mpv.attributes.inputFieldSize}'/>"
         maxlength="<c:out value='${mpv.attributes.inputFieldMaxlength}'/>"
         constraints="{datePattern: '<c:out value="${fieldDateFormat}"/>'}"
         dojoType="atg.widget.form.SimpleDateTextBox"/>
  
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

  function get_<c:out value="${uniqueId}"/> ()
  {
    var fieldId = '<c:out value="${hiddenInputId}"/>';
    return document.getElementById(fieldId).value;
  }

  function set_<c:out value="${uniqueId}"/> ( value)
  {
    var fieldId = '<c:out value="${hiddenInputId}"/>';
    document.getElementById(fieldId).value = value;
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${uniqueId}"/>');
  
  registerOnSubmit(function() {
    var dateField = document.getElementById("<c:out value='${dateInputId}'/>");
    var hiddenField = document.getElementById("<c:out value='${hiddenInputId}'/>");
    hiddenField.value = dateField.value;
  });
</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/date/calendarPopupEdit.jsp#3 $$Change: 658122 $--%>
