<%@ page import="javax.portlet.*,java.util.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>



<!-- Begin ProjectsPortlet's projectAssets.jsp -->

<dspel:page>


<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<fmt:message var="addAssetButton" key="add-asset-button" bundle="${projectsBundle}"/>
<fmt:message var="discardAssetButton" key="discard-asset-button" bundle="${projectsBundle}"/>

<dspel:importbean bean="/atg/epub/servlet/ProjectFormHandler" var="projectFormHandler"/>

<dspel:importbean bean="/atg/epub/servlet/ProjectDiffFormHandler" var="diffFormHandler"/>

<portlet:defineObjects/>

<portlet:actionURL  var="actionURL"/>

<%@ include file="projectConstants.jspf" %>

<%-- GET THE CURRENT PROJECT & ID --%>
<pws:getCurrentProject var="projectContext"/>
<c:set var="project" value="${projectContext.project}"/>

<c:if test="${project ne null}">

<c:set var="currentProjectId" value="${project.id}"/>

<c:url var="assetBrowserURL" context="/PubPortlets" value="/html/ProjectsPortlet/assetBrowser.jsp">
  <c:param name="projectView" value="7"/>
</c:url>

<dspel:getvalueof var="userListSize" bean="/atg/userprofiling/Profile.defaultListing" scope="request"/>
<c:set var="listIndex" value="${0}"/>
<c:if test="${param.assetIdx ne null}"><c:set var="listIndex" value="${param.assetIdx}"/></c:if>
<c:set var="rangeEnd" value="${listIndex + userListSize}"/>

<%-- Handle conflicts by submitting a form that does the diff and switches the view --%>
<script language="JavaScript">

  // URLs to bring user to various processes
  var projectURLs = new Object();

  function handleConflict(assetURI, workspace, latestVersion) {
    document.getElementById("conflictAssetURI").value = assetURI;
    document.getElementById("conflictWorkspace").value = workspace;
    document.getElementById("conflictVersion").value = latestVersion;
    document.getElementById("assetConflictForm").submit();
  }
  // pje: removed 'deleteAssetAction' from init() below since it was
  // causing JavaScript errors on load
  registerOnLoad( function() { init('browser'); resizeAssetBrowser() } );
  registerOnResize( function() { resizeAssetBrowser(); } );
  
  //
  // Invoke Asset Picker iFrame with proper contents
  //
  function showAssetPicker() {
    var assetTypesList = new Array( 0 );

    <pws:getVersionedAssetTypes var="assetTypes">
      <c:forEach items="${assetTypes}" var="result">

        <c:forEach items="${result.types}" var="type">
          <c:choose>
            <c:when test="${result.repository}">
              <c:set var="typeCode" value="repository"/>
            </c:when>
            <c:when test="${result.fileSystem}">
              <c:set var="typeCode" value="file"/>
            </c:when>
          </c:choose>

          var assetType = new Object();
          assetType.typeCode            = '<c:out value="${typeCode}"/>';
          assetType.displayName         = '<c:out value="${type.displayName}"/>';
          assetType.displayNameProperty = 'displayName';
          assetType.typeName            = '<c:out value="${type.typeName}"/>';
          assetType.repositoryName      = '<c:out value="${result.repositoryName}"/>';
          assetType.componentPath       = '<c:out value="${result.componentPath}"/>';
          assetType.createable          = 'true';

          // DL: this may no longer be necessary
          assetType.encodedType = 
            assetType.componentPath + ":" + assetType.typeName;

          <c:if test="${result.repository}">
            <c:if test="${ !empty type.itemDescriptor.itemDisplayNameProperty}">
                assetType.displayNameProperty = 
                  '<c:out value="${type.itemDescriptor.itemDisplayNameProperty}"/>';
            </c:if>
          </c:if>
          assetTypesList[ assetTypesList.length ] = assetType;
        </c:forEach>
      </c:forEach>
    </pws:getVersionedAssetTypes>

    var picker = new AssetBrowser();
    picker.browserMode        = "project";
    picker.isAllowMultiSelect = "true";
    picker.createMode         = "redirect";
    picker.closeAction        = "refresh";
    picker.assetTypes         = assetTypesList;
    picker.formHandler        = "/atg/epub/servlet/ProjectFormHandler";
    picker.formHandlerAction  = "1";
    picker.assetInfoPath      = "/atg/epub/servlet/ProcessAssetInfo";
    picker.defaultView        = "Search";
    picker.invoke();
  }
