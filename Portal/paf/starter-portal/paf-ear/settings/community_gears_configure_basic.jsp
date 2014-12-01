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

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_success_url"  idtype="java.lang.String" param="paf_success_url">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String" param="paf_gear_id">


<dsp:setvalue  bean="GearFormHandler.communityId" value="<%=dsp_community_id%>" />
<dsp:setvalue  bean="GearFormHandler.gearId" value="<%=dsp_gear_id%>" />

<dsp:getvalueof id="gearItem" idtype="atg.portal.framework.Gear" bean="GearFormHandler.gear">
<dsp:getvalueof id="gearName" bean="GearFormHandler.name">

<%
    String mode          = "7";       
    String divider = "<font class='smaller'>&nbsp;&nbsp;|&nbsp;&nbsp;</font>";
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } 
%>

<% String callingPage = "basic"; %>
<%@include file="fragments/community_gears_configure_nav.jspf" %>

<% String gearURL="", communityId=null; %>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td valign"top">

<font class="small">
<dsp:form action="community_gears.jsp" method="post" synchronized="/atg/portal/admin/GearFormHandler">
<br />
<font class="subheader"><b><i18n:message key="community_pages_gears_name"/></b></font><br />
<dsp:input type="text" bean="GearFormHandler.name"/><br /><br />

<font class="subheader"><b><i18n:message key="community_pages_gears_description"/></b></font><br />
<dsp:textarea bean="GearFormHandler.description" cols="34" rows="5" /><br />

  <core:IfNot value="<%=gearItem.isShared()%>" >      
   <br />
   <font class="subheader"><b><i18n:message key="gear_sharing_title"/></b></font><br />

   <dsp:input type="checkbox" bean="GearFormHandler.shared" checked="<%=false%>" value="true"/><i18n:message key="is_gear_shared"/><br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i18n:message key="gear_shared_warning"/>
  </core:IfNot>  
<br /><br />

      <!-- Access level setting -->
      <font class="subheader"><b><i18n:message key="gear_access_form_title"/></b></font>
      <br />
      <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="0"/>
      <i18n:message key="gear_access_any"/><br />
      <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="1"/>
      <i18n:message key="gear_access_registered"/><br />
      <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="2"/>
      <i18n:message key="gear_access_guests"/><br />
      <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="3"/>
      <i18n:message key="gear_access_members"/><br />
      <dsp:input type="radio" bean="GearFormHandler.accessLevel" value="4"/>
      <i18n:message key="gear_access_leaders"/>


<dsp:setvalue param="gearId" beanvalue="GearFormHandler.gearId"/>

<dsp:setvalue bean="GearFormHandler.communityId" value="<%=dsp_community_id%>"/>                  
      
<%-- Show the allow gear shared message only if the gear is currently not shared. --%>    
  
 <core:CreateUrl id="gearPageURL" url="community_edit_gear_details.jsp" >
  <core:UrlParam param="paf_page_id" value="<%=dsp_page_id %>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id %>"/>
        
  </core:CreateUrl>

<br /><br />

<%-- returnURL --%>

  <core:CreateUrl id="updateSuccessURL" url="community_gears.jsp">
   <core:UrlParam param="mode" value="7"/>
   <core:UrlParam param="paf_gear_id" value="<%=dsp_gear_id%>"/>
   <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
   <core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <dsp:input type="hidden" bean="GearFormHandler.successURL" value="<%= portalServletResponse.encodePortalURL(updateSuccessURL.getNewUrl()) %>"/> 
    <dsp:input type="hidden" bean="GearFormHandler.failureURL" value="<%= portalServletResponse.encodePortalURL(updateSuccessURL.getNewUrl()) %>"/> 
  </core:CreateUrl><%-- updateSuccessURL --%>

    
<%-- UPDATE BUTTON --%>
     <i18n:message id="done02" key="update" />
     <dsp:input bean="GearFormHandler.updateGear" type="submit" value="<%= done02 %>"/>&nbsp;&nbsp;&nbsp;

<%-- RESET BUTTON --%>
     <i18n:message id="reset01" key="reset" />
     <input type="reset" value="<%= reset01 %>">

      <br />
    </dsp:form></font>

  </td>

  </tr>
</table>

<%--
 <dsp:include page="community_gear_render.jsp" flush="false">
  <dsp:param  name="paf_dm" value="shared"  /> 
  <dsp:param  name="paf_gm" value="content" /> 
  <dsp:param  name="paf_gear_id" value="<%=dsp_gear_id%>" /> 
  <dsp:param  name="paf_community_id" value="<%=dsp_community_id%>" /> 
 </dsp:include>
--%>


</dsp:getvalueof><%--gearName --%>

</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</paf:hasCommunityRole> 
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_configure_basic.jsp#2 $$Change: 651448 $--%>
