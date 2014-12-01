<%--
  Default property viewer for enumerated values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/enumViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"               %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Find the enumerated property descriptor associated with the given
       property descriptor. --%>
  <web-ui:findEnumPropertyDescriptor var="propDesc"
    propertyDescriptor="${propertyView.propertyDescriptor}"/>

  <%-- Display the property value.  If this property stores the value as a
       numeric code, we have to use a method on the property descriptor to
       obtain the display value.  Otherwise, we can just display the value
       directly. --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>
  <c:if test="${propDesc.useCodeForValue}">
    <web-ui:invoke var="propVal" bean="${propDesc}" method="convertCodeToValue">
      <web-ui:parameter type="java.lang.Integer" value="${propVal}"/>
    </web-ui:invoke>
  </c:if>
  <c:out value="${propVal}" escapeXml="false"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/enumViewer.jsp#2 $$Change: 651448 $--%>
