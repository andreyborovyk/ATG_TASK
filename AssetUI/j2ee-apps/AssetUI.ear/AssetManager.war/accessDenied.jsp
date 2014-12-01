<%--
  Access denied page for the AssetManager.  This just redirects the top-level
  browser window to the BCC home page.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/accessDenied.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
      </title>
    </head>
    <body>
      <script type="text/javascript">
        var top = window;
        while (top.parent != null && top.parent != top) {
          top = top.parent;
        }
        top.document.location = "<c:out value='${config.bccCommunityRoot}'/>";
      </script>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/accessDenied.jsp#2 $$Change: 651448 $--%>
