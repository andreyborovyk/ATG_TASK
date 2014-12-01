<%--
  JSP, showing "ATG commerce, ATG knowledge and ATG repository" source type fields.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_source_type_repository.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="section" var="section"/>
  <d:importbean var="settings" bean="/atg/searchadmin/adminui/navigation/RepositorySourceSettings"/>

  <c:choose>
    <c:when test="${section == 'general'}">

      <table class="form" cellspacing="0" cellpadding="0">
        <tbody>
          <tr>
            <td class="label">
              <span id="settings.indexingOutputConfigPathAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
              <fmt:message key="select_source_type.repository.path"/>
             </td>
            <td colspan="2">
              <d:input type="text" iclass="textField" name="settings.IndexingOutputConfigPath"
                       bean="RepositorySourceSettings.IndexingOutputConfigPath"/>
              <span class="ea"><tags:ea key="embedded_assistant.select_source_type_repository.ioc_path" /></span>
            </td>
          </tr>
          <tr>
            <td class="label">
              <fmt:message key="select_source_type.repository.location"/>
            </td>
            <td colspan="2">
              <ul class="small">
                <li>
                  <d:input type="radio" name="settings.local"
                           bean="RepositorySourceSettings.local" value="true" id="local.true"/>
                  <label for="local.true"><fmt:message key="select_source_type.repository.local"/></label>
                </li>
                <li>
                  <d:input type="radio" name="settings.local"
                           bean="RepositorySourceSettings.local" value="false" id="local.false"/>
                  <label for="local.false"><fmt:message key="select_source_type.repository.remote"/></label>
                </li>
              </ul>
            </td>
          </tr>
          <tr>
            <td class="label"></td>
            <td width="16%" align="right">
              <span id="settings.hostMachineAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
              <fmt:message key="select_source_type.repository.host.machine"/>
            </td>
            <td>
              <d:input type="text" name="settings.hostMachine" iclass="textField halved"
                       bean="RepositorySourceSettings.hostMachine"/>
              <span class="ea"><tags:ea key="embedded_assistant.select_source_type_repository.host_machine" /></span>
            </td>
          </tr>
          <tr>
            <td class="label"></td>
            <td width="16%" align="right">
              <span id="settings.rmiPortAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
              <fmt:message key="select_source_type.repository.port"/>
            </td>
            <td>
              <d:input type="text" name="settings.rmiPort"
                       iclass="textField halved" bean="RepositorySourceSettings.rmiPort"/>
              <span class="ea"><tags:ea key="embedded_assistant.select_source_type_repository.port" /></span>
            </td>
          </tr>
        </tbody>
      </table>

    </c:when>
    <c:when test="${section == 'indexing'}">


    </c:when>
    <c:when test="${section == 'other'}">


    </c:when>
    <c:when test="${section == 'advanced'}">


    </c:when>
  </c:choose>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_source_type_repository.jsp#2 $$Change: 651448 $--%>
