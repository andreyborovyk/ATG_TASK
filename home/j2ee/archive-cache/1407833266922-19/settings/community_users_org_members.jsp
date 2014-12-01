<%@page import="java.io.*,java.util.*"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>
<paf:hasCommunityRole roles="leader,user-manager">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/PagingFormHandler"/>

<admin:GetUsers id="users">

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_organization_id" idtype="java.lang.String" param="organization_id">
<dsp:getvalueof id="mode" idtype="java.lang.String" param="mode">
<dsp:getvalueof param="roleName" idtype="java.lang.String" id="userType">
<dsp:getvalueof id="alphabet" idtype="java.lang.String" bean="PagingFormHandler.organizationAlphabetVar">

<dsp:getvalueof idtype="java.lang.Integer" id="position" bean="PagingFormHandler.currentPositionInMembers"> 
<dsp:getvalueof idtype="java.lang.Integer" id="usersPerPage" bean="PagingFormHandler.showAtATime">



<i18n:message id="i18n_bold"     key="html-bold"/>
<i18n:message id="i18n_end_bold" key="html-end-bold"/>
<dsp:form action="community_users.jsp" method="POST" synchronized="/atg/portal/admin/PagingFormHandler">

 	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
 <i18n:message key="community_organization_indiv_params"> 
  <i18n:messageArg value="<%=users.getOrganizationForId(dsp_organization_id).getName()%>"/>
  <i18n:messageArg value="<%=i18n_bold%>"/>
  <i18n:messageArg value="<%=i18n_end_bold%>"/>
 </i18n:message>
</td></tr></table>
</td></tr></table>

 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
 <i18n:message key="community_organization_indiv_helpertext"/>
</td></tr></table>



<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<font class="adminbody">

<% String alphabetVar = ( alphabet.equals("ALL") ) ? "": alphabet ; %>
  <i18n:message id="submitFilter" key="filter"/>
<i18n:message key="to-filter-by-three-letter-sirname"/>&nbsp;<dsp:input type="text" 
           bean="PagingFormHandler.organizationAlphabetVar"
           size="3" maxlength="3"
           name="userFilter" 
           value="<%=alphabetVar%>" 
 onkeypress="return blockIE(event.keyCode)"/>&nbsp;<dsp:input type="SUBMIT"
       bean="PagingFormHandler.organizationAlphabet"
       border="0"
       name="filter" value="<%=submitFilter%>"
        />

</font>

<core:createUrl  id="memberURL" url="community_users.jsp">
  <core:UrlParam param="mode" value='<%= request.getParameter("mode")%>'/>
  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
  <core:UrlParam param="organization_id" value="<%=dsp_organization_id%>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <dsp:setvalue bean="PagingFormHandler.updateSuccessURL" value="<%=memberURL.getNewUrl()%>"/>
    <dsp:setvalue bean="PagingFormHandler.currentPageURL"   value="<%=memberURL.getNewUrl()%>" />
    <dsp:setvalue bean="PagingFormHandler.organizationMembersPageURL"  value="<%=memberURL.getNewUrl()%>" />
</core:createUrl>
</td></tr></table>
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>


<%--
 // Target renderings of messages include...
 // ( no indivuals )
 // ( no indivuals starting with the letter <b>X</b> )
 //  1 individual    showing 1
 //  3 individuals   showing 3
 //  500 indivuals   showing 26 - 50    Previous 25   Next 25
 //  500 indivuals   showing 1 - 25     Next 25
 //  31 indivuals   showing 1 - 25     Next 6
 //  500 indivuals   showing 475 - 500    Previous 25
 //  516 indivuals   showing 475 - 500    Previous 25   Next 16
 //  1 indivual starting with <b>nam</b>   showing 1
 //  3 indivuals starting with <b>nam</b>  showing 3
 //  500 indivuals  starting with <b>nam</b>   showing 26 - 50    Previous 25   Next 25
--%>

<i18n:message id="i18n_single_guest"  key="community_members_guest"/>
<i18n:message id="i18n_plural_guest"  key="community_members_guests"/>
<i18n:message id="i18n_single_member" key="community_members_member"/>
<i18n:message id="i18n_plural_member" key="community_members_members"/>
<i18n:message id="i18n_single_leader" key="community_members_leader"/>
<i18n:message id="i18n_plural_leader" key="community_members_leaders"/>

