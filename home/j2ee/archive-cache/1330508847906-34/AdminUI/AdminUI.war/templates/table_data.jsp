<%--
JSP, used to be template for table custom tag.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/table_data.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>

  <c:set var="table" value="${requestScope.table}" />
  <c:choose>
    <c:when test="${empty table.rows and not empty table.emptyMessage}">
      <fmt:message key="${table.emptyMessage}"/>
    </c:when>
    <c:otherwise>
      <table class="data" cellspacing="0" cellpadding="0" <c:if test="${not empty table.tableId}">id="${table.tableId}"</c:if>
          onchange="for (var i = 1; i < this.rows.length; i++) {this.rows[i].className = (i % 2) ? '' : 'alt'}">
        <thead>
          <c:if test="${not empty table.header}">
            <tr>
              <td colspan="${table.columnsCount}" class="dataHeader">${table.header}</td>
            </tr>
          </c:if>
          <tr>
            <c:forEach var="column" items="${table.columns}">
              <th <c:if test="${not empty column.width}">width="<c:out value="${column.width}" />"</c:if>
                  <c:if test="${column.type == 'checkbox'}">class="rowSelector"</c:if>>
                <c:choose>
                  <c:when test="${column.type == 'sortable' and not empty table.onSort}">
                    <c:set var="onClickScript">${table.onSort}('${table.tableId}','${column.name}',
                      '${table.sortField == column.name and table.sortMode == 'asc' ? "desc" : "asc"}');return(false);</c:set>
                    <%-- sortMode is "asc" or "desc", so "${table.sortMode}ending" = "ascending" or "descending" --%>
                    <a href="#" onclick="${onClickScript}"
                        <c:if test="${table.sortField == column.name}">class="${table.sortMode}ending"</c:if>>
                      <fmt:message key="${column.title}"/></a>
                  </c:when>
                  <c:otherwise>
                    ${column.headerContent}
                    <c:if test="${not empty column.title}">
                      <fmt:message key="${column.title}"/>
                      <c:if test="${not empty column.messageEA}">
                        <tags:ea key="${column.messageEA}" />
                      </c:if>
                    </c:if>
                  </c:otherwise>
                </c:choose>
              </th>
            </c:forEach>
          </tr>
        </thead>
        <tbody>
          <c:if test="${empty table.rows}">
            <tr>
              <td colspan="${table.columnsCount}"><fmt:message key="error_message.empty_table"/></td>
            </tr>
          </c:if>
          <c:if test="${not empty table.rows}">
            <c:forEach var="row" items="${table.rows}" varStatus="rowStatus">
              <tr class="${rowStatus.index%2 != 0 ? 'alt' : ''} ${row.currentRowInTable ? 'currentRow' : ''}">
                <c:forEach var="cell" items="${row.cells}" varStatus="cellStatus">
                  <td class="${table.columns[cellStatus.index].type == 'icon' ? 'iconCell' : table.columns[cellStatus.index].type == 'trunc' ? 'trunc' : ''}">${cell}</td>
                </c:forEach>
              </tr>
            </c:forEach>
          </c:if>
        </tbody>
        <c:if test="${not empty table.footer and not empty table.rows}">
          <tfoot>
            <tr>
              <td colspan="${table.columnsCount}">${table.footer}</td>
            </tr>
          </tfoot>
        </c:if>
      </table>
    </c:otherwise>
  </c:choose>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/table_data.jsp#2 $$Change: 651448 $--%>
