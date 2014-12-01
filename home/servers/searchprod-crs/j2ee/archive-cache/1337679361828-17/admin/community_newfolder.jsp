<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:setFrameworkLocale />
<dsp:page>

<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>


<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/CommunityFolder"/>

<%  
    String mode = "3"; 
    String clearGif =  response.encodeURL("images/clear.gif");
    String formCommURLStr = null;
%>

<dsp:getvalueof id="theMode" param="mode" idtype="java.lang.String">
<% mode = theMode; %>
</dsp:getvalueof>

<core:CreateUrl id="formCommURL" url="/portal/admin/community.jsp">
 <core:UrlParam param="mode" value="<%=mode%>"/>
 <% formCommURLStr = formCommURL.getNewUrl(); %>
</core:CreateUrl>

 <dsp:form action="<%=formCommURLStr%>" method="POST" name="newfolder">
<%--
<input type="hidden" name="mode" paramvalue="mode">
--%>

    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-newcommunityfolder-newfolder"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller"><i18n:message key="admin-communities-infotext-newfolder-community"/>
	</td></tr></table>
<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<% String folderName = "";
   String folderId = "";
   int    level, pos=0;

%>

  <table width="100%" cellSpacing="0" cellPadding="4" border="0" bgcolor="#c9d1d7">
  <tr>
    <td align=left valign=top width="140">
	<font class="smaller"><i18n:message key="admin-newcommunityfolder-select"/><br>
	</td>
	<td>
<admin:GetFolderLevels id="items" folderType="community">
<font class="small">
<dsp:select bean="CommunityFolder.parentFolder">
<core:ForEach id="allCommFolders"
              values="<%= items.getItemAndLevel() %>"
              castClass="atg.portal.admin.ItemAndLevel"
              elementId="CommFolderItem">

<%
folderName = ((atg.portal.framework.folder.Folder)CommFolderItem.getItem()).getName();
folderId     = ((atg.portal.framework.folder.Folder) CommFolderItem.getItem()).getId();
level        = CommFolderItem.getLevel();
String spacer = "";
int i=0;
for(i=0; i<level;i++) {
  spacer += "&nbsp;&nbsp;";
}
%>

<dsp:option value="<%= folderId %>"/><%= spacer%><%= folderName %>
</core:ForEach>
</dsp:select>
</font>
</admin:GetFolderLevels>
</td></tr>

<tr><td>
<font class="smaller"><i18n:message key="admin-community-new-folder"/>
</td>
<td>
<dsp:input type="text" bean="CommunityFolder.folderName" value=""/>
</td></tr>


<tr><td><font class="smaller"><i18n:message key="admin-community-url"/>
</td>
<td>
<dsp:input type="text" bean="CommunityFolder.folderURL" value=""/>&nbsp;&nbsp;<font class="smaller"><i18n:message key="admin-newcommunityfolder-guidelines"/><br>
</td></tr>

<tr><td>&nbsp;</td><td>
<dsp:input type="hidden" bean="CommunityFolder.SuccessURL" value="community.jsp?mode=3"/>
<dsp:input type="hidden" bean="CommunityFolder.cancelURL" value="community.jsp?mode=3"/>
<dsp:input type="hidden" bean="CommunityFolder.FailureURL" value="community.jsp?mode=3"/>

<i18n:message id="done10" key="save" /><dsp:input type="submit" bean="CommunityFolder.create" value="<%= done10 %>" submitvalue="success" name="OK"/>

<br>&nbsp;
</td></tr>

</table>

</dsp:form>
</admin:InitializeAdminEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_newfolder.jsp#2 $$Change: 651448 $--%>
