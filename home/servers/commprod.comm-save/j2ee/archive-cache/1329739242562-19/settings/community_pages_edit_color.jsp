<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/PageColorFormHandler"/>
<dsp:importbean bean="/atg/portal/framework/admin/ColorLookupDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageColorFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="PageColorFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageColorFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet> 
 
<dsp:form action="community_pages.jsp" method="post" synchronized="/atg/portal/admin/PageColorFormHandler">


<%@include file="fragments/community_pages_edit_nav.jspf"%>


<%
atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
String dsp_page_id      = dynamoRequest.getParameter("paf_page_id");
String dsp_community_id = dynamoRequest.getParameter("paf_community_id");
String dsp_ppage_id     = dynamoRequest.getParameter("paf_ppage_id");
String dsp_page_url     = dynamoRequest.getParameter("paf_page_url");
String mode             = dynamoRequest.getParameter("mode");
String clearGif         = response.encodeURL("images/clear.gif"); 


String dsp_color_id = "";

if ( request.getParameter("paf_color_id") != null) {
    dsp_color_id = dynamoRequest.getParameter("paf_color_id");
} else {
    dsp_color_id =  adminEnv.getPage().getColorPalette().getId(); 
}

%>
<dsp:setvalue bean="PageColorFormHandler.colorPalette" value="<%=dsp_color_id%>"/>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

  <!-- yyyyyyyyyyyyyyyyyyy  -->
  <table width=500 cellspacing=0 cellpadding="0" border="0">
    <tr>
      <td><img src="<%=clearGif%>" height="5" width="200" border="0"></td>
      <td><img src="<%=clearGif%>" height="5" width="14"  border="0"></td>
      <td><img src="<%=clearGif%>" height="5" width="280" border="0"></td>
    </tr><tr>
      <td valign="top" align="center">

    
   <core:CreateUrl id="recursiveUrl" url="community_pages.jsp">
    <core:UrlParam param="mode" value="9"/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id")%>'/>
    <core:UrlParam param="paf_pcommunity_id" value='<%= request.getParameter("paf_pcommunity_id")%>'/>
    <core:UrlParam param="paf_ppage_id" value='<%= request.getParameter("paf_ppage_id")%>'/>
    <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>

    <table width=140 cellspacing=0 cellpadding="0" border="0">
      <tr>
        <td width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
        <td width="30"><img src="<%=clearGif%>" width="30" height="1" border="0"></td>
        <td width="20"><img src="<%=clearGif%>" width="20" height="1" border="0"></td>
      </tr>
      <core:ForEach id="colors"
        values="<%= adminEnv.getColorPalettes() %>"
        castClass="atg.portal.framework.ColorPalette"
        elementId="color">
        <!-- one preview -->
        <tr>
          <td width="1"><img src="<%=clearGif%>" width="1" height="22" border="0"></td>

          <core:ExclusiveIf>
      <core:IfNotNull value="<%= ((atg.portal.framework.ColorPalette) color).getSmallImageURL() %>">
        <td><img src="<%= ((atg.portal.framework.ColorPalette) color).getSmallImageURL() %>" border=0></td>
      </core:IfNotNull>
      <core:DefaultCase>
        <td>
      <table border=0 width=25 cellpadding=0 cellspacing=0>
       <tr>
        <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
        <td bgcolor="#333333" width="23"><img src="<%=clearGif%>" width="23" height="1" border="0"></td>
        <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
      </tr><tr>
        <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="15" border="0"></td>
        <td bgcolor="#<%= color.getPageBackgroundColor() %>" width="23" align="center"><table width="20" cellpadding="0" cellspacing="0" border="0">
        <tr>
         <td width="7"><img src="<%=clearGif%>" width="7" height="1" border="0"></td>
         <td width="2"><img src="<%=clearGif%>" width="2" height="1" border="0"></td>
         <td width="11"><img src="<%=clearGif%>" width="11" height="1" border="0"></td>
       </tr><tr>
         <td bgcolor="#<%= color.getGearTitleBackgroundColor() %>"><img src="<%=clearGif%>" width="1" height="2" border="0"></td>
         <td></td>
         <td bgcolor="#<%= color.getGearTitleBackgroundColor() %>"><img src="<%=clearGif%>" width="1" height="2" border="0"></td>
       </tr><tr>
        <td><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
        <td></td>
        <td bgcolor="#<%= color.getGearBackgroundColor() %>"><img src="<%=clearGif%>" width="1" height="2" border="0"></td>
      </tr><tr>
        <td bgcolor="#<%= color.getGearTitleBackgroundColor() %>"><img src="<%=clearGif%>" width="1" height="2" border="0"></td>
        <td></td>
        <td bgcolor="#<%= color.getGearBackgroundColor() %>"><img src="<%=clearGif%>" width="1" height="2" border="0"></td>
       </tr><tr>
        <td></td>
        <td></td>
        <td bgcolor="#<%= color.getGearBackgroundColor() %>"><img src="<%=clearGif%>" width="4" height="2" border="0"></td>
       </tr>
      </table></td>
      <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
     </tr><tr>
      <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
      <td bgcolor="#333333" width="23"><img src="<%=clearGif%>" width="23" height="1" border="0"></td>
      <td bgcolor="#333333" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
     </tr></table></td>
    </core:DefaultCase>
    </core:ExclusiveIf>

          <td nowrap><font class="smaller">&nbsp;&nbsp;<dsp:a href='<%= recursiveUrl.getNewUrl()+"&paf_color_id="+atg.servlet.ServletUtil.escapeURLString(color.getId()) %>'><%= color.getName() %></dsp:a></font></td>
        </tr><!-- end one preview -->
      </core:ForEach>
    </table>
        </core:CreateUrl>
        <br>
        <!-- end link table -->
      </td>
      <td align="center">&nbsp;&nbsp;</td>

      <td valign="top">


   <dsp:droplet name="ColorLookupDroplet">
    <dsp:param name="id" bean="PageColorFormHandler.colorPalette"/>
    <dsp:oparam name="output">
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="element"/>
        <dsp:oparam name="false">

   <dsp:getvalueof id="gearTitleBackgroundColor" param="element.gearTitleBackgroundColor">
   <dsp:getvalueof id="gearTitleTextColor"       param="element.gearTitleTextColor">
   <dsp:getvalueof id="pageBackgroundColor"      param="element.pageBackgroundColor">
   <dsp:getvalueof id="gearBackgroundColor"      param="element.gearBackgroundColor">
   <dsp:getvalueof id="pageTextColor"            param="element.pageTextColor">
   <dsp:getvalueof id="pageLinkColor"            param="element.pageLinkColor">
   <dsp:getvalueof id="pageVisitedLinkColor"     param="element.pageVisitedLinkColor">


