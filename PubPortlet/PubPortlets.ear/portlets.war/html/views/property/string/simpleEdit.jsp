<%--
  Default edit view for string data
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
  <c:if test="${title != null}">
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
    <c:if test="${textAboveField != null}">
      <c:out value="${textAboveField}" escapeXml="false"/><br />
    </c:if>

    <%-- Show optional pre-label --%>
    <c:if test="${labelBeforeField != null}">
      <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
    </c:if>

    <%-- Set the value of the property to the default if it's not already set --%>
    <dspel:droplet name="/atg/dynamo/droplet/Switch">
      <dspel:param bean="${mpv.formHandlerProperty}" name="value"/>
      <dspel:oparam name="unset">
        <c:if test="${defaultValue != null}">
          <dspel:setvalue bean="${mpv.formHandlerProperty}" value="${defaultValue}"/>
        </c:if>
      </dspel:oparam>
    </dspel:droplet>

    <%-- Show the actual field... disabled, if necessary --%>
    <dspel:input type="text" iclass="formElementCustom" disabled="${pvIsDisabled}"
                 id="propertyValue_${mpv.uniqueId}"
                 size="${mpv.attributes.inputFieldSize}"
                 maxlength="${mpv.attributes.inputFieldMaxlength}" 
                 bean="${mpv.formHandlerProperty}" />

    <%-- Show optional post-label --%>
    <c:if test="${labelAfterField != null}">
      &nbsp;<c:out value="${labelAfterField}" escapeXml="false"/>
    </c:if>

    <%-- Show option trailing text --%>
    <c:if test="${textBelowField != null}">
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
    var fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>';
    return encodeURIComponent(document.getElementById(fieldId).value);
  }

  function set_<c:out value="${mpv.uniqueId}"/> ( value)
  {
    var fieldId = 'propertyValue_<c:out value="${mpv.uniqueId}"/>';
    document.getElementById(fieldId).value = decodeURIComponent(value);
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${mpv.uniqueId}"/>');

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/string/simpleEdit.jsp#2 $$Change: 651448 $--%>
