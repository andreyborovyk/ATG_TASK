<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,page-manager">

<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>

<dsp:getvalueof id="dsp_page_id"  idtype="java.lang.String"     param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<core:demarcateTransaction id="demarcateXA">
<% try { %>
<dsp:form action="community_pages.jsp" method="post" synchronized="/atg/portal/admin/PageFormHandler">
	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="community_page_confirm_delete_header_title"/>
	</td></tr></table>
    </td></tr></table>



<dsp:setvalue bean="PageFormHandler.pageId" value="<%=dsp_page_id%>"/>
<dsp:setvalue bean="PageFormHandler.communityId" value="<%=dsp_community_id%>"/>

<dsp:getvalueof id="pageName" bean="PageFormHandler.name">

<i18n:message id="i18n_question_mark" key="question-mark"/>
<i18n:message id="i18n_bold"     key="html-bold"/>
<i18n:message id="i18n_end_bold" key="html-end-bold"/>

	<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<img src='<%=response.encodeURL("images/error.gif")%>' >&nbsp;
<font class="smaller">
<i18n:message key="page_delete_confirm_question_param">
 <i18n:messageArg value="<%=i18n_question_mark%>"/>
</i18n:message>


<i18n:message key="page_confirm_delete_pagename_param">
 <i18n:messageArg value='<%= pageName %>'/>
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_end_bold%>"/>
 <i18n:messageArg value="<%=i18n_question_mark%>"/>
</i18n:message>



<core:CreateUrl id="deleteUrl" url="community_pages.jsp" >
	<core:UrlParam param="mode" value="1"/>
	<core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
   	<paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
        <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>

<dsp:input type="hidden" bean="PageFormHandler.successURL" value="<%=deleteUrl.getNewUrl()%>"/>

   <core:CreateUrl id="failureURL" url="community_pages.jsp">
	<core:UrlParam param="mode" value="5"/>
	<core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
   	<paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
        <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>

      <dsp:input type="hidden" bean="PageFormHandler.failureURL" value="<%= failureURL.getNewUrl() %>" />
   </core:CreateUrl>
   <core:CreateUrl id="cancelURL" url="community_pages.jsp">
	<core:UrlParam param="mode" value="1"/>
	<core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
   	<paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
        <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>

      <dsp:input type="hidden" bean="PageFormHandler.cancelURL" value="<%= cancelURL.getNewUrl() %>" />
   </core:CreateUrl>
<br><br>

<i18n:message id="submit_label" key="confirm_delete_page_submit"/>
<i18n:message id="cancel_button1" key="cancel"/>
<font class="small">
<dsp:input type="SUBMIT" 
           value="<%=submit_label%>" 
           bean="PageFormHandler.deletePageAdminMode"
           submitvalue="<%=deleteUrl.getNewUrl()%>"
/>&nbsp;&nbsp;
<dsp:input type="SUBMIT" value="<%=cancel_button1%>"   bean="PageFormHandler.cancel" />


</font></td>

</core:CreateUrl><%-- deleteURL --%>
</dsp:getvalueof><%-- pageName --%>

</dsp:form>

</tr></table>

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

</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_communiyt_id --%>
</paf:hasCommunityRole>                
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_confirm_delete.jsp#2 $$Change: 651448 $--%>
