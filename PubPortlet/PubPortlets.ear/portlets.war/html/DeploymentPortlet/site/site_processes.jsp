<!-- BEGIN FILE site_processes.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
 
<dspel:page>
<%
  atg.security.User user = atg.security.ThreadSecurityManager.currentUser();
%>
<style>
   @import url("<c:url value='/templates/style/css/style.jsp' context="${initParam['atg.bizui.ContextPath']}"/>");
</style>

<%-- Paging support --%>
<%
  String gotoPage    = request.getParameter("gotoPage");
  String currentPage = request.getParameter("currentPage");
  String minGotoPage = request.getParameter("minGotoPage");
  String maxGotoPage = request.getParameter("maxGotoPage");

  if ( gotoPage != null 
       && ((Integer.parseInt(gotoPage) >= Integer.parseInt(minGotoPage)) 
            && (Integer.parseInt(gotoPage) <= Integer.parseInt(maxGotoPage))) )
  {
    pageContext.setAttribute("goto_page", gotoPage, pageContext.REQUEST_SCOPE);
  }
  else
  {
    pageContext.setAttribute("goto_page", currentPage, pageContext.REQUEST_SCOPE);
  }  
%>
<c:if test="${!empty goto_page}">
  <c:set var="resultsStartIndex" value="${(goto_page - 1) * userListSize}" scope="session"/>
</c:if>

<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<portlet:defineObjects/>	
<portlet:renderURL var="thisPageURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="results"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
 
<c:set var="isBackDeployable" value="${target.backDeployable}"/>
<c:set var="isTypeWorkflow" value="${!target.oneOff}"/>
<c:set var="isRollbackAllowed" value="${isBackDeployable == true && empty currentDeployment}"/>
<c:set var="enableRollback" value="false"/>
<c:set var="highWaterMark" value="0"/>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <td width="100%">
      <h2>
        <c:choose>
          <c:when test="${isTypeWorkflow}">
            <fmt:message key="deployed-active-processes-for-site" bundle="${depBundle}"> 
              <fmt:param value="${target.name}"/>
            </fmt:message>
          </c:when>
          <c:otherwise>
            <fmt:message key="deployed_processes-for-site" bundle="${depBundle}">
              <fmt:param value="${target.name}"/>
            </fmt:message>
          </c:otherwise>
        </c:choose>
      </h2></td>
      <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
    </tr>
  </table>
</div>

<div id="adminDeployment">

<form id="rollbackProjectForm" action="<c:out value='${thisPageURL}'/>" method="post" style="margin: 0px; padding: 0px;">
<table cellpadding="0" cellspacing="0" border="0" class="dataTable">

<tr>
  <c:if test="${isTypeWorkflow}">
    <th class="centerAligned" style="width: 4%;"><img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt="select multiple column" width="7" height="8" border="0" /></th>
  </c:if>
  <th class="leftAligned"><span class="tableHeader"><fmt:message key="process" bundle="${depBundle}"/></span></th>	
  <th class="centerAligned" style="width: 20%;" colspan="2"><span class="tableHeader"><fmt:message key="creation-date-time" bundle="${depBundle}"/></span></th>
  <th class="centerAligned"><span class="tableHeader"><fmt:message key="status" bundle="${depBundle}"/></span></th>	
  <th class="centerAligned" style="width: 25%;" colspan="2"><span class="tableHeader"><fmt:message key="deploy-date-time" bundle="${depBundle}"/></span></th>
  <th class="centerAligned"><span class="tableHeader"><fmt:message key="forward-deploy-type" bundle="${depBundle}"/></span></th>	
</tr>

