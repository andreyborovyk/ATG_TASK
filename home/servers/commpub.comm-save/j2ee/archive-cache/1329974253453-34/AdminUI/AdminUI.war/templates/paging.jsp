<%--
  Template file, containing paging for tables, it included from another page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/paging.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="totalItems" var="totalItems"/>
  <d:getvalueof param="currentPage" var="currentPage"/>
  <d:getvalueof param="totalPages" var="totalPages"/>
  <d:getvalueof param="itemsPerPage" var="itemsPerPage"/>
  <d:getvalueof param="onPage" var="onPage"/>
  
  <c:if test="${totalPages > 1}">
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td nowrap="true">
          <span>
            <fmt:message key="paging.items">
              <fmt:param value="${(currentPage - 1) * itemsPerPage + 1}" />
              <fmt:param value="${currentPage == totalPages ? totalItems : currentPage * itemsPerPage}" />
              <fmt:param value="${totalItems}" />
            </fmt:message>
          </span>
        </td>

        <td width="15">
          <a href="#"
            <c:if test="${currentPage==1}">
              title="<fmt:message key='paging.first.page.tooltip'/>"
              class="icon pageContentBack inactive" onclick="return false;"
            </c:if>
            <c:if test="${currentPage!=1}">
              title="<fmt:message key='paging.first.page.tooltip'/>"
              class="icon pageContentBack" onclick="return ${onPage}(1);"
            </c:if>
            >&nbsp;</a>
        </td>

        <td width="15">
          <a href="#"
            <c:if test="${currentPage==1}">
              title="<fmt:message key='paging.previous.page.tooltip'/>"
              class="icon pageContentPrev inactive" onclick="return false;"
            </c:if>
            <c:if test="${currentPage!=1}">
              title="<fmt:message key='paging.previous.page.tooltip'/>"
              class="icon pageContentPrev" onclick="return ${onPage}(${currentPage-1});"
            </c:if>
            >&nbsp;</a>
        </td>

        <td width="10" align="center">
          <c:if test="${currentPage > 1}">
            <a href="#" onclick="return ${onPage}(${currentPage - 1});">${currentPage - 1}</a>
          </c:if>
        </td>
        <td width="10" align="center">
          <b>${currentPage}</b>
        </td>
        <td width="10" align="center">
          <c:if test="${currentPage < totalPages}">
            <a href="#" onclick="return ${onPage}(${currentPage + 1});">${currentPage + 1}</a>
          </c:if>
        </td>

        <td width="15">
          <a href="#"
            <c:if test="${currentPage!=totalPages}">
              title="<fmt:message key='paging.next.page.tooltip'/>"
              class="icon pageContentNext" onclick="return ${onPage}(${currentPage+1});"
            </c:if>
            <c:if test="${currentPage==totalPages}">
              title="<fmt:message key='paging.next.page.tooltip'/>"
              class="icon pageContentNext inactive" onclick="return false;"
            </c:if>
            >&nbsp;</a>
        </td>
      </tr>
    </table>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/paging.jsp#2 $$Change: 651448 $--%>
