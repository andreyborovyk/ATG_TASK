<%--
  Facet selector for the inputs. Displays lists of facets with exclusive or non-exclusive seleciton.
  Displays facet trail.

  Included into inputs.jsp.

  The following request-scoped variables are expected to be set:
  
  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler
  @param  formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/facets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                 %>
<%@ taglib prefix="assetui-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <script>
    function refinementSelectOnChange(obj) {
      markAssetModified();
      <c:if test="${formHandler.facetInputModel.mode eq 'single'}">
        controls = document.getElementsByTagName('select');
        for (var i = controls.length; --i >= 0; ) {
          if ('refinementSelect' != controls[i].getAttribute('name'))
            continue;
          controls[i].selectedIndex = (obj != controls[i] ? -1 : controls[i].selectedIndex);
        }
      </c:if>
      return true;
    }

    function clearRefinementSelection() {
      var c = document.getElementById('facets');
      if (c)
        c.style.display = 'none';
      c = document.getElementById('selectedFacets');
      if (c)
        c.style.display = 'none';
      c = document.getElementById('noFacetsReturnedArea');
      if (c)
        c.style.display = 'none';
      c = document.getElementById('undeployedWithoutCategoryAlert');
      if (c)
        c.style.display = 'none';

      var controls = document.getElementsByTagName('input');
      if (controls)
        for (var i = controls.length; --i >= 0; )
          if ('refinementCheckbox' == controls[i].getAttribute('name'))
            controls[i].checked = false;
      controls = document.getElementsByTagName('select');
      if (controls)
        for (var i = controls.length; --i >= 0; )
          if ('refinementSelect' == controls[i].getAttribute('name'))
            controls[i].selectedIndex = -1;

      c = document.getElementById('afterSearchingArea');
      if (c)
        c.style.display = '';
    }
  </script>

  <dspel:input id="removeRefinementButton" type="submit" bean="${formHandlerPath}.facetInputModel.removeFacet" style="display:none"/>
  
  <ul class="atg_facetListing">
    <li>
      <dl>
        <dt><fmt:message key="searchTestingFacetInput.facets.text"/></dt>
        <dd id="afterSearchingArea"<c:if test="${(formHandler.wasSearch or formHandler.facetInputModel.selectedFacetsSize gt 0) and !formHandler.searchCriteriaChanged}"> style="display: none"</c:if>>
          <fmt:message key="searchTestingFacetInput.afterSearching.text"/>
        </dd>
        <c:if test="${formHandler.wasSearch and !formHandler.searchCriteriaChanged}">
          <dspel:getvalueof bean="${formHandlerPath}.value.categoryId" var="categoryId" />
          <c:if test="${empty categoryId and formHandler.testingMode and formHandler.hasChangedFacets and !formHandler.applyChangedFacets}">
            <dd id="undeployedWithoutCategoryAlert"><fmt:message key="searchTestingFacetInput.undeployedWithoutCategory.text"/></dd>
          </c:if>
          <c:if test="${formHandler.facetInputModel.facetsSize eq 0 and formHandler.facetInputModel.selectedFacetsSize eq 0}">
            <dd id="noFacetsReturnedArea"><fmt:message key="searchTestingFacetInput.noFacetsReturned.text"/></dd>
          </c:if>
        </c:if>
      </dl>
    </li>
  </ul>

  <c:if test="${formHandler.facetInputModel.selectedFacetsSize gt 0 and !formHandler.searchCriteriaChanged}">
    <ul id="selectedFacets" class="atg_facetListing">
      <li>
        <b><fmt:message key="searchTestingFacetInput.selected.text"/></b>
        <c:forEach items="${formHandler.facetInputModel.selectedFacets}" var="facet" varStatus="facetStatus">
          <c:forEach items="${facet.values}" var="value" varStatus="facetValueStatus">
            <c:if test="${facetStatus.index gt 0 or facetValueStatus.index gt 0}"><fmt:message key="searchTestingFacetInput.facets.separator.text"/></c:if>
            <dspel:input type="checkbox" bean="${formHandlerPath}.facetInputModel.facetCheckboxControl" name="refinementCheckbox" value="${value}" escapeparamvalue="false" onclick="markAssetModified(); document.getElementById('removeRefinementButton').click(); return true;" />
            <c:choose>
              <c:when test="${value.class.simpleName == 'RangeFacetValue' && value.refinementValue.rangeStart != ''}">
                <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.rangeStart}" var="rangeStart"/>
                <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.rangeEnd}" var="rangeEnd"/>
                <fmt:message key="searchTestingFacetInput.rangeFacet" var="displayName">
                  <fmt:param value="${rangeStart}"/>
                  <fmt:param value="${rangeEnd}"/>
                </fmt:message >
              </c:when>
              <c:otherwise>
                <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.value}" var="displayName"/>
              </c:otherwise>
            </c:choose>
            <c:out value="${displayName}"/>
          </c:forEach>
        </c:forEach>
      </li>
    </ul>
  </c:if>

  <c:if test="${formHandler.facetInputModel.facetsSize gt 0 and !formHandler.searchCriteriaChanged}">
    <ul id="facets" class="atg_facetListing">
      <c:forEach items="${formHandler.facetInputModel.facets}" var="facet">
        <li>
          <dl>
            <dt><assetui-ui:getResourceValue key="${facet.name}" var="label"/><c:out value="${label}"/></dt>
            <dd><dspel:select iclass="multiSelect" bean="${formHandlerPath}.facetInputModel.facetSelectControl" name="refinementSelect" size="6" multiple="true" onchange="return refinementSelectOnChange(this)">
              <c:forEach items="${facet.values}" var="value">
                <dspel:option value="${value}">
                  <c:choose>
                    <c:when test="${value.class.simpleName == 'RangeFacetValue' && value.refinementValue.rangeStart != ''}">
                      <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.rangeStart}" var="rangeStart"/>
                      <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.rangeEnd}" var="rangeEnd"/>
                      <fmt:message key="searchTestingFacetInput.rangeFacet">
                        <fmt:param value="${rangeStart}"/>
                        <fmt:param value="${rangeEnd}"/>
                      </fmt:message >
                    </c:when>
                    <c:otherwise>
                      <assetui-search:propertyValueDisplayTextResolver propertyName="${facet.property}" propertyValue="${value.value}" var="displayName"/>
                      <c:out value="${displayName}"/>
                    </c:otherwise>
                  </c:choose>
                  <fmt:message key="searchTestingFacetInput.option.notSelected">
                    <fmt:param value="${value.matchingDocsCount}"/>
                  </fmt:message >
                </dspel:option>
              </c:forEach>
            </dspel:select></dd>
          </dl>
        </li>
      </c:forEach>
    </ul>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/facets.jsp#2 $$Change: 651448 $--%>
