<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%
  
  //Community
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String communityId = null;
  if(cm != null) {
    communityId = cm.getId();
  }
  //Page
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  String pageId = null;
  if(pg != null) {
    pageId = pg.getId();
  }
  //Request/Response
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<dsp:page>

<paf:hasCommunityRole roles="leader,page-manager">

<admin:InitializeAdminEnvironment id="adminEnv">

<admin:GetUsers id="users">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="i18n_bold"         key="html-bold"/>
<i18n:message id="i18n_end_bold"     key="html-end-bold"/>
<i18n:message id="i18n_italic"       key="html-italic"/>
<i18n:message id="i18n_end_italic"   key="html-end-italic"/>
<i18n:message id="i18n_paren_left"   key="paren_left"/>
<i18n:message id="i18n_paren_right"  key="paren_right"/>

<i18n:message id="submitLabelSearch" key="search"/>
<i18n:message id="submitLabelFind"   key="find"/>
<i18n:message id="submitLabelFilter" key="filter"/>

<i18n:message id="i18n_label_allow"  key="acl_form_radio_button_labels_allow" />
<i18n:message id="i18n_label_deny"   key="acl_form_radio_button_labels_deny" />
<i18n:message id="i18n_label_defer"  key="acl_form_radio_button_labels_defer" />


<dsp:importbean bean="/atg/userprofiling/ProfileUserDirectoryTools"/>
<dsp:importbean bean="/atg/userdirectory/droplet/UserListDroplet"/>
<dsp:importbean bean="/atg/portal/admin/AclTranslator"/>
<dsp:importbean bean="/atg/portal/admin/EditCommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/PagingFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/PageAccessFormHandler"/>

<% 
    // reset page attributes
   java.util.Collection emptyCollection = new ArrayList();
   pageContext.setAttribute("organizationList",emptyCollection,pageContext.PAGE_SCOPE);
   pageContext.setAttribute("userList",emptyCollection,pageContext.PAGE_SCOPE);
%>

<%-- PARAMS ,base urls and common variables --%>
<%
 // general variables
 String clearGif = response.encodeURL("images/clear.gif");
 String selected = "";
 String dsp_page_url     =(request.getParameter("paf_page_url") != null)   ?request.getParameter("paf_page_url") : "";
 String dsp_page_id      =(request.getParameter("paf_page_id")!= null)     ?request.getParameter("paf_page_id")  : "";
 String dsp_gear_id      =(request.getParameter("paf_gear_id")!= null)     ?request.getParameter("paf_gear_id")  : "";
 String dsp_community_id =(request.getParameter("paf_community_id")!= null)?request.getParameter("paf_community_id"):"";
 String mode             =(request.getParameter("mode")!= null) ? request.getParameter("mode") : "";
 String currSelected     =(request.getParameter("userGroupType")!=null) ? request.getParameter("userGroupType"):"";
 String currFilter       =(request.getParameter("searchFilter") !=null) ? request.getParameter("searchFilter") 	:""  ;

 String userType = "";
 if ( currSelected.indexOf("member") > -1 )  userType = "member" ;
 if ( currSelected.indexOf("guest")  > -1 )  userType = "guest" ;
 if ( currSelected.indexOf("leader") > -1 )  userType = "leader" ; 
 String [] sortBy = {"firstName","lastName"};
 String bgcolorA = "dddddd";
 String currName = "";
 boolean hasSeen = false;
 int rowCount = 0;
 boolean showRolesInDropdown = true;
 // end general variables

 // specific variables for this acl editor
 String urlTargetJsp = "community_pages.jsp";
 String beanTarget   = "PageAccessFormHandler";

%>

<%--   HEAD/TITLE SECTION --%>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
<tr><td><font class="pageheader_edit">

<img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0">

<i18n:message key="page_access_header_title">
 <i18n:messageArg value="<%= adminEnv.getPage().getName()%>"/>
