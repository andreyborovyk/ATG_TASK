<%--
  Default property editor for timestamp values.

  The following DSP parameters are supported:

  @param  omitTime  If set, only the date will be editable

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="pOmitTime" param="omitTime"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- This property is a timestamp unless the omitTime parameter is present --%>
  <c:set var="isTimestamp" value="${empty pOmitTime}"/>

  <%-- Determine the style for the input field.  An error style is used if
       there is an exception for the property. --%>
  <c:set var="inputClass" value="formTextField dateField"/>
  <c:if test="${not empty formHandler.propertyExceptions[propertyView.propertyName]}">
    <c:set var="inputClass" value="${inputClass} error"/>
  </c:if>

  <%-- Derive IDs for page elements --%>
  <c:set var="idSuffix"       value="${requestScope.atgPropertyViewId}"/>
  <c:set var="hiddenId"       value="propertyValue_${idSuffix}"/>
  <c:set var="iconId"         value="calendarIcon_${idSuffix}"/>
  <c:set var="dateInputId"    value="propertyDateValue_${idSuffix}"/>
  <c:set var="hoursInputId"   value="propertyHoursValue_${idSuffix}"/>
  <c:set var="minutesInputId" value="propertyMinutesValue_${idSuffix}"/>
  <c:set var="secondsInputId" value="propertySecondsValue_${idSuffix}"/>

  <%-- Determine the hidden input format --%>
  <fmt:message var="dateFormat" key="timestampEditor.dateFormat"/>
  <c:choose>
    <c:when test="${isTimestamp}">
      <c:set var="hiddenInputFormat" value="${dateFormat} HH:mm:ss"/>
    </c:when>
    <c:otherwise>
      <c:set var="hiddenInputFormat" value="${dateFormat}"/>
    </c:otherwise>
  </c:choose>

  <%-- Get the current date value as an ISO8601/RFC3339 string, in order to
       initialize the DateTextBox --%>
  <dspel:getvalueof var="propertyValue" bean="${propertyView.formHandlerProperty}"/>
  <c:set var="dateValue" value=""/>
  <c:if test="${not empty propertyValue}">
    <fmt:formatDate var="dateValue" value="${propertyValue}" pattern="yyyy-MM-dd"/>
  </c:if>

  <%-- Display an icon for popping up the calendar widget --%>
  <fmt:message var="imgAlt" key="timestampEditor.calendarIconText"/>
  <dspel:img id="${iconId}"
             page="${config.imageRoot}/calendar/calendar2_off.gif"
             alt="${imgAlt}" iclass="calIcon" border="0"
             onclick="document.getElementById('${dateInputId}').focus()"/>

  <%-- Use a hidden DSP input for the property value --%>
  <dspel:input type="hidden"
               id="${hiddenId}"
               converter="date"
               nullable="true"
               date="${hiddenInputFormat}"
               bean="${propertyView.formHandlerProperty}"/>

  <%-- Display the date portion of the property value in a DateTextBox --%>
  <script type="text/javascript">
    dojo.registerModulePath("atg.widget.form", "/AssetManager/dijit/form");
    dojo.require("atg.widget.form.SimpleDateTextBox");
  </script>
  <input type="text"
         id="<c:out value='${dateInputId}'/>"
         class="<c:out value='${inputClass}'/>"
         value="<c:out value='${dateValue}'/>"
         onpropertychange="formFieldModified()"
         oninput="markAssetModified()"
         onchange="markAssetModified()"
         constraints="{datePattern: '<c:out value="${dateFormat}"/>'}"
         dojoType="atg.widget.form.SimpleDateTextBox">

  <%-- Display the time portion of the property value in a text field --%>
  <c:if test="${isTimestamp}">
    <c:set var="hoursValue" value=""/>
    <c:set var="minutesValue" value=""/>
    <c:set var="secondsValue" value=""/>
    <c:if test="${not empty propertyValue}">
      <fmt:formatDate var="hoursValue" value="${propertyValue}" pattern="HH"/>
      <fmt:formatDate var="minutesValue" value="${propertyValue}" pattern="mm"/>
      <fmt:formatDate var="secondsValue" value="${propertyValue}" pattern="ss"/>
    </c:if>
    <fmt:message key="timestampEditor.hoursText"/>
    <input type="text"
           id="<c:out value='${hoursInputId}'/>"
           class="formTextField timeField"
           value="<c:out value='${hoursValue}'/>"
           onpropertychange="formFieldModified()"
           oninput="markAssetModified()"
           onchange="markAssetModified()">
    <fmt:message key="timestampEditor.minutesText"/>
    <input type="text"
           id="<c:out value='${minutesInputId}'/>"
           class="formTextField timeField"
           value="<c:out value='${minutesValue}'/>"
           onpropertychange="formFieldModified()"
           oninput="markAssetModified()"
           onchange="markAssetModified()">
    <c:choose>
      <%-- The seconds input field is hidden unless the "displaySeconds" attribute is specified --%>
      <c:when test="${not empty propertyView.attributes.displaySeconds}">
        <c:set var="secondsInputType" value="text"/>
        <fmt:message key="timestampEditor.secondText"/>
      </c:when>
      <c:otherwise>
        <c:set var="secondsInputType" value="hidden"/>
      </c:otherwise>
    </c:choose>
    <input type="<c:out value='${secondsInputType}'/>"
           id="<c:out value='${secondsInputId}'/>"
           class="formTextField timeField"
           value="<c:out value='${secondsValue}'/>"
           onpropertychange="formFieldModified()"
           oninput="markAssetModified()"
           onchange="markAssetModified()">
  </c:if>

  <script type="text/javascript">

    <%-- Indicate that the focus should be assigned to the text input when the
         editor is activated --%>
    registerOnActivate("<c:out value='${propertyView.uniqueId}'/>", function(){
      var elem = document.getElementById("<c:out value='${dateInputId}'/>");
      if (elem)
        elem.focus();
    });

    // This function is called before the containing form is submitted. It
    // copies the value from the text field to the hidden input.
    registerOnSubmit(function() {
      var dateField = document.getElementById("<c:out value='${dateInputId}'/>");
      var hoursField = document.getElementById("<c:out value='${hoursInputId}'/>");
      var minutesField = document.getElementById("<c:out value='${minutesInputId}'/>");
      var secondsField = document.getElementById("<c:out value='${secondsInputId}'/>");
      var hiddenField = document.getElementById("<c:out value='${hiddenId}'/>");
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
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampEditor.jsp#2 $$Change: 651448 $--%>
