<!-- BEGIN FILE site_plan.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<%
  atg.security.User user = atg.security.ThreadSecurityManager.currentUser();
%>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
 <script language="Javascript" type="text/javascript">
 <!--
   function checkBoxChecked(){
     var cancelForm = document.getElementById("cancelDeploymentsForm");
      for(j=0; j < cancelForm.elements.length; j++){
        var formObj=cancelForm.elements[j];
        if(formObj.name == "cancel_deployment_ids"){
          if(formObj.checked) 
           return true;
        }
      }
     return false;
   }
  -->
 </script>

<c:set var="isTargetOneOff" value="${target.oneOff}"/>

<c:choose>
<c:when test="${!isTargetOneOff}">                 
  <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBar">
    <tr>
      <td>
      <div class="attributeExtras">
        <form method="post" action="#">  
        <p/><fmt:message key="show-deployment-type" bundle="${depBundle}"/>:
        <select id="blah" onchange="document.location.href=this.options[this.selectedIndex].value; return true;" name="property">
          <c:forEach var="type" items="All,Scheduled,Queued"> 
            <portlet:renderURL var="valueURL">
              <portlet:param name="atg_admin_menu_group" value="deployment"/>
              <portlet:param name="atg_admin_menu_1" value="overview"/>
              <portlet:param name="details_tabs" value="true"/>
              <portlet:param name="site_tab_name" value="plan"/>
              <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
              <portlet:param name="scheduled_only" value='<%=(pageContext.findAttribute("type")+"").equals("Scheduled")+""%>'/>
              <portlet:param name="queued_only" value='<%=(pageContext.findAttribute("type")+"").equals("Queued")+""%>'/>
              <portlet:param name="plan_show_all" value='<%=(pageContext.findAttribute("type")+"").equals("All")+""%>'/>
              <portlet:param name="plan_start_index" value="0"/>
            </portlet:renderURL>
            <fmt:message var="type_i18n" key="type-${type}" bundle="${depBundle}"/>
            <c:choose>
              <c:when test="${(scheduledOnly && type eq 'Scheduled') || (queuedOnly && type eq 'Queued')}">
                <option selected="selected" value ="<c:out value='${valueURL}'/>" label="<c:out value='${type_i18n}'/>"><c:out value="${type_i18n}"/></option>
              </c:when>
              <c:otherwise>
                <option value ="<c:out value='${valueURL}'/>" label="<c:out value='${type_18n}'/>"><c:out value="${type_i18n}"/></option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
        </form>
      </div>		
      </td>
    </tr>
  </table>
</c:when>
<c:otherwise>
  <portlet:renderURL var="valueURL">
    <portlet:param name="atg_admin_menu_group" value="deployment"/>
    <portlet:param name="atg_admin_menu_1" value="overview"/>
    <portlet:param name="details_tabs" value="true"/>
    <portlet:param name="site_tab_name" value="plan"/>
    <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
    <portlet:param name="scheduled_only" value='false'/>
    <portlet:param name="queued_only" value='true'/>
    <portlet:param name="plan_show_all" value='false'/>
    <portlet:param name="plan_start_index" value="0"/>
  </portlet:renderURL>
  <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBar">
    <tr>
      <td>
        <div class="attributes">
          <p>&nbsp;</p>
        </div>
        <div class="attributeExtras">
          <p>&nbsp;</p>
        </div>			
      </td>
    </tr>
  </table>
</c:otherwise>   
</c:choose>

<div class="contentActions">
 <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
   <td class="blankSpace">&nbsp;</td>
   <td><h2><fmt:message key="site-plan-for-site" bundle="${depBundle}"/>: <c:out value="${target.name}"/></h2></td>
   <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
  </tr>
 </table>
</div>
			