<c:choose>
  <c:when test="${isTypeWorkflow}">
    <pws:getDeployedActiveProjects var="results"
                                   target="${target}"
                                   index="${resultsStartIndex}"
                                   count="${userListSize}"
                                   totalCountVar="totalCount"/>
    
    <c:set var="highWaterMark" value="${totalCount}"/>
    <pws:getDeployedCheckedInProjects var="cipResults"
                                      target="${target}"
                                      index="${resultsStartIndex}"
                                      count="${userListSize}"
                                      totalCountVar="totalCount"/>
    
    <c:if test="${totalCount > highWaterMark}">
      <c:set var="highWaterMark" value="${totalCount}"/>
    </c:if>

    <c:if test="${resultsStartIndex+1 > highWaterMark}"> <%-- If our count is over the start index due to a change in data, roll over to start --%>
      <c:set scope="session" var="resultsStartIndex" value="0"/>
      <pws:getDeployedActiveProjects var="results"
                                     target="${target}"
                                     index="${resultsStartIndex}"
                                     count="${userListSize}"
                                     totalCountVar="totalCount"/>
      
      <pws:getDeployedCheckedInProjects var="cipResults"
                                        target="${target}"
                                        index="${resultsStartIndex}"
                                        count="${userListSize}"
                                        totalCountVar="totalCount"/>
    </c:if>
  </c:when>
  <c:otherwise>
    <pws:getDeployedProjects var="results"
                             target="${target}"
                             index="${resultsStartIndex}"
                             count="${userListSize}"
                             totalCountVar="totalCount"/>
    
    <c:if test="${resultsStartIndex+1 > totalCount}"> <%-- If our count is over the start index due to a change in data, roll over to start --%>
      <c:set scope="session" var="resultsStartIndex" value="0"/>
      <pws:getDeployedProjects var="results"
                               target="${target}"
                               index="${resultsStartIndex}"
                               count="${userListSize}"
                               totalCountVar="totalCount"/>
    </c:if>
    <c:set var="highWaterMark" value="${totalCount}"/>
  </c:otherwise>
</c:choose>


<c:if test="${empty results || empty results.projects}">  
  <tr> 
    <td colspan="5" class="leftAligned">
      <span class="adminDeployment NoData">
        <c:choose>
          <c:when test="${isTypeWorkflow}">
            <c:if test="${resultsStartIndex == 0}">
              <fmt:message key="no-active-projects" bundle="${depBundle}"/> 
            </c:if>
            <c:if test="${resultsStartIndex != 0}">
              <fmt:message key="no-active-projects-in-range" bundle="${depBundle}"/> 
            </c:if>
          </c:when>
          <c:otherwise>
            <fmt:message key="no-projects" bundle="${depBundle}"/>
          </c:otherwise>
        </c:choose>
      </span>
    </td>
  </tr>
</c:if>
<c:set var="lastDeployedSnapshotCreationTime" value="${target.effectiveDeployedSnapshotCreationTime}"/>
<c:forEach var="project" items="${results.projects}" varStatus="st">
    <tr>
<c:if test="${isTypeWorkflow}">
  <td class="centerAligned">
    <c:choose>
      <c:when test='${isRollbackAllowed && st.first}'>
        <input type="radio" name="rollback_project" value="<c:out value='${project.id}'/>"/>
        <c:set var="enableRollback" value="true"/>
      </c:when>
      <c:otherwise>
        <input type="radio" disabled="true" name="rollback_project" value="<c:out value='${project.id}'/>"/>
      </c:otherwise>
    </c:choose>
  </td>
</c:if>

<%
  atg.security.ThreadSecurityManager.setThreadUser(user);
%>
<td class="leftAligned"><span class="tableInfo">
  <dspel:include page="/includes/createProjectURL.jsp" flush="true">
    <dspel:param name="outputAttributeName" value="projectURL"/>
    <dspel:param name="inputProjectId" value="${project.id}"/>
  </dspel:include>
  
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
       <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
  <c:out value="${project.displayName}"/>
  </a> 
</span></td>	
<td class="leftColumn"><span class="tableInfo">
    <fmt:formatDate type="date" value="${project.creationDate}"/>
</span></td>
<td class="rightColumn"><span class="tableInfo">
    <fmt:formatDate type="time" timeStyle="short" value="${project.creationDate}"/>
</span></td>
<td class="centerAligned"><span class="tableInfo">
    <c:if test="${project.status eq 'Active'}">
      <fmt:message key="active" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Completed'}">
      <fmt:message key="completed" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Suspended'}">
      <fmt:message key="suspended" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Error'}">
      <fmt:message key="error" bundle="${depBundle}"/>
    </c:if> 
</span></td>
<td class="leftColumn"><span class="tableInfo"><fmt:formatDate type="date" value="${project.deploymentTimes[target.ID]}"/>
</span></td>	

<td class="rightColumn"><span class="tableInfo">
    <fmt:formatDate type="time" timeStyle="short" value="${project.deploymentTimes[target.ID]}"/>
</span></td>

<td class="centerAligned"><span class="tableInfo">
  <pws:getDeploymentLog var="results" 
                        deploymentId="${project.deploymentIds[target.ID]}"
                        totalCountVar="totalCount"/>
  <c:if test="${totalCount != 0}">
    <c:out value="${(results.deploymentLogs)[0].deploymentType}"/>
  </c:if>
  </span>
