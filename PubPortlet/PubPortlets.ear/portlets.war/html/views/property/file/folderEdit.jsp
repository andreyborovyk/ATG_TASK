<%--
  File folder editor

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
<%@ include file="../includes/buttonAttributes.jspf" %>

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
     Use the property display name by default.  Check '_title' attribute
     for override...
     --%>
    <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
    <c:if test="${!empty title}">
      <c:set var="pvTitle" value="${title}"/>
    </c:if>

    <td <c:out value="${pvClassAttr}" escapeXml="false"/> >
      <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
    </td>

    <!-- <h3><c:out value="${pvTitle}" escapeXml="false"/></h3> -->

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

      <%-- Next line... show the field... --%>
      <!-- <label> -->

      <%-- A hidden field which maps to the real bean property... --%>
      <dspel:input type="hidden" bean="${mpv.formHandlerProperty}"
        id="${mpv.uniqueId}Value"/>

      <%-- Show optional pre-label --%>
      <c:if test="${!empty labelBeforeField}">
        <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
      </c:if>

      <%-- Show the actual field --%>
      <input type="text" 
        disabled 
        class="formElementCustom" 
        size="<c:out value="${mpv.attributes.inputFieldSize}"/>"
        id='<c:out value="${mpv.uniqueId}"/>DisplayName'
        value='<dspel:valueof bean="${mpv.formHandlerProperty}"/>'
        name='<c:out value="${mpv.uniqueId}"/>DisplayName'>

      <script language="JavaScript">

        function <c:out value="${mpv.uniqueId}"/>_set( selected ) {
          var input =
            document.getElementById( '<c:out value="${mpv.uniqueId}"/>' + "Value" );
          input.value = selected.id;
          input = document.getElementById(
            '<c:out value="${mpv.uniqueId}"/>' + "DisplayName" );
          input.value = selected.displayName;
        }

        function <c:out value="${mpv.uniqueId}"/>_pick() {
          // array to contain only those types to be shown in browser
          var allowableTypes = new Array( 0 );

          var assetType = new Object();
            assetType.typeCode            = 'file';
            assetType.displayName         = 'Folder';
            assetType.displayNameProperty = 'displayName';
            assetType.typeName            = 'fileFolder';
            assetType.repositoryName      = '/atg/epub/file/ConfigFileSystem';
            assetType.componentPath       = '/atg/epub/file/ConfigFileSystem';
            assetType.createable          = "true"; 
          
          allowableTypes[ allowableTypes.length ] = assetType;

          var picker = new AssetBrowser( '<c:out value="${assetInfoPath}"/>' );
            picker.browserMode        = "pick";
            picker.isAllowMultiSelect = "false";
            picker.createMode         = "nested";
            picker.onSelect           = '<c:out value="${mpv.uniqueId}"/>_set';
            picker.closeAction        = "hide";
            picker.defaultView        = "Search";
            picker.assetTypes         = allowableTypes;

          picker.invoke();
        }
      </script>

      <%-- Show the picker button --%>
      <c:if test="${ ! pvIsDisabled }">
        <input type="button"
          value='<c:out value="${buttonText}"/>'
          class="standardButton"
          onmousedown='javascript:<c:out value="${mpv.uniqueId}"/>_pick();'/>
      </c:if>

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

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/folderEdit.jsp#2 $$Change: 651448 $--%>

