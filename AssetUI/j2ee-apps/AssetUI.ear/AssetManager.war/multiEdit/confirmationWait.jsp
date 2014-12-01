<%--
  Pass through page between apply to all form and confirmation stage.

  param: op the operation we are confirming
--%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <%@ include file="multiEditConstants.jspf" %>
  <dspel:getvalueof var="op" param="op"/>

<html>
<head>
  <link href="/WebUI/css/cssFramework.css" type="text/css" rel="stylesheet" media="all"></link>

   <!--[if IE 6]>
     <link href="/WebUI/css/cssFramework_IE6.css" type="text/css" rel="stylesheet" media="all"></link>
   <![endif]-->

   <link href="/AssetManager/css/styles.css" type="text/css" rel="stylesheet" media="all"></link>
   <link href="/WebUI/dijit/progress/templates/progress.css" rel="stylesheet" type="text/css"></link>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript"
          src="<c:out value='${config.dojoRoot}'/>/dojo/dojo.js" djConfig="isDebug:true, parseOnLoad: true">
  </script>

    <script type="text/javascript"
            src="<c:out value='${config.dojoRoot}'/>/dojo-fixes.js">
    </script>

<%--
    <script type="text/javascript"
            src="<c:out value='${config.contextRoot}'/>/scripts/dojo-fixes.js">
    </script>
--%>


  <script type="text/javascript" charset="utf-8">
    dojo.require("dijit._Widget");
    dojo.require("dijit._Templated");
    dojo.require("dojo.parser"); // scan page for widgets and instantiate them
    

    // Register the Progress Bar widget namespace
    dojo.registerModulePath("atg.widget", "/WebUI/dijit");
    dojo.require("atg.widget.progress.base");
    
  </script>
  
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>


<c:set target="${sessionInfo.multiEditSessionInfo}" property="multiEditMode" value="${MODE_WAIT}"/>


<script type="text/javascript">

    var stoppedIntervalVar = null;

    // this is the response from asking the server to stop the multi edit operation
    //
    function stoppedResponse(pData, pResponseText) {
         clearInterval(stoppedIntervalVar);
         stoppedIntervalVar = null;
    }
        
    // if the server never gets the stop message, try again.
    function stopAgain() {
       if (stoppedIntervalVar != null)
         parent.issueRequest("<c:out escapeXml='false' value='${stopUrl}'/>", null, stoppedResponse);
    }

    // the stop button calls to the server's multiEditRunnable's stop method
    function stopMultiEditOperation() {

      var stopButton = document.getElementById("multiEditStopButton");
      if (stopButton) {
       stopButton.className = "button disabled";
       stopButton.onclick = function() {return false;};
       stopButton.innerHTML = "<fmt:message key='multiEditWait.stopping'/>";
     }
     <c:url var="stopUrl" value="/components/setValue.jsp">
       <c:param name="bean" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditRunnable.multiEditStopped"/>
       <c:param name="value" value="true"/>
     </c:url>
     parent.issueRequest("<c:out escapeXml='false' value='${stopUrl}'/>", null, stoppedResponse);
     stoppedIntervalVar = setInterval("stopAgain()",5000);
     return;
   }

   // update the counts on the page
  function updateMultiEditWaitBarPage(pWaiting, pRemainCount, pSuccessCount, pErrorCount, pNoChangeCount) {
    var waitLabel = document.getElementById("multiEditWaitBar");
  
    var success = document.getElementById("atg_multiedit_success");
    var unsuccessful = document.getElementById("atg_multiedit_error");
    var noChange = document.getElementById("atg_multiedit_nochange");
    if (waitLabel) {
      success.innerHTML = pSuccessCount;
      unsuccessful.innerHTML = pErrorCount;
      noChange.innerHTML = pNoChangeCount;
    } 
  }


 function handleOnLoad() {
      parent.startPingingMultiEditProgress();
  }


</script>

<body onload="handleOnLoad()" id="framePage">
  <div id="rightPaneHeader">
          <div id="rightPaneHeaderRight"></div>
          <div id="rightPaneHeaderLeft"></div>
        </div> 
<div id="subNav"></div>
<div id="rightPaneContent">
<h3 class="contentHeader">Applying Changes</h3>

<dspel:getvalueof var="percentFinished" bean="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditRunnable.multiEditPercentFinished"/>

<p><fmt:message key="multiEditWait.youCanContinue"/></p>
<div id="atg_progressBar">
  <div id="progress1" topicDriven="true" forwardOnly="true" dojoType="atg.widget.progressBase" containerWidth="350px" percentProgress="<c:out value='${percentFinished}'/>" ></div>
  <a class="button" id="multiEditStopButton" href="javascript:stopMultiEditOperation()"><fmt:message key="multiEditWait.stop"/></a>

</div>  



<div id="multiEditWaitBar">

<table class="formTable">
  <tbody>
    <tr class="atg_multiedit_successRow">
      <td class="formLabel"><fmt:message key="multiEditWait.successful"/></td>
      <td id="atg_multiedit_success"></td>
    </tr>
    <tr class="atg_multiedit_unsuccessfulRow">
      <td class="formLabel"><fmt:message key="multiEditWait.unsuccessful"/></td>
      <td id="atg_multiedit_error"></td>
    </tr>
    <tr>
      <td class="formLabel"><fmt:message key="multiEditWait.noChangeNeeded"/></td>
      <td id="atg_multiedit_nochange"></td>
    </tr>
  </tbody>
</table>

</div>
</div>
</body>
</html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/confirmationWait.jsp#2 $$Change: 651448 $--%>
