<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%-- // this file is a direct include from community_page.jsp 
     // so it has the import beans and error messaging and the adminEnv
--%>


<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,page-manager">

<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<%     String clearGif =  response.encodeURL("images/clear.gif");   %>
<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:form action="community_pages.jsp" method="POST" name="newPage" synchronized="/atg/portal/admin/PageFormHandler">
 <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
<i18n:message key="community_pages_new_subheader_title"/>
</td></tr></table>
</td></tr></table>




<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
&nbsp;<i18n:message key="community_pages_new_subheader_basics"/>
</font>
</td></tr></table>

<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="3"><br>



     <table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
       <table border=0 cellpadding=1 celspacing=0 width="100%"><!-- 2 colomns  -->
        <dsp:setvalue bean="PageFormHandler.communityId" value="<%=adminEnv.getCommunity().getId()%>"/>       

        <tr>

 <dsp:getvalueof id="currPositionVar"  bean="PageFormHandler.position">

          <td width="10%"><font class="adminbody">
          <b><i18n:message key="community_pages_content_pagename"/></b><br></font><font class="small">
          <dsp:input bean="PageFormHandler.name" type="text" /><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="3"><br>
          </font></td>
          <td><font class="adminbody">
           <b><i18n:message key="community_pages_content_position"/></b><br></font><font class="small">
           <dsp:select bean="PageFormHandler.position">
            <core:ForEach id="pagePos"
                          values="<%= adminEnv.getCommunity().getPages() %>"
                          castClass="atg.portal.framework.Page"
                          elementId="childPage">
             <dsp:option value="<%= java.lang.Integer.toString(pagePos.getCount()) %>"/>
             <%= pagePos.getCount()  %>
            </core:ForEach>
<% if ( currPositionVar == null ) { %>
           <dsp:option value="<%= java.lang.Integer.toString( adminEnv.getCommunity().getPages().length + 1) %>" selected="true" /><%= java.lang.Integer.toString( adminEnv.getCommunity().getPages().length + 1) %>
<% } else {  %>
           <dsp:option value="<%= java.lang.Integer.toString( adminEnv.getCommunity().getPages().length + 1) %>"/><%= java.lang.Integer.toString( adminEnv.getCommunity().getPages().length + 1) %>
<% } %>
          </dsp:select>
          </font><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="3"><br></td>
        </tr>
        <tr>
          <td colspan="2"><font class="adminbody">
           <b><i18n:message key="community_pages_content_urlname"/></b><br></font><font class="small">
           <dsp:input bean="PageFormHandler.url" type="text" />
<script language="Javascript1.2">
function suggest(){
 formRef = eval( "document.forms['newPage']"); 
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
       </font><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="3"><br></td>
       </tr>
</dsp:getvalueof><%-- currPositionVar --%>
<dsp:getvalueof id="currAccessLevel"  bean="PageFormHandler.accessLevel">

<tr><td colspan="2">

<font class="subheader"><b><i18n:message key="community_settings_discription">Description:</i18n:message></b></font><br />
<dsp:textarea bean="PageFormHandler.description" cols="34" rows="3" /><br />

</td></tr>
<% boolean   selectedRadio = ( currAccessLevel == null ) ? false : true ; %>
       <tr>
         <td colspan=2 nowrap><font class="subheader">
        <i18n:message key="community_pages_content_accesslevelmessage"/></font><br>
         <font class="smaller">
<core:If value="<%=selectedRadio%>">
<dsp:input type="radio" bean="PageFormHandler.accessLevel" value="0"/><i18n:message key="community_pages_content_accesslevel0"/><br>
</core:If>
<core:If value="<%=!selectedRadio%>">
<dsp:input type="radio" bean="PageFormHandler.accessLevel" value="0" checked="true"/><i18n:message key="community_pages_content_accesslevel0"/><br>
</core:If>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="1"/><i18n:message key="community_pages_content_accesslevel1"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="2"/><i18n:message key="community_pages_content_accesslevel2"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="3"/><i18n:message key="community_pages_content_accesslevel3"/><br>
         <dsp:input type="radio" bean="PageFormHandler.accessLevel" value="4"/><i18n:message key="community_pages_content_accesslevel4"/>
                
         </font><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="3"><br></td>
        </tr>
</dsp:getvalueof><%-- currAccessLevel --%>
        <tr>
         <td colspan=2 nowrap><font class="subheader"><i18n:message key="community_settings_personalization_title"/></font><br><font class="smaller">
       &nbsp;<dsp:input type="checkbox" bean="PageFormHandler.fixed" checked="true"/><i18n:message key="community_pages_content_allowpersonalize"/>
         </font><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="5" width="3"><br></td>
        </tr>
        <tr>

         <td colspan=2 nowrap><font class="subheader"><i18n:message key="community_pages_content_default"/></font><br><font class="smaller">
        &nbsp;<dsp:input type="checkbox" bean="PageFormHandler.makeDefault" />&nbsp;<i18n:message key="community_pages_content_make_default"/></font><br></td>

        </tr>
      </table>


<core:CreateUrl id="CpagesURLfailure"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>
   <dsp:input type="hidden"  bean="PageFormHandler.failureURL" value="<%=CpagesURLfailure.getNewUrl()%>"/>
</core:CreateUrl>
<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value="1"/>
   <dsp:input type="hidden"  bean="PageFormHandler.successURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
</core:CreateUrl>

<br>
<i18n:message id="submitLabel" key="update"/>
<i18n:message id="resetLabel"  key="reset"/>

   <dsp:input type="SUBMIT" value="<%=submitLabel%>" bean="PageFormHandler.createPageAdminMode"/>&nbsp;&nbsp;&nbsp;
   <input type="RESET"  value="<%=resetLabel%>"  />&nbsp;&nbsp;

</dsp:form>



</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) { %>
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

</tr></tr></table>
</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_new.jsp#2 $$Change: 651448 $--%>