<div id="adminDeployment">

	<table cellpadding="0" cellspacing="0" border="0" class="dataTable">
		
 	<tr>
  <th class="centerAligned" style="width: 4%;">
    <input type="checkbox" id="checkAllField" title="<fmt:message key='select-or-deselect' bundle='${depBundle}'/>" class="checkbox" onClick="checkAll('cancelDeploymentsForm','cancel_deployment_ids');"/>
  </th>
	 <th class="leftAligned" style="width: 30%;"><span class="tableHeader"><fmt:message key="processes" bundle="${depBundle}"/></span></th>
	 <th class="centerAligned" style="width: 20%;" colspan="2"><span class="tableHeader"><fmt:message key="deploy-date-time" bundle="${depBundle}"/></span></th>
	 <th class="centerAligned"><span class="tableHeader"><fmt:message key="status" bundle="${depBundle}"/></span></th>
	 <th class="centerAligned nowrap"><span class="tableHeader"><fmt:message key="action" bundle="${depBundle}"/></span></th>
	</tr>
		

	<portlet:renderURL var="thisPageURL">
	  <portlet:param name="atg_admin_menu_group" value="deployment"/>
	  <portlet:param name="atg_admin_menu_1" value="overview"/>
	  <portlet:param name="details_tabs" value="true"/>
	  <portlet:param name="site_tab_name" value="plan"/>
	  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
	</portlet:renderURL>



	<jsp:useBean id="depCodes" scope="application" class="atg.epub.pws.taglib.DeploymentStateCodes"/>
	<pws:getDeployments var="deployments" 
                       target="${target}" 
                       projects="${projects}" 
                       index="${planStartIndex}" 
                       count="${userListSize}" 
                       queuedOnly="${queuedOnly}" 
                       scheduledOnly="${scheduledOnly}"
                       totalCountVar="totalCount"/>
    <%--  if our index reaches over the totalCount we roll over and do this again --%>
    <c:if test="${planStartIndex+1 > totalCount}">
      <c:set scope="session" var="planStartIndex" value="0"/>
     	<pws:getDeployments var="deployments" 
                        target="${target}" 
                        projects="${projects}" 
                        index="${planStartIndex}" 
                        count="${userListSize}" 
                        queuedOnly="${queuedOnly}" 
                        scheduledOnly="${scheduledOnly}"
                        totalCountVar="totalCount"/>
    </c:if>
  <c:if test="${totalCount == 0}">  
    <tr> 
	   <td colspan="4" class="leftAligned">
      <span class="adminDeployment NoData">
      <fmt:message key="no-deployment-pending" bundle="${depBundle}"/>
      </span>
      </td>
    </tr>
  </c:if>

	 <dspel:form action="${thisPageURL}" method="post" id="cancelDeploymentsForm">		
 	<c:forEach var="deployment" items="${deployments.deployments}" varStatus="st">
	<c:choose>
	<c:when test="${st.count % 2 == 0}">
	<tr class="alternateRowHighlight">
	</c:when>
	<c:otherwise>
	<tr>
	</c:otherwise>
	</c:choose>
         <td class="centerAligned"><dspel:input bean="${deploymentFormHandlerName}.deleteDeployments" name="cancel_deployment_ids" id="cancelDeploymentCheckbox-${deployment.deploymentID}" value="${deployment.deploymentID}" type="checkbox" iclass="checkbox" /></td>
           <td class="leftAligned"><span class="tableInfo">
	   <c:if test="${!empty deployment.projectIDs}">
      <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
     </c:if>
	   <c:forEach var="projectID" items="${deployment.projectIDs}" varStatus="prst">
	     <pws:getProject var="project" projectId="${projectID}">
	      <c:if test="${prst.count ne 1}">
	       ,
	      </c:if>
       <%
         atg.security.ThreadSecurityManager.setThreadUser(user);
       %>
	      <dspel:include page="/includes/createProjectURL.jsp" flush="true">
	       <dspel:param name="outputAttributeName" value="projectURL"/>
	       <dspel:param name="inputProjectId" value="${projectID}"/>
	      </dspel:include>
   	      <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">

	       <c:out value="${project.displayName}"/>
	      </a> 
	     </pws:getProject>
	   </c:forEach> <%-- Projects --%>
	  </span></td>
    <c:choose>
     <c:when test="${deployment.status.state == depCodes.WAITING_SCHEDULED}">
     <c:set var="deploymentID" value="${deployment.deploymentID}"/>
      	<portlet:renderURL var="editDeploymentURL">
	  <portlet:param name="atg_admin_menu_group" value="deployment"/>
	  <portlet:param name="atg_admin_menu_1" value="overview"/>
	  <portlet:param name="details_tabs" value="true"/>
	  <portlet:param name="site_tab_name" value="plan"/>
	  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
	  <portlet:param name="edit_deployment_id" value='<%=pageContext.findAttribute("deploymentID")+""%>'/>
	</portlet:renderURL>

	    <td class="leftColumn"><span class="tableInfo">
       <a href="<c:out value='${editDeploymentURL}'/>" onmouseover="status='';return true;">
	     <fmt:formatDate type="date" value="${deployment.deployTime.time}"/>
       </a>
	     </span></td>	
      <td class="rightColumn"><span class="tableInfo">
       <a href="<c:out value='${editDeploymentURL}'/>" onmouseover="status='';return true;">
       <fmt:formatDate type="time" timeStyle="short" value="${deployment.deployTime.time}"/>
       </a>
      </span></td>
     </c:when>
     <c:otherwise>
	    <td class="leftColumn"><span class="tableInfo">
	     <fmt:formatDate type="date" value="${deployment.deployTime.time}"/>
	    </span></td>	
      <td class="rightColumn"><span class="tableInfo">
       <fmt:formatDate type="time" timeStyle="short" value="${deployment.deployTime.time}"/>
      </span></td>
     </c:otherwise>
     </c:choose>

   <td class="centerAligned"><span class="tableInfo">
    <c:if  test="${deployment.status.state == depCodes.WAITING_QUEUED}">
    <fmt:message key="queued" bundle="${depBundle}"/>
    </c:if>
    <c:if  test="${deployment.status.state == depCodes.WAITING_SCHEDULED}">
      <fmt:message key="scheduled" bundle="${depBundle}"/>
     </c:if>
     <c:if test="${deployment.status.stateError}">
     <fmt:message key="error" bundle="${depBundle}"/>
     </c:if>
     <c:if test="${deployment.status.stateActive}">
       <fmt:message key="Active" bundle="${depBundle}"/>
     </c:if>
  </span></td>
 

  <td class="centerAligned nowrap">
   <c:if test="${!st.first || !(empty planStartIndex  || planStartIndex == 0)}">
    <dspel:a href="${thisPageURL}" bean="/atg/epub/deployment/DeploymentFormHandler.runDeploymentNext" value="${deployment.deploymentID}">
    <fmt:message key="run-next" bundle="${depBundle}"/>
    </dspel:a>
    </c:if>
  <c:if test="${st.first && (empty planStartIndex || planStartIndex == 0) && target.halted && target.accessible}">
    <dspel:a href="${thisPageURL}" bean="/atg/epub/deployment/DeploymentFormHandler.runDeploymentNow" value="${deployment.deploymentID}">
    <fmt:message key="run-now" bundle="${depBundle}"/>
    </dspel:a>
    </c:if>
    
  </td>
		</tr>