</script>

<%--
  Create a URL for the asset browser to use to redirect to
  assetEditorPage.jsp when an asset is created.
 --%>

<portlet:renderURL var="assetBrowserCreateURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("NEW_ASSET_VIEW")%>'/>
  <portlet:param name="clearAttributes" value="true"/>
</portlet:renderURL>


<c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
<c:set var="projectAssetURI" value="${assetInfo.context.attributes.assetURI}"/>

<c:set target="${assetInfo.attributes}" 
  property="assetBrowserCreateURL" value="${assetBrowserCreateURL}"/>

<c:set var="ATGSessionPath" value="/atg/web/ATGSession"/>
<dspel:importbean var="ATGSession" bean="${ATGSessionPath}"/>
<c:set target="${ATGSession}" property="assetBrowserCreateURL"
  value="${assetBrowserCreateURL}"/>

<c:set value="true" scope="session"
  var="assetBrowserAllowCreate"/>
<c:set value="${currentProjectId}" scope="request"
  var="assetBrowserProjectId"/>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <td><h2><fmt:message key="asset-list-title" bundle="${projectsBundle}"/></h2></td>
      <%-- FORM ERRORS --%>
      <td class="alertMessage">
        <c:forEach var="ex" items="${projectFormHandler.formExceptions}">
          <span style="color: #FF0000"><c:out value="${ex.message}"/></span><br />
        </c:forEach>
        <c:if test="${projectFormHandler.confirmationMessage ne null}">
	   <span style="color: blue"><c:out value="${projectFormHandler.confirmationMessage}"/></span><br />      
        </c:if>
      </td>
     
      <td class="toolBar_button">
        <c:if test="${project.editable}">
          <a href="javascript:showAssetPicker()" class="add" onmouseover="status='';return true;">
            <img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_addAsset.gif"/>' alt='<c:out value="${addAssetButton}"/>' width="16" height="16" />&nbsp;
            <c:out value="${addAssetButton}"/>
           </a>
        </c:if>
      </td>
    </tr>
  </table>
</div>

  <pws:getProjectAssets var="assetResults" projectId="${currentProjectId}" 
                        checkedIn="${project.checkedIn}" index="${listIndex}" 
   count="${userListSize}"/>
  <c:set var="projectAssets" value="${assetResults.assets}"/>


<c:set var="noAssets" value="${assetResults.size eq 0}"/>

<%-- ASSET LIST TABLE --%>
<table cellpadding="0" cellspacing="0" border="0" class="dataTable">

<dspel:form id="actionForm" formid="actionForm" action="${actionURL}" method="post">

 <dspel:input id="assetAction" type="hidden" bean="ProjectFormHandler.assetAction" 
                   value="2" priority="-1"/>
                   
 <dspel:input type="hidden" bean="ProjectFormHandler.performAssetAction" 
                  value="1" priority="-10"/>
 
