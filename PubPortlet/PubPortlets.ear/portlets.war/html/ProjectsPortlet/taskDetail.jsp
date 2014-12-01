<%--
  Task Detail page

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/taskDetail.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page import="atg.workflow.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<!-- Begin ProjectsPortlet's taskDetail.jsp -->

<dspel:page xml="true">

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<dspel:importbean bean="/atg/web/ATGSession" var="atgSession"/> 
<%-- task Id is set in session by portlet class --%>
<c:set var="taskId" value="${atgSession.processTaskId}"/> 

<%-- 
  The AssetInfo component for these pages
--%>
<c:set var="assetInfoPath" scope="request"
  value="/atg/epub/servlet/ProcessAssetInfo"/>
<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

<%--
  Clear the context stack
--%>
<c:if test="${ 'true' == param.clearContext }">
  <c:set target="${assetInfo}" property="clearContext" value="true"/>
  <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
</c:if>

<%-- the URL to use to return to this task --%>
<portlet:actionURL var="taskURL">
  <portlet:param name="copyParams" value="true"/>
  <portlet:param name="projectView" 
    value='<%=(String)pageContext.getAttribute("PROJECT_TASK_DETAIL_VIEW")%>'/>
  <portlet:param name="taskId" 
    value='<%=(String)pageContext.getAttribute("taskId")%>'/>
</portlet:actionURL>

<%--
  The text, context action (backToURL) and context URL to use to 
  return to this page from the top-level created asset
--%>
<c:set target="${assetInfo.attributes}" property="backToText"
  value="back to task"/>
<c:set target="${assetInfo.attributes}" property="backToURL"
  value="javascript:popContext();"/>
<c:set target="${assetInfo.attributes}" property="firstAssetEditContextLevel"
  value="2"/>
<c:set target="${assetInfo.attributes}" property="contextActionURL"
  value="${taskURL}"/>

<c:set var="projectHandlerPath" value="/atg/epub/servlet/ProjectFormHandler"/>
<dspel:importbean bean="${projectHandlerPath}" var="projectFormHandler"/>
<dspel:importbean bean="/atg/epub/servlet/TaskActionFormHandler" var="taskActionFormHandler"/>
<dspel:importbean bean="/atg/epub/servlet/FireWorkflowOutcomeFormHandler" var="fireOutcomeFormHandler"/>
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dspel:importbean var="userDirectory" bean="/atg/userprofiling/ProfileUserDirectory"/>

<%-- Set Current User & ID --%>
<c:set var="currentUserId" value="${profile.repositoryId}"/>
<dspel:tomap var="currentUser" value="${profile.dataSource}"/>

<pws:getCurrentProject var="projectContext"/>

<c:set var="currentProjectId" value="${projectContext.project.id}"/>
<c:set var="currentProcessId" value="${projectContext.process.id}"/>

<fmt:message var="discardAssetButton" key="discard-placeholder-button" bundle="${projectsBundle}"/>

<c:choose>
  <c:when test="${projectView eq PROJECT_TASK_DETAIL_VIEW}">
    <c:set var="isProjectView" value="${true}"/>
  </c:when>
  <c:when test="${projectView eq PROCESS_TASK_DETAIL_VIEW}">
    <c:set var="isProjectView" value="${false}"/>
  </c:when>
  <c:otherwise>
    <c:set var="isProjectView" value="${false}"/>
  </c:otherwise>
</c:choose>

<%-- create some view-specific URLs --%>
<c:url var="assetBrowserURL" value="/html/ProjectsPortlet/assetBrowser.jsp">
  <c:param name="projectView" value="${ASSET_EDIT_VIEW}"/>
</c:url>

<portlet:renderURL var="taskListURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("TASKS_VIEW")%>'/>
</portlet:renderURL>

<portlet:renderURL var="assetBrowserCreateURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("NEW_ASSET_VIEW")%>'/>
</portlet:renderURL>

<portlet:actionURL var="actionURL">
  <portlet:param name="taskId" value='<%=(String)pageContext.getAttribute("taskId")%>'/>
</portlet:actionURL>

<%-- get the taskInfo for this task, if error, workflow may be gone, redirect back to list page --%>
<c:catch var="taskExc">
  <pws:getTasks var="results"  taskElementId="${taskId}"  processId="${currentProcessId}"/>
  <c:forEach items="${results.projectTasks}" var="task" end="${0}">
     <c:set var="taskInfo" value="${task}"/>
  </c:forEach>
</c:catch>
<c:if test="${taskExc ne null}">
  <script language="JavaScript" type="text/javascript">
    <!--
     document.location.replace('<c:out escapeXml="false" value="${taskListURL}"/>'); 
    // -->
  </script>
