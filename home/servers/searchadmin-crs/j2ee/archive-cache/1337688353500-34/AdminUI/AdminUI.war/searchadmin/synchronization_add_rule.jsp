<%--
Synchronization add auto sync rule page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_add_rule.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationRuleFormHandler"
                var="synchronizationRuleFormHandler"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"
                var="synchronizationManualFormHandler"/>
  
  <c:if test="${projectId == null}">
    <d:getvalueof bean="SynchronizationManualFormHandler.projectId" var="projectId"/>
  </c:if>

  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>

  <admin-ui:initializeFormHandler handler="${synchronizationRuleFormHandler}"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="errorURL" value="/searchadmin/synchronization_add_rule.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    
    <d:form method="post" action="${errorURL}" onsubmit="return false;">
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
              <d:input id="subName" name="subName" bean="SynchronizationRuleFormHandler.subName" iclass="textField"/>
              <d:input bean="SynchronizationManualFormHandler.task" type="hidden"/>
              <d:input bean="SynchronizationManualFormHandler.pauseIndexingProcess" type="hidden"/>
            </td>
          </tr>
          <tr>
          <td class="label"><fmt:message key="synchronization_task_params.comment"/></td>
            <td>
              <d:textarea bean="SynchronizationRuleFormHandler.description" cols="40" rows="5" iclass="textAreaField"/>
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.steps"/></td>
            <td>
              <ul class="simpleBullets">
                <c:set var="deployOnly" value="false"/>
                <c:forTokens items="partitionStep,preIndexStep,contentStep,postIndexStep,indexingOptionStep,deploymentStep"
                             delims="," var="step">
                  <c:set value="${synchronizationManualFormHandler[step]}" var="currentStep" />
                  <c:if test="${not empty currentStep}">
                    <c:if test="${'Indexing_DefaultOption' != currentStep}">
                      <li>
                        <c:choose>
                          <c:when test="${currentStep eq 'PostIndexCustomization_LoadLatest' and synchronizationManualFormHandler.partitionStep eq 'Partition_UseExistingPartition'}">
                            <fmt:message key="synchronization.step.PostIndexCustomization_LoadSelected"/>
                          </c:when>
                          <c:otherwise>
                            <fmt:message key="synchronization.step.${currentStep}"/>
                          </c:otherwise>
                        </c:choose> 
                      </li>
                    </c:if>
                    <d:input bean="SynchronizationManualFormHandler.${step}" type="hidden"/>
                  </c:if>
                  <c:if test="${empty currentStep}">
                    <c:set var="deployOnly" value="true"/>
                  </c:if>
                </c:forTokens>
              </ul>
            </td>
          </tr>

          <c:if test="${synchronizationManualFormHandler.pauseIndexingProcess}">
            <tr>
              <td class="label"><fmt:message key="synchronization_task_params.pause"/></td>
              <td>
                <fmt:message key="synchronization_task_params.paused_task"/>
              </td>
            </tr>
          </c:if>

          <c:if test="${not deployOnly}">
            <tr>
              <td class="label"><fmt:message key="synchronization_task_params.content_sets"/></td>
              <td>
                <d:getvalueof bean="SynchronizationManualFormHandler.allContentSets" var="isAllContentSets" />
                <d:getvalueof bean="SynchronizationManualFormHandler.contentSetsList" var="contentSetsList" />
                <d:getvalueof bean="SynchronizationManualFormHandler.contentList" var="contentList" />
                <d:input bean="SynchronizationManualFormHandler.allContentSets" type="hidden"/>
                <c:choose>
                  <c:when test="${isAllContentSets=='true'}">
                    <fmt:message key="synchronization_task_params.all_content_sets"/>
                  </c:when>
                  <c:when test="${isAllContentSets=='false'}">
                    <c:forEach items="${contentSetsList}" var="contentSetId" varStatus="indexSelections">
                      <d:input bean="SynchronizationManualFormHandler.contentSetsList" type="hidden" value="${contentSetId}"/>
                    </c:forEach>
                    <c:forEach items="${contentList}" var="contentId" varStatus="indexSelections">
                      <d:input bean="SynchronizationManualFormHandler.contentList" type="hidden" value="${contentId}"/>
                    </c:forEach>
                    <c:set var="contentList">
                      <tags:join delimiter="; " items="${project.index.logicalPartitions}" var="logicalPartition">
                        <tags:join items="${contentList}" var="contentSetId"
                            prefix="${logicalPartition.name}: " delimiter=", ">
                          <admin-beans:getContentById id="${contentSetId}" var="contentSourceSetSelection"/> 
                          <c:if test="${contentSourceSetSelection.parentLogicalPartition == logicalPartition}">
                            ${contentSourceSetSelection.name}
                          </c:if>
                        </tags:join>
                        <c:forEach items="${contentSetsList}" var="logPartId">
                          <admin-beans:getLogicalPartitionById var="logPart" id="${logPartId}"/>
                          <c:if test="${logPart == logicalPartition}">
                            ${logicalPartition.name}
                          </c:if>
                        </c:forEach>
                      </tags:join>
                    </c:set>
                    <c:out value="${contentList}" />
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="synchronization_task_params.none"/>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="synchronization_task_params.customizations"/></td>
              <td>
                <d:getvalueof bean="SynchronizationManualFormHandler.postCustomizationsList" var="postCustomizationsList" />
                <d:getvalueof bean="SynchronizationManualFormHandler.allPostCustomizationItems" var="isAllCustomizationItems" />
                <d:input bean="SynchronizationManualFormHandler.allPostCustomizationItems" type="hidden"/>
                <c:choose>
                  <c:when test="${isAllCustomizationItems=='true' or empty postCustomizationsList}">
                    <fmt:message key="synchronization_task_params.all_customization_items"/>
                  </c:when>
                  <c:otherwise>
                    <admin-beans:getProjectCustomizations varPostIndex="postIndex" projectId="${projectId}"/>
                    <tags:join delimiter="; " items="${postIndex}" var="customizationSelection">
                      <c:set var="itemId" value="${customizationSelection.type.id}_${customizationSelection.id}"/> 
                      <c:if test="${adminfunctions:isContains(postCustomizationsList, itemId)}">
                        <d:input bean="SynchronizationManualFormHandler.postCustomizationsList" type="hidden" value="${itemId}"/>
                        <c:choose>
                          <c:when test="${not empty customizationSelection.nameKey}">
                            <fmt:message key="${customizationSelection.nameKey}"/>
                          </c:when>
                          <c:otherwise>
                            <c:out value="${customizationSelection.nameValue}" />
                          </c:otherwise>
                        </c:choose>
                      </c:if>
                    </tags:join>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:if>
          
          <tr>
            <td class="label"><fmt:message key="synchronization_task_params.deploy_to"/></td>
            <td>
              <d:getvalueof bean="SynchronizationManualFormHandler.searchEnvironmentsList" var="searchEnvironments" />
              <c:choose>
                <c:when test="${empty searchEnvironments}">
                  <fmt:message key="synchronization_task_params.none"/>
                </c:when>
                <c:otherwise>
                  <tags:join delimiter="; " items="${searchEnvironments}" var="searchEnvSelectionId">
                    <admin-beans:getSearchEnvironment environmentId="${searchEnvSelectionId}" var="searchEnvironmentSelection"/>
                    <d:input bean="SynchronizationManualFormHandler.searchEnvironmentsList" type="hidden"
                      value="${searchEnvSelectionId}" /><c:out value="${searchEnvironmentSelection.envName}" />
                  </tags:join>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </table>
        <span class="seperator"></span>

        <%-- Schedule section for sync task. --%>
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

        <fmt:message var="create_rule" key="synchronization_task_params.button.create_rule"/>
        <fmt:message var="create_rule_tooltip" key="synchronization_task_params.button.create_rule.tooltip"/>
        <d:input bean="SynchronizationRuleFormHandler.createRule" type="submit" value="${create_rule}"
                 title="${create_rule_tooltip}" iclass="formsubmitter"/>

        <fmt:message var="cancel" key="synchronization_task_params.button.cancel"/>
        <fmt:message var="cancel_tooltip" key="synchronization_task_params.button.cancel.tooltip"/>
        <d:input bean="SynchronizationRuleFormHandler.cancel" type="submit" iclass="formsubmitter"
                 value="${cancel}" title="${cancel_tooltip}"/>
      </div>

    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_add_rule.jsp#2 $$Change: 651448 $--%>
