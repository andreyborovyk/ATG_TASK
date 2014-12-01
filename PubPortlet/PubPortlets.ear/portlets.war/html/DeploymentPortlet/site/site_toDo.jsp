<!-- BEGIN FILE site_toDo.jsp -->
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

<script type="text/javascript">
  function checkboxClick(){
  var field = document.getElementsByName('deploy_reject_projects');

  if (field != null && field.length != null)
  {
    var elemDeploy = document.getElementById("deployButton");
    var elemReject = document.getElementById("rejectButton");
    elemDeploy.onclick=function() { return false; }
    elemReject.onclick=function() { return false; }

    for (i=0; i<field.length; i++) {
        if (field[i].checked == true) {
          elemDeploy.onclick=function() { return true; }
          elemReject.onclick=function() { return true; }
        }
    }
  }
}
  
<!--
    function submitProjectsForm(deploy) {
      if(document.forms["deployRejectProjectsForm"]){
        if(!deploy )
           document.getElementById("deployOrRejectInput").value="reject";
        document.forms["deployRejectProjectsForm"].submit();
      }
	  }
-->
</script>
<portlet:renderURL var="thisPageURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
		<td class="blankSpace">&nbsp;</td>
		<td><h2><fmt:message key="to-do-list-site" bundle="${depBundle}"> 
		         <fmt:param value="${target.name}"/>
				 </fmt:message></h2></td>
   	<td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>		
		</tr>
		</table>
		</div>
			
		<div id="adminDeployment">
		
<form id="deployRejectProjectsForm" action="<c:out value='${thisPageURL}'/>" method="post" style="margin: 0px; padding: 0px;">
		<table cellpadding="0" cellspacing="0" border="0" class="dataTable">
		
		<tr>
  <th class="centerAligned" style="width: 4%;">
    <input type="checkbox" id="checkAllField" title="<fmt:message key='select-or-deselect' bundle='${depBundle}'/>" class="checkbox" onClick="checkAll('deployRejectProjectsForm','deploy_reject_projects');checkboxClick();"/>
  </th>
		<th class="leftAligned"><span class="tableHeader"><fmt:message key="process-name" bundle="${depBundle}"/></span></th>		
		<th class="centerAligned" colspan="2"><span class="tableHeader"><fmt:message key="creation-date-time" bundle="${depBundle}"/></span></th>		
		<th class="centerAligned"><span class="tableHeader"><fmt:message key="status" bundle="${depBundle}"/></span></th>
		<th class="centerAligned"><span class="tableHeader"><fmt:message key="info" bundle="${depBundle}"/></span></th>
		</tr>
	
