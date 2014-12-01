<%--
  JSP, used to create new topic set or to update existing. It includes topicset_navigation.jsp,
  topic_set_footer.jsp, topic_set_general pages.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- topic set id, used to prepopulate formhandler field in edit mode --%>
  <d:getvalueof param="topicSetId" var="topicSetId"/>

  <%-- URL definitions--%>
  <%-- add new topics url --%>
  <c:url value="${topicPath}/add_new_topics.jsp" var="addTopicURL"/>
  <%-- Success url, navigates to topic set page --%>
  <c:url value="${topicPath}/topicset.jsp" var="successURL">
    <c:param name="topicSetId" value="${topicSetId}"/>
  </c:url>
  <%-- Error URL --%>
  <c:url value="${topicPath}/topicset.jsp" var="errorURL">
    <c:param name="topicSetId" value="${topicSetId}"/>
  </c:url>

  <%-- Top navigation of the page --%>
  <d:include src="topicset_navigation.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="topicset.jsp">

      <%-- Prepopulating formhandler --%>
      <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler" var="handler"/>
      <admin-ui:initializeFormHandler handler="${handler}">
        <admin-ui:param name="topicSetId" value="${topicSetId}"/>
      </admin-ui:initializeFormHandler>

      <div id="paneContent">
        <div id="content1">
            <%-- General tab --%>
          <d:include src="topicset_general.jsp">
            <d:param name="mode" value="update"/>
          </d:include>
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
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetId"
               type="hidden" name="topicSetId"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.needInitialization"
               type="hidden" name="needInitialization" value="false"/>
      <div id="paneFooter">
        <fmt:message key="edit_topic_set.buttons.save" var="saveButton"/>
        <fmt:message key="edit_topic_set.buttons.save.tooltip" var="saveButtonTooltip"/>
          <%-- Save button --%>
        <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.update"
                 type="submit" iclass="formsubmitter" value="${saveButton}"
                 title="${saveButtonTooltip}" onclick="return checkForm()"/>
        <admin-validator:validate beanName="ManageTopicSetFormHandler"/>
      </div>
    </d:form>
  </div>
  <topic:topicSetFindByPrimaryKey topicSetId="${topicSetId}" var="topicSet" />
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTopicSetNode"}, {id:"<c:out value="${topicSet.id}"/>", treeNodeType:"<c:out value="${topicSet.nodeType}"/>"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset.jsp#2 $$Change: 651448 $--%>
