<%--
  Policy settings JSP. Used to configure policy settings of environment.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/policy_settings.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page> 
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler"/>

  <h3><fmt:message key="search_env_configure_hosts.policy.settings"/></h3>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label"><fmt:message key="search_env_configure_hosts.policy.rollback"/></td>
        <td>
          <ul class="simple">
            <li>
              <d:input type="radio" id="rollbackPolicyLenient" name="rollbackPolicy"
                           bean="SearchEnvironmentFormHandler.rollbackPolicy" value="lenient"/>
              <fmt:message key="search_env_configure_hosts.policy.rollback.lenient"/>
            </li>
            <li>
              <d:input type="radio" id="rollbackPolicyStrict" name="rollbackPolicy"
                           bean="SearchEnvironmentFormHandler.rollbackPolicy" value="strict"/>
              <fmt:message key="search_env_configure_hosts.policy.rollback.strict"/>
            </li>
          </ul>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/policy_settings.jsp#2 $$Change: 651448 $--%>