</c:if>

<c:if test="${taskInfo ne null}">

<%-- Redirect to task list page if this task is no longer active.  --%>
<c:if test="${!taskInfo.active}">
  <script language="JavaScript" type="text/javascript">
    <!--
     document.location.replace('<c:out escapeXml="false" value="${taskListURL}"/>'); 
    // -->
  </script>
</c:if>

<c:set value="${assetBrowserCreateURL}" scope="session"
  var="assetBrowserCreateURL"/>

<c:set value="true" scope="session"
  var="assetBrowserAllowCreate"/>
<c:set value="${currentProjectId}" scope="session"
  var="assetBrowserProjectId"/>
<c:set value="single" scope="session"
  var="assetBrowserSelectMode"/>

<%-- action bar  --%>
  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td class="blankSpace">&nbsp;</td>
        <td><h2>
          <fmt:message key="task-name-label" bundle="${projectsBundle}"/>: <c:out value="${taskInfo.taskDescriptor.displayName}"/>
        </h2></td>
        <td width="100%" class="error" style="text-align: right;">
          <c:forEach var="outcomeException" items="${fireOutcomeFormHandler.formExceptions}">
            <c:out value="${outcomeException.message}"/><br />
          </c:forEach>
          <c:forEach var="taskException" items="${taskActionFormHandler.formExceptions}">
            <c:out value="${taskException.message}"/><br />
          </c:forEach>
        </td>
        <c:choose>
          <c:when test="${taskInfo.active}">
            <td nowrap class="alertMessage"><fmt:message key="task-active-message" bundle="${projectsBundle}"/></td>
          </c:when>
          <c:otherwise>
            <td nowrap class="alertMessage"><fmt:message key="task-inactive-message" bundle="${projectsBundle}"/></td>
          </c:otherwise>
        </c:choose>
      </tr>
    </table>
  </div>
  

