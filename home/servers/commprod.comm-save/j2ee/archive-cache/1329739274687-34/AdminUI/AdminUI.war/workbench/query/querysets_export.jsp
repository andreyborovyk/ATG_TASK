<%--
  JSP,  allow  export Query Rules Sets.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_export.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="querySetId" var="querySetId"/>

  <c:url var="downloadUrl" value="/download"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <form method="POST" action="${downloadUrl}">
      <input name="itemType" value="query" type="hidden" class="unchanged" />
      <input name="itemId" value="${querySetId}" type="hidden" class="unchanged"/>
      <div id="paneContent">
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left"><h3><fmt:message key="querysets_export.table.title.export_from_queryset"/></h3></td>
            </tr>
            <tr>
              <td class="label">
                <label for="querySetName">
                  <fmt:message key="querysets_export.table.name"/>
                </label>
              </td>
              <td>
                <queryrule:queryRuleSetFindByPrimaryKey queryRuleSetId="${querySetId}" var="queryRuleSet"/>
                <c:out value="${queryRuleSet.name}"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="includeMacros">
                  <span id="includeMacrosAlert"></span><fmt:message key="querysets_export.table.global_macros"/>
                </label>
              </td>
              <td>
                <ul class="simple">
                  <li>
                    <input type="checkbox" value="true" name="includeMacros" class="unchanged" />
                    <fmt:message key="querysets_export.table.export"/>
                  </li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <%-- Export button--%>
        <fmt:message key="querysets_export.button.export" var="updateButtonTitle"/>
        <fmt:message key="querysets_export.button.export.tooltip" var="updateButtonToolTip"/>
        <input type="submit" name="export" value="${updateButtonTitle}" title="${updateButtonToolTip}" class="unchanged" />
        <%-- Cancel button--%>
        <fmt:message key="querysets_export.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="querysets_export.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_export.jsp#2 $$Change: 651448 $--%>
