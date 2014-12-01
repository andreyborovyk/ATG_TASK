<%--
  listEditButtons.jsp
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

  <c:set var="listEditFormHandlerPath" value="/atg/web/assetmanager/multiedit/ListEditFormHandler" scope="request"/>  

  <%-- Import list edit --%>
  <dspel:importbean var="listEditFormHandler" bean="${listEditFormHandlerPath}"/> 

  <c:set target="${listEditFormHandler}" property="singleFormHandler" value="${requestScope.formHandler}"/>

  <%--
    Set the success URL for the form handler 
  --%>
  <c:url var="confirmationURL" value="/multiEdit/confirmationPassThru.jsp?op=${OPERATION_LIST_EDIT}"/>
  <dspel:input type="hidden" bean="${listEditFormHandlerPath}.listEditSuccessURL" 
             value="${confirmationURL}" id="listEditSuccessURLField"/>


  <%--
    Set the error URL for the form handler 
  --%>
  <c:url var="listEditErrorURL" value="${paramActionURL}"/>
  <dspel:input type="hidden" bean="${listEditFormHandlerPath}.listEditErrorURL" 
             value="${listEditErrorURL}"/>

  <script type="text/javascript">
 

    function listEditClicked() {
      actionButtonClicked("listEditButton");
    }
  </script>

  <%--
    listEdit button
  --%>
  <dspel:input type="submit"  style="display:none" id="listEditButton" 
               bean="${listEditFormHandlerPath}.listEditItems" value="List Edit"/>

  <fmt:message var="listEditTitle" key="assetEditor.le.listEdit"/>
  <dspel:a href="javascript:listEditClicked()"
     iclass="button" title="${listEditTitle}">
    <span><fmt:message key="assetEditor.le.listEdit"/></span> 
  </dspel:a>

  <fmt:message var="exitTitle" key="assetEditor.multiEdit.exit"/>
  <dspel:a href="javascript:parent.saveBeforeExitMultiEdit()" 
     iclass="button" title="${exitTitle}"><span><fmt:message key="assetEditor.multiEdit.exit"/></span></dspel:a> 

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/listEditButtons.jsp#2 $$Change: 651448 $ --%>
