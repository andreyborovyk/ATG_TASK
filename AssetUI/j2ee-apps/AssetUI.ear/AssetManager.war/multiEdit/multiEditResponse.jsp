<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>

 { "value": "<c:out value='${sessionInfo.multiEditSessionInfo.multiEditRunnable.multiEditPercentFinished}'/>",
   "remainCount": "<c:out value='${sessionInfo.multiEditSessionInfo.multiEditRemainingCount}'/>",
   "successCount": "<c:out value='${sessionInfo.multiEditSessionInfo.multiEditSuccessCount}'/>",
   "noOpCount": "<c:out value='${sessionInfo.multiEditSessionInfo.multiEditNoopCount}'/>" ,
   "errorCount": "<c:out value='${sessionInfo.multiEditSessionInfo.multiEditErrorCount}'/>" }

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/multiEditResponse.jsp#2 $$Change: 651448 $--%>
