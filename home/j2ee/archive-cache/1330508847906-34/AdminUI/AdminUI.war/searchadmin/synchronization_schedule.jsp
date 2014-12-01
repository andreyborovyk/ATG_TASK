<%--
Synchronization schedule section for add/edit sync rules page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_schedule.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/Configuration" var="configuration" />
  <fmt:message key="dateFormat" var="dateFormat" />
  <fmt:message key="dojo.lang" var="dojoLang" />

  <script type="text/javascript">
    dojo.require("atg.searchadmin.DateTextBox");

    function setScheduleOption(pSelectElm) {
      var selectElmValue = pSelectElm.options[pSelectElm.selectedIndex].value;
      document.getElementById("daysOfWeekDiv").style.display = (selectElmValue == 3) ? "" : "none";
      document.getElementById("intervalDiv").style.display = (selectElmValue == 2) ? "" : "none";
      document.getElementById("endScheduleDiv").style.display = (selectElmValue != 1) ? "" : "none";
      if (selectElmValue == 2) {
        setIntervalOption(document.getElementById("syncTaskScheduleBean.timePeriod"));
      }
      return true;
    }
    function setIntervalOption(pSelectElm) {
      var selectElmValue = pSelectElm.options[pSelectElm.selectedIndex].value;
      document.getElementById("betweenDiv").style.display = (selectElmValue != 3) ? "" : "none";
    }
    function setCheckBox(pParetnElement, pTargetCheckBox) {
      document.getElementById(pTargetCheckBox).checked = true;
      pParetnElement.focus();
      return true;
    }
  </script>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationRuleFormHandler" var="scheduleHandler"/>
  <fmt:message key="calendar_title" var="calendar_title" />

  <h3>
    <d:input bean="SynchronizationRuleFormHandler.syncBy" name="syncBy" id="syncBy.2" type="radio" value="2"/>
    <label for="syncBy.2"><fmt:message key="synchronization_task_params.sync_by_deployment.title"/></label>
    <span class="ea"><tags:ea key="embedded_assistant.synchronization_schedule.sync_by_deployment_title" /></span>
  </h3>

  <table class="form" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td class="label">
        <span class="required"><fmt:message key="required_field"/></span>
        <fmt:message key="synchronization_schedule.deployment.type"/>
      </td>
      <td>
        <d:select bean="SynchronizationRuleFormHandler.deploymentType" iclass="small" name="deploymentType">
          <d:option value="full"><fmt:message key="synchronization_schedule.deployment.type.full"/></d:option>
          <d:option value="incremental"><fmt:message key="synchronization_schedule.deployment.type.incremental"/></d:option>
          <d:option value="customizations"><fmt:message key="synchronization_schedule.deployment.type.customizations"/></d:option>
        </d:select>
      </td>
    </tr>
    <tr>
      <td class="label">
        <span id="deploymentTargetAlert"><span class="required"><fmt:message key="required_field"/></span></span>
        <fmt:message key="synchronization_schedule.deployment.target"/>
      </td>
      <td>
        <d:importbean var="deploymentServer" bean="/atg/epub/DeploymentServer" />
        <c:choose>
          <c:when test="${deploymentServer == null or empty deploymentServer.deploymentTargets}">
            <d:input bean="SynchronizationRuleFormHandler.deploymentTarget" iclass="textField" name="deploymentTarget" />
          </c:when>
          <c:otherwise>
            <d:select bean="SynchronizationRuleFormHandler.deploymentTarget" iclass="small" name="deploymentTarget">
              <d:option value=""><fmt:message key="synchronization_schedule.deployment.target.select"/></d:option>
              <c:forEach var="deploymentTarget" items="${deploymentServer.deploymentTargets}">
                <admin-ui:option value="${deploymentTarget.name}"><c:out value="${deploymentTarget.name}" /></admin-ui:option>
              </c:forEach>
            </d:select>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </tbody>
  </table>

  <span class="seperator"></span>

  <h3>
    <d:input bean="SynchronizationRuleFormHandler.syncBy" name="syncBy" id="syncBy.0" type="radio" value="0"/>
    <label for="syncBy.0"><fmt:message key="synchronization_task_params.sync_by_schedule.title"/></label>
  </h3>
  <table class="form" cellpadding="0" cellspacing="0">
    <tr>
      <td class="label">
        <span id="syncTaskScheduleBean.startDateTimeAlert"></span>
        <fmt:message key="synchronization_schedule.start_date"/>
      </td>
      <td>
        <%-- Get the current date value as an ISO8601/RFC3339 string, in order to initialize the DateTextBox --%>
        <fmt:formatDate var="dateValue" value="${scheduleHandler.syncTaskScheduleBean.startDateTimeAsDate}" pattern="yyyy-MM-dd"/>

        <d:input type="hidden" id="startDateHidden" name="syncTaskScheduleBean.startDate"
            bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.startDate" />
        <input type="text" id="startDate" class="textField date" value="${dateValue}" setValueToId="startDateHidden"
            dojoType="atg.searchadmin.DateTextBox" constraints="{datePattern: '${dateFormat}'}" lang="${dojoLang}" />

        <d:input type="text" id="startTime" iclass="textField small" name="syncTaskScheduleBean.startTime"
              bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.startTime" />
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="synchronization_schedule.schedule"/>
      </td>
      <td>
        <d:select bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.scheduleValue" id="scheduleSet" iclass="small"
                  onchange="setScheduleOption(this);" name="syncTaskScheduleBean.scheduleValue">
          <d:option value="1">
            <fmt:message key="synchronization_schedule.scheduleset.1"/>
          </d:option>
          <d:option value="2">
            <fmt:message key="synchronization_schedule.scheduleset.2"/>
          </d:option>
          <d:option value="3">
            <fmt:message key="synchronization_schedule.scheduleset.3"/>
          </d:option>
        </d:select>
      </td>
    </tr>
  </table>

  <div id="daysOfWeekDiv" style="display:none;">
    <table class="form" cellpadding="0" cellspacing="0">
      <tr>
        <td class="label">
          <span id = "syncTaskScheduleBean.daysOfWeekAlert"></span>
          <fmt:message key="synchronization_schedule.days_of_week"/>
        </td>
        <td>
          <ul class="horizontalList">
            <c:forTokens items="monday,tuesday,wednesday,thursday,friday,saturday,sunday" delims="," var="dow" varStatus="dowStatus">
              <li>
                <nobr>
                  <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.daysOfWeek" name="syncTaskScheduleBean.daysOfWeek"
                           id="syncTaskSchedule.daysOfWeek.${dow}" type="checkbox" value="${(dowStatus.index + 1) mod 7 + 1}"/>
                  <label for="syncTaskSchedule.daysOfWeek.<c:out value='${dow}'/>"><fmt:message key="synchronization_schedule.days_of_week.${dow}"/></label>
                </nobr>
              </li>
            </c:forTokens>
          </ul>
        </td>
      </tr>
    </table>
  </div>

  <div id="intervalDiv" style="display:none;width:100%">
    <table class="form" cellpadding="0" cellspacing="0">
      <tr>
        <td class="label"></td>
        <td>
          <fmt:message key="synchronization_schedule.time.interval">
            <fmt:param>
              <d:input bean="SynchronizationRuleFormHandler.period" name="period"
                       id="syncTaskScheduleBean.period" type="text" iclass="textField number"/>
            </fmt:param>
            <fmt:param>
              <d:select bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.timePeriod" id="syncTaskScheduleBean.timePeriod"
                        iclass="small" name="syncTaskScheduleBean.timePeriod" onclick="setIntervalOption(this);">
                <d:option value="1">
                  <fmt:message key="synchronization_schedule.time.interval.1"/>
                </d:option>
                <d:option value="2">
                  <fmt:message key="synchronization_schedule.time.interval.2"/>
                </d:option>
                <d:option value="3">
                  <fmt:message key="synchronization_schedule.time.interval.3"/>
                </d:option>
              </d:select>
            </fmt:param>
          </fmt:message>
        </td>
      </tr>
      <tr>
        <td class="label"></td>
        <td>
          <div id="betweenDiv" style="display:none;">
            <fmt:message var="timeOnlyFormat" key="timeOnlyFormat"/>
            <fmt:message key="synchronization_schedule.time.between">
              <fmt:param>
                <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.betweenStart"
                         iclass="textField number"/>
              </fmt:param>
              <fmt:param>
                <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.betweenEnd"
                         iclass="textField number"/>
              </fmt:param>
            </fmt:message>
          </div>
        </td>
      </tr>
    </table>
  </div>

  <div id="endScheduleDiv" style="display:none;">
    <table class="form" cellpadding="0" cellspacing="0">
      <tr>
        <td class="label">
          <fmt:message key="synchronization_schedule.end_schedule"/>
        </td>
        <td>
          <ul>
            <li>
              <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.endSchedule" id="endSchedule0"
                       type="radio" name="syncTaskScheduleBean.endSchedule" value="0"/>
              <label for="endSchedule0"><fmt:message key="synchronization_schedule.no_end"/></label>
            </li>
            <li>
              <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.endSchedule" id="endSchedule1"
                       type="radio" name="syncTaskScheduleBean.endSchedule" value="1" />
              <fmt:message key="synchronization_schedule.after_n_occurrences">
                <fmt:param>
                  <d:input bean="SynchronizationRuleFormHandler.afterNOccurrences" name="afterNOccurrences"
                           type="text" iclass="textField number small" onfocus="setCheckBox(this, 'endSchedule1');"
                           onchange="setCheckBox(this, 'endSchedule1');"/>
                </fmt:param>
              </fmt:message>
            </li>
            <li>
              <d:input bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.endSchedule" id="endSchedule2"
                       type="radio" name="syncTaskScheduleBean.endSchedule" value="2"/>
              <fmt:message key="synchronization_schedule.after_end_date">
                <fmt:param>
                  <%-- Get the current date value as an ISO8601/RFC3339 string, in order to initialize the DateTextBox --%>
                  <fmt:formatDate var="dateValue" value="${scheduleHandler.syncTaskScheduleBean.afterEndDateTimeAsDate}" pattern="yyyy-MM-dd"/>

                  <d:input type="hidden" id="afterEndDateHidden" name="syncTaskScheduleBean.afterEndDate"
                      bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.afterEndDate"
                      onchange="setCheckBox(this, 'endSchedule2');" />
                  <input type="text" id="afterEndDate" class="textField date" value="${dateValue}"
                      setValueToId="afterEndDateHidden" dojoType="atg.searchadmin.DateTextBox" lang="${dojoLang}"
                      constraints="{datePattern: '${dateFormat}'}" onfocus="setCheckBox(this, 'endSchedule2');" />

                  <d:input type="text" id="afterEndTime" iclass="textField small" name="syncTaskScheduleBean.afterEndTime"
                        bean="SynchronizationRuleFormHandler.syncTaskScheduleBean.afterEndTime"
                        onfocus="setCheckBox(this, 'endSchedule2');" onchange="setCheckBox(this, 'endSchedule2');" />
                </fmt:param>
              </fmt:message>
            </li>
          </ul>
        </td>
      </tr>
    </table>
  </div>

  <%-- after first page load--%>
  <script type="text/javascript">setScheduleOption(document.getElementById("scheduleSet"));</script>

  <span class="seperator"></span>

  <h3>
    <d:input bean="SynchronizationRuleFormHandler.syncBy" name="syncBy" id="syncBy.1" type="radio" value="1"/>
    <label for="syncBy.1"><fmt:message key="synchronization_task_params.sync_by_request.title"/></label>
    <span class="ea"><tags:ea key="embedded_assistant.synchronization_schedule.sync_by_request_title" /></span>
  </h3>

  <table class="form" cellpadding="0" cellspacing="0">
    <tr>
      <td width="5%"></td>
      <td align="left">
        <fmt:message key="synchronization_task_params.sync_by_request.label"/>
      </td>
    </tr>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_schedule.jsp#2 $$Change: 651448 $--%>
