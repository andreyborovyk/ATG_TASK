<%@ page import="javax.portlet.*" %>
<%@ page import="javax.servlet.jsp.jstl.core.LoopTagStatus" %>
<%@ page import="atg.epub.project.Project,atg.epub.project.Process,atg.repository.SortDirective" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>
<!-- Begin ProjectsPortlet's projectBrowse.jsp -->

<dspel:page xml="true">
  <portlet:defineObjects/>

  <!-- BEGIN PROJECT PORTLET DISPLAY -->
  <fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
  <fmt:message var="goLabel" key="go-label" bundle="${projectsBundle}"/>

  <%-- values for project status property --%>
  <fmt:setBundle var="propertiesBundle" basename="atg.epub.PublishingRepositoryResources"/>
  <fmt:message var="editStatus" key="processStatusEdit" bundle="${propertiesBundle}"/>
  <fmt:message var="editRunningStatus" key="processStatusEditRunning" bundle="${propertiesBundle}"/>
  <fmt:message var="runningStatus" key="processStatusRunning" bundle="${propertiesBundle}"/>
  <fmt:message var="deployedStatus" key="processStatusDeployed" bundle="${propertiesBundle}"/>
  <fmt:message var="completedStatus" key="processStatusCompleted" bundle="${propertiesBundle}"/>
  <fmt:message var="allTypesOption" key="all-types-option" bundle="${projectsBundle}"/>

  <%-- this bean used to get process portlet ID based on workflow type --%>
  <dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>

  <dspel:importbean bean="/atg/epub/servlet/ProcessSearchFormHandler" var="processSearchHandler"/>
  <c:if test="${param.moreResults ne null}">
    <dspel:setvalue  bean="ProcessSearchFormHandler.moreResults" value="${true}"/>
  </c:if>
  <c:if test="${param.resultsPageNum ne null}">
    <dspel:setvalue  bean="ProcessSearchFormHandler.currentResultPageNum" value="${param.resultsPageNum}"/>
  </c:if>


  <%@ include file="projectConstants.jspf" %>
  <portlet:actionURL  var="actionURL"/>

  <portlet:renderURL  var="createProcessURL">
    <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("CREATE_VIEW")%>'/>
  </portlet:renderURL>

  <c:set var="sortAscending"><%=SortDirective.DIR_ASCENDING%></c:set> 
  <c:set var="sortDescending"><%=SortDirective.DIR_DESCENDING%></c:set> 


  <%-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --%>

  <%-- RESULTS DISPLAY --%>
  <c:set var="resultSet" value="${processSearchHandler.searchResults}"/>
  <c:set var="startCount" value="${processSearchHandler.startCount}"/>
  <c:set var="endCount" value="${processSearchHandler.endCount}"/>
  <c:set var="currentPageNum" value="${processSearchHandler.currentResultPageNum}"/>
  <c:set var="resultPageCount" value="${processSearchHandler.resultPageCount}"/>
  <c:set var="resultSetSize" value="${processSearchHandler.resultSetSize}"/>
  <c:set var="resultSetSizeDisplay" value="${resultSetSize}"/>
  <c:set var="moreResults" value="${false}"/>
  <c:if test="${(processSearchHandler.maxRowCount ne -1) && resultSetSize > processSearchHandler.maxRowCount}">
     <fmt:message var="moreSymbol" key="more-results-symbol" bundle="${projectsBundle}"/>
    
     <c:set var="resultSetSizeDisplay" value="${(resultSetSize-1)}${moreSymbol}"/>
     <c:set var="moreResults" value="${true}"/>
     <%-- don't show last page (with partial list) if more results --%>
     <c:set var="resultPageCount" value="${resultPageCount-1}"/>
  </c:if>

  <div id="intro">
    <h2><fmt:message key="project-list-title" bundle="${projectsBundle}"/></h2>
    <p><fmt:message key="intro-text" bundle="${projectsBundle}"/></p>
    <span class="error">
      <%-- ERROR MESSAGES --%>
      <c:forEach var="exception" items="${processsearchhandler.formExceptions}">
        <c:out value="${exception.message}"/><br />
      </c:forEach>
    </span>
  </div>
    
  <!-- begin content -->
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td width="100%" valign="top" >

        <!-- display results per page tools -->
        <div class="displayResults displayResultsTop">
        <a  class="refreshButton" href='javascript:document.forms["searchForm2"].submit();' onmouseover="status='';return true;"><fmt:message key="refresh-results" bundle="${projectsBundle}"/></a>
          <c:if test="${resultSetSize > processSearchHandler.maxResultsPerPage}">
      <c:set var="pageNum" value="${0}"/>
      <%--  removing "showing items" info
            <fmt:message key="paging-info" bundle="${projectsBundle}">
              <fmt:param value="${startCount}"/>
              <fmt:param value="${endCount}"/>
              <fmt:param value="${resultSetSizeDisplay}"/>
            </fmt:message>&nbsp;
      --%>
            <fmt:message key="pages-label" bundle="${projectsBundle}"/>
            <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
        <c:set var="pageNum" value="${loopInfo.count}"/>
              <portlet:renderURL  var="pageURL">
                <portlet:param name="resultsPageNum" value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
              </portlet:renderURL>
              <c:choose>
                <c:when test="${currentPageNum == pageNum}">[<c:out value="${currentPageNum}"/>]</c:when>
                <c:otherwise><a href='<c:out value="${pageURL}"/>' onmouseover="status='';return true;"><c:out value="${pageNum}"/></a>&nbsp;</c:otherwise>
              </c:choose>
            </c:forEach>
          </c:if>
          <c:if test="${moreResults}">
        <c:set var="pageNum" value="${pageNum+1}"/>
              <portlet:renderURL  var="moreURL">
                <portlet:param name="resultsPageNum" value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
                <portlet:param name="moreResults" value="true" />
              </portlet:renderURL>
             <a href='<c:out value="${moreURL}"/>' onmouseover="status='';return true;"><fmt:message key="more-results-link" bundle="${projectsBundle}"/></a>&nbsp;
    </c:if>
    &nbsp;
        </div>
        <!-- end display results per page tools -->