<%-- PAGING LINKS --%>
<c:if test="${assetResults.size gt userListSize}">
<tr><td align="right" colspan="6">
<c:choose>
  <c:when test="${assetResults.size gt rangeEnd}">
    <p><span class="tableInfo">
    <fmt:message key="range-label" bundle="${projectsBundle}">
      <fmt:param value="${listIndex + 1}"/>
      <fmt:param value="${rangeEnd}"/>
      <fmt:param value="${assetResults.size}"/>
    </fmt:message>&nbsp;&nbsp;&nbsp;
    </span>
    <%-- PREV --%>
    <c:choose>
     <c:when test="${listIndex gt 0}">
      <c:set var="prevIndex" value="${listIndex-userListSize}"/>
      <% String prevIndexParam = pageContext.getAttribute("prevIndex").toString(); %>
      <portlet:renderURL  var="prevRangeURL">
        <portlet:param name="assetIdx" value="<%=prevIndexParam%>"/>
      </portlet:renderURL>
      <a href='<c:out value="${prevRangeURL}"/>' onmouseover="status='';return true;"><fmt:message key="prev-link" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;
     </c:when>
     <c:otherwise>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     </c:otherwise>
    </c:choose>
    <%-- NEXT --%>
    <% String nextIndexParam = pageContext.getAttribute("rangeEnd").toString(); %>
    <portlet:renderURL  var="nextRangeURL">
      <portlet:param name="assetIdx" value="<%=nextIndexParam%>"/>
    </portlet:renderURL>
    <a href='<c:out value="${nextRangeURL}"/>' onmouseover="status='';return true;"><fmt:message key="next-link" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;&nbsp;
    </p>
  </c:when>
  <c:otherwise>
    <p><span class="tableInfo">
    <fmt:message key="range-label" bundle="${projectsBundle}">
      <fmt:param value="${listIndex + 1}"/>
      <fmt:param value="${assetResults.size}"/>
      <fmt:param value="${assetResults.size}"/>
    </fmt:message>&nbsp;&nbsp;&nbsp;
    </span>
    <%-- PREV --%>
    <c:if test="${listIndex gt 0}">
    <c:set var="prevIndex" value="${listIndex-userListSize}"/>
    <% String prevIndexParam = pageContext.getAttribute("prevIndex").toString(); %>
    <portlet:renderURL  var="prevRangeURL">
      <portlet:param name="assetIdx" value="<%=prevIndexParam%>"/>
    </portlet:renderURL>
    <a href='<c:out value="${prevRangeURL}"/>' onmouseover="status='';return true;"><fmt:message key="prev-link" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    </c:if>
    </p>
  </c:otherwise>
</c:choose>
</td></tr>
</c:if>

<%-- ASSET COLUMN HEADERS --%>
  <tr>
      <th class="centerAligned">
        <c:if test="${project.editable}">
          <span class="tableHeader"><img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/checkMark1.gif"/>' alt="select multiple column" width="7" height="8" border="0" /></span>
        </c:if>&nbsp;
      </th>
    <th class="leftAligned" style="width: 100%;"><span class="tableHeader"><fmt:message key="asset-name" bundle="${projectsBundle}"/></span></th>
    <th class="centerAligned"><span class="tableHeader"><fmt:message key="asset-type" bundle="${projectsBundle}"/></span></th>
    <th class="centerAligned"><span class="tableHeader"><fmt:message key="base-version" bundle="${projectsBundle}"/></span></th>
    <th class="centerAligned"><span class="tableHeader"><fmt:message key="latest-version" bundle="${projectsBundle}"/></span></th>
    <%-- Don't have lastModified date
    <th class="centerAligned"><span class="tableHeader"><fmt:message key="last-modified" bundle="${projectsBundle}"/></span></th>
    --%>
    <th class="centerAligned"><span class="tableHeader"><fmt:message key="asset-status-header" bundle="${projectsBundle}"/></span></th>
  </tr>



<c:choose>
<c:when test="${!noAssets && !project.checkedIn}">

  <dspel:input type="hidden" bean="ProjectFormHandler.projectId" value="${currentProjectId}"/>

  <c:forEach var="assetVersion" items="${projectAssets}" varStatus="loopInfo">
    <c:set var="assetURI" value="${assetVersion.asset.URI}"/>
    <c:set var="workspace" value="${project.workspace}"/>
    <portlet:renderURL var="assetEditURL">
      <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_EDIT_VIEW")%>'/>
      <portlet:param name="assetURI" value='<%=((atg.versionmanager.VersionManagerURI)pageContext.getAttribute("assetURI")).toString()%>'/>
      <portlet:param name="workspace" value='<%= (String) pageContext.getAttribute("workspace") %>' />
      <portlet:param name="clearContext" value="true" />
      <portlet:param name="clearAttributes" value="true" />
    </portlet:renderURL>
