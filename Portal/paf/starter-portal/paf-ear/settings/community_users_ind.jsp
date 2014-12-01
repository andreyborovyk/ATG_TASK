<%@ page import="java.io.*,java.util.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
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

<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">
<dsp:getvalueof param="roleName"  idtype="java.lang.String"  id="userType">

<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>

<dsp:setvalue bean="CommunityPrincipalFormHandler.id" value="<%=dsp_community_id%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.accessType" value="<%=userType%>"/>
<dsp:setvalue bean="CommunityPrincipalFormHandler.principalType" value="user"/>

<% 
atg.portal.admin.PagingFormHandler pagingFH =
   (atg.portal.admin.PagingFormHandler) atg.servlet.ServletUtil.getDynamoRequest(request).resolveName("/atg/portal/admin/PagingFormHandler");
%>
<dsp:importbean bean="/atg/portal/admin/PagingFormHandler"/>

<i18n:message id="i18n_imageroot" key="imageroot"/>

<core:createUrl  id="membersURL" url="community_users.jsp">
  <core:UrlParam param="mode" value="<%=mode%>"/>
  <core:UrlParam param="colSort" value='<%=request.getParameter("colSort")%>'/>
   <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
     <dsp:setvalue bean="PagingFormHandler.currentPageURL"  value="<%=membersURL.getNewUrl()%>" />
     <dsp:setvalue bean="PagingFormHandler.communityMembersPageURL"  value="<%=membersURL.getNewUrl()%>" />
     <dsp:setvalue bean="PagingFormHandler.updateSuccessURL"  value="<%=membersURL.getNewUrl()%>" />
 </core:createUrl>

<%-- Set the properties for next page --%>
<dsp:setvalue bean="PagingFormHandler.currentPositionInOrganizationMembers"  value="0" />
<dsp:setvalue bean="PagingFormHandler.showAtATime"  value="25" />

<%
 String alphabet = ( request.getParameter("userFilter") != null ) ?  request.getParameter("userFilter") :""; 
%>

<i18n:message id="i18n_community_members_addremoveorg" key="community_members_addremoveorg"/>
<i18n:message id="i18n_community_members_individuals"  key="community_members_individuals"/>

<admin:GetUsers id="users">

<%-- if not null here --%>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
 <% if(userType.equals("guest")) { %>
  <font class="pageheader"><i18n:message key="community_members_individual_guests"/></font><br />
 <% } else if(userType.equals("member")) { %>
  <font class="pageheader"><i18n:message key="community_members_individual_members"/></font><br />
 <% } else if(userType.equals("leader")) { %>
  <font class="pageheader"><i18n:message key="community_members_individual_leaders"/></font><br />
 <% } %>
 </td></tr></table>
</td></tr></table>
 
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<font class="smaller">
 <% if(userType.equals("guest")) { %>
  <font class="smaller"><i18n:message key="community_members_individual_guests_helpertext"/></font><br />
 <% } else if(userType.equals("member")) { %>
  <font class="smaller"><i18n:message key="community_members_individual_members_helpertext"/></font><br />
 <% } else if(userType.equals("leader")) { %>
  <font class="smaller"><i18n:message key="community_members_individual_leaders_helpertext"/></font><br />
 <% } %>
</td></tr></table>


<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0" alt=""><br />

<%
String [] sortBy1 = {"firstName", "lastName"};
Collection searchedUsers1 = users.getUsersForRoleAndAlphabet((String) userType, "ALL", (String) dsp_community_id, 0, -1, sortBy1);
 if(0 != searchedUsers1.size()) {
%>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<font class="smaller">
<form action="community_users.jsp" method="POST" name="filter">
 <input type="hidden" name="mode" value="<%=mode%>" />
 <input type="hidden" name="colSort" value='<%=request.getParameter("colSort")%>' />
 <input type="hidden" name="paf_community_id" value="<%=dsp_community_id%>" />
 <input type="hidden" name="paf_page_url" value="<%=dsp_page_url%>" />

  <% String alphabetVar = ( request.getParameter("userFilter") != null ) ?  request.getParameter("userFilter") :""; %>
  <i18n:message id="submitFilter" key="filter"/>
  <i18n:message key="to-filter-by-three-letter-sirname" />&nbsp;<input type="text" 
           size="3" maxlength="3"
           name="userFilter"
           value="<%=alphabetVar%>" 
           onkeypress="return blockIE(event.keyCode)" />&nbsp;<input type="SUBMIT"     
       name="filter" value="<%=submitFilter%>" />
    </font>
</form>
</td></tr> </table> 
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0" alt=""><br />
<% } %>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<font class="smaller">
   
 <%@include file="fragments/community_users_fragment_listing.jspf" %>

</td></tr></table>
<br />
</admin:GetUsers>

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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_ind.jsp#2 $$Change: 651448 $--%>