<font class="subheader"><b><i18n:message key="community_pages_edit_color_current_color"/></b>&nbsp;</font><font class="smaller"><%= adminEnv.getPage().getColorPalette().getName() %><br><br></font>

<font class="subheader"><b><i18n:message key="community_pages_edit_color_previewing_color"/></b>&nbsp;</font><font class="adminbody"><dsp:valueof param="element.name"/></font><br>
<table width=250 cellspacing="0" cellpadding="0" border="0">
 <tr>
  <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
  <td bgcolor="#000000" width="247"><img src="<%=clearGif%>" width="25" height="1" border="0"></td>
  <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
  <td bgcolor="#ffffff" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
 </tr>
 <tr>
 <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="180" border="0"></td>
  <td bgcolor='<%="#"+pageBackgroundColor%>' width="247" valign="top" align="center">
    <br>
    <table width="90%" cellpadding="2" cellspacing="0" border="0" background="<%=clearGif%>">
     <tr>
      <td bgcolor='<%="#"+gearTitleBackgroundColor %>'><font class="smaller"  color='<%="#"+gearTitleTextColor%>'><i18n:message key="community_content_color_titletext"/></font>
      </td>
     </tr>
     <tr>
      <td bgcolor='<%="#"+gearBackgroundColor%>'><br>
       <blockquote>
       <font class="smaller" color='<%="#"+pageTextColor%>'><i18n:message key="community_content_color_text"/></font><br><br>
        <font class="smaller" color='<%="#"+pageLinkColor%>'><u><i18n:message key="community_content_color_link"/></u></font><br><br>
        <font class="smaller" color='<%="#"+pageVisitedLinkColor%>'><u><i18n:message key="community_content_color_vlink"/></u></font><br><br>
        </blockquote>
      </td>
     </tr>
    </table>
   <br><br>
   </td>
   <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
   <td bgcolor="#999999" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
   </tr>
   <tr>
    <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
    <td bgcolor="#000000" width="247"><img src="<%=clearGif%>" width="25" height="1" border="0"></td>
    <td bgcolor="#000000" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
    <td bgcolor="#999999" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
    </tr>
    <tr>
     <td bgcolor="#FFFFFF" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
     <td bgcolor="#999999" width="247"><img src="<%=clearGif%>" width="25" height="1" border="0"></td>
     <td bgcolor="#999999" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
     <td bgcolor="#999999" width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
    </tr>
   </table>

   </dsp:getvalueof>
   </dsp:getvalueof>
   </dsp:getvalueof>
   </dsp:getvalueof>
   </dsp:getvalueof>
   </dsp:getvalueof>
   </dsp:getvalueof>

        </dsp:oparam>
        <dsp:oparam name="true">
          <i18n:message key="community_pages_edit_color_not_found"/>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
   </dsp:droplet>



   <div align="left" style=";margin-right:20px">
    <table><tr><td align="left">
<br>

      <dsp:droplet name="ColorLookupDroplet">
        <dsp:param name="id" bean="PageColorFormHandler.colorPalette"/>
        <dsp:oparam name="output">
          <dsp:droplet name="IsEmpty">
      <dsp:param name="value" param="element"/>
      <dsp:oparam name="false">
        <dsp:input bean="PageColorFormHandler.colorPalette" type="hidden" name="colorId" paramvalue="element.repositoryId" />

      </dsp:oparam>
      <dsp:oparam name="true">
        <!--  color not found -->
      </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>



  <dsp:input bean="PageColorFormHandler.communityId" type="hidden" value="<%=dsp_community_id%>"/>
  <dsp:input bean="PageColorFormHandler.pageId" type="hidden" value="<%=dsp_page_id%>"/>
  <dsp:input bean="PageColorFormHandler.colorPalette" type="hidden" value="<%=dsp_color_id%>" />


<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageColorFormHandler.successURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="PageColorFormHandler.failureURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>

</core:CreateUrl>



<i18n:message id="submitLabel" key="update"/>
<i18n:message id="resetLabel"  key="reset"/>

  <dsp:input type="submit"  value="<%=submitLabel%>" bean="PageColorFormHandler.updateColorAdminMode"  />
   


    
<br><br>
 </td></tr></table>
</div>
</td></tr></table>

</td></tr></table>

</dsp:form>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_edit_color.jsp#2 $$Change: 651448 $--%>
