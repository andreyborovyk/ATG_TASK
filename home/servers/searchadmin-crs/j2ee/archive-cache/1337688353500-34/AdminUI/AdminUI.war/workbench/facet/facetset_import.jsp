<%--
  facetset_import.jsp. It used for import facet set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_import.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- URL definitions--%>
  <c:url value="${facetPath}/facetsets_general.jsp" var="successURL"/>
  <c:url value="${facetPath}/facetset_import.jsp" var="errorURL"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ImportFacetSetFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">

    <d:form method="POST" action="facetset_import.jsp" enctype="multipart/form-data">
      <div id="paneContent">
        <h3>
          <fmt:message key="facetsets_import.file_message"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <label for="facetSetFile">
                  <fmt:message key="facetsets_import.file_path"/>
                </label>
              </td>
              <td>
                <d:input type="file" bean="ImportFacetSetFormHandler.facetSetFile"
                         id="facetSetFile" iclass="textField" name="facetSetFile"/>
              </td>
            </tr>
          </tbody>
        </table>
        <h3>
          <fmt:message key="facetsets_import.facetset_message"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <span id="facetSetNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="facetSetName">
                  <fmt:message key="facetsets_import.name"/>
                </label>
              </td>
              <td>
                <d:input type="text" id="facetSetName" iclass="textField" name="facetSetName"
                         bean="ImportFacetSetFormHandler.facetSetName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="facetSetDescription">
                  <span id="facetSetDescriptionAlert"></span><fmt:message key="facetsets_import.description"/>
                </label>
              </td>
              <td>
                <d:input type="text" id="facetSetDescription" iclass="textField" name="facetSetDescription"
                         bean="ImportFacetSetFormHandler.facetSetDescription"/>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <d:input bean="ImportFacetSetFormHandler.successURL" type="hidden" value="${successURL}"/>
      <d:input bean="ImportFacetSetFormHandler.errorURL" type="hidden" value="${errorURL}"/>

      <div id="paneFooter">
        <%-- Import button--%>
        <fmt:message key="facetsets_import.button.import" var="updateButtonTitle"/>
        <fmt:message key="facetsets_import.button.import.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" value="${updateButtonTitle}" title="${updateButtonToolTip}"
                 bean="ImportFacetSetFormHandler.import" iclass="formsubmitter"
                 onclick="return checkForm()"/>
        <%-- Cancel button--%>
        <fmt:message key="facetsets_import.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="facetsets_import.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" value="${cancelButtonTitle}" title="${cancelButtonToolTip}"
                 bean="ImportFacetSetFormHandler.cancel" iclass="formsubmitter" />
        <admin-validator:validate beanName="ImportFacetSetFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootFacetSetNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_import.jsp#2 $$Change: 651448 $--%>
