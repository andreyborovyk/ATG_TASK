<%--
  This is the itemView page for the catalog "Search" tab. It defines all the 
  faceted search admin functionality available to a catalog.
  
    @param  imap          A request scoped, MappedItem
    @param  view          A request scoped, MappedItemView
    @param  formHandler   The form handler

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/catalogSearchEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"          uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"      uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"        uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="biz"        uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="web-ui"       uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui"     uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="dcsui-search" uri="http://www.atg.com/taglibs/dcsui-srch"          %>

<!-- Begin catalogSearchEdit.jsp -->

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<%-- Unpack request-scoped parameters into page parameters --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>
<dspel:getvalueof var="imap" param="imap"/>

<%-- determine which mode we're in --%>
<c:choose>
  <c:when test="${imap.mode == 'AssetManager.edit'}">
    <c:set var="disabled" value="false"/>
  </c:when>
  <c:otherwise>
    <c:set var="disabled" value="true"/>
  </c:otherwise>
</c:choose>

<c:if test="${debug}">
  <p>Editing my refineConfig
  <p>item name: <dspel:valueof bean="${formHandlerPath}.itemDescriptorName"/>
  <br>itemId: <dspel:valueof bean="${formHandlerPath}.repositoryId"/>

  <p>item name: <c:out value="${imap.itemName}" />
  <br>imap: <c:out value="${imap.class.name}" />
  <br>view: <c:out value="${view.class.name}" />
  <br>refineConfig: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.id" /><br>
  <br>formHandlerPath: <c:out value="${formHandlerPath}" /><br>
  <br>canInheritFromGlobal: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.canInheritFromGlobal" />
  <br>canInheritFromCatalog: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.canInheritFromCatalog" />
  <br>canChildrenInherit: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.canChildrenInherit" />
  <br>canInheritFromCategory: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.canInheritFromCategory" />
</c:if>
  <c:if test="${formHandler.shared}">
    <dspel:include page="sharedAssetWarning.jsp"/>
  </c:if>
    <fieldset>
    <legend><span><fmt:message key="catalogFacets" bundle="${bundle}"/></span></legend>
    <table class="formTable">
      <tr>
        <td class="formLabel">
          <fmt:message key="facetInheritance" bundle="${bundle}"/>: 
        </td>
        <td>
          <%-- checkbox --%>
          <dspel:input type="checkbox" 
                       disabled="${disabled}"
                       iclass="checkboxBullet"
                       onchange="markAssetModified()"
                       onpropertychange="formFieldModified()"
                       bean="${formHandlerPath}.canInheritFromGlobal"/><fmt:message key="inheritGlobalFacets" bundle="${bundle}"/>
        </td>
      </tr>
      <tr>
        <td>
        </td>
        <td>
          <%-- checkbox --%>
          <dspel:input type="checkbox"
                       disabled="${disabled}"
                       iclass="checkboxBullet"
                       onchange="markAssetModified()"
                       onpropertychange="formFieldModified()"
                       bean="${formHandlerPath}.canInheritFromCatalog"/><fmt:message key="inheritCatalogFacets" bundle="${bundle}"/>
        </td>
      </tr>
      <tr>
        <td>
        </td>
        <td>
          <%-- checkbox --%>
          <dspel:input type="checkbox"
                       disabled="${disabled}"
                       iclass="checkboxBullet"
                       onchange="markAssetModified()"
                       onpropertychange="formFieldModified()"
                       bean="${formHandlerPath}.canChildrenInherit"/><fmt:message key="allowCatalogInherit" bundle="${bundle}"/>
        </td>
      </tr>

      <c:if test="${debug}">
        <dspel:include page="propertyContainer.jsp">
          <dspel:param name="property" value="refineConfig"/>
        </dspel:include>
      </c:if>

      <%@ include file="inheritedFacets.jspf" %>

      <tr>
        <td class="formLabel">
          <fmt:message key="localFacets" bundle="${bundle}"/>:
        </td>
        <td>
        <c:if test="${debug}">
          <c:set var="propertyView" value="${view.propertyMappings['refineConfig']}"/>
          <p>uri: <c:out value="${propertyView.uri}"/>
          <br>comp item desc: <c:out value="${propertyView.componentRepositoryItemDescriptor}"/>
                <biz:getItemMapping var="testimap" 
                                    mode="edit" 
                                    readOnlyMode="view"
                                    showExpert="true"
                                    itemName="refineConfig"
                                    itemPath="/atg/search/repository/RefinementRepository"/>

                <p>testimap: <c:out value="${testimap.class.name}"/>

                <c:forEach items="${testimap.viewMappings}" var="miv">
                  <c:set var="mpv" value="${miv.propertyMappings['refineElements']}"/>
                  <c:if test="${!empty mpv}">
                    <c:set var="testView" value="${mpv}"/>
                  </c:if>
                </c:forEach>
                <p>testView: <c:out value="${testView.class.name}"/>
                <p>component item type: <c:out value="${testView.componentRepositoryItemDescriptor}"/>
          </c:if>
          <dspel:include page="property/collectionEditor.jsp">
            <dspel:param name="property"             value="refineConfig"/>
            <dspel:param name="subProperty"          value="refineElements"/>
            <dspel:param name="subPropertyType"      value="refineElement"/>
            <dspel:param name="hideComponentReorder" value="${true}"/>
          </dspel:include>
        </td>
      </tr>
    </table>
    </fieldset>

    <%-- Not for 2006.3
    <fieldset>
    <legend><span>Search Result Sorting</span></legend>
    <table class="formTable">
      <tr>
        <td class="formLabel">
          Sorting Properties: 
        </td>
        <td>
          <dspel:include page="property/collectionEditor.jsp">
            <dspel:param name="property"        value="refineConfig"/>
            <dspel:param name="subProperty"     value="sortOptions"/>
            <dspel:param name="subPropertyType" value="sortOption"/>
          </dspel:include>
        </td>
      </tr>
    </table>
    </fieldset>
    --%> 

</dspel:page>

<!-- End catalogSearchEdit.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/catalogSearchEdit.jsp#2 $$Change: 651448 $ --%>
