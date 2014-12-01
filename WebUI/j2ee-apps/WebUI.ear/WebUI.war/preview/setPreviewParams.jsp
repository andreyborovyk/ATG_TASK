<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>
  <dspel:importbean bean="/atg/userprofiling/preview/PreviewContext"/>

  <c:catch var="ex">
    <%
      String url = java.net.URLDecoder.decode((String) request.getParameter("url"));
      pageContext.setAttribute("previewUrl", url, javax.servlet.jsp.PageContext.SESSION_SCOPE);
    %>

    <dspel:setvalue bean="PreviewContext.previewURL" value="${previewUrl}"/>
    <dspel:setvalue bean="PreviewContext.previewUserId" paramvalue="atgPreviewId"/>
    <dspel:setvalue bean="PreviewContext.previewSiteId" paramvalue="pushSite"/>

    {
      <c:out value='"success":true' escapeXml='false'/>
    }
  </c:catch>
  <c:if test="${ex != null}">
    <% 
      Exception e = (Exception) pageContext.findAttribute("ex");
      e.printStackTrace(System.err);
    %>
  </c:if>
</dspel:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/preview/setPreviewParams.jsp#2 $$Change: 651448 $--%>
