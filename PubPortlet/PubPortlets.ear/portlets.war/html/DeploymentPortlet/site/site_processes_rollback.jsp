<!-- BEGIN FILE site_processes_rollback.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>
 <portlet:defineObjects/>
 <div id="adminDeployment">
 <script type="text/javascript">
   var djConfig = { 
     parseOnLoad: true
   };
   dojo.registerModulePath("atg.widget.form", "/AssetManager/dijit/form");
   dojo.require("atg.widget.form.SimpleDateTextBox");
   dojo.require("dojo.parser");
   function submitDeployForm() {
     var dateField = document.getElementById("date1Input");
     var hiddenField = document.getElementById("date1InputHidden");
     hiddenField.value = dateField.value;
     submitForm('deployForm');
   }
 </script>
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
  <td class="blankSpace">&nbsp;</td>
  <td><h2><fmt:message key="rollback-process-on-site" bundle="${depBundle}"> 
            <fmt:param value="${target.name}"/>
      </fmt:message>
      </h2></td>
    <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>  
  <td class="blankSpace">&nbsp;</td>
  </tr>
  </table>
  </div>
<portlet:renderURL var="successURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="results"/>
  <portlet:param name="done_rollback" value="true"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
<portlet:renderURL var="failureURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="results"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

  <dspel:form action="${failureURL}" method="post" name="deployForm" id="deployForm">  
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  
  <tr>
  <td class="adminDeploymentLeftStyle">
 
   <table cellpadding="0" cellspacing="0" border="0" class="dataTable" id="adminDeploymentLeft" style="border: none;">
  
   <tr>
   <th class="leftAligned"><span class="tableHeader"><fmt:message key="process-to-rollback-to" bundle="${depBundle}"/></span></th>  
   </tr>
  <c:forEach var="projectToDeployId" items="${rollbackProject}" varStatus="depPrStatus"> 
      <pws:getProject var="projectToDeploy" projectId="${projectToDeployId}"/>
     <dspel:input bean="${deploymentFormHandlerName}.deployProjectIDs" type="hidden" value="${projectToDeploy.id}"/>                  
     <dspel:include page="/includes/createProjectURL.jsp">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${projectToDeployId}"/>
        </dspel:include>
     
      <c:choose>
    <c:when test="${(depPrStatus.count % 2) == 0}"> 
     <tr class="alternateRowHighlight">
     <td style="border-left: none;" class="leftAligned"><span class="tableInfo"><a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
        <c:out value="${projectToDeploy.displayName}"/></a></span></td> 
       </c:when>
    <c:otherwise> 
        <tr>
     <td class="leftAligned"><span class="tableInfo"><a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
        <c:out value="${projectToDeploy.displayName}"/></a></span></td> 
       </c:otherwise>
    </c:choose>
   
   </tr>

      </c:forEach>
  
   
   </table>

  
  </td>

    <td id="adminToDoRight">  
     <table border="0" cellpadding="3" cellspacing="0" width="100%" class="leftAligned">
     <tr>
       <td class="rightAligned" style="width: 30%"><p><fmt:message key="rollback" bundle="${depBundle}"/>:</p></td>
      <td><p><dspel:input bean="${deploymentFormHandlerName}.useNowForTime" value="true" type="radio" iclass="radio" />&nbsp;<fmt:message key="now" bundle="${depBundle}"/></p></td>
     </tr>
   
     <tr>
      <td><p></p></td>
       <td><p><dspel:input bean="${deploymentFormHandlerName}.useNowForTime" value="false" type="radio" iclass="radio"/>&nbsp;<fmt:message key="on-date" bundle="${depBundle}"/>:
       <fmt:message var="dateFormat" bundle="${viewMapBundle}" key="date-format-input"/>
       <dspel:input id="date1InputHidden"
                    bean="${deploymentFormHandlerName}.formattedDate" 
                    type="hidden"/> 
       <input id="date1Input"
              type="text"
              class="fixedDate"
              constraints="{datePattern: '<c:out value="${dateFormat}"/>'}"
              dojoType="atg.widget.form.SimpleDateTextBox"/>
         <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/calendar/calendar2_off.gif'/>" 
              id="date1Anchor" 
              onclick="document.getElementById('date1Input').focus()" 
              alt='<fmt:message key="select-a-date" bundle="${depBundle}"/>' 
              width="17" height="11" class="imgpadded" border="0">
       </p>
        </tr>
        <tr>
       </td>
       <td><p></p></td>
       <td><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="on-time" bundle="${depBundle}"/>: <dspel:input id="timeInput" bean="${deploymentFormHandlerName}.formattedTime" type="text" iclass="fixedTime" /></td>
        </tr>
    <c:if test="${!empty pageContext.response.locale}">
       <dspel:input bean="${deploymentFormHandlerName}.dateLocale" type="hidden" value="${pageContext.response.locale}"/>
       </c:if>
      

   <tr>
   <td><p></p></td>
     <dspel:setvalue bean="${deploymentFormHandlerName}.dateLocale" value="${pageContext.response.locale.displayName}"/>  
      <td><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;e.g., <dspel:valueof bean="${deploymentFormHandlerName}.timeExample"/>  (<fmt:message key="leave-blank" bundle="${depBundle}"/>)</td>
   </p>
      </tr>

      <tr>
   <td><p> <td height="20" colspan="2">&nbsp;</td>
   </tr>
   
   <tr>
   <td class="rightAligned"><p>Deployment Type:</p></td>
   <td><p><dspel:input bean="${deploymentFormHandlerName}.forceFull" value="false" type="radio" iclass="radio"/>&nbsp;<fmt:message key="attempt-incremental-deployment" bundle="${depBundle}"/></p></td>
   </tr>
   
   <tr>
   <td><p></p></td>
   <td><p><dspel:input bean="${deploymentFormHandlerName}.forceFull" value="true" type="radio" iclass="radio"/>&nbsp;<fmt:message key="force-full-deployment" bundle="${depBundle}"/></p></td>
   </tr>
   <dspel:input bean="${deploymentFormHandlerName}.deployTargetId" type="hidden" value="${target.ID}"/>
      <dspel:input bean="${deploymentFormHandlerName}.reverseDeployment" type="hidden" value="true"/>            
   <dspel:input bean="${deploymentFormHandlerName}.scheduleDeployment" type="hidden" priority="-10" value="foo"/>
   <dspel:input bean="${deploymentFormHandlerName}.successURL" type="hidden" value="${successURL}"/>   
   <dspel:input bean="${deploymentFormHandlerName}.failureURL" type="hidden" value="${failureURL}"/>   
     </dspel:form>
   <tr>
   <td height="20" colspan="2">&nbsp;</td>
   </tr>
   </table>
  </td>
  </tr>
  </table>
<portlet:renderURL var="resultsURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="results"/>
  <portlet:param name="done_rollback" value="true"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

  <div class="contentActions">
  
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
  <td class="blankSpace" width="100%">&nbsp;</td>

  <td class="buttonImage"><a href="javascript:submitDeployForm();" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="rollback" bundle="${depBundle}"/></a></td>
  <td class="buttonImage"><a href="<c:out value="${resultsURL}"/>" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel" bundle="${depBundle}"/></a></td>
  <td class="blankSpace">&nbsp;</td>
  </tr>
  
  </table>

  </div>
 <!-- end content -->
  
 </div>



</dspel:page>
<!-- END FILE site_processes_rollback.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_processes_rollback.jsp#2 $$Change: 651448 $--%>
