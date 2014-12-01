<%--
  File picker editor

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/filePicker.jsp#2 $$Change $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page import="java.io.*,java.util.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws"          %>

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

        var fileTypesRegEx = null;

        <c:if test="${mpv.attributes.fileTypes != null}">
          fileTypesRegEx = new RegExp( '<c:out value="${mpv.attributes.fileTypes}"/>', 'g' );
        </c:if>

        function <c:out value="${mpv.uniqueId}"/>_set( selected ) {
          var input = 
            document.getElementById( '<c:out value="${mpv.uniqueId}"/>' + "Value" );
          input.value = selected.id;
          input = document.getElementById( 
            '<c:out value="${mpv.uniqueId}"/>' + "DisplayName" );
          input.value = selected.displayName;
        }

        function <c:out value="${mpv.uniqueId}"/>_pick() {
          var assetTypesList = new Array( 0 );

          <pws:getVersionedAssetTypes var="assetTypes">
            <c:forEach items="${assetTypes}" var="result">

              <c:forEach items="${result.types}" var="type">
                <c:choose>
                  <c:when test="${result.repository}">
                    <c:set var="typeCode" value="repository"/>
                  </c:when>
                  <c:when test="${result.fileSystem}">
                    <c:set var="typeCode" value="file"/>
                  </c:when>
                </c:choose>
                
                if ('<c:out value="${typeCode}"/>' == 'file') {
                  var assetType = new Object();
                    assetType.typeCode            = '<c:out value="${typeCode}"/>';
                    assetType.displayName         = '<c:out value="${type.displayName}"/>';
                    assetType.displayNameProperty = 'displayName';
                    assetType.typeName            = '<c:out value="${type.typeName}"/>';
                    assetType.repositoryName      = '<c:out value="${result.repositoryName}"/>';
                    assetType.componentPath       = '<c:out value="${result.componentPath}"/>';
                    assetType.createable          = 'true';
                  <c:if test="${result.repository}">
                    <c:if test="${ !empty type.itemDescriptor.itemDisplayNameProperty}">
                        assetType.displayNameProperty = 
                          '<c:out value="${type.itemDescriptor.itemDisplayNameProperty}"/>';
                    </c:if>
                  </c:if>

                  var encodedType = assetType.componentPath + ":" + assetType.typeName;

                  // if propertyMapping has a particular value for "fileTypes"
                  // (defined as "path/to/VFS:fileType") in its attributes map
                  if ( fileTypesRegEx != null ) {
                    if ( fileTypesRegEx.exec( encodedType ) )
                      assetTypesList[ assetTypesList.length ] = assetType;
                  }
                  // otherwise display all file types in the picker
                  else {
                    assetTypesList[ assetTypesList.length ] = assetType;
                  }
                }
              </c:forEach>
            </c:forEach>
          </pws:getVersionedAssetTypes>

          var picker = new AssetBrowser( '<c:out value="${assetInfoPath}"/>' );
            picker.browserMode        = "pick";
            picker.isAllowMultiSelect = "false";
            picker.createMode         = "nested";
            picker.onSelect           = '<c:out value="${mpv.uniqueId}"/>_set';
            picker.closeAction        = "hide";
            picker.defaultView        = "Search";
            picker.assetTypes         = assetTypesList;

          picker.invoke();
        }
      </script>

      <%-- Show the picker button --%>
      <input type="button" 
        value='<c:out value="${buttonText}"/>'
        class="standardButton"
        onmousedown='javascript:<c:out value="${mpv.uniqueId}"/>_pick();'/>
               
      <%-- Show optional post-label --%>
      <c:if test="${!empty labelAfterFiel}">
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

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/file/filePicker.jsp#2 $$Change: 651448 $--%>

