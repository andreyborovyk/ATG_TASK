<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %> 
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale/>
<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />
<admin:InitializeAdminEnvironment id="adminEnv">


<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<dsp:setvalue bean="GearFormHandler.communityId" value="<%= dsp_community_id %>"/>  

<i18n:message key="imageroot" id="i18n_imageroot"/>
<i18n:message id="select_to_add" key="community_gears_linkselect"/>

	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
  <font class="pageheader">
  <i18n:message key="community_gears_add_available_page_header_param">
   <i18n:messageArg value="<%= adminEnv.getCommunity().getName(response.getLocale()) %>"/>
  </i18n:message>
	</td></tr></table>
    </td></tr></table>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
	 <i18n:message key="community_gears_add_helpertext"/>
</td></tr></table>
<!-- List of available gears -->
<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%">
<tr>
<td valign="top"><font class="helpertext"><i18n:message key="community_gears_list_of_available"/><BR>
<img src='<%=response.encodeURL("images/clear.gif")%>' height="5" width="1"><BR>

<%-- Show the gear definition folders and the gear definitions within them. --%>
</font><font class="smaller">
         <admin:GetAllItems id="items">

           <% 
               String rowHighlight = "dddddd";
               String colspan = "2";
               rowHighlight = ( rowHighlight.equals("dddddd") ) ?  "ffffff" : "dddddd";
            %> 
              <core:ForEach id="gearFolders"
                        values="<%= items.getGearDefinitionFolders(adminEnv.getCommunity(),atg.portal.framework.Comparators.getFolderComparator()) %>"
                        castClass="atg.portal.framework.folder.GearDefinitionFolder"
                        elementId="gearFolder">

        <table border="0" cellpadding="1" cellspacing="0" width="100%"> 
                <tr><td colspan="<%=colspan%>" bgcolor="#cccccc" nowrap><font class="smaller_bold">&nbsp;<%= gearFolder.getName()%></font></td></tr>
              <core:ForEach id="gearsInFolder"
                            values="<%= gearFolder.getGearDefinitionSet(atg.portal.framework.Comparators.getGearDefinitionComparator()) %>"
                            castClass="atg.portal.framework.GearDefinition"
                            elementId="gearDefn">
              
            <%
               rowHighlight = ( rowHighlight.equals("dddddd") ) ?  "ffffff" : "dddddd";
            %>
            <%-- Show gear definitions that have hide property set to false. --%>
            <core:IfNot value='<%= gearDefn.getHide()%>'>
             <tr bgcolor="<%="#"+rowHighlight%>">
                 <td width="75%" 	bgcolor="<%="#"+rowHighlight%>"><font class="smaller">&nbsp;<%= gearDefn.getName(response.getLocale())%></font></td>
                 <td width="25%" NOWRAP><font class="small">&nbsp;&nbsp;


<dsp:a href="community_gears.jsp">
 <dsp:param name="mode" value="6"/>
 <dsp:param name="paf_page_id"  value="<%=dsp_page_id  %>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url %>"/>
 <dsp:param name="paf_community_id" value="<%=dsp_community_id %>"/>
 <dsp:param name="paf_gear_id" value="<%= gearDefn.getId()%>"/>
  <%=select_to_add%>
</dsp:a>
                  </font></td> 
                </tr>
             </core:IfNot>
 
             <% if ( rowHighlight.equals("ffffff")) { %>
             <tr><td  colspan="<%=colspan%>"  bgcolor="dddddd"><img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"></td></tr>
             <% } %>

            </core:ForEach>
                   
        </table>
		<img src='<%= response.encodeURL("images/clear.gif")%>' height="10" width="1" border="0"><br>
     

          </core:ForEach>     
        </admin:GetAllItems> 

</font></td>

<%--  spacer cell --%>

<td><img src='<%=response.encodeURL("images/clear.gif")%>' height="1" width="1"></td>

<%--  next column  current list of assigned gears --%>

<td valign="top" width="50%"><font class="smaller">
 <i18n:message key="community_gears_current_gears_in_param">
  <i18n:messageArg value="<%= adminEnv.getCommunity().getName(response.getLocale()) %>" />
 </i18n:message><br>
 <img src='<%=response.encodeURL("images/clear.gif")%>' height="5" width="1"><BR>
</font><font class="smaller">

<%

 java.util.Set gears=null; 

 String rowHighlight = "dddddd";
 String colspan = "3";

 String deleteMode = "9";
 String returnMode = "2";
 String titleSize = "smaller";
 boolean showConfigureLink = false;

%>

<%@include file="fragments/community_gears_listing.jspf"%>

</font>
  
</td>
</tr>
</table>



</dsp:getvalueof><%-- dsp_community_id --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_page_id      --%>

</admin:InitializeAdminEnvironment>
</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_add.jsp#2 $$Change: 651448 $--%>
