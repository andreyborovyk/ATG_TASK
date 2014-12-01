<%--
  JSP, used to update existing term weight set. It includes term_weight_set_navigation.jsp,
  term_weight_set_general.jsp, term_weight_set_search_projects.jsp pages.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler"/>
  <d:getvalueof param="termWeightId" var="termWeightId"/>

  <%-- URL definitions --%>
  <c:url value="${weightPath}/term_weight_set.jsp" var="successURL">
    <c:param name="termWeightId" value="${termWeightId}"/>
  </c:url>

  <c:url value="${weightPath}/term_weight_set.jsp" var="errorURL">
    <c:param name="termWeightId" value="${termWeightId}"/>
  </c:url>
  
  <%-- Top navigation of the page --%>
  <d:include src="term_weight_set_navigation.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">
      <%-- Prepopulating formhandler --%>
      <d:getvalueof bean="ManageTermWeightSetFormHandler" var="handler"/>
      <admin-ui:initializeFormHandler handler="${handler}">
        <admin-ui:param name="termWeightId" value="${termWeightId}"/>
      </admin-ui:initializeFormHandler>

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
      <d:input bean="ManageTermWeightSetFormHandler.errorURL" type="hidden" value="${errorURL}" name="errorURL"/>
      <d:input bean="ManageTermWeightSetFormHandler.termId" type="hidden" name="termId"/>
      <d:input bean="ManageTermWeightSetFormHandler.needInitialization" type="hidden" name="needInitialization" value="false"/>

      <div id="paneFooter">
        <fmt:message key="term_weight.buttons.save" var="saveButton"/>
        <fmt:message key="term_weight.buttons.save.tooltip" var="saveButtonTooltip"/>
        <%-- Save button --%>
        <d:input bean="ManageTermWeightSetFormHandler.update" type="submit" iclass="formsubmitter"
                 value="${saveButton}" title="${saveButtonTooltip}" onclick="return checkForm()"/>
        <admin-validator:validate beanName="ManageTermWeightSetFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTermWeightNode"}, {id:"<c:out value="${termWeightId}"/>", treeNodeType:"termWeightSet"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set.jsp#2 $$Change: 651448 $--%>
