<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>
      <c:if test="${not empty param.__title}">
        <c:out value="${param.__title}"/>
      </c:if>
    </title>
    <script type="text/javascript">
      if (!(parent.pageIds)) {
        parent.pageIds = new Array();
      }
      function onLoad() {
        var url = document.location.href;
        if (parent.pageIds[url]) {
          eval("parent.__ppr_<c:out value="${param.__actionId}"/>.retransact(\"" + url.substr(url.indexOf("?") + 1) + "\");");
        }
        else {
          parent.pageIds[url] = url;
        }
      }
    </script>
  </head>
  <body onload="javascript:onLoad();">
  </body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/partial_page_renderer_history.jsp#2 $$Change: 651448 $--%>