<div id="taskDetail">
		
  <table border="0" cellpadding="0" cellspacing="5" width="100%" class="overview">
  <tr>
  <td><h1><c:out value="${taskInfo.taskDescriptor.displayName}"/></h1>
  </td>

  </tr>
		
  <tr>
    <td colspan="2"><p><c:out value="${taskInfo.taskDescriptor.description}"/></p></td>
  </tr>
      
  <tr>
  <td>

     <%-- value to indicated if there is a required placeholder asset that is missing --%>
     <c:set var="missingAssetName" value="${null}"/>

     <table border="0" cellpadding="0" cellspacing="0">
     <tr>
     <td>
	   <table border="0" cellpadding="0" cellspacing="3" width="100%">
	   <tr>
                  <%-- ASSET PLACEHOLDER STUFF --%>

                  <c:choose>
                    <c:when test="${taskInfo.taskDescriptor.subject ne null}">
                      <td class="formLabel formPadding">Related Asset:</td>
                      <c:set var="placeholder" value="${taskInfo.taskDescriptor.subject.name}"/>
                      <pws:getPlaceholderAsset var="phItem" assetDescriptor="${taskInfo.taskDescriptor.subject}" 
                        processId="${currentProcessId}" />
                      <c:choose>
                        <c:when test="${ phItem ne null }">
                          <pws:createVersionManagerURI var="assetURI" object="${phItem}" />
                          <portlet:renderURL var="assetEditURL">
                            <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_EDIT_VIEW")%>'/>
                            <portlet:param name="assetURI" value='<%= (String) pageContext.getAttribute("assetURI")%>'/>
                            <portlet:param name="workspace" value='<%= (String) pageContext.getAttribute("workspace") %>' />
                            <portlet:param name="pushContext" value="true" />
                          </portlet:renderURL>
                          <td class="formPadding">
			    <p>
			      <c:out value="${placeholder}"/>: 
			      <a href='<c:out value="${assetEditURL}"/>' onmouseover="status='';return true;">
                                <c:choose>
                                  <c:when test='${"atg.workflow.FileAssetDescriptor" eq taskInfo.taskDescriptor.subject.class.name }'>
                                    <c:out value="${phItem.absolutePath}"/>
                                  </c:when>
                                  <c:when test='${"atg.workflow.RepositoryAssetDescriptor" eq taskInfo.taskDescriptor.subject.class.name }'>
                                    <c:out value="${phItem.itemDisplayName}"/>
                                  </c:when>
                                </c:choose>
			      </a>
			    </p>
		          </td>
                        </c:when>

                        <c:otherwise>
                          <c:if test="${taskInfo.taskDescriptor.subject.required}">
			     <c:set var="missingAssetName" value="${taskInfo.taskDescriptor.subject.name}"/>
			  </c:if>
                          <td class="formPadding"><p><c:out value="${placeholder}"/>:
                            <a href="javascript:pickPlaceholderAsset();" class="add" onmouseover="status='';return true;">
                              <fmt:message key="supply-asset-link" bundle="${projectsBundle}"/>
                            </a>
                            </p>
			  </td>
                        </c:otherwise>
                      </c:choose>
                    </c:when>
                    <c:otherwise>
                      <td>&nbsp;</td>
                    </c:otherwise>
                  </c:choose>
                  <td>&nbsp;</td>
	   </tr>
	   </table>
     </td>
     </tr>
	   <tr>
	     <td>
			
	 <table border="0" cellpadding="0" cellspacing="3">
          <tr>
            <td class="formLabel formPadding"><fmt:message key="task-current-owner-label" bundle="${projectsBundle}"/>:</td>
			
            <%-- CLAIM/ASSIGN FORM --%>
            <dspel:form formid="assignForm" name="assignForm" method="post" action="${actionURL}">
                  <dspel:input type="hidden" priority="-1" bean="TaskActionFormHandler.assignTask" value="1"/>
              <c:choose>
                <c:when test="${taskInfo.taskDescriptor.assignable eq false}">
                  <td nowrap>&nbsp;</td>
                </c:when>
                <c:otherwise>
                  <dspel:input type="hidden" bean="TaskActionFormHandler.processId" value="${projectContext.process.id}"/>
                  <c:if test="${isProjectView eq true}">
                    <dspel:input type="hidden" bean="TaskActionFormHandler.projectId" value="${projectContext.project.id}"/>
                  </c:if>
                  <dspel:input type="hidden" bean="TaskActionFormHandler.taskElementId" value="${taskInfo.taskDescriptor.taskElementId}"/>
                  <pws:getAssignableUsers var="assUsers" taskDescriptor="${taskInfo.taskDescriptor}"/> 
                  <c:set var="unowned" value="${taskInfo.owner eq null}"/>

                  <c:choose>
                    <c:when test="${taskStatus.completed}">
                      <td class="leftAligned noWrap"><c:out value="${taskInfo.owner.firstName} ${taskInfo.owner.lastName}"/>&nbsp;</td>
                    </c:when>

                    <c:otherwise>
                      <td class="leftAligned noWrap">
                      <dspel:select bean="TaskActionFormHandler.assignee">
                        <c:choose>
                          <c:when test="${taskInfo.owner ne null}" >
                            <dspel:option value="RELEASE"><fmt:message key="release-button-label" bundle="${projectsBundle}"/></dspel:option>
                          </c:when>
                          <c:otherwise>
                            <dspel:option value="UNASSIGNED" selected="${unowned}"><fmt:message key="unassigned-label" bundle="${projectsBundle}"/></dspel:option>
                          </c:otherwise>
                        </c:choose>

                        <option value="" disabled>_____________________</option>

                        <dspel:option value="${currentUserId}:${userDirectory.userDirectoryName}" selected="${currentUserId eq taskInfo.owner.primaryKey}"><c:out value="${currentUser.firstName}"/> <c:out value="${currentUser.lastName}"/>&nbsp;</dspel:option>

                        <option value="" disabled>_____________________</option>

                        <c:forEach var="user" items="${assUsers}">
                          <c:if test="${currentUserId ne user.primaryKey}">
                            <dspel:option value="${user.primaryKey}:${user.userDirectory.userDirectoryName}" selected="${user.primaryKey eq taskInfo.owner.primaryKey}"><c:out value="${user.firstName} ${user.lastName}"/></dspel:option>
                          </c:if>
                        </c:forEach>
                      </dspel:select>
		      </td>
		      <td>&nbsp;
                      <a href='javascript:document.assignForm.submit()' class="standardButton" onmouseover="status='';return true;"><fmt:message key="assign-button-label" bundle="${projectsBundle}"/>&nbsp;&raquo;</a>
                      &nbsp;
                      </td>
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
            </dspel:form>
           </tr>
	  </table>
			
	</td>
	</tr>
	<tr>
	<td>
			
	  <table border="0" cellpadding="0" cellspacing="3">
	   <tr>
           <%-- WORKFLOW ACTION FORM --%>
           <script language="JavaScript" type="text/javascript">
            function setIframe() {
              var actionSelect=document.getElementById("actionOption");
              if (actionSelect) {
               if (actionSelect.options[actionSelect.selectedIndex].value!="") {
                 document.getElementById('workflowIframe').src=actionSelect.options[actionSelect.selectedIndex].value;
                 showIframe('approveAction'); 
               }
              }
            }
          </script>
                <td class="formLabel formPadding"><fmt:message key="task-actions-label" bundle="${projectsBundle}"/>:</td>
	    <c:choose>
	      <c:when test="${missingAssetName ne null}">
	      <td class="formLabel formPadding" nowrap colspan="2">
                <fmt:message key="missing-asset-message" bundle="${projectsBundle}">
                  <fmt:param value="${missingAssetName}"/>
                </fmt:message>
	      </td>
	      </c:when>
	      <c:otherwise>
                  <c:set var="actionFormName" value="actionForm${taskLoop.count}"/>
                  <form name='<c:out value="actionFormName"/>'>
            <td class="leftAligned noWrap">
            <pws:canFireTaskOutcome var="taskPermission" taskInfo="${taskInfo}"/>
              <c:if test ="${taskPermission.hasAccess}">
                  <c:if test="${taskInfo.active}">
  <%--
        <select id="actionOption" class="table tableInfo" onChange="javascript:if (this.options[selectedIndex].value !='0'){document.getElementById('workflowIframe').src=this.options[selectedIndex].value;}">
  --%>
