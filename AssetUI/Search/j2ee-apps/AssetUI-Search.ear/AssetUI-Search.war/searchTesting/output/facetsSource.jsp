<%--
  Facet Source Table.

  Included into facets.jsp.

  The following parameters are passed into the page:

  @param facetSources        collection of facet sources (from the form handler)

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facetsSource.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="assetui-ui" uri="http://www.atg.com/taglibs/asset-ui" %>

<dspel:page>
<dspel:getvalueof var="facetSources" param="facetSources"/>

<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<table class="atg_dataTable atg_smerch_summaryTable">
<tr>
  <th class="atg_smerch_summaryTitle">
    <fmt:message key="searchTestingResultFacetsSource.facetSource.title"/>
  </th>
  <th class="atg_smerch_summaryTitle">
    <fmt:message key="searchTestingResultFacetsSource.facetOptions.title"/>
  </th>
  <th class="atg_smerch_summaryTitle">
    <fmt:message key="searchTestingResultFacetsSource.facets.title"/>
  </th>
</tr>
  <%--<c:forEach items="${facetSources}" var="facetSource" varStatus="itemStatus">--%>
<c:forEach items="${facetSources}" var="sourceItem" varStatus="itemStatus">

<c:choose>
  <c:when test="${0 == (itemStatus.index mod 2)}">
    <c:set var="trClass" value=""/>
  </c:when>
  <c:otherwise>
    <c:set var="trClass" value="atg_altRow"/>
  </c:otherwise>
</c:choose>

<tr class="<c:out value='${trClass}'/>">
<c:set var="isCurrentElementFacet" value="false"/>
<c:choose>
  <%--todo--%>
  <c:when test="${sourceItem.repositorySource.itemDescriptorName == 'catalog'}">
    <td class="atg_smerch_facetSource_catalog">
      <c:out value="${sourceItem.defaultDisplayName}"/>
    </td>
  </c:when>
  <c:when test="${sourceItem.repositorySource.itemDescriptorName == 'category'}">
    <td class="atg_smerch_facetSource_category">
      <c:out value="${sourceItem.defaultDisplayName}"/>
    </td>
  </c:when>
  <c:when test="${sourceItem.repositorySource.itemDescriptorName == 'refineElement'}">
    <assetui-ui:getResourceValue key="${sourceItem.defaultDisplayName}" var="label"/>
    <td class="atg_smerch_facetSource_facet">
      <c:out value="${label}"/>
    </td>
    <c:set var="isCurrentElementFacet" value="true"/>
  </c:when>
  <c:otherwise>
    <td>
      <c:out value="${sourceItem.defaultDisplayName}"/>
    </td>
  </c:otherwise>
</c:choose>


<td>
  <c:choose>
    <c:when
        test="${sourceItem.repositorySource.global || isCurrentElementFacet}">
      <fmt:message key="searchTestingResultFacetsSource.options.no"/>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${sourceItem.inheritOk}">
          <fmt:message key="searchTestingResultFacetsSource.options.all"></fmt:message>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${sourceItem.inheritAllDisabled}">
              <fmt:message key="searchTestingResultFacetsSource.options.disabled.all" var="disabledOption"/>
            </c:when>
            <c:otherwise>
              <c:set var="disabledOption">
                <c:if test="${sourceItem.globalInheritDisabled}">
                  <fmt:message key="searchTestingResultFacetsSource.options.disabled.global"/>
                  <c:set value="true" var="notFirstOption"/>
                </c:if>
                <c:if test="${sourceItem.parentCategoryInheritDisabled}">
                  <c:if test="${notFirstOption == true}">
                    <fmt:message key="searchTestingResultFacetsSource.options.delimeter"/>
                  </c:if>
                  <fmt:message key="searchTestingResultFacetsSource.options.disabled.parentCategories"/>
                  <c:set value="true" var="notFirstOption"/>
                </c:if>
                <c:if test="${sourceItem.subCategoryInheritDisabled}">
                  <c:if test="${notFirstOption == true}">
                    <fmt:message key="searchTestingResultFacetsSource.options.delimeter"/>
                  </c:if>
                  <fmt:message key="searchTestingResultFacetsSource.options.disabled.subCategories"/>
                  <c:set value="true" var="notFirstOption"/>
                </c:if>
                <c:if test="${sourceItem.catalogInheritDisabled}">
                  <c:if test="${notFirstOption == true}">
                    <fmt:message key="searchTestingResultFacetsSource.options.delimeter"/>
                  </c:if>
                  <fmt:message key="searchTestingResultFacetsSource.options.disabled.parentCatalogs"/>
                </c:if>
              </c:set>
            </c:otherwise>
          </c:choose>
          <fmt:message key="searchTestingResultFacetsSource.options.disabled">
            <fmt:param>
              <c:out value="${disabledOption}"/>
            </fmt:param>
          </fmt:message>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</td>
<td>
  <c:forEach items="${sourceItem.facets}" var="facet" varStatus="status">
    <c:if test="${!status.first}">
      <fmt:message key="searchTestingResultFacetsSource.options.delimeter"/>
    </c:if>
    <assetui-ui:getResourceValue key="${facet.label}" var="label"/>
    <c:choose>
      <c:when test="${facet.inRefineConfig}">
        <c:out value="${label}"/>
      </c:when>
      <c:otherwise>
        <del>
          <c:out value="${label}"/>
        </del>
      </c:otherwise>
    </c:choose>
  </c:forEach>
</td>
</tr>
</c:forEach>
</table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facetsSource.jsp#2 $$Change: 651448 $--%>
