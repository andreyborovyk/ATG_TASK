<%@ taglib uri="/dsp-taglib" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
  <dsp:page>
    <paf:InitializeGearEnvironment id="pafEnv">
      <%@ include file="../includeBundle.jspf" %>
      <i18n:message id="sharedURLLabel" key="displayConfigSharedURLLabel"/>
      <i18n:message id="fullURLLabel" key="displayConfigFullURLLabel"/>
      <i18n:message id="fullModeLinkTextLabel" key="displayConfigFullModeLinkTextLabel"/>
      <i18n:message id="resourceBundleLabel" key="updateResourceBundleLabel"/>
      
     <i18n:message id="alertNoOptionDesc" key="displayConfigAlertNoOptionDesc"/>
     <i18n:message id="alertLockedOptionDesc" key="displayConfigAlertLockedOptionDesc"/>
     <i18n:message id="alertOpenedOptionDesc" key="displayConfigAlertOpenedOptionDesc"/>

      
      <%
         String origURI= pafEnv.getOriginalRequestURI();
         String pageID = request.getParameter("paf_page_id");
         String gearID = request.getParameter("paf_gear_id");
         String pageURL = request.getParameter("paf_page_url");
         String communityID = request.getParameter("paf_community_id");
         String communityAdminURI="/portal/settings/community_gears.jsp";
         String contextPath = pafEnv.getGearDefinition().getServletContext();
      %>
	  

<img src="<%= contextPath %>/images/clear.gif" height=1 width=1 border=0><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<br>
<font class="smaller_bold"><i18n:message key="displayConfigBasicConfigLabel"/></font>
     
	 
 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
   <tr><td bgcolor="#666666" colspan="2"><img src="<%= contextPath %>/images/clear.gif" height=1 width=1 border=0></td></tr>
      <tr bgcolor="#dddddd">
            <td width="50%">
            <font class="smaller"><%= sharedURLLabel %></font></td>
            <td>
            <font class="smaller">
          <core:IfNotNull value='<%= pafEnv.getGearInstanceParameter("instanceSharedURL") %>'>
           <%= pafEnv.getGearInstanceParameter("instanceSharedURL") %>
           </core:IfNotNull>&nbsp;
           </font></td>
      </tr>
      <tr bgcolor="#ffffff">
            <td>
            <font class="smaller"><%= fullURLLabel %></font></td>
            <td>
            <font class="smaller">
          <core:IfNotNull value='<%= pafEnv.getGearInstanceParameter("instanceSharedURL") %>'>
          <%= pafEnv.getGearInstanceParameter("instanceFullURL") %>
          </core:IfNotNull>&nbsp;
      </font></td>
      </tr>
      <tr bgcolor="#dddddd">
            <td>
            <font class="smaller"><%= fullModeLinkTextLabel %></font></td>
            <td>
            <font class="smaller">
          <core:IfNotNull value='<%= pafEnv.getGearInstanceParameter("fullModeLinkText") %>'>
           <%= pafEnv.getGearInstanceParameter("fullModeLinkText") %>
           </core:IfNotNull>&nbsp;
           </font></td>
      </tr>
     
      <tr bgcolor="#ffffff">
      <td><font class="smaller"><%= resourceBundleLabel %></font></td>
            <td>
            <font class="smaller">
          <core:IfNotNull value='<%= pafEnv.getGearInstanceParameter("resourceBundleName") %>'>
          <%= pafEnv.getGearInstanceParameter("resourceBundleName") %>
          </core:IfNotNull>
      </font></td>
      </tr>
	     <tr><td bgcolor="#666666" colspan="2"><img src="<%= contextPath %>/images/clear.gif" height=1 width=1 border=0></td></tr>
       </table>
<br>
<font class="smaller_bold"><i18n:message key="displayConfigAlertConfigLabel"/></font>

 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
   <tr><td bgcolor="#666666" colspan="2"><img src="<%= contextPath %>/images/clear.gif" height=1 width=1 border=0></td></tr>
      <tr bgcolor="#dddddd">
      <td width="50%">
      <font class="smaller"><i18n:message key="displayConfigAlertOptionsLabel"/></font></td>
      <core:exclusiveIf>
       <core:if value='<%="yes_locked".equals(pafEnv.getGearInstanceParameter("globalAlertMode"))%>'>
       <td><font class="smaller"><%= alertLockedOptionDesc %></font></td>
       </core:if>

       <core:if value='<%="yes_opened".equals(pafEnv.getGearInstanceParameter("globalAlertMode"))%>'>
       <td><font class="smaller"><%= alertOpenedOptionDesc %></font></td>
       </core:if>

       <core:defaultCase>
       <td>
       <font class="smaller"><%= alertNoOptionDesc %></font></td>
       </core:defaultCase>
      </core:exclusiveIf>
    </tr>
	 </table>
   <br><br>
   </td></tr>
</table>

      
    </paf:InitializeGearEnvironment>
  </dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/html/admin/displayInstanceConfig.jsp#2 $$Change: 651448 $--%>
