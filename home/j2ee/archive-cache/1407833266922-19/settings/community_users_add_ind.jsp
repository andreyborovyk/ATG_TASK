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

<dsp:importbean bean="/atg/portal/admin/EditCommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/PagingFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/PropertyManager"/>

<% 
atg.portal.admin.PagingFormHandler pagingFH =
   (atg.portal.admin.PagingFormHandler) atg.servlet.ServletUtil.getDynamoRequest(request).resolveName("/atg/portal/admin/PagingFormHandler");
String alphabet = "HELLO";
%>


<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>

<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">
<dsp:getvalueof id="userType" idtype="java.lang.String" param="roleName" >

<dsp:getvalueof id="lastNameProperty" bean="PropertyManager.lastNamePropertyName">


<dsp:setvalue bean="CommunityPrincipalFormHandler.id" value="<%=dsp_community_id%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.accessType" value="<%=userType%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.principalType" value="user"/>

<i18n:message id="submitLabelSearch" key="search"/>

<core:If value='<%=request.getParameter("hasSearched") == null%>'>
 <dsp:setvalue bean="PagingFormHandler.currentPositionInSearchUsers"  value="0" />
 <dsp:setvalue bean="PagingFormHandler.showAtATime"  value="25" />
</core:If>


<%-- SEARCH  FORM --%>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
  <font class="pageheader">
    <core:Switch value="<%=userType%>">
	<core:Case value="guest">
 		<i18n:message key="community_members_addidividuals_header_addremoveguest"/>
	</core:Case>
	<core:Case value="member">
 		<i18n:message key="community_members_addidividuals_header_addremovemember"/>
	</core:Case>
	<core:Case value="leader">
 		<i18n:message key="community_members_addidividuals_header_addremoveleader"/>
	</core:Case>
    </core:Switch>
 </font>
</td></tr></table>
</td></tr></table>
 
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
    <core:Switch value="<%=userType%>">
	<core:Case value="guest">
 		<i18n:message key="community_members_addidividuals_helpertext_addremoveguest"/>
	</core:Case>
	<core:Case value="member">
 		<i18n:message key="community_members_addidividuals_helpertext_addremovemember"/>
	</core:Case>
	<core:Case value="leader">
 		<i18n:message key="community_members_addidividuals_helpertext_addremoveleader"/>
	</core:Case>
    </core:Switch>
</td></tr></table>

<!-- xxxxxxxxxxxxxxxxxxxxx  -->

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>

<font class="smaller"><i18n:message key="community_members_addidividuals_title_search"/></font><br><br>

<table cellpadding=0 cellspacing=0 border=0><!-- 3 cols -->
<form action="community_users.jsp" method="GET" NAME="search">
<tr>
<input type="hidden" name="hasSearched"      value="true">
<input type="hidden" name="paf_page_id"      value="<%=dsp_page_id%>">
<input type="hidden" name="paf_page_url"     value="<%=dsp_page_url%>" >
<input type="hidden" name="paf_community_id" value="<%=dsp_community_id%>">
<input type="hidden" name="mode" value="<%=mode%>">

<dsp:getvalueof id="userFname" param="firstName">
<% userFname  = (userFname   == null ) ? "" :userFname ; %>
 <td><font class="smaller"><b><i18n:message key="community_members_addidividuals_firstname"/></b></font><br>
 <font class="small"><input name="firstName" type="text" value="<%=userFname%>"/></font><br><img src="<%= response.encodeURL("images/clear.gif")%>" height="4" width="1" border="0"><br></td>
</dsp:getvalueof><!--  id="userFname" -->

 <td><font class="smaller">&nbsp;&nbsp;</font></td>

<dsp:getvalueof id="userLogin" param="login">
<%  userLogin  = ( userLogin  == null ) ? "" : userLogin ; %>
 <td><font class="smaller"><b><i18n:message key="community_members_user_name"/></b></font><br>
 <font class="small"><input name="login" type="text" value="<%=userLogin%>"/></font><br><img src="<%= response.encodeURL("images/clear.gif")%>" height="4" width="1" border="0"><br></td>
</dsp:getvalueof><!--  id="userLogin" -->

</tr>

<tr>

<dsp:getvalueof id="userLname"  param="lastName">
<%userLname  = (userLname  == null ) ? "" : userLname  ; %>
 <td><font class="smaller"><b><i18n:message key="community_members_addidividuals_lastname"/></b></font><br>
 <font class="small"><input name="lastName" type="text" value="<%=userLname%>"/></font><br><img src="<%= response.encodeURL("images/clear.gif")%>" height="4" width="1" border="0"><br></td>
