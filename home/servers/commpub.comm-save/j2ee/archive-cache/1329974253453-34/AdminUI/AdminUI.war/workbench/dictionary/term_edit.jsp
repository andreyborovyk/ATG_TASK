<%--
  JSP provides dictionary edition

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_edit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="termId" var="termId" />

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="termId" value="${termId}"/>
  </admin-ui:initializeFormHandler>

  <c:set var="nextId" value="${handler.nextTermId}" />
  <c:url value="${dictionaryPath}/term_edit.jsp" var="successAlternativeURL">
    <c:param name="termId" value="${nextId}" />
  </c:url>
  <c:url value="${dictionaryPath}/term_create.jsp" var="addTermUrl">
    <c:param name="parentTermId" value="${termId}"/>
  </c:url>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">

      <d:input type="hidden" bean="TermFormHandler.successAlternativeURL" value="${successAlternativeURL}" />
      <d:input type="hidden" bean="TermFormHandler.termId" name="termId"/>
      <dictionary:termFindByPrimaryKey termId="${termId}" var="term"/>
      <div id="paneContent">
        <d:include page="term_basics.jsp"/>

        <h3>
          <fmt:message key="term.child.terms">
            <fmt:param value="${fn:length(term.children)}"/>
          </fmt:message>
        </h3>

        <tags:buttonLink href="${addTermUrl}" titleKey="term.button.add.new.term" tooltipKey="term.button.add.new.tooltip.term"/>
        <tags:selectLink defaultOptionKey="term.default" items="${term.children}" var="childTerm">
          <c:url value="${dictionaryPath}/term_edit.jsp" var="childTermURL">
            <c:param name="termId" value="${childTerm.id}"/>
          </c:url>
          <option value="${childTermURL}"><c:out value="${childTerm.name}"/></option>
        </tags:selectLink>

        <d:include page="termdict_synonyms_table.jsp" />

        <br/>
      </div>

      <div id="paneFooter">
        <fmt:message var="saveButton" key="term.button.save"/>
        <fmt:message var="saveEditButton" key="term.button.save.edit"/>
        <fmt:message var="saveTooltipButton" key="term.button.tooltip.save"/>
        <fmt:message var="saveTooltipEditButton" key="term.button.tooltip.save.edit"/>

        <d:input type="submit" bean="TermFormHandler.updateTerm"
                 value="${saveButton}" iclass="formsubmitter"
                 title="${saveTooltipButton}"
                 name="updateTerm" onclick="return checkForm();"/>
        <d:input type="submit" bean="TermFormHandler.updateEditTerm"
                 value="${saveEditButton}" iclass="formsubmitter"
                 title="${saveTooltipEditButton}"
                 name="updateEditTerm" onclick="return checkForm();" disabled="${nextId eq null}"/>
        <d:input type="hidden" bean="TermFormHandler.needInitialization" value="false" name="needInitialization"/>
      </div>
    </d:form>
  </div>
  <admin-beans:getTermHierarchy termId="${termId}" var="hierarchy"/>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = new Array();
    top.hierarchy[0] = {id:"rootDictNode"};
    <c:forEach items="${hierarchy}" var="item" varStatus="status">
      top.hierarchy[${status.index + 1}] = {id:"${item.id}", treeNodeType:"${item.nodeType}"}
    </c:forEach>
    top.syncTree();
  </script>
  <admin-validator:validate beanName="TermFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_edit.jsp#2 $$Change: 651448 $--%>
