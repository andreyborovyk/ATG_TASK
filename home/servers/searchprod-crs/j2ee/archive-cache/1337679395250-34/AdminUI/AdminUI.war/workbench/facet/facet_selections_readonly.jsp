<%--
  Page shows "selections" information on "read only" facet page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_selections_readonly.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<d:page>
  <d:getvalueof param="indexedFacet" var="indexedFacet" />

  <c:set value="${indexedFacet.selectionType}" var="selectionOption" />
  <c:choose>
    <c:when test="${selectionOption eq 'oneSelection'}">
      <fmt:message key="facet.selection_option.0"/>
    </c:when>
    <c:when test="${selectionOption eq 'dynamicRangeSelection'}">
      <fmt:message key="facet.selection_option.1"/>
      <ul style="margin:10px 0 0 40px;">
        <li>
          <fmt:message key="facet.selection_option.1.guidelines.0"/>
          &nbsp;
          ${indexedFacet.numberOfRanges}
        </li>
        <li>
          <fmt:message key="facet.selection_option.1.guidelines.1"/>
          &nbsp;
          ${indexedFacet.minFacetValuesPerRange}
        </li>
        <li>
          <fmt:message key="facet.selection_option.1.guidelines.2"/>
          &nbsp;
          ${indexedFacet.numSizeOfRange}
        </li>
        <li>
          <fmt:message key="facet.selection_option.1.guidelines.3"/>
          &nbsp;
          ${indexedFacet.roundSelectionValues}
        </li>
      </ul>
    </c:when>
    <c:when test="${selectionOption eq 'fixedRangeSelection'}">
      <fmt:message key="facet.selection_option.2"/>
      <ul style="margin:10px 0 0 40px;">
        <li>
          <fmt:message key="facet.selection_option.2.guidelines.0"/>
          &nbsp;
          ${indexedFacet.numberOfRanges}
        </li>
        <li>
          <fmt:message key="facet.selection_option.2.guidelines.1"/>
          &nbsp;
          ${indexedFacet.numSizeOfRange}
        </li>
      </ul>
    </c:when>
    <c:when test="${selectionOption eq 'specifiedRangeSelection'}">
      <fmt:message key="facet.selection_option.3"/>
      <c:set var="specifiedFacetRanges" value="${indexedFacet.facetSpecifiedRanges}" />
      <ul style="margin:10px 0 0 40px;">
        <li>
          <table id="addRangePointTable" cellspacing=0 cellpadding=0 style="width:30%">
            <tbody>
              <c:if test="${not empty specifiedFacetRanges}">
                <c:forEach items="${specifiedFacetRanges}" var="item" varStatus="step">
                  <tr>
                    <td style="width:40%">
                      <c:if test="${step.first}">
                        <fmt:message key="facet.from"/>
                      </c:if>
                      <c:if test="${not step.first}">
                        <fmt:message key="facet.to"/>
                      </c:if>
                    </td>
                    <td class="textSpaceTd" <c:if test='${step.last}'>style="border-bottom: 1px solid #c2c2c2"</c:if>>
                      ${item}
                    </td>
                    <td class="iconSpaceTd" <c:if test='${step.last}'>style="border-bottom: 1px solid #c2c2c2"</c:if>>&nbsp;</td>
                  </tr>
                </c:forEach>
              </c:if>
            </tbody>
          </table>
        </li>
      </ul>
    </c:when>
  </c:choose>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_selections_readonly.jsp#2 $$Change: 651448 $--%>
