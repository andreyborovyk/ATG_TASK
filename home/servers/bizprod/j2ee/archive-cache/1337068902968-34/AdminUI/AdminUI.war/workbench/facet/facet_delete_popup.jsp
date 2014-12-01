<%--
  Page provides popup for deleting facet.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_delete_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="facetId" var="facetId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteFacetFormHandler"/>

  <facets:facetFindByPrimaryKey facetId="${facetId}" var="facet"/>
  <c:set var="parent" value="${facet.parentBaseFacet}" />
  <c:choose>
    <c:when test="${parent.baseFacetType eq 'facet'}">
      <c:url value="${facetPath}/facet.jsp" var="targetURL">
        <c:param name="facetId" value="${parent.id}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url value="${facetPath}/facetset.jsp" var="targetURL">
        <c:param name="facetSetId" value="${parent.id}"/>
      </c:url>
    </c:otherwise>
  </c:choose>
  <c:url value="${facetPath}/facet_delete_popup.jsp" var="actionUrl">
    <c:param name="facetId" value="${facetId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">
    <d:input type="hidden" bean="DeleteFacetFormHandler.errorURL"     value="${actionUrl}" name="errorURL"/>
    <d:input type="hidden" bean="DeleteFacetFormHandler.successURL"  value="${targetURL}"/>

    <div class="content">
      <p>
        <fmt:message key="facet_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="facet_delete_popup.question2">
                <fmt:param>
                  <c:out value="${facet.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="DeleteFacetFormHandler.facetId" value="${facetId}" name="facetId"/>
      <fmt:message key="facet_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="facet_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteFacetFormHandler.deleteFacet" type="submit" value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}" name="delete" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='facet_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='facet_delete_popup.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_delete_popup.jsp#2 $$Change: 651448 $--%>
