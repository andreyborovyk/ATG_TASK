<%--
  Browse SAO Sets JSP

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_sets_browse.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TPOSetSaoFormHandler"/>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" var="activeProjectId"/>

  <c:url value="${dictionaryPath}/sao/sao_sets_browse.jsp" var="successURL"/>
  
  <fmt:message key="sao_browse.title" var="saoTitle"/>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">

      <p>
        <c:if test="${not empty activeProjectId}">
          <c:url value="${dictionaryPath}/sao/sao_set_edit.jsp" var="newSaoSetURL"/>
          <input type="button" onclick="return loadRightPanel('${newSaoSetURL}');"
            value="<fmt:message key='sao_browse.new.button'/>" title="<fmt:message key='sao_browse.new.tooltip'/>" />
          <span class="ea"><tags:ea key="embedded_assistant.sao_sets_browse" /></span>
        </c:if>
      </p>

      <%--Set TPO bundle--%>
      <admin-beans:setTPOResourceBundle var="bundle" useSAO="true" />
      <admin-beans:getSAOSetsByActiveProject var="saoSets"/>

      <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                      var="saoSet" items="${saoSets}">
        <admin-ui:column title="sao_browse.table.option_set_name" type="static" name="name">
          <c:set var="name" value="${saoSet.name}"/>
          <c:if test="${empty saoSet.projectId}">
            <fmt:message key="sao_browse.tooltip.default" var="saoSetTooltip"/>
            <c:url var="saoDetailsUrl" value="${dictionaryPath}/sao/sao_set_details.jsp">
              <c:param name="saoSetId" value="${saoSet.id}"/>
            </c:url>
            <a href="${saoDetailsUrl}" title="${saoSetTooltip}" onclick="return loadRightPanel(this.href);">
              <c:out value="${name}"/>
            </a>
          </c:if>
          <c:if test="${not empty saoSet.projectId}">
            <fmt:message key="sao_browse.tooltip" var="saoSetTooltip"/>
            <c:url var="saoEditUrl" value="${dictionaryPath}/sao/sao_set_edit.jsp">
              <c:param name="saoSetId" value="${saoSet.id}"/>
            </c:url>
            <a href="${saoEditUrl}" title="${saoSetTooltip}" onclick="return loadRightPanel(this.href);">
              <c:out value="${name}"/>
            </a>
          </c:if>
        </admin-ui:column>
        <admin-ui:column title="sao_browse.table.search_language" type="static" name="searchLanguage">
          <fmt:message key="sao_browse.table.search_language.tooltip" var="projectTooltip"/>
          <fmt:message key="language.values.${saoSet.options.language.values[0]}" bundle="${bundle}"/>
        </admin-ui:column>
        <admin-ui:column title="sao_browse.table.result_languages" type="static" name="resultLanguages">
          <fmt:message key="sao_browse.table.result_languages.tooltip" var="projectTooltip"/>
          <fmt:message key="targetLanguage.values.${saoSet.options.targetLanguage.values[0]}" bundle="${bundle}"/>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <fmt:message key="sao_browse.table.copy.tooltip" var="copyTitle"/>
          <c:set var="quotedEscapedName">"${adminfunctions:escapeJsString(name)}"</c:set>
          <a class="icon propertyCopy" title="${copyTitle}" href="#"
             onclick="submitForm('${saoSet.id}', ${fn:escapeXml(quotedEscapedName)});">copy</a>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <c:if test="${not empty saoSet.projectId}">
            <fmt:message key="sao_browse.table.delete.tooltip" var="deleteTitle"/>
            <c:url value="/workbench/tpo/tpo_set_delete_popup.jsp" var="popUrl">
              <c:param name="tpoSetId" value="${saoSet.id}"/>
              <c:param name="level" value="sao"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </c:if>
        </admin-ui:column>
      </admin-ui:table>

      <d:form method="POST" action="sao_sets_browse.jsp">
        <d:input bean="TPOSetSaoFormHandler.copy" value="field mode" type="submit" iclass="formsubmitter"
                 name="copyInput" id="copyInput" style="display:none"/>
        <d:input bean="TPOSetSaoFormHandler.projectId" type="hidden" name="projectId" id="projectId" value="${activeProjectId}"/>
        <d:input bean="TPOSetSaoFormHandler.TPOSetId" type="hidden" name="TPOSetId" id="TPOSetId"/>
        <d:input bean="TPOSetSaoFormHandler.TPOSetName" type="hidden" name="TPOSetName" id="TPOSetName"/>
        <d:input bean="TPOSetSaoFormHandler.successURL" value="${successURL}" type="hidden" name="successURL"
                 id="successURL"/>
        <d:input bean="TPOSetSaoFormHandler.sort" value="field mode" type="submit"
                 id="sortInput" style="display:none" iclass="formsubmitter"/>
      </d:form>
    </div>
  </div>
  <script type="text/javascript">
    function submitForm(id, name) {
      document.getElementById('TPOSetId').value = id;
      document.getElementById('TPOSetName').value = name;
      document.getElementById('copyInput').click();
    }
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictInspectNode"}, {id:"saoSetsNode"}];
    top.syncTree();
    function refreshRightPane(obj) {
      loadRightPanel('${successURL}');
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_sets_browse.jsp#2 $$Change: 651448 $--%>
