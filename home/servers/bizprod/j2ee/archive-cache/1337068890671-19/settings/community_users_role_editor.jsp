<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle id="settings" baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:bundle id="roleNameBundle" baseName="atg.portal.admin.RoleNameResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,user-manager">

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/CommunityRoleFormHandler"/>

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode" idtype="java.lang.String" param="mode">

<dsp:getvalueof id="user_id" idtype="java.lang.String" param="user">
<dsp:getvalueof param="roleName" idtype="java.lang.String" id="userType">

<%-- initialize form handler fields --%>
<dsp:setvalue bean="CommunityRoleFormHandler.communityId" value="<%=dsp_community_id%>"/>
<dsp:setvalue bean="CommunityRoleFormHandler.userId" value="<%=user_id%>"/>

<%-- Create destination URLs --%>

<core:CreateUrl id="formURL" url="/portal/settings/community_users.jsp">
 <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
 <core:UrlParam  param="mode" value="1d"/>
 <core:UrlParam param="user" value="<%=user_id%>"/>

<dsp:form action="<%=formURL.getNewUrl()%>" method="POST" synchronized="/atg/portal/admin/CommunityRoleFormHandler">

<%-- Tell the form where to go if the submission is cancelled --%>
<core:CreateUrl id="cancelURL" url="/portal/settings/community_users.jsp">
 <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
 <core:UrlParam  param="mode" value="1"/>
   <dsp:input type="hidden" bean="CommunityRoleFormHandler.cancelURL" value="<%=cancelURL.getNewUrl()%>"/>
</core:CreateUrl>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"> <img src='images/write.gif' height="15" width="28" alt="" border="0">Edit administrative access for
<dsp:getvalueof id="user" idtype="atg.userdirectory.User" bean="CommunityRoleFormHandler.user">
  <%=user.getFirstName()%> <%=user.getLastName()%>
</dsp:getvalueof><%-- user --%>
</td></tr></table>
</td></tr></table>
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
Use the controls below to delegate administrative functionality to this user for this community.
</td></tr></table>
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

<dsp:getvalueof id="roleSettings" idtype="boolean[]" bean="CommunityRoleFormHandler.roleSettings">
<dsp:getvalueof id="categories" idtype="String[]" bean="CommunityRoleFormHandler.categories">
<dsp:getvalueof id="roleNames" idtype="String[]" bean="CommunityRoleFormHandler.roleNames">

<% int index = 0; %>
<% String i18nRoleName = ""; %>
<core:ForEach id="roleNameLoop" values="<%=roleNames%>"
              castClass="java.lang.String"
              elementId="roleName">

  <% 
    i18nRoleName = ( roleNameBundle.getString(roleName).equals("") ) ? roleName : roleNameBundle.getString(roleName) ;
  %>





  <tr>
    <td NOWRAP><font class="smaller">
<dsp:input type="checkbox" bean='<%= "CommunityRoleFormHandler.roleSettings[" + index++ + "]" %>'/>
&nbsp;<%=i18nRoleName%></font></td>
  </tr>
 
</core:ForEach><%-- roleName --%>

</dsp:getvalueof><%-- categories --%>
</dsp:getvalueof><%-- roleNames --%>
</dsp:getvalueof><%-- settings --%>

<tr><td>
<font class="smaller">
<i18n:message  bundleRef="settings" id="update01" key="update" />
<dsp:input type="SUBMIT" value="<%=update01%>" bean="CommunityRoleFormHandler.update" />
<i18n:message  bundleRef="settings" id="cancel01" key="cancel" />&nbsp;
<dsp:input type="SUBMIT" value="<%=cancel01%>" bean="CommunityRoleFormHandler.cancel"/> 
</font>
</td></tr>
</table>
</dsp:form>

</core:CreateUrl><%-- formURL          --%>
</dsp:getvalueof><%-- roleName         --%>
</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- user_id          --%>
</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_community_id --%>


</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) { %>
  <core:setTransactionRollbackOnly id="rollbackOnlyXA">
    <core:ifNot value="<%= rollbackOnlyXA.isSuccess() %>">
      The following exception was thrown:
      <pre>
       <%= rollbackOnlyXA.getException() %>
      </pre>
    </core:ifNot>
  </core:setTransactionRollbackOnly>
  <% } %>
</core:demarcateTransaction>

</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_role_editor.jsp#2 $$Change: 651448 $--%>
