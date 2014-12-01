<%--
  Text Processing Option landing JSP.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_sets_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <p>
        <fmt:message key="tpo_sets_general.head"/>
      </p>

      <ul class="simpleBullets">
        <li>
          <c:url value="${tpoPath}/tpo_browse.jsp" var="projectTPOSetURL">
            <c:param name="level" value="index"/>
          </c:url>
          <a href="${projectTPOSetURL}" onclick="return loadRightPanel(this.href);">
            <fmt:message key="tpo_sets_general.project_tpo_link"/>
          </a>
        </li>
        <li>
          <c:url value="${tpoPath}/tpo_browse.jsp" var="contentTPOSetURL">
            <c:param name="level" value="content"/>
          </c:url>
          <a href="${contentTPOSetURL}" onclick="return loadRightPanel(this.href);">
            <fmt:message key="tpo_sets_general.content_tpo_link"/>
          </a>
        </li>
      </ul>
    </div>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTpoNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_sets_general.jsp#2 $$Change: 651448 $--%>
