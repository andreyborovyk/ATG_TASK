<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
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

<%
  boolean hasLocalAccess = false;
%>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,page-manager" >
<% hasLocalAccess = true; %>

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">


<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<i18n:message key="imageroot" id="i18n_imageroot"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>

<title><i18n:message key="title-community_pages"/></title>


<%
    String clearGif =  response.encodeURL("images/clear.gif");
    String thisNavHighlight = "pages"; 
    String mode          = "1";


    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to community pages
    }

%>

<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="pages" /> 
</dsp:include>



<br /><center>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>
<td width="150" valign="top">

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="sideBarIndex" value="2" /> 
 </dsp:include>

</td>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>

<td valign="top" width="80%" align="left">

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageFormHandler.formError"/>
  <dsp:oparam name="true">
     <dsp:setvalue bean="PageFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>
 
 <core:Switch value="<%= mode%>">
 
  <core:Case value="5"><%-- delete confirm page --%>
   <dsp:include  page="community_pages_confirm_delete.jsp" flush="false"/>
  </core:Case>

  <core:Case value="6"><%-- edit specific page basic --%>
    <dsp:include page="community_pages_edit_basic.jsp" flush="false"/>
  </core:Case>
  <core:Case value="7"><%-- edit specific page basic --%>
    <dsp:include page="community_pages_edit_gears.jsp" flush="false"/> 
  </core:Case>
  <core:Case value="8"><%-- edit specific page basic --%>
    <dsp:include page="community_pages_edit_layout.jsp" flush="false"/>
  </core:Case>
  <core:Case value="9"><%-- edit specific page basic --%>
    <dsp:include page="community_pages_edit_color.jsp"  flush="false"/>
  </core:Case>

  <core:Case value="10"><%-- edit page access --%>
    <dsp:include page="community_pages_access_editor.jsp"  flush="false"/>
  </core:Case>



  <core:Case value="2"><%-- new blank page --%>
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" bean="PageFormHandler.formError"/>
     <dsp:oparam name="true">
       <dsp:setvalue bean="PageFormHandler.resetFormExceptions"/>
    </dsp:oparam>
    <dsp:oparam name="false">
      <%--Don't reset the form it there is form exception --%>
      <dsp:setvalue bean="PageFormHandler.reset"/>
    </dsp:oparam>
  </dsp:droplet>

   <dsp:include page="community_pages_new.jsp" flush="false"/>

  </core:Case>

  <core:Case value="3"><%-- about pages --%>
  
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
<i18n:message key="community_pages_about_page_header"/>
</td></tr></table>
</td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="small"><br /><blockquote>
    <i18n:message key="helpertext-about-pagesA"/><br /><br />
    <i18n:message key="helpertext-about-pagesB"/><br /><br />
    <i18n:message key="helpertext-about-pagesC"/> 
</font></blockquote>
</td></tr></table>
  </core:Case>


  <core:DefaultCase><%-- mode=1 --%>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
        <i18n:message id="i18n_bold" key="html-bold"/>
        <i18n:message id="i18n_end_bold" key="html-end-bold"/>
        <i18n:message key="community_main_pages_boxtop_params">
         <i18n:messageArg value="<%= adminEnv.getCommunity().getName() %>"/>
         <i18n:messageArg value="<%=i18n_bold%>"/>
         <i18n:messageArg value="<%=i18n_end_bold%>"/>
       </i18n:message>
	</font></td></tr></table>
    </td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller"><i18n:message key="community_pages_helpertext"/></font>
</td></tr></table>
<img src='<%=clearGif %>' height="1" width="20"  alt="" /><br />

     <table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
      <table border=0 cellspacing=0 cellpadding=2>
            <% 
              boolean notJustOne = ( adminEnv.getCommunity().getPages().length > 1) ;
              int firstItem = 1;
              %>
           

           <i18n:message id="i18n_linkdelete"  key="community_main_pages_linkdelete"/>
           <i18n:message id="i18n_pagedefault" key="community_main_pages_default"/>
           <i18n:message id="i18n_pageaccess"  key="community_main_pages_access"/>
           <i18n:message key="community_settings_discription" id="discriptionLabel"/>


           <core:ForEach id="childpages"
                          values="<%= adminEnv.getCommunity().getPageSet() %>"
                          castClass="atg.portal.framework.Page"                          
                          elementId="pageAtt">

              <tr>
        <core:ExclusiveIf>
         <core:If value='<%= pageAtt.getDescription() != null && (!(pageAtt.getDescription().trim()).equals("")) %>'>
                <td  valign="bottom" ><img src='<%= response.encodeURL("images/discription.gif")%>' alt='<%= discriptionLabel+"\n"+ pageAtt.getDescription() %>' /></td>
         </core:If>
         <core:DefaultCase>
              <td><font class="adminbody">&nbsp;</font></td>
         </core:DefaultCase>
       </core:ExclusiveIf>

                <td nowrap><font class="adminbody"><%= pageAtt.getName() %>&nbsp;&nbsp;</font></td>
