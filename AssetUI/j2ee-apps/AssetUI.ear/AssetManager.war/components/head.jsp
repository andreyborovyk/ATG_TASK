<%--
  Common imports to be placed in the HTML HEAD tag such as javascript and CSS.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/head.jsp#3 $$Change: 654746 $
  @updated $DateTime: 2011/06/27 09:01:12 $$Author: ikalachy $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <dspel:getvalueof var="paramSkipDojo" param="skipDojo"/>

  <dspel:importbean var="preferences" bean="/atg/web/assetmanager/Preferences"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:link href="${config.dojoRoot}/dojo/resources/dojo.css"
              rel="stylesheet"
              type="text/css"
              media="all"/>

  <dspel:link href="${config.dojoThemeFile}"
              rel="stylesheet"
              type="text/css"
              media="all"/>

  <dspel:link href="${config.webuiRoot}/css/cssFramework.css"
              rel="stylesheet"
              type="text/css"
              media="all"/>

  <!--[if IE 6]>
    <dspel:link href="${config.webuiRoot}/css/cssFramework_IE6.css"
                rel="stylesheet"
                type="text/css"
                media="all"/>
  <![endif]-->

  <dspel:link page="${config.styleSheet}"
              rel="stylesheet"
              type="text/css"
              media="all"/>

  <dspel:link href="${config.webuiRoot}/css/inlineExpreditorStyles.css"
              rel="stylesheet"
              type="text/css"
              media="all"/>

  <c:forEach var="browserType" items="${config.browserSpecificStyleSheets}">
    <!--[if <c:out value="${browserType.key}"/>]>
    <dspel:link page="${browserType.value}"
                rel="stylesheet"
                type="text/css"
                media="all"/>
    <![endif]-->
  </c:forEach>

  <c:forEach var="styleSheet" items="${config.additionalStyleSheets}">
    <dspel:link href="${styleSheet}"
                rel="stylesheet"
                type="text/css"
                media="all"/>
  </c:forEach>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/common.js">
  </script>

  <%-- Initializing the expression editor's asset picker title.  (Could obtain
       the title from grammar construct attributes instead). --%>
  <script type="text/javascript">
    <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle"/>
    <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
    defaultAssetPickerTitle = "<c:out value='${assetPickerTitle}'/>";
  </script>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/assetPickerFunctions.js">
  </script>

  <c:if test="${empty paramSkipDojo}">
    <script type="text/javascript">
      var djConfig = {
        <c:if test="${config.dojoDebug}">
          isDebug: true,
          popup: true,
        </c:if>
        parseOnLoad: true,
        usesApplets: true
      };
    </script>

    <c:choose>
      <c:when test="${config.dojoDebug}">
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojo/dojo.js.uncompressed.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojo/dojo-atg-apps.js.uncompressed.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dijit/dijit.js.uncompressed.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dijit/dijit-atg-apps.js.uncompressed.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojox/dojox-atg-apps.js.uncompressed.js'/>"></script>
      </c:when>
      <c:otherwise>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojo/dojo.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojo/dojo-atg-apps.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dijit/dijit.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dijit/dijit-atg-apps.js'/>"></script>
        <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojox/dojox-atg-apps.js'/>"></script>
      </c:otherwise>
    </c:choose>

    <script type="text/javascript">
      dojo.require("dojo.parser");
      dojo.require("dojo.cookie");
      dojo.require("dijit.dijit");

      dojo.registerModulePath("atg.widget", "/WebUI/dijit");
      dojo.require("atg.widget.checkAll.checkAll");
      dojo.require("dijit._Calendar");

    </script>

    <script type="text/javascript" src="<c:url context='${config.dojoRoot}' value='/dojo-fixes.js'/>"></script>

    <script type="text/javascript"
            src="<c:out value='${config.webuiRoot}'/>/atg.js">
    </script>

    <script type="text/javascript"
            src="<c:out value='${config.contextRoot}'/>/scripts/gridServerModel.js">
    </script>

    <script type="text/javascript"
            src="<c:out value='${config.contextRoot}'/>/scripts/gridCollectionEditor.js">
    </script>

    <script type="text/javascript"
            src="<c:out value='${config.contextRoot}'/>/scripts/assetPickerLauncher.js">
    </script>
    <script type="text/javascript">
      globalAssetPickerDefaultView = '<c:out value="${preferences.assetPickerDefaultView}"/>';
    </script>

  </c:if>

  <script type="text/javascript"
          src="<c:out value='${config.webuiRoot}'/>/scripts/inlineExpreditor.js">
  </script>

  <c:forEach var="javaScriptFile" items="${config.additionalJavaScriptFiles}">
    <script type="text/javascript"
            src="<c:out value='${javaScriptFile}'/>">
    </script>
  </c:forEach>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/head.jsp#3 $$Change: 654746 $--%>
