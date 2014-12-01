<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
  
<html>
<head>
<title>
  <i18n:message key="title-register-success"/>
</title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>

<dsp:page>

<%
   // We get a community URL (the one that the user wanted to go to originally).
   // On the success page, we have to display the community name and the community URL.
   // So we get the communityURL as a parameter here, and extract out the 2nd portion
   // of it (or the first, if no second) as a hack for the ID.

   // Note: For deployment, the above is a bit of a hack, but since we have no
   // way to easily determine the community ID, this logic may have to be adjusted to 
   // conform to the actual context root and community URL format.
    
   String communityId = null;
   String relativeURL = "";
   String communityURL = "";
   {  
     communityURL = request.getParameter("communityURL");

     // Do some sanity checking to make sure this is set up right...     
     if (! atg.core.util.StringUtils.isBlank(communityURL) ) {
       
        int idx1=-1;
        int idx2=-1;
        int idx3=-1;

        idx1=communityURL.indexOf("/",1);
        if (idx1>0) {
          idx2=communityURL.indexOf("/",idx1+1);
          if (idx2>0) {
            idx3=communityURL.indexOf("/",idx2+1);
            if (idx3>0) {
              communityId = communityURL.substring(idx2+1,idx3);
            } else {
              idx3=communityURL.indexOf("?",idx2+1);
              if (idx3>0) {
                communityId = communityURL.substring(idx2+1,idx3);
              } else {
                idx3=communityURL.indexOf(";",idx2+1);
                if (idx3>0) {
                  communityId = communityURL.substring(idx2+1,idx3);
                } else {
                  communityId = communityURL.substring(idx2+1);
                }
              }
            }
          } else {
              idx2=communityURL.indexOf("?",idx1+1);
              if (idx2>0) {
                communityId = communityURL.substring(idx1+1,idx2);
              } else {
                idx2=communityURL.indexOf(";",idx1+1);
                if (idx2>0) {
                  communityId = communityURL.substring(idx1+1,idx2);
                } else {
                  communityId = communityURL.substring(idx1+1);
                }
              }
          }
        } else {
           communityId = communityURL.substring(1);
        }
     }            
   }
   
   String userMessage = request.getParameter("userMessage");
   if ( userMessage == null) {
        userMessage   = "";
   }
   String greetingName="";
   String pageBGColor=request.getParameter("col_pb");
   if (pageBGColor==null) {
      pageBGColor="999999";
   }
   String titleBGColor=request.getParameter("col_gtb");
   if (titleBGColor==null) {
      titleBGColor="336699";
   }
   String titleTextColor=request.getParameter("col_gtt");
   if (titleTextColor==null) {
      titleTextColor="CCCCCC";
   }
   String gearBGColor=request.getParameter("col_gb");
   if (gearBGColor==null) {
      gearBGColor="FFFFFF";
   }
   String gearTextColor=request.getParameter("col_gt");
   if (gearTextColor==null) {
      gearTextColor="000000";
   }
   
   String loginURL=request.getContextPath() +"/html/loginForm.jsp"; 
%>

<!--       
       relativeURL = <%= relativeURL %>       
       communityURL = <%= communityURL %>
       communityId= <%=communityId%>
-->

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

 <dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.transient" name="value"/>
  <%-- redirect if the user is not logged in --%>
  <dsp:oparam name="true">
         <core:CreateUrl id="redirectURL" url="<%= loginURL%>">
             <paf:encodeUrlParam param="successURL" value='<%=request.getParameter("successURL")%>'/>
             <core:Redirect url="<%= redirectURL.getNewUrl() %>"/>
         </core:CreateUrl>
  </dsp:oparam>
 </dsp:droplet>

<body   bgcolor='#<%=pageBGColor%>' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
<br><br>
<blockquote>
<table border="0" cellpadding="1" cellspacing="0"  bgcolor="#333333"><tr><td>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="#<%=gearBGColor%>"><tr><td valign="top">

<table width=100% border="0" cellspacing="0" cellpadding="0">

  <tr bgcolor="#<%= titleBGColor %>">
     <td colspan="2" align="center"><font class="large_bold" color="#<%=titleTextColor%>"><i18n:message key="registration-title"/></font></td>
  </tr>

  <tr bgcolor="#33333">
     <td colspan="2"><img src='<%= request.getContextPath() +"/images/clear.gif"%>' height="1" width="1" border="0"></td>
  </tr>
