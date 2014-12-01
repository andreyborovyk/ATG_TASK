<%--
  Main page for the ATG Business Control Center web application.

  @version $Id: //product/BCC/version/10.0.3/src/web-apps/ControlCenter/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"              %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"               %>

<dsp:page>

  <dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
  <dsp:importbean var="ccs" bean="/atg/remote/controlcenter/service/ControlCenterService"/>

  <fmt:setBundle basename="atg.remote.controlcenter.Resources"/>

  <c:set var="appElemId" value="controlCenterApp"/>
  <c:set var="appSwfName" value="ControlCenterApp.swf"/>
  <c:set var="bgcolor" value="#21283b"/>

  <%
    // Assemble FlashVars.
    StringBuffer buf = new StringBuffer();

    // Append the session id.
    buf.append("jsessionid=").append(session.getId());

    // Append the user's locale.
    java.util.Locale locale = atg.servlet.ServletUtil.getUserLocale();
    buf.append("&locale=").append(locale);

    // Append any request parameters.
    java.util.Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String name = (String) parameterNames.nextElement();
      String value = request.getParameter(name);
      if (value != null) {
        buf.append('&');
        buf.append(name);
        buf.append('=');
        buf.append(value);
      }
    }

    pageContext.setAttribute("flashVars", buf.toString());
  %>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
      <title>
        <fmt:message key="controlCenter.pageTitle"/>
      </title>
      <link rel="icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon"/>
      <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon"/>
      <style type="text/css">
        body, html {
          margin:   0px;
          padding:  0px;
          overflow: hidden;
          height:   100%;
        }
      </style>
    </head>

    <c:choose>
      <c:when test="${profile.transient}">
        <c:set var="bccRoot" scope="page">
          <c:out value="${ccs.bccCommunityRoot}"/>
        </c:set>
        <%
          String bccRoot = (String)pageContext.getAttribute("bccRoot");
          atg.servlet.ServletUtil.getDynamoResponse(request,response).sendRedirect(bccRoot);
        %>
      </c:when>
      <c:otherwise>
        <body scroll="no">
          <script language="JavaScript" type="text/javascript">
            var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
            var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
            var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
            var str = '';
            if (isIE && isWin && !isOpera) {
              str += '<object ';
              str += ' classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"';
              str += ' id="<c:out value='${appElemId}'/>"';
              str += ' name="<c:out value='${appElemId}'/>"';
              str += ' width="100%" height="100%" align="middle"';
              str += ' type="application/x-shockwave-flash"';
              str += ' codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">';
              str += '  <param name="movie" value="<c:out value='${appSwfName}'/>"/>';
              str += '  <param name="FlashVars" value="<c:out value='${flashVars}' escapeXml='false'/>"/>';
              str += '  <param name="quality" value="high"/>';
              str += '  <param name="bgcolor" value="<c:out value='${bgcolor}'/>"/>';
              str += '  <param name="allowScriptAccess" value="sameDomain"/>';
              str += '</object>';
            }
            else {
              str += '<embed ';
              str += ' id="<c:out value='${appElemId}'/>"';
              str += ' name="<c:out value='${appElemId}'/>"';
              str += ' src="<c:out value='${appSwfName}'/>"';
              str += ' FlashVars="<c:out value='${flashVars}' escapeXml='false'/>"';
              str += ' quality="high" bgcolor="<c:out value='${bgcolor}'/>"';
              str += ' width="100%" height="100%" align="middle"';
              str += ' allowScriptAccess="sameDomain"';
              str += ' type="application/x-shockwave-flash"';
              str += ' pluginspage="http://www.adobe.com/go/getflashplayer">';
              str += ' </embed>';
            }
            document.write(str);
          </script>
          <noscript>
            <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                    id="<c:out value='${appElemId}'/>"
                    width="100%" height="100%"
                    codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
              <param name="movie" value="<c:out value='${appSwfName}'/>"/>
              <param name="FlashVars" value="<c:out value='${flashVars}' escapeXml='false'/>"/>
              <param name="quality" value="high"/>
              <param name="bgcolor" value="<c:out value='${bgcolor}'/>"/>
              <param name="allowScriptAccess" value="sameDomain"/>
              <embed name="<c:out value='${appElemId}'/>" src="<c:out value='${appSwfName}'/>"
                     FlashVars="<c:out value='${flashVars}' escapeXml='false'/>"
                     quality="high" bgcolor="<c:out value='${bgcolor}'/>"
                     width="100%" height="100%" align="middle"
                     play="true" loop="false"
                     allowScriptAccess="sameDomain"
                     type="application/x-shockwave-flash"
                     pluginspage="http://www.adobe.com/go/getflashplayer">
              </embed>
            </object>
          </noscript>
        </body>
      </c:otherwise>
    </c:choose>

    <script language="JavaScript" type="text/javascript">
    function onInitialized() {
      var f = document.getElementById('<c:out value="${appElemId}"/>');
      if (f) {
        f.focus();
      }
    }
    </script>
  </html>
  
</dsp:page>
<%-- @version $Id: //product/BCC/version/10.0.3/src/web-apps/ControlCenter/index.jsp#2 $$Change: 651448 $--%>
