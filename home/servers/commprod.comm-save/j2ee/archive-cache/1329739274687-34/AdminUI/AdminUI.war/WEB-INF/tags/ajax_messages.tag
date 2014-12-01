<%@ tag language="java" body-content="empty"%>

<%@ include file="/templates/top.tagf" %>
<c:if test="${empty messagesLoaded}">
  <c:set var="messagesLoaded" scope="request" value="true" />
  <admin-dojo:jsonObject forceOut="true">
    <d:getvalueof var="messages" bean="/atg/searchadmin/adminui/validator/Messages" />
    <c:set var="hasError" value="${false}" />
    <admin-dojo:jsonArray name="messages">
      <c:forEach var="message" items="${messages.messagesArray}">
        <admin-dojo:jsonObject>
          <c:if test="${message.type == 'error'}">
            <c:set var="hasError" value="${true}" />
          </c:if>
          <admin-dojo:jsonValue name="type" value="${message.type}"/>
          <admin-dojo:jsonValue name="message" value="${message.message}"/>
          <admin-dojo:jsonValue name="fieldName" value="${message.fieldName}"/>
          <c:if test="${not empty message.itemInfoBean}">
            <admin-dojo:jsonValue name="itemId" value="${message.itemInfoBean.id}"/>
            <admin-dojo:jsonValue name="operation" value="${message.itemInfoBean.operation}"/>
            <admin-dojo:jsonValue name="treeNodeType" value="${message.itemInfoBean.treeNodeType}"/>
            <admin-dojo:jsonValue name="itemName" value="${message.itemInfoBean.name}"/>
            <admin-dojo:jsonValue name="itemDescription" value="${message.itemInfoBean.description}"/>
            <admin-dojo:jsonValue name="index" value="${message.itemInfoBean.index}"/>
            <admin-dojo:jsonValue name="isFolder" value="${message.itemInfoBean.folder}"/>
            <admin-dojo:jsonValue name="parentId" value="${message.itemInfoBean.parentId}"/>
            <admin-dojo:jsonValue name="parentNodeType" value="${message.itemInfoBean.parentNodeType}"/>
          </c:if>
        </admin-dojo:jsonObject>
      </c:forEach>
    </admin-dojo:jsonArray>
    <admin-dojo:jsonObject name="title">
      <c:set var="label" value="${messages.handlerAlertLinkLabel}" />
      <c:if test="${label == null or hasError and not message.rewriteMessageLinkLabel}">
        <fmt:message var="label" key="error_message.unable_complete" />
      </c:if>
      <admin-dojo:jsonValue name="label" value="${label}"/>
  
      <c:set var="title" value="${messages.handlerMessageTitle}" />
      <c:if test="${empty messages.messagesArray}">
        <fmt:message var="title" key="error_title.errors.in.form" />
      </c:if>
      <c:if test="${title == null or hasError and not message.rewriteMessageTitle}">
        <fmt:message var="title" key="error_message.unable_complete" />
      </c:if>
      <admin-dojo:jsonValue name="title" value="${title}"/>
    </admin-dojo:jsonObject>
  </admin-dojo:jsonObject>
</c:if>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/ajax_messages.tag#2 $$Change: 651448 $--%>
