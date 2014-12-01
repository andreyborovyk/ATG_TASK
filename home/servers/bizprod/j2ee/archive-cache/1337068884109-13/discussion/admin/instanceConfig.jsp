<%-- 
Page:   	instanceConfig.jsp
Gear:  	 	Discussion Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This is the top-level page for the instance configuration mode of the discussion
		gear.  This page will include one of several pages based on the "config_page" 
		parameter. 
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%-- needed by @ includes --%>
<%@ taglib uri="/discussion-taglib" prefix="discuss" %>

<dsp:page>

<paf:hasCommunityRole roles="leader,gear-manager">
<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle baseName="atg.gears.discussion.discussionAdmin" localeAttribute="userLocale" changeResponseLocale="false" />

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String successURL = request.getParameter("paf_success_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String styleOn  = " class='smaller_bold' style='text-decoration:none;color:#000000;' ";
   String styleOn2  = " class='smaller_bold' style='color:#0000cc;' ";
   String styleOff = " class='smaller' ";
   String tempStyle = styleOff;
   String style = "";
   String configTarget = "ForumAdmin";  // DEFAULT VIEW  HERE
   if (  request.getParameter("config_page") != null ) {
      configTarget =  request.getParameter("config_page");
   }
 %>


<core:demarcateTransaction id="discussionInstanceConfigXA">
<%-- Display config options --%>
<table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">
    <tr>
      <td><font class="smaller">&nbsp;&nbsp;
      
	<core:CreateUrl id="mainURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>


<%
   if ( "AddGearForum".equals(configTarget) || 
        "TopicList".equals(configTarget)    || 
        "ThreadList".equals(configTarget) )    tempStyle =  styleOn2 ;  
%>

<% style = ("ForumAdmin".equals(configTarget)) ? styleOn : tempStyle ; %>
<nobr><a  href='<%= mainURL.getNewUrl() + "&config_page=ForumAdmin" %>' <%=style %> ><i18n:message key="forum-admin-link"/></a></nobr>


<font class="small">&nbsp;|&nbsp;</font>
<% style = ("DisplayParams".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a    href='<%= mainURL.getNewUrl() + "&config_page=DisplayParams" %>' <%=style %> ><i18n:message key="displayParamsLinkText"/></a></nobr>

<%--  I think this has been removed --%>

<%--<font class="small">&nbsp;|&nbsp;</font>
<% style = ("DisplayText".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a   href='<%= mainURL.getNewUrl() + "&config_page=DisplayText" %>' <%=style %> ><i18n:message key="displayTextLinkText"/></a></nobr>
--%>

<font class="small">&nbsp;|&nbsp;</font>
<% style = ("Permissions".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a  href='<%= mainURL.getNewUrl() + "&config_page=Permissions" %>'  <%=style %> ><i18n:message key="permissionsLinkText"/></a></nobr>


<font class="small">&nbsp;|&nbsp;</font>
<% style = ("alerts".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a href='<%= mainURL.getNewUrl() + "&config_page=alerts" %>'  <%=style %> ><i18n:message key="alertsLinkText"/></a></nobr>

<font class="small">&nbsp;|&nbsp;</font>
<% style = ("resourceBundle".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a href='<%= mainURL.getNewUrl() + "&config_page=resourceBundle" %>'  <%=style %> ><i18n:message key="resourcesLinkText"/></a></nobr>

        </core:CreateUrl>
     </font></td>
    </tr>
   </table>
</core:demarcateTransaction>

   <core:ExclusiveIf>

      <core:If value='<%="ForumAdmin".equals(configTarget)%>'>
	<jsp:include page="gearForumAdmin.jsp" flush="true" />
      </core:If>

      <core:If value='<%="DisplayParams".equals(configTarget)%>'>
	<jsp:include page="configDisplayParams.jsp" flush="true" />
      </core:If>

      <core:If value='<%="Permissions".equals(configTarget)%>'>
	<jsp:include page="configPermissions.jsp" flush="true" />
      </core:If>

      <core:If value='<%="alerts".equals(configTarget)%>'>
	<!-- need to use @include directive since this form will issue a redirect -->
	<%@ include file="configAlerts.jspf" %>
      </core:If>

      <core:If value='<%="resourceBundle".equals(configTarget)%>'>
	<jsp:include page="configResourceBundle.jsp" flush="true" />
      </core:If>

      <core:If value='<%="AddGearForum".equals(configTarget)%>'>
	<!-- need to use @include directive since this form will issue a redirect -->
	<%@ include file="addGearForum.jspf" %>
      </core:If> 

      <core:If value='<%="TopicList".equals(configTarget)%>'>
	<jsp:include page="topicListAdmin.jsp" flush="true" />
      </core:If>

      <core:If value='<%="ThreadList".equals(configTarget)%>'>
	<jsp:include page="threadListAdmin.jsp" flush="true" />
      </core:If>

  </core:ExclusiveIf>


</paf:InitializeGearEnvironment>
</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
