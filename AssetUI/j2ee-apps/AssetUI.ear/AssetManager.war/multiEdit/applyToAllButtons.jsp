<%--
  appplyToAllButtons.jsp
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramActionURL" param="actionURL"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <c:set var="applyFormHandlerPath" value="/atg/web/assetmanager/multiedit/ApplyToAllFormHandler" scope="request"/>  

  <%-- Import apply to all --%>
  <dspel:importbean var="applyFormHandler" bean="${applyFormHandlerPath}"/> 

  <c:set target="${applyFormHandler}" property="singleFormHandler" value="${requestScope.formHandler}"/>

  <%--
    Set the success URL for the form handler 
  --%>
  <c:url var="confirmationURL" value="/multiEdit/confirmationPassThru.jsp?op=${OPERATION_APPLY_TO_ALL}"/>
  <dspel:input type="hidden" bean="${applyFormHandlerPath}.applyToAllSuccessURL" 
             value="${confirmationURL}" id="applyToAllSuccessURLField"/>


  <%--
    Set the error URL for the form handler 
  --%>
  <c:url var="applyAllErrorURL" value="${paramActionURL}"/>
  <dspel:input type="hidden" bean="${applyFormHandlerPath}.applyToAllErrorURL" 
             value="${applyAllErrorURL}"/>


  <%--
    Set the wait URL for the form handler 
  --%>
  <c:url var="confirmationWaitURL" value="/multiEdit/confirmationWait.jsp?op=${OPERATION_APPLY_TO_ALL}"/>
  <dspel:input type="hidden" bean="${applyFormHandlerPath}.applyToAllWaitURL" 
             value="${confirmationWaitURL}" id="applyToAllWaitURLField"/>



  <script type="text/javascript">

    function applyToAllClicked() {
      parent.setMultiEditWait(true);
      actionButtonClicked("applyToAllButton");
    }

    registerOnLoad(function() {parent.setMultiEditWait(false);});

  </script>

  <%--
    Apply To All button
  --%>
  <dspel:input type="submit"  style="display:none" id="applyToAllButton" 
               bean="${applyFormHandlerPath}.applyToAllItems" value="Apply To All"/>

  <fmt:message var="applyToAllTitle" key="assetEditor.ata.applyToAll"/>
  <dspel:a href="javascript:applyToAllClicked()"
     iclass="button" title="${applyToAllTitle}">
    <span><fmt:message key="assetEditor.ata.applyToAll"/></span> 
  </dspel:a>

  <fmt:message var="exitTitle" key="assetEditor.multiEdit.exit"/>
  <dspel:a href="javascript:parent.saveBeforeExitMultiEdit();" 
     iclass="button" title="${exitTitle}"><span><fmt:message key="assetEditor.multiEdit.exit"/></span></dspel:a> 

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/applyToAllButtons.jsp#2 $$Change: 651448 $ --%>
