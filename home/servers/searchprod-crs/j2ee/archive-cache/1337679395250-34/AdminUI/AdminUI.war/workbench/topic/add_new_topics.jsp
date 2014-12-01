
<%--
JSP allows to create new topics for particular topic set or another topic.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/add_new_topics.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="parentId" var="parentId"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TopicsFormHandler" var="topicsFormHandler"/>

  <%-- Trying to find a topic set by ID first. If impossible, parent is a topic. --%>
  <c:catch var="exception">
    <topic:topicSetFindByPrimaryKey topicSetId="${parentId}" var="parent"/>
    <c:set var="topicSetId" value="${parentId}"/>
  </c:catch>
  <c:if test="${not empty exception}">
    <topic:topicFindByPrimaryKey topicId="${parentId}" var="firstTopic"/>
    <c:forEach items="${firstTopic.ancestors}" begin="0" end="0" var="firstExistentTopic">
      <c:set var="firstTopic" value="${firstExistentTopic}"/>
    </c:forEach>
    <c:set var="parent" value="${firstTopic.parent}"/>
    <c:set var="parentTopicId" value="${parentId}"/>
  </c:if>

  <c:url value="${topicPath}/topic.jsp" var="successURL" />
  <c:url value="${topicPath}/add_new_topics.jsp" var="errorURL">
    <c:param name="parentId" value="${parentId}"/>
  </c:url>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}"  method="POST">
      <div id="paneContent">
        <p>
          <strong><fmt:message key="add_new_topics.create.new.topic"/></strong>
          <c:out value="${parent.name}" />
        </p>

        <fmt:message var="deleteTopicTooltip" key="add_new_topics.topic.delete.tooltip" />
        <table class="data simple" id="tableTopic" >
          <thead>
            <tr>
              <th><fmt:message key="add_new_topics.topic.name"/></th>
              <th><fmt:message key="add_new_topics.display.name"/></th>
              <th><fmt:message key="add_new_topics.description.name"/></th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="cursorTopicName" items="${topicsFormHandler.topicName}" varStatus="status">
                <c:set var="cursorTopicDisplayName" value="${topicsFormHandler.displayName[status.index]}"/>
                <c:set var="cursorTopicDescription" value="${topicsFormHandler.description[status.index]}"/>
                <tr>
                  <td>
                    <d:input type="text" style="width:95%" iclass="textField" name="topicName"
                      bean="TopicsFormHandler.topicName" value="${cursorTopicName}"
                      onkeyup="addEmptyField(this);" onchange="addEmptyField(this);" />
                  </td>
                  <td>
                    <d:input type="text" style="width:95%" iclass="textField" name="displayName"
                      bean="TopicsFormHandler.displayName" value="${cursorTopicDisplayName}"
                      onkeyup="addEmptyField(this);" onchange="addEmptyField(this);" />
                  </td>
                  <td>
                    <d:textarea rows="1" style="width:95%" iclass="textAreaField medium" name="description"
                      bean="TopicsFormHandler.description" default="${cursorTopicDescription}"
                      onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
                  </td>
                  <td>
                    <a class="icon propertyDelete" href="#" onclick="return deleteField(this);"
                      title="${deleteTopicTooltip}">del</a>
                  </td>
                </tr>
            </c:forEach>
          </tbody>
        </table>
        <script type="text/javascript">
          initTable(document.getElementById("tableTopic"));
        </script>
      </div>
      <div id="paneFooter">
        <fmt:message var="createButtonTooltip" key='add_new_topics.tooltip.create'/>
        <fmt:message var="cancelButtonTooltip" key='add_new_topics.tooltip.cancel'/>
        <fmt:message var="createButton"        key='add_new_topics.button.create'/>
        <fmt:message var="cancelButton"        key='add_new_topics.button.cancel'/>

        <d:input type="hidden" bean="TopicsFormHandler.successURL" value="${successURL}" name="successURL"/>
        <d:input type="hidden" bean="TopicsFormHandler.errorURL" value="${errorURL}" name="errorURL"/>
        <d:input type="hidden" bean="TopicsFormHandler.parentId" value="${parentId}" name="parentId"/>
        <d:input type="hidden" bean="TopicsFormHandler.parentTopicId" value="${parentTopicId}" name="parentTopicId"/>
        <d:input type="hidden" bean="TopicsFormHandler.topicSetId" value="${topicSetId}" name="topicSetId"/>

        <d:input type="submit" value="${createButton}" bean="TopicsFormHandler.createTopics" iclass="formsubmitter"
                 title="${createButtonTooltip}" onclick="return checkForm();" name="createTopics" />
        <%-- Cancel button --%>
        <d:input type="submit" value="${cancelButton}" bean="TopicsFormHandler.cancel" iclass="formsubmitter"
                 title="${cancelButtonTooltip}" />
      </div>
      <admin-validator:validate beanName="TopicsFormHandler"/>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/add_new_topics.jsp#2 $$Change: 651448 $--%>
