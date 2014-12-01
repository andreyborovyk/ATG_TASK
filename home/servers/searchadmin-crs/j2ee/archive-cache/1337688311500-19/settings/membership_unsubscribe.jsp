<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />
<dsp:page>

<%-- This page will process a membership "unsubscribe" request for the current user in the
     community specified on the URL.  Process will differ depending on the 
     "Membership Request Mode" of the community, which can be allowed or disallowed,
     and if allowed, automatic or manual

     NOTE:  modification - changing this page so that it will _always_ submit and unsubscribe
            request as "automatic".
--%>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%  // set this so we can get an environment with a valid community
    request.setAttribute("atg.paf.Community", request.getParameter("paf_community_id")); 
%>

<paf:InitializeEnvironment id="pafEnv">

<html>
<head>
<title><i18n:message key="title-community_membership_unsubscribe"/></title>

 <core:ExclusiveIf>
  <core:IfNotNull value="<%=  pafEnv.getCommunity().getStyle() %>">

   <core:CreateUrl id="ccc_encoded_url" url="<%= pafEnv.getCommunity().getStyle().getCSSURL() %>">
    <link rel="STYLESHEET" type="text/css" href="<%= ccc_encoded_url.getNewUrl() %>" src="<%= ccc_encoded_url.getNewUrl() %>">
  </core:CreateUrl>

  </core:IfNotNull>

  <core:DefaultCase>
    <style type="text/css">
      <!--
       body { font-family:verdana,arial,geneva,helvetica,san-serif ; }
       font {font-family:verdana,arial,geneva,helvetica,san-serif ; }      
      -->
    </style>
  </core:DefaultCase>
 </core:ExclusiveIf>

 <style type="text/css">
 
 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}
 
 a.useradmin_nav       { text-decoration: none; }
 a.useradmin_leftnav   { text-decoration: none; }
 
 a.breadcrumb   {  font-size: 10px; text-decoration:none; }
 a.gear_nav     { font-weight:700; text-transform:lowercase; text-decoration:none; }
 a.gear_content { font-weight:300 }
 a.admin_link   { font-size: 10px; font-weight:300; text-decoration:none; }

  .smaller {font-size:10px; }
  .small   {font-size:12px; }
  .medium  {font-size:13px; }
  .large   {font-size:15px; }
  .larger  {font-size:17px; }

  .smaller_bold   {font-size:10px; font-weight:700 }
  .small_bold     {font-size:12px; font-weight:700 }
  .medium_bold    {font-size:13px; font-weight:700 }
  .large_bold     {font-size:15px; font-weight:700 }
  .larger_bold    {font-size:17px; font-weight:700 }
  .humungous_bold { font-size:22px;font-weight:700}
 
  .breadcrumb_link {font-size:10px; color : 0000cc}
  .small_link {font-size:12px; color : 0000cc}
 
  .error  {font-size:10px; color : cc0000;  }
  .info   {font-size:10px; color : 000000;  }
 
  .helpertext {font-size:10px; color : 333333}
  .adminbody {font-size:10px; color : 333333}
  .subheader{font-size:10px; color : 333333; font-weight:700}
  .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
  .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}
 
  .side-reg       { color:0000cc; font-size:8pt; }
  .side-on-main   { color:000000; font-size:8pt; font-weight:700; text-decoration:none;  }
  .side-on-return { color:0000cc; font-size:8pt; font-weight:700; }

</style>

</head>
<%
   String origURL=request.getParameter("origURL");
   String pageBGColor=request.getParameter("col_pb");
   if (pageBGColor==null) {
       pageBGColor="ffffff";
   }
   atg.portal.framework.Community community=pafEnv.getCommunity();
   String clearGif =  response.encodeURL("images/clear.gif");
%>

<body   bgcolor='#<%=pageBGColor%>' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>

