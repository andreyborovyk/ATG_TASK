<%@ page import="java.io.*,java.util.*,atg.repository.RepositoryItem" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/fcg-taglib" prefix="fcg" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<% String fptClearGIF = request.getContextPath(); %>

<paf:setFrameworkLocale />

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:page>

<i18n:bundle baseName="atg.portal.templates.DefaultTemplateResources" localeAttribute="userLocale" changeResponseLocale="false" />


<core:transactionStatus id="beginXAStatus">
  <core:if value="<%= beginXAStatus.isNoTransaction() %>">
    <core:beginTransaction id="beginSharedPageXA">
      <core:ifNot value="<%= beginSharedPageXA.isSuccess() %>">
          <i18n:message key="beginXAErrorMessage" id="xaBeginErrorMessage"/>
        <paf:log message="<%= xaBeginErrorMessage %>"
                 throwable="<%= beginSharedPageXA.getException() %>"/>
      </core:ifNot>
    </core:beginTransaction>
  </core:if>
</core:transactionStatus>

<dsp:getvalueof id="profile" bean="Profile">
<paf:InitializeEnvironment id="pafEnv">

<html>
<head>
<title>
  <%= pafEnv.getCommunity().getName() %>: <%= pafEnv.getPage().getName() %>
</title>

<core:ExclusiveIf>
  <core:IfNotNull value="<%= pafEnv.getCommunity().getStyle() %>">

   <core:CreateUrl id="ccc_encoded_url" url="<%= pafEnv.getCommunity().getStyle().getCSSURL() %>">
    <link rel="STYLESHEET" type="text/css" href="<%= ccc_encoded_url.getNewUrl() %>" src="<%= ccc_encoded_url.getNewUrl() %>">
  </core:CreateUrl>

  </core:IfNotNull>

  <core:DefaultCase>
    <style type="text/css">
      <!--
        font {font-family:verdana,arial,geneva,helvetica }
        body {font-family:verdana,arial,geneva,helvetica }

       .smaller {font-size:10px; }
       .small   {font-size:11px; }
       .medium  {font-size:12px; }
       .large   {font-size:14px; }
       .larger  {font-size:16px; }
      -->
    </style>
  </core:DefaultCase>
</core:ExclusiveIf>

</head>

<body <%= pafEnv.getPage().getColorPalette().getBodyTagData() %>
      marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>

<%--  Header include here --%>
<dsp:include page="header_main.jsp" flush="false"/>
<% 
 String communitySwitch = "";
 if (request.getParameter("paf_communities") != null ) {
   communitySwitch=request.getParameter("paf_communities");
 }
 
%>
<fcg:UserCommunities id="faves" user="<%= (atg.repository.RepositoryItem) profile %>">

<core:ExclusiveIf>
    <core:If value='<%= communitySwitch.equals("all")%>'>
      <blockquote>
      <table border=0 cellpadding=1 cellspacing=0 width="250">
      <tr><td><img src="/portal/layoutTemplates/images/clear.gif" height="18" width="160" border="0"></td></tr>
      <tr><td bgcolor="#333333"><table width=100% bgcolor="#<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>" border=0 cellpadding=3 cellspacing=0><tr><td>
      <%@ include file="communities_all.jspf" %>
     </td></tr></table></td></tr></table></blockquote>
    </core:If>

    <core:If value='<%= communitySwitch.equals("edit") %>'>
          <blockquote>
      <table border=0 cellpadding=1 cellspacing=0 width="250">
      <tr><td><img src="/portal/layoutTemplates/images/clear.gif" height="18" width="160" border="0"></td></tr>
      <tr><td bgcolor="#333333"><table width=100% bgcolor="#<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>" border=0 cellpadding=3 cellspacing=0><tr><td>
     <%@ include file="communities_edit.jspf" %>
     </td></tr></table></td></tr></table></blockquote>
    </core:If>

    <core:DefaultCase>
     <%-- full layout start --%>
     <paf:RenderLayout displayMode="full" page="<%= pafEnv.getPage() %>"/>
    </core:DefaultCase>
</core:ExclusiveIf>

</fcg:UserCommunities>

</body>

</html>

</paf:InitializeEnvironment>
</dsp:getvalueof>


<core:transactionStatus id="sharedPageXAStatus">
  <core:exclusiveIf>
    <%-- if we couldn't get the transaction status successfully, then rollback --%>
    <core:ifNot value="<%= sharedPageXAStatus.isSuccess() %>">
      <core:rollbackTransaction id="failedXAStatusRollback"/>
    </core:ifNot>

    <%-- if the transaction is marked for rollback, then roll it back --%>
    <core:if value="<%= sharedPageXAStatus.isMarkedRollback() %>">
      <core:rollbackTransaction id="sharedPageRollbackXA">
        <core:ifNot value="<%= sharedPageRollbackXA.isSuccess() %>">
            <i18n:message key="rollbackXAErrorMessage" id="xaRollbackErrorMessage"/>
          <paf:log message="<%= xaRollbackErrorMessage %>"
                   throwable="<%= sharedPageRollbackXA.getException() %>"/>
	</core:ifNot>
      </core:rollbackTransaction>
    </core:if>

    <%-- if the transaction is marked as active, then commit it. if that fails, then rollback --%>
    <core:if value="<%= sharedPageXAStatus.isActive() %>">
      <core:commitTransaction id="sharedPageCommitXA">
        <core:ifNot value="<%= sharedPageCommitXA.isSuccess() %>">
            <i18n:message key="commitXAErrorMessage" id="xaCommitErrorMessage"/>
          <paf:log message="<%= xaCommitErrorMessage %>"
                   throwable="<%= sharedPageCommitXA.getException() %>"/>
	  <core:rollbackTransaction id="secondTryRollbackXA"/>
	</core:ifNot>
      </core:commitTransaction>
    </core:if>    
  </core:exclusiveIf>
</core:transactionStatus>


</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/full_page_template.jsp#2 $$Change: 651448 $--%>
