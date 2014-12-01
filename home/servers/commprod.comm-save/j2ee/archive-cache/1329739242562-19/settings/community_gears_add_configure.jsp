<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />
<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager">

<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>

<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String" param="paf_gear_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_success_url"  idtype="java.lang.String" param="paf_success_url">

<dsp:setvalue bean="GearFormHandler.gearDefinitionId" value="<%= dsp_gear_id %>"/>
<dsp:setvalue bean="GearFormHandler.communityId" value= "<%= dsp_community_id %>"/>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
	 <img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0"><i18n:message key="community_pages_gears_title"/>
</td></tr></table>
</td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#96C1DF" width="100%"><tr><td>
  <font class="smaller"><i18n:message key="community_gears_add_configure_helpertext"/>

</font>
</td></tr></table>
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br />
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="small">
 <dsp:form action="community_gears.jsp" method="post"  synchronized="/atg/portal/admin/GearFormHandler">
  <input type="hidden" name="mode"             value="2">
  <input type="hidden" name="paf_page_id"      value="<%=dsp_page_id%>" >
  <input type="hidden" name="paf_page_id"      value="<%=dsp_page_url%>" >
  <input type="hidden" name="paf_community_id" value="<%=dsp_community_id%>" >

  <font class="subheader"><i18n:message key="community_pages_gears_name"/></font><br />
  <dsp:input type="text" bean="GearFormHandler.name"/>

  <br /><br />

  <font class="subheader"><i18n:message key="community_pages_gears_description"/></font><br />
  <dsp:textarea bean="GearFormHandler.description" cols="34" rows="5" />

  <br /><br />

  <font class="subheader"><i18n:message key="gear_sharing_title"/></font><br />
  <font class="smaller">

  <dsp:input type="checkbox" bean="GearFormHandler.shared" value="true"/>&nbsp;<i18n:message key="is_gear_shared"/><br />

          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i18n:message key="gear_shared_warning"/>
  </font>

  <br /><br />

  <%-- ACCESS LEVELS --%>
  <font class="subheader"><i18n:message key="gear_access_form_title"/></font><br />
  <font class="smaller">
<dsp:getvalueof id="currAccessLevel"  bean="GearFormHandler.accessLevel">

  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="0"/>&nbsp;<i18n:message key="gear_access_any"/><br />
  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="1"/>&nbsp;<i18n:message key="gear_access_registered"/><br />
  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="2"/>&nbsp;<i18n:message key="gear_access_guests"/><br />
  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="3"/>&nbsp;<i18n:message key="gear_access_members"/><br />
<core:IfNull value="<%=currAccessLevel%>">
  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="4" checked="true"/>&nbsp;<i18n:message key="gear_access_leaders"/><br />
</core:IfNull>
<core:IfNotNull value="<%=currAccessLevel%>">
  <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="4"/>&nbsp;<i18n:message key="gear_access_leaders"/><br />
</core:IfNotNull>
  </font>
  <br />
</dsp:getvalueof><%-- id="currAccessLevel" --%>

 <%-- ??? INITIAL or INSTANCE configuration screens --%>
 <dsp:getvalueof bean="GearFormHandler.gearDefinition" id="gearDefItem" idtype="atg.portal.framework.GearDefinition">


<% String sucessBaseUrl = "community_gears.jsp"; %>
<core:If value="<%= (gearDefItem.getGearMode(atg.portal.framework.GearMode.GEARMODE_INITIALCONFIG) != null)%>">
<%        sucessBaseUrl = "community_gears_configure_initial.jsp"; %>
</core:If>
  <core:CreateUrl id="gearUrlS" url="<%=sucessBaseUrl%>" >
        <core:UrlParam param="mode" value="2"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.successURL" value="<%= (String) gearUrlS.getNewUrl() %>"/>        
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlC" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="2"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.cancelURL" value="<%= (String) gearUrlC.getNewUrl() %>"/>        
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlF" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="6"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <core:UrlParam param="paf_gear_id"       value="<%=dsp_gear_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.failureURL" value="<%= (String) gearUrlF.getNewUrl() %>"/>
   </core:CreateUrl>

   <i18n:message id="done01" key="done" />
   <dsp:input bean="GearFormHandler.addGear" type="submit" value="<%= done01 %>" submitvalue="done" />
   <i18n:message id="cancel01" key="cancel" />&nbsp;
   <dsp:input bean="GearFormHandler.cancel" type="submit" value="<%= cancel01 %>"/>
  <br /><br />

<core:If value="<%= (gearDefItem.getGearMode(atg.portal.framework.GearMode.GEARMODE_INITIALCONFIG) != null)%>">
<%-- INITIAL CONFIG REDIRECT NOTICE --%>
<font class="smaller">
<img src='<%= response.encodeURL("images/info.gif")%>' align="left"><i18n:message key="community_gears_init_configure_helpertext_additional"> This is an intitial configuration screen assoisiated with this gear.</i18n:message>
<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i18n:message key="community_gears_init_configure_helpertext_additional_b">After adding gear,you will be redirected to the initial configuration screen.</i18n:message>

</font><br>
</core:If>


<core:If value="<%= (gearDefItem.getGearMode(atg.portal.framework.GearMode.GEARMODE_INSTANCECONFIG) != null)%>">
<%-- ADDITIONAL CONFIG PAGES NOTICE --%>
<font class="smaller">
<img src='<%= response.encodeURL("images/info.gif")%>' align="left"><i18n:message key="community_gears_add_configure_helpertext_additional"> There are additional configurations associated with this gear.</i18n:message>
<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i18n:message key="community_gears_add_configure_helpertext_additional_b">After adding gear, return to Current Gear Instances, configure this, and then click additional configurations.</i18n:message>

</font>
</core:If>
</dsp:getvalueof>

  </td></tr></table>
 </dsp:form>
</font>



</dsp:getvalueof><%--dsp_gear_id      --%>
</dsp:getvalueof><%--dsp_success_url  --%>
</dsp:getvalueof><%--dsp_community_id --%>
</dsp:getvalueof><%--dsp_page_url     --%>
</dsp:getvalueof><%--dsp_page_id      --%>

</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_add_configure.jsp#2 $$Change: 651448 $--%>

