<%--
Manual synchronization page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_manual.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"
                var="synchronizationManualFormHandler"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <admin-ui:initializeFormHandler handler="${synchronizationManualFormHandler}">
    <admin-ui:param name="projectId" value="${projectId}" />
  </admin-ui:initializeFormHandler>

  <c:set value="${synchronizationManualFormHandler.task}" var="task"/>
  <script type="text/javascript">
    setIncremental("${task}");
  </script>
  <c:set var="stepNumber" value="0"/>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form  formid="options" method="post" action="synchronization_add_rule.jsp">
      <div id="paneContent">
        <h3><fmt:message key="synchronization_manual.sync.task"/></h3>
        <br/>
        <table class="form" style="width:100% !important;">
        <tr><td colspan="2">
          <nobr>
            <d:select id="syncTask" iclass="small" onchange="enableDisabledCheckboxes();document.getElementById('taskChange').click();" name="syncTask" bean="SynchronizationManualFormHandler.task">
              <c:forTokens items="complete,full_selected_content,incremental,apply_post_index_customization_only,deploy_only" delims="," var="syncTask">      
                <d:option value="${syncTask}"><fmt:message key="synchronization.task.${syncTask}"/></d:option>
              </c:forTokens>
            </d:select>
            <c:url var="stepViewURL" value="/searchadmin/synchronization_manual.jsp">
              <c:param name="projectId" value="${projectId}"/>
            </c:url>
            <c:url var="queueURL" value="/searchadmin/synchronization_status_monitor.jsp">
              <c:param name="projectId" value="${projectId}"/>
              <c:param name="isQueuedTask" value="true"/>
            </c:url>
            <d:input type="hidden" id="queueURL" name="queueURL" bean="SynchronizationManualFormHandler.queueURL" value="${queueURL}" />
            <d:input type="hidden" id="stepViewURL" name="stepViewURL" bean="SynchronizationManualFormHandler.stepViewURL" value="${stepViewURL}" />
            <d:input bean="SynchronizationManualFormHandler.taskChange" type="submit" iclass="formsubmitter"
                     style="display:none;" name="taskChange" id="taskChange" value="taskChange"/>
          </nobr>
        <c:if test="${task eq 'complete' or task eq 'full_selected_content'}">
          </td></tr><tr id="indexingPlanEstimation"><td colspan="2">
            <d:input type="checkbox" id="pauseIndexingProcess" bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.pauseIndexingProcess" name="pauseIndexingProcess"/>
            <label for="pauseIndexingProcess"><fmt:message key="project_synchronization_perform.estimate"/></label>
        </c:if>
        
        <c:set var="partitionStepValue" value="${synchronizationManualFormHandler.partitionStep}" />
        <c:if test="${not empty partitionStepValue}">
          </td></tr><tr><td id="index" colspan="2">
            <c:set var="stepNumber" value="${stepNumber + 1}"/>
            <c:if test="${stepNumber eq '1'}"><span class="seperator"></span></c:if>
            <p><strong><fmt:message key="synchronization_manual.steps.pattern">
                <fmt:param value="${stepNumber}"/>
                <fmt:param><fmt:message key="synchronization_manual.steps.indexing"/></fmt:param>
            </fmt:message></strong></p>
            <d:input type="hidden" id="partitionStep" name="partitionStep" value="${partitionStepValue}" 
              bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.partitionStep"/>
            
            <c:choose>
              <c:when test="${task eq 'full_selected_content' and partitionStepValue eq 'Partition_UseCleanPartition'}">
                <span class="step"><fmt:message key="synchronization.step.${task}.${partitionStepValue}"/></span>
              </c:when>
              <c:otherwise>
                <span class="step"><fmt:message key="synchronization.step.${partitionStepValue}"/></span>
              </c:otherwise>
            </c:choose>
        </c:if>
          
        <c:set var="preIndexStepValue" value="${synchronizationManualFormHandler.preIndexStep}" />
        <c:if test="${not empty preIndexStepValue}">
          </td></tr><tr><td id="preIndex" colspan="2">
            <c:set var="stepNumber" value="${stepNumber + 1}"/>
            <c:if test="${stepNumber eq '1'}"><span class="seperator"></span></c:if>
            <p><strong><fmt:message key="synchronization_manual.steps.pattern">
                <fmt:param value="${stepNumber}"/>
                <fmt:param><fmt:message key="synchronization_manual.steps.pre.customization"/></fmt:param>
            </fmt:message></strong></p>
           <d:input type="hidden" id="preIndexStep" name="preIndexStep" value="${preIndexStepValue}" 
              bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.preIndexStep"/>
             
            <span class="step"><fmt:message key="synchronization.step.${preIndexStepValue}"/></span>
        </c:if>
          
        <c:set var="contentStepValue" value="${synchronizationManualFormHandler.contentStep}" />
        <c:if test="${not empty contentStepValue}">
          </td></tr><tr id="contentSet"><td colspan="2">
            <c:set var="stepNumber" value="${stepNumber + 1}"/>
            <c:if test="${stepNumber eq '1'}"><span class="seperator"></span></c:if>
            <p><strong><fmt:message key="synchronization_manual.steps.pattern">
              <fmt:param value="${stepNumber}"/>
              <fmt:param><fmt:message key="synchronization_manual.steps.content"/></fmt:param>
            </fmt:message></strong></p>
            <d:input type="hidden" id="contentStep" name="contentStep" value="${contentStepValue}" 
              bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.contentStep"/>
            
            <c:if test="${task eq 'complete' or contentStepValue eq 'Content_DoNotIndex'}">
              <span class="step"><fmt:message key="synchronization.step.${contentStepValue}"/></span>
            </c:if>
            
            <c:if test="${task eq 'full_selected_content' or contentStepValue eq 'Content_UpdateIndex'}">
              <c:set var="allContent" value="${synchronizationManualFormHandler.allContentSets eq 'true'}"/>
              <c:if test="${task eq 'full_selected_content'}">
                <span class="step"><fmt:message key="synchronization_manual.select.content.sets"/></span>
                <script type="text/javascript">
                  showAllContentCheckboxes(false);
                </script>
              </c:if>
              <c:if test="${contentStepValue eq 'Content_UpdateIndex'}">
                <ul id="contentSetSelect" class="simple">
                  <li><input id="contentSourceAll" type="radio" name="contentSource" value="1" onclick="radioSwitch(this); contentSwitch('true');"
                             <c:if test="${allContent}">checked="true"</c:if>/>
                    <label for="contentSourceAll"><fmt:message key="synchronization_manual.all.content.sets"/></label></li>
                  <li><input id="contentSourceSelected" type="radio" name="contentSource" value="2" onclick="radioSwitch(this); contentSwitch('false');"
                             <c:if test="${not allContent}">checked="true"</c:if>/>
                    <label for="contentSourceSelected"><fmt:message key="synchronization_manual.select.content"/></label></li>
                </ul>
              </c:if>
              <span id="contentSetSelect1" style="display:none;"></span>
              <table id="contentSetSelect2" style="display:none;width:100%"><tr>
              <td id="csViewId">
                <d:include src="synchronization_content_table.jsp"/>
                <tags:setHiddenField name="contentSetsList" beanName="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"/>
                <tags:setHiddenField name="contentList" beanName="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"/>
              </td></tr></table>
              <script type="text/javascript">
                <c:if test="${not allContent}">
                  document.getElementById("contentSetSelect2").style.display = "";
                  disableIndexingButtons();
                </c:if>
              </script>  
            </c:if>
        </c:if> 
          
        <c:set var="postIndexStepValue" value="${synchronizationManualFormHandler.postIndexStep}" />
        <c:if test="${not empty postIndexStepValue}">
          </td></tr><tr id="postCustomization"><td colspan="2">
            <c:set var="stepNumber" value="${stepNumber + 1}"/>
            <c:if test="${stepNumber eq '1'}"><span class="seperator"></span></c:if>
            <p><strong><fmt:message key="synchronization_manual.steps.pattern">
                <fmt:param value="${stepNumber}"/>
                <fmt:param><fmt:message key="synchronization_manual.steps.post.customization"/></fmt:param>
            </fmt:message></strong></p>
            <d:input type="hidden" id="postIndexStep" name="postIndexStep" value="${postIndexStepValue}" 
                bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.postIndexStep"/>
            
            <c:if test="${task ne 'apply_post_index_customization_only'}">
              <span class="step" id="customizationLabel"><fmt:message key="synchronization.step.${postIndexStepValue}"/></span>
            </c:if>
            
            <c:if test="${task eq 'apply_post_index_customization_only' or task eq 'incremental'}">
              <ul id="postCustomizationSelect" class="simple">
                <li id="useExistingCustomization"><input id="customizationUseExisting" type="radio" name="customization" value="1" onclick="radioSwitch(this); postCustomSwitch('true', 'PostIndexCustomization_UseExisting');"
                           <c:if test="${synchronizationManualFormHandler.postIndexStep == 'PostIndexCustomization_UseExisting'}">checked="true"</c:if> />
                  <label for="customizationUseExisting"><fmt:message key="synchronization_manual.use.existing"/></label></li>
                <li><input id="customizationAll" type="radio" name="customization" value="2" onclick="radioSwitch(this); postCustomSwitch('true', 'PostIndexCustomization_LoadLatest');"
                           <c:if test="${synchronizationManualFormHandler.postIndexStep == 'PostIndexCustomization_LoadLatest' and synchronizationManualFormHandler.allPostCustomizationItems == 'true'}">checked="true"</c:if> />
                  <label for="customizationAll"><fmt:message key="synchronization_manual.all.customizations"/></label></li>
                <li><input id="customizationSelected" type="radio" name="customization" value="3" onclick="radioSwitch(this); postCustomSwitch('false', 'PostIndexCustomization_LoadLatest');"
                           <c:if test="${synchronizationManualFormHandler.postIndexStep == 'PostIndexCustomization_LoadLatest' and synchronizationManualFormHandler.allPostCustomizationItems == 'false'}">checked="true"</c:if> />
                  <label for="customizationSelected"><fmt:message key="synchronization_manual.select.customizations"/></label></li>
              </ul>
              <script type="text/javascript">
                <c:if test="${task eq 'apply_post_index_customization_only'}">
                  document.getElementById("useExistingCustomization").style.display = "none";
                </c:if>
                <c:if test="${task eq 'incremental'}">
                    showCustomizationOptions(${allContent});
                </c:if>
                <c:if test="${synchronizationManualFormHandler.allPostCustomizationItems == 'false'}">
                  document.getElementById("postCustomizationSelect3").style.display = "";
                </c:if>
              </script>
              <span id="postCustomizationSelect1" style="display:none;"></span>
              <span id="postCustomizationSelect2" style="display:none;"></span>
              <table id="postCustomizationSelect3" style="display:none;width:100%"><tr><td>
                <d:include src="sync_manual_post_customization.jsp"/>
                <d:input bean="SynchronizationManualFormHandler.projectId" type="hidden" value="${projectId}"/>
                <tags:setHiddenField name="postCustomizationsList" beanName="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"/>
              </td></tr></table>
            </c:if>
        </c:if>

        <c:set var="indexingOptionStepValue" value="${synchronizationManualFormHandler.indexingOptionStep}" />
        <c:if test="${not empty indexingOptionStepValue}">
          <d:input type="hidden" id="indexingOptionStep" name="indexingOptionStep" value="${indexingOptionStepValue}" 
              bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.indexingOptionStep"/>
        </c:if>
          
        <c:set var="deploymentStepValue" value="${synchronizationManualFormHandler.deploymentStep}" />
        </td></tr><tr id="searchEnvironments"><td colspan="2">
          <c:set var="stepNumber" value="${stepNumber + 1}"/>
          <c:if test="${stepNumber eq '1'}"><span class="seperator"></span></c:if>
          <p><strong><fmt:message key="synchronization_manual.steps.pattern">
              <fmt:param value="${stepNumber}"/>
              <fmt:param><fmt:message key="synchronization_manual.steps.deploy"/></fmt:param>
          </fmt:message></strong></p>
          <d:input type="hidden" id="deploymentStep" name="deploymentStep" value="${deploymentStepValue}" 
                bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler.deploymentStep"/>
          <c:set var="deploy" value="${synchronizationManualFormHandler.deploymentStep eq 'Deployment_DeployIndex'}"/>
          
          <c:if test="${synchronizationManualFormHandler.task eq 'deploy_only'}">
            <span class="step"><fmt:message key="synchronization.step.Deployment_DeployIndex"/></span>
          </c:if>
                
          <c:if test="${synchronizationManualFormHandler.task ne 'deploy_only'}">
            <ul id="environmentSelect" class="simple">
              <li><input id="dontDeploy" type="radio" name="deploy" value="1" <c:if test="${not deploy}">checked="true"</c:if>
                        onclick="radioSwitch(this); document.getElementById('deploymentStep').value='Deployment_DoNotDeployIndex';"/>
                <label for="dontDeploy"><fmt:message key="synchronization.step.Deployment_DoNotDeployIndex"/></label></li>
              <li><input id="deploy" type="radio" name="deploy" value="2" <c:if test="${deploy}">checked="true"</c:if> 
                        onclick="radioSwitch(this); document.getElementById('deploymentStep').value='Deployment_DeployIndex';"/>
                <label for="deploy"><fmt:message key="synchronization.step.Deployment_DeployIndex"/></label></li>
            </ul>
          </c:if>
          <span id="environmentSelect1" style="display:none;"></span>
          <table id="environmentSelect2" style="display:none;width:100%"><tr><td>
            <d:include page="search_environments_table.jsp">
              <d:param name="projectId" value="${projectId}"/>
              <d:param name="tableId" value="searchEnvTable"/>
              <d:param name="deploymentOnly" value="true"/>
              <d:param name="indexingHandler" value="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"/>
            </d:include>
          </td></tr></table>
          
          <script type="text/javascript">
            <c:if test="${deploy}">
              document.getElementById("environmentSelect2").style.display = "";
            </c:if>
          </script>
        </td></tr></table>
      </div>
      <c:url var="errorURL" value="/searchadmin/synchronization_manual.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
      <c:url var="addRuleUrl" value="/searchadmin/synchronization_add_rule.jsp"/>
      <c:url var="statusMonitorUrl" value="/searchadmin/synchronization_status_monitor.jsp"/>
      
      <d:input type="hidden" id="successUrl" name="successUrl" bean="SynchronizationManualFormHandler.successURL" value="${statusMonitorUrl}" />
      <d:input type="hidden" id="addRuleUrl" name="addRuleUrl" bean="SynchronizationManualFormHandler.addRuleURL" value="${addRuleUrl}" />
      <d:input type="hidden" id="errorUrl" name="errorUrl" bean="SynchronizationManualFormHandler.errorURL" value="${errorURL}" />
      <d:input type="hidden" id="projectId" name="projectId" bean="SynchronizationManualFormHandler.projectId" />
      <d:input type="hidden" name="allContentSets" id="searchIsAllContentSetsHidden"  bean="SynchronizationManualFormHandler.allContentSets"/>
      <d:input type="hidden" name="allPostCustomizationItems" id="isAllCustomizationsHidden"  bean="SynchronizationManualFormHandler.allPostCustomizationItems"/>
      <d:input bean="SynchronizationManualFormHandler.needInitialization" value="false" type="hidden" name="needInitialization"/>
      
      <d:input type="hidden" bean="SortFormHandler.successURL" value="${errorURL}"/>
      <d:input type="submit" bean="SortFormHandler.sort" iclass="formsubmitter" style="display:none" id="sortInput"/>
      
      <div id="paneFooter">
        <admin-beans:getProjectCustomizations varHasLanguageErrors="hasLanguageErrors" checkLanguages="${true}" projectId="${projectId}" />
        <admin-beans:getDuplicatedContentLabels projectId="${projectId}" varHasDuplication="labelsDuplicated"/>
        <admin-beans:getProjectStatus var="projectStatus" projectId="${projectId}"/>
        <fmt:message var="buttonPerform" key="synchronization_manual.button.perform.sync"/>
        <fmt:message var="buttonPerformToolTip" key="synchronization_manual.button.perform.sync.tooltip"/>
        <d:input bean="SynchronizationManualFormHandler.performSynchronization" type="submit" value="${buttonPerform}"
                onclick="return checkForm();" iclass="formsubmitter" id="build_button"
                disabled="${projectStatus == 2 or hasLanguageErrors or labelsDuplicated}"
                title="${buttonPerformToolTip}"/>
        <fmt:message var="buttonSave" key="synchronization_manual.button.save_as_auto_sync_rule"/>
        <fmt:message var="buttonSaveToolTip" key="synchronization_manual.button.save_as_auto_sync_rule.tooltip"/>
        <d:input bean="SynchronizationManualFormHandler.saveAsAutoSyncRule" type="submit" value="${buttonSave}"
                 onclick="return checkForm();" iclass="formsubmitter" id="save_rule_button"
                 title="${buttonSaveToolTip}"/>
      </div>
    </d:form>
  </div>
  <admin-validator:validate beanName="SynchronizationManualFormHandler" />
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_manual.jsp#2 $$Change: 651448 $--%>
