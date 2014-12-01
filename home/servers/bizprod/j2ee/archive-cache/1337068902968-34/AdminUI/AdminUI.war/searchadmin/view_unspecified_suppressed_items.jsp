<%--
Shows unspecified suppressed items.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/view_unspecified_suppressed_items.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="contentId" var="contentId"/>
  <common:contentSourceFindByPrimaryKey contentSourceId="${contentId}" var="content"/>
  <tags:separateWindow title="view_unspecified_suppressed_items.title">
    <div class="content_sp">
      <br/>
      <p>
        <fmt:message key="view_unspecified_suppressed_items.description"/>
      </p>
      <h2><c:out value="${content.name}" /></h2>
      <admin-beans:getSuppressedFilesData var="suppressedData" contentId="${contentId}"
        varUnspecifiedKey="unspecifiedKey" />
      <table class="data" cellspacing="0" cellpadding="0" style="width: 50%">
      <thead>
        <tr>
          <th><fmt:message key="view_unspecified_suppressed_items.extension"/></th>
          <th><fmt:message key="view_unspecified_suppressed_items.count"/></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="suppressedItem" items="${suppressedData[unspecifiedKey]}" varStatus="cursor">
          <tr <c:if test="${cursor.index % 2 == 1}">class="alt"</c:if>>
            <td>${suppressedItem.key}</td>
            <td>${suppressedItem.value}</td>
          </tr>
        </c:forEach>
      </tbody>
      </table>
    </div>
  </tags:separateWindow>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/view_unspecified_suppressed_items.jsp#1 $$Change: 651360 $--%>
