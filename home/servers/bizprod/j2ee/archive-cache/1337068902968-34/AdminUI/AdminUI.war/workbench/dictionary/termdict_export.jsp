<%--
 * JSP allow export Term Dictionary from file
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_export.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="dictionaryId" var="dictionaryId"/>

  <c:url var="downloadUrl" value="/download"/>

  <dictionary:termDictionaryFindByPrimaryKey termDictionaryId="${dictionaryId}" var="dictionary"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <form method="POST" action="${downloadUrl}">
      <input name="itemType" value="dictionary" type="hidden" class="unchanged" />
      <input name="itemId" value="${dictionaryId}" type="hidden" class="unchanged" />

      <div id="paneContent">
        <%-- Table contain necessary field and info for export term dictionary --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left"><h3>
                <fmt:message key="termdict_export.table.title.export_from_dictionary"/>
              </h3></td>
            </tr>
            <tr>
              <td class="label">
                <label for="dictionaryName">
                  <span id="dictionaryNameAlert"></span>
                  <fmt:message key="termdict_export.table.name"/>
                </label>
              </td>
              <td>
                <c:out value="${dictionary.name}"/>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <%-- Export button--%>
        <fmt:message key="termdict_export.button.export" var="updateButtonTitle"/>
        <fmt:message key="termdict_export.button.export.tooltip" var="updateButtonToolTip"/>
        <input type="submit" name="export" value="${updateButtonTitle}" title="${updateButtonToolTip}" class="unchanged" />
        <%-- Cancel button--%>
        <fmt:message key="termdict_export.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="termdict_export.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_export.jsp#2 $$Change: 651448 $--%>
