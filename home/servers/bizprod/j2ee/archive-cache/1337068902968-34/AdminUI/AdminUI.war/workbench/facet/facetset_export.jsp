<%--
  JSP, used to export facet set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_export.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
   <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">

    <%-- Facet set id--%>
    <d:getvalueof param="facetSetId" var="facetSetId"/>
    <d:getvalueof param="projectId" var="projectId"/>
    <d:getvalueof param="facetSetName" var="facetSetName"/>
    <c:if test="${not empty facetSetId}">
      <facets:facetSetFindByPrimaryKey facetSetId="${facetSetId}" var="facetSet"/>
      <c:set var="itemId" value="${facetSetId}" />
      <c:set var="itemType" value="facet" />
    </c:if>
    <c:if test="${empty facetSetId}">
      <c:set var="itemId" value="${facetSetName}" />
      <c:set var="itemType" value="facetIndexed" />
    </c:if>

    <c:set var="downloadUrl" value="${pageContext.request.contextPath}/download?itemId=${itemId}&itemType=${itemType}&projectId=${projectId}"/>

    <%-- download form--%>
    <form action="<c:out value="${downloadUrl}"/>" method="POST" id="exportForm">
      <div id="paneContent">
        <h3>
          <fmt:message key="facetsets_export.facetset.message"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <fmt:message key="facetsets_export.name"/>
              </td>
              <td>
                <c:if test="${not empty facetSetId}">
                  ${facetSet.name}
                </c:if>
                <c:if test="${empty facetSetId}">
                  ${facetSetName}
                </c:if>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div id="paneFooter">
        <%-- Export button--%>
        <fmt:message key="facetsets_export.button.export" var="updateButtonTitle"/>
        <fmt:message key="facetsets_export.button.export.tooltip" var="updateButtonToolTip"/>
        <input type="button" value="<fmt:message key='facetsets_export.button.export'/>"
                             title="<fmt:message key='facetsets_export.button.export.tooltip'/>"
                             onclick="return submitExportForm();"/>
        <%-- Cancel button--%>
        <fmt:message key="facetsets_export.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="facetsets_export.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
    
    <script type="text/javascript">
      function submitExportForm() {
        document.getElementById("exportForm").submit();
        return false;
      }
    </script>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_export.jsp#2 $$Change: 651448 $--%>
