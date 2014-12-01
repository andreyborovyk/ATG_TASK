<%--
  confirmButtons.jsp
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramAssetURI" param="assetURI"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="confirmFormHandlerPath" value="/atg/web/assetmanager/multiedit/MultiEditConfirmFormHandler" scope="request"/>
  <%-- Import confirm to all --%>
  <dspel:importbean var="confirmFormHandler" bean="${confirmFormHandlerPath}"/> 


  <script type="text/javascript">


    registerOnModified(function () {
      var saveButton = document.getElementById("saveConfirmButtonLink");
      var saveAndCloseButton = document.getElementById("saveAndCloseConfirmButtonLink");
      var revertButton = document.getElementById("revertConfirmButtonLink");
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
        document.getElementById('confirmCancelButton').click(); 
       }
     }

     function confirmSaveAndClose() {
       var updateSuccessURL = document.getElementById("updateSuccessURLField");
       if (updateSuccessURL != null) {
         updateSuccessURL.value = updateSuccessURL.value + "&finishSaveConfirmFunction=" + "showConfirmationPage";
       }
       actionButtonClicked('confirmButton');
     }

   </script>

  <c:set target="${confirmFormHandler}" property="singleFormHandler" value="${requestScope.formHandler}"/>
  <dspel:input type="hidden" bean="${confirmFormHandlerPath}.assetURI" value="${paramAssetURI}"/>

  <dspel:input type="submit"  style="display:none" id="confirmButton" 
               bean="${confirmFormHandlerPath}.confirmUpdateItem" value="confirm"/>

  <dspel:input type="submit"  style="display:none" id="confirmCancelButton" 
               bean="${confirmFormHandlerPath}.cancel" value="cancel"/>

  <c:choose>
    <c:when test="${requestScope.atgItemViewMapping.attributes.saveAlwaysEnabled == true}">
      <a href="javascript:actionButtonClicked('confirmButton')" id="saveConfirmButtonLink"
         class="button" title="<fmt:message key='common.save.title'/>" >
        <span><fmt:message key="common.save"/></span> 
      </a>
      &nbsp;
      <a href="javascript:confirmSaveAndClose()" id="saveAndCloseConfirmButtonLink"
         class="button" title="<fmt:message key='assetEditor.saveAndClose.title'/>" >
         <span><fmt:message key="assetEditor.saveAndClose"/></span> 
      </a>
      &nbsp;
    </c:when>

    <c:otherwise>
      <a href="javascript:actionButtonClicked('confirmButton')" id="saveConfirmButtonLink"
         class="button disabled" onclick="return false" title="<fmt:message key='assetEditor.nothingToSave.title'/>" >
        <span><fmt:message key="common.save"/></span> 
      </a>
      &nbsp;
      <a href="javascript:confirmSaveAndClose()" id="saveAndCloseConfirmButtonLink"
         class="button disabled" onclick="return false" title="<fmt:message key='assetEditor.nothingToSave.title'/>" >
         <span><fmt:message key="assetEditor.saveAndClose"/></span> 
      </a>
      &nbsp;
    </c:otherwise>

  </c:choose>

  <c:choose>
    <c:when test="${not empty requestScope.formHandler.formExceptions}">
      <a href="javascript:confirmRevert()" id="revertConfirmButtonLink"
          class="button" title="<fmt:message 
          key='assetEditor.revert.title'/>"><span><fmt:message 
          key="assetEditor.revert"/></span>
      </a> 
    </c:when>
    <c:otherwise>
      <a href="javascript:confirmRevert()" id="revertConfirmButtonLink"
          class="button disabled" onclick="return false" title="<fmt:message 
          key='assetEditor.nothingToRevert.title'/>"><span><fmt:message 
          key="assetEditor.revert"/></span>
      </a> 
    </c:otherwise>
  </c:choose>

  &nbsp;
  <a href="javascript:parent.showConfirmationPage()" id="closeButtonLink"
           class="button"
           title="<fmt:message key='assetEditor.multiEdit.closeMultiEdit.title'/>" ><span><fmt:message 
           key="assetEditor.close"/></span></a>

  <%-- <fmt:message var="exitTitle" key="assetEditor.multiEdit.exit"/>
  <dspel:a href="javascript:parent.changeMode('Single','null','${requestScope.multiEditPropertyGroupIndex}');document.getElementById('cancelButton').click();" 
     iclass="button" title="${exitTitle}"><span><fmt:message key="assetEditor.multiEdit.exit"/></span></dspel:a>  --%>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/confirmButtons.jsp#2 $$Change: 651448 $ --%>
