<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />


<% 
   String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif";

%>




<font class="smaller">
  <core:CreateUrl id="returnURL" url="community_gears.jsp">
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
      <a href="<%= returnURL.getNewUrl() %>" style="text-decoration:none"><i18n:message key="config-back-link"/></a>
  </core:CreateUrl></b>
</font>
<br><br>
  
  
  
  
  <i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="instance-config-title">
 <i18n:messageArg value='<%= gearEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>  
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="admin_instance_header_helper" />
        </td></tr></table>

<img src="<%=clearGif%>" height="1" width="1" border="0"><br>


<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>

    <%-- ****** Display Parameters ****** --%>

 <font class="smaller_bold"><i18n:message key="instance-config-dp-title"/></font><br>
  
 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
    <tr bgcolor="#dddddd">
      <td width="30%"><font class="smaller"><i18n:message key="share-page-count-label"/></font>&nbsp;</td>
      <td><font class="smaller"><%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %></font>&nbsp;</td>
    </tr>

    <tr bgcolor="#ffffff">
      <td><font class="smaller"><i18n:message key="full-page-count-label"/></font>&nbsp;</td>
      <td><font class="smaller"><%= gearEnv.getGearInstanceParameter("fullDisplayCount") %></font>&nbsp;</td>
    </tr>
     <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
   </table>
	 
  <core:CreateUrl id="editGearUrl" url="<%= gearEnv.getOriginalRequestURI()  %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="config_page" value="displayParams"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
        <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
        <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
        <paf:encodeUrlParam param="paf_success_url" value='<%= request.getParameter("paf_success_url") %>' />
        <a href="<%= editGearUrl.getNewUrl() %>"><font class="smaller"><i18n:message key="disp-param-link"/></font></a>
      </core:CreateUrl>
    <br><br>
	        <font class="smaller_bold"><i18n:message key="instance-config-sp-title"/></font>
    
	
 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
    <tr bgcolor="#dddddd">
      <td width="30%"><font class="smaller"><i18n:message key="sort-field-label"/></font>&nbsp;</td>
      <td><font class="smaller"><%= gearEnv.getGearInstanceParameter("defaultSortField") %></font>&nbsp;</td>
    </tr>

    <tr bgcolor="#ffffff">
      <td><font class="smaller"><i18n:message key="sort-order-label"/></font>&nbsp;</td>
      <td><font class="smaller"><%= gearEnv.getGearInstanceParameter("defaultSortOrder") %></font>&nbsp;</td>
    </tr>
  <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
	  </table>

      <core:CreateUrl id="editGearUrl" url="<%=  gearEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="config_page" value="sortParams"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
        <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
        <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
        <paf:encodeUrlParam param="paf_success_url" value='<%= request.getParameter("paf_success_url") %>' />
        <a href="<%= editGearUrl.getNewUrl() %>"><font class="smaller"><i18n:message key="sort-param-link"/></a>
      </core:CreateUrl>
  
  </td></tr></table>
  
  <br><br>
  

</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/admin/displayInstanceConfig.jsp#2 $$Change: 651448 $--%>