</td>

</tr>
</c:forEach> <%-- deployed projects --%>
</table>


<c:if test="${!isTypeWorkflow}">
<pws:getDeploymentLog var="logResults"
                      target="${target}"
                      deploymentType="1"
                      totalCountVar="totalLogCount"/>

<c:if test="${totalLogCount != 0}">
<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <td width="100%">
      <h2>
            <fmt:message key="last-full-deployment-for-site" bundle="${depBundle}"> 
              <fmt:param value="${target.name}"/>
              <fmt:param><fmt:formatDate type="both" timeStyle="short" value="${(logResults.deploymentLogs)[0].endTime}"/></fmt:param>
            </fmt:message>
      </h2>
      </td>
    </tr>
  </table>
</div>
</c:if>
</c:if>



<c:if test="${isTypeWorkflow}">
<table cellpadding="0" cellspacing="0" border="0" class="dataTable">
  <tr>
    <tr> 
      <td colspan="5" class="leftAligned">
        <span class="adminDeployment NoData">
        </span>
      </td>
    </tr>
  </tr>
</table>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <td width="100%"><h2><fmt:message key="deployed-checkedin-processes-for-site" bundle="${depBundle}"> 
            <fmt:param value="${target.name}"/>
          </fmt:message>
      </h2></td>
    </tr>
  </table>
</div>

<table cellpadding="0" cellspacing="0" border="0" class="dataTable">
<tr>
  <th class="centerAligned" style="width: 4%;"><img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt="select multiple column" width="7" height="8" border="0" /></th>
  <th class="leftAligned"><span class="tableHeader"><fmt:message key="process" bundle="${depBundle}"/></span></th>	
  <th class="centerAligned" style="width: 20%;" colspan="2"><span class="tableHeader"><fmt:message key="creation-date-time" bundle="${depBundle}"/></span></th>
  <th class="centerAligned"><span class="tableHeader"><fmt:message key="status" bundle="${depBundle}"/></span></th>	
  <th class="centerAligned" style="width: 25%;" colspan="2"><span class="tableHeader"><fmt:message key="deploy-date-time" bundle="${depBundle}"/></span></th>
  <th class="centerAligned"><span class="tableHeader"><fmt:message key="forward-deploy-type" bundle="${depBundle}"/></span></th>	
</tr>

<c:if test="${empty cipResults || empty cipResults.projects}">  
  <tr> 
    <td colspan="5" class="leftAligned">
      <span class="adminDeployment NoData">
        <c:if test="${resultsStartIndex == 0}">
          <fmt:message key="no-checkedin-projects" bundle="${depBundle}"/>
        </c:if>
        <c:if test="${resultsStartIndex != 0}">
          <fmt:message key="no-checkedin-projects-in-range" bundle="${depBundle}"/>
        </c:if>      
      </span>
    </td>
  </tr>
</c:if>
<c:forEach var="project" items="${cipResults.projects}" varStatus="st">
<c:choose>
  <c:when test="${(project.checkinDate != null && lastDeployedSnapshotCreationTime != null) &&  project.checkinDate.time > lastDeployedSnapshotCreationTime}">
    <tr class="overview siteAlert">
  </c:when>
  <c:otherwise>
    <tr>
  </c:otherwise>
</c:choose>

<c:if test="${isTypeWorkflow}">
  <td class="centerAligned">
    <c:if test="${project.checkedIn && !empty project.deploymentSnapshots[target.ID]}">
      <c:choose>
        <c:when test='${isRollbackAllowed == true}'>
          <input type="radio" name="rollback_project" value="<c:out value='${project.id}'/>"/>
          <c:set var="enableRollback" value="true"/>
        </c:when>
        <c:otherwise>
          <input type="radio" disabled="true" name="rollback_project" value="<c:out value='${project.id}'/>"/>
        </c:otherwise>
      </c:choose>
    </c:if>
    <c:if test="${!project.checkedIn || empty project.deploymentSnapshots[target.ID]}">
      <input type="radio" disabled="true" name="rollback_project" value="<c:out value='${project.id}'/>"/>
    </c:if>    
  </td>
</c:if>

<%
  atg.security.ThreadSecurityManager.setThreadUser(user);
%>
<td class="leftAligned"><span class="tableInfo">
  <dspel:include page="/includes/createProjectURL.jsp" flush="true">
    <dspel:param name="outputAttributeName" value="projectURL"/>
    <dspel:param name="inputProjectId" value="${project.id}"/>
  </dspel:include>
  
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
       <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
  <c:out value="${project.displayName}"/>
  </a> 
