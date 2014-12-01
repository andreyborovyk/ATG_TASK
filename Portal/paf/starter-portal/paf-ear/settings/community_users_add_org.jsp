<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,user-manager">

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>

<i18n:message id="i18n_imageroot" key="imageroot"/>

<i18n:message id="i18n_community_members_addremoveorg" key="community_members_addremoveorg"/>
<i18n:message id="i18n_community_members_individuals" key="community_members_individuals"/>

<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">
<dsp:getvalueof id="userType"         idtype="java.lang.String" param="roleName">

<dsp:setvalue bean="CommunityPrincipalFormHandler.id" value="<%=dsp_community_id%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.accessType" value="<%=userType%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.principalType" value="organization"/>

<dsp:form action="community_users.jsp" method="POST" name="add" synchronized="/atg/portal/admin/CommunityPrincipalFormHandler">
  	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader"> 
<% if(userType.equals("guest")) { %>
 <font class="pageheader"> <i18n:message key="community_organization_add_header_guest"/>
<% } else if(userType.equals("member")) { %>
 <font class="pageheader"><i18n:message key="community_organization_add_header_member"/>
<% } else if(userType.equals("leader")) { %>
 <font class="pageheader"><i18n:message key="community_organization_add_header_leader"/>
<% } %>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<% if(userType.equals("guest")) { %>
 <font class="smaller"><i18n:message key="community_organization_add_helpertext_guest"/>
<% } else if(userType.equals("member")) { %>
 <font class="smaller"><i18n:message key="community_organization_add_helpertext_member"/>
<% } else if(userType.equals("leader")) { %>
 <font class="smaller"><i18n:message key="community_organization_add_helpertext_leader"/>
<% } %>
</td></tr></table>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>

<admin:GetUsers id="users">
<admin:GetProfileItemsAndLevels id="organizations">
 <%
   String clearGif = response.encodeURL("images/clear.gif");
   String indent = "";
   int boxCount = 0;
   String bgcolor       = "ffffff";
   String indentcolor       = "cccccc";
 %>

<table border=0 cellpadding=0 cellspacing=0 width="100%">
<tr>
<td NOWRAP>
<font size="-2" color="#D7DEE5"><nobr><i18n:message key="community_members_addidividuals_js_select_all"/>&nbsp;</nobr></font>
</td>
<td colspan="2"></td>
</tr>


<tr>
<td align="center"><font class="smaller"  style="text-decoration:none;" color="#000000"><nobr>
<script language="Javascript">

var toggle = 1;
function all(toggleB) {
 formRef = eval( "document.forms['add']"); 
 if ( formRef != null ) {
  i = 0;
  itemsFound = 0;
  itemNames = "";
  currUserId  = "";
  prevUserId  = "";
  while ( i < formRef.elements.length ) {
    if( ( formRef.elements[i].name.indexOf("principalIds") > -1) && (  formRef.elements[i].name.indexOf("_D") == -1) ) {
      if (toggle == 1) {
           formRef.elements[i].checked = true;
      } else {
           formRef.elements[i].checked = false;
      }
    }
   i++;
  }
  toggle = ( toggle == 1) ? 0 : 1 ;
 }
}

document.write("<a href='Javascript:all(-1)' taget='_self' style='text-decoration:none'><i18n:message key='community_members_addidividuals_js_select_all'/></a> ");

</script>&nbsp;</nobr></font></td>
<td width="90%"><font class="smaller"><b><i18n:message key="community_members_organization"/></b></font></td>
</tr>

<tr>
<td colspan=3 bgcolor="#666666"><img src="<%=clearGif%>"  width="1" height="1" border="0"></td>
</tr>

<% 
  Collection bigList = organizations.getItemList();
  Collection hasList =  users.getOrganizationsForRole((String) userType,(String) dsp_community_id );
  bigList.removeAll(hasList);

%>

  <core:ForEach id="pagePos"
                values="<%=bigList /*organizations.getItemList()*/%>"
                castClass="atg.portal.admin.ItemAndLevel"
                elementId="organization1">
