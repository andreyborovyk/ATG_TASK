<!-- BEGIN FILE config_add_site.jsp -->
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
 
 <tr>
 <td width="100%">


<portlet:renderURL var="addSiteURL"> 
       <portlet:param name="atg_admin_menu_group" value="deployment"/>  
       <portlet:param name="atg_admin_menu_1" value="configuration"/>  
       <portlet:param name="add_site" value="true"/>  
       <portlet:param name="config_tab_name" value="details"/>  
</portlet:renderURL> 

<c:set var="targetDisplayName" value=""/>
<c:set var="description" value=""/>
<c:set var="flagAgents" value="false"/>
<c:set var="targetOneOff" value="false"/>
<c:if test="${!empty atgSession.targetDef}">
  <c:set var="targetDef" value="${atgSession.targetDef}" />
  <c:set var="targetDisplayName" value="${targetDef.displayName}"/>
  <c:set var="description" value="${targetDef.description}"/>
  <c:set var="flagAgents" value="${targetDef.flagAgents}"/>
  <c:set var="targetOneOff" value="${targetDef.oneOff}"/>
</c:if>

  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
  <td class="blankSpace">&nbsp;</td>
  <td><h2>        <fmt:message key="adding-new-site" bundle="${depBundle}"/></h2></td>
                  <portlet:renderURL var="thisPageURL"> 
                    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                    <portlet:param name="add_site" value="true"/>  
                 </portlet:renderURL> 
    <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>  
 </tr>
  </table>
  </div>
   
 <dspel:form action="${thisPageURL}" method="post" id="addSiteForm"> 
 
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview">
  <tr>
    <td>        

    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
    <td>
      <table border="0" cellpadding="0" cellspacing="3">
      <tr>
        <td class="formLabel"><label for="anotherEnumeration">        
         <fmt:message key="site-name" bundle="${depBundle}"/>:</label>
        </td>
        <td class="formPadding">
          <dspel:input iclass="formElement" bean="${topologyFormHandlerName}.targetDisplayName" type="text" value="${targetDisplayName}"/>           
        </td>
           </tr>
       </table>

       </td>
       </tr>
       <tr>
       <td>     
           
           
 <pws:getTargetDefs var="selectTargetDefs" topologyType="primary"/> 
  <dspel:importbean var="versionManager" bean="/atg/epub/version/VersionManagerService"/>
    
        <table border="0" cellpadding="0" cellspacing="3">
        <tr>
          <td class="formLabel"><label for="anotherEnumeration">        
            <fmt:message key="site-init-options" bundle="${depBundle}"/>:</label>
          </td>
          <td class="formLabelNoBorderGray formPadding"> 
                <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.flagAgents" value="true" checked="${flagAgents}"/>
            <fmt:message key="flag-agents" bundle="${depBundle}"/><br/>
                <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.flagAgents" value="false" checked="${!flagAgents}"/>
           <fmt:message key="no-flag-agents" bundle="${depBundle}"/> 
            </td>
            </tr>
            </table>
            
            </td>
            </tr>

     
     <tr>
     <td>

     <table border="0" cellpadding="0" cellspacing="3">
       <tr>
         <td class="formLabel"><label for="description">        
             <fmt:message key="site-type" bundle="${depBundle}"/>:
           </label>           
         </td>
         <td class="formLabelNoBorderGray formPadding"> 
           <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.targetOneOff" value="false" checked="${!targetOneOff}" />
           <fmt:message key="workflow-target-site" bundle="${depBundle}"/> 
           <br/> 
           <dspel:input type="radio" iclass="radio" bean="${topologyFormHandlerName}.targetOneOff" value="true" checked="${targetOneOff}" />
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
  <dspel:textarea bean="${topologyFormHandlerName}.description"rows="8" cols="100" iclass="propertyEdit" default="${description}"></dspel:textarea>
        
    </td>
    </tr>
 </table>
 
 </td>
 </tr>
 </table>
  </td>
  </tr>
  </table>

        
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.initFromBranchName" value="${versionManager.mainBranch.name}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${sitesURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${thisPageURL}"/>
    <c:if test="${empty atgSession.targetDef}">
      <dspel:input type="hidden" bean="${topologyFormHandlerName}.addTarget" value="foo" priority="-1"/>
    </c:if>
    <c:if test="${!empty atgSession.targetDef}">
      <dspel:input type="hidden" bean="${topologyFormHandlerName}.targetID" value="${targetDef.ID}"/>
      <dspel:input type="hidden" bean="${topologyFormHandlerName}.updateTarget" value="foo" priority="-1"/>
    </c:if>
    </div>
  </td>
  </tr>
  </table>
  
  <dspel:importbean scope="request" var="deploymentServer" bean="/atg/epub/DeploymentServer"/>
  <c:if test="${deploymentServer.useDafDeployment}">
    <c:set var="createTarget" value="${empty atgSession.targetDef}" />
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
  <td class="buttonImage">
    <a href="javascript:updateTarget('addSiteForm');" class="mainContentButton save" onmouseover="status='';return true;">        
      <c:if test="${empty atgSession.targetDef}">
        <fmt:message key="save-changes" bundle="${depBundle}"/>
      </c:if>    
      <c:if test="${!empty atgSession.targetDef}">
        Update
      </c:if>    
    </a>
  </td>   
  <td class="buttonImage"><a href="<c:out value='${addSiteURL}'/>" class="mainContentButton delete" onmouseover="status='';return true;">        <fmt:message key="cancel" bundle="${depBundle}"/></a></td>  
 
  <td class="blankSpace"></td>
  </tr>
  </table>
  </div>
  
 <!-- end content -->
 
</dspel:page>   
<!-- END FILE config_add_site.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_add_site.jsp#2 $$Change: 651448 $--%>
