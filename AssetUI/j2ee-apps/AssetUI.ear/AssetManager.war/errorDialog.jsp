<%--
  Error dialog for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/errorDialog.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="profile"
                    bean="/atg/userprofiling/Profile"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>

      <%-- NOTE: This JSP is specified as the src of an initially invisible
           iframe in assetManager.jsp.  We can't include dojo.js in such an
           iframe.  See PR 146771 for details. --%>
      <dspel:include page="components/head.jsp">
        <dspel:param name="skipDojo" value="true"/>
      </dspel:include>
    </head>

    <body id="assetBrowser" class="errorDialog">

      <%-- If not logged in (session expired) close window and refresh parent --%>
      <c:if test="${profile.transient}">
        <script type="text/javascript">
          parent.messages.hide();
          parent.location.reload();
        </script>
      </c:if>

      <h1>
        <span class="errorDialogIcon">&nbsp;</span>
        <fmt:message key="errorDialog.header"/>
      </h1>

      <div id="confirmContentBody">
        <div id="confirmScrollContainer">
          <p id="errorDialogMessage" class="confirmMessage">
          </p>
        </div>
      </div>

      <div id="assetBrowserFooterRight">
        <a href="javascript:parent.messages.hide()"
           class="button" title="<fmt:message key='common.ok.title'/>">
          <span>
            <fmt:message key="common.ok"/>
          </span>
        </a>
      </div>
      <div id="assetBrowserFooterLeft">
      </div>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/errorDialog.jsp#2 $$Change: 651448 $--%>