<%-- get all workflow definitions to use here and in the search form --%>
<pws:getWorkflowDefinitions var="workflowDefinitions" />

<%-- Big Blue Bar --%>
 <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarProcess">
 <tr>
 <td>
         <div class="attributes">
   <p><em><fmt:message key="search-attributes-label" bundle="${projectsBundle}"/>:</em></p>
     <c:set var="searchType" value="${allTypesOption}"/>
     <c:if test="${processSearchHandler.workflowType ne null && processSearchHandler.workflowType ne ''}">
  <%-- need this to get the display name rather than the path of the .wdl file...  --%>
  <c:forEach var="workflow" items="${workflowDefinitions.workflowDefinitions}">
    <c:if test="${workflow.processName eq processSearchHandler.workflowType}">
      <web-ui:getWorkflowElementDisplayName var="searchType" element="${workflow}"/>
    </c:if>
  </c:forEach>
     </c:if>
 <p><fmt:message key="process-type-attr-label" bundle="${projectsBundle}"/>: <em><c:out value="${searchType}"/></em></p>
   <p><fmt:message key="containing-attr-label" bundle="${projectsBundle}"/>: <em><dspel:valueof bean="ProcessSearchFormHandler.textInput"/></em></p>
   <p><fmt:message key="results-count-attr-label" bundle="${projectsBundle}"/>: <em><c:out value="${resultSetSizeDisplay}"/></em></p>
         </div>

         <div class="attributeExtras">
         <br /><br />
         </div>
 </td>
 </tr>
 </table>

      <dspel:form formid="searchForm" action="${actionURL}" method="post">
        <table width="100%" cellpadding="0" cellspacing="0" border="0" class="dataTableNoGrid">
          <%-- PROJECT SEARCH FORM --%>

            <%-- default sort property --%>
            <dspel:input type="hidden" bean="ProcessSearchFormHandler.sortProperty" value="displayName" />
            <dspel:input type="hidden" bean="ProcessSearchFormHandler.sortedSearch" value="1" />
        <%-- SORTABLE COLUMN HEADERS --%>
            <tr>
        <%-- Process name --%>
              <th class="leftAligned"><span class="tableHeader">
          <a href='javascript:document.forms[0].elements["/atg/epub/servlet/ProcessSearchFormHandler.sortProperty"].value = "displayName";document.forms[0].submit();' onmouseover="status='';return true;"><fmt:message key="project-name-label" bundle="${projectsBundle}"/>&nbsp;
    <c:if test='${processSearchHandler.sortProperty eq "displayName"}'>
       <c:choose>
         <c:when test="${processSearchHandler.sortDirection eq sortAscending}">
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowAscending.gif")%>' alt='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:when>
         <c:otherwise>
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowDescending.gif")%>' alt='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:otherwise>
       </c:choose>
    </c:if>
    </a>
        </span></th>
              <th class="centerAligned"><span class="tableHeader">
          <a href='javascript:document.forms[0].elements["/atg/epub/servlet/ProcessSearchFormHandler.sortProperty"].value = "status";document.forms[0].submit();' onmouseover="status='';return true;"><fmt:message key="status-label" bundle="${projectsBundle}"/>&nbsp;
    <c:if test='${processSearchHandler.sortProperty eq "status"}'>
       <c:choose>
         <c:when test="${processSearchHandler.sortDirection eq sortAscending}">
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowAscending.gif")%>' alt='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:when>
         <c:otherwise>
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowDescending.gif")%>' alt='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:otherwise>
       </c:choose>
    </c:if>
    </a>
        </span></th>
              <th class="centerAligned"><span class="tableHeader"><fmt:message key="workflow-type-header" bundle="${projectsBundle}"/></span> </th>
              <th class="centerAligned"><span class="tableHeader">
          <a href='javascript:document.forms[0].elements["/atg/epub/servlet/ProcessSearchFormHandler.sortProperty"].value = "creator.lastName";document.forms[0].submit();' onmouseover="status='';return true;"><fmt:message key="creator-header" bundle="${projectsBundle}"/>&nbsp;
    <c:if test='${processSearchHandler.sortProperty eq "creator.lastName"}'>
       <c:choose>
         <c:when test="${processSearchHandler.sortDirection eq sortAscending}">
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowAscending.gif")%>' alt='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:when>
         <c:otherwise>
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowDescending.gif")%>' alt='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:otherwise>
       </c:choose>
    </c:if>
    </a>
        </span></th>
              <th class="centerAligned"><span class="tableHeader">
          <a href='javascript:document.forms[0].elements["/atg/epub/servlet/ProcessSearchFormHandler.sortProperty"].value = "creationDate";document.forms[0].submit();' onmouseover="status='';return true;"><fmt:message key="start-date" bundle="${projectsBundle}"/>&nbsp;
    <c:if test='${processSearchHandler.sortProperty eq "creationDate"}'>
       <c:choose>
         <c:when test="${processSearchHandler.sortDirection eq sortAscending}">
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowAscending.gif")%>' alt='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-ascending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:when>
         <c:otherwise>
                          <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/sort_arrowDescending.gif")%>' alt='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' title='<fmt:message key="sort-descending" bundle="${projectsBundle}"/>' width="7" height="5" border="0" />
         </c:otherwise>
       </c:choose>
    </c:if>
    </a>
        </span></th>
              <th class="centerAligned"><span class="tableHeader"><fmt:message key="locked-header" bundle="${projectsBundle}"/></span></th>
    <th class="leftColumn"><span class="tableHeader"><fmt:message key="current-task-header" bundle="${projectsBundle}"/>&nbsp;:</span></th>
    <th class="rightColumn"><span class="tableHeader"><fmt:message key="current-task-owner-header" bundle="${projectsBundle}"/></span></th>
            </tr>

          <%-- RESULTS TABLE --%>
          <c:choose>
            <c:when test="${empty resultSet}">
              <tr><td colspan="9" class="centerAligned error"><fmt:message key="no-processes-message" bundle="${projectsBundle}"/></td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="process" varStatus="loopInfo" items="${resultSet}">

      <%-- check if process is still valid, it may have been deleted --%>
      <c:set var="processIsValid" value="${true}"/>
      <c:set var="ex" value="${null}"/>
      <c:catch var="ex">
         <c:set var="tmp" value="${process.project.creator}"/>
      </c:catch>
      <c:if test="${ex ne null}">
        <c:set var="processIsValid" value="${false}"/>
      </c:if>

      <%-- skip row if process is not valid --%>
      <c:if test="${processIsValid}">

                  <!-- tr -->
                  <c:choose>
                    <c:when test="${loopInfo.count % 2 == 0}"><tr></c:when> 
                    <c:otherwise><tr class="alternateRowHighlight"></c:otherwise>
                  </c:choose>

                  <%-- *** NOTE: the detailURL has the project param and portletID appended 
                     so it will be visible to the ProjectFilter page templates--%>
                  <portlet:renderURL  var="detailURL">
                    <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("PROJECT_DETAIL_VIEW")%>'/>
                    <portlet:param name="process" value='<%=((Process)pageContext.getAttribute("process")).getId()%>'/>
                  </portlet:renderURL>
      <%-- determine portlet to render based on workflow type --%>
      <%-- TODO - replace with a tag once workflow category is supported --%>
      <c:set var="portletID" value='${bizuiPortletConfig.defaultProcessPortletID}'/>
      <c:if test="${process.status ne 'Completed'}">
       <c:set var="workflow" value="${null}"/>
        <c:catch var="e">
         <pws:getWorkflowDescriptor var="workflow" processId="${process.id}"/>
         <c:set var="workflowPath" value="${workflow.processWorkflow.processName}"/>
         <%@ include file="getWorkflowType.jspf" %>
         <c:set var="portletID" value='${bizuiPortletConfig.workflowPortletMap[workflowType]}'/>
        </c:catch>
       <%-- if no workflow exists (i.e. process complete) check for processData item to determine if this is a Campaign --%>
       <c:if test="${e ne null || workflow eq null}">
        <c:if test="${process.processData ne null}">
         <c:if test="${'communicationCampaignData' eq process.processData.itemDescriptor.itemDescriptorName}">
          <c:set var="portletID" value='${bizuiPortletConfig.workflowPortletMap["campaign"]}'/>
         </c:if>
        </c:if>
       </c:if>
      </c:if>
      <%-- if process is completed check for processData item to determine if this is a Campaign other use the default portlet id --%>
      <c:if test="${process.status eq 'Completed'}">
        <c:set var="workflow" value="${null}"/>
        <%-- check for processData item to determine if this is a Campaign --%>
        <c:if test="${process.processData ne null}">
         <c:if test="${'communicationCampaignData' eq process.processData.itemDescriptor.itemDescriptorName}">
          <c:set var="portletID" value='${bizuiPortletConfig.workflowPortletMap["campaign"]}'/>
         </c:if>
        </c:if>
      </c:if>
      <c:set var="detailURL" value="${detailURL}&project=${process.project.id}&processPortlet=${portletID}"/>

                  <td class="leftAligned wrapNoBorder"><span class="tableInfo"><a href='<c:out value="${detailURL}"/>' onmouseover="status='';return true;"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/icon_process.gif")%>' alt="Process" width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" /><c:out value="${process.displayName}"/></a></span>
      
      </td>
                  <td class="centerAligned"><span class="tableInfo">
                    <c:choose>
                      <c:when test="${process.status eq 'Edit'}"><c:out value="${editStatus}"/></c:when>
                      <c:when test="${process.status eq 'EditRunning'}"><c:out value="${editRunningStatus}"/></c:when>
                      <c:when test="${process.status eq 'Running'}"><c:out value="${runningStatus}"/></c:when>
                      <c:when test="${process.status eq 'Deployed'}"><c:out value="${deployedStatus}"/></c:when>
                      <c:when test="${process.status eq 'Completed'}"><c:out value="${completedStatus}"/></c:when>
                      <c:otherwise><c:out value=" "/></td></c:otherwise>                    
                    </c:choose>
                  </span></td>
                  <td class="centerAligned wrapNoBorder"><span class="tableInfo">
                    <c:choose>
                      <c:when test="${workflow == null || workflow.processWorkflow == null}">
                        &nbsp;
                      </c:when>
                      <c:otherwise>
                        <c:catch var="e">
                          <web-ui:getWorkflowElementDisplayName var="workflowDisplayName" element="${workflow.processWorkflow}"/>
                          <c:out value="${workflowDisplayName}"/>
                        </c:catch>
                      </c:otherwise>
                    </c:choose>
                  </span></td>
                  <td class="centerAligned wrapNoBorder"><span class="tableInfo">
                    <dspel:tomap var="creator" value="${process.project.creator}"/>
                    <c:out value="${creator.firstName}"/> <c:out value="${creator.lastName}"/>
                  </span></td>
                  <td class="centerAligned"><span class="tableInfo"><fmt:formatDate type="date" dateStyle="medium" value="${process.project.creationDate}"/><br /></span></td>
                  <td class="centerAligned"><span class="tableInfo"><c:if test="${!process.project.editable}"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/html/ProjectsPortlet/images/icon_locked.gif")%>' alt="locked" title='<fmt:message key="locked-info-text" bundle="${projectsBundle}"/>' width="16" height="16" border="0" /></c:if>&nbsp;</span></td>
                  <pws:getTasks var="results" active="${true}" unowned="${false}" userOnly="${false}" processId="${process.id}"/>
                  <td class="leftColumn"><span class="tableInfo">
                    <c:forEach var="taskInfo" items="${results.tasks}">
                    <web-ui:getWorkflowElementDisplayName var="taskDisplayName" element="${taskInfo.taskDescriptor}"/>
                    <a href='<c:out value="${detailURL}"/>' class="current" onmouseover="status='';return true;"><c:out value="${taskDisplayName}"/></a>&nbsp;:<br />
                    </c:forEach>
                  </span></td>
                  <td class="rightColumn"><span class="tableInfo">
                    <c:forEach var="taskInfo" items="${results.tasks}">
                      <c:if test="${taskInfo.owner eq null}"><fmt:message key="unassigned-label" bundle="${projectsBundle}"/></c:if>
                      <c:out value="${taskInfo.owner.firstName}"/> <c:out value="${taskInfo.owner.lastName}"/><br />
                    </c:forEach>
                  </span></td>  
                </tr> <!-- /tr matching "if"ed tr above -->
            
                  <!-- tr -->
                  <c:choose>
                    <c:when test="${loopInfo.count % 2 == 0}"><tr></c:when> 
                    <c:otherwise><tr class="alternateRowHighlight"></c:otherwise>
                  </c:choose>
                  <td class="wrapBorder" colspan="9"><span class="tableInfo"><c:out value="${process.description}"/>&nbsp;</span></td>
                </tr> <!-- /tr matching second "if"ed tr above -->
          </c:if>  <%-- if projectIsValid --%>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </table>
      </dspel:form>   <%-- end search/sorting form --%>
   

    <div class="contentActions">
      <table cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td class="blankSpace" width="100%">&nbsp;</td>
  </tr>
      </table>
    </div>

        <!-- display results per page tools -->
        <div class="displayResults">
          <c:if test="${resultSetSize > processSearchHandler.maxResultsPerPage}">
      <c:set var="pageNum" value="${0}"/>
            <fmt:message key="paging-info" bundle="${projectsBundle}">
              <fmt:param value="${startCount}"/>
              <fmt:param value="${endCount}"/>
              <fmt:param value="${resultSetSizeDisplay}"/>
            </fmt:message>&nbsp;
            <fmt:message key="pages-label" bundle="${projectsBundle}"/>
            <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
        <c:set var="pageNum" value="${loopInfo.count}"/>
              <portlet:renderURL  var="pageURL">
                <portlet:param name="resultsPageNum" value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
              </portlet:renderURL>
              <c:choose>
                <c:when test="${currentPageNum == pageNum}">[<c:out value="${currentPageNum}"/>]</c:when>
                <c:otherwise><a href='<c:out value="${pageURL}"/>' onmouseover="status='';return true;"><c:out value="${pageNum}"/></a>&nbsp;</c:otherwise>
              </c:choose>
            </c:forEach>
          </c:if>
          <c:if test="${moreResults}">
        <c:set var="pageNum" value="${pageNum+1}"/>
              <portlet:renderURL  var="moreURL">
                <portlet:param name="resultsPageNum" value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
                <portlet:param name="moreResults" value="true" />
              </portlet:renderURL>
             <a href='<c:out value="${moreURL}"/>' onmouseover="status='';return true;"><fmt:message key="more-results-link" bundle="${projectsBundle}"/></a>&nbsp;
    </c:if>
        </div>
        <!-- end display results per page tools -->

   </td>
         <td id="sideBar">

           <p>&nbsp;</p>  

           <div>
             <table cellpadding="0" cellspacing="0" border="0">
             <dspel:form name="searchForm2" formid="searchForm2" action="${actionURL}" method="post">
             <dspel:input type="hidden" priority="-1" bean="ProcessSearchFormHandler.search" value="1" />
               <tr>
                 <td class="title"><fmt:message key="search-options-title" bundle="${projectsBundle}"/></td>
               </tr>

               <tr>
                 <td class="formLabel"><fmt:message key="status-search-label" bundle="${projectsBundle}"/>:</td>
               </tr>

               <tr>
                 <%-- project status --%>
                 <td>
                   <dspel:select iclass="formElement" bean="ProcessSearchFormHandler.status"> 
                     <dspel:option value=""><fmt:message key="all-status-option" bundle="${projectsBundle}"/></dspel:option>
                     <dspel:option value="Edit"><c:out value="${editStatus}"/></dspel:option>
                     <dspel:option value="EditRunning"><c:out value="${editRunningStatus}"/></dspel:option>
                     <dspel:option value="Running"><c:out value="${runningStatus}"/></dspel:option>
                     <dspel:option value="Deployed"><c:out value="${deployedStatus}"/></dspel:option>
                     <dspel:option value="Completed"><c:out value="${completedStatus}"/></dspel:option>
                   </dspel:select>
                 </td>
               </tr>
    
               <tr>
                 <td class="formLabel"><fmt:message key="workflow-type-search-label" bundle="${projectsBundle}"/>:</td>
               </tr>

               <tr>
                 <%-- workflow type --%>
                 <pws:getWorkflowDefinitions var="workflowDefinitions" />
                 <td>
                   <dspel:select iclass="formElement" bean="ProcessSearchFormHandler.workflowType">
                     <dspel:option value=""><c:out value="${allTypesOption}"/></dspel:option>
                     <c:forEach var="workflow" items="${workflowDefinitions.workflowDefinitions}">
                        <web-ui:getWorkflowElementDisplayName var="workflowDisplayName" element="${workflow}"/>
                        <dspel:option value="${workflow.processName}"><c:out value="${workflowDisplayName}"/></dspel:option>
                      </c:forEach>
                    </dspel:select>
                  </td>
                </tr>

    <%-- always use "contains" --%>
    <dspel:input type="hidden" bean="ProcessSearchFormHandler.startingWith" value="false"/>

                <tr>
                  <td class="formLabel"><fmt:message key="name-search-label" bundle="${projectsBundle}"/>:</td>
                </tr>

                <tr>
                  <td class="formElement">
                    <dspel:input iclass="formElementInputText" bean="ProcessSearchFormHandler.textInput" size="30" type="text"/>
                  </td>
                </tr>

                <tr>
                  <td class="" style="text-align: left; padding: 4px;">
                    <dspel:input iclass="formElementRadio" type="radio" bean="ProcessSearchFormHandler.mineOnly" value="false" id="all"/><label for="all"><fmt:message key="ownership-all-label" bundle="${projectsBundle}"/></label><br />
                    <dspel:input iclass="formElementRadio" type="radio" bean="ProcessSearchFormHandler.mineOnly" value="true" id="mineOnly"/><label for="mineOnly"><fmt:message key="ownership-mine-label" bundle="${projectsBundle}"/></label>
                  </td>
                </tr>


    <%-- REMOVED SORT OPTIONS FROM SEARCH BOX 
                <tr>
                  <td class="formLabel"><fmt:message key="sortby-label" bundle="${projectsBundle}"/>:
                  </td>
                </tr>

                <tr>
                  <td>
                    <dspel:select iclass="formElement" bean="ProcessSearchFormHandler.sortProperty">
   
                      <dspel:option value="displayName"><fmt:message key="project-name-label" bundle="${projectsBundle}"/></dspel:option>
      
                      <dspel:option value="creationDate"><fmt:message key="start-date" bundle="${projectsBundle}"/></dspel:option>
   
                      <dspel:option value="creator.lastName"><fmt:message key="creator-header" bundle="${projectsBundle}"/></dspel:option>
                      <dspel:option value="status"><fmt:message key="status-label" bundle="${projectsBundle}"/></dspel:option>
   
                    </dspel:select>
      </td>
    <tr>
      <td class="" style="text-align: left; padding: 4px;">
                        <dspel:input type="radio" iclass="formElementRadio" bean="ProcessSearchFormHandler.sortDirection" value="${sortAscending}" /><fmt:message key="sort-ascending" bundle="${projectsBundle}"/><br />
                        <dspel:input type="radio" iclass="formElementRadio" bean="ProcessSearchFormHandler.sortDirection" value="${sortDescending}" /><fmt:message key="sort-descending" bundle="${projectsBundle}"/>
   
                  </td>
                </tr>
