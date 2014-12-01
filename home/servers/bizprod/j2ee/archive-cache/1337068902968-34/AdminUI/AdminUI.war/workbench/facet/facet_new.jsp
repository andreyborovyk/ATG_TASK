<%--
  Page, used to create new facet.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_new.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="baseFacetId" var="baseFacetId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
  <d:getvalueof bean="FacetFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="baseFacetId" value="${baseFacetId}" />
  </admin-ui:initializeFormHandler>

  <%-- URL definitions --%>
  <c:url value="${facetPath}/facet.jsp" var="successURL"/>
  <c:url value="${facetPath}/facet_new.jsp" var="createAndAddURL">
    <c:param name="baseFacetId" value="${baseFacetId}"/>
  </c:url>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
  
    <d:form method="POST" action="facet_new.jsp">
      <div id="paneContent">
        <%-- General tab --%>
        <d:include page="facet_basics.jsp"/>
        <d:include page="facet_edit.jsp"/>
      </div>

      <d:input type="hidden" bean="FacetFormHandler.successURL" value="${successURL}" name="successURL" />
      <d:input type="hidden" bean="FacetFormHandler.successAlternativeURL" value="${createAndAddURL}" name="successAlternativeURL" />
      <d:input type="hidden" bean="FacetFormHandler.baseFacetId" name="baseFacetId"/>
      <d:input type="hidden" bean="FacetFormHandler.needInitialization" value="false" name="needInitialization"/>

      <div id="paneFooter">
        <fmt:message var="createButtonTooltip" key="facet.tooltip.create"/>
        <fmt:message var="createAddAnotherButtonTooltip" key="facet.tooltip.create_add_another"/>
        <fmt:message var="cancelButtonTooltip" key="facet.tooltip.cancel"/>
        <fmt:message var="createButton" key="facet.create"/>
        <fmt:message var="createAddAnotherButton" key="facet.create_add_another"/>
        <fmt:message var="cancelButton" key="facet.cancel"/>
        <d:input type="submit" value="${createButton}" bean="FacetFormHandler.create" iclass="formsubmitter"
                 title="${createButtonTooltip}" id="createButton" onclick="return checkForm()"/>
        <d:input type="submit" value="${createAddAnotherButton}" bean="FacetFormHandler.createAddAnother" iclass="formsubmitter"
                 title="${createAddAnotherButtonTooltip}" onclick="return checkForm()"/>
        <d:input type="submit" value="${cancelButton}" bean="FacetFormHandler.cancel" iclass="formsubmitter"
                 title="${cancelButtonTooltip}" />
        <admin-validator:validate beanName="FacetFormHandler"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_new.jsp#2 $$Change: 651448 $--%>
