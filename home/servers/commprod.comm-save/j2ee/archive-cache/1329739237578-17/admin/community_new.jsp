<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:setFrameworkLocale />
<dsp:page>

<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>

<%
  if (!atg.portal.framework.LicenseUtils.allowCommunityCreation(Utilities.getPortal("default"))) {
%>
  <font class="error">
    <img src='<%= response.encodeURL("images/error.gif")%>'>&nbsp;&nbsp;<i18n:message key="admin-community-no-more"/>
  </font>
<%
    return;
  }
%>

<admin:InitializeAdminEnvironment id="adminEnv">

<core:CreateUrl id="formCommURL" url="/portal/admin/community.jsp">
 <core:UrlParam param="mode" value='<%=request.getParameter("mode")%>'/>
<dsp:form action="<%=formCommURL.getNewUrl()%>"  method="POST" name="newcommunity" synchronized="/atg/portal/admin/CommunityFormHandler">
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-community-new"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller"><i18n:message key="admin-communities-infotext-new-community"/>
	</td></tr></table>

  <img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>

	<table cellpadding="6" cellspacing="0" border="0" bgcolor="#c9d1d7" width="100%"><tr><td>
<font class="smaller">

<%
   String folderName = "";
   String folderId = "";
   int    level, pos=0, i=0;
   String spacers = "";
   String clearGif = response.encodeURL("images/clear.gif");
%>

  <table cellSpacing="0" cellPadding="3" border="0">
  <admin:GetFolderLevels id="items" folderType="community">
  <tr>
    <td align="left" valign="top" NOWRAP>
    <font class="smaller"><nobr>
    <i18n:message key="admin-community-folders"/>
    </font><br><font class="small"><dsp:select bean="CommunityFormHandler.parentFolder">
    <core:ForEach id="allCommFolders"
              values="<%= items.getItemAndLevel() %>"
              castClass="atg.portal.admin.ItemAndLevel"
              elementId="CommFolderItem">
<%
 folderName   = ((atg.portal.framework.folder.CommunityFolder)CommFolderItem.getItem()).getName(response.getLocale());
 folderId     = ((atg.portal.framework.folder.CommunityFolder)CommFolderItem.getItem()).getId();
 level        = CommFolderItem.getLevel();
 spacers      = "";
 i=0;
 for(i=0; i<level;i++) {
  spacers += "&nbsp;&nbsp;";
 }
%>
<%-- the commented out lines were there to block the root folder, which there's no reason to block --%>
<%-- <core:IfNotNull value='<%=CommFolderItem.getItem().getPropertyValue("parent")%>'> --%>
  <dsp:option value="<%= folderId %>" selected="<%=false%>"/><%=spacers%><%= folderName %>
<%-- </core:IfNotNull> --%>

     </core:ForEach>
     </dsp:select></td>
    </tr>
    </admin:GetFolderLevels>
 
  <tr>
   <td align="left"><font class="smaller"><i18n:message key="admin-community-name"/></font><br><font class="smaller"><dsp:input bean="CommunityFormHandler.name"/></font></td>
  </tr>

  <tr>
   <td align="left"><font class="smaller"><i18n:message key="admin-community-url"/></font><br><font class="smaller"><dsp:input bean="CommunityFormHandler.url"/>&nbsp;&nbsp;<i18n:message key="admin-community-url-example"/></font></td>
  </tr>
  
  
      <tr>
    
        <td>
          <table border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td valign="top">
<dsp:getvalueof id="currStatus"  bean="CommunityFormHandler.active">
              <font class="smaller"><i18n:message key="community_settings_status_title"/></font><br><font class="small"><dsp:select bean="CommunityFormHandler.active">
<dsp:option value="true"/><i18n:message key="community_settings_status_online"/>
 <core:IfNull value="<%=currStatus%>">
<dsp:option value="false" selected="true" /><i18n:message key="community_settings_status_offline"/>
</core:IfNull>
<core:IfNotNull value="<%=currStatus%>">
 <dsp:option value="false"/><i18n:message key="community_settings_status_offline"/>
</core:IfNotNull>
</dsp:select></font></td>
</dsp:getvalueof>
              <td>&nbsp;&nbsp;</td>
              <td valign="middle" ><font class="adminbody">
              <br><i18n:message key="community_settings_status_notation"/><br></font>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    


<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String dsp_community_id = "";
  if(cm != null) {
    dsp_community_id = cm.getId();
  }
%> 
<%-- <admin:GetCommunityDetails id="communityItem" communityType="community" itemId="<%=dsp_community_id%>"> --%>


    <%-- DESCRIPTION --%>
       <tr>
    
         <td><font class="smaller"><i18n:message key="community_settings_description"/></font><br><font class="smaller"><dsp:textarea bean="CommunityFormHandler.description" cols="38" rows="3" /></font></td>
       </tr>



    <%-- MEMBERSHIP REQUEST --%>

       <tr>
        <%-- SET DEFAULT membershipRequest  value --%>
        <dsp:getvalueof id="memRequest" bean="CommunityFormHandler.membershipRequest">
          <core:IfNull value="<%=memRequest%>">
            <dsp:setvalue bean="CommunityFormHandler.membershipRequest" value="disallow"/>
          </core:IfNull>
        </dsp:getvalueof>
         <td>
           <font class="smaller"><i18n:message key="community_settings_requestmembership_title"/></font><br>
           <table cellpadding="0" cellspacing="0" border="0">
             <tr>
               <td><img src="<%=clearGif%>" width="20" height="1"></td>
               <td><img src="<%=clearGif%>" width="20" height="1"></td>
             </tr>
             <tr>
               <td><dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value="automatic"/></td>
               <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_autoexcept"/></font></td>
             </tr>
             <tr>
               <td><dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value="allow"/></td>
               <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_notify"/></font></td>
             </tr>
             <tr>
               <td><dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value="disallow" /></td>
               <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_noexcept"/></font></td>
             </tr>
           </table>
           </font>
         </td>

       </tr>
     

      <%-- TEMPLATES & STYLES  --%>


       <admin:GetAllItems id="items">
       <tr>
    
         <td>

