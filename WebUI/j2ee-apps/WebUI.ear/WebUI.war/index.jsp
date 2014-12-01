<%--
  Index page for the WebUI application.
  This is merely a placeholder for verifying that the application is running.
  
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"                     %>
<%--
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
--%>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/Configuration"/>
 
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="index.pageTitle"/>
      </title>

      <dspel:link page="${config.styleSheet}" rel="stylesheet" type="text/css"/>
    </head>

    <body>
      <fmt:message var="message" key="index.message"/>
      <c:out value="${message}"/><br/>
      <%--
      <web-ui:size var="size" string="${message}"/>
      (<c:out value="${size}"/>)<br/>
      --%>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/index.jsp#2 $$Change: 651448 $ --%>
