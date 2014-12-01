<%--
  Default property editor for enumerated values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/enumEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"               %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  

  <%-- Find the enumerated property descriptor associated with the given
       property descriptor. --%>
  <web-ui:findEnumPropertyDescriptor var="propDesc"
    propertyDescriptor="${propertyView.propertyDescriptor}"/>

  <%-- Get the current property value --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>

  <%-- Display the property value in a select box --%>
  <dspel:select id="${inputId}"
                bean="${propertyView.formHandlerProperty}"
                onchange="markAssetModified()"
                converter="nullable">

    <%-- If the property value is optional, allow its value to be set to null --%>    
    <c:if test="${not propDesc.required}">
      <dspel:option value="">
        <fmt:message key="enumEditor.unspecified"/>
      </dspel:option>
    </c:if>

    <%-- Iterate over each of the possible values --%>
    <c:forEach var="value"
               items="${propDesc.enumeratedValues}"
               varStatus="loop">

      <%-- Determine if this is the current value. --%>
      <c:choose>
        <c:when test="${propDesc.useCodeForValue}">
          <c:set var="selected" value="${propVal == propDesc.enumeratedCodes[loop.index]}"/>
        </c:when>
        <c:otherwise>
          <c:set var="selected" value="${propVal == value}"/>
        </c:otherwise>
      </c:choose>
              
      <%-- Include a menu option for this value. --%>
      <dspel:option value="${value}" selected="${selected}">
        <c:out value="${value}"/>
      </dspel:option>

    </c:forEach>
  </dspel:select>               

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/enumEditor.jsp#2 $$Change: 651448 $--%>

