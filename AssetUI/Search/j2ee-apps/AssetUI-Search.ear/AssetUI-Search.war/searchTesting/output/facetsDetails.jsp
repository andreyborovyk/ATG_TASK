<%--
  Facets Details Table.

  Included into facets.jsp.

  The following parameters are passed into the page:

  @param facets              collection of facets
  @param searchResponse      search response object
  @param applyChangedFacets  true if we are applying changes from current project
  @param categoryId          id of the selected category

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facetsDetails.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="assetui-ui" uri="http://www.atg.com/taglibs/asset-ui" %>
<%@ taglib prefix="pws" uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<dspel:getvalueof var="facets" param="facets"/>
<dspel:getvalueof var="searchResponse" param="searchResponse"/>
<dspel:getvalueof var="applyChangedFacets" param="applyChangedFacets"/>
<dspel:getvalueof var="categoryId" param="categoryId"/>
<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>
<c:choose>
  <c:when test="${empty facets || empty searchResponse.refinements.refinementsList}">
    <p class="atg_fildsetNote">
      <fmt:message key="facets.noFacets"/>
    </p>
    <br/>
  </c:when>
  <c:otherwise>
    <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultFacetsDetails.facet.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultFacetsDetails.property.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultFacetsDetails.range.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultFacetsDetails.results.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultFacetsDetails.status.title"/>
      </th>
    </tr>
    <c:forEach items="${facets}" var="facet" varStatus="refStatus">
    <%--every even row has background--%>
    <c:choose>
      <c:when test="${0 == (refStatus.index mod 2)}">
        <c:set var="trClass" value=""/>
      </c:when>
      <c:otherwise>
        <c:set var="trClass" value="atg_altRow"/>
      </c:otherwise>
    </c:choose>
    <assetui-search:getContainerSize container="${facet.refinement.refinementValues}" var="refinementValuesCount"/>
    <%--choose between has facet value or hasn't(error status)--%>
    <c:choose>
      <c:when test="${refinementValuesCount > 0}">
        <%--iterate over facet values and create table using rowspan for Facet, Property and Status columns--%>
        <c:forEach items="${facet.values}" var="facetValue" varStatus="valStatus">
          <tr class="<c:out value='${trClass}'/>">
            <c:if test="${valStatus.index == 0}">
              <td rowspan="<c:out value='${refinementValuesCount}'/>">
                  <%--add <del> element if status of facet is not succeed --%>
                <c:if test="${facet.refinement.debug != null && facet.refinement.debug != ''}">
                <del></c:if>
                  <%--add '>' to show that facet is child. The count of '>' depends on nesting --%>
                  <c:if test="${facet.refinement.level > 0}">
                    <c:forEach begin="1" end="${facet.refinement.level}">></c:forEach>
                  </c:if>
                  <assetui-ui:getResourceValue key="${facet.refinement.label}" var="label"/>
                  <c:out value="${label}"/>
                  <c:if test="${facet.refinement.debug != null && facet.refinement.debug != ''}">
                </del>
                </c:if>
              </td>
              <td rowspan="<c:out value='${refinementValuesCount}'/>">
                <assetui-search:propertyDisplayNameResolver propertyName="${facet.refinement.name}" var="property"/>
                <c:out value="${property}"/>
              </td>
            </c:if>
            <td>
            
              <%--prepare range or value label--%>
              <c:set var="rangeOrValue">
                <c:choose>
                  <%--range label--%>
                  <c:when test="${facet.refinement.range}">
                    <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.refinement.name}" propertyValue="${facetValue.refinementValue.rangeStart}" 
                      var="rangeStartDisplayValue"/>
                    <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.refinement.name}" propertyValue="${facetValue.refinementValue.rangeEnd}" 
                      var="rangeEndDisplayValue"/>
                    
                    <fmt:message key="searchTestingResultFacetsDetails.range.value">
                      <fmt:param value="${rangeStartDisplayValue}"/>
                      <fmt:param value="${rangeEndDisplayValue}"/>
                    </fmt:message>
                  </c:when>
                  <%--value label--%>
                  <c:otherwise>
                    <%--if facet type is date then convert it in date format--%>              
                    <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.refinement.name}" propertyValue="${facetValue.refinementValue.value}" 
                      var="valueDisplayValue"/>
                    <c:out value="${valueDisplayValue}" escapeXml="false"></c:out>
                  </c:otherwise>
                </c:choose>
              </c:set>
    
              <c:choose>
                <%--link--%>
                <c:when test="${facet.refinement.debug != 'filter'}">
                  <a href="#" name="<c:out value='${facetValue.trailString}'/>"
                     onclick="performFacetedSearch(this.name); return false;" 
                     title="<fmt:message key='searchTestingResultFacetsDetails.range.value.title'/>"/>
                     <c:out value="${rangeOrValue}"/>
                  </a>
                </c:when>
                <c:otherwise>
                <%--plain text--%>
                  <c:out value="${rangeOrValue}"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:choose>
                <c:when test="${facetValue.refinementValue.count == searchResponse.refinements.selectConfig.total}">
                  <fmt:message key="searchTestingResultDeterminationCategory.all.value">
                    <fmt:param value="${facetValue.refinementValue.count}"/>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <c:out value="${facetValue.refinementValue.count}"/>
                </c:otherwise>
              </c:choose>
            </td>
            <c:if test="${valStatus.index == 0}">
              <td rowspan="<c:out value='${refinementValuesCount}'/>">
                <c:choose>
                  <c:when test="${facet.refinement.debug == 'filter'}">
                    <fmt:message key="searchTestingResultFacetsDetails.filter.status"/>
                  </c:when>
                  <c:when test="${facet.refinement.debug == 'one'}">
                    <fmt:message key="searchTestingResultFacetsDetails.singleIgnored.status"/>
                  </c:when>
                  <c:when test="${facet.refinement.debug == 'refineMax'}">
                    <fmt:message key="searchTestingResultFacetsDetails.max.status"/>
                  </c:when>
                  <c:otherwise>
                    <assetui-search:hasFacetBeenModified facetId="${facet.refinement.id}" var="isFacetModified"/>
                    <c:if test="${isFacetModified}">
                      <fmt:message key="searchTestingResultFacetsDetails.modified.status"/>
                      <c:set var="facetsWasModified" value="true"/>
                    </c:if>
                  </c:otherwise>
                </c:choose>
              </td>
            </c:if>
          </tr>
        </c:forEach>
      </c:when>
      <%--simple row in case of error status. Not need to show values, so not need to use rowspan--%>
      <c:otherwise>
        <tr class="<c:out value='${trClass}'/>">
          <td>
              <%--add <del> element if status of facet is not succeed --%>
            <c:if test="${facet.refinement.debug != null && facet.refinement.debug != ''}">
            <del></c:if>
              <c:if test="${facet.refinement.level > 0}">
                <c:forEach begin="1" end="${facet.refinement.level}">></c:forEach>
              </c:if>
              <c:out value="${facet.refinement.label}"/>
              <c:if test="${facet.refinement.debug != null && facet.refinement.debug != ''}">
            </del>
            </c:if>
          </td>
          <td>
            <c:out value="${facet.refinement.name}"/>
          </td>
          <td>
            <fmt:message key="searchTestingResultFacetsDetails.no.value"/>
          </td>
          <td>
            <fmt:message key="searchTestingResultFacetsDetails.no.value"/>
          </td>
          <td>
            <c:choose>
              <c:when test="${facet.refinement.debug == 'none'}">
                <fmt:message key="searchTestingResultFacetsDetails.error.status"/>
              </c:when>
              <c:when test="${facet.refinement.debug == 'filter'}">
                <fmt:message key="searchTestingResultFacetsDetails.filter.status"/>
              </c:when>
            </c:choose>
          </td>
        </tr>
    
      </c:otherwise>
    </c:choose>
    
    </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<c:if test="${facetsWasModified}">
  <p class="atg_tableNote">
    <fmt:message key="searchTestingResultFacetsDetails.modifiedDescription"/>
  </p>
