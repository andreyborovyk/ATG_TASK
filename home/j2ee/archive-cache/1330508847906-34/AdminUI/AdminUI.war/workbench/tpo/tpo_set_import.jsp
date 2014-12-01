<%--
  JSP,  allow import TPO Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_import.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="level" var="level" />

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/ImportTPOFormHandler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="level" value="${level}"/>
  </admin-ui:initializeFormHandler>
  <c:url var="successURL" value="${tpoPath}/tpo_browse.jsp">
    <c:param name="level" value="${level}"/>
  </c:url>
  <c:url var="errorURL" value="${tpoPath}/tpo_set_import.jsp">
    <c:param name="level" value="${level}"/>
  </c:url>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="${errorURL}" enctype="multipart/form-data">

      <div id="paneContent">
        <h3><fmt:message key="tpo_set.import.message.file"/></h3>

        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <label for="TPOFile">
                  <fmt:message key="tpo_set.import.file.path"/>
                </label>
              </td>
              <td>
                <d:input type="file" bean="ImportTPOFormHandler.TPOFile" id="TPOFile" iclass="textField" name="TPOFile"/>
              </td>
            </tr>
          </tbody>
        </table>

        <h3><fmt:message key="tpo_set.import.message.tpo"/></h3>

        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <span id="tpoNameAlert">
                  <span class="required"><fmt:message key="project_general.required_field"/></span>
                </span>
                <label for="tpoName">
                  <fmt:message key="tpo_set.import.name"/>
                </label>
              </td>
              <td align="left">
                <d:input type="text" bean="ImportTPOFormHandler.tpoName" id="tpoName" iclass="textField" name="tpoName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="type">
                  <fmt:message key="tpo_set.import.type"/>
                </label>
              </td>
              <td align="left">
                <ul class="simple">
                  <li>
                    <d:input type="radio" id="type_0" value="0" name="type" bean="ImportTPOFormHandler.type"/>
                    <label for="type_0"><fmt:message key="tpo_set.import.type.search_project"/></label>
                  </li>
                  <li>
                    <d:input type="radio" id="type_1" value="1" name="type" bean="ImportTPOFormHandler.type"/>
                    <label for="type_1"><fmt:message key="tpo_set.import.type.content_set"/></label>
                  </li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <d:input bean="ImportTPOFormHandler.successURL" type="hidden" value="${successURL}" name="successURL"/>
        <d:input bean="ImportTPOFormHandler.errorURL" type="hidden" value="${errorURL}" name="errorURL"/>
        <d:input bean="ImportTPOFormHandler.needInitialization" type="hidden" value="false" name="needInitialization"/>

        <fmt:message key="tpo_set.import.buttons" var="updateButtonTitle"/>
        <fmt:message key="tpo_set.import.buttons.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ImportTPOFormHandler.import" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" onclick="return checkForm()"/>

        <%-- Cancel button --%>
        <fmt:message key="tpo_set.import.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="tpo_set.import.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="ImportTPOFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" />
        <admin-validator:validate beanName="ImportTPOFormHandler"/>
      </div>
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTpoNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_import.jsp#2 $$Change: 651448 $--%>
