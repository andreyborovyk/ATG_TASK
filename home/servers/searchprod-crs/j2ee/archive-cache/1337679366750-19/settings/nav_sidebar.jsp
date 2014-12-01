<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:getvalueof id="thisNavHighlight" idtype="java.lang.String"      param="thisNavHighlight">
<dsp:getvalueof id="mode"             idtype="java.lang.String"      param="mode">
<dsp:getvalueof id="sideBarIndex"     idtype="java.lang.String"      param="sideBarIndex">
<dsp:getvalueof id="dhtmlFlag"        idtype="java.lang.String"      param="dhtmlFlag">
<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String"      param="paf_gear_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String"      param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String"      param="paf_community_id">

<%
 String CsettingsURLStr = null;
 String CusersURLStr = null;
 String CpagesURLStr = null;
 String CgearsURLStr = null;

 Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
 String communityId = null;
 if(cm != null) {
     communityId = cm.getId();
 }
 if ( dsp_community_id == null ) dsp_community_id = communityId;

 String clearGif =  response.encodeURL("images/clear.gif");
 String outputStr = "";

%>

<core:CreateUrl id="CsettingsURL"   url="/portal/settings/community_settings.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CsettingsURLStr = CsettingsURL.getNewUrl();%>
</core:CreateUrl>
    
<core:CreateUrl id="CusersURL"      url="/portal/settings/community_users.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CusersURLStr = CusersURL.getNewUrl();%>
</core:CreateUrl>

<core:CreateUrl id="CpagesURL"      url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CpagesURLStr = CpagesURL.getNewUrl();%>
</core:CreateUrl>
  
<core:CreateUrl id="CgearsURL"      url="/portal/settings/community_gears.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CgearsURLStr = CgearsURL.getNewUrl();%>
</core:CreateUrl>


<%-- sidebar links --%>
<%-- settings --%>
<i18n:message id="i18n_comm_settings"         key="nav-sidebar-community-settings"/>
<i18n:message id="i18n_comm_access"           key="nav-sidebar-community-access"/>
<i18n:message id="i18n_comm_advanced"         key="nav-sidebar-community-advanced"/>
<i18n:message id="i18n_about_settings"        key="nav-sidebar-about-settings"/>
<%-- users --%>
<i18n:message id="i18n_individual_members"    key="nav-sidebar-individual-members"/>
<i18n:message id="i18n_member_organizations"  key="nav-sidebar-member-organizations"/>
<i18n:message id="i18n_individual_guests"     key="nav-sidebar-individual-guests"/>
<i18n:message id="i18n_guest_organizations"   key="nav-sidebar-guest-organizations"/>
<i18n:message id="i18n_individual_leaders"    key="nav-sidebar-individual-leaders"/>
<i18n:message id="i18n_leader_organizations"  key="nav-sidebar-leader-organizations"/>
<i18n:message id="i18n_create_user"           key="nav-sidebar-create-user"/>
<i18n:message id="i18n_about_users"           key="nav-sidebar-about-users"/>
  <i18n:message id="i18n_add_members"         key="nav-sidebar-add-members"/>
  <i18n:message id="i18n_add_guests"          key="nav-sidebar-add-guests"/>
  <i18n:message id="i18n_add_leaders"         key="nav-sidebar-add-leaders"/>
  <i18n:message id="i18n_add_organizations"   key="nav-sidebar-add-organizations"/>
  <i18n:message id="i18n_membership_request"  key="nav-sidebar-membership-request"/>

<%-- pages --%>
<i18n:message id="i18n_current_pages"         key="nav-sidebar-current-pages"/>
<i18n:message id="i18n_create_new_pages"      key="nav-sidebar-create-new-page"/>
<i18n:message id="i18n_about_pages"           key="nav-sidebar-about-pages"/>
<%-- gears --%>
<i18n:message id="i18n_current_gears"         key="nav-sidebar-gears-current-instances"/>
<i18n:message id="i18n_add_gears"             key="nav-sidebar-gears-select-new"/>
<i18n:message id="i18n_add_shared_gears"      key="nav-sidebar-gears-select-shared"/>
<i18n:message id="i18n_about_gears"           key="nav-sidebar-about-gears"/>


