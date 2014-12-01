<%--
  stepButtons.jsp
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:url var="nextAssetURL" value="/multiEdit/nextStep.jsp"/>

  <c:url var="prevAssetURL" value="/multiEdit/prevStep.jsp"/>

  <span id="prevbuttons" style="display:inline">
  <a href='javascript:parent.editPrevAsset()'
     class="button" title="<fmt:message key='assetEditor.multiEdit.prev.title'/>">
    <span><fmt:message key="assetEditor.multiEdit.prev"/></span> 
  </a>

  <a href='javascript:saveNextPrevButtonClicked("<c:out value="${prevAssetURL}"/>")'
     class="button" title="<fmt:message key='assetEditor.multiEdit.savePrev.title'/>" style="display:none" id="saveprev">
    <span><fmt:message key="assetEditor.multiEdit.savePrev"/></span> 
  </a>
  </span>

  <a href="javascript:actionButtonClicked('saveButton')"
     class="button" title="<fmt:message key='common.save.title'/>" >
    <span><fmt:message key="common.save"/></span> 
  </a>

  <span id="nextbuttons" style="display:inline">
  <a href='javascript:saveNextPrevButtonClicked("<c:out value="${nextAssetURL}"/>")'
     class="button" title="<fmt:message key='assetEditor.multiEdit.saveNext.title'/>" style="display:none" id="savenext">
    <span><fmt:message key="assetEditor.multiEdit.saveNext"/></span> 
  </a>

  <a href='javascript:parent.editNextAsset()'
     class="button" title="<fmt:message key='common.next.title'/>" style="display:none" id="next">
    <span><fmt:message key="common.next"/></span> 
  </a>
  </span>

  <fmt:message var="exitTitle" key="assetEditor.multiEdit.exit"/>
  <a href="javascript:parent.saveBeforeExitMultiEdit()" 
      class="button" title="<c:out value='${exitTitle}'/>"><span><fmt:message key="assetEditor.multiEdit.exit"/></span></a> 

  <script type="text/javascript">

    function showNextPrevButtons() {
      if (!parent.nextAssetExists())
        disableButtons("nextbuttons");
      if (!parent.prevAssetExists())
        disableButtons("prevbuttons");
    }

    function disableButtons(buttonContainerId) {
      var showButton = document.getElementById(buttonContainerId);
      if (showButton != null) { 
        var buttons = showButton.getElementsByTagName("a");
        for (var ii=0; ii<buttons.length; ii++) {
          var button = buttons[ii];
          button.className="button disabled";
          button.onclick = function() { return false; };
        }
      }
    }

    showNextPrevButtons();

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/stepButtons.jsp#2 $$Change: 651448 $ --%>
