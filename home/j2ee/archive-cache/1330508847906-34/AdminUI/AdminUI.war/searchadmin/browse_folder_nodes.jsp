<%--
JSP, used as controller for dojo topic tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_folder_nodes.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof var="action" param="action"/>
  <d:getvalueof var="data" param="data"/>

  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="node"/>
  <admin-dojo:getSubObject jsonObjectData="${node}" jsonSubObjectName="widgetId" var="path"/>
  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="treeNodeType" var="nodeType"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="children">
      <c:choose>
        <c:when test="${nodeType == 'rootNode'}">
          <fmt:message var="rootTitle" key="browse_file_system_popup.root_node" />
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="root"/>
            <admin-dojo:jsonValue name="titleText" value="${rootTitle}"/>
            <admin-dojo:jsonValue name="toolTip" value="${rootTitle}"/>
            <admin-dojo:jsonValue name="isFolder" value="${true}"/>
          </admin-dojo:jsonObject>
        </c:when>
        <c:otherwise>
          <admin-beans:getFolders var="folders" path="${path}"/>
          <c:forEach items="${folders}" var="folder">
            <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="directory:${folder.id}"/>
              <admin-dojo:jsonValue name="titleText" value="${folder.name}"/>
              <admin-dojo:jsonValue name="toolTip" value="${folder.description}"/>
              <admin-dojo:jsonValue name="isFolder" value="${folder.children}"/>
            </admin-dojo:jsonObject>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_folder_nodes.jsp#2 $$Change: 651448 $--%>
