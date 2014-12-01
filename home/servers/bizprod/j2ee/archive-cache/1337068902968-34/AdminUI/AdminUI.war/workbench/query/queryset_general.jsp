<%--
 * Query Rule Set General Info.
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_general.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/QueryRuleSetFormHandler"/>

  <h3><fmt:message key="queryset_general.message.title"/></h3>
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="querySetNameAlert">
            <span class="required"><fmt:message key="project_general.required_field"/></span>
          </span>
          <label for="querySetName">
            <fmt:message key="queryset_general.table.name"/>
          </label>
        </td>
        <td>
          <%-- Query set name --%>
          <d:input bean="QueryRuleSetFormHandler.querySetName" type="text" iclass="textField" name="querySetName"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <label for="querySetDescription">
            <span id="querySetDescriptionAlert"></span><fmt:message key="queryset_general.table.description"/>
          </label>
        </td>
        <td>
          <%-- Query set name --%>
          <d:input bean="QueryRuleSetFormHandler.querySetDescription" type="text" iclass="textField" name="querySetDescription"/>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/queryset_general.jsp#2 $$Change: 651448 $--%>
