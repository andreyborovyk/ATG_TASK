<!-- BEGIN FILE site_toDo_deploy.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle"     basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>
 <portlet:defineObjects/>
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
 <div id="adminDeployment">
    </script>  
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
  <td class="blankSpace">&nbsp;</td>
  <td><h2>        <fmt:message key="deploy-processes-to-site" bundle="${depBundle}"> 
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
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="done_deploy" value="true"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
<portlet:renderURL var="failureURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="todo"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>

  <dspel:form action="${failureURL}" method="post" id="deployForm">  
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  
  <tr>
  <td class="adminDeploymentLeftStyle">
 
   <table cellpadding="0" cellspacing="0" border="0" class="dataTable" id="adminDeploymentLeft" style="border: none;">
  
   <tr>
   <th class="leftAligned"><span class="tableHeader">            <fmt:message key="processes-to-be-deployed" bundle="${depBundle}"/></span></th>  
   <th class="leftAligned"><span class="tableHeader">            <fmt:message key="dependent-on" bundle="${depBundle}"/></span></th>  
   </tr>
  <pws:getProjects var="projectsToDeploy" projectIds="${deployRejectProjects}"/>
  <pws:getDependentProjects var="dependentProjects" candidateProjects="${projectsToDeploy.projects}" target="${target}"/>
         <tr>
  <td style="border-left: none;" class="leftAligned"><span class="tableInfo">
  <c:forEach var="projectToDeployId" items="${deployRejectProjects}" varStatus="depPrStatus"> 
      <pws:getProject var="projectToDeploy" projectId="${projectToDeployId}"/>
     <dspel:input bean="${deploymentFormHandlerName}.deployProjectIDs" type="hidden" value="${projectToDeploy.id}"/>                  
     <dspel:include page="/includes/createProjectURL.jsp">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${projectToDeployId}"/>
        </dspel:include>

          <c:if test="${depPrStatus.count > 1}"><br/></c:if>
     <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
      <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
        <c:out value="${projectToDeploy.displayName}"/></a> 
        
      </c:forEach>
   </span></td> 
   <td style="border-left: none; class="leftAligned"><span class="tableInfo">      
      <c:forEach var="dependentProject" items="${dependentProjects}" varStatus="st"> 
          <c:if test="${st.count > 1}"><br/></c:if>

         <dspel:include page="/includes/createProjectURL.jsp">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${dependentProject.id}"/>
        </dspel:include>
         <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
         <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_process.gif'/>" alt="Process" style="margin-right: 6px; vertical-align: middle;" border="0" height="16" width="16"/>
         <c:out value="${dependentProject.displayName}"/></a>
        </c:forEach>
 
   </tr>
   </table>
<br/><br/>
  <p/>        <fmt:message key="dependent-note" bundle="${depBundle}"/> 
  
  </td>

    <td id="adminToDoRight">  
   <table border="0" cellpadding="3" cellspacing="0" width="100%" class="leftAligned">
   <tr>
   <td class="rightAligned" style="width: 30%"><p>            <fmt:message key="deploy" bundle="${depBundle}"/>:</p></td>
   <td><p><dspel:input bean="${deploymentFormHandlerName}.useNowForTime" value="true" type="radio" iclass="radio" />&nbsp;            <fmt:message key="now" bundle="${depBundle}"/></p></td>
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
      <!-- [Calendar Container] -->
      <div id="calendarContainer" class="atgCalendar_divShow"></div>
      <!-- [/Calendar Container] -->
   </p>
      </tr>
      <%--
     <tr>
   <td><p></p></td>
     <dspel:setvalue bean="${deploymentFormHandlerName}.dateLocale" value="${pageContext.response.locale.displayName}"/>  
      <td><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    <fmt:message key="example" bundle="${depBundle}"/>: <dspel:valueof bean="${deploymentFormHandlerName}.dateExample"/></td>
   </p>
      </tr>
      --%>
      <tr>
   </td>
   <td><p></p></td>
   <td><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            <fmt:message key="at-time" bundle="${depBundle}"/>: <dspel:input id="timeInput" bean="${deploymentFormHandlerName}.formattedTime" type="text" iclass="fixedTime" /></td>
      <tr>
    <c:if test="${!empty pageContext.response.locale}">
       <dspel:input bean="${deploymentFormHandlerName}.dateLocale" type="hidden" value="${pageContext.response.locale}"/>
       </c:if>
      

   <tr>
   <td><p></p></td>
     <dspel:setvalue bean="${deploymentFormHandlerName}.dateLocale" value="${pageContext.response.locale.displayName}"/>  
      <td><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    <fmt:message key="example" bundle="${depBundle}"/> <dspel:valueof bean="${deploymentFormHandlerName}.timeExample"/>  (<fmt:message key="leave-blank" bundle="${depBundle}"/>)</td>
   </p>

      </tr>
 
      
   <td><p> <td height="20" colspan="2">&nbsp;</td>
   </tr>
   
   <tr>
   <td class="rightAligned"><p>            <fmt:message key="deployment-type" bundle="${depBundle}"/>:</p></td>
   <td><p><dspel:input bean="${deploymentFormHandlerName}.forceFull" value="false" type="radio" iclass="radio"/>&nbsp;            <fmt:message key="attempt-incremental-deployment" bundle="${depBundle}"/></p></td>
   </tr>
   
   <tr>
   <td><p></p></td>
   <td><p><dspel:input bean="${deploymentFormHandlerName}.forceFull" value="true" type="radio" iclass="radio"/>&nbsp;            <fmt:message key="force-full-deployment" bundle="${depBundle}"/></p></td>
   </tr>

 <dspel:importbean scope="request" var="deploymentServer" bean="/atg/epub/DeploymentServer"/>
 <c:if test="${deploymentServer.useDafDeployment}">
   <tr>
     <p><td height="20" colspan="2">&nbsp;</td>
   </tr>
   <tr>
     <td class="rightAligned"><p>&nbsp;</p></td>
     <td><p><dspel:input bean="${deploymentFormHandlerName}.strictFileOpsOverride" type="checkbox" iclass="checkbox"/>&nbsp;&nbsp;<fmt:message key="strict-file-ops" bundle="${depBundle}"/></p></td>
   </tr>
   <tr>
     <td><p>&nbsp;</p></td>
     <td><p><dspel:input bean="${deploymentFormHandlerName}.strictRepoOpsOverride" type="checkbox" iclass="checkbox"/>&nbsp;&nbsp;<fmt:message key="strict-repository-ops" bundle="${depBundle}"/></p></td>
   </tr>
 </c:if>

   <dspel:input bean="${deploymentFormHandlerName}.deployTargetId" type="hidden" value="${target.ID}"/>
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

  <div class="contentActions">
  
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
  <td class="blankSpace" width="100%">&nbsp;</td>
  <td class="buttonImage"><a href="javascript:submitDeployForm();" class="mainContentButton go" onmouseover="status='';return true;">        <fmt:message key="deploy" bundle="${depBundle}"/></a></td>
  <td class="buttonImage"><a href="<c:out value="${successURL}"/>" class="mainContentButton delete" onmouseover="status='';return true;">        <fmt:message key="cancel" bundle="${depBundle}"/></a></td>
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
<!-- END FILE site_toDo_deploy.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_toDo_deploy.jsp#2 $$Change: 651448 $--%>
