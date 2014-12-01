<%--
  JSP, used to create new topic set.
  It includes topicset_navigation.jsp and topicset_languages.jsp pages.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_new.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- search project id. it is not empty is we came from manage project customizations page --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <c:if test="${not empty projectId}">
    <d:setvalue bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.initProjectId" value="${projectId}"/>
  </c:if>

  <%-- url definitions --%>
  <%-- add new topics url --%>
  <c:url value="${topicPath}/add_new_topics.jsp" var="addTopicURL"/>
  <script type="text/javascript">
    function callCreateMethod(hiddenInput) {
      var successURLHidden = document.getElementById(hiddenInput);
      successURLHidden.value = "${addTopicURL}";
    }
  </script>
  <%-- Success url, navigates to topic set page --%>
  <c:url value="${topicPath}/topicset.jsp" var="successURL"/>
  <%-- Error URL --%>
  <c:url value="${topicPath}/topicset_new.jsp" var="errorURL"/>

  <%-- Top navigation of the page --%>
  <d:include src="topicset_navigation.jsp"/>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="topicset.jsp">
      <div id="paneContent">
        <div id="content1">
          <%-- General tab --%>
          <br/>
          <h3>
            <fmt:message key="new_topic_set.head"/>
          </h3>
          <table class="form" cellspacing="0" cellpadding="0">
            <tbody>
              <tr>
                <td class="label">
                  <span id="topicSetNameAlert">
                    <span class="required"><fmt:message key="project_general.required_field"/></span>
                  </span>
                  <label for="topicSetName">
                    <fmt:message key="new_topic_set.form.name"/>
                  </label>
                </td>
                <td>
                  <%-- Topic set name --%>
                  <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetName"
                           type="text" id="topicSetName" iclass="textField" name="topicSetName"/>
                </td>
              </tr>
              <tr>
                <td class="label">
                  <label for="topicSetDescription">
                    <span id="topicSetDescriptionAlert"></span><fmt:message key="new_topic_set.form.description"/>
                  </label>
                </td>
                <td>
                  <%-- Topic set description --%>
                  <d:input type="text"
                           bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetDescription"
                           id="topicSetDescription" iclass="textField" name="topicSetDescription"/>
                </td>
              </tr>
              <tr>
                <td class="label">
                  <label for="language">
                    <fmt:message key="new_topic_set.form.language"/>
                  </label>
                </td>
                <td>
                  <d:include page="topicset_languages.jsp"/>
                  <span class="ea"><tags:ea key="embedded_assistant.topicset_general.language" /></span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div id="content2" style="display:none">
            <%-- Used in search projects tab --%>
          <d:include src="topicset_search_projects.jsp"/>
        </div>
      </div>

      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.successURL"
               type="hidden" value="${successURL}" name="successURL" id="successURLInputHidden"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.errorURL"
               type="hidden" value="${errorURL}" name="errorURL"/>

      <div id="paneFooter">
        <fmt:message key="new_topic_set.buttons.create" var="createButton"/>
        <fmt:message key="new_topic_set.buttons.create.tooltip" var="createButtonTooltip"/>
        <fmt:message key="new_topic_set.buttons.create_and_add" var="createAndAddButton"/>
        <fmt:message key="new_topic_set.buttons.create_and_add.tooltip" var="createAndAddButtonTooltip"/>
        <fmt:message key="new_topic_set.buttons.cancel" var="cancelButton"/>
        <fmt:message key="new_topic_set.buttons.cancel.tooltip" var="cancelButtonTooltip"/>

        <%-- Create button --%>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.create" value="${createButton}"
                 title="${createButtonTooltip}" iclass="formsubmitter" type="submit" onclick="return checkForm()"/>
        <%-- Create and add button --%>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.createAndAddNew" value="${createAndAddButton}"
                 title="${createAndAddButtonTooltip}" iclass="formsubmitter" type="submit"
                 onclick="callCreateMethod('successURLInputHidden');return checkForm()"/>

        <%-- Cancel button --%>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.cancel"
                 value="${cancelButton}" title="${cancelButtonTooltip}" iclass="formsubmitter" type="submit" />

        <admin-validator:validate beanName="ManageTopicSetFormHandler"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_new.jsp#2 $$Change: 651448 $--%>
