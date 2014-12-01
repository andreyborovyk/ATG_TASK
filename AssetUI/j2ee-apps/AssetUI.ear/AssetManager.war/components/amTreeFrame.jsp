<%--
  This JSP delegates the tree frame to webui.
  
  It is intended to be enclosed in an iframe.  It is just a wrapper around the
  tree.jsp fragment.
  
  See tree.jsp for more details.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/amTreeFrame.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
      </title>
      <%-- Include the given page fragment. --%>
      <c:if test="${not empty param.headIncludePage}">
        <dspel:include otherContext="${param.headIncludeContextRoot}" page="${param.headIncludePage}"/>
      </c:if>

      <%-- Load styles from the given style sheet. --%>
      <c:if test="${not empty param.styleSheet}">
        <dspel:link href="${param.styleSheet}"
                    rel="stylesheet"
                    type="text/css"
                    media="all"/>
      </c:if>
    </head>

    <body class="treeFrame" onload="atgTreeFrameLoaded()">
      <div id="screen"></div>
      <dspel:include otherContext="/WebUI" page="/tree/tree.jsp"/>
    </body>

  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/amTreeFrame.jsp#2 $$Change: 651448 $--%>