<!-- Showing the Outcome Description - Part 1
	To add/change the items of the Outcome Description, the below option items
	must correspond to the <p> tags highlighted below. 
-->
                  <select id="actionOption" class="table tableInfo" onchange="switchDesc(this.id,'actionDesc');">

                    <option value="" label="Select Action">Select Action</option>

                    <option disabled>_____________________</option>
          
                    <c:forEach var="outcome" items="${taskInfo.taskDescriptor.outcomes}" varStatus="outcomeLoop">
                       <c:url var="iframeUrl" value="/html/ProjectsPortlet/actionNote.jsp">
                           <c:param name="processId" value="${projectContext.process.id}"/>
                           <c:if test="${isProjectView eq true}">
                             <c:param name="projectId" value="${projectContext.project.id}"/>
                           </c:if>
                           <c:param name="taskId" value="${taskInfo.taskDescriptor.taskElementId}"/>
                           <c:param name="outcomeId" value="${outcome.outcomeElementId}"/>
                           <c:param name="outcomeName" value="${outcome.displayName}"/>
                       <c:if test="${outcome.formURI ne null}">
                             <c:param name="outcomeFormURI" value="${outcome.formURI}"/>
                       </c:if>
                       </c:url>
                       <option value='<c:out value="${iframeUrl}"/>'><c:out value="${outcome.displayName}"/></option>
                   </c:forEach>
                 </select>
		 </td>
                 <td>&nbsp;<a href="javascript:setIframe();" class="standardButton" onmouseover="status='';return true;"><fmt:message key="go-label" bundle="${projectsBundle}"/>&nbsp;&raquo;</a>
                  </c:if>
              </c:if>
                  &nbsp;
            </td>
                  </form>
	      </c:otherwise>
	    </c:choose>
			
  	  </tr>
	</table>
			
	 </td>
	</tr>
        <tr>
	  <td>
            <table border="0" cellpadding="0" cellspacing="3" width="100%">
              <tr>
                 <td class="formLabel formPadding"><label for="taskAction1">Action description:</label></td>
                 <td id="actionDesc" colspan="2">

<!-- Showing the Process Type Description - Part 2
	Corresponding <p> tags to the above option items
-->
                 <p>&nbsp;</p>
                 <p>&nbsp;</p>
                 <c:forEach var="outcome" items="${taskInfo.taskDescriptor.outcomes}" varStatus="outcomeLoop">
                   <p><c:out value="${outcome.description}"/></p>
                 </c:forEach>
                 </td>
	       </tr>
           </table>
	  </td>
	</tr>
				
      </table>
     </td>	
		
<td class="rightAligned"><p>&nbsp;</p></td>
</tr>		

</table>
		
</div>		

