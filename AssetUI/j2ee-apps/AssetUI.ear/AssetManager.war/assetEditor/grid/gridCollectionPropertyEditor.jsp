<%--
  Property editor for array, list, or set values that uses a Grid
  to do the actual work of displaying and editing the collection.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/grid/gridCollectionPropertyEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"         uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"    uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>
  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>

  <%-- Get the collection type for this property --%>
  <c:set var="collectionType" value="${propertyView.propertyDescriptor.typeName}"/>
  <c:set var="isMap"  value="${collectionType == 'map'}"/>
  <c:set var="isSet"  value="${collectionType == 'set'}"/>
  <c:set var="isList" value="${not isMap and not isSet}"/>

  <%-- Determine the full bean path of the property being edited
       and the number of elements in the collection. --%>
  <c:set var="collectionBeanProp" value="${propertyView.formHandlerProperty}"/>
  <dspel:getvalueof var="elements" bean="${collectionBeanProp}"/>
  <web-ui:collectionPropertySize var="numElements" collection="${elements}"/>

  <%-- Determine if this is a collection of RepositoryItems --%>
  <c:set var="componentDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>
  <c:set var="containsRepositoryItems" value="${not empty componentDescriptor}"/>

  <c:if test="${containsRepositoryItems}">
    <%-- Determine the full bean path of individual items in the collection.
         This is needed to unwrap repository items inside RepositoryFormList
         and RepositoryFormMap. --%>
    <c:set var="elementBeanProp" value="${collectionBeanProp}.value"/>

    <%-- URL for drilling down into individual items in the collection --%>
    <c:url var="drilldownURL" value="/assetEditor/editAsset.jsp">
     <c:param name="pushContext"      value="true"/>
     <c:param name="linkPropertyName" value="${propertyView.propertyName}"/>
    </c:url>

    <%-- Derive a URL for creating a new asset --%>
    <c:url var="createAssetURL" value="/assetEditor/subTypeSelection.jsp">
      <c:param name="repositoryPathName" value="${componentDescriptor.repository.absoluteName}"/>
      <c:param name="itemDescriptorName" value="${componentDescriptor.itemDescriptorName}"/>
      <c:param name="linkPropertyName"   value="${propertyView.propertyName}"/>
      <c:param name="cancelSubTypeURL"   value="${requestScope.cancelSubTypeURL}"/>
    </c:url>

    <%-- Allow the appearance and width of an ID column to be controlled
         using view mapping attributes. --%>
    <c:set var="showIdColumn" value="false"/>
    <c:if test="${not empty propertyView.attributes.showIdColumn}">
      <c:set var="showIdColumn" value="${propertyView.attributes.showIdColumn}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.showIdColumn}">
      <c:set var="showIdColumn" value="${propertyView.componentAttributes.showIdColumn}"/>
    </c:if>
    <c:set var="idColumnWidth" value="60"/>
    <c:if test="${not empty propertyView.attributes.idColumnWidth}">
      <c:set var="idColumnWidth" value="${propertyView.attributes.idColumnWidth}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.idColumnWidth}">
      <c:set var="idColumnWidth" value="${propertyView.componentAttributes.idColumnWidth}"/>
    </c:if>
  </c:if>

  <c:set var="viewModeId" value=""/>
  <c:if test="${propertyView.mode == 'AssetManager.edit'}">
    <c:set var="viewModeId" value="Edit"/>
  </c:if>
  <c:if test="${propertyView.mode == 'AssetManager.view'}">
    <c:set var="viewModeId" value="View"/>
  </c:if>

  <%-- Construct a unique id --%>
  <c:set var="id"
         value="${requestScope.uniqueAssetID}${propertyView.uniqueId}${viewModeId}"/>

  <%-- Determine whether we should support delete, reorder, and add
       operations based on our mode and the type of collection. --%>
  <c:if test="${propertyView.mode == 'AssetManager.edit'}">
    <c:set var="allowDelete" value="true"/>
    <c:set var="hideAddExisting" value="false"/>
    <c:set var="hideAddNew" value="false"/>

    <c:choose>
      <c:when test="${isList}">
        <c:set var="allowReorder" value="true"/>
      </c:when>
      <c:otherwise>
        <c:set var="allowReorder" value="false"/>
      </c:otherwise>
    </c:choose>

    <%-- Optionally allow these to be specified as viewmapping attributes. --%>
    <c:if test="${not empty propertyView.attributes.allowDelete}">
      <c:set var="allowDelete" value="${propertyView.attributes.allowDelete}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.allowDelete}">
      <c:set var="allowDelete" value="${propertyView.componentAttributes.allowDelete}"/>
    </c:if>
    <c:if test="${not empty propertyView.attributes.allowReorder}">
      <c:set var="allowReorder" value="${propertyView.attributes.allowReorder}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.allowReorder}">
      <c:set var="allowReorder" value="${propertyView.componentAttributes.allowReorder}"/>
    </c:if>
    <c:if test="${not empty propertyView.attributes.hideAddNew}">
      <c:set var="hideAddNew" value="${propertyView.attributes.hideAddNew}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.hideAddNew}">
      <c:set var="hideAddNew" value="${propertyView.componentAttributes.hideAddNew}"/>
    </c:if>
    <c:if test="${not empty propertyView.attributes.hideAddExisting}">
      <c:set var="hideAddExisting" value="${propertyView.attributes.hideAddExisting}"/>
    </c:if>
    <c:if test="${not empty propertyView.componentAttributes.hideAddExisting}">
      <c:set var="hideAddExisting" value="${propertyView.componentAttributes.hideAddExisting}"/>
    </c:if>
  </c:if>

  <c:if test="${propertyView.mode == 'AssetManager.view'}">
    <c:set var="allowDelete" value="false"/>
    <c:set var="allowReorder" value="false"/>
    <c:set var="hideAddExisting" value="true"/>
    <c:set var="hideAddNew" value="true"/>
  </c:if>

  <%--  Hide Add Existing if property is cascade delete. --%>
  <c:if test="${propertyView.propertyDescriptor.cascadeDelete}">
    <c:set var="hideAddExisting" value="true"/>
  </c:if>

  <%-- Hide Add New in multi-edit mode or when the property is a
       collection of references from a non-versioned item to versioned items. --%>
  <c:if test="${requestScope.atgIsMultiEditView or
                requestScope.atgPropertyDescriptorWrapper.referenceToVersionedItem}">
    <c:set var="hideAddNew" value="true"/>
  </c:if>

  <%-- Allow the grid model page size to be specified using a view mapping
       attribute. This is the number of items retrieved at a time from
       the server. --%>
  <c:set var="modelPageSize" value="100"/>
  <c:if test="${not empty propertyView.attributes.modelPageSize}">
    <c:set var="modelPageSize" value="${propertyView.attributes.modelPageSize}"/>
  </c:if>
  <c:if test="${not empty propertyView.componentAttributes.modelPageSize}">
    <c:set var="modelPageSize" value="${propertyView.componentAttributes.modelPageSize}"/>
  </c:if>

  <%-- Allow the grid keepRows property to be specified using a view mapping
        attribute. This is the number of items in the grid rendering cache. --%>
  <c:set var="keepRows" value="500"/>
  <c:if test="${not empty propertyView.attributes.keepRows}">
    <c:set var="keepRows" value="${propertyView.attributes.keepRows}"/>
  </c:if>
  <c:if test="${not empty propertyView.componentAttributes.keepRows}">
    <c:set var="keepRows" value="${propertyView.componentAttributes.keepRows}"/>
  </c:if>

  <%-- Allow the appearance of column headers to be controlled using a
       view mapping attribute. --%>
  <c:set var="useColumnHeaders" value="false"/>
  <c:if test="${not empty propertyView.attributes.useColumnHeaders}">
    <c:set var="useColumnHeaders" value="${propertyView.attributes.useColumnHeaders}"/>
  </c:if>
  <c:if test="${not empty propertyView.componentAttributes.useColumnHeaders}">
    <c:set var="useColumnHeaders" value="${propertyView.componentAttributes.useColumnHeaders}"/>
  </c:if>

  <%-- This is how we determine the component JSP, in order of preference:
       1) If present, use the componentContextRoot and componentUri defined
          as attributes on the propertyView. This allows configuration at the
          property level for specific properties on items.
       2) Use propertyView.componentUri and propertyView.componentContextRoot.
          In the case of collections of repository items, the viewmapping
          system will automatically check first for a component editor
          whose type is the encoded repository and item type of the items.
          This allows configuration at the item type level.
       3) If no specific property editor exists, fall back and use the
          default editor for the "Repository Item" or other scalar types.
  --%>
  <c:set var="componentContextRoot" value="/${propertyView.componentContextRoot}"/>
  <c:set var="componentURI" value="${propertyView.componentUri}"/>
  <c:if test="${not empty propertyView.attributes.componentContextRoot and
                not empty propertyView.attributes.componentUri}">
    <c:set var="componentContextRoot" value="${propertyView.attributes.componentContextRoot}"/>
    <c:set var="componentURI" value="${propertyView.attributes.componentUri}"/>
  </c:if>

  <%-- Enclose the grid and its associated widgets inside a DIV. --%>
  <div class="formPropCollectionEditor">

    <%-- Display the property name. --%>
    <div class="formLabelCE">
      <c:out value="${propertyView.propertyDescriptor.displayName}"/>
    </div>

    <%-- Use a grid widget to display the collection. --%>
    <dspel:include page="basicGrid.jsp">
      <dspel:param name="componentContextRoot" value="${componentContextRoot}"/>
      <dspel:param name="componentURI" value="${componentURI}"/>
      <dspel:param name="modelPageSize" value="${modelPageSize}"/>
      <dspel:param name="keepRows" value="${keepRows}"/>
      <dspel:param name="useColumnHeaders" value="${useColumnHeaders}"/>
      <dspel:param name="numElements" value="${numElements}"/>
      <dspel:param name="uniqueId" value="${id}"/>
      <dspel:param name="containsRepositoryItems" value="${containsRepositoryItems}"/>
      <dspel:param name="isListCollection" value="${isList}"/>

      <dspel:param name="allowDelete" value="${allowDelete}"/>
      <dspel:param name="allowReorder" value="${allowReorder}"/>
      <dspel:param name="hideAddNew" value="${hideAddNew}"/>
      <dspel:param name="hideAddExisting" value="${hideAddExisting}"/>

      <dspel:param name="collectionAdapter" value="${collectionBeanProp}"/>
      <dspel:param name="collectionBeanProp" value="${collectionBeanProp}"/>

      <c:if test="${containsRepositoryItems}">
        <dspel:param name="drilldownURL" value="${drilldownURL}"/>
        <dspel:param name="elementBeanProp" value="${elementBeanProp}"/>
        <dspel:param name="createAssetURL" value="${createAssetURL}"/>
        <dspel:param name="showIdColumn" value="${showIdColumn}"/>
        <dspel:param name="idColumnWidth" value="${idColumnWidth}"/>
      </c:if>
    </dspel:include>

  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/grid/gridCollectionPropertyEditor.jsp#2 $$Change: 651448 $--%>
