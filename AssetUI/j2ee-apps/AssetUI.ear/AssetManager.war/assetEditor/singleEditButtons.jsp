<%--
  singleEditButtons.jsp
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramTransient" param="transient"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Get the current left tab --%>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  
  <%-- Get the assetEditor for the current left tab --%>
  <c:if test="${not empty currentView}">
    <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
    <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
  </c:if>

  <script type="text/javascript">

    registerOnModified(function () {
      var saveButton = document.getElementById("saveButtonLink");
      var saveAndCloseButton = document.getElementById("saveAndCloseButtonLink");
      var revertButton = document.getElementById("revertButtonLink");
      if (saveButton != null) {
        saveButton.onclick = function() { return true; };
        saveButton.title = "<fmt:message key='common.save.title'/>";
        saveButton.className = "button";
      } 
      if (saveAndCloseButton != null) {
        saveAndCloseButton.onclick = function() { return true; };
        saveAndCloseButton.title = "<fmt:message key='assetEditor.saveAndClose.title'/>";
        saveAndCloseButton.className = "button";
      } 
      if (revertButton != null ) {
        revertButton.onclick = function() { return true; };
        revertButton.title = "<fmt:message key='assetEditor.revert.title'/>";
        revertButton.className = "button";
      }
    });

    function confirmRevert() {
      if (confirm("<fmt:message key='assetEditor.confirm.revert'/>")) { 
        document.getElementById('cancelButton').click(); 
     }
   }

  </script>
  <c:choose>

    <c:when test="${paramTransient}">
      <a href="javascript:actionButtonClicked('addButton')"
         class="button" 
         title="<fmt:message key='assetEditor.create.title'/>"><span><fmt:message 
            key="assetEditor.create"/></span></a>
      &nbsp; 
      <c:if test="${assetEditor.size > 1}">
        <a href="javascript:saveAndClose()" id="createAndCloseButtonLink"
           class="button" title="<fmt:message 
           key='assetEditor.createAndClose.title'/>" ><span><fmt:message 
           key="assetEditor.createAndClose"/></span></a>
        &nbsp;
      </c:if>
      <a href="javascript:cancelCreate()" 
         class="button" 
         title="<fmt:message key='common.cancel.title'/>"><span><fmt:message 
         key="common.cancel"/></span></a> 
    </c:when>

    <c:when test="${requestScope.atgItemViewMapping.attributes.saveAlwaysEnabled == true}">
      <a href="javascript:actionButtonClicked('saveButton')" id="saveButtonLink"
         class="button"
         title="<fmt:message key='common.save.title'/>" ><span><fmt:message 
         key="common.save"/></span></a>
      &nbsp;
      <c:if test="${assetEditor.size > 1}">
        <a href="javascript:saveAndClose()" id="saveAndCloseButtonLink"
           class="button"
           title="<fmt:message key='assetEditor.saveAndClose.title'/>" ><span><fmt:message 
           key="assetEditor.saveAndClose"/></span></a>
        &nbsp;
      </c:if>
      <a href="javascript:confirmRevert()" id="revertButtonLink"
         class="button" title="<fmt:message 
         key='assetEditor.revert.title'/>"><span><fmt:message 
         key="assetEditor.revert"/></span></a> 
      <c:if test="${assetEditor.size > 1}">
        &nbsp;
        <a href="javascript:popContextBreadcrumb(true)" id="closeButtonLink"
           class="button"
           title="<fmt:message key='assetEditor.close.title'/>" ><span><fmt:message 
           key="assetEditor.close"/></span></a>
      </c:if>
    </c:when>

    <c:otherwise>
      <a href="javascript:actionButtonClicked('saveButton')" id="saveButtonLink"
         class="button disabled" onclick="return false" 
         title="<fmt:message key='assetEditor.nothingToSave.title'/>" ><span><fmt:message 
         key="common.save"/></span></a>
      &nbsp;
      <c:if test="${assetEditor.size > 1}">
        <a href="javascript:saveAndClose()" id="saveAndCloseButtonLink"
           class="button disabled" onclick="return false" 
           title="<fmt:message key='assetEditor.nothingToSave.title'/>" ><span><fmt:message 
           key="assetEditor.saveAndClose"/></span></a>
        &nbsp;
      </c:if>
      <a href="javascript:confirmRevert()" id="revertButtonLink"
         class="button disabled" onclick="return false" title="<fmt:message 
         key='assetEditor.nothingToRevert.title'/>"><span><fmt:message 
         key="assetEditor.revert"/></span></a> 
      <c:if test="${assetEditor.size > 1}">
        &nbsp;
        <a href="javascript:popContextBreadcrumb(true)" id="closeButtonLink"
           class="button"
           title="<fmt:message key='assetEditor.close.title'/>" ><span><fmt:message 
           key="assetEditor.close"/></span></a>
      </c:if>
    </c:otherwise>
               
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/singleEditButtons.jsp#2 $$Change: 651448 $ --%>
