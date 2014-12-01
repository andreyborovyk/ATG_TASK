<%--
  component property editor for searchConfig.rankingProperties (Property Weighting tab)

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyWeightingComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramInitialize"   param="initialize"/>
  <dspel:getvalueof var="paramRenderHeader" param="renderHeader"/>
  <dspel:getvalueof var="paramIndex"        param="index"/>
  <dspel:getvalueof var="paramTemplate"     param="template"/>
  <dspel:getvalueof var="paramDisplayName"  param="displayName"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:choose>

    <%--
                                           INITIALIZE

         This section is only included once by the collection editor, before the component rows are rendered
                                                                                                              --%>
    <c:when test="${paramInitialize}">
    </c:when>

    <%--
                                           HEADER

         Render the table header if requested
                                                                                                              --%>
    <c:when test="${paramRenderHeader}">
      <thead>
        <tr>
          <th class="formValueCell">
            <fmt:message key="propertyWeighting.propertyNameTitle" />
          </th>
          <th class="formValueCell">
            <fmt:message key="propertyWeighting.searchWeightTitle" />
          </th>
          <th class="formValueCell">
            <fmt:message key='propertyWeighting.relativeWeight'/>
          </th>
          <th class="formValueCell">
            <fmt:message key="propertyWeighting.rankValuesTitle" />
          </th>
        </tr>
      </thead>
    </c:when>

    <%--
                             A SINGLE ROW

         This is included for each component row of collection editor
                                                                           --%>
    <c:otherwise>
      <c:if test="${not paramTemplate}">

        <td class="formValueCell">
          <%-- Display the name of the current asset --%>
          <c:out value="${paramDisplayName}"/>
        </td>
        <td>
          <dspel:getvalueof var="weightingValue" bean="${propertyView.formHandlerProperty}[${paramIndex}].weighting"/>
          <dspel:select id="${inputId}" disabled="${!requestScope.atgIsAssetEditable}"
            bean="${requestScope.formHandlerPath}.value.rankingProperties[${paramIndex}].weighting"
            onchange="markAssetModified();updateWeightingValue('${paramIndex}',this.value);updateRelativeWeightingValue();"
            converter="nullable"
            name="foo_${paramIndex}">

            <c:forEach begin="0" end="10" varStatus="weightLoop">
              <c:set var="selected" value="${weightingValue == weightLoop.index}"/>
              <dspel:option value="${weightLoop.index}" selected="${selected}">
                <fmt:message key="propertyWeighting.weight${weightLoop.index}" />
       	      </dspel:option>
            </c:forEach>
          </dspel:select>
          <dspel:img id="rankingImage_${paramIndex}"
            src="/images/ranking/ranking_${weightingValue}.gif"
            otherContext="${config.contextRoot}"/>
        </td>
        <td>
          <span id="foo_<c:out value='${paramIndex}'/>"></span>
        </td>
        <td>
          <c:set var="summarystyle" value="block"/>
          <c:if test="${weightingValue == 0}">
            <c:set var="summarystyle" value="none"/>
          </c:if>
          <div id="rankingValues_<c:out value='${paramIndex}'/>" style="display:<c:out value='${summarystyle}'/>">
            <dspel:include page="/assetEditor/property/valueRankSummary.jsp"/>
          </div>
        </td>

      </c:if> <%-- not paramTemplate --%>

    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyWeightingComponentEditor.jsp#2 $$Change: 651448 $--%>
