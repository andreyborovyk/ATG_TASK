<!-- BEGIN FILE action_import.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<dspel:page>

<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <dspel:importbean scope="request" var="topologyEditFormHandler" bean="/atg/epub/deployment/TopologyEditFormHandler"/>  	
 <c:set var="topologyFormHandlerName" value="/atg/epub/deployment/TopologyEditFormHandler" scope="request"/> 

<html>
<head>
 <dspel:include page='/includes/head.jsp' flush="false"/>
  <script language="Javascript" type="text/javascript">
  <!--
    if(<c:out value="${param.importFormSubmitted}">false</c:out> && !<c:out value="${topologyEditFormHandler.formError}"/>){
    parent.showIframe('importAction');
    parent.location.reload();
  }
  -->
 </script>

</head>
<body class="actionContent">

	<div id="confirmHeader"><h2>    <fmt:message key="confirmation" bundle="${depBundle}"/></h2></div>
   <dspel:include page="../../includes/formErrors.jsp"/>	
	
	<div id="actionContent">
	<h3>    <fmt:message key="import-prompt" bundle="${depBundle}"/></h3>
   <p>
    <dspel:form enctype="multipart/form-data" method="post" action="#" name="importFormFrame" id="importFormFrame" formid="importFormFrame" >
 		 <fmt:message key="file" bundle="${depBundle}"/>:
     <dspel:input type="file" bean="${topologyFormHandlerName}.uploadedDefinitionFile" accept="text/xml"/>
     <input type="hidden" name="importFormSubmitted" value="true"/>
     <dspel:input type="hidden" bean="${topologyFormHandlerName}.importIncremental" priority="-10" value="foo"/>
    </dspel:form>  
   </p>
	</div>    
	<div class="actionOK">
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td width="100%">&nbsp;</td>
	<td class="buttonImage"><a href="javascript:submitForm('importFormFrame	');" class="mainContentButton go" onmouseover="status='';return true;">    <fmt:message key="ok" bundle="${depBundle}"/></a></td>		
	<td class="buttonImage"><a href="javascript:parent.showIframe('importAction')" class="mainContentButton delete" onmouseover="status='';return true;">    <fmt:message key="cancel" bundle="${depBundle}"/></a></td>		
	<td class="blankSpace"></td>
	</tr>
	</table>
	</div>
</body>
</html>
</dspel:page>
<!-- END FILE action_import.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/iframes/action_import.jsp#2 $$Change: 651448 $--%>