<%-- ASSET TABLE --%>
   <!-- tr  - Highlight if in conflict, alternate backgrounds otherwise -->
   <c:choose>
      <c:when test="${!assetVersion.upToDate}"><tr style="background: #FFD283;"></c:when>
      <c:when test="${loopInfo.count % 2 == 0}"><tr class="alternateRowHighlight"></c:when>
      <c:otherwise><tr></c:otherwise>
   </c:choose>
      <td>
        <c:if test="${project.editable}">

          <%-- Even if the item is not a secured repository item, you can pass it into this droplet
               and the 'true' oparam will be rendered. This keeps you from having to check if the
               item is secure or not before you use this droplet --%>
          <dspel:droplet name="/atg/repository/secure/HasItemAccess">
            <dspel:param name="secureItem" value="${assetVersion.repositoryItem}"/>
            <dspel:param name="right" value="write"/>
            <dspel:oparam name="true">
              <dspel:input iclass="checkbox" type="checkbox" bean="ProjectFormHandler.assets" value="${assetURI}"/>
            </dspel:oparam>
            <dspel:oparam name="false">
              <dspel:input iclass="checkbox" type="checkbox" bean="ProjectFormHandler.assets" value="${assetURI}" disabled="true"/>
            </dspel:oparam>
            <dspel:oparam name="error">
              <dspel:input iclass="checkbox" type="checkbox" bean="ProjectFormHandler.assets" value="${assetURI}" disabled="true"/>
            </dspel:oparam>
          </dspel:droplet>

        </c:if>
     </td>
      <td class="leftAligned"><span class="tableInfo">
        <c:choose>
          <c:when test="${ ! empty assetVersion.virtualFile }">
            <c:set var="displayName" value="${assetVersion.virtualFile.absolutePath}"/>        
          </c:when>
          <c:otherwise>
            <c:set var="displayName" value="${assetVersion.repositoryItem.itemDisplayName}"/>        
          </c:otherwise>
        </c:choose>
        <c:choose>
          <c:when test="${!assetVersion.deleted}">
            <a href='<c:out value="${assetEditURL}"/>' onmouseover="status='';return true;">
              <c:out value="${displayName}"/>
            </a>
          </c:when>
          <c:otherwise>
            <c:out value="${displayName}"/>
          </c:otherwise>
        </c:choose>
      </span></td>
      <td class="rightAligned nowrap"><span class="tableInfo"><c:out value="${assetVersion.repositoryItem.itemDescriptor.itemDescriptorName}"/></span></td>
      <td class="centerAligned"><span class="tableInfo"><c:out value="${assetVersion.parentVersion.version}"/></span></td>
   <c:choose>
      <c:when test="${assetVersion.upToDate}">
        <td class="centerAligned"><span class="tableInfo"><c:out value="${assetVersion.version}"/></span></td>
      </c:when>
      <c:otherwise>
        <td nowrap class="centerAligned noWrap">
          <span class="tableInfo">
          <a href='javascript:handleConflict("<c:out value="${assetURI}"/>", "<c:out value="${workspace}"/>", "<c:out value="${assetVersion.asset.mainVersion.version}"/>");' onmouseover="status='';return true;">
            <fmt:message key="asset-merge-link" bundle="${projectsBundle}"/>&nbsp;&raquo;
          </a>
          </span>
        </td>
      </c:otherwise>
   </c:choose>
      <%-- Don't have lastModified date
      <td><span class="tableInfo">&nbsp;</span></td>
      --%>
      <%-- ASSET STATUS --%>
      <c:choose>
        <c:when test="${assetVersion.deleted}">
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-deleted" bundle="${projectsBundle}"/></span></td>
 </c:when>
        <c:when test="${assetVersion.added}">
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-created" bundle="${projectsBundle}"/></span></td>
 </c:when>
 <c:otherwise>
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-modified" bundle="${projectsBundle}"/></span></td>
 </c:otherwise>
      </c:choose>
    </tr>
  </c:forEach>

</c:when>

