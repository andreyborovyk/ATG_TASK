<%@ include file="/templates/top.jspf" %>
<d:page xml="true">
  <admin-dojo:jsonObject>
    <c:if test="${not empty url}">
      <c:set var="data">
        <c:import url="${url}" charEncoding="${charEncoding}">
          <c:param name="atg.searchadmin.skipMessages" value="true" />
        </c:import>
      </c:set>
      <admin-dojo:jsonValue name="data" value="${data}"/>
    </c:if>
    <c:if test="${not empty popupAction}">
      <admin-dojo:jsonValue name="popupAction" value="${popupAction}"/>
    </c:if>
    <admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/ajax_response.jsp#1 $$Change: 651360 $--%>
