<%--
JSP, used to prepare json formatted messages for dojo tree client controller.
Used as success or error URL after method handler calling.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/weight_json_response.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/Messages" var="messagesComponent"/>
  <d:getvalueof var="termWeightId" param="termWeightId"/>
  <d:getvalueof var="termWeightSetId" param="termWeightSetId"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="messages">
      <c:forEach items="${messagesComponent.messagesArray}" var="message">
        <admin-dojo:jsonObject>
          <admin-dojo:jsonValue name="type" value="${message.type}"/>
          <admin-dojo:jsonValue name="message" value="${message.message}"/>
        </admin-dojo:jsonObject>
      </c:forEach>
    </admin-dojo:jsonArray>
    <c:if test="${termWeightSetId != null}">
      <termweights:termWeightSetFindByPrimaryKey termWeightSetId="${termWeightSetId}" var="set"/>
      <admin-beans:getTermWeightSetIndex var="index" termWeightSet="${set}"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${termWeightSetId}"/>
        <admin-dojo:jsonValue name="name" value="${set.name}"/>
        <admin-dojo:jsonValue name="description" value="${set.description}"/>
        <admin-dojo:jsonValue name="seq" value="${index}"/>
      </admin-dojo:jsonObject>
    </c:if>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/weight_json_response.jsp#2 $$Change: 651448 $--%>
