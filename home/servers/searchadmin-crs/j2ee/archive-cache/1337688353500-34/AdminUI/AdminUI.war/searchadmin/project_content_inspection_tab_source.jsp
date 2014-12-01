<%--
@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_tab_source.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="env" var="env"/>
  <d:getvalueof param="documentUrl" var="documentUrl"/>
  <div id="contentSourceDoc">
    <div class="content_tab">
      <br/>
      <table width="96%" cellspacing="0" cellpadding="0">
        <tr>
          <td>
            <fieldset>
              <legend><fmt:message key='content_inspection_tabs.tab.metadata.meta_sets'/></legend>
              <div class="fieldset_content">
                <admin-beans:getDocumentContent var="response" environmentName="${env}" documentUrl="${documentUrl}" />
                <c:out value="${response.documentText}" />
              </div>
            </fieldset>
          </td>
        </tr>
      </table>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_tab_source.jsp#2 $$Change: 651448 $--%>
