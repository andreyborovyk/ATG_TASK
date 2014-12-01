<%--
Edit facet page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="facetId" var="facetId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler" />

  <c:url value="${facetPath}/facet.jsp" var="successURL">
    <c:param name="facetId" value="${facetId}"/>
  </c:url>
  <c:url value="${facetPath}/facet.jsp" var="errorURL">
    <c:param name="facetId" value="${facetId}"/>
  </c:url>
  
  <%-- Pre-populating the form handler --%>
  <d:getvalueof bean="FacetFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="facetId" value="${facetId}"/>
  </admin-ui:initializeFormHandler>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">

    <d:form action="facet.jsp" method="post">
      <div id="paneContent">
        <d:include page="facet_basics.jsp"/>

        <facets:facetFindByPrimaryKey facetId="${facetId}" var="facet"/>
        <h3>
          <fmt:message key="facet.child_facets">
            <fmt:param value="${fn:length(facet.childFacets)}" />
          </fmt:message>
        </h3>

        <%-- Add new facet button --%>
        <p>
          <c:url value="${facetPath}/facet_new.jsp" var="addNewFacetURL">
            <c:param name="baseFacetId" value="${facetId}"/>
          </c:url>
          <input id="addNewFacet" name="addNewFacet"
                 type="button" value="<fmt:message key='facet.add_new_facet'/>"
                 title="<fmt:message key='facet.add_new_facet'/>"
                 onclick="return loadRightPanel('${addNewFacetURL}');"/>
        </p>

        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.facetComparator" var="comparator"/>
        <admin-ui:sort var="facets" items="${facet.childFacets}" comparator="${comparator}" sortMode="undefined"/>
      
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="facet" items="${facets}">
          <admin-ui:column title="table_readonly.facet.name" name="name" type="static">
            <c:url var="facetUrl" value="${facetPath}/facet.jsp">
              <c:param name="facetId" value="${facet.id}"/>
            </c:url>
            <a href="${facetUrl}" onclick="return loadRightPanel(this.href);">
              ${facet.name}
            </a>
          </admin-ui:column>
          <admin-ui:column title="table_readonly.facet.property" name="description" type="static">
            ${facet.property}
          </admin-ui:column>
          <admin-ui:column title="table_readonly.facet.property.type" name="mappings" type="static">
            ${facet.propertyType}
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="facet_table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#" onclick="return copyFacet('${facet.id}');">copy</a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="facet_table.delete" var="deleteTitle"/>
            <c:url value="${facetPath}/facet_delete_popup.jsp" var="popUrl">
              <c:param name="facetId" value="${facet.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>
        <script type="text/javascript">
          function copyFacet(id) {
            document.getElementById("refConfigId").value = id;
            document.getElementById("copyInput").click();
            return false;
          }
        </script>
        <d:include page="facet_edit.jsp"/>
      </div>
      <d:input type="hidden" bean="FacetFormHandler.facetId" name="facetId"/>
      <d:input bean="FacetFormHandler.needInitialization" type="hidden"
               name="needInitialization" value="false" id="needInitializationInput"/>
      <d:input type="hidden" bean="FacetFormHandler.successURL" value="${successURL}" name="successURL" />
      <d:input type="hidden" bean="FacetFormHandler.errorURL" value="${errorURL}" name="errorURL"/>
      <d:getvalueof bean="FacetFormHandler.nextFacetId" var="nextFacetId"/>
      <c:url value="${facetPath}/facet.jsp" var="nextFacetURL">
        <c:param name="facetId" value="${nextFacetId}"/>
      </c:url>
      <d:input type="hidden" bean="FacetFormHandler.successAlternativeURL" value="${nextFacetURL}" name="successAlternativeURL" />
      <d:input type="hidden" bean="FacetFormHandler.refConfigId" id="refConfigId" name="refConfigId"/>
      <d:input bean="FacetFormHandler.copyFacetTable" value="field mode" type="submit" iclass="formsubmitter"
               id="copyInput" style="display:none" name="copyInput"/>

      <div id="paneFooter">
        <fmt:message var="saveButton" key="facet.save"/>
        <fmt:message var="saveEditNextButton" key="facet.save_edit_next"/>
        <fmt:message var="saveButtonTooltip" key="facet.tooltip.save"/>
        <fmt:message var="saveEditNextButtonTooltip" key="facet.tooltip.save_edit_next"/>
        <d:input type="submit" value="${saveButton}" bean="FacetFormHandler.save" iclass="formsubmitter"
                 title="${saveButtonTooltip}" onclick="return checkForm()" id="saveFacetButton"/>
        <d:input type="submit" disabled="${empty nextFacetId}" bean="FacetFormHandler.saveEditNext" iclass="formsubmitter"
                 value="${saveEditNextButton}" title="${saveEditNextButtonTooltip}" />
        <admin-validator:validate beanName="FacetFormHandler"/>
      </div>
    </d:form>
  </div>
  <admin-beans:getFacetHierarchy facetId="${facetId}" var="hierarchy"/>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = new Array();
    top.hierarchy[0] = {id: "rootFacetSetNode"};
    <c:forEach items="${hierarchy}" var="item" varStatus="status">
      top.hierarchy[${status.index + 1}] = {id:"${item.id}", treeNodeType:"${item.baseFacetType}"}
    </c:forEach>
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet.jsp#2 $$Change: 651448 $--%>
