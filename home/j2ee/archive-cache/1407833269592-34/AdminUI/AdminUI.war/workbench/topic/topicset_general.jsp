<%--
  JSP, showing "general" tab of create or update topic set page. This page is included from topic_set.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetId"
                var="currentTopicSetId"/>
  <topic:topicSetFindByPrimaryKey topicSetId="${currentTopicSetId}" var="currentTopicSet"/>
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
          <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetDescription"
                   id="topicSetDescription" iclass="textField" type="text" name="topicSetDescription"/>
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
      <tr>
        <td class="label">
          <fmt:message key="edit_topic_set.pattern_macros"/>
        </td>
        <td>
          <c:url value="${topicPath}/global_macros.jsp" var="patterMacrosURL">
            <c:param name="topicSetId" value="${currentTopicSetId}" />
            <c:param name="macrosType" value="global" />
            <c:param name="topicId" value="${topicId}" />
          </c:url>
          <topic:globalMacroFindAll var="globalMacros"/>
          ${fn:length(globalMacros)}
          [<a href="${patterMacrosURL}" title="<fmt:message key='edit_topic_set.edit_macros.local.tooltip'/>" 
              onclick="return loadRightPanel(this.href);"><fmt:message key="edit_topic_set.edit_macros"/></a>]
        </td>
      </tr>
    </tbody>
  </table>
  <h3>
    <fmt:message key="edit_topic_set.top_level_topics">
      <fmt:param value="${fn:length(currentTopicSet.children)}" />
    </fmt:message>
  </h3>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetId"
                var="currentTopicSetId"/>
  <c:url value="${topicPath}/add_new_topics.jsp" var="addTopUrl">
    <c:param name="parentId" value="${currentTopicSetId}"/>
  </c:url>
  <p>
    <%-- Add new topic url --%>
    <input type="button" value="<fmt:message key='edit_topic_set.buttons.add_new_topics'/>"
           title="<fmt:message key='edit_topic_set.buttons.add_new_topics.tooltip'/>"
           onclick="return loadRightPanel('${addTopUrl}');"/>
    <%-- Drop-down list of all top-level topics of this topic set --%>
    <tags:selectLink defaultOptionKey="edit_topic_set.go_to_topic" items="${currentTopicSet.children}" var="currentTopic">
      <c:url value="${topicPath}/topic.jsp" var="viewTopicURL">
        <c:param name="topicId" value="${currentTopic.id}"/>
      </c:url>
      <option value="${viewTopicURL}"><c:out value="${currentTopic.name}"/></option>
    </tags:selectLink>
  </p>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_general.jsp#2 $$Change: 651448 $--%>