<%





%>
           <font class="smaller"><i18n:message key="community_settings_pagetemplate_title"/></font><br><font class="small"><dsp:select bean="CommunityFormHandler.pageTemplate">
<core:ForEach id="allTemplates"
              values="<%= items.getPageTemplates(atg.portal.framework.Comparators.getPageTemplateComparator()) %>"
              castClass="atg.portal.framework.PageTemplate"
              elementId="templateItem">
<dsp:option value="<%=  templateItem.getId() %>"/>
<%=  templateItem.getName() %>
</core:ForEach>
</dsp:select></font>
              <br>
        <img src="<%=clearGif%>" border="0" height="6" width="1"><br>
        <font class="smaller"><i18n:message key="community_settings_style_title"/></font><br><font class="small">

<dsp:select  bean="CommunityFormHandler.style">
<% boolean noStyle = false; %>

<dsp:getvalueof id="crntStyle" bean="CommunityFormHandler.style">
  <core:ifNull value="<%= crntStyle %>">
    <% noStyle = true; %>  
  </core:ifNull>

<%--
<dsp:option value="" selected="<%= noStyle %>"/><i18n:message key="community-nostylesheet"/>
--%>
<core:ForEach id="allStyles"
              values="<%= items.getStyles(atg.portal.framework.Comparators.getStyleComparator()) %>"
              castClass="atg.portal.framework.Style"
              elementId="styleItem">
<dsp:option value="<%=  styleItem.getId() %>"/><%= styleItem.getName() %>
</core:ForEach>
</dsp:getvalueof>
</dsp:select></font>
<br>
        <img src="<%=clearGif%>" border="0" height="6" width="1"><br>
                              
<font class="smaller"><i18n:message key="community_settings_geartemplate_title"/></font><br><font class="small"><dsp:select bean="CommunityFormHandler.gearTitleTemplate">
<core:ForEach id="allTemplates"
              values="<%= items.getGearTitleTemplates(atg.portal.framework.Comparators.getGearTitleTemplateComparator()) %>"
              castClass="atg.portal.framework.GearTitleTemplate"
              elementId="templateItem">
<dsp:option value="<%=  templateItem.getId() %>"/>

<%=  templateItem.getName() %>

</core:ForEach>
</dsp:select>
</font></td>
</tr>

  </admin:GetAllItems>



      <tr>

        <%-- SET DEFAULT personalization level --%>
        <dsp:getvalueof id="customizeLevel" bean="CommunityFormHandler.personalization">
          <core:IfNull value="<%=customizeLevel%>">
            <dsp:setvalue bean="CommunityFormHandler.personalization" value="none"/>
          </core:IfNull>
        </dsp:getvalueof>

        <td NOWRAP><font class="adminbody">
        <i18n:message key="community_settings_personalization_title"/><br>

        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="community"/>
        <i18n:message key="community_settings_personalization_membersandcreate"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="page"/>
        <i18n:message key="community_settings_personalization_membersnocreate"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="none"/>
        <i18n:message key="community_settings_personalization_noallow"/><br>
                <br>

        </font></td>
      </tr>

      <tr>

        <%-- SET DEFAULT accessLevel  value --%>
        <%-- I would start with high restriction as to allow for admin to finish configuring community --%>
        <dsp:getvalueof id="accessLevel" bean="CommunityFormHandler.accessLevel">
          <core:IfNull value="<%=accessLevel%>">
            <dsp:setvalue bean="CommunityFormHandler.accessLevel" value="4"/>
          </core:IfNull>
        </dsp:getvalueof>
        <td><font class="adminbody">
       <i18n:message key="community_settings_access_title"/><br>

        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="0" />
        <i18n:message key="community_settings_access_any"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="1"/>
        <i18n:message key="community_settings_access_all"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="2"/>
        <i18n:message key="community_settings_access_guestandmeberandleader"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="3"/>
        <i18n:message key="community_settings_access_memberandleader"/>

        <br>
        &nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="4"/>
        <i18n:message key="community_settings_access_leader"/><br>
                <br>

        </font></td>
      </tr>

  <tr>
  <td>
<dsp:input type="hidden" priority="<%=5%>" bean="CommunityFormHandler.successURL" value="./community.jsp"/>
<dsp:input type="hidden" priority="<%=5%>" bean="CommunityFormHandler.failureURL" value="./community.jsp?mode=2"/>
<i18n:message id="save04" key="save" /><dsp:input type="submit" bean="CommunityFormHandler.create" value="<%= save04 %>"/>
<i18n:message id="reset04" key="reset" /><dsp:input type="reset" bean="CommunityFormHandler.reset" value="<%= reset04 %>"/>
<%--
<i18n:message id="cancel05" key="cancel" /><dsp:input type="submit" bean="CommunityFormHandler.cancel" value="<%= cancel05 %>"/>
--%>
</center>
</td>
</tr>

</table>


</td></tr></table>
<%-- </admin:GetCommunityDetails> --%>


</dsp:form>
</core:CreateUrl>
</font>

</admin:InitializeAdminEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_new.jsp#2 $$Change: 651448 $--%>
