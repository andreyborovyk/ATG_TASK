<%--
  Default property editor for boolean values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/booleanEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  

  <%-- Display the property value in a radio group --%>
  <dspel:input type="radio" id="${inputId}_true"
               onchange="markAssetModified()"
               onpropertychange="formFieldModified()"
               bean="${propertyView.formHandlerProperty}" value="true"/>
  <label for="<c:out value='${inputId}'/>_true">
    <fmt:message key="propertyEditor.yes"/>
  </label>
  <dspel:input type="radio" id="${inputId}_false"
               onchange="markAssetModified()"
               onpropertychange="formFieldModified()"
               bean="${propertyView.formHandlerProperty}" value="false"/>
  <label for="<c:out value='${inputId}'/>_false">
    <fmt:message key="propertyEditor.no"/>
  </label>

 </dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/booleanEditor.jsp#2 $$Change: 651448 $--%>
