<%--
  JSP, showing table of all customization sources.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/search_workbench.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <fmt:message key="search_workbench.head" var="title"/>
      <c:if test="${not empty title}">
        <p>
          <c:out value="${title}"/>
        </p>
      </c:if>

      <h3>
        <fmt:message key="search_workbench.customization_data_type"/>
      </h3>
      <ul class="simpleBullets">
        <d:getvalueof bean="/atg/searchadmin/adminui/navigation/NavigationTreeComponent.areasById.workbench.stateNode"
                      var="workbenchStateNode"/>
        <c:forEach var="customization" items="${workbenchStateNode.children}">
          <c:if test="${not empty customization.node.activeProjectType}">
            <li>
              <a href="${pageContext.request.contextPath}${customization.link}" onclick="return loadRightPanel(this.href);">
                <fmt:message key="${customization.node.title}"/>
              </a>
            </li>
          </c:if>
        </c:forEach>
      </ul>

    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/search_workbench.jsp#2 $$Change: 651448 $--%>
