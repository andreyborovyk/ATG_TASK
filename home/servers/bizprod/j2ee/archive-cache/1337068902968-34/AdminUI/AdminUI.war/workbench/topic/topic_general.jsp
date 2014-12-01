<%--
  General tab on edit topic page. This page is included into topic.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler.topicId"
                var="currentTopicId"/>
  <topic:topicFindByPrimaryKey topicId="${currentTopicId}" var="currentTopic"/>

  <br/>
  <h3>
    <fmt:message key="topic_general.topic_basics"/>
  </h3>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <label for="topicName">
            <span id="topicNameAlert"></span><fmt:message key="topic_general.form.name"/>
          </label>
        </td>
        <td>
            <%-- Topic name --%>
          <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler.topicName"
                   type="text"
                   id="topicName" iclass="textField"
                   name="topicName"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="topic_general.form.full_path"/>
        </td>
        <td>
          <topic:topicGetTopicPath topicId="${currentTopicId}" var="fullTopicPath"/>
          <c:out value="${fullTopicPath}"/>
        </td>
      </tr>

      <tr>
        <td class="label">
          <label for="topicDisplayName">
            <span id="topicDisplayNameAlert"></span><fmt:message key="topic_general.form.display_name"/>
          </label>
        </td>
        <td>
            <%-- Topic set description --%>
          <d:input type="text"
                   bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler.topicDisplayName"
                   id="topicDisplayName" iclass="textField"
                   name="topicDisplayName"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <label for="topicDescription">
            <span id="topicDescriptionAlert"></span><fmt:message key="topic_general.form.description"/>
          </label>
        </td>
        <td>
            <%-- Topic set description --%>
          <d:input type="text"
                   bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler.topicDescription"
                   id="topicDescription" iclass="textField"
                   name="topicDescription"/>
        </td>
      </tr>
    </tbody>
  </table>
  <h3>
    <fmt:message key="topic_general.child_topics">
      <fmt:param value="${fn:length(currentTopic.children)}" />
    </fmt:message>
  </h3>

  <c:url value="${topicPath}/add_new_topics.jsp" var="addTopUrl">
    <c:param name="parentId" value="${currentTopicId}"/>
  </c:url>
  <p>
    <tags:buttonLink titleKey="topic_general.new_topics_button" tooltipKey="topic_general.new_topics_button.tooltip" href="${addTopUrl}" />
    <tags:selectLink defaultOptionKey="topic_general.go_to_topic" items="${currentTopic.children}" var="currentChildTopic">
      <c:url value="${topicPath}/topic.jsp" var="viewTopicURL">
        <c:param name="topicId" value="${currentChildTopic.id}"/>
      </c:url>
      <option value="${viewTopicURL}"><c:out value="${currentChildTopic.name}"/></option>
    </tags:selectLink>
  </p>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_general.jsp#2 $$Change: 651448 $--%>
