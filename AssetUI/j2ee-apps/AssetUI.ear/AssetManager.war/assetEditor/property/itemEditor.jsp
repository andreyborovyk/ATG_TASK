<%--
  Default property editor for RepositoryItem values.

  The following request-scoped variables are expected to be set:

  @param  atgIsMultiEditView  True if we are in multiedit.
  @param  atgPropertyViewId   Id of this form field.
  @param  cancelSubTypeURL    ???
  @param  formHandler         The form handler for the form that displays this view
  @param  mpv                 A MappedPropertyView item for this view

  The following paramters may be passed in:

  @param customOnPickFunc      Optional. A custom javascript function to invoke when asset is picked.
  @param customClearValueFunc  Optional. A custom javascript function to invoke when asset is picked.
  @param myPickerHeader        Optional. A custom title for the asset picker popup.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="customOnPickFunc"      param="onPickFunc"/>
  <dspel:getvalueof var="customClearValueFunc"  param="clearValueFunc"/>

  <%--
    you could pass asset picker header from other places in case you want
    to create composite header, that consists of label and params,
    for example:

    Asset Picker {0} for item {1}
  --%>
  <dspel:getvalueof var="assetPickerHeader" param="myPickerHeader"/>
  <c:if test="${empty assetPickerHeader}">
    <c:set var="assetPickerHeader" value="${propertyView.attributes.assetPickerHeader}"/>
  </c:if>

  <%-- Get the descriptor for this property's item type --%>
  <c:set var="itemDescriptor" value="${propertyView.repositoryItemDescriptor}"/>

  <%-- Determine the display name of the currently specified property value.
       This is normally determined using the value of the displayNameProperty
       for the item.  But if there is no displayNameProperty, use the repository
       ID instead. --%>
  <c:set var="displayNameProperty"
     value="${propertyView.propertyDescriptor.propertyBeanInfo.itemDisplayNameProperty}"/>

  <c:if test="${not empty displayNameProperty}">
    <dspel:getvalueof var="displayNameValue" bean="${propertyView.formHandlerProperty}.${displayNameProperty}"/>
  </c:if>

  <c:if test="${empty displayNameValue}">
    <dspel:getvalueof var="displayNameValue" bean="${propertyView.formHandlerProperty}.repositoryId"/>
  </c:if>

  <c:set var="allowCreate"
         value="${not requestScope.atgIsMultiEditView and
                  not propertyView.attributes.hideCreate and
                  not requestScope.atgPropertyDescriptorWrapper.referenceToVersionedItem}"/>

  <c:set var="nullable" value="${not propertyView.propertyDescriptor.required}"/>

  <%-- include the page that renders the item editor --%>
  <dspel:include page="itemEditorPanel.jsp" >
     <dspel:param name="formFieldId"            value="${requestScope.atgPropertyViewId}"/>
     <dspel:param name="formFieldBean"          value="${propertyView.formHandlerProperty}.repositoryId"/>
     <dspel:param name="assetPickerHeader"      value="${assetPickerHeader}"/>
     <dspel:param name="propItemDescriptorName" value="${itemDescriptor.itemDescriptorName}"/>
     <dspel:param name="repositoryPath"         value="${itemDescriptor.repository.absoluteName}"/>
     <dspel:param name="repositoryName"         value="${itemDescriptor.repository.repositoryName}"/>
     <dspel:param name="propertyName"           value="${propertyView.propertyName}"/>
     <dspel:param name="displayNameProperty"    value="${displayNameProperty}"/>
     <dspel:param name="displayNameValue"       value="${displayNameValue}"/>
     <dspel:param name="nullable"               value="${nullable}"/>
     <dspel:param name="allowCreate"            value="${allowCreate}"/>
     <dspel:param name="customOnPickFunc"       value="${customOnPickFunc}"/>
     <dspel:param name="customClearValueFunc"   value="${customClearValueFunc}"/>
     <dspel:param name="allowDrilldown"         value="${not propertyView.attributes.prohibitDrillDown}"/>
     <dspel:param name="allowSelect"            value="${not propertyView.propertyDescriptor.cascadeDelete}"/>
   </dspel:include>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemEditor.jsp#2 $$Change: 651448 $--%>
