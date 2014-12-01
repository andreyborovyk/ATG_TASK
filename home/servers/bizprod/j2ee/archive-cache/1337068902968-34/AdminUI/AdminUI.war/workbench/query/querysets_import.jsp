<%--
  JSP,  allow  import Query Rules Sets.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_import.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ImportQuerySetFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="successURL" value="${queryPath}/querysets_general.jsp"/>
    <c:url var="errorURL"   value="${queryPath}/querysets_import.jsp"/>

    <d:form method="POST" action="${successURL}" enctype="multipart/form-data">
      <div id="paneContent">
        <%-- Table contain necessary field for import query rule set --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="querysets_import.table.title.import_from_xml"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="filePath">
                  <span id="filePathAlert"></span><fmt:message key="querysets_import.table.filepath"/>
                </label>
              </td>
              <td>
               <d:input type="file" bean="ImportQuerySetFormHandler.queryFile" value=""
                        id="filePath" iclass="textField" name="filePath"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="macros">
                  <span id="macrosAlert"></span><fmt:message key="querysets_import.table.global_macros"/>
                </label>
              </td>
              <td>
                <d:select bean="ImportQuerySetFormHandler.enableMacrosOption" iclass="small">
                  <d:option value="0">
                    <fmt:message key="querysets_import.dropdown.option.not_import"/>
                  </d:option>
                  <d:option value="1">
                    <fmt:message key="querysets_import.dropdown.option.import_as_global.exist.skip"/>
                  </d:option>
                  <d:option value="2">
                    <fmt:message key="querysets_import.dropdown.option.import_as_global.exist.overwrite"/>
                  </d:option>
                </d:select>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="querysets_import.table.title.import_to_queryset"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <span id="querySetNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="querySetName">
                  <fmt:message key="querysets_import.table.name"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportQuerySetFormHandler.querySetName"
                         id="querySetName" iclass="textField" name="querySetName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="querySetDescription">
                  <span id="querySetDescriptionAlert"></span><fmt:message key="querysets_import.table.description"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportQuerySetFormHandler.querySetDescription"
                         iclass="textField" id="querySetDescription" name="querySetDescription"/>
              </td>
            </tr>
         </tbody>
       </table>
      </div>
      <%-- Footer --%>
      <div id="paneFooter">
        <d:input bean="ImportQuerySetFormHandler.successURL" type="hidden"
                 value="${successURL}" name="successURL"/>
        <d:input bean="ImportQuerySetFormHandler.errorURL" type="hidden"
                 value="${errorURL}" name="errorURL"/>
        <%-- Import button--%>
        <fmt:message key="querysets_import.button.import" var="updateButtonTitle"/>
        <fmt:message key="querysets_import.button.import" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ImportQuerySetFormHandler.import" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" onclick="return checkForm()"/>
        <%-- Cancel button--%>
        <fmt:message key="querysets_import.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="querysets_import.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="ImportQuerySetFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
        
        <admin-validator:validate beanName="ImportQuerySetFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootQrNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_import.jsp#2 $$Change: 651448 $--%>
