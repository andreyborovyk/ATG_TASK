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

<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">
<dsp:getvalueof id="role"             idtype="java.lang.String" param="roleName">

<% 
atg.portal.admin.PagingFormHandler pagingFH =
   (atg.portal.admin.PagingFormHandler) atg.servlet.ServletUtil.getDynamoRequest(request).resolveName("/atg/portal/admin/PagingFormHandler");
%>
<dsp:importbean bean="/atg/portal/admin/PagingFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>
<i18n:message id="i18n_imageroot" key="imageroot"/>



<%-- Set the properties for next page --%>
<dsp:setvalue bean="PagingFormHandler.currentPositionInOrganizationMembers"  value="0" />
<dsp:setvalue bean="PagingFormHandler.showAtATime"  value="25" />

<dsp:setvalue bean="CommunityPrincipalFormHandler.id" value="<%=dsp_community_id%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.accessType" value="<%=role%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.principalType" value="organization"/>
<%

  String userType = role.toString(); // pagingFH.getUserType(); 
  String alphabet = pagingFH.getUserAlphabetVar();
%>

<i18n:message id="i18n_community_members_addremoveorg" key="community_members_addremoveorg"/>
<i18n:message id="i18n_community_members_individuals" key="community_members_individuals"/>
<i18n:message id="i18n_bold" key="html-bold"/>
<i18n:message id="i18n_end_bold" key="html-end-bold"/>

<admin:GetUsers id="users">
<dsp:form action="community_users.jsp" method="POST" name="remove" synchronized="/atg/portal/admin/CommunityPrincipalFormHandler">

  	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>

<core:exclusiveIf>
  <core:if value='<%= userType.equals("guest") %>'>
    <font class="pageheader"><i18n:message key="community_members_guest_organizations"/></font><br>
  </core:if>
  <core:if value='<%= userType.equals("member") %>'>
    <font class="pageheader"><i18n:message key="community_members_member_organizations"/></font><br>
  </core:if>
  <core:if value='<%= userType.equals("leader") %>'>
    <font class="pageheader"><i18n:message key="community_members_leader_organizations"/></font><br>
  </core:if>
</core:exclusiveIf>

</td></tr></table>
</td></tr></table>

 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<core:exclusiveIf>
  <core:if value='<%= userType.equals("guest") %>'>
    <font class="smaller"><i18n:message key="community_members_guest_organizations_helpertext"/>
  </core:if>
  <core:if value='<%= userType.equals("member")%>'>
    <font class="smaller"><i18n:message key="community_members_member_organizations_helpertext"/>
  </core:if>
  <core:if value='<%= userType.equals("leader")%>'>
    <font class="smaller"><i18n:message key="community_members_leader_organizations_helpertext"/>
  </core:if>
</core:exclusiveIf>

</td></tr></table>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<font class="smaller">
<img src="<%= response.encodeURL("images/clear.gif")%>" height="5" width="1" border="0"><br>
 <% if(0 == users.getOrganizationsForRole((String) userType ,(String) dsp_community_id).size()) { %>
   <i18n:message key="community_members_no_organizations"/>
 <% } %>

 <% if(0 != (users.getOrganizationsForRole((String) userType ,(String) dsp_community_id)).size())  { %>




<table border=0 cellpadding=0 cellspacing=0 width="100%">
<tr>
<td NOWRAP>

<font size="-2" color="#D7DEE5"><nobr><i18n:message key="community_members_addidividuals_js_select_all"/>&nbsp;</nobr>

</td>
<td colspan="2"></td>
</tr>

<tr>
 <td width="30"><font class="smaller"><nobr>
<script language="Javascript">

var toggle = 1;
function all(toggleB) {
 formRef = eval( "document.forms['remove']"); 
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

document.write("<a href='Javascript:all(-1)' taget='_self' style='text-decoration:none;color:0000ff'><i18n:message key='community_members_addidividuals_js_select_all'/></a>");

</script>
</nobr></font></td>
  <td><font class="subheader"><i18n:message key="community_members_organization"/></font></td>
  <td width="90%"><font class="adminbody">&nbsp;</font></td>
</tr>

<tr>
<td bgcolor="#666666" colspan=3><img src="images/clear.gif" height="1" width="250"></td>
</tr>
<%
  String bgcolorA = "dddddd";
  int boxCount = 0;
%>
  <core:ForEach id="org"
   values="<%= users.getOrganizationsForRole((String) userType,(String) dsp_community_id ) %>"
   castClass="atg.userdirectory.Organization"
   elementId="organization">
   <%
     bgcolorA = (  bgcolorA == "dddddd" ) ? "ffffff" : "dddddd";
   %> 
<tr bgcolor="#<%=bgcolorA%>">

<td align="center">
<font  class="smaller"><dsp:input type="checkbox" checked="false" bean="CommunityPrincipalFormHandler.principalIds" value="<%= organization.getPrimaryKey() %>"/></font></td>

<td NOWRAP><nobr><font class="adminbody"><%= organization.getName()%>&nbsp;&nbsp;&nbsp;</font></nobr></td>
<td><font class="smaller">
    <core:createUrl  id="organizationURL" url="community_users.jsp">
      <core:UrlParam param="mode" value='<%=request.getParameter("mode") + "b" %>'/>
      <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
      <core:UrlParam param="organization_id" value="<%= organization.getPrimaryKey() %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>

<core:If value="<%=(users.getUsersCountForOrganization((String)organization.getPrimaryKey()) > 0)%>">
&nbsp;&nbsp;&nbsp;<dsp:a href="<%=(String) organizationURL.getNewUrl()%>"><%= users.getUsersCountForOrganization((String)organization.getPrimaryKey()) %>&nbsp;<%= i18n_community_members_individuals %></dsp:a>
</core:If>

<% if(users.getUsersCountForOrganization((String)organization.getPrimaryKey()) == 0) { %>
&nbsp;&nbsp;&nbsp;<%= users.getUsersCountForOrganization((String)organization.getPrimaryKey()) %>&nbsp;<%= i18n_community_members_individuals %>
<% } %>

</core:createUrl>

</font></td>

</tr>

</core:ForEach>

<tr>
<td bgcolor="#666666" colspan=3><img src="images/clear.gif" height="1" width="250"></td>
</tr>
</table>


<core:CreateUrl id="CusersURLsuccess"       url="/portal/settings/community_users.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=dsp_page_url%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=dsp_community_id%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=dsp_page_id%>'/>
  <core:UrlParam param="mode"               value='<%=mode%>'/>

   <dsp:input type="hidden"  bean="CommunityPrincipalFormHandler.successURL" value="<%=CusersURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="CommunityPrincipalFormHandler.failureURL" value="<%=CusersURLsuccess.getNewUrl()%>"/>

</core:CreateUrl>


<font class="small"><br>
 <i18n:message id="done02" key="remove_selected" />
 <dsp:input type="SUBMIT"  bean="CommunityPrincipalFormHandler.unassignPrincipals"     value="<%= done02 %>" />
 <i18n:message id="cancel02" key="reset" />&nbsp;
 <input bean="" type="RESET" value="<%= cancel02 %>"/>
</font>

<% } %>

</dsp:form>

</td></tr></table>
</admin:GetUsers>



</dsp:getvalueof><%-- roleName         --%>
</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- dsp_community_id --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_page_id      --%>




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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_org.jsp#2 $$Change: 651448 $--%>
