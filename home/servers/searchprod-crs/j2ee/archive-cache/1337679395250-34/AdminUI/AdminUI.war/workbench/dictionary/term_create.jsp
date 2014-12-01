<%--
  JSP provides dictionary edition

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_create.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="parentDictId" var="parentDictId"/>
  <d:getvalueof param="parentTermId" var="parentTermId"/>

  <d:importbean var="inspectionHandler" bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler"/>
  <c:set var="parentId" value="${empty parentDictId ? parentTermId : parentDictId}" />
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="parentId" value="${parentId}"/>
    <admin-ui:param name="dictExport" value="${inspectionHandler.searchResponse}"/>
  </admin-ui:initializeFormHandler>

  <c:url value="${dictionaryPath}/term_edit.jsp" var="successURL"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">

      <d:input type="hidden" bean="TermFormHandler.successURL" value="${successURL}" name="successURL"/>
      <d:input type="hidden" bean="TermFormHandler.needInitialization" value="false" name="needInitialization"/>
      <d:input type="hidden" bean="TermFormHandler.exportingTerm" value="${handler.exportingTerm}" name="exportingTerm"/>

      <d:input type="hidden" bean="TermFormHandler.parentId" name="parentId"/>

      <div id="paneContent">
        <d:include page="term_basics.jsp"/>

        <d:include page="termdict_synonyms_table.jsp"/>

        <br/>
      </div>

      <div id="paneFooter">
        <fmt:message var="createButton" key="new_term.button.create"/>
        <fmt:message var="createAddButton" key="new_term.button.create.add"/>
        <fmt:message var="cancelButton" key="new_term.button.cancel"/>
        <fmt:message var="createTooltipButton" key="new_term.button.tooltip.create"/>
        <fmt:message var="createTooltipAddButton" key="new_term.button.tooltip.create.add"/>
        <fmt:message var="cancelTooltipButton" key="new_term.button.tooltip.cancel"/>

        <d:input type="submit" bean="TermFormHandler.createTerm" value="${createButton}" onclick="return checkForm();"
                 title="${createTooltipButton}" name="createTerm" iclass="formsubmitter"/>
        <d:input type="submit" bean="TermFormHandler.createAddTerm" value="${createAddButton}"
                 onclick="return checkForm();" iclass="formsubmitter"
                 title="${createTooltipAddButton}" name="createAddTerm"/>
        <%-- Cancel button --%>
        <d:input bean="TermFormHandler.cancel" type="submit" iclass="formsubmitter"
                   value="${cancelButton}" title="${cancelButtonTooltip}"/>
      </div>
    </d:form>
  </div>
  <c:if test="${not empty inspectionHandler.searchResponse}">
    <dictionary:termDictionaryFindByPrimaryKey termDictionaryId="${parentId}" var="dictionary"/>
    <script type="text/javascript">
      //dojo tree refresh
      top.hierarchy = [{id:"rootDictNode"},{id:"<c:out value="${dictionary.id}"/>", treeNodeType:"<c:out value="${dictionary.nodeType}"/>"}];
      top.syncTree();
    </script>
  </c:if>
  <admin-validator:validate beanName="TermFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_create.jsp#2 $$Change: 651448 $--%>
