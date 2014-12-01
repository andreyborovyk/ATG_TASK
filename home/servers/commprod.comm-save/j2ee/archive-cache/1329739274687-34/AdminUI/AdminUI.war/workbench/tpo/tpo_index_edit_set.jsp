<%--
JSP, used to show and edit TPO options from TPO index level set.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_index_edit_set.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="tpoSetId" param="tpoSetId"/>

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tpo_edit_set.js"></script>
  
  <c:set var="handlerName" value="/atg/searchadmin/workbenchui/formhandlers/TPOSetIndexFormHandler"/>
  <d:importbean var="handler" bean="${handlerName}"/>
  <d:getvalueof var="textProcessingOptions" bean="/atg/searchadmin/repository/service/TextProcessingOptionsService"/>

  <c:if test="${empty tpoSetId}" >
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="tpoSetId" value=""/>
      <admin-ui:param name="tpoSetLevel" value="index"/>
    </admin-ui:initializeFormHandler>
  </c:if>
  <c:if test="${not empty tpoSetId}" >
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="tpoSetId" value="${tpoSetId}"/>
      <admin-ui:param name="tpoSetLevel" value="index"/>
    </admin-ui:initializeFormHandler>
  </c:if>

  <c:url value="${tpoPath}/tpo_index_edit_set.jsp" var="backURL"/>   

  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <c:forEach items="${textProcessingOptions.categories}" var="category">
        <c:if test="${category != textProcessingOptions.queryCategoryName}">
          <li id="_${category}"
              <c:if test="${category == textProcessingOptions.basicCategoryName}">class="current"</c:if>
              >
            <a href="#" onclick="return switchMultiDivContent('${category}', true);">
              <fmt:message key="tpo_set.option.category.${category}"/>
            </a>
          </li>
        </c:if>
      </c:forEach>
      <li id="usedIn">
        <a href="#" onclick="return switchMultiDivContent('usedIn', false);">
          <fmt:message key="tpo_set.edit.index.used"/>
        </a>
      </li>
    </ul>
  </div>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${backURL}" method="POST">
      <div id="paneContent">
        <table id="_options_table" class="form tpoTable">
          <%-- TPO set general info --%>
          <tr id="_${textProcessingOptions.basicCategoryName} _index" class="linepadded">
            <td class="label">
              <span id="TPOSetNameAlert"><span class="required"><fmt:message
                  key="project_general.required_field"/></span>
              </span>
              <fmt:message key="tpo_set.edit.index.name"/>
            </td>
            <td>
              <d:input type="text" iclass="textField" bean="TPOSetIndexFormHandler.TPOSetName" name="TPOSetName"/>
            </td>
          </tr>
          <script type="text/javascript">
            registerTPO('_${textProcessingOptions.basicCategoryName} _index');
          </script>
          <%-- TPO option fields --%>
          <c:forEach items="${textProcessingOptions.options}" var="optionDef" varStatus="cursor">
            <c:if test="${optionDef.category != textProcessingOptions.queryCategoryName}">
              <d:include page="options/TPO_index.jsp">
                <d:param name="handlerName" value="${handlerName}"/>
                <d:param name="handler" value="${handler}"/>
                <d:param name="tpoSetId" value="${tpoSetId}"/>
                <d:param name="optionDef" value="${optionDef}"/>
                <d:param name="id" value="options_pane_${optionDef.category}_${cursor.index}"/>
              </d:include>
            </c:if>
          </c:forEach>
        </table>
          <%-- Used In tab page --%>
        <div id="usedIn _index" style="display:none">
          <d:include page="tpo_set_search_projects.jsp"/>
        </div>
        <script type="text/javascript">
          registerTPO('usedIn _index');
        </script>
      </div>

      <d:input type="submit" bean="TPOSetIndexFormHandler.restoreToDefaults" name="restoreToDefaults" 
               id="_restoreToDefaults_submit" style="display:none;" iclass="formsubmitter"/>

      <div id="paneFooter">
        <d:input type="hidden" bean="TPOSetIndexFormHandler.needInitialization" value="false" name="needInitialization"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.level" name="level"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.indexId" name="indexId"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.default" value="${handler.default}" name="default"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.successURL" value="${backURL}" name="successURL"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.cancelURL" value="${backURL}" name="cancelURL"/>
        <d:input type="hidden" bean="TPOSetIndexFormHandler.errorURL" value="${backURL}" name="errorURL"/>

        <fmt:message key="tpo_set.edit.button.restore_defaults" var="restoreButtonTitle"/>
        <fmt:message key="tpo_set.edit.button.restore_defaults" var="restoreButtonToolTip"/>
        <input type="button" onclick="restoreDefaultsConfirm();"
               value="${restoreButtonTitle}" title="${restoreButtonToolTip}" class="restoreToDefaults"/>

        <fmt:message key="tpo_set.edit.button.save" var="saveButtonTitle"/>
        <fmt:message key="tpo_set.edit.button.save" var="saveButtonToolTip"/>
        <d:input type="submit" bean="TPOSetIndexFormHandler.save" name="save" onclick="syncTPOViews(); return checkForm();"
                 value="${saveButtonTitle}" title="${saveButtonToolTip}" iclass="formsubmitter"/>
      </div>
      <admin-validator:validate beanName="TPOSetIndexFormHandler"/>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    function restoreDefaultsConfirm(){
      if (confirm('<fmt:message key="tpo_set.edit.button.restore_defaults.message"/>')){
        document.getElementById('_restoreToDefaults_submit').click();
      }
    }
    top.hierarchy = [{id:"rootTpoNode"}, {id:"indexTpoNode"}, {id:"<c:out value="${tpoSetId}"/>", treeNodeType:"index"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_index_edit_set.jsp#2 $$Change: 651448 $--%>