</c:if>
<%--show view with changes link only if user is in context of project and category wasn't indicated --%>
<pws:getCurrentProject var="projectContext"/>
<c:set var="project" value="${projectContext.project}"/>
<c:set var="isInTheContext" value="${(not (empty project)) and (project.editable)}"/>
<c:if test="${isInTheContext && (categoryId == null || categoryId == '') && 
  (not (empty formHandler.testingMode)) and (true eq formHandler.testingMode)}">
  <p class="atg_tableNote">
    <c:choose>
      <c:when test="${applyChangedFacets}">
        <a href="#" onclick="applyChangedFacets('false'); return false;">
          <fmt:message key="searchTestingResultFacetsDetails.viewWithoutChanges.link"/>
        </a>
      </c:when>
      <c:otherwise>
        <c:if test="${formHandler.hasChangedFacets}">
          <span id="undeployedWithoutCategoryAlert2"><fmt:message key="searchTestingFacetInput.undeployedWithoutCategory.text"/></span>
          <br/>
        </c:if>
        <a href="#" onclick="applyChangedFacets('true'); return false;">
          <fmt:message key="searchTestingResultFacetsDetails.viewWithChanges.link"/>
        </a>
      </c:otherwise>
    </c:choose>
    <br/>
    <fmt:message key="searchTestingResultFacetsDetails.actionDescription"/>
  </p>
</c:if>

<script type="text/javascript">
  function performFacetedSearch(value) {
    var controls = document.getElementsByTagName('select');
    for (var i = controls.length; --i >= 0; ) {
      if ('refinementSelect' != controls[i].getAttribute('name'))
        continue;
      controls[i].selectedIndex = -1;
    }

    var hidden = document.getElementById('searchTestingInputsFacetClicked');
    hidden.value = value;
    document.getElementById('facetSearchButton').click();
  }
  function applyChangedFacets(value) {
    var hidden = document.getElementById('searchTestingInputsApplyFacets');
    hidden.value = value;
    document.getElementById('applyFacetChangesButton').click();
  }
</script>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facetsDetails.jsp#2 $$Change: 651448 $--%>
