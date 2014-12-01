<!-- BEGIN FILE site_agents_switchables.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>

<portlet:renderURL var="agentOverviewURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="goto_details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="agents"/>
  <portlet:param name="agent_tablet_name" value="overview"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
<portlet:renderURL var="agentSwitchablesURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="goto_details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="agents"/>
  <portlet:param name="agent_tablet_name" value="switchables"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

<div class="contentActionsAgents">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">
        &nbsp;
      </td>
      <td>
        <h2> <fmt:message key="viewing-agents-site" bundle="${depBundle}"> 
             <fmt:param value="${target.name}"/>
             </fmt:message>
        </h2>
        <br><br>
        <h2> 
             <a href="<c:out value='${agentOverviewURL}'/>"><fmt:message key="agents-overview" bundle="${depBundle}"/></a> | <fmt:message key="agent-switchables" bundle="${depBundle}"/>
        </h2>
      </td>
      <td width="100%" class="error rightAlign">
        <dspel:include page="../includes/formErrors.jsp"/>
      </td>   
    </tr>
  </table>
</div>
    
<div id="adminDeployment">
  <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
    <tr>
      <th class="leftAligned" width="40%">
        <span class="tableHeader"><fmt:message key="agent-name" bundle="${depBundle}"/></span>
      </th>
      <th class="leftAligned">
        &nbsp;<span class="tableHeader"><img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt="select multiple column" width="7" height="8" border="0" />&nbsp;&nbsp;<fmt:message key="switchable-datastore-name" bundle="${depBundle}"/></span>
      </th>
      <th class="leftAligned">
        <span class="tableHeader"><fmt:message key="live-datastore-name" bundle="${depBundle}"/></span>
      </th>    
    </tr>

   <pws:getAgentSwitchables var="agents" targetId="${target.ID}" />
    
   <dspel:form method="post" action="${agentSwitchablesURL}" id="agentSwitchForm" formid="agentSwitchForm" name="agentSwitchForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${agentSwitchablesURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${agentSwitchablesURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.switchTargetID" value="${target.ID}" />
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.switchAgentDatastores" value="1" priority="-10"/>
     
     <c:choose>
       <c:when test="${empty agents}">
         <tr>
           <td class="leftAligned">
             <span class="tableInfo">
               <fmt:message key="no-switchable-agents-available" bundle="${depBundle}"/>
             </span>
           </td>
           <td class="centerAligned"></td>
           <td class="centerAligned"></td>
           <td class="centerAligned"></td>
         </tr>
       </c:when>
       <c:otherwise>
         <c:forEach var="agent" items="${agents}" varStatus="aSt"> 
           <c:forEach var="switchable" items="${agent.switchables}" varStatus="sSt">

             <c:choose>
               <c:when test="${aSt.count % 2 == 0}">
                 <tr class="alternateRowHighlight">
               </c:when>
               <c:otherwise>
                 <tr>
               </c:otherwise>
             </c:choose>
           
               <%-- COLUMN 1 --%>
               <td class="leftAligned noBorder">
                 <span class="tableInfo">
                   <c:if test="${sSt.first}">
                     <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" style="margin-right: 6px; vertical-align: middle;" width="16" height="16" border="0" />
                     <c:out value="${agent.agentName}"/>
                   </c:if>
                 </span>
               </td>  

               <%-- COLUMN 2 --%>
               <td class="leftAligned noBorder">
                 <span class="tableInfo">
                     <dspel:input bean="${deploymentFormHandlerName}.agentIDs" 
                                  type="checkbox" 
                                  iclass="checkbox" 
                                  value="${agent.agentID}${deploymentFormHandler.agentDelimiter}${switchable.name}"/>&nbsp;&nbsp;
                     <c:out value="${switchable.name}"/>
                 </span>
               </td>

               <%-- COLUMN 3 --%>
               <td class="leftAligned noBorder">
                 <span class="tableInfo">
                   <c:out value="${switchable.liveDatastore}"/>
                 </span>
               </td>

             </tr>
           </c:forEach>
         </c:forEach>
       </c:otherwise>
     </c:choose>
   </dspel:form>

  </table>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>  
        <td class="blankSpace" width="100%">&nbsp;</td>
        <c:choose>       
          <c:when test="${empty agents}">
            <td>
              <a href="javascript:showIframe('switchAgentAction')" class="mainContentButton go" onmouseover="status='';return true;" onclick="return false;"><fmt:message key="switch-selected-datastores" bundle="${depBundle}"/></a>
            </td>
          </c:when>
          <c:otherwise>
            <td class="buttonImage">
              <a href="javascript:showIframe('switchAgentAction')" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="switch-selected-datastores" bundle="${depBundle}"/></a>
            </td>
          </c:otherwise>
        </c:choose>
        <td class="blankSpace"></td>
     </tr>
    </table>
  </div>
  
</div>  
    
</td>
</tr>

</table>
    
<!-- end content -->
<script language="Javascript" type="text/javascript">
  <!--
  registerOnLoad(function() {init('switchAgentAction');})
   -->
</script>
<div id="switchAgentAction" class="confirmAction">
 <dspel:iframe page="iframes/action_switch_agent_datasources.jsp" scrolling="no"/>
</div>
</dspel:page>

<!-- END FILE site_agents_switchables.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_agents_switchables.jsp#2 $$Change: 651448 $--%>
