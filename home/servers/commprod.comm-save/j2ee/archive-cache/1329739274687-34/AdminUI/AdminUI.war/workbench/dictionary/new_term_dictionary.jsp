<%--
  JSP provides creation/edition of the dictionary

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/new_term_dictionary.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler" />
  <%-- search project id. it is not empty is we came from manage project customizations page --%>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="dictId" var="dictId"/>
  <d:getvalueof param="action" var="action" />
  <c:if test="${not empty projectId}">
    <d:setvalue bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler.initProjectId" value="${projectId}"/>
  </c:if>

  <c:url value="${dictionaryPath}/term_dictionary.jsp" var="successURL"/>
  <c:url value="${dictionaryPath}/new_term_dictionary.jsp" var="errorURL"/>

  <d:include src="termdicts_navigation.jsp" />
    
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
  
    <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler" var="handler"/>
    <d:form action="${formActionUrl}" method="POST">
      <div id="paneContent">
        <div id="content1">
          <d:include page="termdict_basics.jsp"/>
        </div>

        <div id="content2" style="display:none">
          <d:include src="termdict_search_projects.jsp"/>
        </div>
      </div>

      <div id="paneFooter">
        <fmt:message var="createButton" key='new_term_dictionary.button.create'/>
        <fmt:message var="cancelButton" key='new_term_dictionary.button.cancel'/>
        <fmt:message var="createButtonTooltip" key='new_term_dictionary.button.tooltip.create'/>
        <fmt:message var="cancelButtonTooltip" key='new_term_dictionary.button.tooltip.cancel'/>

        <d:input type="hidden" bean="TermDictsFormHandler.successURL" value="${successURL}"  name="successURL"/>
        <d:input type="hidden" bean="TermDictsFormHandler.errorURL"   value="${errorURL}" name="errorURL"/>

        <d:input type="submit" bean="TermDictsFormHandler.createDictionary" value="${createButton}" iclass="formsubmitter"
                 onclick="return checkForm();" title="${createButtonTooltip}" name="createDictionary"/>
        <%-- Cancel button --%>
        <d:input bean="TermDictsFormHandler.cancel" type="submit" iclass="formsubmitter"
                 value="${cancelButton}" title="${cancelButtonTooltip}"/>
      </div>

      <admin-validator:validate beanName="TermDictsFormHandler"/>
      
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/new_term_dictionary.jsp#2 $$Change: 651448 $--%>