<c:when test="${!noAssets && project.checkedIn}">

  <dspel:input type="hidden" bean="ProjectFormHandler.projectId" value="${currentProjectId}"/>

  <c:forEach var="assetVersion" items="${projectAssets}" varStatus="loopInfo">
    <c:set var="assetURI" value="${assetVersion.asset.URI}"/>
    <c:set var="workspace" value="${project.workspace}"/>
    <portlet:renderURL var="assetEditURL">
      <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_EDIT_VIEW")%>'/>
      <portlet:param name="assetURI" value='<%=((atg.versionmanager.VersionManagerURI)pageContext.getAttribute("assetURI")).toString()%>'/>
      <portlet:param name="workspace" value='<%= (String) pageContext.getAttribute("workspace") %>' />
      <portlet:param name="clearContext" value="true" />
      <portlet:param name="clearAttributes" value="true" />
    </portlet:renderURL>
<%-- ASSET TABLE --%>
   <!-- tr  - Highlight if in conflict, alternate backgrounds otherwise -->
   <c:choose>
      <c:when test="${loopInfo.count % 2 == 0}"><tr class="alternateRowHighlight"></c:when>
      <c:otherwise><tr></c:otherwise>
   </c:choose>
      <td>
        <c:if test="${project.editable}">
   <dspel:input iclass="checkbox" type="checkbox" bean="ProjectFormHandler.assets" value="${assetURI}"/>
        </c:if>&nbsp;
      </td>
      <td class="leftAligned">
        <span class="tableInfo">
          <c:choose>
            <c:when test="${ ! empty assetVersion.virtualFile }">
              <c:set var="displayName" value="${assetVersion.virtualFile.absolutePath}"/>        
            </c:when>
            <c:otherwise>
              <c:set var="displayName" value="${assetVersion.repositoryItem.itemDisplayName}"/>        
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${!assetVersion.deleted}">
              <a href='<c:out value="${assetEditURL}"/>' onmouseover="status='';return true;">
                <c:out value="${displayName}"/>
              </a>
            </c:when>
            <c:otherwise>
              <c:out value="${displayName}"/>
            </c:otherwise>
          </c:choose>
        </span>
      </td>
      <td class="rightAligned nowrap"><span class="tableInfo"><c:out value="${assetVersion.repositoryItem.itemDescriptor.itemDescriptorName}"/></span></td>
      <td class="centerAligned"><span class="tableInfo"><c:out value="${assetVersion.parentVersion.version}"/></span></td>
        <td class="centerAligned"><span class="tableInfo"><c:out value="${assetVersion.version}"/></span></td>
      <%-- Don't have lastModified date
      <td><span class="tableInfo">&nbsp;</span></td>
      --%>
      <%-- ASSET STATUS --%>
      <c:choose>
        <c:when test="${assetVersion.deleted}">
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-deleted" bundle="${projectsBundle}"/></span></td>
 </c:when>
        <c:when test="${assetVersion.parentVersion eq null}">
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-created" bundle="${projectsBundle}"/></span></td>
 </c:when>
 <c:otherwise>
           <td class="centerAligned"><span class="tableInfo"><fmt:message key="asset-status-modified" bundle="${projectsBundle}"/></span></td>
 </c:otherwise>
      </c:choose>
    </tr>
  </c:forEach>

</c:when>
<c:otherwise>
   <tr><td colspan="7" class="centerAligned error"><fmt:message key="no-assets-message" bundle="${projectsBundle}"/></td></tr>
</c:otherwise>
</c:choose>

