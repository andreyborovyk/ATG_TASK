<!-- BEGIN FILE site_toDo_reject.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
	<div id="adminDeployment">
		
		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
		<td class="blankSpace">&nbsp;</td>
		<td><h2><fmt:message key="reject-processes-for-dep-to-site" bundle="${depBundle}"> 
		          <fmt:param value="${target.name}"/>
				  </fmt:message></h2></td>
   	<td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
		<td class="blankSpace">&nbsp;</td>
		</tr>
		</table>
		</div>
<portlet:renderURL var="successURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="done_reject" value="true"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
<portlet:renderURL var="failureURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

		<dspel:form action="${failureURL}" method="post" id="rejectForm">		
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
		
		<tr>
		<td class="adminDeploymentLeftStyle">
	
			<table cellpadding="0" cellspacing="0" border="0" class="dataTable" id="adminDeploymentLeft" style="border: none;">
		
			<tr>
			<th class="leftAligned"><span class="tableHeader"><fmt:message key="processes-to-be-rejected" bundle="${depBundle}"/></span></th>		
			</tr>
		<c:forEach var="projectToRejectId" items="${deployRejectProjects}" varStatus="depPrStatus">	
      <pws:getProject var="projectToReject" projectId="${projectToRejectId}"/>
  			<dspel:input bean="${deploymentFormHandlerName}.rejectProjectIDs" type="hidden" value="${projectToReject.id}"/>                  
	<dspel:include page="/includes/createProjectURL.jsp">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${projectToRejectId}"/>
     </dspel:include>
  			
                     <c:choose>
			<c:when test="${(depPrStatus.count % 2) == 0}"> 
  			<tr class="alternateRowHighlight">

			<td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;"><c:out value="${projectToReject.displayName}"/><//a></span></td>	
                        </c:when>
			<c:otherwise> 
                         <tr>
			<td class="leftAligned"><span class="tableInfo"><a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;"><c:out value="${projectToReject.displayName}"/></a></span></td>	
                        </c:otherwise>
		      </c:choose>
			
			</tr>

      </c:forEach>
		
			
			</table>
		<td id="adminToDoRight">		
		 <table border="0" cellpadding="0" cellspacing="0" width="100%" class="leftAligned">
		  <tr>
		   <td class="rightAligned" style="width: 30%">
	      	<p><fmt:message key="include-note" bundle="${depBundle}"/>:</p>
      		<p><dspel:textarea bean="${deploymentFormHandlerName}.note" cols="40" rows="8"></dspel:textarea></p>
	        </td>
	       </tr>		
		</td>
		</tr>
			<tr>
			<td height="20" colspan="2">&nbsp;</td>
			</tr>
			</table>
		</td>
		</tr>
		</table>
<dspel:input bean="${deploymentFormHandlerName}.rejectTargetId" type="hidden" value="${target.ID}"/>
<dspel:input bean="${deploymentFormHandlerName}.rejectProjects" type="hidden" priority="-10" value="foo"/>
<dspel:input bean="${deploymentFormHandlerName}.successURL" type="hidden" value="${successURL}"/>			
<dspel:input bean="${deploymentFormHandlerName}.failureURL" type="hidden" value="${failureURL}"/>	
</dspel:form>
<portlet:renderURL var="toDoListURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="done_deploy" value="todo"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

		<div class="contentActions">
		
		<table cellpadding="0" cellspacing="0" border="0">
		<tr>
		<td class="blankSpace" width="100%">&nbsp;</td>
		<td class="buttonImage"><a href="javascript:submitForm('rejectForm');" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="reject" bundle="${depBundle}"/></a></td>
		<td class="buttonImage"><a href="<c:out value="${toDoListURL}"/>" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel-reject" bundle="${depBundle}"/></a></td>
		<td class="blankSpace">&nbsp;</td>
		</tr>
		
		</table>

		</div>

		</div>
	</td>
	</tr>
	</table>	
	<!-- end content -->
		
	</div>

</dspel:page>
<!-- END FILE site_toDo_reject.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_toDo_reject.jsp#2 $$Change: 651448 $--%>
