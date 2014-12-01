<%--
JSP, used to prepare json formatted messages for dojo tree client controller.
Used as success or error URL after method handler calling by dojo.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/qr_json_response.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/Messages" var="messagesComponent"/>
  <d:getvalueof var="querySetId" param="querySetId"/>
  <d:getvalueof var="queryGroupId" param="queryGroupId"/>
  <d:getvalueof var="queryRuleId" param="queryRuleId"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="messages">
      <c:forEach items="${messagesComponent.messagesArray}" var="message">
        <admin-dojo:jsonObject>
          <admin-dojo:jsonValue name="type" value="${message.type}" />
          <admin-dojo:jsonValue name="message" value="${message.message}" />
        </admin-dojo:jsonObject>
      </c:forEach>
    </admin-dojo:jsonArray>
    <c:if test="${querySetId != null}">
      <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${querySetId}" var="querySet"/>
      <admin-beans:getQuerySetIndex var="index" queryRuleSet="${querySet}"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${querySetId}"/>
        <admin-dojo:jsonValue name="name" value="${querySet.name}"/>
        <admin-dojo:jsonValue name="description" value="${querySet.description}"/>
        <admin-dojo:jsonValue name="seq" value="${index}"/>
      </admin-dojo:jsonObject>
    </c:if>
    <c:if test="${queryGroupId != null}">
      <queryrule:queryRuleGroupFindByPrimaryKey queryRuleGroupId="${queryGroupId}" var="queryGroup"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${queryGroupId}"/>
        <admin-dojo:jsonValue name="name" value="${queryGroup.name}"/>
        <admin-dojo:jsonValue name="description" value="${queryGroup.name}"/>
        <admin-dojo:jsonValue name="seq" value="${queryGroup.seq}"/>
        <admin-dojo:jsonValue name="parentId" value="${queryGroup.parentNode.id}"/>
      </admin-dojo:jsonObject>
    </c:if>
    <c:if test="${queryRuleId != null}">
      <queryrule:queryRuleFindByPrimaryKey queryRuleId="${queryRuleId}" var="queryRule"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${queryRuleId}"/>
        <admin-dojo:jsonValue name="name" value="${queryRule.name}"/>
        <admin-dojo:jsonValue name="description" value="${queryRule.name}"/>
        <admin-dojo:jsonValue name="seq" value="${queryRule.seq}"/>
        <admin-dojo:jsonValue name="treeIndex" value="${queryRule.seq + fn:length(queryRule.queryRuleGroup.childQueryRuleGroups)}"/>
        <admin-dojo:jsonValue name="parentId" value="${queryRule.queryRuleGroup.id}"/>
      </admin-dojo:jsonObject>
    </c:if>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/qr_json_response.jsp#2 $$Change: 651448 $--%>
