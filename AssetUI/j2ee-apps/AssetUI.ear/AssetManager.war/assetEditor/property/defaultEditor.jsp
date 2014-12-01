<%--
  Default property editor.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/defaultEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <%-- Determine the style for the input field.  An error style is used if
       there is an exception for the property. --%>
  <c:set var="inputClass" value="formTextField"/>
  <c:if test="${not empty formHandler.propertyExceptions[propertyView.propertyName]}">
    <c:set var="inputClass" value="${inputClass} error"/>
  </c:if>

  <%-- Get an ID for the input field --%>
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>

  <%-- Allow the input field size and maxlength to be specified using attributes. --%>
  <c:set var="inputSize" value=""/>
  <c:if test="${not empty propertyView.attributes.inputFieldSize}">
    <c:set var="inputSize" value="${propertyView.attributes.inputFieldSize}"/>
  </c:if>

  <c:set var="inputMaxlength" value=""/>
  <c:if test="${not empty propertyView.attributes.inputFieldMaxlength}">
    <c:set var="inputMaxlength" value="${propertyView.attributes.inputFieldMaxlength}"/>
  </c:if>
  
  <%-- Allow the converter to be specified using an attribute.  The default
       converter is "nullable". --%>
  <c:set var="converter" value="nullable"/>
  <c:if test="${not empty propertyView.attributes.converter}">
    <c:set var="converter" value="${propertyView.attributes.converter}"/>
  </c:if>  

  <%-- Display the property value in a text input --%>
  <dspel:input type="text"
               id="${inputId}"
               iclass="${inputClass}"
               converter="${converter}"
               size="${inputSize}"
               maxlength="${inputMaxlength}" 
               onpropertychange="formFieldModified()"
               oninput="markAssetModified()"
               bean="${propertyView.formHandlerProperty}"/>

  <%-- Indicate that the focus should be assigned to the text input when the
       editor is activated --%>
  <script type="text/javascript">
    registerOnActivate("<c:out value='${propertyView.uniqueId}'/>", function(){
      var elem = document.getElementById("<c:out value='${inputId}'/>");
      if (elem)
        elem.focus();
    });

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/defaultEditor.jsp#2 $$Change: 651448 $--%>
