<%--
  Editor for the application property.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/applicationNameEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  

  <%-- Get the value we will set for this property --%>
  <c:set var="appName" value="${config.sessionInfo.currentApplicationName}"/>

  <dspel:input type="hidden" id="${inputId}" value="${appName}" bean="${propertyView.formHandlerProperty}"/>
</dspel:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/applicationNameEditor.jsp#2 $$Change: 651448 $--%>

