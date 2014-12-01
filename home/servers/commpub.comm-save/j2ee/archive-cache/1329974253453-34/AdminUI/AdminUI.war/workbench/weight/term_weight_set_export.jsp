<%--
 * JSP allow export Term Dictionary from file
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_export.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="termWeightId" var="termWeightId"/>

  <termweights:termWeightSetFindByPrimaryKey termWeightSetId="${termWeightId}" var="termWeightSet"/>

  <c:url var="downloadUrl" value="/download"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <form method="POST" action="${downloadUrl}">
      <input name="itemType" value="termWeight" type="hidden" class="unchanged" />
      <input name="itemId" value="${termWeightId}" type="hidden" class="unchanged" />

      <div id="paneContent">
        <%-- Table contain necessary field and info for export term weight set --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left">
                <h3>
                  <fmt:message key="term_weight_export.table.title.export_from_dictionary"/>
                </h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="term_weight_export.table.name"/>
              </td>
              <td>
                ${termWeightSet.name}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <%-- Export button--%>
        <fmt:message key="term_weight_export.button.export" var="updateButtonTitle"/>
        <fmt:message key="term_weight_export.button.export.tooltip" var="updateButtonToolTip"/>
        <input type="submit" name="export"
               value="${updateButtonTitle}" title="${updateButtonToolTip}" class="unchanged" />
        <%-- Cancel button--%>
        <fmt:message key="term_weight_export.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="term_weight_export.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_export.jsp#2 $$Change: 651448 $--%>
