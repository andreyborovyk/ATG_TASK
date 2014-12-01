<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />
<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager"/>
<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />
<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<%
   String mode = "";
   if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to gears display page
    }

%>

<i18n:message key="imageroot" id="i18n_imageroot"/>
<i18n:message id="select_to_add" key="community_gears_linkselect"/>
<i18n:message id="select_to_remove" key="community_gears_linkremove"/>
	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
  <i18n:message key="community_gears_add_shared_page_header_param">
   <i18n:messageArg value="<%= adminEnv.getCommunity().getName(response.getLocale()) %>"/>
 </i18n:message>
 </td></tr></table>
  </td></tr></table>
  
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
    <i18n:message key="community_gears_add_shared_helptext"/>
</td></tr></table> 

<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%">
<tr>
<!-- List of available gears -->
<td valign="top" width="50%"><font class="smaller"><i18n:message key="community_gears_list_of_shared"/><br>
<img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="1" border="0"><br>
<%-- Show the community folders and the communities within them and the shared gear instances within them. --%>
           
            <% String folderName = "";
               String folderId = "";
               int    level, spos=0;
               java.util.Collection communities; 
               String indentUnit = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
               String indentSpace = "";

               String rowHighlight = "dddddd";
               String colspan = "2";
               rowHighlight = ( rowHighlight.equals("dddddd") ) ?  "ffffff" : "dddddd";
            %>

       <admin:GetFolderLevels id="items" folderType="community">



           <core:ForEach id="allCommFolders"
                values="<%= items.getItemAndLevel() %>"
                castClass="atg.portal.admin.ItemAndLevel"
                elementId="CommFolderItem">
                <%
                  indentSpace  = "";
                  folderName   = ((atg.portal.framework.folder.Folder) CommFolderItem.getItem()).getName();
                  folderId     = ((atg.portal.framework.folder.Folder) CommFolderItem.getItem()).getId();
                  level        = CommFolderItem.getLevel();
                  communities  = ((atg.portal.framework.folder.CommunityFolder) CommFolderItem.getItem()).getCommunitySet(atg.portal.framework.Comparators.getCommunityComparator());  
                                  
                  for(int i=0; i<level;i++) {
                    indentSpace += indentUnit ;
                  }
                %>

<table border="0" cellpadding="1" cellspacing="0" width="100%"> 
<tr><td colspan="<%=colspan%>" bgcolor="#d4d4d4" nowrap><font class="smaller_bold">&nbsp;<%= indentSpace + folderName %></font></td></tr>

               <core:ForEach id="childCommunities"
                  values="<%= communities %>"
                  castClass="atg.portal.framework.Community"
                  elementId="CommunityItem">


            <%
                     rowHighlight = ( rowHighlight.equals("dddddd") ) ?  "ffffff" : "dddddd";
            %>

                    <core:If value="<%= CommunityItem.getGearsShared(atg.portal.framework.Comparators.getGearComparator()).size()==0 %>">

<tr bgcolor="<%="#"+rowHighlight%>"><td><font class="smaller"><%= indentSpace+indentUnit + CommunityItem.getName(response.getLocale())%>&nbsp;&nbsp;</font></td><td><font class="smaller"><i18n:message key="community_pages_noshared_gears"/></font></td></tr>

                    </core:If>
                    
                    <core:If value="<%= CommunityItem.getGearsShared().size()>0 %>">

<tr bgcolor="<%="#"+rowHighlight%>"><td colspan="<%=colspan%>"><font class="smaller"><%= indentSpace+indentUnit + CommunityItem.getName(response.getLocale())%></font></td></tr>


                    <%-- Show the shared gears in the community.  --%>                    
                     <core:ForEach id="childGearsShared"
                            values="<%= CommunityItem.getGearsShared(atg.portal.framework.Comparators.getGearComparator()) %>"
                            castClass="atg.portal.framework.Gear"
                            elementId="SharedGearItem">

            <%
                     rowHighlight = ( rowHighlight.equals("dddddd") ) ?  "ffffff" : "dddddd";
            %>

<tr bgcolor="<%="#"+rowHighlight%>">
<td><font class="smaller" nowrap><nobr><%= indentSpace+indentUnit + indentUnit%><%= SharedGearItem.getName(response.getLocale())%></font></td>
<td width="20%"><font class="smaller">
<core:IfNot value="<%=SharedGearItem.getParentCommunity().getId().equals(dsp_community_id)%>">
<dsp:a href="community_gears.jsp">
 <dsp:param name="mode" value="11"/>
 <dsp:param name="paf_gear_id" value="<%= SharedGearItem.getId()%>"/>
 <dsp:param name="paf_page_id" value="<%=dsp_page_id %>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url %>"/>
 <dsp:param name="paf_community_id" value="<%= dsp_community_id %>"/>
 <dsp:param name="paf_gear_src" value="remote"/><%=select_to_add%></dsp:a>
 </core:IfNot>&nbsp;
 </font></td>
</tr>
 
                      
                     </core:ForEach><%-- shared gear --%>


<tr bgcolor="#ffffff"><td colspan="<%=colspan%>"><img src="<%= response.encodeURL("images/clear.gif")%>" height="10" width="1" border="0"></td></tr>                    

                   </core:If>

                </core:ForEach><%--  child community --%>



</table>
<img src='<%= response.encodeURL("images/clear.gif")%>' height="10" width="1" border="0"><br>
           </core:ForEach><%--  community folders id="allCommFolders" --%>

        </admin:GetFolderLevels>


      </td>
<td width="1"><img src='<%=response.encodeURL("images/clear.gif")%>' height="1" width="1"></td>

<td valign="_top" width="50%"><font class="smaller">
<i18n:message key="community_gears_current_gears_in_param">
  <i18n:messageArg value="<%=adminEnv.getCommunity().getName(response.getLocale())%>"/>
</i18n:message><br>
<img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="1" border="0"><br>

<%

 String deleteMode = "10";
 String returnMode = "3";
 colspan = "3"; // declared earlier
 String titleSize = "smaller";
 boolean showConfigureLink = false;

%>
<%@include file="fragments/community_gears_listing.jspf"%>

</font>
  

      </td>
    </tr>
  </table>

</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>


</admin:InitializeAdminEnvironment>
 
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_add_shared.jsp#2 $$Change: 651448 $--%>
