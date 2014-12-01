<%@ page buffer="64kb" %>

<%@ page import="java.io.*,java.util.*,atg.repository.RepositoryItem" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/fcg-taglib" prefix="fcg" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

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
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache" >
<title>
  <%= pafEnv.getCommunity().getName() %><i18n:message key="title_commname_pagename_seperator"/><%= pafEnv.getPage().getName() %>
</title>

<%
 String clearGIF = "/portal/layoutTemplates/images/clear.gif"; 
 boolean isNav4   = false;
 boolean isNav6   = false;
 boolean isOpera  = false;
 boolean isOpera6 = false;
 boolean isIE     = false;
 boolean isIE5    = false;
 boolean isMoz5   = false;

 String uAgent = "";

 if (request.getHeader("USER-AGENT") != null) {
   uAgent =  request.getHeader("USER-AGENT");
   if (uAgent.indexOf("MSIE") != -1) {
     isIE = true;
     if (uAgent.indexOf("MSIE 5") != -1 ) {
      isIE5  = true;
     }
   } else if ( uAgent.indexOf("Opera") != -1  ) {
     isOpera = true;
     if (uAgent.indexOf("Opera 5") != -1 ) {
      isOpera6 = true;
     }
   } else {
     isNav4 = true;   
     if ( uAgent.indexOf("Mozilla/5")!= -1  && uAgent.indexOf("Netscape6") != -1 ) {
      isNav6 = true;
      isNav4 = false;    
     }
     if ( uAgent.indexOf("Mozilla/5")!= -1  && uAgent.indexOf("Netscape6") == -1 ) {
      isMoz5 = true;
      isNav4 = false;    
     }
   }
 }

%>


<core:If value="<%=! isNav4 %>">
 <style type="text/css">
  <!--
   #bodyMain  { z-index:10; position: absolute; top: 0px;  left: 0px; width: 100%;  visibility: visible }
   #headerSub { z-index:100; position: absolute; top: 84px; left:10px; width: 250px; visibility:hidden}
  -->
 </style>
</core:If>

<core:ExclusiveIf>

  <core:IfNotNull value="<%= pafEnv.getCommunity().getStyle() %>">
   <link rel="STYLESHEET" type="text/css" href="<%= response.encodeUrl(pafEnv.getCommunity().getStyle().getCSSURL()) %>" >
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

<script type="text/javascript"><!--

    CSStopExecution = false;

    function CSAction(array) { 
      return CSAction2(CSAct, array);
    }

    function CSAction2(fct, array) { 
      var result;
      for (var i=0;i<array.length;i++) {
        if(CSStopExecution) return false; 
        var actArray = fct[array[i]];
        if(actArray == null) return false; 
        var tempArray = new Array;
        for(var j=1;j<actArray.length;j++) {
          if((actArray[j] != null) && (typeof(actArray[j]) == "object") && (actArray[j].length == 2)) {
            if(actArray[j][0] == "VAR") {
              tempArray[j] = CSStateArray[actArray[j][1]];
            }
            else {
              if(actArray[j][0] == "ACT") {
                tempArray[j] = CSAction(new Array(new String(actArray[j][1])));
              }
              else
                tempArray[j] = actArray[j];
            }
          }
          else
            tempArray[j] = actArray[j];
        }            
        result = actArray[0](tempArray);
      }
      return result;
    }

    CSAct = new Object;
    CSAg = window.navigator.userAgent;
    CSBVers = parseInt(CSAg.charAt(CSAg.indexOf("/")+1),10);

    function CSIEStyl(s) {
          return document.all.tags("div")[s].style; 
    }
    function CS6Styl(s) { 
          return document.getElementById(s); 
    }

    function CSNSStyl(s) { return CSFindElement(s,0); }

    function CSFindElement(n,ly) {
      if (CSBVers < 4) return document[n];
      var curDoc = ly ? ly.document : document; var elem = curDoc[n];
      if (!elem) { for (var i=0;i<curDoc.layers.length;i++) {
        elem = CSFindElement(n,curDoc.layers[i]); if (elem) return elem; }}
      return elem;
    }

    function CSSetStyleVis(s,v) {
      if  (CSAg.indexOf("MSIE") > 0) {
         CSIEStyl(s).visibility = (v == 0) ? "hidden" : "visible";
      } else if (CSAg.indexOf("Opera 6") > 0 ) {
         CS6Styl(s).style.visibility = (v == 0) ? 'hidden' : 'visible';
      } else if (document.layers ) {
         CSNSStyl(s).visibility = (v == 0) ? 'hide' : 'show';      
      } else {
         CS6Styl(s).style.visibility = (v == 0) ? 'hidden' : 'visible';      
      }
    }
 
    function  cssStyleSetVisibility(visibility)    {
        this.styleObj.visibility = visibility;
    }
    function CSGetStyleVis(s) {
      if (CSAg.indexOf("MSIE")>0 ) return (CSIEStyl(s).visibility == "hidden") ? 0 : 1;
      else return (CSNSStyl(s).visibility == 'hide') ? 0 : 1;
    }

    function CSShowHide(action) {
      if (action[1] == '') return;
      var type=action[2];
      if(type==0) CSSetStyleVis(action[1],0);
      else if(type==1) CSSetStyleVis(action[1],1);
      else if(type==2) { 
        if (CSGetStyleVis(action[1]) == 0) CSSetStyleVis(action[1],1);
        else CSSetStyleVis(action[1],0);
      }
    }


