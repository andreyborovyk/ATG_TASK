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

<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>  

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityPrincipalFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="CommunityPrincipalFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="CommunityPrincipalFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>

<i18n:message id="i18n_imageroot" key="imageroot"/>

<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof param="roleName" idtype="java.lang.String" id="role">

<dsp:setvalue bean="CommunityPrincipalFormHandler.id" value="<%=dsp_community_id%>"/>

<dsp:form action="community_users.jsp" method="post" name="accept" synchronized="/atg/portal/admin/CommunityPrincipalFormHandler">

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader"><i18n:message key="community_members_requestadmin_header"/>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
<i18n:message key="community_members_requestadmin_helpertext"/>
</td></tr></table>
<!-- xxxxxxxxxxxxxxxxxx-->

      <core:CreateUrl  id="URL" url="community_users.jsp">
        <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
        <core:UrlParam param="mode" value='<%=request.getParameter("mode")%>'/>
        <dsp:input type="hidden" bean="CommunityPrincipalFormHandler.successURL"  value="<%=URL.getNewUrl()%>" />
        <dsp:input type="hidden" bean="CommunityPrincipalFormHandler.failureURL"  value="<%=URL.getNewUrl()%>" />          
      </core:CreateUrl>

<%--                                                      --%>
<%--Get the membership requests for the current community --%>
<%--                                                      --%>
<admin:getMembershipRequests id="requestsTag" communityId="<%=dsp_community_id%>">
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%">
<tr><td>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td colspan="4"><font class="smaller"><b><i18n:message key="community_member_requests_header"/>:</b><br><br>
</font></td>
</tr>

<core:exclusiveIf>
<core:if value="<%=(requestsTag.getMembershipRequests().size() > 0)%>">

<tr>
  <td NOWRAP><font class="small"><nobr>&nbsp;
<script language="Javascript">

function all(toggle) {
 formRef = eval( "document.forms['accept']"); 
 if ( formRef != null ) {
  i = 0;
  itemsFound = 0;
  itemNames = "";
  currUserId  = "";
  prevUserId  = "";
  while ( i < formRef.elements.length ) {
    if( ( formRef.elements[i].name.indexOf("approveDeclineFlags") > -1)
        && (  formRef.elements[i].name.indexOf("_D") == -1) ) {
     
      if (toggle == 1) {
       if (formRef.elements[i].value == "true") {
           formRef.elements[i].checked = true;
       }
      } else if (toggle == 0) {
       if (formRef.elements[i].value == "false" ) {
           formRef.elements[i].checked = true;
       }
      } else {
       if (formRef.elements[i].value == "no-decision" ) {
           formRef.elements[i].checked = true;
       }
      }
    }
   i++;
  }
 }
 //alert (" total found = " + itemsFound+ itemNames );
}

function dWrite(value,label) {

 document.write("<a href='Javascript:all("+value+")' taget='_self' style='text-decoration:none'>"+label+"</a>");

}

dWrite("-1","<i18n:message key='community_membership_js_link_all_later'/>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
dWrite("1","<i18n:message key='community_membership_js_link_accept_all'/>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
dWrite("0","<i18n:message key='community_membership_js_link_decline_all'/>");

</script></nobr></font></td>
  <td><font class="subheader"><i18n:message key="community_members_addidividuals_first"/></font></td>
  <td><font class="subheader"><i18n:message key="community_members_addidividuals_last"/></font></td>
  <td width="60%"><font class="subheader"><i18n:message key="community_members_addidividuals_email"/></font></td>
</tr>

<tr>
 <td bgcolor="#666666" colspan=4><img src="images/clear.gif" height="1" width="250"></td>
</tr>
<%
  boolean hasSeenContent = false ;
  String bgcolorA = "dddddd";
  String firstName     = "";
  String lastName      = "";
  String userEmail     = "";
  int radioCount = 0;
%>
  <dsp:setvalue bean="CommunityPrincipalFormHandler.noOfMembershipRequests" value='<%=new Integer(requestsTag.getMembershipRequests().size()).toString()%>'/>

<core:ForEach id="requests"
     values="<%=requestsTag.getMembershipRequests()%>"
     castClass="atg.portal.framework.MembershipRequest"
     elementId="memRequests">
<%
  if (!hasSeenContent){ hasSeenContent = true;}
  if (bgcolorA.equals("ffffff")) {
  bgcolorA ="dddddd";
  } else {
   bgcolorA="ffffff";
  }
 firstName  = (((atg.userdirectory.User)(memRequests.getUser())).getFirstName()== null) ? "&nbsp;" :(String)((atg.userdirectory.User)(memRequests.getUser())).getFirstName();
 lastName   = (((atg.userdirectory.User)(memRequests.getUser())).getLastName() == null) ? "&nbsp;" :(String)((atg.userdirectory.User)(memRequests.getUser())).getLastName() ;
 userEmail  = (((atg.userdirectory.User)(memRequests.getUser())).getEmailAddress()   == null) ? "&nbsp;" :(String)((atg.userdirectory.User)(memRequests.getUser())).getEmailAddress() ;
 %>

<tr bgcolor="#<%=bgcolorA%>">

<td nowrap align="center"><font class="smaller">
<dsp:input type="radio"  bean='<%= "CommunityPrincipalFormHandler.approveDeclineFlags["+ radioCount + "]" %>' value="no-decision"/>Later&nbsp;&nbsp;
<dsp:input type="radio"  bean='<%= "CommunityPrincipalFormHandler.approveDeclineFlags["+ radioCount + "]" %>' value="true"/><i18n:message key="community_member_request_approve"/>&nbsp;&nbsp;
<dsp:input type="radio"  bean='<%= "CommunityPrincipalFormHandler.approveDeclineFlags["+ radioCount + "]" %>' value="false"/><i18n:message key="community_member_request_decline"/>&nbsp;&nbsp;&nbsp;&nbsp;

<dsp:input type="hidden"  bean='<%= "CommunityPrincipalFormHandler.approveDeclinePrincipalIds["+ radioCount++ +"]" %>' value="<%= ((atg.userdirectory.User)(memRequests.getUser())).getPrimaryKey() %>"/>&nbsp;&nbsp;&nbsp;&nbsp;
</font></td>

<td nowrap><font class="adminbody"><%= firstName  %>&nbsp;</font></td>
<td nowrap><font class="adminbody"><%= lastName %>&nbsp;</font></td>
<td nowrap><font class="adminbody"><%= userEmail  %></font></td>

</tr>

</core:ForEach>


<tr>
 <td bgcolor="#666666" colspan=4><img src="images/clear.gif" height="1" width="250"></td>
</tr>

<tr>
<td  colspan="4">
<font class="small"><br>
<i18n:message id="done01" key="update" />
<dsp:input type="SUBMIT"  bean="CommunityPrincipalFormHandler.approveDeclineMembershipRequests"  value="<%= done01 %>" />
<i18n:message id="cancel01" key="reset" />&nbsp;
<input type="RESET" value="<%= cancel01 %>"/>
</font>
</td></tr>

</core:if>
 <core:defaultCase>
   <tr><td colspan=4><font class="smaller">

   <i18n:message key="community_member_request_no_requests"/>

   </font></td></tr>
 </core:defaultCase>

</core:exclusiveIf>

</table>

</table>
</admin:getMembershipRequests> 

</dsp:form>

</dsp:getvalueof><%-- role             --%>
</dsp:getvalueof><%-- dsp_community_id --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_page_id      --%>

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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users_membership.jsp#2 $$Change: 651448 $--%>
