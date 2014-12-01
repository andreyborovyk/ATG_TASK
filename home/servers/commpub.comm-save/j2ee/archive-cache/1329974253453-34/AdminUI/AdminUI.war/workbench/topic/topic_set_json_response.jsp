<%--
JSP, used to prepare json formatted messages for dojo tree client controller.
Used as success or error URL after method handler calling.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_set_json_response.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/Messages" var="messagesComponent"/>
  <d:getvalueof var="topicSetId" param="topicSetId"/>
  <d:getvalueof var="topicId" param="topicId"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="messages">
      <c:forEach items="${messagesComponent.messagesArray}" var="message">
        <admin-dojo:jsonObject>
          <admin-dojo:jsonValue name="type" value="${message.type}" />
          <admin-dojo:jsonValue name="message" value="${message.message}" />
        </admin-dojo:jsonObject>
      </c:forEach>
    </admin-dojo:jsonArray>
    <c:if test="${topicSetId != null}">
      <topic:topicSetFindByPrimaryKey topicSetId="${topicSetId}" var="topicSet"/>
      <admin-beans:getTopicSetIndex var="index" topicSet="${topicSet}"/>
      <topic:topicSetFindAll var="topics" />
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${topicSetId}"/>
        <admin-dojo:jsonValue name="name" value="${topicSet.name}"/>
        <admin-dojo:jsonValue name="description" value="${topicSet.description}"/>
        <admin-dojo:jsonValue name="seq" value="${index}"/>
      </admin-dojo:jsonObject>
    </c:if>
    <c:if test="${topicId != null}">
      <topic:topicFindByPrimaryKey topicId="${topicId}" var="topic"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${topicId}"/>
        <admin-dojo:jsonValue name="name" value="${topic.name}"/>
        <admin-dojo:jsonValue name="description" value="${topic.description}"/>
        <topic:topicGetTopicSequence topicId="${topicId}" var="seq" />
        <admin-dojo:jsonValue name="seq" value="${seq}"/>
        <admin-dojo:jsonValue name="parentId" value="${topic.parent.id}"/>
      </admin-dojo:jsonObject>
    </c:if>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_set_json_response.jsp#2 $$Change: 651448 $--%>