</c:if>  <%-- taskInfo not null --%>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td class="blankSpace" width="100%">&nbsp;</td>
        <td class="blankSpace">&nbsp;</td>
      </tr>
    </table>
  </div> 
		
  <%-- 
    Project Form
  --%>  
  <dspel:form formid="projectForm" id="projectForm" method="post" action="${actionURL}">
    <dspel:input type="hidden" value="false" priority="100"
      bean="${projectHandlerPath}.loggingDebug"/>
    <dspel:input type="hidden" value="${placeholder}" priority="100"
      bean="${projectHandlerPath}.placeholderName"/>
    <dspel:input type="hidden" value="null" id="asset" priority="100"
      bean="${projectHandlerPath}.asset"/>
    <dspel:input type="hidden" value="${currentProjectId}" priority="100"
      bean="${projectHandlerPath}.projectId"/>
    <dspel:input type="hidden" value="${projectFormHandler.ADD_ASSET_ACTION}" priority="-1"
      bean="${projectHandlerPath}.assetAction"/>
    <dspel:input type="hidden" value="1" priority="-10"
      bean="${projectHandlerPath}.performAssetAction"/>
  </dspel:form>

  <script language="JavaScript">

    // Not using form saving on this page
    function fsOnSubmit() {
      return false;
    }

    function getAssetBrowserConfig() {
      var options = new Object();
      options.contextOp = '<c:out value="${assetInfo.CONTEXT_PUSH}"/>';
      return options;
    }

    function setPlaceholder( selected ) {
      var form = document.getElementById( "projectForm" );
      form.elements[ '<c:out value="${projectHandlerPath}.asset"/>' ].value = 
        selected.uri;
      form.submit();
    }

    function pickPlaceholderAsset() {
      // array to contain only those types to be shown in browser
      var allowableTypes = new Array( 0 );
    
      var assetType = new Object();
      <c:choose>
       <c:when test='${"atg.workflow.FileAssetDescriptor" eq taskInfo.taskDescriptor.subject.class.name }'>
          assetType.typeCode            = 'file';
          assetType.displayName         = '<c:out value="${taskInfo.taskDescriptor.subject.fileType}"/>';
          assetType.displayNameProperty = 'displayName';
          assetType.typeName            = '<c:out value="${taskInfo.taskDescriptor.subject.fileType}"/>';
          assetType.repositoryName      = '<c:out value="${taskInfo.taskDescriptor.subject.VFSPath}"/>';
          assetType.componentPath       = '<c:out value="${taskInfo.taskDescriptor.subject.VFSPath}"/>';
          assetType.createable          = 'true';
          assetType.encodedType = 
            '<c:out value="${taskInfo.taskDescriptor.subject.VFSPath}:${taskInfo.taskDescriptor.subject.fileType}"/>';
        </c:when>
        <c:when test='${"atg.workflow.RepositoryAssetDescriptor" eq taskInfo.taskDescriptor.subject.class.name }'>
          assetType.typeCode            = 'repository';
          assetType.displayName         = '<c:out value="${taskInfo.taskDescriptor.subject.itemType}"/>';
          assetType.displayNameProperty = 'displayName';
          assetType.typeName            = '<c:out value="${taskInfo.taskDescriptor.subject.itemType}"/>';
          assetType.repositoryName      = '<c:out value="${taskInfo.taskDescriptor.subject.repositoryPath}"/>';
          assetType.componentPath       = '<c:out value="${taskInfo.taskDescriptor.subject.repositoryPath}"/>';
          assetType.createable          = 'true';
          assetType.encodedType = 
           '<c:out value="${taskInfo.taskDescriptor.subject.repositoryPath}:${taskInfo.taskDescriptor.subject.itemType}"/>';
        </c:when>
      </c:choose>
      
      allowableTypes[ allowableTypes.length ] = assetType;

      var picker = new AssetBrowser( '<c:out value="${assetInfoPath}"/>' );
        picker.browserMode        = 'pick';
        picker.isAllowMultiSelect = 'false';
        picker.createMode         = 'nested';
        picker.onSelect           = 'setPlaceholder';
        picker.closeAction        = 'hide';
        picker.defaultView        = "Search";
        picker.assetTypes         = allowableTypes;
        picker.assetPickerFrameElement = document.getElementById('iFrame');
        picker.assetPickerDivId        = "browser";
        picker.invoke();
    }

    <c:if test="${ ! empty assetInfo.context.attributes.newAssetAssetURI }">

      function updatePickerValue() {
        var selected = new Object();
        selected.id = '<c:out value="${assetInfo.context.attributes.newAssetID}"/>';
        selected.displayName = '<c:out value="${assetInfo.context.attributes.newAssetDisplayName}"/>';
        selected.uri = '<c:out value="${assetInfo.context.attributes.newAssetAssetURI}"/>';
        setPlaceholder( selected );
      }

      registerOnLoad( updatePickerValue );

      <c:set target="${assetInfo.context.attributes}" property="onSelect" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetID" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetAssetURI" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetDisplayName" value=""/>
    </c:if>

  </script>

  <%@ include file="../ProjectsPortlet/contextForm.jspf" %>

</dspel:page>

<!-- End ProjectsPortlet's taskDetail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/taskDetail.jsp#2 $$Change: 651448 $--%>