<pws:getProjectsPendingDeployment var="results" 
                       target="${target}" 
                       index="${toDoStartIndex}" 
                       count="${userListSize}"
                       totalCountVar="totalCount"/>
    <c:if test="${toDoStartIndex+1 > totalCount}">
            <c:set scope="session" var="toDoStartIndex" value="0"/>
            <pws:getProjectsPendingDeployment var="results" 
                       target="${target}" 
                       index="${toDoStartIndex}" 
                       count="${userListSize}"
                       totalCountVar="totalCount"/>

    </c:if>
  <c:if test="${totalCount == 0}">  
    <tr> 
	   <td colspan="4" class="leftAligned">
      <span class="adminDeployment NoData">
       <fmt:message key="no-projects-todo" bundle="${depBundle}"/>
      </span>
      </td>
    </tr>
  </c:if>
  <c:forEach var="toDoProject" items="${results}" varStatus="st">
	<c:choose>
	<c:when test="${st.count % 2 == 0}">
	<tr class="alternateRowHighlight">
	</c:when>
	<c:otherwise>
	<tr>
	</c:otherwise>
	</c:choose>
	<td class="centerAligned"><input id="check_${st.count}" onclick="checkboxClick()" type="checkbox" name="deploy_reject_projects" value="<c:out value='${toDoProject.project.id}'/>" class="checkbox" /></td>
	<input type="hidden" name="deploy_or_reject" value="deploy" id="deployOrRejectInput"/>
 <%
   atg.security.ThreadSecurityManager.setThreadUser(user);
 %>
	<dspel:include page="/includes/createProjectURL.jsp">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${toDoProject.project.id}"/>
     </dspel:include>
	
	<td class="leftAligned"><span class="tableInfo">
  <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
 	  <c:out value="${toDoProject.project.displayName}"/>
	</a></span></td>
  <td class="leftColumn"><span class="tableInfo">
   <fmt:formatDate type="date" value="${toDoProject.project.creationDate}"/>
   </span></td>
  <td class="rightColumn"><span class="tableInfo">
    <fmt:formatDate type="time" timeStyle="short" value="${toDoProject.project.creationDate}"/>
  </span></td>
 <c:if test="${toDoProject.orphaned}">
		<td class="centerAligned"><span class="tableInfo error">
		<fmt:message key="orphaned" bundle="${depBundle}"/>
		</span></td>		
	 </c:if>
	 <c:if test="${!toDoProject.orphaned}">
		<td class="centerAligned"><span class="tableInfo">
		<fmt:message key="approved" bundle="${depBundle}"/>
		</span></td>		
	 </c:if>
	<td class="centerAligned"><span class="tableInfo">
       <c:choose>
          <c:when test="${toDoProject.days eq 0}">
          <fmt:message key="today" bundle="${depBundle}"/>
          </c:when>
          <c:when test="${toDoProject.days eq 1}">
            <c:out value="${toDoProject.days}"/> <fmt:message key="day-old" bundle="${depBundle}"/> 
          </c:when>
           <c:when test="${toDoProject.days gt 1}">
            <c:out value="${toDoProject.days}"/> <fmt:message key="days-old" bundle="${depBundle}"/>
          </c:when>
      </c:choose>
      </span></td>

	</tr>
    </c:forEach> <%-- results --%>

		
		</table>

</form>	
		<div class="contentActions">
		<table cellpadding="0" cellspacing="0" border="0">
		<tr>	
                 
		<td class="blankSpace" width="100%">&nbsp;</td>
		<td class="buttonImage"><a id ="deployButton" href="javascript:submitProjectsForm(true);" class="mainContentButton go" onmouseover="status='';return true;" onclick="return false;"><fmt:message key="deploy-selected-processes" bundle="${depBundle}"/></a></td>

		<td class="buttonImage"><a id ="rejectButton" href="javascript:submitProjectsForm(false);" class="mainContentButton delete" onmouseover="status='';return true;" onclick="return false;"><fmt:message key="reject-selected-processes" bundle="${depBundle}"/></a></td>
		<td class="blankSpace"></td>
		</tr>
		</table>
		</div>
	
		</div>
    
    <%-- Paging support --%>
    <c:set var="currentPageCount" value="${(toDoStartIndex+1)/userListSize}"/>
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
         
        <c:set var="todo_start_index" value="${((st.count-1)*userListSize)}"/>
          <portlet:renderURL var="pagingURL">
            <portlet:param name="atg_admin_menu_group" value="deployment"/>
            <portlet:param name="atg_admin_menu_1" value="overview"/>
            <portlet:param name="details_tabs" value="true"/>
            <portlet:param name="site_tab_name" value="todo"/>
            <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
            <portlet:param name="todo_start_index" value='<%=pageContext.findAttribute("todo_start_index")+""%>'/>
         </portlet:renderURL>
   
          <a href="<c:out value='${pagingURL}'/>" onmouseover="status='';return true;"> <c:out value="${st.count}"/></a>
         </c:otherwise>
      </c:choose>
     
    <c:if test="${st.last}">
    </div>
    </c:if>

    </c:forEach>
        <script language="Javascript">
          document.getElementById("pagingDivTop").innerHTML = document.getElementById("pagingDivBottom").innerHTML
        </script>

    </c:if>

    <%--  End Paging support  --%>

	</td>
	</tr>
	
	</table>	
	<!-- end content -->

</dspel:page>

<!-- END FILE site_toDo.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_toDo.jsp#2 $$Change: 651448 $--%>
