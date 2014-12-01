<%--
  facetset_delete_popup.jsp provides popup for deleting facet set

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_delete_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="facetSetId" var="facetSetId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteFacetSetFormHandler"/>

  <c:url value="${facetPath}/facetset_delete_popup.jsp" var="actionUrl">
    <c:param name="facetSetId" value="${facetSetId}"/>
  </c:url>
  <c:url var="targetUrl" value="${facetPath}/facetsets_general.jsp"/>

  <d:form action="${actionUrl}" method="POST">
    <d:input type="hidden" bean="DeleteFacetSetFormHandler.errorURL"     value="${actionUrl}" name="errorURL"/>
    <d:input type="hidden" bean="DeleteFacetSetFormHandler.successURL"  value="${targetUrl}" name="successURL"/>

    <facets:facetSetFindByPrimaryKey facetSetId="${facetSetId}" var="facetSet"/>

    <div class="content">
      <p>
        <fmt:message key="facetset_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="facetset_delete_popup.question2">
                <fmt:param>
                  <c:out value="${facetSet.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>

      <p>
        <fmt:message key="facetset_delete_popup.used_in_search_projects"/>
      </p>
      <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${facetSetId}" itemType="facet_set"/>
      <ul>
        <c:choose>
          <c:when test="${empty projects}">
            <li>
              <fmt:message key="facetset_delete_popup.used_in_search_projects.none"/>
            </li>
          </c:when>
          <c:otherwise>
            <c:forEach items="${projects}" var="currentProject">
              <li>
                <c:out value="${currentProject.name}"/>
              </li>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="DeleteFacetSetFormHandler.facetSetId" value="${facetSetId}" name="facetSetId"/>
      <fmt:message key="facetset_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="facetset_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteFacetSetFormHandler.deleteFacetSet" type="submit" value="${deleteButtonTitle}"
               title="${deleteButtonToolTip}" name="delete" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='facetset_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='facetset_delete_popup.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_delete_popup.jsp#2 $$Change: 651448 $--%>