</table>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
         
        <td class="paddingLeft" width="100%">
             &nbsp;<%-- remove asset move functionality for the 2007.0 release
             <fmt:message key="move-assets" bundle="${projectsBundle}"/>
    	                  
    	                  <pws:getProjects var="projects" sortProperties="displayName">
    	                    <dspel:select bean="ProjectFormHandler.moveAssetsProjectId" id="gotoProjectId" priority="10">
    	                      <dspel:option value=""><fmt:message key="select-option-selectone" bundle="${projectsBundle}"/>:</dspel:option>
    	                      <c:forEach items="${projects.projects}" var="Project">
    	                        <c:if test="${Project.id != currentProjectId }">
    	                        <c:if test="${Project.editable}">
    	                          <dspel:option value="${Project.id}">
    	                            <c:out value="${Project.displayName}"/> -
    	                            <dspel:tomap var="creator" value="${project.creator}"/>
    	                            - <c:out value="${creator.firstName} ${creator.lastName}"/>
    	                            (<fmt:formatDate value="${Project.creationDate}" 
    	                              type="BOTH" dateStyle="SHORT" timeStyle="SHORT"/>)
    	                          </dspel:option>
    	  
    	  
    	                          <script language="JavaScript">
    	                               projectURLs[ '<c:out value="${Project.id}"/>' ] = 
    	                                '/atg/bcc/process?projectId=<c:out value="${Project.id}"/>&projectView=3';
    	                            </script>
    	
    	                      
    	                        </c:if>
    	                        
    	                        </c:if>
    	                      </c:forEach>
    	                    </dspel:select>
    	                  </pws:getProjects>
    	                   
    	
    	                  <input type="checkbox" class="checkbox" id="toProcess" priority="10"/>
    	                  <label for="toProcess">
    	                    <fmt:message key="page-label-to-process" bundle="${projectsBundle}"/>:    &nbsp;&nbsp;
    	                  </label>
    	                  
    	                  <a href="javascript:submitProjectAction()" class="goButton" id="addToProces" onmouseover="status='';return true;"><fmt:message key="page-link-submit-project-form" bundle="${projectsBundle}"/></a>
             --%>       
        </td>
        
         <c:if test="${project.editable}">
          <td class="buttonImage">
            <a href='javascript:document.forms[0].submit();' class="mainContentButton remove" onmouseover="status='';return true;"><c:out value="${discardAssetButton}"/></a>
          </td>
          <td class="blankSpace">&nbsp;</td>
         </c:if>
    </tr>
  </table>
</div>



</dspel:form>  <%-- Discard Assets form --%>

<%-- Conflict detection form. This form hangs off the page and is used solely
     to submit data to the ProjectDiffFormHandler.
--%>
<dspel:form formid="assetConflictForm" id="assetConflictForm" action="${actionURL}" method="post">

  <%-- Call diffVersions on the form handler to handle the submit. --%>
  <dspel:input type="hidden" priority="-1" bean="ProjectDiffFormHandler.diffVersions" value="1"/>

  <%-- Set up the form. --%>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.viewAttribute" value="projectView"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.successView" value="${ASSET_CONFLICT_VIEW}"/>
  <dspel:input type="hidden" id="conflictAssetURI" bean="ProjectDiffFormHandler.assetURI"/>
  <dspel:input type="hidden" id="conflictWorkspace" bean="ProjectDiffFormHandler.workspaceName"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.indexedVersions[0]" value="${null}"/>
  <dspel:input type="hidden" id="conflictVersion" bean="ProjectDiffFormHandler.indexedVersions[1]"/>

</dspel:form> <%-- Conflict detection form --%>

</c:if>  <%-- project is not null --%>

<c:if test="${projectContext.project eq null}">
   <fmt:message key="no-current-project-message" bundle="${projectsBundle}"/>
</c:if>


    <script language="JavaScript">

     
      //
      // Button Actions
      //
      function submitProjectAction() {

        var form = document.getElementById( "actionForm" );
        var projectId = document.getElementById( "gotoProjectId" ).value;

        if (! projectId ) {
                alert( "<fmt:message key="no-process-selected" bundle="${projectsBundle}"/>" );
                return ;
         }
              
        var changeToProcess = document.getElementById("toProcess").checked;
       
   
        document.getElementById("assetAction").value ="5";
       
      
        if ( changeToProcess ) {
      	  form.action = projectURLs[projectId];
         }

        
        
        form.submit();

      }

   

    </script>
    
</dspel:page>
<!-- End ProjectsPortlet's projectAssets.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectAssets.jsp#2 $$Change: 651448 $--%>

