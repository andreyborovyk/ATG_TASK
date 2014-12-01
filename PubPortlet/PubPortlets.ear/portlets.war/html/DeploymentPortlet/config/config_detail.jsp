<!-- BEGIN FILE config_detail.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
 
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
  <td class="blankSpace">&nbsp;</td>
  <td><h2>        <fmt:message key="configuring-details" bundle="${depBundle}">
                                  <fmt:param value="${targetDef.displayName}"/>
                                </fmt:message>
                                
        </h2></td>
    <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>  
    </tr>
  </table>
  </div>
   
  <div id="adminDeployment">
                  <portlet:renderURL var="failureURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("target_def_id")+""%>'/>  
                    <portlet:param name="goto_config_details_tabs" value="true"/>  
                 </portlet:renderURL> 

    <c:choose>
    <c:when test="${!empty param.from_make_changes_live}">
              <portlet:renderURL var="successURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="make_changes_live" value="true"/>  
                 </portlet:renderURL> 
    </c:when>
    <c:otherwise>
                 <portlet:renderURL var="successURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("target_def_id")+""%>'/>  
                    <portlet:param name="goto_config_details_tabs" value="true"/>  
                 </portlet:renderURL> 
  </c:otherwise>
    </c:choose>
  <dspel:form method="post" id="editSiteForm" name="editSiteForm" action="${successURL}">
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview">
  <tr>
    <td>        
        <table border="0" cellpadding="0" cellspacing="0">
        <tr>
        <td>
          <table border="0" cellpadding="0" cellspacing="3">
          <tr>
            <td class="formLabel"><label for="anotherEnumeration">        
           <fmt:message key="site-name" bundle="${depBundle}"/>:
            </td>
            <td class="formPadding">
                <c:choose> 
                <c:when test="${targetDef.primary}">
                <dspel:input iclass="formElement" disabled="true" bean="${topologyFormHandlerName}.targetDisplayName" type="text" value="${targetDef.displayName}" />
                </c:when>
                <c:otherwise>
                <dspel:input iclass="formElement" bean="${topologyFormHandlerName}.targetDisplayName" type="text" value="${targetDef.displayName}" />
                </c:otherwise>
                </c:choose>
            </td>
         </tr>
          </table>
        
        </td>
        </tr>
        <tr>
        <td>     
        
    <c:if test="${empty targetDef.surrogateFor}">
    
  <pws:getTargetDefs var="selectTargetDefs" topologyType="primary"/> 
  <dspel:importbean var="versionManager" bean="/atg/epub/version/VersionManagerService"/>
    
    <c:set var="initializedTargetExists" value="false"/>
   <c:forEach var="primaryTarget" items="${selectTargetDefs}">
<!--
-->
    </c:forEach>
    
    
     <c:if test="${!targetDef.primary}">
        <table border="0" cellpadding="0" cellspacing="3">
        <tr>
          <td class="formLabel"><label for="anotherEnumeration">       
          
            <fmt:message key="site-init-options" bundle="${depBundle}"/>:</label>
             </td>
              <td class="formLabelNoBorderGray formPadding"> 
              <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.flagAgents" value="true" checked="${targetDef.flagAgents}" />
            <fmt:message key="flag-agents" bundle="${depBundle}"/> 
             <br/> 
              <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.flagAgents" value="false" checked="${!targetDef.flagAgents}"/>
             <fmt:message key="no-flag-agents" bundle="${depBundle}"/> 
              </td>
                        
           </tr>
        </table>
        </c:if>
        </td>
        </tr>
      
      <tr>
      <td>
 
   </c:if>
   
   <table border="0" cellpadding="0" cellspacing="3">
     <tr>
       <td class="formLabel"><label for="anotherEnumeration">        
           <fmt:message key="site-type" bundle="${depBundle}"/>:
         </label>           
       </td>
       <td class="formLabelNoBorderGray formPadding"> 
         <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.targetOneOff" value="false" checked="${!targetDef.oneOff}" disabled="${!empty targetDef.surrogateFor}"/>
         <fmt:message key="workflow-target-site" bundle="${depBundle}"/> 
         <br/> 
         <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.targetOneOff" value="true" checked="${targetDef.oneOff}" disabled="${!empty targetDef.surrogateFor}"/>
         <fmt:message key="one-off-target-site" bundle="${depBundle}"/> 
       </td>
     </tr>
   </table>
   
   <table border="0" cellpadding="0" cellspacing="3">
   <tr>
     <td class="formLabel"><label for="description">        
       <fmt:message key="site-description" bundle="${depBundle}"/>:
           </label>           
     </td>
     <td class="formPadding">
     <c:choose>
     <c:when test="${targetDef.primary}">
        <dspel:textarea disabled="true" bean="${topologyFormHandlerName}.description" rows="8" cols="100" iclass="propertyEdit" ><c:out value="${targetDef.description}"/></dspel:textarea>
     </c:when>
     <c:otherwise>
        <dspel:textarea bean="${topologyFormHandlerName}.description" rows="8" cols="100" iclass="propertyEdit" ><c:out value="${targetDef.description}"/></dspel:textarea>
     </c:otherwise>
     </c:choose>
     
     </td>
      </tr>
   </table>
   
   </td>
   </tr>

   </td>
   </tr>
   </table>
   
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.initFromBranchName}" value="${versionManager.mainBranch.name}" />
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.targetID" value="${targetDef.ID}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${successURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${failureURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.targetOneOff" value="${targetDef.oneOff}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.updateTarget" value="foo" priority="-1"/>

  </div>
  </td>
  </tr>
  </table>

  <dspel:importbean scope="request" var="deploymentServer" bean="/atg/epub/DeploymentServer"/>
  <c:if test="${deploymentServer.useDafDeployment}">
    <c:set var="createTarget" value="false" />
    <%@ include file="config_edit_mappings.jspf" %>
  </c:if>

  <script type="text/Javascript">
    function updateTarget(form)
    {
      var isDafDeployment = "<c:out value='${deploymentServer.useDafDeployment}' />";
      if (isDafDeployment == "true") {
        var editSiteForm = document.getElementById(form);
        editSiteForm.delimitedMappings.value = createDelimitedFromMap();
      }
      submitForm(form);
    }
  </script>

  </dspel:form>

  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
 <td class="blankSpace" width="100%">&nbsp;</td>
     <c:if test="${!targetDef.primary}">
 <td class="buttonImage"><a href="javascript:updateTarget('editSiteForm');" class="mainContentButton save" onmouseover="status='';return true;">    <fmt:message key="save-changes" bundle="${depBundle}"/></a></td>  
    </c:if>
  <td class="blankSpace"></td>
  </tr>
  </table>
  </div>
  
 <!-- end content -->
</dspel:page>  

<!-- END FILE config_detail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_detail.jsp#2 $$Change: 651448 $--%>
