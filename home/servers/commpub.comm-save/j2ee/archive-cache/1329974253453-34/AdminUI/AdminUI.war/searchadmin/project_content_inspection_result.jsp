<%--
@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_result.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="handler" var="handler"/>
  <d:getvalueof param="resultProperty" var="resultProperty"/>
  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
   
  <script type="text/javascript">
    function doPaging(pValue) {
      if (pValue) {
        var pagingHidden = document.getElementById('currentPageHidden');
        var v = parseInt(pValue);
        if (isNaN(v) || v <= 0) {
          v = 1;
        }
        pagingHidden.value = v - 1;
      }
      var pagingButton = document.getElementById('pagingButton');
      pagingButton.click();
      return false;
    }
  </script>
  <d:input bean="ContentInspectionFormHandler.paging" type="submit" id="pagingButton"
           iclass="formsubmitter" style="display:none" name="paging"/>
  <d:input bean="ContentInspectionFormHandler.currentPage" type="hidden"
           name="currentPage" id="currentPageHidden" />

  <d:include page="/templates/paging.jsp">
    <d:param name="totalItems" value="${handler.totalItems}" />
    <d:param name="currentPage" value="${handler.currentPage + 1}" />
    <d:param name="totalPages" value="${handler.totalPages}" />
    <d:param name="itemsPerPage" value="${handler.itemsPerPage}" />
    <d:param name="onPage" value="doPaging" />
  </d:include>
  <div class="paneResult onelinerows">
    <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                    var="result" items="${handler.results}" tableId="resultsTable">
      <c:url var="viewDetailed" value="/searchadmin/project_content_inspection_tabs.jsp">
        <c:param name="documentUrl" value="${result.url}"/>
        <c:param name="searchEnvironmentName" value="${handler.environmentName}"/>
      </c:url>
      <admin-ui:column title="content_inspection.documents.table.url" type="static">
        <div><a href="${viewDetailed}" onclick="return openSeparateWindow(this.href);"
          title="${result.url}">${result.urlExcerpt}</a></div>
      </admin-ui:column>
      <admin-ui:column headerContent="${resultProperty}" type="trunc">
        <div title="<c:out value='${result.resultValue}' />"><c:out value="${result.resultValue}" /></div>
      </admin-ui:column>
    </admin-ui:table>
  </div>
  <d:include page="/templates/paging.jsp">
    <d:param name="totalItems" value="${handler.totalItems}" />
    <d:param name="currentPage" value="${handler.currentPage + 1}" />
    <d:param name="totalPages" value="${handler.totalPages}" />
    <d:param name="itemsPerPage" value="${handler.itemsPerPage}" />
    <d:param name="onPage" value="doPaging" />
  </d:include>
    <script>
      var contInspectionResizeHandler = null;
      function contInspectionOnWindowResize() {
        var rt = document.getElementById("resultsTable");
        if (rt) {
          var ftw = document.getElementById("formTable").clientWidth;
          rt.style.width = ftw + "px";
          for (var i = 0; i < rt.rows.length; i++) {
            var c = rt.rows[i].cells[1];
            var w = ftw - c.offsetLeft;
            if (w < 100) {
              w = 100;
            }
            c.style.width = w + "px";
            if (i > 0) {
              // TODO remove hard-code "8".
              c.getElementsByTagName("div")[0].style.width = (w - 8) + "px";
            }
          }
        } else {
          dojo.disconnect(contInspectionResizeHandler);
        }
      }
      function customLoad() {
        contInspectionResizeHandler = dojo.connect(window, "onresize", contInspectionOnWindowResize);
        contInspectionOnWindowResize();
      }
    </script>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_result.jsp#2 $$Change: 651448 $--%>