<%
 if( request.getParameter("colSort") != null ) {
 //  sortBy = request.getParameter("colSort");
 }
 boolean hasSearch              = false;
 boolean isOnFirstPage          = false;
 boolean isOnLastPage           = false;
 int numberOfUsers              = 0;
 int numberOnNextPage           = 0;
 String userTypeString          = "";
 String[] sortBy = {"firstName", "lastName"};

 int startRange = ((Integer) usersPerPage).intValue()  * ((Integer) position).intValue();
 int endRange   = (((Integer) position).intValue() +1) * ((Integer) usersPerPage).intValue();
    Collection searchedUsers  = users.getUsersForOrganizationAndAlphabet((String)dsp_organization_id,(String) alphabet , 0, -1, sortBy);
    numberOfUsers =  searchedUsers.size();
    
    isOnFirstPage = ( startRange > 0 )          ? false :true; 
    isOnLastPage  = ( numberOfUsers > endRange) ? false :true; 
    if (! isOnLastPage ) {
       // if not on last page then determine number of user to display on next page
       numberOnNextPage = (( endRange   + ((Integer) usersPerPage).intValue() ) > numberOfUsers) ?
                           ( numberOfUsers - endRange ) :  ((Integer) usersPerPage).intValue();                            
    }
 if(userType.equals("guest")) {
   userTypeString =  ( numberOfUsers == 1) ?  i18n_single_guest  :  i18n_plural_guest ;
 } else  if(userType.equals("member"))  {
   userTypeString =  ( numberOfUsers == 1) ?  i18n_single_member :  i18n_plural_member;
 } else {
   userTypeString =  ( numberOfUsers == 1) ?  i18n_single_leader :  i18n_plural_leader;
 }  
 if ( !alphabet.equals("ALL") && !alphabet.equals(""))  {
    hasSearch = true;
 }

%>
<font class="smaller"><nobr>
<% 
  if ( hasSearch && ( numberOfUsers == 0 ) ) { 
    // ( no individuals starting with {bold} {alphabet}{end_bold} )
%>
   <i18n:message key="community_users_no_ind_with">
    <i18n:messageArg value="<%=alphabet%>"/>
    <i18n:messageArg value="<%=i18n_bold%>"/>
    <i18n:messageArg value="<%=i18n_end_bold%>"/>
   </i18n:message>

<%
  }
  if ( !hasSearch && ( numberOfUsers == 0 ) ) {
  //       ( no individuals  )
%>
  <i18n:message key="community_users_no_ind"/>
<%
  }
  if ( numberOfUsers > 0 ) {
    if ( hasSearch ) {
      if (numberOfUsers < endRange) {   
//{numberOfUsers} {userTypeString} beginning with {alphabet}&nbsp;&nbsp;&nbsp;&nbsp;Showing {startRange+1} - {numberOfUsers}
%>
   <i18n:message key="community_users_number_of_x_with">
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
    <i18n:messageArg value="<%=userTypeString%>"/>
    <i18n:messageArg value="<%=alphabet%>"/>
    <i18n:messageArg value="<%=i18n_bold%>"/>
    <i18n:messageArg value="<%=i18n_end_bold%>"/>
   </i18n:message>
      &nbsp;&nbsp;&nbsp;&nbsp;
   <i18n:message key="community_users_showing_x_thru_y">
    <i18n:messageArg value="<%=new Integer(startRange+1).toString()%>"/>
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
   </i18n:message>
<%        
      } else { 
//{numberOfUsers} {userTypeString} beginning with {alphabet}&nbsp;&nbsp;&nbsp;&nbsp;Showing {startRange+1} - {endRange}
%>

   <i18n:message key="community_users_number_of_x_with">
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
    <i18n:messageArg value="<%=userTypeString%>"/>
    <i18n:messageArg value="<%=alphabet%>"/>
    <i18n:messageArg value="<%=i18n_bold%>"/>
    <i18n:messageArg value="<%=i18n_end_bold%>"/>
   </i18n:message>
      &nbsp;&nbsp;&nbsp;&nbsp;
   <i18n:message key="community_users_showing_x_thru_y">
    <i18n:messageArg value="<%=new Integer(startRange+1).toString()%>"/>
    <i18n:messageArg value="<%=new Integer(endRange).toString()%>"/>
   </i18n:message>

<%
      }
    } else { // NO search
      if (numberOfUsers < endRange) {
//{numberOfUsers} {userTypeString}&nbsp;&nbsp;&nbsp;&nbsp;Showing {startRange+1} - {numberOfUsers}
%>
   <i18n:message key="community_users_number_of_x">
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
    <i18n:messageArg value="<%=userTypeString%>"/>
   </i18n:message>
      &nbsp;&nbsp;&nbsp;&nbsp;
   <i18n:message key="community_users_showing_x_thru_y">
    <i18n:messageArg value="<%=new Integer(startRange+1).toString()%>"/>
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
   </i18n:message>
<%
      } else { 
//{numberOfUsers} {userTypeString}&nbsp;&nbsp;&nbsp;&nbsp;Showing {startRange+1} - {endRange}
%>
   <i18n:message key="community_users_number_of_x">
    <i18n:messageArg value="<%=new Integer(numberOfUsers).toString()%>"/>
    <i18n:messageArg value="<%=userTypeString%>"/>
   </i18n:message>
      &nbsp;&nbsp;&nbsp;&nbsp;
   <i18n:message key="community_users_showing_x_thru_y">
    <i18n:messageArg value="<%=new Integer(startRange+1).toString()%>"/>
    <i18n:messageArg value="<%=new Integer(endRange).toString()%>"/>
   </i18n:message>

<%  
      }  
    }
  }
