<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager">
<admin:InitializeAdminEnvironment id="adminEnv">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/portal/admin/AddGearsFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>

<i18n:message id="i18n_bold"     key="html-bold"/>
<i18n:message id="i18n_end_bold" key="html-end-bold"/>

<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_gear_id" idtype="java.lang.String" param="paf_gear_id">

<%--<dsp:setvalue bean="GearFormHandler.setLocale"/>--%>
<dsp:setvalue bean="GearFormHandler.gearId"      value="<%=dsp_gear_id%>"/>
<dsp:setvalue bean="GearFormHandler.communityId" value="<%=dsp_community_id%>"/>

<dsp:form action="community_gears.jsp" method="POST"  synchronized="/atg/portal/admin/GearFormHandler">

	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"><i18n:message key="community_gears_shared_add_confirm_header"/>
</td></tr></table>    
</td></tr></table>

    

<dsp:getvalueof id="gName" bean="GearFormHandler.name">
<dsp:getvalueof id="gear" bean="GearFormHandler.gear" idtype="atg.portal.framework.Gear">
	<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
	<font class="smaller">
  <i18n:message key="community_gears_shared_add_confirm_message">
   <i18n:messageArg value="<%=gName%>"/>
   <i18n:messageArg value="<%=i18n_bold%>"/>
   <i18n:messageArg value="<%=i18n_end_bold%>"/>
  </i18n:message>

<i18n:message key="community_gears_shared_add_confirm_message_extra"/>

<core:IfNotNull value="<%= gear.getDescription() %>">
 <font class="smaller"><b><i18n:message key="community_pages_gears_description"/></b><br>
 <%= gear.getDescription() %><br>
</core:IfNotNull>
<br><br>
<%--
<%=  gear.getParentCommunity().getId().equals(dsp_community_id) %>
--%>

<font class="smaller"><i18n:message key="gear_access_form_title_is"/>


<core:Switch value="<%= String.valueOf(gear.getAccessLevel()) %>">

      <core:Case value="0">
       <i18n:message key="gear_access_any"/><br>
      </core:Case>

      <core:Case value="1">
      <i18n:message key="gear_access_registered"/><br>
      </core:Case>

      <core:Case value="2">
      <i18n:message key="gear_access_guests"/><br>
      </core:Case>

      <core:Case value="3">
      <i18n:message key="gear_access_members"/><br>
      </core:Case>

      <core:Case value="4">
      <i18n:message key="gear_access_leaders"/>
      </core:Case>

</core:Switch>

</font>
</blockquote>
</dsp:getvalueof><%-- gear --%>
</dsp:getvalueof><%-- gName --%>

  <core:CreateUrl id="gearUrlS" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="3"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.successURL" value="<%= (String) gearUrlS.getNewUrl() %>"/>        
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlC" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="3"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.cancelURL" value="<%= (String) gearUrlC.getNewUrl() %>"/>        
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlF" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="11"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <core:UrlParam param="paf_gear_id"       value="<%=dsp_gear_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.failureURL" value="<%= (String) gearUrlF.getNewUrl() %>"/>
   </core:CreateUrl>


<i18n:message id="done01" key="yes" />
<dsp:input type="SUBMIT"  bean="GearFormHandler.addSharedGear"     value="<%= done01 %>" />
<i18n:message id="cancel01" key="cancel" />&nbsp;
<dsp:input bean="GearFormHandler.cancel" type="submit" value="<%= cancel01 %>"/>
</font></td></tr></table>
</dsp:form>



</dsp:getvalueof><%-- dsp_gear_id --%>
</dsp:getvalueof><%-- dsp_community_id --%>
</dsp:getvalueof><%-- dsp_page_url --%>
</dsp:getvalueof><%-- dsp_page_id --%>

</admin:InitializeAdminEnvironment>

</paf:hasCommunityRole> 
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_add_shared_confirm.jsp#2 $$Change: 651448 $--%>
