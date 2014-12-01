<%--
  JSP, used to create new term weight set. It includes term_weight_set_navigation.jsp,
  term_weight_set_general.jsp, term_weight_set_search_projects.jsp pages.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/new_term_weight_set.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- search project id. it is not empty is we came from manage project customizations page --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler"/>

  <c:if test="${not empty projectId}">
    <d:setvalue bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler.initProjectId" value="${projectId}"/>
  </c:if>
  <%-- URL definitions --%>
  <c:url value="${weightPath}/term_weight_set.jsp" var="successURL"/>
  <c:url value="${weightPath}/new_term_weight_set.jsp" var="sortURL"/>
  <c:url value="${weightPath}/new_term_weight_set.jsp" var="errorURL"/>

  <%-- Top navigation of the page --%>
  <d:include src="term_weight_set_navigation.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">
      <div id="paneContent">
        <div id="content1">
          <%-- General tab --%>
          <d:include src="term_weight_set_general.jsp"/>
        </div>

        <div id="content2" style="display:none">
          <%-- Used in search projects tab --%>
          <d:include src="term_weight_set_search_projects.jsp"/>
        </div>
      </div>

      <d:input bean="ManageTermWeightSetFormHandler.successURL" type="hidden" value="${successURL}" name="successURL"/>
      <d:input bean="ManageTermWeightSetFormHandler.SortNewWeightSetURL" type="hidden" value="${sortURL}" name="SortNewWeightSetURL"/>
      <d:input bean="ManageTermWeightSetFormHandler.errorURL" type="hidden" value="${errorURL}" name="errorURL"/>

      <div id="paneFooter">
        <%-- Create button --%>
        <fmt:message key="term_weight.buttons.create" var="createButton"/>
        <fmt:message key="term_weight.buttons.create" var="createButtonTooltip"/>
        <d:input bean="ManageTermWeightSetFormHandler.create" value="${createButton}"
                 title="${createButtonTooltip}"
                 type="submit" iclass="formsubmitter" onclick="return checkForm()"/>
        <%-- Cancel button --%>
        <fmt:message key="term_weight.buttons.cancel" var="cancelButton"/>
        <fmt:message key="term_weight.buttons.cancel.tooltip" var="cancelButtonTooltip"/>
        <d:input bean="ManageTermWeightSetFormHandler.cancel" value="${cancelButton}"
                 title="${cancelButtonTooltip}" type="submit" iclass="formsubmitter" />

        <admin-validator:validate beanName="ManageTermWeightSetFormHandler"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/new_term_weight_set.jsp#2 $$Change: 651448 $--%>
