<%--
  Pass through page between apply to all form and confirmation stage.

  param: op the operation we are confirming
--%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <%@ include file="multiEditConstants.jspf" %>
  <dspel:getvalueof var="op" param="op"/>
  <script type="text/javascript">
    parent.changeMode('<c:out value="${MODE_CONFIRM}"/>','<c:out value="${op}"/>','null');
  </script>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/confirmationPassThru.jsp#2 $$Change: 651448 $--%>
