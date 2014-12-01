<%--
  Default property editor for bigstring properties.

  The following request-scoped variables are expected to be set:
  
  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/bigStringEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get an ID for the input field --%>  
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>

  <%-- Allow the input field dimensions to be specified using attributes. --%>
  <c:set var="numRows" value=""/>
  <c:if test="${not empty propertyView.attributes.rows}">
    <c:set var="numRows" value="${propertyView.attributes.rows}"/>
  </c:if>

  <c:set var="numColumns" value=""/>
  <c:if test="${not empty propertyView.attributes.columns}">
    <c:set var="numColumns" value="${propertyView.attributes.columns}"/>
  </c:if>
    
  <%-- Display the property value in a text area --%>
  <dspel:textarea id="${inputId}"
                  converter="nullable"
                  rows="${numRows}"
                  cols="${numColumns}"
                  onpropertychange="formFieldModified()"
                  oninput="markAssetModified()"
                  bean="${propertyView.formHandlerProperty}"/>
  
  <%-- Indicate that the focus should be assigned to the text area when the
       editor is activated --%>
  <script type="text/javascript">
    registerOnActivate("<c:out value='${propertyView.uniqueId}'/>", function(){
      var elem = document.getElementById("<c:out value='${inputId}'/>");
      if (elem)
        elem.focus();
    });

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/bigStringEditor.jsp#2 $$Change: 651448 $--%>
