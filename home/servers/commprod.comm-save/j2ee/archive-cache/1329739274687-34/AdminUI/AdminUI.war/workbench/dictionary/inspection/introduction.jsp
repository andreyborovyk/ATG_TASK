<%--
Main dictionary inspection page

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/introduction.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject" var="activeSearchProject"/>
  <c:choose>
    <c:when test="${empty activeSearchProject.activeProjectId || empty activeSearchProject.environmentId}">
      <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
           executeScripts="true" cacheContent="false" scriptSeparation="false">
        <div id="paneContent">

          <p><fmt:message key="dictionary_inspection.introductory.allows"/></p>

          <p><fmt:message key="dictionary_inspection.introductory.includes"/></p>

          <p><b><fmt:message key="dictionary_inspection.introductory.term_lookup"/></b><br/>
          <fmt:message key="dictionary_inspection.introductory.term_lookup_discover"/></p>

          <p><b><fmt:message key="dictionary_inspection.introductory.text_processing"/></b><br/>
          <fmt:message key="dictionary_inspection.introductory.text_processing_evaluate"/></p>

          <p><b><fmt:message key="dictionary_inspection.introductory.text_analysis"/></b><br/>
          <fmt:message key="dictionary_inspection.introductory.text_analysis_explore"/></p><br/>

          <p><fmt:message key="dictionary_inspection.introductory.note"/></p><br/>

          <p><fmt:message key="dictionary_inspection.introductory.select"/></p>

          <script type="text/javascript">
            function refreshRightPane(obj) {
              loadRightPanel('${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp');
            }
          </script>
        </div>
      </div>
      <script type="text/javascript">
        //dojo tree refresh
        top.hierarchy = [{id:"rootDictInspectNode"}];
        top.syncTree();
      </script>
    </c:when>
    <c:otherwise>
      <script>loadingStatus.setRedirectUrl("${pageContext.request.contextPath}${dictionaryPath}/inspection/term_lookup.jsp");</script>
    </c:otherwise>
  </c:choose>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/introduction.jsp#2 $$Change: 651448 $--%>
