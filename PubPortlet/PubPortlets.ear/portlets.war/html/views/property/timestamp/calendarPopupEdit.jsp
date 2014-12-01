<%--
  Edit view for a timestamp.

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
<%@ include file="../includes/fieldAttributes.jspf" %>
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>

<%-- Derive IDs for page elements --%>
<c:set var="uniqueId"       value="${mpv.uniqueId}"/>
<c:set var="hiddenInputId"  value="input_${uniqueId}"/>
<c:set var="dateInputId"    value="propertyDateValue_${uniqueId}"/>
<c:set var="hoursInputId"   value="propertyHoursValue_${uniqueId}"/>
<c:set var="minutesInputId" value="propertyMinutesValue_${uniqueId}"/>
<c:set var="secondsInputId" value="propertySecondsValue_${uniqueId}"/>

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

    <td <c:out value="${pvClassAttr}" escapeXml="false"/>>
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
      <c:set var="hiddenInputFormat" value="${fieldDateFormat} HH:mm:ss"/>

      <%-- Show the actual field --%>
      <%-- Only show the calendar widget if the field is writable --%>
      <c:if test="${empty pvIsDisabled}">
        <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/calendar/calendar2_off.gif'/>"
             id='<c:out value="${mpv.uniqueId}"/>_anchor'
             onclick="document.getElementById('<c:out value="${dateInputId}"/>').focus()"
             alt="<fmt:message bundle='${viewMapBundle}' key='select-date-time-text'/>" 
             width="17" height="11" class="imgpadded" border="0"/>
      </c:if>
      
      <%-- Use a hidden DSP input for the property value --%>
      <dspel:input type="hidden"
                   id="${hiddenInputId}"
                   converter="date"
                   nullable="true"
                   date="${hiddenInputFormat}"
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
      
      <%-- Display the time --%>
      <c:set var="hoursValue" value=""/>
      <c:set var="minutesValue" value=""/>
      <c:set var="secondsValue" value=""/>
      <c:if test="${not empty propertyValue}">
        <fmt:formatDate var="hoursValue" value="${propertyValue}" pattern="HH"/>
        <fmt:formatDate var="minutesValue" value="${propertyValue}" pattern="mm"/>
        <fmt:formatDate var="secondsValue" value="${propertyValue}" pattern="ss"/>
      </c:if>
      <span class="dateTimeLabel">
        <fmt:message bundle='${viewMapBundle}' key='hour-format-label'/>
      </span>
      <input type="text"
             id="<c:out value='${hoursInputId}'/>"
             class="formElementCustom timeField"
             <c:out value='${pvIsDisabled}'/>
             value="<c:out value='${hoursValue}'/>"/>
      <span class="dateTimeLabel">
        <fmt:message bundle='${viewMapBundle}' key='minute-format-label'/>
      </span>
      <input type="text"
             id="<c:out value='${minutesInputId}'/>"
             class="formElementCustom timeField"
             <c:out value='${pvIsDisabled}'/>
             value="<c:out value='${minutesValue}'/>"/>
      <c:choose>
        <%-- The seconds input field is hidden unless the "displaySeconds" attribute is specified --%>
        <c:when test="${not empty mpv.attributes.displaySeconds}">
          <c:set var="secondsInputType" value="text"/>
          <span class="dateTimeLabel">
            <fmt:message bundle='${viewMapBundle}' key='second-format-label'/>
          </span>
        </c:when>
        <c:otherwise>
          <c:set var="secondsInputType" value="hidden"/>
        </c:otherwise>
      </c:choose>
      <input type="<c:out value='${secondsInputType}'/>"
             id="<c:out value='${secondsInputId}'/>"
             class="formElementCustom timeField"
             <c:out value='${pvIsDisabled}'/>
             value="<c:out value='${secondsValue}'/>"/>

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
      var hoursField = document.getElementById("<c:out value='${hoursInputId}'/>");
      var minutesField = document.getElementById("<c:out value='${minutesInputId}'/>");
      var secondsField = document.getElementById("<c:out value='${secondsInputId}'/>");
      var hiddenField = document.getElementById("<c:out value='${hiddenInputId}'/>");
      hiddenField.value = dateField.value;
      if (hoursField && hiddenField.value) {
        var hoursValue = hoursField.value;
        if (!hoursValue)
          hoursValue = "00";
        var minutesValue = minutesField.value;
        if (!minutesValue)
          minutesValue = "00";
        var secondsValue = secondsField.value;
        if (!secondsValue)
          secondsValue = "00";
        hiddenField.value += " " + hoursValue + ":" + minutesValue + ":" + secondsValue;
      }
    });
</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/timestamp/calendarPopupEdit.jsp#2 $$Change: 651448 $--%>
