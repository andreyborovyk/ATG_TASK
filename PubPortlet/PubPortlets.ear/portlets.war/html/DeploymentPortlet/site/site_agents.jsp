<!-- BEGIN FILE site_agents.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>

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
		<td class="blankSpace">&nbsp;</td>
		<td><h2><fmt:message key="viewing-agents-site" bundle="${depBundle}"> 
		          <fmt:param value="${target.name}"/>
				</fmt:message>
        </h2>
        <br><br>
        <h2> 
              <fmt:message key="agents-overview" bundle="${depBundle}"/> | <a href="<c:out value='${agentSwitchablesURL}'/>"><fmt:message key="agent-switchables" bundle="${depBundle}"/></a>
        </h2>           
     </td>
   	<td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
		</tr>
		</table>
		</div>
    
		<div id="adminDeployment">
		
		<table cellpadding="0" cellspacing="0" border="0" class="dataTable">
		
		<tr>
		<th class="leftAligned" width="60%"><span class="tableHeader"><fmt:message key="agent-name" bundle="${depBundle}"/></span></th>		
		<th class="centerAligned"><span class="tableHeader"><fmt:message key="agent-status" bundle="${depBundle}"/></span></th>
		<th class="centerAligned"><span class="tableHeader"><fmt:message key="responsible" bundle="${depBundle}"/></span></th>		
		<th class="centerAligned"><span class="tableHeader"><fmt:message key="switchable" bundle="${depBundle}"/></span></th>
                <c:if test="${!target.oneOff}">                
                <th class="rightAligned" width="4%"><span class="tableHeader"><fmt:message key="installed-snapshot" bundle="${depBundle}"/></span></th>		
                </c:if>
            </tr>
	<c:forEach var="agent" items="${target.agents}" varStatus="st">	
	<c:choose>
	<c:when test="${st.count % 2 == 0}">
	  <tr class="alternateRowHighlight">
	</c:when>
	<c:otherwise>
	<tr>
	</c:otherwise>
	</c:choose>

		<td class="leftAligned"><span class="tableInfo">
<img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" style="margin-right: 6px; vertical-align: middle;" width="16" height="16" border="0" />
    <c:out value="${agent.name}"/></span></td>	
		<td class="centerAligned"><span class="tableInfo">
    <c:if test="${agent.status.stateError}">
     <span class="error"> <c:out value="${agent.status.userStateString}"/></span>
    </c:if>
     <c:if test="${!agent.status.stateError}">
     <c:out value="${agent.status.userStateString}"/>
    </c:if>
   
    </span></td>
		<td class="centerAligned"><span class="tableInfo">
		<%pageContext.setAttribute("hasReposibilities",new Boolean(((atg.deployment.server.AgentRef)pageContext.findAttribute("agent")).hasDeploymentResponsibilities()));%>
		<c:if test="${hasReposibilities}">
		<fmt:message key="yes" bundle="${depBundle}"/>
		</c:if>
     	<c:if test="${!hasReposibilities}">
     	<fmt:message key="no" bundle="${depBundle}"/>
		</c:if>		
		</span></td>		
		<td class="centerAligned"><span class="tableInfo">
		
		<c:if test="${agent.status.switchable}">
		<fmt:message key="yes" bundle="${depBundle}"/>
		</c:if>
		<c:if test="${!agent.status.switchable}">
		<fmt:message key="no" bundle="${depBundle}"/>
		</c:if>
		
		</span></td>
                <c:if test="${!target.oneOff}">                
		<td class="rightAligned"><span class="tableInfo"><c:out value="${agent.status.deployedSnapshot}"/></span></td>
                </c:if>
		</tr>
	</c:forEach>		
		</table>

		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0">
		<tr>	
		<td class="blankSpace" width="100%">&nbsp;</td>
		</tr>
		</table>
		</div>
	
		</div>	
		
	</td>
	</tr>
	
	</table>
		
	<!-- end content -->
		
</dspel:page>

<!-- END FILE site_agents.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_agents.jsp#2 $$Change: 651448 $--%>
