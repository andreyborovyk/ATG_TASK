<%--
 JSP tag, used for paging on jsp pages. It's the first part of dojo paging. The second is dojo content pane where
 content will be shown.
 @author pkouzmit, amarchan

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/dojoPanePaging.tag#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.tagf" %>
<%--Count of items per page--%>
<%@ attribute name="pageSize" required="true" description="Count of items per page" %>
<%--From what page to start. Default is 0--%>
<%@ attribute name="firstPage" required="false" description="From what page to start. Default is 0"%>
<%--Content pane href pageSize param name. Default is pageNum --%>
<%@ attribute name="pageSizeParamName" required="false" description="Content pane href pageSize param name. Default is pageNum"%>
<%--Content pane href pageNumber param name. Default is pageSize --%>
<%@ attribute name="pageNumberParamName" required="false" description="Content pane href pageNumber param name. Default is pageSize"%>
<%--Content pane href maxDocsPerSet param name. Default is maxDocsPerSet --%>
<%@ attribute name="maxDocsPerSetParamName" required="false" description="Content pane href maxDocsPerSet param name. Default is maxDocsPerSet"%>

<d:page>
  <script type="text/javascript">
    pageSize = ${pageSize};
    pageSizeParamName = '${pageSizeParamName}';
    pageNumberParamName = '${pageNumberParamName}';
    maxDocsPerSetParamName = '${maxDocsPerSetParamName}';
    firstPage = '${firstPage}';
  </script>

  <table width="95%" id="pagingTable" style="display:none">
    <tr valign="bottom">
      <td nowrap="nowrap">
          <%--Items--%>
        <fmt:message key="paging.items.header"/>
        <span id="firstDisplayed"></span>&nbsp;
        <fmt:message key="paging.items.itemDelimiter"/>
        <span id="lastDisplayed"></span>&nbsp;
        <fmt:message key="paging.items.countDelimiter"/>
        <span id="itemsCount"></span>
      </td>
      <td nowrap="nowrap">
          <%--Current page--%>
        <label for="field_currentPage">
          <span id="pageInfoBean.currentPageIndexAlert"></span>
        </label>
        <fmt:message key="paging.page.caption"/>
        <input type="text" size="3" class="textField number small" id="currentPageIndex"/>
        <fmt:message key="paging.button.go" var="buttonCaption"/>
        <input type="button" value="${buttonCaption}" onclick="gotoPage();" id="goButton"/>
      </td>
      <td nowrap="nowrap">
          <%--Pages--%>
        <a class="icon pageBack" href="javascript:pageBack();" id="pageBackLink"></a>
        <a class="icon pageForward" href="javascript:pageNext();" id="pageForwardLink"></a>
            <span>
          <fmt:message key="paging.page.header"/>
          <span id="currentPage"></span>&nbsp;
          <fmt:message key="paging.page.countDelimiter"/>
          <span id="pagesCount"></span>&nbsp;
        </span>
      </td>
    </tr>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/dojoPanePaging.tag#2 $$Change: 651448 $--%>
