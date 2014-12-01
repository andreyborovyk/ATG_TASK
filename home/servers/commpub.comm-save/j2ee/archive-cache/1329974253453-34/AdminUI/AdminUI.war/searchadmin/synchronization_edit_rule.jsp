<%--
Synchronization edit auto sync rule page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_edit_rule.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationRuleFormHandler"
                var="synchronizationRuleFormHandler"/>
  
  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
  <admin-beans:getSyncTaskDefinition taskDefId="${syncTaskId}" var="syncTaskDef" />
  <c:set var="baseSyncTask" value="${syncTaskDef.baseSyncTask}"/>

  <%-- init formhandler --%>
  <admin-ui:initializeFormHandler handler="${synchronizationRuleFormHandler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="syncTaskDefinitionId" value="${syncTaskId}"/>
  </admin-ui:initializeFormHandler>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="errorURL" value="/searchadmin/synchronization_edit_rule.jsp">
      <c:param name="projectId" value="${projectId}"/>
      <c:param name="syncTaskId" value="${syncTaskDef.id}"/>
    </c:url>

    <d:form method="post" action="${errorURL}">
      <d:input type="hidden" bean="SynchronizationRuleFormHandler.syncTaskDefinitionId"/>

      <div id="paneContent">
        <h3><fmt:message key="synchronization_task_params.sync_task"/></h3>

        <table class="form" cellpadding="0" cellspacing="0">
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.status"/></td>
            <td>
              <d:input type="radio" bean="SynchronizationRuleFormHandler.status" name="status" id="status_true" value="true" iclass="radio"/>
              <label for="status_true"><fmt:message key="synchronization_task_params.status.enable"/></label>
              <d:input type="radio" bean="SynchronizationRuleFormHandler.status" name="status" id="status_false" value="false" iclass="radio"/>
              <label for="status_false"><fmt:message key="synchronization_task_params.status.disable"/></label>
            </td>
          </tr>
          <tr>
            <td class="label">
              <span id="subNameAlert"><span class="required"><fmt:message key="required_field"/></span></span>
              <fmt:message key="synchronization_task_params.sync_task_name"/>
            </td>
            <td>
              <d:input bean="SynchronizationRuleFormHandler.subName" type="text" iclass="textField" name="subName" id="subName"/>
            </td>
          </tr>
          <tr>
            <td class="label">
              <fmt:message key="synchronization_task_params.comment"/>
            </td>
            <td>
              <d:textarea bean="SynchronizationRuleFormHandler.description" cols="40" rows="5" iclass="textAreaField"/>
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.steps"/></td>
            <td>
              <ul class="simpleBullets">
                <c:if test="${baseSyncTask.partitionReuseType != null}">
                  <li>
                    <fmt:message key="synchronization.step.${baseSyncTask.partitionReuseType.optionType}"/>
                  </li>
                </c:if>
                <c:if test="${baseSyncTask.preIndexCustomization != null}">
                  <li>
                    <fmt:message key="synchronization.step.${baseSyncTask.preIndexCustomization.optionType}"/>
                  </li>
                </c:if>
                <c:if test="${baseSyncTask.indexType != null}">
                  <li>
                    <fmt:message key="synchronization.step.${baseSyncTask.indexType.optionType}"/>
                  </li>
                </c:if>
                <c:if test="${baseSyncTask.postIndexCustomization != null}">
                  <li>
                    <c:choose>
                      <c:when test="${baseSyncTask.postIndexCustomization.optionType eq 'PostIndexCustomization_LoadLatest' and baseSyncTask.partitionReuseType.optionType eq 'Partition_UseExistingPartition'}">
                        <fmt:message key="synchronization.step.PostIndexCustomization_LoadSelected"/>
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="synchronization.step.${baseSyncTask.postIndexCustomization.optionType}"/>
                      </c:otherwise>
                    </c:choose> 
                  </li>
                </c:if>
                <%--<c:if test="${baseSyncTask.taskType.enum != 'apply_post_index_customization_only' and baseSyncTask.taskType.name != 'Deploy Only'}">
                  <li>
                    <fmt:message key="synchronization.step.Indexing_DefaultOption"/>
                  </li>
                </c:if>--%>
                <c:if test="${baseSyncTask.deploymentType != null}">
                  <li>
                    <fmt:message key="synchronization.step.${baseSyncTask.deploymentType.optionType}"/>
                  </li>
                </c:if>
              </ul>
            </td>
          </tr>

          <c:if test="${baseSyncTask.pauseIndexing}">
            <tr>
              <td class="label"><fmt:message key="synchronization_task_params.pause"/></td>
              <td>
                <fmt:message key="synchronization_task_params.paused_task"/>
              </td>
            </tr>
          </c:if>

          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.content_sets"/></td>
            <td>
              <c:choose>
                <c:when test="${baseSyncTask.includeAllContentSources}">
                  <fmt:message key="synchronization_task_params.all_content_sets"/>
                </c:when>
                <c:otherwise>
                  <c:set var="contentList">
                    <tags:join delimiter="; " items="${project.index.logicalPartitions}" var="logicalPartition">
                      <tags:join items="${baseSyncTask.contentSourceSetSelections}" var="contentSourceSetSelection"
                          prefix="${logicalPartition.name}: " delimiter=", ">
                        <c:if test="${contentSourceSetSelection.parentLogicalPartition == logicalPartition}">
                          ${contentSourceSetSelection.name}
                        </c:if>
                      </tags:join>
                      <c:forEach items="${baseSyncTask.logicalPartitions}" var="logPart">
                        <c:if test="${logPart == logicalPartition}">
                          ${logicalPartition.name}
                        </c:if>
                      </c:forEach>
                    </tags:join>
                  </c:set>
                  <c:choose>
                    <c:when test="${not empty contentList}">
                      <c:out value="${contentList}" />
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="synchronization_task_params.none"/>
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.customizations"/></td>
            <td>
              <c:choose>
                <c:when test="${baseSyncTask.includeAllCustomData}">
                  <c:set var="isAllCustomItems" value="true"/>
                  <fmt:message key="synchronization_task_params.all_customization_items"/>
                </c:when>
                <c:otherwise>
                  <admin-beans:getProjectCustomizations baseSyncTask="${baseSyncTask}" varPostIndex="syncTaskCust" />
                  <tags:join delimiter="; " items="${syncTaskCust}" var="customizationSelection">
                    <c:choose>
                      <c:when test="${not empty customizationSelection.nameKey}">
                        <fmt:message key="${customizationSelection.nameKey}"/>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${customizationSelection.nameValue}" />
                      </c:otherwise>
                    </c:choose>
                  </tags:join>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.deploy_to"/></td>
            <td>
              <c:set var="searchEnvironments" value="${baseSyncTask.searchEnvironmentSelections}"/>
              <c:choose>
                <c:when test="${empty searchEnvironments}">
                  <fmt:message key="synchronization_task_params.none"/>
                </c:when>
                <c:otherwise>
                  <tags:join delimiter="; " items="${searchEnvironments}" var="searchEnvironmentSelection">
                    <c:out value="${searchEnvironmentSelection.envName}" />
                  </tags:join>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </table>
        <span class="seperator"></span>

        <%-- Schedule section for sync task --%>
        <d:include page="synchronization_schedule.jsp"/>
      </div>

      <div id="paneFooter">
        <c:url var="successURL" value="/searchadmin/synchronization_automatic.jsp">
          <c:param name="projectId" value="${projectId}"/>
        </c:url>
        <d:input type="hidden" bean="SynchronizationRuleFormHandler.successURL" value="${successURL}" />
        <d:input type="hidden" bean="SynchronizationRuleFormHandler.errorURL" value="${errorURL}" />
        <d:input type="hidden" bean="SynchronizationRuleFormHandler.projectId" value="${projectId}" />
        <d:input type="hidden" bean="SynchronizationRuleFormHandler.sourceIndexId" value="null" />
        <d:input bean="SynchronizationRuleFormHandler.needInitialization" value="false" type="hidden" name="needInitialization"/>
        <fmt:message var="save_rule" key="synchronization_task_params.button.save_rule"/>
        <fmt:message var="save_rule_tooltip" key="synchronization_task_params.button.save_rule.tooltip"/>
        <d:input bean="SynchronizationRuleFormHandler.saveRule" type="submit" value="${save_rule}"
                 title="${save_rule_tooltip}" iclass="formsubmitter" />

        <fmt:message var="cancel" key="synchronization_task_params.button.cancel"/>
        <fmt:message var="cancel_tooltip" key="synchronization_task_params.button.cancel.tooltip"/>
        <d:input bean="SynchronizationRuleFormHandler.cancel" type="submit" iclass="formsubmitter" 
                value="${cancel}" title="${cancel_tooltip}"/>
      </div>

    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_edit_rule.jsp#2 $$Change: 651448 $--%>