%>

<core:CreateUrl id="basePageUrl" url="community_users.jsp">
 <core:UrlParam param="mode" value="<%=mode%>"/>
 <core:UrlParam param="colSort" value='<%=request.getParameter("colSort")%>'/>
 <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
<%
 if ( !isOnFirstPage && (startRange > 0 ) && (numberOfUsers > 0)) { 
%>

   <i18n:message id="prevLink" key="community_users_previous_x">
    <i18n:messageArg value="<%=usersPerPage.toString()%>"/>
   </i18n:message>

&nbsp;&nbsp;&nbsp;<dsp:a href="<%=basePageUrl.getNewUrl()%>" bean="PagingFormHandler.prevMembers" value=""><font class="smaller"><%=prevLink%></font></dsp:a>

<%
 }
%>
<% if (( !isOnLastPage ) && (numberOfUsers > 0)) {
%>
   <i18n:message id="nextLink" key="community_users_next_x">
    <i18n:messageArg value="<%=new Integer(numberOnNextPage).toString()%>" />
   </i18n:message>

&nbsp;&nbsp;&nbsp;<dsp:a href="<%=basePageUrl.getNewUrl()%>" bean="PagingFormHandler.nextMembers" value=""><font class="smaller" ><%=nextLink%></font></dsp:a>

<% 
 }
%>
</core:CreateUrl>
</nobr>
</font>


<core:If value="<%=(numberOfUsers >  0 )%>">

<br><br>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>

<td nowrap><font class="smaller" style="text-decoration:none;font-weight:700;"><i18n:message key="community_members_name"/></font></td>

<td nowrap><font class="smaller" style="text-decoration:none;" ><i18n:message key="community_members_email"/></font></td>

<td nowrap><font class="smaller" style="text-decoration:none;"><i18n:message key="community_members_organization"/></font></td>

</tr>

<tr>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="120" height="1" border="0"></td>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="120" height="1" border="0"></td>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="80" height="1" border="0"></td>

</tr>
<%
  String bgcolor       = "ffffff";
  String userName      = "";
  String firstName     = "";
  String lastName      = "";
  String userEmail     = "";
  String userOrg       = "";
//  String userPhone     = "";
//  String userLastAct   = "";
 %>

  <core:ForEach id="roles"
        values='<%= searchedUsers %>'
        castClass="atg.userdirectory.User"
        elementId="user">

<tr bgcolor="#<%=bgcolor%>">
 <%
   bgcolor = ( bgcolor.equals("ffffff") ) ? "cccccc" : "ffffff";
   firstName  = ( user.getFirstName()    == null   ) ? "&nbsp;"     : user.getFirstName() ;
   lastName   = ( user.getLastName()     == null   ) ? "&nbsp;"     : user.getLastName() ;
   userEmail  = ( user.getEmailAddress()        == null   ) ? "&nbsp;"     : user.getEmailAddress() ;
   if ( user.getParentOrganization() != null ) {
    userOrg    = ( user.getParentOrganization().getName() == null ) ? "&nbsp;" : user.getParentOrganization().getName()  ;
   } else {
    userOrg = "";
   }

  %>

<td nowrap><font class="adminbody"><%= firstName   %>&nbsp;<%= lastName %>&nbsp;</font><font clas="small">&nbsp;<!--this forces some padding for the table--></td>
<td nowrap><font class="adminbody"><%= userEmail   %>&nbsp;</font></td>
<td nowrap><font class="adminbody"><%= userOrg     %>&nbsp;</font></td>

</tr>
</core:ForEach>
<tr>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="120" height="1" border="0"></td>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="120" height="1" border="0"></td>
<td colspan=1 bgcolor="#666666"><img src="images/clear.gif" width="80" height="1" border="0"></td>

</tr>
</table>

</core:If>

</dsp:form>
</td></tr></table>
<br>

</dsp:getvalueof><%-- position            --%>
</dsp:getvalueof><%-- usersPerPage        --%>

</dsp:getvalueof><%-- alphabet            --%>
</dsp:getvalueof><%-- role / userType     --%>
</dsp:getvalueof><%-- mode                --%>
</dsp:getvalueof><%-- dsp_organization_id --%>
</dsp:getvalueof><%-- dsp_community_id    --%>
</dsp:getvalueof><%-- dsp_page_url        --%>
</dsp:getvalueof><%-- dsp_page_id         --%>


</admin:GetUsers>



</admin:InitializeAdminEnvironment>
</paf:hasCommunityRole>
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_org_members.jsp#2 $$Change: 651448 $--%>
