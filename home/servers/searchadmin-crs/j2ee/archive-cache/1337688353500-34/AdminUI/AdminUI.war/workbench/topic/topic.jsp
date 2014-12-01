<%--
  JSP, used to manage topic. It includes navigation (topic_nagivation.jsp), that contain navigation tabs for this page,
  page, that display general tab content (topic_general), etc..

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Id of topic, used to initialize formhandler fields --%>
  <d:getvalueof param="topicId" var="topicId"/>
  <d:getvalueof param="tab" var="tab"/>
  <c:if test="${empty tab}">
    <c:set var="tab" value="1"/>
  </c:if>

  <%-- form handler initialization --%>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="topicId" value="${topicId}"/>
  </admin-ui:initializeFormHandler>

  <script type="text/javascript">
    // This function is used by script on the topic_navigation.jsp page
    function changeTab(tabNum) {
      document.getElementById("tab").value=tabNum;
    }
  </script>

  <%-- Navigation tabs --%>
  <d:include page="topic_navigation.jsp">
    <d:param name="topicId" value="${topicId}"/>
    <d:param name="tab" value="${tab}"/>
  </d:include>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST" >
      <div id="paneContent">
        <%-- General tab section --%>
        <div id="content1" <c:if test="${tab == '2'}">style="display:none"</c:if>>
          <d:include page="topic_general.jsp"/>
        </div>

        <%-- Rules tab --%>
        <div id="content2" <c:if test="${tab == '1'}">style="display:none"</c:if>>
          <d:include page="topic_rules.jsp"/>
        </div>
      </div>

      <%-- URL definitions--%>
      <c:url value="${topicPath}/topic.jsp" var="successURL">
        <c:param name="topicId" value="${topicId}"/>
      </c:url>
      <d:getvalueof bean="ManageTopicFormHandler.nextTopicId" var="nextTopicId"/>
      <c:url value="${topicPath}/topic.jsp" var="nextTopicURL">
        <c:param name="topicId" value="${nextTopicId}"/>
      </c:url>

      <d:input bean="ManageTopicFormHandler.successURL" type="hidden" value="${successURL}" name="successURL" />
      <d:input bean="ManageTopicFormHandler.successAlternativeURL" type="hidden" value="${nextTopicURL}" />
      <d:input bean="ManageTopicFormHandler.topicId" type="hidden" name="topicId"/>
      <d:input bean="ManageTopicFormHandler.needInitialization" type="hidden" name="needInitialization" value="false"/>
      <d:input bean="ManageTopicFormHandler.nextTopicId" type="hidden" name="nextTopicId"/>
      <d:input bean="ManageTopicFormHandler.tab" value="${tab}" type="hidden" name="tab" id="tab"/>

      <div id="paneFooter">
        <%-- Save button --%>
        <fmt:message key="topic_footer.save_button" var="saveButtonValue"/>
        <fmt:message key="topic_footer.save_button.tooltip" var="saveButtonTitle"/>
        <d:input bean="ManageTopicFormHandler.update" value="${saveButtonValue}" title="${saveButtonTitle}"
                 iclass="formsubmitter" type="submit" id="saveTopicButton" onclick="return checkForm()"/>

        <%-- Save and Edit Next button --%>
        <fmt:message key="topic_footer.save_and_edit_next_button" var="saveAndEditButtonValue"/>
        <fmt:message key="topic_footer.save_and_edit_next_button.tooltip" var="saveAndEditButtonTitle"/>
        <d:input bean="ManageTopicFormHandler.updateEditNext" disabled="${empty nextTopicId}"
                 iclass="formsubmitter" type="submit" value="${saveAndEditButtonValue}"
                 title="${saveAndEditButtonTitle}" />
      </div>

      <admin-validator:validate beanName="ManageTopicFormHandler" />
    </d:form>
  </div>
  <topic:topicGetAncestorsIncludingTopicSet topicId="${topicId}" var="hierarchy"/>
  <topic:topicFindByPrimaryKey topicId="${topicId}" var="topic"/>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = new Array({id:"rootTopicSetNode"},
    <c:forEach items="${hierarchy}" var="item">
      {id:"${item.id}", treeNodeType:"${item.nodeType}"},
    </c:forEach>
      {id:"${topic.id}", treeNodeType:"${topic.nodeType}"});
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic.jsp#2 $$Change: 651448 $--%>
