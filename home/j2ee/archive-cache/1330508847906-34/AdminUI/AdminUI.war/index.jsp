<%--
  The main framework page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
<d:importbean bean="/atg/searchadmin/adminui/Configuration" var="configuration"/>
<d:getvalueof bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent.current" var="currentNode"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%-- need for i18n --%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
  <c:set var="dojoBase" value="${configuration.dojoBaseScriptUri}"/>
  <fmt:message var="dojoLang" key="dojo.lang" />
  <script type="text/javascript" src="${dojoBase}${configuration.dojoJS}"
      djConfig="isDebug: false, parseOnLoad: true, usePlainJson: true, extraLocale: ['${dojoLang}']"></script>
  <link type="text/css" href="${dojoBase}dojo/resources/dojo.css" rel="stylesheet" />
  <link type="text/css" href="${dojoBase}dijit/themes/dijit.css" rel="stylesheet" />
  <link type="text/css" href="${dojoBase}dijit/themes/atg/atg.css" rel="stylesheet" />
  <!--script type='text/javascript' 
        src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script-->

  <link type="text/css" href="css/content.css" rel="stylesheet" media="all"/>
  <link type="text/css" href="css/tabs.css"   rel="stylesheet" media="all" />  
  <link type="text/css" href="css/dojo_tree.css" rel="stylesheet" media="all">
  <link type="text/css" href="css/navigation.css" rel="stylesheet" media="all"/>

  <link type="text/css" href="/WebUI/dijit/assistance/templates/base.css" rel="stylesheet"/>
  <link type="text/css" href="/WebUI/dijit/assistance/templates/popup.css" rel="stylesheet"/>
  <link type="text/css" href="/WebUI/dijit/assistance/templates/inline.css" rel="stylesheet"/>

  <fmt:message var="responseWaiting" key="formsubmitter.on_response_waiting.message"/>
  <fmt:message var="loadingMessage" key="tree.dojo.loading"/>
  <fmt:message var="confirmMessage" key="index.confirm_message"/>
  <fmt:message var="sessionExpiredMessage" key="index.session_expired"/>
  <fmt:message var="serverUnavailableMessage" key="index.server_unavailable"/>

  <script type="text/javascript">
    var uiConfig = {
      contextPath: "${pageContext.request.contextPath}",
      fieldsChanged: false,
      responseWaiting: "${adminfunctions:escapeJsString(responseWaiting)}",
      confirmMessage: "${adminfunctions:escapeJsString(confirmMessage)}",
      sessionExpiredMessage: "${adminfunctions:escapeJsString(sessionExpiredMessage)}",
      serverUnavailableMessage: "${adminfunctions:escapeJsString(serverUnavailableMessage)}",
      maxRange: "${configuration.treeBucketSize}",
      checkForm: function() {
        if (!this.fieldsChanged) {
          return true;
        }
        if (!confirm(uiConfig.confirmMessage)) {
          return false;
        }
        this.fieldsChanged = false;
        return true;
      },
      ajaxTimeout: ${configuration.ajaxTimeout}
    };

    <c:set var="currentUrl" value="/searchadmin/new_project.jsp"/>
    <c:if test="${not empty currentNode}">
      <d:getvalueof bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent.linkBuilder" var="linkBuilder"/>
      <c:set var="currentUrl" value="${linkBuilder.link}"/>
    </c:if>
    dojo.addOnLoad(function() {
      atg.searchadmin.adminui.formsubmitter.loadingMessage = "${adminfunctions:escapeJsString(loadingMessage)}";
      loadRightPanel("${pageContext.request.contextPath}${currentUrl}");
    });
  </script>

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/startup.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/navigation.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/global.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/common.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/content.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/content_table.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/tree_common.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/alerting.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/view_host.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/synchronization_manual.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/formsubmitter.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/LazyLoadStore.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/DojoTree.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/DojoTreeMenu.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/DojoTreeDnDSource.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/DojoTreeTooltip.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/BrowseDojoTree.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/tree/BrowseLazyStore.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/sync_status_monitor.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/table_modification.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/facet_selections.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/datetime.js"></script>

  <script type="text/javascript">
    dojo.registerModulePath("atg.searchadmin.adminui", uiConfig.contextPath + "/scripts");
    dojo.registerModulePath("atg.searchadmin.tree", uiConfig.contextPath + "/scripts/tree");
    dojo.require("atg.searchadmin.adminui.formsubmitter");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.SplitContainer");
    dojo.require("dojo.parser");
    dojo.require("dijit.Dialog");
    dojo.require("dijit.Tooltip");
    dojo.require("dojo.data.ItemFileWriteStore");
    dojo.require("atg.searchadmin.tree.LazyLoadStore");
    dojo.require("atg.searchadmin.tree.DojoTree");
    dojo.require("atg.searchadmin.tree.DojoTreeMenu");
    dojo.require("atg.searchadmin.tree.DojoTreeDnDSource");
    dojo.require("atg.searchadmin.tree.DojoTreeTooltip");
    dojo.require("atg.searchadmin.tree.BrowseDojoTree");
    dojo.require("atg.searchadmin.tree.BrowseLazyStore");
    dojo.require("dijit.Menu");
    dojo.require("dijit.Tree");
    dojo.require("dijit.form.Button");
    dojo.require("dijit._tree.dndSource");
    dojo.require("dojo.io.iframe");
  </script> 
    
  <title><fmt:message key="index.title"/></title>
