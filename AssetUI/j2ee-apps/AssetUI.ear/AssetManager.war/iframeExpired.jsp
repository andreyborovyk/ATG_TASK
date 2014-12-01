<%--
  Panel displayed in an iframe if the user is not logged, presumably
  because the session expired.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/iframeExpired.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title></title>
      <dspel:include page="/components/head.jsp"/>
    </head>

    <body id="framePage">

      <div id="error">
        <fmt:message key="iframeExpired.message"/>
      </div>

    </body>
  </html>
          
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/iframeExpired.jsp#2 $$Change: 651448 $ --%>