// --></script>

<script type="text/javascript">
<!--
    CSAct[/*CMP*/ '2D55DE0'] = new Array(CSShowHide,/*CMP*/ 'headerSub',1);
    CSAct[/*CMP*/ '2DF1FD1'] = new Array(CSShowHide,/*CMP*/ 'headerSub',2);
    CSAct[/*CMP*/ '2E52BB2'] = new Array(CSShowHide,/*CMP*/ 'headerSub',0);
// -->
</script>


</head>

<body <%= pafEnv.getPage().getColorPalette().getBodyTagData() %>  marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
  <core:If value="<%=!isNav4%>"> 
   <div id="bodyMain">
  </core:If>

  <%-- HEADER INCLUDE --%>
  <dsp:include page="header_main.jsp" flush="false"/>

  <%-- LAYOUT INCLUDE --%>
<%
   // On WebSphere the JspWriter needs to be flushed to the ServletOutputStream
   // As the tag uses the PrintWriter to write its content.  Inorder for the
   // Content to appear in the correct order the JspWriter needs to be flushed.
   if(atg.servlet.ServletUtil.isWebSphere())
      out.flush();
%>
  <paf:RenderLayout displayMode="shared" page="<%= pafEnv.getPage() %>"/>

  <%-- DHTML FAVORITES --%>


 <core:If value="<%=!isNav4%>"> 
   </div>
  </core:If>

 <%-- DHTML FAVORITES --%>
<core:ExclusiveIf>
 <core:If value="<%=isNav4%>">
  <LAYER NAME="headerSub" ID="headerSub"  left=&{10}; top=&{85}; width=250  VISIBILITY=HIDE onmouseout="CSAction(new Array('2E52BB2'));" csout="2E52BB2" >
 </core:If>
 <core:If value="<%=isIE5 %>">
  <div id="headerSub"  onmouseleave="CSAction(new Array('2E52BB2'));" csout="2E52BB2" >
 </core:If>
 <core:DefaultCase>
  <div id="headerSub" onmouseover="CSAction(new Array('2D55DE0'));" onmouseout="CSAction(new Array('2E52BB2'));" csout="2E52BB3" >
 </core:DefaultCase>
</core:ExclusiveIf>

<table border=0 cellpadding=1 cellspacing=0 width="250">
<tr>
<td><img src="<%=clearGIF%>" height="18" width="160" border="0"></td>
</tr>
<tr>
<td bgcolor="#333333"><table width=100% bgcolor="#<%= pafEnv.getPage().getColorPalette().getGearBackgroundColor() %>" border=0 cellpadding=3 cellspacing=0><tr><td>


<fcg:UserCommunities id="faves" user="<%= (atg.repository.RepositoryItem) profile %>">
 <%@ include file="shared_favorites.jspf" %>
</fcg:UserCommunities>
 
</td></tr></table></td></tr></table>

<core:ExclusiveIf>
 <core:If value="<%=isNav4%>">
  </LAYER>
 </core:If>
 <core:DefaultCase>
  </div>
 </core:DefaultCase>
</core:ExclusiveIf>

<img src='<%= clearGIF %>' height="1" width="1" border="0">

<!-- <%= uAgent %> -->

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
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/shared_page_template.jsp#2 $$Change: 651448 $--%>
