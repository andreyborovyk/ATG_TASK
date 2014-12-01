<%--
  Fragment that displays a task outcome selector.

  @param  projectContext  The current project context
  @param  task            The current TaskInfo object

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/taskOutcomes.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramProjectContext" param="projectContext"/>
  <dspel:getvalueof var="paramTask" param="task"/>
  <dspel:getvalueof var="paramAutoShowDialog" param="autoShowTaskActionDialog"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${requestScope.managerConfig.resourceBundle}"/>

  <web-ui:getWorkflowElementDisplayName var="taskName" element="${paramTask.taskDescriptor}"/>

  <%-- Display the outcome selector if the task is active and the user has
       permission to generate an outcome for the task.  Otherwise, just display
       the task name. --%>
  <pws:canFireTaskOutcome var="taskPermission" taskInfo="${paramTask}"/>
  <c:choose>
    <c:when test="${paramTask.active && taskPermission.hasAccess}">

      <script type="text/javascript">
    
        function onOutcomeSelect() {
          var actionSelect = document.getElementById("formTaskOutcome");
          if (actionSelect.selectedIndex > 1)
            saveAndShowTaskActionDialog();
        }

        function saveAndShowTaskActionDialog() {
          // no task actions while in multi edit
          if (parent.multiEditWait) {
            messages.addError("<fmt:message key='assetManager.multiEditInProgress'/>");
            return;
          }
        
          if (!actionsAreLocked()) {
            var actionSelect = document.getElementById("formTaskOutcome");

            atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.showTaskActionDialog, actionSelect.selectedIndex, "showTaskActionDialog");
          }
        }

        function showTaskActionDialog(autoSelectIndex) {
          var actionSelect = document.getElementById("formTaskOutcome");

          if (autoSelectIndex && actionSelect && actionSelect.options[autoSelectIndex].value != "") {
            document.getElementById("iFrame2").src = actionSelect.options[autoSelectIndex].value;
            showIframe("confirm");
          }
          else if (actionSelect && actionSelect.options[actionSelect.selectedIndex].value != "") {
            document.getElementById("iFrame2").src = actionSelect.options[actionSelect.selectedIndex].value;
            showIframe("confirm");
          }
	  else {
            messages.addError("<fmt:message key='assetManager.noTaskActionSelected'/>");
          }
        }
      
      </script>

      <select id="formTaskOutcome" onchange="onOutcomeSelect()" title="<fmt:message key='taskOutcomes.selectTooltip'/>">
        <option value="" label="">
          <c:out value="${taskName}"/>
        </option>
        <option disabled>
          _____________________
        </option>
        <c:forEach var="outcome" items="${paramTask.taskDescriptor.outcomes}">
          <web-ui:getWorkflowElementDisplayName var="outcomeName" element="${outcome}"/>
          <web-ui:encodeParameterValue var="encodedOutcomeName" value="${outcomeName}"/>

          <c:url var="iframeUrl" value="/taskOutcomeConfirm.jsp">
            <c:param name="processId" value="${paramProjectContext.process.id}"/>
            <c:param name="projectId" value="${paramProjectContext.project.id}"/>
            <c:param name="taskId" value="${paramTask.taskDescriptor.taskElementId}"/>
            <c:param name="outcomeId" value="${outcome.outcomeElementId}"/>
            <c:param name="outcomeName" value="${encodedOutcomeName}"/>
            <c:if test="${outcome.formURI ne null}">
              <c:param name="outcomeFormURI" value="${outcome.formURI}"/>
            </c:if>
          </c:url>

          <option value='<c:out value="${iframeUrl}"/>'>
            <c:out value="${outcomeName}"/>
          </option>
        </c:forEach>
      </select>
    </c:when>
    <c:otherwise>
      <c:out value="${taskName}"/>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/taskOutcomes.jsp#2 $$Change: 651448 $--%>
