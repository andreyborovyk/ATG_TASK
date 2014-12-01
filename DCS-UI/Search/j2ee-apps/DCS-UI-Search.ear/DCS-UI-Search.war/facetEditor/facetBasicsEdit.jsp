<%--
  Defines the Facet Value Source category in the facet detail page
  
    @param  view          A request scoped, MappedItem (itemMapping) item for this view
    @param  imap          A request scoped, MappedItemView (itemViewMapping)
    @param  formHandler   The form handler


  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetBasicsEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"            uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"        uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"          uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dcsui-search" uri="http://www.atg.com/taglibs/dcsui-srch"            %>
<%@ taglib prefix="web-ui"       uri="http://www.atg.com/taglibs/web-ui"                %>

<!-- Begin facetBasicsEdit.jsp -->

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<dspel:importbean var="assetManagerConfig"
                  bean="/atg/web/assetmanager/ConfigurationInfo"/>
<fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

<dspel:getvalueof var="imap" param="imap"/>

<c:if test="${debug}">
  <p>imap.description: <c:out value="${imap.description}"/>
  <br>view.description: <c:out value="${view.description}"/>
</c:if>

<%-- get the formHandler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>

<%-- determine which mode we're in --%>
<c:choose>
  <c:when test="${view.itemMapping.mode == 'AssetManager.view'}">
    <c:set var="disabled" value="disabled"/>
  </c:when>
  <c:otherwise>
    <c:set var="disabled" value=""/>
  </c:otherwise>
</c:choose>

<%-- Set a request variable that is unique across all property views that are
     currently being displayed in the asset editor.  This can be used to
     formulate IDs that must be unique. --%>
<c:set scope="request" var="uniqueAssetID" value="facetDetails_"/>

<dspel:getvalueof scope="request" var="categoryId" param="categoryId"/>

<fieldset>
<legend><span><fmt:message key="facetValueSource" bundle="${bundle}"/></span></legend>
<table class="formTable">
  <dspel:include otherContext="${propView.contextRoot}" page="/assetEditor/property/indexedPropertySelector.jsp">
    <dspel:param name="formNameProperty" value="property"/>
    <dspel:param name="formTypeProperty" value="propertyType"/>
    <dspel:param name="resourceBundle"   value="${config.resourceBundle}"/>
    <dspel:param name="nameResourceKey"  value="property"/>
    <dspel:param name="typeResourceKey"  value="dataType"/>
  </dspel:include>

  <dspel:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dspel:droplet name="IsEmpty">
    <dspel:param name="value" bean="${formHandlerPath}.value.property"/>
    <dspel:oparam name="false">
      <dspel:include page="propertyContainer.jsp">
        <dspel:param name="property" value="label"/>
      </dspel:include>
      <dspel:include page="propertyContainer.jsp">
        <dspel:param name="property" value="priority"/>
      </dspel:include>
    </dspel:oparam>
  </dspel:droplet>
</table>
</fieldset>

<c:if test="${debug}">
  <p>PropertyType: <dspel:valueof bean="${formHandlerPath}.value.propertyType"/>
  <br>PropertyType: <c:out value="${param.propertyType}"/>
  <p>Property: <dspel:valueof bean="${formHandlerPath}.value.property"/>
  <br>Property: <c:out value="${param.property}"/>
</c:if>

<%-- display facet details only if a property and propertyType are selected --%>
<dspel:getvalueof var="propertyType" bean="${formHandlerPath}.value.propertyType"/>
<c:if test="${!empty propertyType}">
  <dspel:include page="facetDetails.jsp"/>
</c:if>

<script type="text/javascript">
  function showFacetPickerForParent ()
  {
    var allowableTypes = new Array(0);
    
    var assetType = new Object();
    assetType.typeCode            = "repository";
    assetType.displayName         = "<fmt:message key='commerceRefineConfigDisplayName' bundle='${bundle}'/>";
    assetType.typeName            = "commerceRefineConfig";
    assetType.repositoryName      = "RefinementRepository";
    assetType.componentPath       = "/atg/search/repository/RefinementRepository";
    assetType.createable          = "false";
    allowableTypes[ allowableTypes.length ] = assetType;  
    
    <dspel:importbean var="productCatalog" bean="/atg/commerce/catalog/MerchandisingProductCatalog"/>
    assetType = new Object();
    assetType.typeCode            = "repository";
    assetType.displayName         = "<fmt:message key='categoryDisplayName' bundle='${bundle}'/>";
    assetType.typeName            = "category";
    assetType.repositoryName      = "<c:out value='${productCatalog.repositoryName}'/>";
    assetType.componentPath       = "<c:out value='${productCatalog.absoluteName}'/>";
    assetType.createable          = "false";
    allowableTypes[ allowableTypes.length ] = assetType;  

    <dspel:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
    <dspel:droplet name="ComponentExists">
      <dspel:param name="path" value="/atg/modules/CustomCatalogs"/>
      <dspel:oparam name="true">
        assetType = new Object();
        assetType.typeCode            = "repository";
        assetType.displayName         = "<fmt:message key='catalogDisplayName' bundle='${bundle}'/>";
        assetType.typeName            = "catalog";
        assetType.repositoryName      = "<c:out value='${productCatalog.repositoryName}'/>";
        assetType.componentPath       = "<c:out value='${productCatalog.absoluteName}'/>";
        assetType.createable          = "false";
        allowableTypes[ allowableTypes.length ] = assetType;  
      </dspel:oparam>
    </dspel:droplet>

    var viewConfiguration = new Array();
    viewConfiguration.treeComponent="/atg/search/web/browse/FacetTreeState";
    
    var picker = new AssetBrowser();
    picker.mapMode                 = "AssetManager.assetPicker";
    picker.clearAttributes         = "true";
    picker.pickerURL               = '/AssetManager/assetPicker.jsp?apView=1&';
    <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle" bundle="${assetManagerBundle}"/>
    <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
    picker.assetPickerTitle        = "<c:out value='${assetPickerTitle}'/>";
    picker.browserMode             = "pick";
    picker.isAllowMultiSelect      = "false";
    picker.createMode              = "none";
    picker.onSelect                = "onPickerEditSelect";
    picker.closeAction             = "hide";
    picker.defaultView             = "Browse";
    picker.assetTypes              = allowableTypes;
    picker.assetPickerParentWindow = window;
    picker.assetPickerFrameElement = parent.document.getElementById('iFrame');
    picker.assetPickerDivId        = "browser";
    picker.viewConfiguration       = viewConfiguration;
    picker.invoke();
  }
        
        
</script>

</dspel:page>

<!-- End facetBasicsEdit.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetBasicsEdit.jsp#2 $$Change: 651448 $ --%>