</span></td>	
<td class="leftColumn"><span class="tableInfo">
    <fmt:formatDate type="date" value="${project.creationDate}"/>
</span></td>
<td class="rightColumn"><span class="tableInfo">
    <fmt:formatDate type="time" timeStyle="short" value="${project.creationDate}"/>
</span></td>
<td class="centerAligned"><span class="tableInfo">
    <c:if test="${project.status eq 'Active'}">
      <fmt:message key="active" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Completed'}">
      <fmt:message key="completed" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Suspended'}">
      <fmt:message key="suspended" bundle="${depBundle}"/>
    </c:if> 
    <c:if test="${project.status eq 'Error'}">
      <fmt:message key="error" bundle="${depBundle}"/>
    </c:if> 
</span></td>
<td class="leftColumn"><span class="tableInfo"><fmt:formatDate type="date" value="${project.deploymentTimes[target.ID]}"/>
</span></td>	

<td class="rightColumn"><span class="tableInfo">
    <fmt:formatDate type="time" timeStyle="short" value="${project.deploymentTimes[target.ID]}"/>
</span></td>
<td class="centerAligned"><span class="tableInfo">
  <pws:getDeploymentLog var="logResults" 
                        deploymentId="${project.deploymentIds[target.ID]}"
                        totalCountVar="totalLogCount"/>
  <c:if test="${totalLogCount != 0}">
    <c:out value="${(logResults.deploymentLogs)[0].deploymentType}"/>
  </c:if>
  </span>
</td>

</tr>
</c:forEach> <%-- deployed projects --%>
</table>
</c:if> 
</form>	

<c:if test="${isTypeWorkflow}">
  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <c:choose>
        <c:when test="${enableRollback}">
          <tr>	
            <td class="blankSpace" width="100%">&nbsp;</td>
            <td class="buttonImage"><a href="javascript:submitForm('rollbackProjectForm');" class="mainContentButton rollback" onmouseover="status='';return true;"><fmt:message key="rollback-to-selected" bundle="${depBundle}"/></a></td>		
            <td class="blankSpace"></td>
          </tr>
        </c:when>  
        <c:otherwise>
          <tr>	
            <td class="paddingLeft" width="100%"></td>
            <td><a href="javascript:submitForm('rollbackProjectForm');" class="button disabled" onmouseover="status='';return true;"  onclick="return false;"><fmt:message key="rollback-to-selected" bundle="${depBundle}"/></a></td>		
            <td class="blankSpace">&nbsp;</td>
            <td class="blankSpace">&nbsp;</td>
          </tr>
        </c:otherwise>
      </c:choose>
    </table>
  </div>
</c:if>