</head>

<body style="overflow: auto;" class="atgSearchAdmin">

<!-- Pop-up Dialog Widget Begin -->
<%-- <div dojoType="dijit.Dialog" id="dialog1" href="#" class="popUpDiv" style="display: none;" onLoad="atg.searchadmin.adminui.formsubmitter.initialize();">
</div>--%>
<div id="dialogDiv">
  <div dojoType="dojox.layout.ContentPane" parseOnLoad="true" id="dialogContentPane" href="#" class="popUpDiv" style="display: none;" onLoad="dialogLoad">
  </div>
</div>
<!-- Pop-up Dialog Widget End -->


<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; padding: 0; margin: 0; border: 0;">

<%-- Utilities sub-menu --%>
<div id="utilDD" style="display:none;">
  <ul>
    <%-- TODO: maybe we will need to add security check for showing reporting link --%>
    <c:catch var="cognosUnavailable">
      <c:set var="reportingLink"><d:valueof bean="/atg/cognos/Configuration.gatewayURI" /></c:set>
    </c:catch>
    <c:if test="${empty cognosUnavailable}">
      <li class="reporting">
        <a href="<c:out value='${reportingLink}' />" onclick="utilitiesMenu.hide();">
          <fmt:message key="index.utility.navigation.reports.label"/>
        </a>
      </li>
    </c:if>
    <li class="docs" >
      <a href="<fmt:message key="index.utility.navigation.docs.url"/>" target="_blank" onclick="utilitiesMenu.hide();">
        <fmt:message key="index.utility.navigation.docs.label"/>
      </a>
    </li>
    <li class="about">
      <fmt:message var="aboutPage" key="page_about"/>
      <a href="<c:url value='${aboutPage}'/>" onclick="utilitiesMenu.hide(); return openSeparateWindow(this.href, 'AboutWindow', 400, 440);">
        <fmt:message key="index.utility.navigation.about.label"/>
      </a>
    </li>
  </ul>
</div>

