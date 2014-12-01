<%--
  JSP, allowing to manage project index, includes project_index.jsp with all functionality.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_manage_index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- id of index's project --%>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <d:include page="project_index.jsp">
        <d:param name="projectId" value="${projectId}"/>
      </d:include>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_manage_index.jsp#2 $$Change: 651448 $--%>