</dsp:getvalueof><!--  id="userLname" -->

 <td><font class="smaller">&nbsp;&nbsp;</font></td>

<dsp:getvalueof id="useremail" param="email">
<% useremail = ( useremail == null ) ? "" : useremail ; %>
 <td><font class="smaller"><b><i18n:message key="community_members_addidividuals_email"/></b></font><br>
 <font class="small"><input name="email" type="text" value="<%=useremail%>"/></font><br><img src="<%= response.encodeURL("images/clear.gif")%>" height="4" width="1" border="0"><br></td>
</dsp:getvalueof><!-- id="useremail"-->

</tr>

<!--  ORG PULLDOWN -->
<tr>
<td colspan="3" align="LEFT">
<font  class="smaller"><b><i18n:message key="community_members_addidividuals_organization"/></b>&nbsp;</font><br>
<font  class="small">
  <admin:GetProfileItemsAndLevels id="organizations">
<dsp:getvalueof id="orgId" param="organizationId"> 
 <% String selectedItem = ""; %>
  <select name=organizationId>
     <option value=""/><i18n:message key="community_members_addidividuals_any"/>
        <core:ForEach id="orgs"
                      values="<%=organizations.getItemList()%>"
                      castClass="atg.portal.admin.ItemAndLevel"
                      elementId="organization">
  <% selectedItem = (  ((atg.userdirectory.Organization)organization.getItem()).getPrimaryKey().equals(orgId) ) ?
        "selected" : "" ; 
  %>
<option value="<%= (String)((atg.userdirectory.Organization)organization.getItem()).getPrimaryKey()  %>" <%=selectedItem%> /><%= ((atg.userdirectory.Organization)organization.getItem()).getName() %>

        </core:ForEach>
    </select>
 </dsp:getvalueof>
 </admin:GetProfileItemsAndLevels>

  <input type="SUBMIT"
       name="search"
       value="<%=submitLabelSearch%>" />
   </font></form></td>
 </tr>
</table>

</td></tr></table>


<%-- RESULTS  --%>

<core:If value='<%= request.getParameter("hasSearched")!=null%>'>

<admin:GetUsers id="users">
 <dsp:getvalueof id="orgId" param="organizationId"> 
 <dsp:getvalueof id="userFname" param="firstName">
 <dsp:getvalueof id="userLname"  param="lastName">
 <dsp:getvalueof id="userLogin" param="login">
 <dsp:getvalueof id="userEmail" param="email">


 <core:createUrl  id="membersURL" url="community_users.jsp">
   <core:UrlParam param="mode" value="<%=mode%>"/>
   <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
   <core:UrlParam param="organizationId" value="<%=orgId%>" /> 
   <core:UrlParam param="hasSearched" value="true"/> 
   <core:UrlParam param="firstName" value="<%=userFname%>" />
   <core:UrlParam param="lastName" value="<%=userLname%>" />
   <core:UrlParam param="login" value="<%=userLogin%>" />
   <core:UrlParam param="email" value="<%=userEmail%>" />
     <dsp:setvalue bean="PagingFormHandler.searchUsersURL"  value="<%=membersURL.getNewUrl()%>" />
 </core:createUrl>       


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
<i18n:message id="i18n_single_individual" key="community_members_individual_search"/>
<i18n:message id="i18n_plural_individual" key="community_members_individuals_search"/>

<i18n:message id="i18n_bold" key="html-bold"/>
<i18n:message id="i18n_end_bold" key="html-end-bold"/>



<%

 String clearGif = response.encodeURL("images/clear.gif");
 boolean hasSearch              = false;
 boolean isOnFirstPage          = false;
 boolean isOnLastPage           = false;
 int numberOfUsers              = 0;
 int numberOnNextPage           = 0;
 String userTypeString          = "";
 Integer position     = pagingFH.getCurrentPositionInSearchUsers(); 
 Integer usersPerPage = pagingFH.getShowAtATime(); 
 
 int startRange = ((Integer) usersPerPage).intValue()  * ((Integer) position).intValue();
 int endRange   = (((Integer) position).intValue() +1) * ((Integer) usersPerPage).intValue();
 numberOfUsers  = users.getSearchUserCount((String) orgId ,(String) userFname ,(String) userLname ,(String) userLogin ,(String) userEmail);

    if (numberOfUsers < startRange ) {
        pagingFH.setCurrentPositionInSearchUsers(new Integer (0));
        position =  pagingFH.getCurrentPositionInSearchUsers();
        startRange = ((Integer) usersPerPage).intValue()  * ((Integer) position).intValue();
    }
    isOnFirstPage = ( startRange > 0 )          ? false :true;
    isOnLastPage  = ( numberOfUsers > endRange) ? false :true;
    if (! isOnLastPage ) {
       // if not on last page then determine number of user to display on next page
       numberOnNextPage = (( endRange   + ((Integer) usersPerPage).intValue() ) > numberOfUsers) ?
                           ( numberOfUsers - endRange ) :  ((Integer) usersPerPage).intValue();
    }