</table>


<center>
<table width=100% border="0" cellspacing="0" cellpadding="0">

<%-- establish columns --%>
  <tr>
    <td width="30%"><img src="/gear/user_registration/images/clear.gif" height="1" width="190" border="0"></td>
    <td width="30%"><img src="/gear/user_registration/images/clear.gif" height="1" width="100" border="0"></td>
    <td width="40%"><img src="/gear/user_registration/images/clear.gif" height="1" width="100" border="0"></td>
  </tr>


  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="/gear/user_registration/images/clear.gif" height="10" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td colspan="3" valign=middle align=center><font class="small" color="#<%=gearTextColor%>">
      <i18n:message key="registrationSuccessMsg"/>  
      <p>
      <core:ifNotNull value="<%= communityId %>">
      <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
        <dsp:param name="repository" value="/atg/portal/framework/PortalRepository"/>
        <dsp:param name="itemDescriptor" value="community"/>
        <dsp:param name="queryRQL" value="urlName = :urlName"/>
        <dsp:param name="urlName" value="<%= communityId %>"/>
        <dsp:oparam name="output">
          <i18n:message key="communityLinkMsg"/>
          <dsp:getvalueof id="urlName" idtype="java.lang.String" param="element.urlName">
            <core:createUrl id="url" url="<%= communityURL %>">      
              <a href="<%= url.getNewUrl() %>" class="gear_content"><font class="small"><dsp:valueof param="element.name">unnamed</dsp:valueof></font></a>               
            </core:createUrl>   
          </dsp:getvalueof>
          <i18n:message key="communityLinkEndMsg"/>
        </dsp:oparam>
	<dsp:oparam name="empty">
          <i18n:message key="communityNotFoundLinkMsg"/>
            <core:createUrl id="url" url="<%= communityURL %>">      
              <a href="<%= url.getNewUrl() %>" class="gear_content"><i18n:message key="communityNotFoundLinkText"/></a>               
            </core:createUrl>   
          <i18n:message key="communityNotFoundLinkEndMsg"/>
	</dsp:oparam>
      </dsp:droplet>
      </core:ifNotNull>
      </p>        
      </font></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="/gear/user_registration/images/clear.gif" height="10" width="1" border="0"></td>
  </tr>

<%--  Need something like this to provide the Membership request link, but need to initialize an enviroment first

<core:If value="<%=pafEnv.getCommunity().isAllowMembershipRequest() && !pafEnv.isLeader()%>">
     <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
         <dsp:oparam name="false">
	      <core:ExclusiveIf>
   		 <core:If value="<%=pafEnv.isMember()%>">
		    <core:CreateUrl id="memRequestUrl" url="/portal/settings/membership_unsubscribe.jsp">
       		      <core:UrlParam param="paf_community_id"  value="<%= pafEnv.getCommunity().getId()%>"/>
       		      <core:UrlParam param="origURL"  value="<%= pafEnv.getOriginalRequestURI()%>"/>
                      <td><b><a style="text-decoration:none" href="<%=memRequestUrl.getNewUrl()%>"><font face="Verdana,Arial,Helvetica,Geneva,Swiss,SunSans-Regular" color="#333333" size="-2"><i18n:message key="template_header_link_unsubscribe"/></font></a></b></td>
                      </core:CreateUrl>

         	   </core:If>
         	   <core:DefaultCase>
		      <core:CreateUrl id="memRequestUrl" url="/portal/settings/membership_request.jsp">
       		       	<core:UrlParam param="paf_community_id"  value="<%= pafEnv.getCommunity().getId()%>"/>
       			<core:UrlParam param="origURL"  value="<%= pafEnv.getOriginalRequestURI()%>"/>
                      	<td><b><a style="text-decoration:none" href="<%=memRequestUrl.getNewUrl()%>"><font face="Verdana,Arial,Helvetica,Geneva,Swiss,SunSans-Regular" color="#333333" size="-2"><i18n:message key="template_header_link_become_member"/></font></a></b></td>
                      </core:CreateUrl>
         	   </core:DefaultCase>
      	       </core:ExclusiveIf>
         </dsp:oparam>
     </dsp:droplet>
</core:If>
--%>

</td></tr></table>
</table>
</center>

</td></tr></table></td></tr></table>
</blockquote>
</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/registrationSuccessPage.jsp#2 $$Change: 651448 $--%>
