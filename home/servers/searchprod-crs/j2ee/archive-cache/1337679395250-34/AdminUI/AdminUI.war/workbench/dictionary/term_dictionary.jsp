<%--
  JSP provides creation/edition of the dictionary

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dictionary.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler"/>
  <d:getvalueof param="dictId" var="dictId"/>

  <dictionary:termDictionaryFindByPrimaryKey termDictionaryId="${dictId}" var="dictionary"/>

  <c:url value="${dictionaryPath}/term_dictionary.jsp" var="backURL">
    <c:param name="dictId" value="${dictId}"/>
  </c:url>
  <c:url value="${dictionaryPath}/term_dictionary.jsp" var="errorURL">
    <c:param name="dictId" value="${dictId}"/>
  </c:url>
  <c:url value="${dictionaryPath}/term_create.jsp" var="addTermUrl">
    <c:param name="parentDictId" value="${dictId}"/>
  </c:url>

  <d:include src="termdicts_navigation.jsp"/>
    
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
      
    <admin-validator:validate beanName="TermDictsFormHandler"/>

    <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler" var="handler"/>
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="dictId" value="${dictId}"/>
    </admin-ui:initializeFormHandler>
    <d:form action="${backURL}" method="POST">

      <div id="paneContent">
        <div id="content1">
          <d:include page="termdict_basics.jsp"/>
          <h3>
            <fmt:message key="term_dictionary.top.level.terms">
              <fmt:param value="${fn:length(dictionary.children)}" />
            </fmt:message>
          </h3>
          <tags:buttonLink titleKey="term_dictionary.button.add.new.term" tooltipKey="term_dictionary.button.tooltip.add.new.term" href="${addTermUrl}"/>

          <tags:selectLink defaultOptionKey="term_dictionary.default" items="${dictionary.children}" var="term">
            <c:url value="${dictionaryPath}/term_edit.jsp" var="termURL">
              <c:param name="termId" value="${term.id}"/>
            </c:url>
            <option value="${termURL}"><c:out value="${term.name}"/></option>
          </tags:selectLink>
        </div>

        <div id="content2" style="display:none">
          <d:include src="termdict_search_projects.jsp"/>
        </div>
      </div>

      <div id="paneFooter">
        <fmt:message var="saveButton" key='term_dictionary.button.save'/>
        <fmt:message var="saveButtonTooltip" key='term_dictionary.button.tooltip.save'/>

        <d:input type="hidden" bean="TermDictsFormHandler.successURL" value="${backURL}" name="successURL"/>
        <d:input type="hidden" bean="TermDictsFormHandler.errorURL" value="${errorURL}" name="errorURL"/>

        <d:input type="submit" bean="TermDictsFormHandler.updateDictionary" value="${saveButton}" iclass="formsubmitter"
                 title="${saveButtonTooltip}" name="saveDictionary" onclick="return checkForm();"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictNode"},{id:"<c:out value="${dictionary.id}"/>", treeNodeType:"<c:out value="${dictionary.nodeType}"/>"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dictionary.jsp#2 $$Change: 651448 $--%>