Collection searchedUsers = users.getUsersForLoginAndNameAndEmailAndOrganization( (String) orgId ,(String) userFname ,(String) userLname ,(String) userLogin ,(String) userEmail , startRange ,endRange, null);

 userTypeString =  ( numberOfUsers == 1) ?  i18n_single_individual  :  i18n_plural_individual ;



%>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<dsp:form action="community_users.jsp" method="POST" name="add" synchronized="/atg/portal/admin/CommunityPrincipalFormHandler">
<font class="smaller"><b><i18n:message key="community_members_addidividuals_searchresults"/></b></font>&nbsp;&nbsp;<font class="smaller"><nobr>
<% 
  if  ( numberOfUsers == 0 )  { 
%>
     <i18n:message key="community_users_no_ind"/>
<%
  } else {
      if (numberOfUsers < endRange) {
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
%>

<core:CreateUrl id="basePageUrl" url="community_users.jsp">
 <core:UrlParam param="mode" value="<%=mode%>"/>
 <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
<%
 if ( !isOnFirstPage && (startRange > 0 ) && (numberOfUsers > 0)) { 
%>

   <i18n:message id="prevLink" key="community_users_previous_x">
    <i18n:messageArg value="<%=usersPerPage.toString()%>"/>
   </i18n:message>

&nbsp;&nbsp;&nbsp;<dsp:a href="<%=basePageUrl.getNewUrl()%>" bean="PagingFormHandler.prevMembersSearchPage" value=""><font class="small"><%=prevLink%></font></dsp:a>

<%
 }
 if (( !isOnLastPage ) && (numberOfUsers > 0)) {
%>
   <i18n:message id="nextLink" key="community_users_next_x">
    <i18n:messageArg value="<%=new Integer(numberOnNextPage).toString()%>" />
   </i18n:message>

&nbsp;&nbsp;&nbsp;<dsp:a href="<%=basePageUrl.getNewUrl()%>" bean="PagingFormHandler.nextMembersSearchPage" value=""><font class="small" ><%=nextLink%></font></dsp:a>

<% 
 }
%>
</core:CreateUrl>
</nobr>
</font>
<br>
<table cellpadding=0 cellspacing=0 border="0" width="100%">
<tr>
<td  NOWRAP width="48">&nbsp;<font size="-2" color="#D7DEE5"><nobr><i18n:message key="community_members_addidividuals_js_select_all"/>&nbsp;&nbsp;</nobr></font></td>
<td colspan="3"></td>
</tr>

<% if ( numberOfUsers > 0 ) {
 // if not null on results 
%>

<tr>

<td width="20" align="center"><font class="smaller"><nobr>
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

document.write("<a href='Javascript:all(-1)' taget='_self' style='text-decoration:none'><i18n:message key='community_members_addidividuals_js_select_all'/></a>");

</script>&nbsp;
</nobr></font></td>

<td nowrap><font class="smaller" style="text-decoration:none;font-weight:700" color="#333333"><i18n:message key="community_members_name"/></font></td>

<td nowrap><font class="smaller" style="text-decoration:none;"><i18n:message key="community_members_user_name"/></font></td>
<td nowrap><font class="smaller" style="text-decoration:none;"><i18n:message key="community_members_organization"/></font></td>
<td nowrap><font class="smaller" style="text-decoration:none;"><i18n:message key="community_members_email"/></font></td>
</tr>

<tr>
<td bgcolor="#666666" colspan=6><img src="images/clear.gif" height="1" width="250"></td>
</tr>
<%
  boolean hasSeenContent = false ;
  String bgcolorA = "dddddd";
  String login         = "";
  String firstName     = "";
  String lastName      = "";
  String resultUserEmail     = "";
  String userOrg     = "";
  int boxCount = 0;
%>

 <core:ForEach id="userSearchLoop"
 values='<%= searchedUsers %>'
        castClass="atg.userdirectory.User"
        elementId="userSearch"> 
<%
 if (!hasSeenContent){ hasSeenContent = true;}
 if (bgcolorA.equals("ffffff")) {
    bgcolorA ="dddddd";
 } else {
    bgcolorA="ffffff";
 }
 firstName  = ( userSearch.getFirstName() == null) ? "&nbsp;" : userSearch.getFirstName();
 lastName   = ( userSearch.getLastName()  == null) ? "&nbsp;" : userSearch.getLastName();
 login      = ( userSearch.getLogin()     == null) ? "&nbsp;" : userSearch.getLogin() ;
 resultUserEmail = (userSearch.getEmailAddress() == null) ? "&nbsp;" :(String) userSearch.getEmailAddress() ;
if ( userSearch.getParentOrganization() != null ) {
  userOrg    = ( userSearch.getParentOrganization().getName() == null ) ? "&nbsp;" : userSearch.getParentOrganization().getName()  ;
} else {
    userOrg = "";
}
%>

<tr bgcolor="#<%=bgcolorA%>">
 <td align="center"><font  class="smaller"><dsp:input type="checkbox" checked="false" bean="CommunityPrincipalFormHandler.principalIds" value="<%=userSearch.getPrimaryKey() %>"/></font></td>
 <td nowrap><font class="adminbody"><%= firstName  %>&nbsp;<%= lastName  %>&nbsp;</font></td> 
 <td nowrap><font class="adminbody"><%= login      %>&nbsp;</font></td>
 <td nowrap><font class="adminbody"><%= userOrg      %>&nbsp;</font></td>
 <td nowrap><font class="adminbody"><%= resultUserEmail  %>&nbsp;</font></td>
</tr>

 
 </core:ForEach>

<tr>
<td bgcolor="#666666" colspan=6><img src="images/clear.gif" height="1" width="250"></td>
</tr>

<% if( !(hasSeenContent)) { %>

<tr><td colspan=6><font class="smaller"><i18n:message key="community_members_addidividuals_nomatches"/></font></td></tr>

<% } // end hasSeenContent %>

 <core:CreateUrl id="searchAddUrl" url="community_users.jsp" >
   <core:UrlParam param="mode" value="<%=mode%>"/>
   <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
   <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
   <core:UrlParam param="organizationId" value="<%=orgId%>" /> 
   <core:UrlParam param="hasSearched" value="true"/> 
   <core:UrlParam param="firstName" value="<%=userFname%>" />
   <core:UrlParam param="lastName" value="<%=userLname%>" />
   <core:UrlParam param="login" value="<%=userLogin%>" />
   <core:UrlParam param="email" value="<%=userEmail%>" />
    <dsp:input type="hidden" bean="CommunityPrincipalFormHandler.failureURL" value="<%= (String) searchAddUrl.getNewUrl() %>"/>
 </core:CreateUrl>

 <core:CreateUrl id="AddUrl" url="community_users.jsp" >
   <core:UrlParam param="mode" value="<%=mode.substring(0,1)%>"/>
   <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
   <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>

    <dsp:input type="hidden" bean="CommunityPrincipalFormHandler.successURL" value="<%= (String) AddUrl.getNewUrl() %>"/>

 </core:CreateUrl>


<tr><td colspan=5>
<font class="small"><br>
 <i18n:message id="done02" key="add_selected" />
 <dsp:input type="SUBMIT"  bean="CommunityPrincipalFormHandler.assignPrincipals"     value="<%= done02 %>" />
 <i18n:message id="cancel02" key="reset" />&nbsp;
 <input bean="" type="RESET" value="<%= cancel02 %>"/>
</font>
</td></tr>

<%

} //end if not null on results
%>



<core:If value="<%=numberOfUsers < 1%>">
  <tr>
   <td colspan=5><font class="smaller">

     <i18n:message key="community_members_addidividuals_nomatches"/>
 
   </font></td>
  </tr>
</core:If>


</table>

</td>
</tr>
</table>


</dsp:form>

 </dsp:getvalueof>
 </dsp:getvalueof>
 </dsp:getvalueof>
 </dsp:getvalueof>
 </dsp:getvalueof>
</admin:GetUsers>

</core:If><%-- hasSearched --%>

<%-- END DISPLAY RESULTS --%>

</dsp:getvalueof><%-- id="lastNameProperty" --%>
</dsp:getvalueof><%-- roleName         --%>
</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_community_id --%>
</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) {e.printStackTrace();%>
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
<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_add_ind.jsp#2 $$Change: 651448 $--%>
