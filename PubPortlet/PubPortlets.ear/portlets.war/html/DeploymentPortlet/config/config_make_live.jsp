<!-- BEGIN FILE config_make_live.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
	<!-- begin content -->
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	
	<div id="adminDeployment">
    <p>     <fmt:message key="flagging-intro-text" bundle="${depBundle}"/></p>
    <br/>
  </div>
	<tr>
	<td width="100%">
	
		<table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBar">
		<tr>
		<td>
		
			<div class="attributes">
			<br /><br />
			</div>
			
			<div class="attributeExtras">
			<br /><br />
			</div>

		</td>
		</tr>
		</table>
		
		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
		<td class="blankSpace">&nbsp;</td>
		<td><h2>        <fmt:message key="new-site-initialization-options-header" bundle="${depBundle}"/></h2></td>
               	 <portlet:renderURL var="sitesURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="from_menu" value="true"/>  
                    <portlet:param name="topology_changed" value="true"/>  
                 </portlet:renderURL> 
               	 <portlet:renderURL var="thisPageURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="make_changes_live" value="true"/>  
                 </portlet:renderURL> 
   	<td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
	</tr>
		</table>
		</div>
			
		<div id="adminDeployment">
	<dspel:form action="${thisPageURL}" method="post" id="makeChangesLiveForm">	
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="dataTable">
    <tr><td colspan="2">
    </div>

     </td>
     </tr>
          
    <pws:getTargetDefs var="allTargets" topologyType="both"/>
    <tr>
    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="dataTable">
    
      <c:forEach var="targetDefinition" items="${allTargets}" varStatus="loopStatus">
   <c:if test="${empty targetDefinition.surrogateFor}">   
<tr>
  	 <th colspan="2" class="leftAligned"><span class="tableHeader">
         <fmt:message key="init-options-for-site" bundle="${depBundle}">
           <fmt:param value="${targetDefinition.displayName}"/>
          </fmt:message>
</th>
     </tr>
    <tr>
    	 <td class="leftAlign">
    	   &nbsp;
    	 </td>

  	 <td class="leftAlign " >    	 <span class="tableInfo">
            <dspel:input type="radio" bean="${topologyFormHandlerName}.surrogateTargetIDToInitOptionMap.${targetDefinition.ID}" checked="${targetDefinition.flagAgents}" value="true"/>
             :<fmt:message key="flag-agents" bundle="${depBundle}"/>
          </td>
    	 </span>          
    </tr>
    <tr>
    	 <td class="leftAlign">
    	   &nbsp;
    	 </td>

  	 <td class="leftAlign">    	 <span class="tableInfo">    
            <dspel:input type="radio" bean="${topologyFormHandlerName}.surrogateTargetIDToInitOptionMap.${targetDefinition.ID}" checked="${!targetDefinition.flagAgents}" value="false"/>
            :<fmt:message key="no-flag-agents" bundle="${depBundle}"/>
    	 </td>
    	 </span>

    </tr>

    <c:set var="def_id" value="${targetDefinition.ID}"/>
                  <portlet:renderURL var="editSiteURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("def_id")+""%>'/>  
                    <portlet:param name="goto_config_details_tabs" value="true"/>  
                    <portlet:param name="from_make_changes_live" value="true"/>  
                 </portlet:renderURL> 
 
     </c:if>			
  </c:forEach>
  </table>
	</tr>

		</table>
    
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${sitesURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${thisPageURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.initializeTopology"  priority="-10" value="foo"/>
		</dspel:form>
		</div>

		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0">
		<tr>
		<td class="blankSpace" width="100%">&nbsp;</td>
				
		<td class="buttonImage"><a href="javascript:submitForm('makeChangesLiveForm');" class="mainContentButton save" onmouseover="status='';return true;">        <fmt:message key="make-changes-live" bundle="${depBundle}"/></a></td><td class="buttonImage"><a href="<c:out value='${sitesURL}'/>" class="mainContentButton delete" onmouseover="status='';return true;">        <fmt:message key="cancel" bundle="${depBundle}"/></a></td>		
		<td class="blankSpace"></td>
		</tr>
		</table>
		</div>
		
		</td>
		</tr>
		</table>
	<!-- end content -->

</dspel:page>			
<!-- END FILE config_make_live.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_make_live.jsp#2 $$Change: 651448 $--%>
