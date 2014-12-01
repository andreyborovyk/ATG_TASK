<%--
Brogress bar of the  synchronization status monitor page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_progress_bar.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskHistoryInfo" var="syncTaskHistoryInfo"/>
  <table class="form" cellpadding="0" cellspacing="0">
    <tbody>
    <tr>
      <td class="label">&nbsp;</td>
      <td>
        <table class="progressBar" cellpadding="0" cellspacing="0">
          <tr>
            <c:forEach begin="0" end="20" varStatus="currentIndex">
              <td <c:if test="${(syncTaskHistoryInfo.percentItemsCompleteAsString)/5 >= currentIndex.index}">class="completed"</c:if>>
              </td>
            </c:forEach>
          <tr>
        </table>
      </td>
    </tr>
    <tr>
      <td class="label">&nbsp;</td>
      <td>
        <fmt:message key="sync_sm_progress_bar.note" />
      </td>
    </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_progress_bar.jsp#2 $$Change: 651448 $--%>
