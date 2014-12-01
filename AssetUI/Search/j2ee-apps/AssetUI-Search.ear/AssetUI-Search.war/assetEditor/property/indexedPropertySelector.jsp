<%--
    This is a dual property editor, used to view/select availble indexed 
    properties for the repositories configured for this application. It then
    saves the property and its type to the appropriate property names passed 
    into this page as parameters when it was included.
  
    @param  view             A request scoped, MappedItem (itemMapping) item for this view
    @param  imap             A request scoped, MappedItemView (itemViewMapping)
    @param  formHandler      The form handler
    @param  formHandlerPath  The form handler path
    @param  formNameProperty The name of the form handler property that holds the indexed
                             property's name
    @param  formTypeProperty The name of the form handler property that holds the indexed
                             property's type
    @param  resourceBundle   Including page specified resource bundle name (optional)
    @param  typeResourceKey  Resource key for the type property for display in the UI
    @param  nameResourceKey  Resource key for the name property for display in the UI
    @param  readonlyPropName boolean indicating whether the property name field should be editable

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/indexedPropertySelector.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle var="resourceBundle" basename="${config.resourceBundle}"/>

<%-- Get the form handler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>

<%-- Get the form properties --%>
<dspel:getvalueof var="formNameProperty" param="formNameProperty"/>
<dspel:getvalueof var="formTypeProperty" param="formTypeProperty"/>

<%-- Get the resource parameters --%>
<dspel:getvalueof var="readonlyPropName"     param="readonlyPropName"/>
<dspel:getvalueof var="typeResourceKey"      param="typeResourceKey"/>
<dspel:getvalueof var="nameResourceKey"      param="nameResourceKey"/>
<dspel:getvalueof var="userSpecifiedBundle"  param="resourceBundle"/>
<c:choose>
  <c:when test="${!empty userSpecifiedBundle}">
    <fmt:setBundle var="userSpecifiedBundle" basename="${userSpecifiedBundle}"/>
  </c:when>
  <c:otherwise>
    <fmt:setBundle var="userSpecifiedBundle" basename="${config.resourceBundle}"/>
  </c:otherwise>
</c:choose>

<c:if test="${!readonlyPropName}">
  <script type="text/javascript">
    var dropDownArray = new Array();

    <assetui-search:getIndexedPropertyValues var="indexedProperties" />
    <c:forEach items="${indexedProperties}" var="property" varStatus="status">
      var arrayElement<c:out value="${status.index}"/> = new Object();
        arrayElement<c:out value="${status.index}"/>.propertyName =        "<c:out value='${property.name}' escapeXml='false'/>";
        arrayElement<c:out value="${status.index}"/>.propertyType =        "<c:out value='${property.typeString}' escapeXml='false'/>";
        arrayElement<c:out value="${status.index}"/>.propertyTypeDisplay = "<c:out value='${property.displayName}' escapeXml='false'/>";
        dropDownArray[<c:out value="${status.index}"/>] = arrayElement<c:out value="${status.index}"/>;
    </c:forEach>
    
    function setPropertyType()
    {
      var propertiesList = document.getElementById("propertyName");
      var propertyName = propertiesList.value;
      if (propertyName == "_initial_") {
        document.getElementById("propertyTypeLabel").innerHTML = "";
        return;
      }

      document.getElementById("propertyTypeLabel").innerHTML = getPropertyLabel(dropDownArray[propertiesList.selectedIndex - 1].propertyType);
      document.getElementById("propertyType").value = dropDownArray[propertiesList.selectedIndex - 1].propertyType;
      document.getElementById("propertyDsp").value = dropDownArray[propertiesList.selectedIndex - 1].propertyName;

      markAssetModified();
    }

    function getPropertyLabel(propertyType) {
      var res = null;

      if(propertyType == "date") {
        res = '<fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.date"/>';
      }

      if(propertyType == "string") {
        res = '<fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.string"/>';
      }

      if(propertyType == "enum") {
        res = '<fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.enum"/>';
      }

      if(propertyType == "boolean" || propertyType == "bool") {
        res = '<fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.boolean"/>';
      }

      if(propertyType == "float" || propertyType == "integer") {
        res = '<fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.number"/>';
      }
      
      return res;
    }
  
    function createPropertyNameDropdown()
    {
      var selectedProperty = "<c:out value='${param.property}'/>";
      if (selectedProperty == "") 
      {
        selectedProperty = "<dspel:valueof bean='${formHandlerPath}.value.${formNameProperty}'/>";
      }
      var propertyNameSelect = document.getElementById("propertyName");
      for (var i=0; i<dropDownArray.length; i++)
      {
        propertyNameSelect.options.length += 1;
        propertyNameSelect.options[propertyNameSelect.options.length - 1] =
          new Option(dropDownArray[i].propertyTypeDisplay);
        propertyNameSelect.options[propertyNameSelect.options.length - 1].value =
          dropDownArray[i].propertyName;
        if (selectedProperty == dropDownArray[i].propertyName) 
        {
          var selectIndex = propertyNameSelect.options.length - 1;
          propertyNameSelect.options.selectedIndex = selectIndex;
        }
      }
    }
  </script>
</c:if>

<%-- determine which mode we're in --%>
<c:choose>
  <c:when test="${view.itemMapping.mode == 'AssetManager.view'}">
    <c:set var="disabled" value="disabled"/>
  </c:when>
  <c:otherwise>
    <c:set var="disabled" value=""/>
  </c:otherwise>
</c:choose>

  <tr>
    <td class="formLabel">
      <fmt:message bundle="${userSpecifiedBundle}" key="${nameResourceKey}"/>: 
    </td>
    <td>
      <c:choose>
        <c:when test="${readonlyPropName}">
          <dspel:valueof bean="${formHandlerPath}.repositoryItem.displayName"/>
        </c:when>
        <c:otherwise>
          <select id="propertyName" onchange="setPropertyType();" <c:out value='${disabled}'/> >
            <option value="_initial_">
            </option>
          </select>
          <c:if test="${!empty param.property}">
            <dspel:setvalue bean="${formHandlerPath}.value.${formNameProperty}" value="${param.property}"/>
          </c:if>
          <dspel:input type="hidden"
                       id="propertyDsp"
                       bean="${formHandlerPath}.value.${formNameProperty}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="formLabel">
      <fmt:message bundle="${userSpecifiedBundle}" key="${typeResourceKey}"/>: 
    </td>
    <td>
      <span id="propertyTypeLabel"></span>

      <c:if test="${readonlyPropName}">
        <dspel:include otherContext="AssetUI-Search" page="/assetEditor/property/propertyTypeDisplay.jsp">
          <dspel:param name="underlyingValue" bean="${formHandlerPath}.value.${formTypeProperty}"/>
        </dspel:include>
      </c:if>

      <dspel:input type="hidden"
                     id="propertyType" 
                     bean="${formHandlerPath}.value.${formTypeProperty}"/>

      <c:if test="${debug}">
        <dspel:input type="text"
                     id="propertyTypeDebug" 
                     bean="${formHandlerPath}.value.${formTypeProperty}"/>
      </c:if>
    </td>
  </tr>

  <c:if test="${!readonlyPropName}">
    <script type="text/javascript">
      createPropertyNameDropdown();
    </script>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/indexedPropertySelector.jsp#2 $$Change: 651448 $--%>
