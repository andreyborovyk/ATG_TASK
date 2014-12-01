<%--
Content Inspection page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof var="projectId" param="projectId" />
  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}" />
  </admin-ui:initializeFormHandler> 
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client"
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" projectId="${projectId}" />
      <c:if test="${syncTaskHistoryInfo == null or empty handler.environments}">
        <p><fmt:message key="content_inspection.unavailable" /></p>
        <p><fmt:message key="project_index_review.unavailable" /></p>
      </c:if>
      <c:if test="${syncTaskHistoryInfo != null and not empty handler.environments}">

        <d:form action="project_content_inspection.jsp" method="post">
          <c:url var="successUrl" value="/searchadmin/project_content_inspection.jsp">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <d:input type="hidden" bean="ContentInspectionFormHandler.errorURL" value="${successUrl}" name="errorUrl"/>
          <d:input type="hidden" bean="ContentInspectionFormHandler.successURL" value="${successUrl}" name="successUrl"/>

          <br/>
          <table class="form" cellspacing="0" cellpadding="0">
            <tbody>
              <tr>
                <td width="7%">
                  <fmt:message key="content_inspection.environment"/>
                </td>
                <td align="left">
                  <d:select bean="ContentInspectionFormHandler.environmentName" iclass="small unchanged"
                      name="environmentName" onchange="document.getElementById('changeEnvironmentButton').click();">
                    <c:forEach items="${handler.environments}" var="environment">
                      <c:if test="${handler.environmentName == environment.envName}">
                        <c:set var="currentEnvironment" value="${environment}" />
                      </c:if>
                      <admin-ui:option value="${environment.envName}">
                        <c:out value="${environment.envName}" />
                      </admin-ui:option>
                    </c:forEach>
                  </d:select>
                  <d:input style="display: none;" type="submit" name="changeEnvironment" id="changeEnvironmentButton"
                      iclass="formsubmitter" bean="ContentInspectionFormHandler.changeEnvironment" />
                </td>
                <td>
                  <admin-beans:getSyncTaskByEnvironment environment="${currentEnvironment}" var="currentSyncTask"/>
                  <fmt:message key="content_inspection.last_index"/>
                  <fmt:message var="timeFormat" key="timeFormat"/>
                  <fmt:formatDate value="${currentSyncTask.currentEndDate}" pattern="${timeFormat}"/>
                  (<fmt:message key="synchronization.task.${currentSyncTask.syncTaskType}" />)
                  <span class="ea"><tags:ea key="embedded_assistant.content_inspection_environment" /></span>
                </td>
              </tr>
            </tbody>
          </table>

          <fmt:message var="addButton" key="content_inspection.button.add" />
          <fmt:message var="addTooltip" key="content_inspection.button.add.tooltip" />
          <fmt:message var="delButton" key="content_inspection.button.delete" />
          <fmt:message var="delTooltip" key="content_inspection.button.delete.tooltip" />
          <c:set var="moreOptionsStyle"><c:if test="${not handler.moreOptions}">style="display: none"</c:if></c:set>
          <div class="inputPanel">
            <table class="form" cellspacing="0" cellpadding="0" id="formTable">
              <tbody>
                <tr>
                  <td class="checkboxColumn">
                    <fmt:message key="content_inspection.enable.constraint" />
                  </td>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <c:set var="lastTermIndex" value="${fn:length(handler.term) - 1}" />
                <c:forEach begin="0" end="${lastTermIndex}" varStatus="termStatus">
                  <tr>
                    <td class="checkboxColumn">
                      <tags:checkbox name="termEnabled" checked="${handler.termEnabled[termStatus.index]}"
                          beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                    </td>
                    <td class="labelColumn"><span><fmt:message key="content_inspection.term" /></span></td>
                    <td colspan="2">
                      <d:input type="text" iclass="textField halved unchanged" name="term" id="termElement"
                               bean="ContentInspectionFormHandler.term" value="${handler.term[termStatus.index]}" />
                      &nbsp;
                      <tags:checkbox name="termAllForms" checked="${handler.termAllForms[termStatus.index]}"
                          id="allForms_${termStatus.index}"
                          beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                      <label for="allForms_${termStatus.index}"><fmt:message key="content_inspection.all_forms" /></label>
                      &nbsp;
                      <c:if test="${lastTermIndex > 0}">
                        <input type="button" value="${delButton}" class="tableButton" title="${delTooltip}"
                            onclick="deleteTerm(${termStatus.index});" />
                      </c:if>
                      <c:if test="${termStatus.index == lastTermIndex}">
                        <input type="button" value="${addButton}" class="tableButton" title="${addTooltip}"
                            onclick="addTerm();" />
                      </c:if>
                      <c:if test="${termStatus.index == 0}">
                        <span class="ea"><tags:ea key="embedded_assistant.content_inspection_term" /></span>
                      </c:if>
                    </td>
                  </tr>
                </c:forEach>
                <tr>
                  <td class="checkboxColumn">
                    <tags:checkbox name="languageEnabled" checked="${handler.languageEnabled}"
                        beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                  </td>
                  <td class="labelColumn"><span><fmt:message key="content_inspection.language" /></span></td>
                  <td colspan="2">
                    <d:select bean="ContentInspectionFormHandler.language" name="language" iclass="small unchanged">
                      <d:option value="">
                        <fmt:message key="content_inspection.language.all" />
                      </d:option>
                      <c:forEach items="${handler.languageNames}" var="language">
                        <admin-ui:option value="${language}">
                          <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${language}"/>
                          ${localizedLanguage}
                        </admin-ui:option>
                      </c:forEach>
                    </d:select>
                  </td>
                </tr>
                <c:set var="lastPropertyIndex" value="${fn:length(handler.propertyEnabled) - 1}" />
                <c:forEach begin="0" end="${lastPropertyIndex}" varStatus="propertyStatus">
                  <tr>
                    <td class="checkboxColumn">
                      <tags:checkbox name="propertyEnabled" checked="${handler.propertyEnabled[propertyStatus.index]}"
                          beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                    </td>
                    <td class="labelColumn"><span><fmt:message key="content_inspection.property" /></span></td>
                    <td colspan="2">
                      <c:set var="propertyName" value="${handler.property[propertyStatus.index]}" />
                      <c:set var="propertyValue" value="${handler.propertyValue[propertyStatus.index]}" />
                      <d:select bean="ContentInspectionFormHandler.property" name="property" iclass="small unchanged"
                          onchange="document.getElementById('propertyValue.${propertyStatus.index}').disabled = this.value == '';">
                        <d:option><fmt:message key="content_inspection.select" /></d:option>
                        <c:forEach var="property" items="${handler.propertyNames}">
                          <admin-ui:option value="${property}" selected="${property == propertyName}">
                            <c:out value="${property}" />
                          </admin-ui:option>
                        </c:forEach>
                      </d:select>
                      &nbsp;
                      <nobr>
                        <fmt:message key="content_inspection.value" />
                        <input type="text" class="textField halved unchanged" id="propertyValue.${propertyStatus.index}"
                            value="<c:out value='${propertyValue}' />"
                            <c:if test="${empty propertyName}">disabled="true"</c:if>
                            onchange="document.getElementById(this.id + '.hidden').value = this.value;" />
                        <d:input type="hidden" value="${propertyValue}"
                                 bean="ContentInspectionFormHandler.propertyValue"
                                 name="propertyValue" id="propertyValue.${propertyStatus.index}.hidden" />
                        &nbsp;
                        <c:if test="${lastPropertyIndex > 0}">
                          <input type="button" value="${delButton}" class="tableButton" title="${delTooltip}"
                              onclick="deleteProperty(${propertyStatus.index});" />
                        </c:if>
                        <c:if test="${propertyStatus.index == lastPropertyIndex}">
                          <input type="button" value="${addButton}" class="tableButton" title="${addTooltip}"
                              onclick="addProperty();" />
                        </c:if>
                      </nobr>
                      <c:if test="${propertyStatus.index == 0}">
                        <span class="ea"><tags:ea key="embedded_assistant.content_inspection_property" /></span>
                      </c:if>
                    </td>
                  </tr>
                </c:forEach>
                <tr class="moreOptions" ${moreOptionsStyle}>
                  <td class="checkboxColumn">&nbsp;</td>
                  <td class="labelColumn">&nbsp;</td>
                  <td colspan="2">
                    <d:input type="radio" id="contentType_unstructured" value="unstructured" name="contentType"
                             bean="ContentInspectionFormHandler.contentType" iclass="unchanged"/>
                    <label for="contentType_unstructured"><fmt:message key="content_inspection.radio.unstructured" /></label>
                    &nbsp;
                    <d:input type="radio" id="contentType_structured" value="structured" name="contentType"
                             bean="ContentInspectionFormHandler.contentType" iclass="unchanged"/>
                    <label for="contentType_structured"><fmt:message key="content_inspection.radio.structured" /></label>
                    &nbsp;
                    <d:input type="radio" id="contentType_both" value="both" name="contentType"
                             bean="ContentInspectionFormHandler.contentType" iclass="unchanged"/>
                    <label for="contentType_both"><fmt:message key="content_inspection.radio.both" /></label>
                  </td>
                </tr>
                <c:set var="lastDocsetIndex" value="${fn:length(handler.docsetEnabled) - 1}" />
                <c:forEach begin="0" end="${lastDocsetIndex}" varStatus="docsetStatus">
                  <tr class="moreOptions" ${moreOptionsStyle}>
                    <td class="checkboxColumn">
                      <tags:checkbox name="docsetEnabled" checked="${handler.docsetEnabled[docsetStatus.index]}"
                          beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                    </td>
                    <td class="labelColumn"><span><fmt:message key="content_inspection.document_set" /></span></td>
                    <td colspan="2">
                      <table class="browseField" cellpadding="0" cellspacing="0"><tr>
                        <td width="100%">
                          <d:input type="text" iclass="textField unchanged"
                                   value="${handler.docset[docsetStatus.index]}"
                                   bean="ContentInspectionFormHandler.docset" name="docset" />
                        </td>
                        <td nowrap="">
                          <c:if test="${lastDocsetIndex > 0}">
                            <input type="button" value="${delButton}" class="tableButton" title="${delTooltip}"
                                   onclick="deleteDocset(${docsetStatus.index});" />
                          </c:if>
                          <input type="button" value="${addButton}" class="tableButton" title="${addTooltip}"
                                 onclick="addDocset();"
                                 <c:if test="${docsetStatus.index lt lastDocsetIndex}">style="visibility:hidden;"</c:if> />
                          <c:if test="${docsetStatus.index == 0}">
                            <span class="ea"><tags:ea key="embedded_assistant.content_inspection_docset" /></span>
                          </c:if>
                        </td>
                      </tr></table>
                    </td>
                  </tr>
                </c:forEach>
                <tr class="moreOptions" ${moreOptionsStyle}>
                  <td class="checkboxColumn">
                    <tags:checkbox name="urlEnabled" checked="${handler.urlEnabled}"
                        beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                  </td>
                  <td class="labelColumn"><span><fmt:message key="content_inspection.url" /></span></td>
                  <td colspan="2">
                    <d:input type="text" iclass="textField unchanged" name="url" bean="ContentInspectionFormHandler.url" />
                    <span class="ea"><tags:ea key="embedded_assistant.content_inspection_url" /></span>
                  </td>
                </tr>
                <tr class="moreOptions" ${moreOptionsStyle}>
                  <td class="checkboxColumn">
                    <tags:checkbox name="docFormatEnabled" checked="${handler.docFormatEnabled}"
                        beanName="/atg/searchadmin/adminui/formhandlers/ContentInspectionFormHandler" />
                  </td>
                  <td class="labelColumn"><span><fmt:message key="content_inspection.document_format" /></span></td>
                  <td colspan="2">
                    <%-- TODO localize --%>
                    <d:select bean="ContentInspectionFormHandler.docFormat" name="docFormat" iclass="small unchanged">
                      <d:option><fmt:message key="content_inspection.document_format.all" /></d:option>
                      <c:forEach var="format" items="${handler.docFormats}">
                        <d:option value="${format}">${format}</d:option>
                      </c:forEach>
                    </d:select>
                  </td>
                </tr>
                <tr>
                  <td class="checkboxColumn">&nbsp;</td>
                  <td class="labelColumn">&nbsp;</td>
                  <td colspan="2">
                    <a id="moreOptionsLink" href="#" onclick="return more(true);"
                        <c:if test="${handler.moreOptions}">style="display: none"</c:if>><fmt:message key="content_inspection.more" /></a>
                    <a id="fewerOptionsLink" href="#" onclick="return more(false);" 
                        ${moreOptionsStyle}><fmt:message key="content_inspection.fewer" /></a>
                  </td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td>&nbsp;</td>
                  <td class="labelColumn"><span><fmt:message key="content_inspection.button.results.display"/></span></td>
                  <td style="vertical-align: top;">
                    <d:select bean="ContentInspectionFormHandler.resultProperty" name="resultProperty" iclass="small unchanged">
                      <c:forEach var="property" items="${handler.propertyNames}">
                        <admin-ui:option value="${property}"><c:out value="${property}" /></admin-ui:option>
                        <c:set var="selProperty" value="${(property == handler.resultProperty) ? property : selProperty}" />
                      </c:forEach>
                      <c:forEach var="property" items="${handler.additionalResultProperties}">
                        <fmt:message key="content_inspection.property.${property}" var="dispProperty" />
                        <c:set var="missedProperty" value="???content_inspection.property.${property}???" />
                        <c:if test="${dispProperty == missedProperty}">
                          <c:set var="dispProperty" value="${property}" />
                        </c:if>
                        <admin-ui:option value="${property}">${dispProperty}</admin-ui:option>
                        <c:set var="selProperty" value="${(empty selProperty and property == handler.resultProperty) ? dispProperty : selProperty}" />
                      </c:forEach>
                    </d:select>
                  </td>
                  <td align="right">
                    <fmt:message var="addButtonLabel" key="content_inspection.button.search"/>
                    <fmt:message var="addButtonToolTip" key="content_inspection.button.search.tooltip"/>
                    <d:input type="submit" value="${addButtonLabel}" title="${addButtonToolTip}"
                        bean="ContentInspectionFormHandler.search" name="search" iclass="formsubmitter"/>
                    <fmt:message var="clearButtonLabel" key="content_inspection.button.clear"/>
                    <fmt:message var="clearButtonToolTip" key="content_inspection.button.clear.tooltip"/>
                    <d:input type="submit" value="${clearButtonLabel}" title="${clearButtonToolTip}"
                        bean="ContentInspectionFormHandler.clear" name="clear" iclass="formsubmitter"/>
                  </td>
                </tr>
              </tfoot>
            </table>
            <d:input type="hidden" bean="ContentInspectionFormHandler.deleteIndex" name="deleteIndex" id="deleteIndexField" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.deleteTerm"
                name="deleteTermF" iclass="formsubmitter" id="deleteTermButton" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.addTerm" 
                name="addTermF" iclass="formsubmitter" id="addTermButton" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.deleteProperty" 
                name="deletePropertyF" iclass="formsubmitter" id="deletePropertyButton" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.addProperty" 
                name="addPropertyF" iclass="formsubmitter" id="addPropertyButton" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.deleteDocset" 
                name="deleteDocsetF" iclass="formsubmitter" id="deleteDocsetButton" />
            <d:input style="display: none;" type="submit" bean="ContentInspectionFormHandler.addDocset" 
                name="addDocsetF" iclass="formsubmitter" id="addDocsetButton" />
            <d:input type="hidden" bean="ContentInspectionFormHandler.moreOptions" name="moreOptionsF" id="moreOptionsField" />
          </div>
          <br/>
          <c:if test="${handler.results != null}">
            <c:choose>
              <c:when test="${not empty handler.results}">
                <d:include page="project_content_inspection_result.jsp">
                  <d:param name="handler" value="${handler}"/>
                  <d:param name="resultProperty" value="${selProperty}"/>
                </d:include>
              </c:when>
              <c:otherwise>
                <fmt:message key="content_inspection.documents.no_results" />
                <c:if test="${fn:length(handler.details) > 0}">
                  <p>
                    <b><fmt:message key="content_inspection.documents.no_results.details" /></b>
                    <c:forEach items="${handler.details}" var="detail">
                      <br/><c:out value="${detail}" />
                    </c:forEach>
                  </p>
                </c:if>
              </c:otherwise>
            </c:choose>
          </c:if>
        </d:form>
        <br/>
        <script type="text/javascript">
          function more(display) {
            var table = document.getElementById("formTable");
            for (var i = 0; i < table.rows.length; i++) {
              if (table.rows[i].className == "moreOptions") {
                table.rows[i].style.display = display ? "" : "none";
              }
            }
            document.getElementById("moreOptionsLink").style.display = display ? "none" : "";
            document.getElementById("fewerOptionsLink").style.display = display ? "" : "none";
            document.getElementById("moreOptionsField").value = display;
            return false;
          }
          function deleteTerm(index) {
            document.getElementById("deleteIndexField").value = index;
            document.getElementById("deleteTermButton").click();
          }
          function addTerm() {
            document.getElementById("addTermButton").click();
          }
          function deleteProperty(index) {
            document.getElementById("deleteIndexField").value = index;
            document.getElementById("deletePropertyButton").click();
          }
          function addProperty() {
            document.getElementById("addPropertyButton").click();
          }
          function deleteDocset(index) {
            document.getElementById("deleteIndexField").value = index;
            document.getElementById("deleteDocsetButton").click();
          }
          function addDocset() {
            document.getElementById("addDocsetButton").click();
          }
          function focusFormElement() {
            document.getElementById("termElement").focus();
          }
        </script>

      </c:if>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection.jsp#1 $$Change: 651360 $--%>