<core:CreateUrl id="contentTargetUserURL" url="<%=adminEnv.getCreateUserURI()%>">
 <paf:encodeUrlParam param="successURL"  value='<%= "/portal/settings/community_users.jsp?paf_community_id="+request.getParameter("paf_community_id") %>'/>
 <core:UrlParam param="paf_community_id" value='<%=request.getParameter("paf_community_id")%>'/>

<core:CreateUrl id="createUserURL" url="/portal/settings/frame.jsp">

  <core:UrlParam param="navHighLight" value="users"/>
  <core:UrlParam param="mode" value="7"/>
  <core:UrlParam param="currPid" value="<%=sideBarIndex%>"/>
  <paf:encodeUrlParam param="paf_page_url" value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id" value='<%=request.getParameter("paf_community_id")%>'/>
  <paf:encodeUrlParam param="contentTarget" value="<%=contentTargetUserURL.getNewUrl()%>"/>


<table border="0" cellpadding="0" cellspacing="0">
<tr><td bgcolor="#000000"><img src='<%=clearGif%>' height="1" width="150" alt=""></td></tr>
<tr><td bgcolor="#EAEEF0"><img src='<%=clearGif%>' height="3" width="150" alt=""></td></tr>
<tr><td>
<table border="0" cellpadding="2" cellspacing="0" width="100%">


<%
    // this data stucture holds the target base url in its first index
    // the follow indices hold the side bar link text
    // the numbers are 1) the mode for the hightlight in this case it make the link black
    // and not underlined.  the other numbers are to trigger the link to be highlighted 
    // and blue and unerlined when the modes match.
