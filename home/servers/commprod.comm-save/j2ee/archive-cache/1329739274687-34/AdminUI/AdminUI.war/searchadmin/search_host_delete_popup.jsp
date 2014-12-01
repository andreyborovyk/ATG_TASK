<%--
 Search host delete JSP. Allows to delete a host.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_host_delete_popup.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" />
  <d:getvalueof param="environmentId" var="environmentId" />
  <d:getvalueof param="hostId" var="hostId" />
  <admin-beans:getProjectEnvHostName environmentId="${environmentId}" hostId="${hostId}" var="hostName"/>
  
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteSearchHostFormHandler"/>
  
  <c:set var="formHandler" value="DeleteSearchHostFormHandler"/>
  <script type="text/javascript">
    function customLoad(){
      atg.searchadmin.adminui.formsubmitter.customPopupHandleResponse = function(data) {
        beforeClosePopUp();
      };
    }
  </script>
  <c:url var="configureHostsURL" value="/searchadmin/search_env_configure_hosts.jsp">
    <c:if test="${not empty projectId}">
      <c:param name="projectId" value="${projectId}"/>
    </c:if>
    <c:param name="environmentId" value="${environmentId}"/>
  </c:url>
  <c:url var="errorURL" value="/searchadmin/search_host_delete_popup.jsp">
    <c:if test="${not empty projectId}">
      <c:param name="projectId" value="${projectId}"/>
    </c:if>
    <c:param name="environmentId" value="${environmentId}"/>
  </c:url>

  <d:form action="${errorURL}" method="POST">
    <d:input type="hidden" bean="${formHandler}.hostId" value="${hostId}" name="hostId"/>
    <d:input type="hidden" bean="${formHandler}.errorURL" value="${errorURL}" name="errorUrl"/>

    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="search_host_delete.question">
          <fmt:param>
            <strong>
              <fmt:message key="search_host_delete.host"/>
              <c:out value="${hostName}"/>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="search_host_delete.button.ok" var="okButton"/>
      <fmt:message key="search_host_delete.button.ok.tooltip" var="okButtonToolTip"/>
      <d:input type="submit" value="${okButton}" iclass="formsubmitter"
                   bean="${formHandler}.delete"
                   title="${okButtonToolTip}"/>
      <input type="button" value="<fmt:message key='search_host_delete.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='search_host_delete.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_host_delete_popup.jsp#1 $$Change: 651360 $--%>
