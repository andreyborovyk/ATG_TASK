<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<%--
  this page is used for both the confirm delete and confirm remove gears page
  so only the wording is changed and is triggered by the request param removeFlag
--%>
 <i18n:message key="confirm_delete_title_bar" id="main_title"/>
 <i18n:message key="confirm_remove_title_bar" id="remove_title"/>
 <i18n:message id="confirm_delete_submit" key="confirm_delete_gear_submit"/>
 <i18n:message id="confirm_remove_submit" key="confirm_remove_gear_submit"/>
<%
 String removeFlag = "";
 if ( request.getParameter("removeFlag") != null && request.getParameter("removeFlag").equals("true")  ) {
   main_title = remove_title;
   confirm_delete_submit = confirm_remove_submit ;
   removeFlag = "true";

 } else {
   removeFlag = "true";
 }

%>

<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>


<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="mode" idtype="java.lang.String" param="mode">

<% String dsp_gear_id = request.getParameter("paf_gear_id");  %>

<dsp:setvalue  bean="GearFormHandler.communityId" value="<%=dsp_community_id%>"/>
<dsp:setvalue  bean="GearFormHandler.gearId" value='<%=request.getParameter("paf_gear_id")%>'/>

<dsp:getvalueof id="name" bean="GearFormHandler.name">
<dsp:getvalueof id="gear" bean="GearFormHandler.gear" idtype="atg.portal.framework.Gear">

<dsp:form action="community_gears.jsp" method="post" synchronized="/atg/portal/admin/GearFormHandler">

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

<font class="pageheader_edit"><%= main_title%> <%= name %></font>

</td></tr></table>
</td></tr></table>
	



  <input type="hidden" name="mode"              value='<%= request.getParameter("returnMode")%>'/>
  <input type="hidden" name="paf_page_id"       value="<%=dsp_page_id  %>"/>
  <input type="hidden" name="paf_page_url"      value="<%=dsp_page_url  %>"/>
  <input type="hidden" name="paf_community_id"  value="<%=dsp_community_id  %>"/>

	<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

<font class="smaller">
        <i18n:message id="i18n_bold" key="html-bold"/>
        <i18n:message id="i18n_end_bold" key="html-end-bold"/>
        <i18n:message id="i18n_question_mark" key="question-mark"/>

        <img src='<%=response.encodeURL("images/error.gif")%>' >&nbsp;&nbsp;

<core:If value='<%=removeFlag%>'>
        <i18n:message key="remove_confirm_question_param">
         <i18n:messageArg value="<%=i18n_question_mark%>"/>
        </i18n:message>
        <i18n:message key="confirm_remove_gearname_param">
         <i18n:messageArg value="<%=name%>"/>
         <i18n:messageArg value="<%=i18n_bold%>"/>
         <i18n:messageArg value="<%=i18n_end_bold%>"/>
         <i18n:messageArg value="<%=i18n_question_mark%>"/>
       </i18n:message>
</core:If>

<core:IfNot value='<%=removeFlag%>'>
<%-- delete messages --%>
        <i18n:message key="remove_confirm_question_param">
         <i18n:messageArg value="<%=i18n_question_mark%>"/>
        </i18n:message>
        <i18n:message key="confirm_remove_gearname_param">
         <i18n:messageArg value="<%=name%>"/>
         <i18n:messageArg value="<%=i18n_bold%>"/>
         <i18n:messageArg value="<%=i18n_end_bold%>"/>
         <i18n:messageArg value="<%=i18n_question_mark%>"/>
       </i18n:message>
</core:IfNot>



  <core:CreateUrl id="gearUrlS" url="community_gears.jsp" >
        <core:UrlParam param="mode" value='<%= request.getParameter("returnMode")%>'/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
        <core:UrlParam param="removeFlag"  value="<%=removeFlag %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.successURL" value="<%= (String) gearUrlS.getNewUrl() %>"/>        
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlC" url="community_gears.jsp" >
        <core:UrlParam param="mode" value='<%= request.getParameter("returnMode")%>'/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
        <core:UrlParam param="removeFlag"  value="<%=removeFlag %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.cancelURL" value="<%= (String) gearUrlC.getNewUrl() %>"/>  
  </core:CreateUrl>
  <core:CreateUrl id="gearUrlF" url="community_gears.jsp" >
        <core:UrlParam param="mode" value="<%=mode%>"/>
        <core:UrlParam param="returnMode" value='<%= request.getParameter("returnMode")%>'/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <core:UrlParam param="paf_gear_id"       value="<%=dsp_gear_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
        <core:UrlParam param="removeFlag"  value="<%=removeFlag %>"/>
         <dsp:input type="hidden" bean="GearFormHandler.failureURL" value="<%= (String) gearUrlF.getNewUrl() %>"/>
  </core:CreateUrl>




<br><br>


 <i18n:message id="cancel_button" key="cancel"/>


<dsp:input type="SUBMIT" value="<%=confirm_delete_submit%>" bean="GearFormHandler.removeGear"/>&nbsp;&nbsp;

<input type="SUBMIT"  bean="GearFormHandler.cancel" value="<%=cancel_button%>" />
 </td>
</tr></dsp:form></table>

</dsp:getvalueof><%-- id="gear" --%> 
</dsp:getvalueof><%-- id="name" --%> 

</dsp:getvalueof><%-- mode            --%>
</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_communiyt_id --%>
                

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_confirm_delete.jsp#2 $$Change: 651448 $--%>
