<%--
JSP, used to prepare json formatted messages for dojo tree client controller.
Used when you apply some operation using dojo tree context menu or DnD.
Used as success or error URL after method handler calling.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/dict_json_response.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/Messages" var="messagesComponent"/>
  <d:getvalueof var="termId" param="termId"/>
  <d:getvalueof var="dictId" param="dictId"/>
  <d:getvalueof var="tpoId" param="tpoSetId"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="messages">
      <c:forEach items="${messagesComponent.messagesArray}" var="message">
        <admin-dojo:jsonObject>
          <admin-dojo:jsonValue name="type" value="${message.type}"/>
          <admin-dojo:jsonValue name="message" value="${message.message}"/>
        </admin-dojo:jsonObject>
      </c:forEach>
    </admin-dojo:jsonArray>
    <c:if test="${dictId != null}">
      <dictionary:termDictionaryFindByPrimaryKey termDictionaryId="${dictId}" var="dict"/>
      <admin-beans:getDictionaryIndex var="index" termDictionary="${dict}"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${dictId}"/>
        <admin-dojo:jsonValue name="name" value="${dict.name}"/>
        <admin-dojo:jsonValue name="description" value="${dict.description}"/>
        <admin-dojo:jsonValue name="seq" value="${index}"/>
      </admin-dojo:jsonObject>
    </c:if>
    <c:if test="${termId != null}">
      <dictionary:termFindByPrimaryKey termId="${termId}" var="term"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${termId}"/>
        <admin-dojo:jsonValue name="name" value="${term.name}"/>
        <admin-dojo:jsonValue name="seq" value="${term.seq}"/>
        <admin-dojo:jsonValue name="parentId" value="${term.parentNode.id}"/>
      </admin-dojo:jsonObject>
    </c:if>
    <c:if test="${tpoId != null}">
      <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${tpoId}" var="tpoSet"/>
      <admin-beans:getSAOSetIndex var="index" saoSet="${tpoSet}"/>
      <admin-dojo:jsonObject name="data">
        <admin-dojo:jsonValue name="id" value="${tpoId}"/>
        <admin-dojo:jsonValue name="name" value="${tpoSet.name}"/>
        <admin-dojo:jsonValue name="description" value="${tpoSet.description}"/>
        <admin-dojo:jsonValue name="seq" value="${index}"/>
      </admin-dojo:jsonObject>
    </c:if>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/dict_json_response.jsp#2 $$Change: 651448 $--%>