//    if ( request.getParameter("currPid") != null) {
//      currPid =  request.getParameter("currPid");
//    }
    String secondLevelData[][][] = {
    {/* Settings */
     { CsettingsURLStr  ,""  ,"" ,"" ,"", "settings-manager"},
     {i18n_comm_settings         ,"1" ,"" ,"" ,"", "settings-manager"},
     {i18n_comm_access           ,"3" ,"" ,"" ,"", "settings-manager"},
          {i18n_comm_advanced    ,"4" ,"" ,"-3-4-" ,"", "settings-manager"},
     {i18n_about_settings        ,"2" ,"" ,"" ,"", "settings-manager"}
     },
    {/* users */
     { CusersURLStr      ,""  ,"" ,"" ,"", "user-manager"},
     {i18n_individual_members     ,"1" ,"-1d-" ,"" ,"", "user-manager"},
         {i18n_add_members          ,"1a","","-1-1a-1b-1d-" ,"", "user-manager"},
         {i18n_membership_request   ,"1b","","-1-1a-1b-1d-" ,"", "user-manager"},
     {i18n_member_organizations   ,"2" ,"-2b-" ,"" ,"", "user-manager"},
         {i18n_add_organizations    ,"2a","-2ab-","-2-2a-2b-2ab-" ,"", "user-manager"},
     {i18n_individual_guests      ,"3" ,"" ,"" ,"", "user-manager"},
         {i18n_add_guests           ,"3a","","-3-3a-" ,"", "user-manager"},
     {i18n_guest_organizations    ,"4" ,"" ,"" ,"", "user-manager"},
         {i18n_add_organizations    ,"4a","-4ab-","-4-4a-4b-4ab-" ,"", "user-manager"},
     {i18n_individual_leaders     ,"5" ,"" ,"" ,"", "user-manager"},
         {i18n_add_leaders          ,"5a","","-5-5a-" ,"", "user-manager"},
     {i18n_leader_organizations   ,"6" ,"" ,"" ,"", "user-manager"},
         {i18n_add_organizations    ,"6a","-6ab-","-6-6a-6b-6ab-" ,"", "user-manager"},
     {i18n_create_user            ,"7" ,"" ,"",createUserURL.getNewUrl(), "user-manager"},
     {i18n_about_users            ,"8" ,"" ,"" ,"", "user-manager"}
     },
    {/* pages */
     { CpagesURLStr      ,""  ,"" ,"" ,"", "page-manager"},
     {i18n_current_pages          ,"1" ,"-5-6-7-8-9-10-" ,"" ,"", "page-manager"},
     {i18n_create_new_pages       ,"2" ,"" ,"" ,"", "page-manager"},
     {i18n_about_pages            ,"3" ,"" ,"" ,"", "page-manager"},
     },
    {/* gears */
     { CgearsURLStr      ,""  ,"" ,"" ,"", "gear-manager"},
     {i18n_current_gears          ,"1" ,"-5-7-8-12-13-14-" ,"" ,"", "gear-manager"},
     {i18n_add_gears              ,"2" ,"-6-9-" ,"" ,"", "gear-manager"},
     {i18n_add_shared_gears       ,"3" ,"-10-11-" ,"" ,"", "gear-manager"},
     {i18n_about_gears            ,"4" ,"" ,"" ,"", "gear-manager"},
     },
    { {"","" ,"" ,"" ,"", null}},
    { {"","" ,"" ,"" ,"", null}},
    { {"","" ,"" ,"" ,"", null}},
   { {"","" ,"" ,"" ,"", null}}

};

 outputStr = ""; // reset
 String indentStr = "&nbsp;&nbsp;&nbsp;";
 String hrefLink = "";
 int index = (Integer.valueOf(sideBarIndex)).intValue();
 if ( secondLevelData[index] != null ) {
  String secondLevel[][] =  secondLevelData[index];
  for ( int i = 1 ; i < secondLevel.length ; i++ ) {
    // check access level to see if we should render the link
    String accessRole = secondLevel[i][5];
    if (accessRole != null) {
      atg.portal.framework.Community community = atg.portal.framework.RequestUtilities.getCommunity(request);
      if ((community == null) ||
           (!community.isLeader(null) && // also checks portal admin
            !community.hasRole(accessRole)))
        continue;
   }

   if ( ( secondLevel[i][3].equals("")) || ( secondLevel[i][3].indexOf("-"+mode+"-") > -1 ) ) {
  
   indentStr = ( secondLevel[i][3].indexOf("-"+mode+"-") > -1 ) 
             ? "<tr bgcolor='#EAEEF0'><td nowrap><font class='smaller' color='#9999999'>&nbsp;&nbsp;&#149;</font></td><td>" : "<tr bgcolor='#EAEEF0'><td nowrap colspan=2>";

   hrefLink = secondLevel[0][0] + "&mode="+secondLevel[i][1];
   if (! secondLevel[i][4].equals("")) hrefLink =  secondLevel[i][4];

   if (! secondLevel[i][1].equals(mode) ) {
    if ( secondLevel[i][2].indexOf("-"+mode+"-") > -1 ) {
       outputStr += indentStr+"<a class='commadmin_leftnav' style='' target='_top' href='";
       outputStr += hrefLink;
       outputStr += "'><b>";
       outputStr += secondLevel[i][0];
       outputStr += "</b></a></td></tr>\n";      
     } else { 
       outputStr += indentStr+"<a class='commadmin_leftnav' target='_top' href='";
       outputStr += hrefLink;
       outputStr += "'>";
       outputStr += secondLevel[i][0];
       outputStr += "</a></td></tr>\n";
     }
   } else {
       outputStr += indentStr+"<a class='commadmin_leftnav' style='text-decoration:none;color:#000000;' target='_top' href='";
       outputStr += hrefLink;
       outputStr += "'><b>";
       outputStr += secondLevel[i][0];
       outputStr += "</b></a></td></tr>\n";
   }  
  }
 }
 }
%>
<%= outputStr %>
</table>
</td></tr>
<tr><td bgcolor="#EAEEF"><img src='<%=clearGif%>' height="3" width="150" alt=""></td></tr>
<tr><td bgcolor="#000000"><img src='<%=clearGif%>' height="1" width="150" alt=""></td></tr>
</table>

</core:CreateUrl><%-- create user url override  --%>
</core:CreateUrl><%-- content target  create users --%>



</dsp:getvalueof><%-- id="thisNavHighlight" --%>
</dsp:getvalueof><%-- id="mode"             --%>
</dsp:getvalueof><%-- id="sideBarIndex"     --%>
</dsp:getvalueof><%-- id="dhtmlFlag"        --%>
</dsp:getvalueof><%-- id="dsp_page_id"      --%>
</dsp:getvalueof><%-- id="dsp_gear_id"      --%>
</dsp:getvalueof><%-- id="dsp_page_url"     --%>
</dsp:getvalueof><%-- id="dsp_community_id" --%>

</admin:InitializeAdminEnvironment>
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/nav_sidebar.jsp#2 $$Change: 651448 $--%>