<% 

   bgcolor = ( bgcolor.equals("ffffff") ) ? "dddddd" : "ffffff";
  indentcolor  = ( indentcolor.equals("cccccc") ) ? "eeeeee" : "cccccc";

%>
<tr bgcolor="<%="#"+bgcolor%>">


<% if (!((atg.userdirectory.Organization)organization1.getItem()).hasAssignedRole(adminEnv.getCommunity().getRole(userType)) ) {%>
<td align="center"><font class="smaller"><dsp:input type="checkbox"  bean="CommunityPrincipalFormHandler.principalIds" value="<%=((atg.userdirectory.Organization)organization1.getItem()).getPrimaryKey()%>" checked="false"/></font></td>
<% } else {%>

<td align="center">&nbsp;<font class="smaller" color="#666666"><i18n:message key="community_org_is_already_in_list"/></font></td>
<% } %>

 <%
       int level = organization1.getLevel();
       indent = "";
       for(int i=0; i<(level*2);i++) {
        indent += "&nbsp;&nbsp;" ;
       }
  %>

<td nowrap><font class="smaller">
<font color='<%="#"+indentcolor%>'><%=indent%>&nbsp;&nbsp;</font><%= ((atg.userdirectory.Organization)organization1.getItem()).getName()  %></font></td>
<td width="50%"><font class="smaller">
    <core:createUrl  id="organizationURL" url="community_users.jsp">
      <core:UrlParam param="mode" value='<%=request.getParameter("mode") + "b" %>'/>
      <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
      <core:UrlParam param="organization_id" value="<%=((atg.userdirectory.Organization)organization1.getItem()).getPrimaryKey() %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>

<core:If value="<%=(users.getUsersCountForOrganization((String)(((atg.userdirectory.Organization)(organization1.getItem())).getPrimaryKey())) > 0)%>">
&nbsp;&nbsp;&nbsp;<dsp:a href="<%=(String) organizationURL.getNewUrl()%>"><%= users.getUsersCountForOrganization((String)((((atg.userdirectory.Organization)organization1.getItem()).getPrimaryKey()))) %>&nbsp;<%= i18n_community_members_individuals %></dsp:a>
</core:If>

<% if(users.getUsersCountForOrganization((String)(((atg.userdirectory.Organization)organization1.getItem()).getPrimaryKey())) == 0) { %>
&nbsp;&nbsp;&nbsp;<%= users.getUsersCountForOrganization((String)(((atg.userdirectory.Organization)(organization1.getItem())).getPrimaryKey())) %>&nbsp;<%= i18n_community_members_individuals %>
<% } %>

</core:createUrl>

</font></td>

</tr>

</core:ForEach>

<tr>
 <td colspan=3 bgcolor="#666666"><img src="<%=clearGif%>"  width="1" height="1" border="0"></td>
</tr>
</table>

</admin:GetProfileItemsAndLevels>
</admin:GetUsers>


<core:CreateUrl id="CusersURLsuccess"       url="/portal/settings/community_users.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=dsp_page_url%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=dsp_community_id%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=dsp_page_id%>'/>
  <core:UrlParam param="mode"               value='<%=mode.substring(0,1)%>'/>

   <dsp:input type="hidden"  bean="CommunityPrincipalFormHandler.successURL" value="<%=CusersURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="CommunityPrincipalFormHandler.failureURL" value="<%=CusersURLsuccess.getNewUrl()%>"/>

</core:CreateUrl>


<font class="small"><br>
<i18n:message id="done01" key="add_selected" />
<dsp:input type="SUBMIT"  bean="CommunityPrincipalFormHandler.assignPrincipals" value="<%= done01 %>" />
<i18n:message id="cancel01" key="reset" />&nbsp;
<input type="RESET" value="<%= cancel01 %>"/>
</font>

</dsp:form>
</td></tr></table>
<br>
</dsp:getvalueof><%-- roleName         --%>
</dsp:getvalueof><%-- mode             --%>
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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_add_org.jsp#2 $$Change: 651448 $--%>
