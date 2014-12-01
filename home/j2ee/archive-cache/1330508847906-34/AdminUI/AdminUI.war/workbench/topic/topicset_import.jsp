<%--
  JSP,  Import topic set. Allow import topic set from file

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_import.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ImportTopicSetFormHandler"/>

  <c:url var="successURL" value="${topicPath}/global_general.jsp"/>
  <c:url var="errorURL"   value="${topicPath}/topicset_import.jsp"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="topicset_import.jsp" enctype="multipart/form-data">
      <div id="paneContent">
        <%-- Table contain necessary field for import topic --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="topicset_import.table.title.import_from_xml"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="filePath">
                  <span id="filePathAlert"></span><fmt:message key="topicset_import.table.filepath"/>
                </label>
              </td>
              <td>
               <d:input type="file" bean="ImportTopicSetFormHandler.topicFile" value=""
                        id="filePath" iclass="textField" name="filePath"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="macros">
                  <span id="macrosAlert"></span><fmt:message key="topicset_import.table.global_macros"/>
                </label>
              </td>
              <td>
                <d:select bean="ImportTopicSetFormHandler.enableMacrosOption" iclass="small">
                  <d:option value="0">
                    <fmt:message key="topicset_import.dropdown.option.not_import"/>
                  </d:option>
                  <d:option value="1">
                    <fmt:message key="topicset_import.dropdown.option.import_as_global.exist.skip"/>
                  </d:option>
                  <d:option value="2">
                    <fmt:message key="topicset_import.dropdown.option.import_as_global.exist.overwrite"/>
                  </d:option>
                </d:select>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="topicids">
                  <span id="importTopicIdsAlert"></span><fmt:message key="topicset_import.table.import_topicids"/>
                </label>
              </td>
              <td>
                <ul class="simple">
                  <li>
                    <d:input type="radio" id="importTopicIds" name="importTopicIds"
                             bean="ImportTopicSetFormHandler.importTopicIds" value="true"/>
                    <fmt:message key="topicset_import.topicids.option.preserve"/>
                  </li>
                  <li>
                    <d:input type="radio" id="importTopicIds" name="importTopicIds"
                             bean="ImportTopicSetFormHandler.importTopicIds" value="false"/>
                    <fmt:message key="topicset_import.topicids.option.generate"/>
                  </li>
                </ul>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="left">
                <h3><fmt:message key="topicset_import.table.title.import_to_topicset"/></h3>
              </td>
            </tr>
            <tr>
              <td class="label">
                <span id="topicSetNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="topicSetName">
                  <fmt:message key="topicset_import.table.name"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportTopicSetFormHandler.topicSetName"
                         id="topicSetName" iclass="textField" name="topicSetName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="topicSetDescription">
                  <span id="topicSetDescriptionAlert"></span><fmt:message key="topicset_import.table.description"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportTopicSetFormHandler.topicSetDescription"
                         iclass="textField" id="topicSetDescription" name="topicSetDescription"/>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <%-- Footer --%>
      <div id="paneFooter">
        <d:input bean="ImportTopicSetFormHandler.successURL" type="hidden"
               value="${successURL}" name="successURL"/>
        <d:input bean="ImportTopicSetFormHandler.errorURL" type="hidden"
               value="${errorURL}" name="errorURL"/>

        <%-- Import button--%>
        <fmt:message key="topicset_import.button.import" var="updateButtonTitle"/>
        <fmt:message key="topicset_import.button.import" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ImportTopicSetFormHandler.import"
               value="${updateButtonTitle}" title="${updateButtonToolTip}"
               onclick="return checkForm()" iclass="formsubmitter"/>
        <%-- Cancel button--%>
        <fmt:message key="topicset_import.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="topicset_import.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="ImportTopicSetFormHandler.cancel"
               value="${cancelButtonTitle}" title="${cancelButtonToolTip}" iclass="formsubmitter"/>
        <admin-validator:validate beanName="ImportTopicSetFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTopicSetNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_import.jsp#2 $$Change: 651448 $--%>