</c:forEach> <%-- Deployments --%>
</dspel:form>


				
		</table>

		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0">
		<tr>	
		<td class="blankSpace" width="100%">&nbsp;</td>
		<td class="buttonImage"><a href="javascript:if(checkBoxChecked()){showIframe('cancelDeploymentsAction')};" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel-selected" bundle="${depBundle}"/></a></td>
		<td class="blankSpace"></td>
		</tr>
		</table>
		</div>
	

	    <%-- Paging support --%>
    <c:set var="currentPageCount" value="${(planStartIndex+1)/userListSize}"/>
    <c:set var="noOfPages" value="${totalCount/userListSize}"/>
    <% 
    Double pageCountDouble = (Double) pageContext.findAttribute("currentPageCount");
    pageContext.setAttribute("currentPageCount", new Double(Math.ceil(pageCountDouble.doubleValue())));

    Double noOfPagesDouble = (Double) pageContext.findAttribute("noOfPages");
    pageContext.setAttribute("noOfPages", new Double(Math.ceil(noOfPagesDouble.doubleValue())));

    %>
    <c:if test="${noOfPages >1}">
    <c:forEach begin="1" end="${noOfPages}" varStatus="st">
      <c:if test="${st.first}">
		    <div id="pagingDivBottom" class="displayResults">
		    <fmt:message key="displaying-page" bundle="${depBundle}"/>: 
       </c:if>
    
      <c:choose>
        <c:when test="${st.count == currentPageCount}">
         [<c:out value="${st.count}"/>]
        </c:when>  
       
        <c:otherwise>
        <c:set var="plan_start_index" value="${((st.count-1)*userListSize)}"/>
      	<portlet:renderURL var="pagingURL">
	        <portlet:param name="atg_admin_menu_group" value="deployment"/>
      	  <portlet:param name="atg_admin_menu_1" value="overview"/>
      	  <portlet:param name="details_tabs" value="true"/>
      	  <portlet:param name="site_tab_name" value="plan"/>
      	  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
      	  <portlet:param name="plan_start_index" value='<%=pageContext.findAttribute("plan_start_index")+""%>'/>
      	</portlet:renderURL>

          <a href="<c:out value='${pagingURL}'/>" onmouseover="status='';return true;"> <c:out value="${st.count}"/></a>
         </c:otherwise>
      </c:choose>
     
    <c:if test="${st.last}">
    </div>
    </c:if>

    </c:forEach>
        <script language="Javascript" type="text/javascript">
          document.getElementById("pagingDivTop").innerHTML = document.getElementById("pagingDivBottom").innerHTML
        </script>
   </c:if>
  <%--  End Paging Support --%>
    
	<!-- end content -->
		
</div>
    <script language="Javascript" type="text/javascript">
  <!--
  registerOnLoad(function() {init('cancelDeploymentsAction');})
   -->
 </script>

<div id="cancelDeploymentsAction" class="confirmAction">
	<dspel:iframe page="./iframes/action_cancel_deployments.jsp" scrolling="no"/>
</div>

</dspel:page>

<!-- END FILE site_plan.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_plan.jsp#2 $$Change: 651448 $--%>
