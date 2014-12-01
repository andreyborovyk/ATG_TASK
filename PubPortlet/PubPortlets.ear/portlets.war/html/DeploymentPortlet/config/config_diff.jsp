<!-- BEGIN FILE config_diff.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
 <portlet:defineObjects/>
 <!-- begin content -->
  
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
   <tr>
    <td class="blankSpace">&nbsp;</td>
    <td><h2>        <fmt:message key="site-config-changes" bundle="${depBundle}"/></h2></td>
      <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>  
    </tr>
  </table>
  </div>
    <c:set var="primaryDef" value="${targetDef.surrogateFor}"/>


    <div id="nonTableContent" style="padding-top: 0px;">
       <table border="0" cellpadding="5" cellspacing="0">
   <tr>
   <td>&nbsp;</td>
   <td width="20">&nbsp;</td>
   <td>&nbsp;</td>
   </tr>
   <tr>
   <td class="verticalAligned diffDate"><h5><fmt:message key="your-version" bundle="${depBundle}">Your Version</fmt:message></h5></td>
   <td>&nbsp;</td>
   <td class="verticalAligned diffDate"><h5><fmt:message key="live-version" bundle="${depBundle}">Live Version</fmt:message></h5></td>
   </tr>

   <tr>
   <td colspan="3" class="sep">&nbsp;</td>
   </tr>

    <tr>
      <td class="verticalAligned">
        <h5>
        <c:if test='${!(targetDef.displayName eq primaryDef.displayName)}'>
          <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
        </c:if>
      <fmt:message key="site-name" bundle="${depBundle}"/></h5><p> <c:out value="${targetDef.displayName}"/> </p></td>
      <td>&nbsp;</td>
      <td class="verticalAligned"><h5>
        <fmt:message key="site-name" bundle="${depBundle}"/></h5><p>
         <c:out value="${primaryDef.displayName}"/> </p>
      </td>
   </tr>
   
   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>
 
   <tr>
    
      <td class="verticalAligned">
        <h5>
        <c:if test='${!(targetDef.description eq primaryDef.description)}'>
        <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
        </c:if>
 
        <fmt:message key="description" bundle="${depBundle}"/>  </h5><p> 
        <c:set var="countAndTargetId" value="${targetDef.ID}1"/>
        <c:out value="${targetDef.description}"/> 
    </p>
   </td>    
      <td>&nbsp;</td>
       
      <td class="verticalAligned">
         <h5>
        <fmt:message key="description" bundle="${depBundle}"/>  </h5><p> 
       <c:set var="countAndTargetId" value="${targetDef.ID}2"/>
        <c:out value="${primaryDef.description}"/> 
        </p>
   </td>
  </tr>  

  <dspel:importbean scope="request" var="deploymentServer" bean="/atg/epub/DeploymentServer"/>
  <c:if test="${deploymentServer.useDafDeployment}">
    <tr>
      <td colspan="3" class="sep">&nbsp;</td>
    </tr>

    <tr>
      <td class="verticalAligned">
        <h5>
          <c:if test='${!(targetDef.destinations eq primaryDef.destinations)}'>
            <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
          </c:if>
          <fmt:message key="repository-mappings" bundle="${depBundle}"/>
        </h5>
        <p> 
          <c:forEach items="${targetDef.destinations}" var="mapping">
            <c:out value="${mapping.key}"/><b>&nbsp;:&nbsp;</b><c:out value="${mapping.value}"/><br>
          </c:forEach>
        </p>
      </td>    
      <td>&nbsp;</td>
       
      <td class="verticalAligned">
        <h5>
          <fmt:message key="repository-mappings" bundle="${depBundle}"/>
        </h5>
        <p> 
          <c:forEach items="${primaryDef.destinations}" var="mapping">
            <c:out value="${mapping.key}"/><b>&nbsp;:&nbsp;</b><c:out value="${mapping.value}"/><br>
          </c:forEach>
        </p>
      </td>
    </tr>
  </c:if>

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

   <c:forEach var="agent" items="${targetDef.agents}">
   <c:set var="primaryAgent" value="${agent.surrogateFor}"/>
   
     <tr>
         <td class="verticalAligned">
       <h5>    
           <c:if test='${!(agent.displayName eq primaryAgent.displayName)}'>
            <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
           </c:if>
       <fmt:message key="agent-name" bundle="${depBundle}"/></h5><p>
        <c:out value="${agent.displayName}"/></p>
     </td>
     
      <td>&nbsp;</td>

     <td class="virticalAligned">
       <h5>    
       <fmt:message key="agent-name" bundle="${depBundle}"/></h5><p>

        <c:out value="${primaryAgent.displayName}"/></p>
     </td>
  </tr>

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

  <tr>  
  <td class="virticalAligned">
      <h5>
        <c:if test='${!(agent.transport.transportType eq primaryAgent.transport.transportType)}'>
        <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
       </c:if>
        <fmt:message key="transport-type" bundle="${depBundle}"/></h5><p>
       <c:out value="${agent.transport.transportType}"/></p>
  </td>

      <td>&nbsp;</td>
  <td class="virticalAligned">

       <h5> <fmt:message key="transport-type" bundle="${depBundle}"/></h5><p>
   <c:out value="${primaryAgent.transport.transportType}"/></p>
  </td>

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

  <tr>  
     <td class="virticalAligned">
     <h5>
     <c:if test='${!(agent.principalAssets eq primaryAgent.principalAssets)}'>
     <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
    </c:if>
     
      <fmt:message key="role" bundle="${depBundle}"/></h5><p>
        <c:forEach items="${agent.principalAssets}" var="pAsset">
        <c:if test="${pAsset eq 'VFS'}">        <fmt:message key="principal-vfs" bundle="${depBundle}"/></c:if>
        <c:if test="${pAsset eq 'REPOSITORY'}">        <fmt:message key="principal-repository" bundle="${depBundle}"/></c:if>
        <c:if test="${pAsset eq 'NONE'}">        <fmt:message key="principal-none" bundle="${depBundle}"/></c:if>
        <c:if test="${pAsset eq 'ALL'}">        <fmt:message key="principal-all" bundle="${depBundle}"/></c:if>
        </c:forEach>
        </p>
  </td>
  <td>&nbsp;</td>
  <td class="virticalAligned">
     <h5> <fmt:message key="role" bundle="${depBundle}"/></h5><p>
   <c:forEach items="${primaryAgent.principalAssets}" var="pAsset">
     <c:if test="${pAsset eq 'VFS'}">        <fmt:message key="principal-vfs" bundle="${depBundle}"/></c:if>
     <c:if test="${pAsset eq 'REPOSITORY'}">        <fmt:message key="principal-repository" bundle="${depBundle}"/></c:if>
     <c:if test="${pAsset eq 'NONE'}">        <fmt:message key="principal-none" bundle="${depBundle}"/></c:if>
     <c:if test="${pAsset eq 'ALL'}">        <fmt:message key="principal-all" bundle="${depBundle}"/></c:if>
   </c:forEach></p>
 
  </td>
  
  </tr>

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

  <tr>
  <c:remove var="priTranURI"/>
  <c:remove var="tranURI"/>
  <c:if test="${primaryAgent.transport.transportType eq 'RMI'}">
   <c:set var="priTranURI" value='${primaryAgent.transport.rmiURI}'/>
  </c:if>
  <c:if test="${primaryAgent.transport.transportType eq 'JNDI'}">
   <c:set var="priTranURI" value='${primaryAgent.transport.jndiName}'/>
  </c:if>
 <c:if test="${agent.transport.transportType eq 'RMI'}">
  <c:set var="tranURI" value='${agent.transport.rmiURI}'/>
 </c:if>
 <c:if test="${agent.transport.transportType eq 'JNDI'}">
  <c:set var="tranURI" value='${agent.transport.jndiName}'/>
 </c:if>
  
  <td class="virticalAligned">
 <h5>
  <c:if test='${!(tranURI eq priTranURI)}'>
  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
 </c:if>
 
  <fmt:message key="transport-url" bundle="${depBundle}"/></h5><p>
     <c:out value="${tranURI}"/></p>
  </td>
 
  <td>&nbsp;</td>
  <td class="virticalAligned">
  <h5>
  <fmt:message key="transport-url" bundle="${depBundle}"/></h5><p>
  <c:out value="${priTranURI}"/>
  </td>
 </tr>
 
   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

    <tr>
       <td class="virticalAligned">
       <h5>
       <c:if test='${!(agent.includeAssetDestinations eq primaryAgent.includeAssetDestinations)}'>
       <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
      </c:if>
       
       <fmt:message key="include-destinations" bundle="${depBundle}"/></h5><p>
                <c:if test="${agent.includeAssetDestinations['empty']}"><fmt:message key="none" bundle="${depBundle}"/></c:if>
        <c:forEach var="asset" items="${agent.includeAssetDestinations}" varStatus="st"> 
        <c:out value="${asset}"/><br/>
        </c:forEach>
        </p>
       </td>
      <td>&nbsp;</td>
       <td class="virticalAligned">
       <h5>    <fmt:message key="include-destinations" bundle="${depBundle}"/></h5><p>
         <c:if test="${primaryAgent.includeAssetDestinations['empty']}"><fmt:message key="none" bundle="${depBundle}"/></c:if>       
      <c:forEach var="asset" items="${primaryAgent.includeAssetDestinations}" varStatus="st">  
        <c:out value="${asset}"/><br/>
      </c:forEach>
    </p>
      </td>
      
      </tr>       

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

    <tr>
       <td class="virticalAligned">
       <h5>
       <c:if test='${!(agent.destinationMap eq primaryAgent.destinationMap)}'>
       <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
      </c:if>
       
       <fmt:message key="destination-map" bundle="${depBundle}"/></h5><p>
                <c:if test="${agent.destinationMap['empty']}"><fmt:message key="none" bundle="${depBundle}"/></c:if>
        <c:forEach var="entry" items="${agent.destinationMap}" varStatus="st"> 
        <c:out value="${entry.key}"/>-><c:out value="${entry.value}"/><br/>
        </c:forEach>
        </p>
       </td>
      <td>&nbsp;</td>
       <td class="virticalAligned">
       <h5>    <fmt:message key="destination-map" bundle="${depBundle}"/></h5><p>
         <c:if test="${primaryAgent.destinationMap['empty']}"><fmt:message key="none" bundle="${depBundle}"/></c:if>       
      <c:forEach var="entry" items="${primaryAgent.destinationMap}" varStatus="st">  
        <c:out value="${entry.key}"/>-><c:out value="${entry.value}"/><br/>
      </c:forEach>
    </p>
      </td>
      
      </tr>       

   <tr>
  <td colspan="3" class="sep">&nbsp;</td>
  </tr>

    <tr>       
         
      <td class="virticalAligned">
      <h5>
      <c:if test='${!(agent.excludeAssetDestinations eq primaryAgent.excludeAssetDestinations)}'>
      <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" alt='<fmt:message key="different" bundle="${depBundle}"/>' width="7" height="8" border="0" />
     </c:if>
      
      <fmt:message key="exclude-destinations" bundle="${depBundle}"/></h5><p>
    <c:if test="${agent.excludeAssetDestinations['empty']}">    <fmt:message key="none" bundle="${depBundle}"/></c:if>
    <c:forEach var="asset" items="${agent.excludeAssetDestinations}" varStatus="st"> 
    <c:out value="${asset}"/><br/>
    </c:forEach></p>
    </td>
      <td>&nbsp;</td>
      <td class="virticalAligned">
      <h5>    <fmt:message key="exclude-destinations" bundle="${depBundle}"/></h5><p>
       <c:if test="${primaryAgent.excludeAssetDestinations['empty']}">    <fmt:message key="none" bundle="${depBundle}"/></c:if>       
    <c:forEach var="asset" items="${primaryAgent.excludeAssetDestinations}" varStatus="st">    
    <c:out value="${asset}"/><br/>
    </c:forEach></p>
      </td>   
       
</tr>
  </c:forEach> 
  </table>
    
  </div>

  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
  <td class="blankSpace" width="100%">&nbsp;</td>
  <td class="blankSpace"></td>
  </tr>
  </table>
  </div>
  
  </td>
  </tr>
  </table>
 <!-- end content -->

</dspel:page>   
<!-- END FILE config_diff.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_diff.jsp#2 $$Change: 651448 $--%>
