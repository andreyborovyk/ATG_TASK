<!-- BEGIN FILE action_init_site.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<dspel:page>

<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<html>
<head>
 <script language="Javascript" type="text/javascript">
 <!--
   function submitParentInitForm(){
     var initOption = parent.document.getElementById("flagAgentInitOption");
     var initOptionGiven = document.getElementById("flagAgents1");
     initOption.value = initOptionGiven.checked;
     parent.submitForm('initSiteForm');
     parent.showIframe('initSiteAction');
   }
  -->
 </script>

<dspel:include page='/includes/head.jsp' flush="false"/>
</head>
<body class="actionContent">
	<div id="confirmHeader"><h2>    <fmt:message key="confirmation" bundle="${depBundle}"/></h2></div>
	<div id="actionContent">
	<h3>    <fmt:message key="init-site-prompt" bundle="${depBundle}"/></h3>

	<form action="#" method="post" id="initDummyActionForm" name="initDummyActionForm">
    <p><input type="radio" id="flagAgents1" name="flagAgents" value="true"/>
    <label for="flagAgents1">
    <fmt:message key="flag-agents" bundle="${depBundle}"/></label><br/>
    <input type="radio" id="flagAgents2" name="flagAgents" value="false"/>
    <label for="flagAgents2"><fmt:message key="no-flag-agents" bundle="${depBundle}"/></label> </p>
  </form>
	</div>  

	<div class="actionOK">
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td width="100%">&nbsp;</td>
	<td class="buttonImage"><a href="javascript:submitParentInitForm();" class="mainContentButton go" onmouseover="status='';return true;">    <fmt:message key="ok" bundle="${depBundle}"/></a></td>		
	<td class="buttonImage"><a href="javascript:parent.showIframe('initSiteAction')" class="mainContentButton delete" onmouseover="status='';return true;">    <fmt:message key="cancel" bundle="${depBundle}"/></a></td>		
	<td class="blankSpace"></td>
	</tr>
	</table>
	
  </div>
</body>
</html>
</dspel:page>
<!-- END FILE action_init_site.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/iframes/action_init_site.jsp#2 $$Change: 651448 $--%>