--%>

    <%--  Results per page --%>
                <tr>
                  <td class="formLabel"><fmt:message key="results-per-page-label" bundle="${projectsBundle}"/>:</td>
                </tr>

                <tr>
                  <td>
                    <dspel:select iclass="formElementSmallSelect" bean="ProcessSearchFormHandler.maxResultsPerPage">
                      <dspel:option  value="10"><fmt:message key="results-count-10" bundle="${projectsBundle}"/></dspel:option>
                      <dspel:option  value="20"><fmt:message key="results-count-20" bundle="${projectsBundle}"/></dspel:option>
                      <dspel:option  value="50"><fmt:message key="results-count-50" bundle="${projectsBundle}"/></dspel:option>
                      <dspel:option  value="100"><fmt:message key="results-count-100" bundle="${projectsBundle}"/></dspel:option>
                    </dspel:select>
                  </td>
                </tr>

                <tr>
      <%--
                  <td class="action"><dspel:input iclass="goButton" type="submit" bean="ProcessSearchFormHandler.search" value="${goLabel}" /></td>
      --%>
                  <td class="action">
                     <a  class="goButton" href='javascript:document.forms["searchForm2"].submit();' onmouseover="status='';return true;"><fmt:message key="go-label" bundle="${projectsBundle}"/></a>
      </td>
                </tr>
       </dspel:form>
              </table>
    
            </div>
    
          </td>  
        </tr>
      </table>

  <%-- HEADER ACTION BAR --%>

  <%-- create new process link 
    <div id="processTools">
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <!--<td><a href='<c:out value="${createProcessURL}"/>' onmouseover="status='';return true;"><img src="/images/icon_newProcess.gif" alt="Create New Process" width="16" height="16" border="0" /><fmt:message key="create-project-link" bundle="${projectsBundle}"/></a></td>-->
          <td id="assetBrowserButtonClosed"><a href='<c:out value="${createProcessURL}"/>' onmouseover="status='';return true;"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/images/icon_newProcess.gif")%>' alt="<fmt:message key='create-project-link' bundle='${projectsBundle}'/>" width="16" height="16" border="0" /><fmt:message key="create-project-link" bundle="${projectsBundle}"/></a></td>
        </tr>
      </table>
    </div>
--%>

<div id="assetBrowser" class="panelClosed">
  <iframe src="javascript:false;" ></iframe>
  <div class="closeWin">
  <a href="javascript:moveAssetBrowser()" onmouseover="status='';return true;">close window</a>
  </div>  
</div>

</dspel:page>

<!-- End ProjectsPortlet's projectBrowse.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectBrowse.jsp#2 $$Change: 651448 $--%>