<div class="user_admin_header_bg_img" ><img src="<%=clearGif%>" height="61" width="680" border="0" alt="" /></div>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- Make sure this business is actually allowed --%>
<core:If value="<%=community.isAllowMembershipRequest()%>">
<dsp:droplet name="Switch">
  <dsp:param bean="Profile.transient" name="value"/>
    <dsp:oparam name="false">

	<core:ExclusiveIf>
     	  <core:IfNotNull value="<%=community%>">
            <core:ExclusiveIf>
	     <%-- check if user already a member --%>
             <core:IfNot value="<%=pafEnv.isMember()%>">
	       <center>
   	       <br>
               <font class="small"><i18n:message key="unsubscribe_not_member"/></font>
	       <br>
	       <core:IfNotNull value="<%=origURL%>">
		  <core:CreateUrl id="backURL" url="<%=origURL%>">
		     <br><dsp:a href="<%= backURL.getNewUrl() %>"><i18n:message key="mem_request_back_link"/></dsp:a>
		  </core:CreateUrl>
	       </core:IfNotNull>
	       </center>
             </core:IfNot>


             <core:DefaultCase>
		  <%-- process request and redirect back from whence they came, if available --%>
		  <dsp:getvalueof id="profile" bean="Profile">
		  <paf:membershipRequest id="autoMemRequest"
		                         user="<%= (atg.userprofiling.Profile)profile%>"
		                         userRole="member"
		                         communityId="<%= community.getId()%>"
		                         env="<%= pafEnv%>"
					 autoMembership="<%=true%>"
					 enrollMode="unsubscribe">

		   <core:ExclusiveIf>
		      <core:If value="<%=autoMemRequest.getSuccess()%>">
		         <core:ExclusiveIf>
	                  <core:IfNotNull value="<%=origURL%>">
		             <core:CreateUrl id="backURL" url="<%=origURL%>">

                              <core:If value="<%=( community.getAccessLevel() < 2 ) %>">
                                 <core:Redirect url="<%= backURL.getNewUrl() %>"/>
                              </core:If>
                              <core:If value="<%=( community.getAccessLevel() > 1 ) %>">
                                <center>
	 	                <br>
  	                         <font class="small">
                                 <i18n:message key="unsubscribe_processed_param"> 
                                  <i18n:messageArg value="<%=community.getName(response.getLocale()) %>"/>
                                 </i18n:message>

                                <br><br>

                                 <i18n:message key="unsubscribe_processed_now_not_allowed_param">
                                  <i18n:messageArg value="<%=community.getName(response.getLocale()) %>"/>
                                 </i18n:message>
                                </font>
			       <br>
                              </core:If>

		             </core:CreateUrl>
	                  </core:IfNotNull>
	                  <core:DefaultCase>
		            <center>
		             <br>
  	                      <font class="small">
                              <i18n:message key="unsubscribe_processed_param"> 
                               <i18n:messageArg value="<%=community.getName(response.getLocale()) %>"/>
                              </i18n:message>
 
                               <core:If value="<%=( community.getAccessLevel() > 1 ) %>">
                                <br><br>
                                 <i18n:message key="unsubscribe_processed_now_not_allowed_param">
                                 <i18n:messageArg value="<%=community.getName(response.getLocale()) %>"/>
                                </i18n:message>
                              </core:If>

                              </font>
			     <br>
			    </center>
	                  </core:DefaultCase>
		         </core:ExclusiveIf>
		      </core:If> 
	              <core:DefaultCase>
		        <i18n:message key="mem_request_error"/> 
	              </core:DefaultCase>
		   </core:ExclusiveIf>
		  </paf:membershipRequest>
		  </dsp:getvalueof>

             </core:DefaultCase>
            </core:ExclusiveIf>
          </core:IfNotNull>

          <core:DefaultCase>
             <i18n:message key="mem_request_no_comm_id"/>
          </core:DefaultCase>
       </core:ExclusiveIf>

    </dsp:oparam>

    <dsp:oparam name="default">
       <center>
	<br><br><font class="small"><i18n:message key="unsubscribe_not_logged_in"/></font><br><br>
	<core:IfNotNull value="<%=origURL%>">
	   <core:CreateUrl id="backURL" url="<%=origURL%>">
		<br><dsp:a href="<%= backURL.getNewUrl() %>"><i18n:message key="mem_request_back_link"/></dsp:a>
	   </core:CreateUrl>
	</core:IfNotNull>
       </center>
    </dsp:oparam>
</dsp:droplet>

</core:If>

<core:IfNot value="<%=community.isAllowMembershipRequest()%>">
  <center>
  <br><br>
  <font class="small"><i18n:message key="unsubscribe_not_allowed"/></font><br>
  <core:IfNotNull value="<%=origURL%>">
   <core:CreateUrl id="backURL" url="<%=origURL%>">
	<br><dsp:a href="<%= backURL.getNewUrl() %>"><i18n:message key="mem_request_back_link"/></dsp:a>
   </core:CreateUrl>
  </core:IfNotNull>
  </center>
</core:IfNot>

</body>
</html>
</paf:InitializeEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/membership_unsubscribe.jsp#2 $$Change: 651448 $--%>