<%-- Paging support --%>
    <c:set var="noOfPages" value="${highWaterMark/userListSize}"/>
    <c:set var="currentPageCount" value="${(resultsStartIndex+1)/userListSize}"/>

    <% 
    Double pageCountDouble = (Double) pageContext.findAttribute("currentPageCount");
    pageContext.setAttribute("currentPageCount", new Double(Math.ceil(pageCountDouble.doubleValue())));

    Double  noOfPagesDouble = (Double) pageContext.findAttribute("noOfPages");
    pageContext.setAttribute("noOfPages", new Double(Math.ceil(noOfPagesDouble.doubleValue())));

    %>

    <c:if test="${noOfPages >1}">
    <div id="pagingDivBottom" class="displayResults">

    <%-- Goto first button --%>
    <c:set var="results_start_index" value="0"/>
    <portlet:renderURL var="pagingURL">
      <portlet:param name="atg_admin_menu_group" value="deployment"/>
      <portlet:param name="atg_admin_menu_1" value="overview"/>
      <portlet:param name="details_tabs" value="true"/>
      <portlet:param name="site_tab_name" value="results"/>
      <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
      <portlet:param name="results_start_index" value='<%=pageContext.findAttribute("results_start_index")+""%>'/>
    </portlet:renderURL>      
    <a href="<c:out value='${pagingURL}'/>" title="Go to first page" class="standardButton" onmouseover="status='';return true;"><<</a>


    <%-- Goto prev button --%>
    <c:choose>
      <c:when test="${currentPageCount == 1}">
        <c:set var="results_start_index" value="0"/>
      </c:when>
      <c:otherwise>
        <c:set var="results_start_index" value="${resultsStartIndex - userListSize}"/>
      </c:otherwise>
    </c:choose>
    <portlet:renderURL var="pagingURL">
      <portlet:param name="atg_admin_menu_group" value="deployment"/>
      <portlet:param name="atg_admin_menu_1" value="overview"/>
      <portlet:param name="details_tabs" value="true"/>
      <portlet:param name="site_tab_name" value="results"/>
      <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
      <portlet:param name="results_start_index" value='<%=pageContext.findAttribute("results_start_index")+""%>'/>
    </portlet:renderURL>      
    <a href="<c:out value='${pagingURL}'/>" title="Go to previous page" class="standardButton" onmouseover="status='';return true;"><</a>
    
    <%-- Goto next button --%>
    <c:choose>
      <c:when test="${currentPageCount == noOfPages}">
        <c:set var="results_start_index" value="${resultsStartIndex}"/>
      </c:when>
      <c:otherwise>
        <c:set var="results_start_index" value="${resultsStartIndex + userListSize}"/>
      </c:otherwise>
    </c:choose>
    <portlet:renderURL var="pagingURL">
      <portlet:param name="atg_admin_menu_group" value="deployment"/>
      <portlet:param name="atg_admin_menu_1" value="overview"/>
      <portlet:param name="details_tabs" value="true"/>
      <portlet:param name="site_tab_name" value="results"/>
      <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
      <portlet:param name="results_start_index" value='<%=pageContext.findAttribute("results_start_index")+""%>'/>
    </portlet:renderURL>      
    <a href="<c:out value='${pagingURL}'/>" title="Go to next page" class="standardButton" onmouseover="status='';return true;">></a>
    
    <%-- Goto last button --%>
    <c:set var="results_start_index" value="${(noOfPages * userListSize) - userListSize}"/>
    <portlet:renderURL var="pagingURL">
      <portlet:param name="atg_admin_menu_group" value="deployment"/>
      <portlet:param name="atg_admin_menu_1" value="overview"/>
      <portlet:param name="details_tabs" value="true"/>
      <portlet:param name="site_tab_name" value="results"/>
      <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
      <portlet:param name="results_start_index" value='<%=((Double)pageContext.findAttribute("results_start_index")).intValue()+""%>'/>
    </portlet:renderURL>      
    <a href="<c:out value='${pagingURL}'/>" title="Go to last page" class="standardButton" onmouseover="status='';return true;">>></a>


    <%-- Text field for selecting a page --%>
    <fmt:message key="page-of-1" bundle="${depBundle}"/>
    <dspel:form action="${thisPageURL}" id="gotoPage" method="post">
      <input name="gotoPage" type="text" id="gotoPageNumber"
             size="2" value="<%= ((Double)pageContext.findAttribute("currentPageCount")).intValue()+""%>"
             onkeypress="return gotoPageKeyPress(event)"/>
      <input name="currentPage" type="hidden" id="currentPageNumber" value='<%= ((Double)pageContext.findAttribute("currentPageCount")).intValue()+""%>'/>
      <input name="minGotoPage" type="hidden" id="minGotoPageNumber" value='1'/>
      <input name="maxGotoPage" type="hidden" id="maxGotoPageNumber" value='<%= ((Double)pageContext.findAttribute("noOfPages")).intValue()+""%>'/>
    </dspel:form>
    <fmt:message key="page-of-2" bundle="${depBundle}"/>
    <span id="numPages">
      <%= ((Double)pageContext.findAttribute("noOfPages")).intValue()+""%>
    </span>

  </div>
    
        <script language="Javascript">
          document.getElementById("pagingDivTop").innerHTML = document.getElementById("pagingDivBottom").innerHTML
        </script>

       <script language="Javascript">
          function gotoPageKeyPress(evt)
          {
            // Determine which key was pressed.
            if (evt == null) evt = event;
            var charCode = evt.charCode;
            if (charCode == null || charCode == 0) charCode = evt.keyCode;
            if (charCode == null) charCode = evt.which;

            // If the user hit Enter, go to the indicated page.
            if (charCode == 13 || charCode == 3) {
               var form = document.getElementById( "gotoPage" );
               form.submit();
               return false;
            }
            else if (charCode >= 32 && (charCode < 48 || charCode > 57)) {
              // Ignore non-numeric text.
              return false;
            }

            // Process other characters normally.
            return true;
          }
        </script>
    </c:if>

    <%--  End Paging support  --%>

		</div>
		
	<!-- end content -->

</dspel:page>
<!-- END FILE site_processes.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_processes.jsp#2 $$Change: 651448 $--%>
