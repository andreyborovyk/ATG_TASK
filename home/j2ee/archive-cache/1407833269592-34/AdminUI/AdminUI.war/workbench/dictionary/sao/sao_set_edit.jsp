<%--
JSP, used to edit SAO options from Search Analysis Option set.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_set_edit.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="saoSetId" param="saoSetId"/>

  <c:set var="handlerName" value="/atg/searchadmin/workbenchui/formhandlers/TPOSetSaoFormHandler"/>
  <d:importbean var="handler" bean="${handlerName}"/>
  <d:getvalueof var="searchAnalysisOptions" bean="/atg/searchadmin/repository/service/SearchAnalysisOptionsService"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="tpoSetId" value="${saoSetId}"/>
    <admin-ui:param name="tpoSetLevel" value="sao"/>
  </admin-ui:initializeFormHandler>

  <d:getvalueof bean="TPOSetSaoFormHandler.saoOptions" var="options"/>
  <c:url value="${dictionaryPath}/sao/sao_sets_browse.jsp" var="successURL"/>
  <c:url value="${dictionaryPath}/sao/sao_set_edit.jsp" var="backURL">
    <c:if test="${not empty saoSetId}">
      <c:param name="saoSetId" value="${saoSetId}"/>
    </c:if>
  </c:url>
  <%--Set TPO bundle--%>
  <admin-beans:setTPOResourceBundle var="bundle" useSAO="true" />

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
        executeScripts="true" cacheContent="false" scriptSeparation="false">

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tpo_edit_set.js"></script>

    <d:form action="${successURL}" method="POST">
      <div id="paneContent">
        <p></p>
        <table class="form" cellpadding="0" cellspacing="0">
          <tbody>
          <%-- SAO set general info --%>
          <tr>
            <td class="label">
              <span id="TPOSetNameAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
              <fmt:message key="sao_set.edit.name"/>
            </td>
            <td>
              <d:input type="text" iclass="textField" bean="TPOSetSaoFormHandler.TPOSetName" name="TPOSetName"/>
              <span class="ea"><tags:ea key="embedded_assistant.sao_set_edit" /></span>
            </td>
          </tr>

          <%-- TPO option fields --%>
          <c:set var="isSearchParams" value="true"/>
          <c:forEach items="${options}" var="optionDef" varStatus="cursor">
            <c:if test="${optionDef.category==searchAnalysisOptions.searchParamsCategoryName && isSearchParams}">
              <tr class="_${searchAnalysisOptions.searchParamsCategoryName} _sao" style="display:none">
                <td class="label">
                  <h2><fmt:message key="sao_set.search_parameters.label"/></h2>
                </td>
                <td&nbsp;</td>
             </tr>
              <c:set var="isSearchParams" value="false"/>
            </c:if>
            <tr id="options_pane_${cursor.index}" class="_${optionDef.category} _sao"
              <c:if test="${optionDef.category != searchAnalysisOptions.basicCategoryName}">style="display:none"</c:if>
            >
              <td class="label">
                <span id="<c:out value='options.${optionDef.name}.indexValues' />Alert"></span>
                <fmt:message key="${optionDef.name}.label" bundle="${bundle}" />
              </td>
              <td>
                <d:include page="/workbench/tpo/options/${optionDef.display}.jsp">
                  <d:param name="handlerName" value="${handlerName}" />
                  <d:param name="handler" value="${handler}" />
                  <d:param name="optionDef" value="${optionDef}" />
                  <d:param name="valuesProp" value="indexValues" />
                  <d:param name="bundle" value="${bundle}" />
                </d:include>
                <span class="ea"><tags:ea key="embedded_assistant.sao.${optionDef.name}" /></span>
              </td>
            </tr>
          </c:forEach>
          <tr>
            <td class="label"></td>
            <td>
              <span id="showFewerOptions" style="display:none">
                <a href="#" onclick="return showOptions(false, '<c:out value='_${searchAnalysisOptions.basicCategoryName} _sao'/>');"
                  title="<fmt:message key='sao_set.edit.link.show_fewer.tooltip'/>">
                  <fmt:message key="sao_set.edit.link.show_fewer"/>
                </a>
              </span>
              <span id="showMoreOptions">
                <a href="#" onclick="return showOptions(true, '<c:out value='_${searchAnalysisOptions.basicCategoryName} _sao'/>');"
                  title="<fmt:message key='sao_set.edit.link.show_more.tooltip'/>">
                  <fmt:message key="sao_set.edit.link.show_more"/>
                </a>
              </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                   var="activeProjectId"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.projectId" value="${activeProjectId}" name="projectId"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.needInitialization" value="false" name="needInitialization"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.level" name="level"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.saoId" name="saoId"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.default" value="${handler.default}" name="default"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.successURL" value="${successURL}" name="successURL"/>
        <d:input type="hidden" bean="TPOSetSaoFormHandler.errorURL" value="${backURL}" name="errorURL"/>

        <fmt:message key="tpo_set.edit.button.save" var="saveButtonTitle"/>
        <fmt:message key="tpo_set.edit.button.save" var="saveButtonToolTip"/>
        <d:input type="submit" bean="TPOSetSaoFormHandler.save" name="save" onclick="syncTPOViews(); return checkForm();"
                 value="${saveButtonTitle}" title="${saveButtonToolTip}" iclass="formsubmitter"/>
      </div>
      <admin-validator:validate beanName="TPOSetSaoFormHandler"/>
    </d:form>
  </div>
  <script type="text/javascript">
    function showOptions(show, className) {
      var divs = document.getElementsByTagName("tr");
      for (var i = 0; i < divs.length; i++){
        var parts = divs[i].className.split('_');
        var postfix = parts[parts.length - 1]; 
        if ((postfix == "sao") && (divs[i].className != className)){
          divs[i].style.display = show ? "" : "none";
        }
      }
      document.getElementById("showFewerOptions").style.display = show ? "" : "none";
      document.getElementById("showMoreOptions").style.display = show ? "none" : "";
    }
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictInspectNode"}, {id:"saoSetsNode"}, {id:"<c:out value="${saoSetId}"/>", treeNodeType:"sao"}];
    top.syncTree();
    function refreshRightPane(obj) {
      loadRightPanel('${backURL}');
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_set_edit.jsp#2 $$Change: 651448 $--%>
