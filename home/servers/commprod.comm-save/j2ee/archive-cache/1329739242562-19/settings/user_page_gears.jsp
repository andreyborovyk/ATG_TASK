<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
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

<paf:setFrameworkLocale/>

<i18n:bundle baseName="atg.portal.admin.UserSettingsResource" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:RegisteredUserBarrier/>
<admin:InitializeUserAdminEnvironment id="userAdminEnv">

<dsp:importbean bean="/atg/portal/admin/PageGearsFormHandler" />

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageGearsFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="PageGearsFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageGearsFormHandler.reset"/>
  </dsp:oparam>
 </dsp:droplet> 

<dsp:setvalue bean="PageGearsFormHandler.community"  value="<%= userAdminEnv.getCommunity() %>" />
<dsp:setvalue bean="PageGearsFormHandler.page"  value="<%=userAdminEnv.getPage() %>" />


<% String highLight = "gears"; %>
<%@include file="user_page_nav.jspf" %>

<img src="images/clear.gif" width="1" height"1" border="0"><br>
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
 
 <!-- dont think we need this
<font class="smaller"><i18n:message key="user_pages_basic_available_gears"/></font><br>
-->


<font class="smaller"><i18n:message key="user_pages_basic_available_gears_helper"/></font><br><br>

<%-- The list of gears that are in community are shown as unselected. The list of gears
that are in page property of the regions in a page are shown as selected. --%>


<table border=0 cellspacing=0 cellpadding=0>
<tr><td valign="top">
<table border=0 cellspacing=0 cellpadding=1>

<dsp:form action="user.jsp" method="POST" synchronized="/atg/portal/admin/PageGearsFormHandler">
<%
 int totalGears =  userAdminEnv.getSourceCommunity().getGearSet().size();
 int splitGear  = 100;
 int countGear  = 1 ;
 boolean splitCol = (totalGears > 15) ;
 if ( splitCol ) {
   splitGear =(totalGears / 2)+1;
 }
%>

<core:ForEach id="allgears"
      values="<%= userAdminEnv.getSourceCommunity().getGearSet(atg.portal.framework.Comparators.getGearComparator()) %>"
      castClass="atg.portal.framework.Gear"
      elementId="gear">
 <tr>
  <td width="10"><dsp:input type="checkbox" bean="PageGearsFormHandler.gears" value="<%= gear.getId() %>"/></td>
  <td nowrap><font class="smaller">&nbsp;&nbsp;<%= gear.getName(response.getLocale()) %></font></td>
  </tr>

<%
 if (splitCol) {
   if ( splitGear == countGear ) {
%>
 </table></td>
 <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 <td valign="top">
<table border=0 cellspacing=0 cellpadding=1>
<% }
}
 countGear++;
%>

 </core:ForEach>

</table>
</td></tr></table>
<br>
</font>

<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/user.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageGearsFormHandler.successURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLsuccess.getNewUrl())%>"/>
   <dsp:input type="hidden"  bean="PageGearsFormHandler.failureURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLsuccess.getNewUrl())%>"/>

</core:CreateUrl>


</font>
<i18n:message id="submitLabel" key="update"/>
<dsp:input type="SUBMIT" value="<%=submitLabel%>"  bean="PageGearsFormHandler.updateGearsUserMode"/>
</td></tr></table>

<!-- end inc_gears  -->
</dsp:form>

</admin:InitializeUserAdminEnvironment>
</dsp:page>  
     
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/user_page_gears.jsp#2 $$Change: 651448 $--%>