<div id="header" dojoType="dojox.layout.ContentPane" layoutAlign="top">
  <div id="logoHeader">
    <div id="logoHeaderRight">
      <d:importbean bean="/atg/userprofiling/Profile" var="profile"/>
      <c:if test="${not profile.transient}">
        <fmt:message key="index.message.logged_in">
          <fmt:param><d:valueof bean="/atg/userprofiling/Profile.firstName"/></fmt:param>
          <fmt:param><d:valueof bean="/atg/userprofiling/Profile.lastName"/></fmt:param>
        </fmt:message>
        <span>|</span>
      </c:if>
      <a id="utilitiesMenuItem" href='#' class="utils" onclick="return false;" >
        <fmt:message key="index.utility.navigation.utilities.label" />
      </a>
      <span id="spanEl">|</span>
      <d:a href="bcc.jsp" iclass="home" onclick="return uiConfig.checkForm();">
        <fmt:message key="index.link.bcc_home.title"/>
      </d:a>
      <span>|</span>
      <c:url var="logoutUrl" value="/logout"/>
      <d:a href="${logoutUrl}" iclass="logout" onclick="return uiConfig.checkForm();">
        <fmt:message key="index.link.logout.title"/>
      </d:a>
    </div>

    <div id="logoHeaderLeft">
      <h1>
        <fmt:message key="index.administration"/>
      </h1>
    </div>
  </div>

</div>

<script type="text/javascript">
  var utilitiesMenu = new MenuItem(document.getElementById('utilitiesMenuItem'),
      document.getElementById('utilDD'), document.getElementById('logoHeaderRight'));
</script>

<div id="subheading" dojoType="dojox.layout.ContentPane" layoutAlign="top">
  <%-- TODO JS strings should be escaped --%>
  <script type="text/javascript">
    alerting.alertHeaderAShow = "<fmt:message key="alert_header.a.show"/>";
    alerting.alertHeaderAHide = "<fmt:message key="alert_header.a.hide"/>";
    alerting.alertHeaderTitleShow = "<fmt:message key="alert_header.title.show"/>";
    alerting.alertHeaderTitleHide = "<fmt:message key="alert_header.title.hide"/>";
  </script>
   
  <div id="tabContainer" class="atg_adminMainNav" >
    <!-- element to show only one message as title -->
    <div id="alertBrief" style="display:none;"><div id="alertBriefTitle"></div></div> 
    <!-- element to show detailed messages -->
    <div id="alert" style="display:none;">
      <a href="#" id="alertTitleMessage" onclick="return alerting.detailsOnClick()"></a>
      <span class="alertDetail">
        <a href="#" onclick="return alerting.detailsOnClick()">
          [<span><fmt:message key="alert_header.a.show"/></span>
          <span id="count"></span>
          <span><fmt:message key="alert_header.a.details"/></span>]
        </a>
      </span>
    </div>
    
    <ul>
      <d:getvalueof var="navTree" bean="/atg/searchadmin/adminui/navigation/NavigationTreeComponent" />
      <c:forEach items="${navTree.areas}" var="area">
        <li id="area_${area.id}">
          <a href="#" onclick="return areaSupport.switchArea('${area.id}', 'tab');"><fmt:message key="${area.title}" /></a>
        </li>
      </c:forEach>
    </ul>
  </div>
</div>

<div id="content" layoutAlign="client" dojoType="dijit.layout.SplitContainer" orientation="horizontal"
     sizerWidth="4" activeSizing="true" persist="false">
  <div id="navPane" dojoType="dojox.layout.ContentPane" sizeMin="150" sizeShare="29"
       href="${pageContext.request.contextPath}/templates/tree.jsp" 
       executeScripts="true" cacheContent="false" style="overflow:auto" 
       loadingMessage="<fmt:message key='tree.dojo.loading'/>" scriptSeparation="false">
  </div>
  
  <div dojoType="dojox.layout.ContentPane" id="rightPanel" class="rightPanel" executeScripts="true" cacheContent="false" 
       loadingMessage="<fmt:message key='tree.dojo.loading'/>" scriptSeparation="false" sizeMin="300" sizeShare="71"
       style="overflow:auto" onload="rightPanelLoad()"></div>

  <%--<div id="pageClear"></div>--%>
</div>
</div>


<div id="alertPop" style="display:none">
  <iframe src="${pageContext.request.contextPath}/searchadmin/alert_confirm_pop.jsp" frameborder="0"
          scrolling="no" width="465" height="135" id="alertFrame" name="alertFrame"></iframe>
</div>

</body>
</html>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/index.jsp#2 $$Change: 651448 $--%>
