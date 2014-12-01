<%--
  Provides facet set adaptors table, shows adaptor names with adaptor info.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_adaptors.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <admin-beans:getAdaptorsFacet varItems="adaptors" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <br>

      <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                      var="adaptor" items="${adaptors}" >
        <admin-ui:column title="facetset_adaptors" name="adaptors" type="static">
          <fmt:message key="${adaptor.nameKey}" />
        </admin-ui:column>
        <admin-ui:column title="facetset_adaptors.info" name="info" type="static">
          <c:if test="${not empty adaptor.locationKey}">
            <c:if test="${not empty adaptor.locationParams}" >
              <fmt:message key="${adaptor.locationKey}" var="location">
                <c:if test="${not empty adaptor.locationParams}">
                  <c:forEach var="locationParam" items="${adaptor.locationParams}">
                    <fmt:param value="${locationParam}"/>
                  </c:forEach>
                </c:if>
              </fmt:message>
            </c:if>
            <c:if test="${empty adaptor.locationParams}" >
              <fmt:message key="${adaptor.locationKey}" var="location" />
            </c:if>
            ${location}
          </c:if>
          <c:if test="${not adaptor.available}">
            <c:set var="location"><span style="color:red">${location}</span></c:set>
          </c:if>
        </admin-ui:column>
        <admin-ui:column title="facetset_adaptors.auto.apply" name="autoreply" type="static">
          <c:if test="${adaptor.autoApply eq 'true'}" >
            <fmt:message key="facetset_adaptors.auto.apply.yes" />
          </c:if>
          <c:if test="${adaptor.autoApply eq 'false'}" >
            <fmt:message key="facetset_adaptors.auto.apply.no" />
          </c:if>
        </admin-ui:column>
      </admin-ui:table>
      <br>
      <fmt:message key="facetset_adaptors.auto.apply.note" />
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_adaptors.jsp#2 $$Change: 651448 $ --%>
