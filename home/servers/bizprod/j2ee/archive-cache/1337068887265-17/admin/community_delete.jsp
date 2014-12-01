<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>

<% 
   String clearGif =  response.encodeURL("images/clear.gif");
%>
<dsp:form action="community.jsp" method="POST" synchronized="/atg/portal/admin/CommunityFormHandler">

<core:demarcateTransaction id="demarcateXA">
  <% try { %>



<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
<i18n:message key="admin-community-delete-header"/></font>
</td></tr></table>
</td></tr></table>
<img src='<%=clearGif%>' height="1" width="1" alt=""><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td><font class="smaller">
<!-- Cocantanate -->
<p>
<i18n:message key="admin-html-bold"     id="i18n_bold"/>
<i18n:message key="admin-html-end-bold" id="i18n_end_bold"/>

<i18n:message key="admin-community-delete-confirm" >
<i18n:messageArg value="<%=  adminEnv.getCommunity().getName(response.getLocale()) %>"/>
<i18n:messageArg value="<%=i18n_bold%>"/>
<i18n:messageArg value="<%=i18n_end_bold%>"/>
</i18n:message>


</font>
<br><br>

<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String dsp_community_id = "";
  if(cm != null) {
    dsp_community_id = cm.getId();
  }
%>
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String" param="paf_page_url">

<dsp:input type="hidden" bean="CommunityFormHandler.failureURL" value="<%=dsp_page_url%>"/>
<dsp:input type="hidden" bean="CommunityFormHandler.id" value="<%=dsp_community_id%>"/>
<dsp:input type="hidden" bean="CommunityFormHandler.name" value="<%= adminEnv.getCommunity().getName(response.getLocale()) %>"/>
<dsp:input type="hidden" bean="CommunityFormHandler.successURL" value="<%=dsp_page_url%>"/>
<dsp:input type="hidden" bean="CommunityFormHandler.cancelURL"  value="<%=dsp_page_url%>"/>

<i18n:message id="yes01" key="yes" />
<dsp:input type="submit" bean="CommunityFormHandler.delete" value="<%= yes01 %>" name="Yes"/>&nbsp;&nbsp;&nbsp;
<i18n:message id="no01" key="no" />
<dsp:input type="submit" bean="CommunityFormHandler.cancel" value="<%= no01 %>" name="No"/>

</dsp:getvalueof>
</div>

</td></tr></table>
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

</dsp:form>


</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_delete.jsp#2 $$Change: 651448 $--%>
