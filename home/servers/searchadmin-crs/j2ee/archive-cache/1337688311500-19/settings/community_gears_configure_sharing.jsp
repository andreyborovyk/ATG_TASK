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

<% String callingPage = "sharing"; %>
<%@include file="fragments/community_gears_configure_nav.jspf" %>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%"><tr><td valign="top">

<font class="small">
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="280" border="0">
<dsp:form action="community_gears.jsp" method="post" synchronized="/atg/portal/admin/GearFormHandler">

<font class="smaller"><dsp:input type="checkbox" bean="GearFormHandler.shared" checked="<%= gearItem.isShared()%>" value="true"/><i18n:message key="is_gear_shared"/><br />

<br />

<admin:getCommunitiesHavingGear id="tag" gearId="<%= dsp_gear_id%>">
<core:if value="<%= (tag.getCommunities(atg.portal.framework.Comparators.getCommunityComparator()).size() > 1)%>">

<% if  (tag.getCommunities(atg.portal.framework.Comparators.getCommunityComparator()).size() > 2) {

 Integer tempValue = new Integer( tag.getCommunities(atg.portal.framework.Comparators.getCommunityComparator()).size() - 1);
 String numValueCommunity = tempValue.toString();
 %>

<i18n:message key="community_gear_unshare_warning_plural_param">
 <i18n:messageArg value="<%=numValueCommunity %>"/>
</i18n:message>

<% } else {%>
<i18n:message key="community_gear_unshare_warning_single"/>
<% } %>


<ul>  <core:ForEach id="communities"
            values="<%= tag.getCommunities(atg.portal.framework.Comparators.getCommunityComparator()) %>"
            castClass="atg.portal.framework.Community"
            elementId="community">
   <core:ifNot value="<%= dsp_community_id.equals(community.getId())%>">            
            <li><%= community.getName(response.getLocale())%>
   </core:ifNot>
  </core:ForEach>
  </ul>
</core:if>  
</admin:getCommunitiesHavingGear>  
</font>
<br />

<dsp:setvalue param="gearId" beanvalue="GearFormHandler.gearId"/>
<dsp:setvalue bean="GearFormHandler.communityId" value="<%=dsp_community_id%>"/>                  

  <core:CreateUrl id="updateSuccessURL" url="community_gears.jsp">
   <core:UrlParam param="mode" value="7"/>
   <core:UrlParam param="paf_gear_id" value="<%=dsp_gear_id%>"/>
   <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
   <core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <dsp:input type="hidden" bean="GearFormHandler.successURL" value="<%= updateSuccessURL.getNewUrl() %>"/> 
  </core:CreateUrl><%-- updateSuccessURL --%>
  
  <%-- UPDATE BUTTON --%>

     <i18n:message id="done02" key="update" />
     <dsp:input bean="GearFormHandler.unshareGear" type="submit" value="<%= done02 %>"/>
		&nbsp;&nbsp;&nbsp;
  <%-- RESET BUTTON --%>
     <i18n:message id="reset01" key="reset" />
     <input type="reset" value="<%= reset01 %>">

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
</dsp:getvalueof><%--gearItem --%>

</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>

</paf:hasCommunityRole>
</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_configure_sharing.jsp#2 $$Change: 651448 $--%>
