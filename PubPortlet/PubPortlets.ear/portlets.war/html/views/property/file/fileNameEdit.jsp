<%--
  Edit view for file names, edit widget + download option when file exists
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/fileNameEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="java.io.*,java.util.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
<%@ include file="../includes/fieldAttributes.jspf" %>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
  
 <c:url var="downloadActionURL" context="/atg" value="/bcc/process/assetEditPage.jsp"/>

  <script type="text/javascript">
    // Need to substitute the form's submit URL for one that will 
    // cause the file form handler to execute outside portals because
    // of the portal no-redirct restriction.
    function submitDownloadFile() {
      var form = getForm();
      
      // var actionURL = '<c:out value="${downloadActionURL}" escapeXml="false"/>';
      // saving the original theAssetForm's action URL
      var actionURL = form.action;
      
      // this is the actionURL only to be used when downloading a file. This accesses
      // assetEditPage.jsp outside of the portals context
      var actionURL_dl = '/PubPortlets/html/ProjectsPortlet/assetEditPage.jsp';

      // Get the _DARGS argument from the current form action URL 
      // and append it to the URL generated above so Dynamo knows 
      // what to execute
      var dargsIndex = form.action.search( /_DARGS=.*/ );
      if ( actionURL_dl.indexOf("?") > 0 ) {
        actionURL_dl += "&";
      }
      else {
        actionURL_dl += "?";
      }
      actionURL_dl += form.action.substr( dargsIndex );
      
      form.action = actionURL_dl;
      form.elements["<c:out value="${imap.formHandler.path}.actionType"/>"].value = "download";
      form.submit();
      
      // setting the form.action back to the portals context
      form.action = actionURL;
    }

    function toggleVisibility(elementId) {
      var elem = document.getElementById(elementId);
      if (elem != null) {
        if (elem.style.display == "none") {
          elem.style.display = "block";
        } 
        else {
          elem.style.display = "none";
        }
      }
    }

  </script>

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
      <span class="error"> &nbsp;
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

  <%-- Show the actual field... disabled, if necessary --%>
  <dspel:input type="text" iclass="formElementCustom" disabled="${pvIsDisabled}"
               id="propertyValue_${mpv.uniqueId}"
               size="${mpv.attributes.inputFieldSize}"
               maxlength="${mpv.attributes.inputFieldMaxlength}" 
               bean="${mpv.formHandlerProperty}" />

  <%-- Show optional post-label --%>
  <c:if test="${!empty labelAfterField}">
    &nbsp;<c:out value="${labelAfterField}" escapeXml="false"/>
  </c:if>

  <%-- Show option trailing text --%>
  <c:if test="${!empty textBelowField}">
    <br />
    <c:out value="${textBelowField}" escapeXml="false"/>
  </c:if>

  <%-- Show download button if file has contents --%>
  <c:if test="${formHandler.canDownload}">
    <a href='#' onclick='submitDownloadFile();return false;'>
      Download&nbsp;&nbsp;
    </a>
  </c:if>

  <dspel:importbean bean="/atg/epub/file/FileExtensionMIMETyper" var="femt"/>

<%--
  'debug' no longer available, commenting for now
  <c:set target="${femt}" property="loggingDebug" value="${debug}"/>
--%>

  <dspel:getvalueof var="fileName" bean="${mpv.formHandlerProperty}"/>

  <c:if test="${ ! empty fileName }">
    <dspel:droplet var="mimeTyper" name="FileExtensionMIMETyper">
      <dspel:param name="value" value="${fileName}"/>
      <dspel:oparam name="output">
        <c:if test="${ 'image' == mimeTyper.topLevel }">
          <span id="<c:out value='${mpv.formHandlerProperty}'/>.imageLink">
            <a href="#" onclick="toggleVisibility('<c:out value="${mpv.formHandlerProperty}"/>.image');toggleVisibility('<c:out value="${mpv.formHandlerProperty}"/>.imageLink');return false;">Display Image</a>
          </span> 
          <div class="formElementCustom" id="<c:out value='${mpv.formHandlerProperty}'/>.image"
       style="display: none;">
            <a href="#" onclick="toggleVisibility('<c:out value="${mpv.formHandlerProperty}"/>.image');toggleVisibility('<c:out value="${mpv.formHandlerProperty}"/>.imageLink');return false;">Hide Image</a>
            <br /><img src='<c:out value="/atg/vmuri?uri=${assetURI}"/>' />
          </div>
        </c:if>
      </dspel:oparam>
    </dspel:droplet>
  </c:if>

  <%-- Close out field --%>
  </td>

</tr>
</table>


<%-- Every property editor should declare 'get' and 'set' functions
     so that they work with the "form saver" that enabled form values
     to be retained without having to hit "Save Changes"  --%>
<script type="text/javascript">

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

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/fileNameEdit.jsp#2 $$Change: 651448 $--%>