</i18n:message>

</font></td></tr></table>
</td></tr></table>



<%-- PRINT CURRENT ACL --%>
<%--
<dsp:getvalueof id="aclTranslator" idtype="atg.portal.admin.AclTranslator" bean="AclTranslator">
  <%
    List aceDescriptions = aclTranslator.getAclDescription(request, response, pg);
    if ((aceDescriptions != null) && (aceDescriptions.size() > 0)) {
  %>
  <core:ForEach id="aceIterator"
                values="<%=aceDescriptions%>"
                castClass="String"
                elementId="aceDescription">
    &nbsp;&nbsp;<font class="info"><%=aceDescription%></font><br>
  </core:ForEach>
  <br>
  <% } %>
</dsp:getvalueof>
--%>
<%-- END PRINT CURRENT ACL --%>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">

<i18n:message key="page_access_editor_intro"/><br>

</font>
</td></tr></table>
<%-- END HEAD/TITLE SECTION --%>

<%-- SECTION ONE --%>
<img src="<%= clearGif %>" height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">

<i18n:message key="page_access_instruction_item_1_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>

<%@ include file="fragments/access_type_pulldown.jspf" %>

</font>
</td></tr></table>
<%-- END SECTION ONE --%>




<%-- ROLE SELECTED --%>
<core:If value='<%=(currSelected.equals("all_roles"))%>'>

 <dsp:getvalueof id="userDirectoryTools" idtype="atg.portal.admin.userdirectory.PortalUserDirectoryTools" bean="ProfileUserDirectoryTools">
  <%
   java.util.Collection allRoles = userDirectoryTools.getRoles();
   if ( allRoles != null ) {
    pageContext.setAttribute("roleList",allRoles,pageContext.PAGE_SCOPE);
   }
  %>
 </dsp:getvalueof>

  <%
  if ( ((java.util.Collection) pageContext.getAttribute("roleList",pageContext.PAGE_SCOPE)).size() > 0 ) {

  %>
  <%-- SECTION TWO for role --%>

<img src="<%= clearGif %>" height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">

<i18n:message key="page_access_instruction_item_2_role_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>

<br>

    <%@ include file="fragments/access_type_filter.jspf" %>

</font>
</td></tr></table>
   
   <%-- SECTION THREE for roles --%>

   <img src="<%= clearGif %>" height="1" width="1" border="0"><br>
   <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
     <font class="smaller">

<i18n:message key="page_access_instruction_item_3_role_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>


       <%@include file="fragments/access_role_listing.jspf" %>

     </td></tr></table>

    <core:If value="<%= hasSeen == true%>">

     <img src="<%= clearGif %>" height="1" width="1" border="0"><br>
     <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
     <font class="smaller"><br>

<i18n:message key="page_access_role_allow_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

<i18n:message key="page_access_role_deny_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

<i18n:message key="page_access_role_defer_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

   </font></td></tr></table>
   </core:If><%-- not an empty list but filters reduced list to zero --%>

 <%
  // end if not empty list of roles 
  } else {
 %>

<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">
<i18n:message key="acl_no_role_of_type" />
</font>
</td></tr></table>
 
<% } %>
</core:If>
<%-- END ROLE SELECTED --%>




<%-- ROLE NOT SELECTED --%>
<core:IfNot value='<%=(currSelected.equals("all_roles"))%>'>

