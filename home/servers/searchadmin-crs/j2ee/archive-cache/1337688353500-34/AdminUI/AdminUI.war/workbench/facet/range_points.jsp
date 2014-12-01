<%--
  Page, showing range points table for facet page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/range_points.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="addMessages" var="addMessages" />
  <d:importbean var="facetFormHandler" bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
  <c:set var="specifiedFacetRanges" value="${facetFormHandler.specifiedFacetRangesAsList}" />
  <admin-dojo:jsonObject>
    <c:if test="${addMessages == null}">
      <admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue>
    </c:if>
    <c:set var="theTable">
      <table id="addRangePointTable" cellspacing="0" cellpadding="0" style="width:30%">
        <tbody>
          <c:if test="${empty specifiedFacetRanges}">
            <tr>
              <td style="width:40%"><fmt:message key="facet.from"/></td>
              <td class="textSpaceTd">
                &nbsp;
              </td>
              <td class="iconSpaceTd" style="border-bottom: 1px solid #c2c2c2;">
                <a class="icon propertyDelete inactive" href="#" onclick="return false;">del</a>
              </td>
            </tr>
          </c:if>
          <c:if test="${not empty specifiedFacetRanges}">
            <c:forEach items="${specifiedFacetRanges}" var="item" varStatus="itemIndex">
              <tr>
                <td style="width:40%">
                  <fmt:message key="facet.${itemIndex.first ? 'from' : 'to'}"/>
                </td>
                <td class="textSpaceTd" <c:if test="${itemIndex.last}">style="border-bottom: 1px solid #c2c2c2;"</c:if>>
                  ${item}
                  <input type="hidden" name="specifiedFacetRanges" value="${item}"/>
                </td>
                <td class="iconSpaceTd" <c:if test="${itemIndex.last}">style="border-bottom: 1px solid #c2c2c2;"</c:if>>
                  <a class="icon propertyDelete" href="#" onclick="return facetSelections.deleteRangePoint(${itemIndex.index});">del</a>
                </td>
              </tr>
            </c:forEach>
          </c:if>
        </tbody>
      </table>
    </c:set>
    <admin-dojo:jsonValue name="tableContent" value="${theTable}" />
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/range_points.jsp#2 $$Change: 651448 $--%>