<i18n:message id="visitCommLink" key="community_main_pages_linkview"/>
                <core:ExclusiveIf>
                  <core:If value='<%= adminEnv.getCommunity().isEnabled() %>'>
  <core:CreateUrl id="viewingUrl" url="<%= adminEnv.getPageURI(pageAtt.getId()) %>">
   <core:UrlParam param="paf_default_view" value="true"/>
                    <td nowrap><font class="adminbody"><dsp:a href="<%=viewingUrl.getNewUrl() %>"><%=visitCommLink%></dsp:a>&nbsp;&nbsp;</font></td>
  </core:CreateUrl>
                  </core:If>
                  <core:DefaultCase>
                    <td></td>
                  </core:DefaultCase>
                </core:ExclusiveIf>
                
                <core:CreateUrl id="editCommAdminPageUrl" url="/portal/settings/community_pages.jsp">
                 <core:UrlParam param="mode" value="6"/><%-- basic --%>
                  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
                  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
                  <core:UrlParam param="paf_page_id" value="<%= pageAtt.getId() %>"/>

                  <td nowrap><font class="adminbody"><dsp:a href="<%= editCommAdminPageUrl.getNewUrl() %>" ><i18n:message key="community_main_pages_linkedit"/></dsp:a>&nbsp;&nbsp;</font></td>
                </core:CreateUrl>
   
                <core:CreateUrl id="editCommAdminPageUrl" url="/portal/settings/community_pages.jsp">
                 <core:UrlParam param="mode" value="10"/><%-- access --%>
                  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
                  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
                  <core:UrlParam param="paf_page_id" value="<%= pageAtt.getId() %>"/>

                  <td nowrap><font class="adminbody"><dsp:a href="<%= editCommAdminPageUrl.getNewUrl() %>" ><i18n:message key="community_main_pages_access"/></dsp:a>&nbsp;&nbsp;</font></td>
                </core:CreateUrl>


                <core:If value="<%= ! (adminEnv.getCommunity().getDefaultPage().getId()).equals(pageAtt.getId())%>">
               <core:If value="<%=notJustOne %>">
                <core:CreateUrl id="deleteUrl" url="community_pages.jsp" >
                  <core:UrlParam param="mode" value="5"/>
                  <core:UrlParam param="delId" value="<%= pageAtt.getId() %>"/>
                  <core:UrlParam param="delCancel" value="community_pages"/>
                  <core:UrlParam param="paf_page_id" value="<%= pageAtt.getId() %>"/>
                  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
                  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>

                  <td><font class="adminbody"><dsp:a href="<%= deleteUrl.getNewUrl() %>" ><%=i18n_linkdelete%></dsp:a>
                  </font></td>
                </core:CreateUrl>
                </core:If>
                </core:If>
                <core:If value="<%=(adminEnv.getCommunity().getDefaultPage().getId()).equals(pageAtt.getId())%>">
                  <td><font class="adminbody" color="#666666"><%=i18n_pagedefault%></font></td>
                </core:If>
              </tr>
           <%--   <core:IfNotNull value="<%= pageAtt.getDescription() %>">
              <tr>
               <td colspan="5"><font size="-2"><%= pageAtt.getDescription() %></font></td>
              </tr>
              </core:IfNotNull>
            --%>
            </core:ForEach>

     
          </table>


</td>
</tr>
</table>

  </core:DefaultCase><%-- mode=1 --%>
</core:Switch>


</td>
</tr>
</table>
</center>

</body>
</html>

</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_communiyt_id --%>
                

</admin:InitializeAdminEnvironment>

  <% } catch (RuntimeException e) { 

   e.printStackTrace();
  %>
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
<paf:hasCommunityRole roles="settings-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_settings.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>
<paf:hasCommunityRole roles="user-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_users.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>
<paf:hasCommunityRole roles="gear-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_gears.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>

<paf:hasCommunityRole roles="leader,settings-manager,page-manager,gear-manager,user-manager" barrier="true"/>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages.jsp#2 $$Change: 651448 $--%>