<%-- ORGANIZATION SELECTED --%>
<core:If value='<%=(currSelected.indexOf("_orgs") > -1 )%>'>

 <dsp:getvalueof id="userDirectoryTools" idtype="atg.portal.admin.userdirectory.PortalUserDirectoryTools" bean="ProfileUserDirectoryTools">
  <%

   if (currSelected.indexOf("all_") > -1 ) {
     java.util.Collection allOrganizations = userDirectoryTools.getOrganizations();
     if ( allOrganizations != null ) {
      pageContext.setAttribute("organizationList",allOrganizations,pageContext.PAGE_SCOPE);
     }
   } else {
     if ( users.getOrganizationsForRole((String) userType,(String) communityId ) != null ) {
     pageContext.setAttribute("organizationList",users.getOrganizationsForRole((String) userType,(String) communityId ),pageContext.PAGE_SCOPE);
     }
   }
  %>
 </dsp:getvalueof>

  <%
  if ( ((java.util.Collection) pageContext.getAttribute("organizationList",pageContext.PAGE_SCOPE)).size() > 0 ) {

  %>
  <%-- SECTION TWO for org --%>

<img src="<%= clearGif %>" height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">

<i18n:message key="page_access_instruction_item_2_org_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>

<br>

    <%@ include file="fragments/access_type_filter.jspf" %>

</font>
</td></tr></table>
   
   <%-- SECTION THREE for organizations --%>

   <img src="<%= clearGif %>" height="1" width="1" border="0"><br>
   <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
     <font class="smaller">

<i18n:message key="page_access_instruction_item_3_org_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>


       <%@include file="fragments/access_org_listing.jspf" %>

     </td></tr></table>

    <core:If value="<%= hasSeen == true%>">

     <img src="<%= clearGif %>" height="1" width="1" border="0"><br>
     <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
     <font class="smaller"><br>

<i18n:message key="page_access_org_allow_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

<i18n:message key="page_access_org_deny_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

<i18n:message key="page_access_org_defer_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

   </font></td></tr></table>
   </core:If><%-- not an empty list but filters reduced list to zero --%>

 <%
  // end if not empty list of orgs 
  } else {
 %>

<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">
<i18n:message key="acl_no_org_of_type" />
</font>
</td></tr></table>
 
<% } %>
</core:If>
<%-- END ORGANIZATION SELECTED --%>






<%-- INDIVIDUALS  SELECTED --%>
<core:If value='<%=(currSelected.indexOf("_orgs") == -1 )%>'>
  

   <% 

     if ( currSelected.indexOf("all_") > -1 ) { %>
      <%@ include file="fragments/access_ind_search_all.jspf"%>
   <% } else { %>
      <%--  ROLES  Guests || Members --%>
      <%@ include file="fragments/access_ind_search_role.jspf" %>
   <% } // END IS role search  %>

  <core:If value='<%=(((java.util.Collection) pageContext.getAttribute("userList",pageContext.PAGE_SCOPE)).size() > 0 )  %>' >
   <%-- SECTION THREE for individuals --%>

   <img src="<%= clearGif %>" height="1" width="1" border="0"><br>
   <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
     <font class="smaller">

<i18n:message key="page_access_instruction_item_3_ind_param">
  <i18n:messageArg value="<%=i18n_bold%>"/>
  <i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>
    
     <%@ include file="fragments/access_ind_listing.jspf" %>

   </font></td></tr></table>

   <img src="<%= clearGif %>" height="1" width="1" border="0"><br>

   <table cellpadding="6" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
   <font class="smaller">

   <br>

<i18n:message key="page_access_ind_allow_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

<i18n:message key="page_access_ind_deny_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

   <br><br>

<i18n:message key="page_access_ind_defer_param">
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_italic%>"/>
 <i18n:messageArg value="<%=i18n_end_italic%>"/>
 <i18n:messageArg value="<%=i18n_paren_left%>"/>
 <i18n:messageArg value="<%=i18n_paren_right%>"/>
</i18n:message>

    <br><br>

 </font></td></tr></table>

 <%-- END SECTION THREE for individuals --%>

 </core:If>

</core:If>

<%-- END INDIVIDUAL  SELECTED --%>

</core:IfNot>

<%-- END ROLE NOT SELECTED --%>

</admin:GetUsers>

</admin:InitializeAdminEnvironment>

</paf:hasCommunityRole>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_access_editor.jsp#2 $$Change: 651448 $--%>
