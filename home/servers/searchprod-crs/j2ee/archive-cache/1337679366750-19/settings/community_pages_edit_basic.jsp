<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>
<dsp:form  action="community_pages.jsp" method="POST" name="editBasic" synchronized="/atg/portal/admin/PageFormHandler">

<%@include file="fragments/community_pages_edit_nav.jspf"%>


<%
 atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
 String dsp_page_id = dynamoRequest.getParameter("paf_page_id");
 String dsp_community_id = dynamoRequest.getParameter("paf_community_id");
 String dsp_ppage_id = dynamoRequest.getParameter("paf_ppage_id");
 String dsp_page_url = dynamoRequest.getParameter("paf_page_url");
 
 if(null == dsp_page_url) {
    dsp_page_url = request.getParameter("paf_page_url");
 }
 if(null == dsp_page_id) { 
   dsp_page_id = dsp_ppage_id;
 }
%>

<dsp:setvalue bean="PageFormHandler.pageId"  value="<%= dsp_page_id %>" /> 
<dsp:setvalue bean="PageFormHandler.communityId"  value="<%= dsp_community_id %>" /> 
    


<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <table border=0 cellpadding=1 celspacing=0 width="320"><!-- 2 colomns  -->
   <tr>
    <td><img src="images/clear.gif" height=1 width=150 border=0></td>
    <td><img src="images/clear.gif" height=1 width=150 border=0></td>
   </tr>

    <tr>
      <td><font class="adminbody">
      <b><i18n:message key="community_pages_content_pagename"/></b><br>

      <dsp:input bean="PageFormHandler.name" type="text"/>
      </font></td>
      <td><font class="adminbody">
      <b><i18n:message key="community_pages_content_position"/></b><br>

      <dsp:select bean="PageFormHandler.position">
      <core:ForEach id="pagePos"
                    values="<%= adminEnv.getCommunity().getPages() %>"
                    castClass="atg.portal.framework.Page"
                    elementId="childPage">
          <dsp:option value="<%= java.lang.Integer.toString(pagePos.getCount()) %>"/>
          <%= pagePos.getCount()  %>
       </core:ForEach>
      </dsp:select>
      </font></td>
     </tr>
     <tr>
      <td colspan="2"><font class="adminbody">
       <b><i18n:message key="community_pages_content_urlname"/></b><br>
       <dsp:input bean="PageFormHandler.url" type="text"/>

<script language="Javascript1.2">
function suggest(){
 formRef = eval( "document.forms['editBasic']"); 
  if ( formRef != null ) {
     i = 0;
     while ( i++ < formRef.elements.length ) {
       if( ( formRef.elements[i].name.indexOf("PageFormHandler.name") > -1) 
          &&(formRef.elements[i].name.indexOf("_D") == -1)) sourceElemValue  = formRef.elements[i].value;         
       if((formRef.elements[i].name.indexOf("PageFormHandler.url") > -1) 
          && (formRef.elements[i].name.indexOf("_D") == -1)) {  destElemIndex = i ; break; }
   }
    if ( formRef.elements[destElemIndex] != null ) {
       if ( sourceElemValue != "" ) {
         modifiedElemValue = "";
         sourceElemValue =  sourceElemValue.toLowerCase();
      // resevered charactors ( space  \ / ? & = , : ; $ @ ^ ~ * % " ' ` < > ! . , ~ | + [ ] { } #   )
         formRef.elements[destElemIndex].value  = sourceElemValue.replace( / |\\|\?|&|=|,|:|;|\$|@|\^|\~|\*|\%|\"|\'|\/|\<|\>|\!|\.|\,|\~|\||\+|\[|\]|\{|\}|`|\#/gi , "" );
      } else {
       alert('<i18n:message key="community_pages_web_friendly_suggest_js_alert_noname"/>');
      }
   }
  }
}
//document.write("<a href='Javascript:suggest()'><font size='-2'><i18n:message key="community_pages_web_friendly_suggest_link"/></font></a>");

document.write("<font size='-2'><input type='submit' onclick='suggest();return false;' value='<i18n:message key="community_pages_web_friendly_suggest_link"/>' /></font>");
</script>
     </font></td>
     </tr>

    <dsp:setvalue bean="PageFormHandler.fixed" value="<%= new Boolean(String.valueOf(adminEnv.getPage().isFixed())) %>"/> 

<tr><td colspan="2">

<font class="subheader"><b><i18n:message key="community_settings_discription">Description:</i18n:message></b></font><br />
<dsp:textarea bean="PageFormHandler.description" cols="34" rows="3" /><br />

</td></tr>
    <tr>
     <td colspan=2 nowrap><font class="subheader">
        <i18n:message key="community_pages_content_accesslevelmessage"/></font><br>
         <font class="smaller">
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="0"/><i18n:message key="community_pages_content_accesslevel0"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="1"/><i18n:message key="community_pages_content_accesslevel1"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="2"/><i18n:message key="community_pages_content_accesslevel2"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="3"/><i18n:message key="community_pages_content_accesslevel3"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="4"/><i18n:message key="community_pages_content_accesslevel4"/><br>
                
         </font></td>
     </tr>
                
     <tr>
      <td colspan=2 nowrap><font class="subheader"><i18n:message key="community_settings_personalization_title"/></font><br><font class="smaller">&nbsp;<dsp:input type="checkbox" bean="PageFormHandler.fixed" /><i18n:message key="community_pages_content_allowpersonalize"/></font></td>

      </tr>
<dsp:getvalueof id="isDefault" idtype="java.lang.Boolean" bean="PageFormHandler.makeDefault">
<core:If value="<%=!(isDefault.booleanValue())%>">
      <tr>

         <td colspan=2 nowrap><font class="subheader"><i18n:message key="community_pages_content_default"/></font><br><font class="smaller">
        &nbsp;<dsp:input type="checkbox" bean="PageFormHandler.makeDefault" />&nbsp;<i18n:message key="community_pages_content_make_default"/></font></td>

        </tr>
</core:If>
</dsp:getvalueof>
   </table>

<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageFormHandler.successURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="PageFormHandler.failureURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>

</core:CreateUrl>

<br>
<i18n:message id="submitLabel" key="update"/>
<i18n:message id="resetLabel"  key="reset"/>

   <dsp:input type="SUBMIT" value="<%=submitLabel%>" bean="PageFormHandler.updatePageAdminMode"/>&nbsp;&nbsp;&nbsp;
   <input type="RESET"  value="<%=resetLabel%>"  />

</td></tr></table>
</dsp:form>
</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_edit_basic.jsp#2 $$Change: 651448 $--%>
