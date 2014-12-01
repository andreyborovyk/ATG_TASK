<%--
 *  JSP allow import Term Dictionary to file
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_import.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ImportDictionaryFormHandler"/>
  <c:url var="successURL" value="${dictionaryPath}/termdicts_general.jsp"/>
  <c:url var="errorURL"   value="${dictionaryPath}/termdict_import.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="${successURL}" enctype="multipart/form-data">
      <div id="paneContent">
        <%-- Table contain necessary field for import query rule set --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="termdict_import.table.title.import_from_xml"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="filePath">
                  <fmt:message key="termdict_import.table.filepath"/>
                </label>
              </td>
              <td>
               <d:input type="file" bean="ImportDictionaryFormHandler.dictionaryFile" value=""
                        id="filePath" iclass="textField" name="filePath"/>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="termdict_import.table.title.import_to_dictionary"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <span id="dictionaryNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="dictionaryName">
                  <fmt:message key="termdict_import.table.name"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportDictionaryFormHandler.dictionaryName"
                         id="dictionaryName" iclass="textField" name="dictionaryName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="dictionaryDescription">
                  <span id="dictionaryDescriptionAlert"></span><fmt:message key="termdict_import.table.description"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportDictionaryFormHandler.dictionaryDescription"
                         iclass="textField" id="dictionaryDescription" name="dictionaryDescription"/>
              </td>
            </tr>
         </tbody>
       </table>
      </div>
      <%-- Footer --%>
      <div id="paneFooter">
        <d:input bean="ImportDictionaryFormHandler.successURL" type="hidden"
                 value="${successURL}" name="successURL"/>
        <d:input bean="ImportDictionaryFormHandler.errorURL" type="hidden"
                 value="${errorURL}" name="errorURL"/>
        <%-- Import button--%>
        <fmt:message key="termdict_import.button.import" var="updateButtonTitle"/>
        <fmt:message key="termdict_import.button.import" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ImportDictionaryFormHandler.import"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}"
                 onclick="return checkForm();" iclass="formsubmitter"/>
        <%-- Cancel button--%>
        <fmt:message key="termdict_import.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="termdict_import.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input bean="ImportDictionaryFormHandler.cancel" type="submit" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
        <admin-validator:validate beanName="ImportDictionaryFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_import.jsp#2 $$Change: 651448 $--%>
