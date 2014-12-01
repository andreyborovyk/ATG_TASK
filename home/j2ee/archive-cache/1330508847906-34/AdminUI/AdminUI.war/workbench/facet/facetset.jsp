<%--
  Page, used to edit facet set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="facetSetId" var="facetSetId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageFacetSetFormHandler" />

  <%-- URL definitions --%>
  <c:url value="${facetPath}/facetset.jsp" var="successURL">
    <c:param name="facetSetId" value="${facetSetId}" />
  </c:url>
  <c:url value="${facetPath}/facetset.jsp" var="errorURL">
    <c:param name="facetSetId" value="${facetSetId}" />
  </c:url>

  <d:include src="facetset_navigation.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
      
    <%-- form handler initialization --%>
    <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/ManageFacetSetFormHandler" var="handler"/>
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="facetSetId" value="${facetSetId}"/>
    </admin-ui:initializeFormHandler>

    <d:form action="facetset.jsp" method="POST">
      <div id="paneContent">
        <div id="content1">
          <d:include page="facetset_general.jsp"/>
        </div>
        <div id="content2" style="display:none">
          <d:include src="facetsets_search_projects.jsp"/>
        </div>
      </div>

      <d:input type="hidden" bean="ManageFacetSetFormHandler.successURL" value="${successURL}"  name="successURL"/>
      <d:input type="hidden" bean="ManageFacetSetFormHandler.errorURL"   value="${errorURL}" name="errorURL"/>

      <d:input bean="ManageFacetSetFormHandler.facetSetId" type="hidden" name="facetSetId"/>

      <d:input bean="ManageFacetSetFormHandler.needInitialization" type="hidden" name="needInitialization" value="false"/>

      <div id="paneFooter">
        <fmt:message var="saveButton"        key='facetset.button.save'/>
        <fmt:message var="saveButtonTooltip" key='facetset.button.save.tooltip'/>
        <d:input type="submit" bean="ManageFacetSetFormHandler.update" value="${saveButton}" iclass="formsubmitter"
                 title="${saveButtonTooltip}" name="update" onclick="return checkForm()"/>
      </div>
      <admin-validator:validate beanName="ManageFacetSetFormHandler"/>
    </d:form>
  </div>
  <facets:facetSetFindByPrimaryKey facetSetId="${facetSetId}" var="facetSet"/>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootFacetSetNode"}, {id:"<c:out value="${facetSet.id}"/>", treeNodeType:"<c:out value="${facetSet.baseFacetType}"/>"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset.jsp#2 $$Change: 651448 $--%>
