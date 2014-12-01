<%--
JSP, used to show and edit TPO options from TPO content level set.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_content_edit_set.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="tpoSetId" param="tpoSetId"/>

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tpo_edit_set.js"></script>

  <c:set var="handlerName" value="/atg/searchadmin/workbenchui/formhandlers/TPOSetContentFormHandler"/>
  <d:importbean var="handler" bean="${handlerName}"/>
  <d:getvalueof var="textProcessingOptions" bean="/atg/searchadmin/repository/service/TextProcessingOptionsService"/>

  <c:if test="${not empty tpoSetId}" >
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="tpoSetId" value="${tpoSetId}"/>
      <admin-ui:param name="tpoSetLevel" value="content"/>
    </admin-ui:initializeFormHandler>
  </c:if>
  <c:if test="${empty tpoSetId}" >
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="tpoSetId" value=""/>
      <admin-ui:param name="tpoSetLevel" value="content"/>
    </admin-ui:initializeFormHandler>
  </c:if>

  <c:url value="${tpoPath}/tpo_content_edit_set.jsp" var="backURL"/>  
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
          <fmt:message key="tpo_set.edit.content.used"/>
        </a>
      </li>
    </ul>
  </div>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">

    <d:form action="${backURL}" method="POST">
      <div id="paneContent">
        <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                      var="activeProjectId"/>
        <table id="_options_table" class="form tpoTable">
          <%-- TPO set general info --%>
          <c:if test="${activeProjectId != null}">
            <c:set var="projectActive" value="true"/>
            <d:getvalueof bean="TPOSetContentFormHandler.indexId" var="tpoSetIndexLevelId"/>
            <c:if test="${tpoSetIndexLevelId != null && not empty tpoSetIndexLevelId}">
              <tr id="_${textProcessingOptions.basicCategoryName} _combined" class="linepadded">
                <td class="label">
                  <fmt:message key="tpo_set.edit.index.name"/>
                </td>
                <td>
                  <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${tpoSetIndexLevelId}"
                                                                var="tpoSet"/>
                  <c:out value="${tpoSet.name}"/>
                </td>
              </tr>
              <script type="text/javascript">
                registerTPO('_${textProcessingOptions.basicCategoryName} _combined');
              </script>
            </c:if>
          </c:if>
          <tr id="_${textProcessingOptions.basicCategoryName} _content" class="linepadded">
            <td class="label">
              <span id="TPOSetNameAlert"><span class="required"><fmt:message key="project_general.required_field"/></span>
              </span>
              <fmt:message key="tpo_set.edit.content.name"/>
            </td>
            <td>
              <d:input type="text" iclass="textField" bean="TPOSetContentFormHandler.TPOSetName" name="TPOSetName"/>
            </td>
          </tr>
          <script type="text/javascript">
             registerTPO('_${textProcessingOptions.basicCategoryName} _content');
          </script>
          <%-- TPO option fields --%>
          <c:forEach items="${textProcessingOptions.options}" var="optionDef" varStatus="cursor">
            <c:if test="${optionDef.category != textProcessingOptions.queryCategoryName && (optionDef.contentLevel || tpoSetIndexLevelId != null)}">
              <c:choose>
                <c:when test="${tpoSetIndexLevelId != null}">
                  <d:include page="options/TPO_combined.jsp">
                    <d:param name="handlerName" value="${handlerName}"/>
                    <d:param name="handler" value="${handler}"/>
                    <d:param name="optionDef" value="${optionDef}"/>
                    <d:param name="indexTPOSetName" value="${tpoSet.name}"/>
                    <d:param name="tpoSetId" value="${tpoSetId}"/>
                    <d:param name="id" value="options_pane_${optionDef.category}_${cursor.index}"/>
                  </d:include>
                </c:when>  
                <c:otherwise>
                  <d:include page="options/TPO_content.jsp">
                    <d:param name="handlerName" value="${handlerName}"/>
                    <d:param name="optionDef" value="${optionDef}"/>
                    <d:param name="handler" value="${handler}"/>
                    <d:param name="tpoSetId" value="${tpoSetId}"/>
                    <d:param name="id" value="options_pane_${optionDef.category}_${cursor.index}"/>
                  </d:include>
                </c:otherwise>
              </c:choose>
            </c:if>
          </c:forEach>
        </table>
        <%-- Used In tab page --%>
        <div id="usedIn _content" style="display:none">
          <d:include page="tpo_set_content_sets.jsp"/>
        </div>
        <script type="text/javascript">
          registerTPO('usedIn _content');
        </script>
      </div>

      <div id="paneFooter">
        <d:input type="hidden" bean="TPOSetContentFormHandler.needInitialization" value="false" name="needInitialization"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.level" name="level"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.projectActive" value="${projectActive}" name="projectActive"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.indexId" name="indexId"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.contentId" name="contentId"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.successURL" value="${backURL}" name="successURL"/>
        <d:input type="hidden" bean="TPOSetContentFormHandler.errorURL" value="${backURL}" name="errorURL"/>

        <fmt:message key="tpo_set.edit.button.save" var="saveButtonTitle"/>
        <fmt:message key="tpo_set.edit.button.save" var="saveButtonToolTip"/>
        <d:input type="submit" bean="TPOSetContentFormHandler.save" name="save" onclick="syncTPOViews(); return checkForm();"
                 value="${saveButtonTitle}" title="${saveButtonToolTip}" iclass="formsubmitter"/>
      </div>
      <admin-validator:validate beanName="TPOSetContentFormHandler"/>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTpoNode"}, {id:"contentTpoNode"}, {id:"<c:out value="${tpoSetId}"/>", treeNodeType:"content"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_content_edit_set.jsp#2 $$Change: 651448 $--%>
