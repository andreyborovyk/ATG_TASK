<%--
 *  JSP allow import Term Weight Set to file
 *
 * @author dsitnits
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_import.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ImportTermWeightSetFormHandler"/>

  <c:url var="successURL" value="${weightPath}/term_weight_sets.jsp"/>
  <c:url var="errorURL" value="${weightPath}/term_weight_set_import.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="${successURL}" enctype="multipart/form-data">
      <div id="paneContent">
        <%-- Table contain necessary field for import term weight set --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="term_weight_import.table.title.import_from_xml"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="termWeightFile">
                  <span id="termWeightFileAlert"></span><fmt:message key="term_weight_import.table.filepath"/>
                </label>
              </td>
              <td>
                <d:input type="file" bean="ImportTermWeightSetFormHandler.termWeightFile"
                         id="termWeightFile" iclass="textField" name="termWeightFile"/>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="term_weight_import.table.title.import_to_term_weight"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <span id="termWeightNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="termWeightName">
                  <fmt:message key="term_weight_import.table.name"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportTermWeightSetFormHandler.termWeightName"
                         id="termWeightName" iclass="textField" name="termWeightName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="termWeightDescription">
                  <span id="termWeightDescriptionAlert"></span><fmt:message key="term_weight_import.table.description"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportTermWeightSetFormHandler.termWeightDescription"
                         iclass="textField" id="termWeightDescription" name="termWeightDescription"/>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <%-- Footer --%>
      <div id="paneFooter">
        <d:input bean="ImportTermWeightSetFormHandler.successURL" type="hidden"
                 value="${successURL}" name="successURL"/>
        <d:input bean="ImportTermWeightSetFormHandler.errorURL" type="hidden"
                 value="${errorURL}" name="errorURL"/>
        <%-- Import button--%>
        <fmt:message key="term_weight_import.button.import" var="updateButtonTitle"/>
        <fmt:message key="term_weight_import.button.import.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ImportTermWeightSetFormHandler.import" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" onclick="return checkForm()"/>
        <%-- Cancel button--%>
        <fmt:message key="term_weight_import.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="term_weight_import.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="ImportTermWeightSetFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />

        <admin-validator:validate beanName="ImportTermWeightSetFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTermWeightNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_import.jsp#2 $$Change: 651448 $--%>
