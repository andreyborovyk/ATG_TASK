<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%-- This page outputs JSON format --%>  

<dspel:page>
  <dspel:importbean bean="/atg/userprofiling/preview/PreviewContext"/>

  <c:catch var="ex">
    {
     "selectedUser":"<dspel:valueof bean='PreviewContext.previewUserId'/>",
     "selectedUrl":"<dspel:valueof bean='PreviewContext.previewURL'/>",
     "selectedSite":"<dspel:valueof bean='PreviewContext.previewSiteId'/>",
     "error":null,
     "success":true
    }
  </c:catch>
  <c:if test="${ex != null}">
    {
     "selectedUser":"",
     "selectedUrl":"",
     "selectedSite":"",
     "error":<c:out value="${ex}"/>,
     "success":false
    }
    <% 
      Exception e = (Exception) pageContext.findAttribute("ex");
      e.printStackTrace(System.err);
    %>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/preview/getSelectedUserAndUrl.jsp#2 $$Change: 651448 $--%>
