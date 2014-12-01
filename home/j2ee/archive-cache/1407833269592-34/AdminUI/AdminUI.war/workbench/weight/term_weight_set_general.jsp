<%--
  JSP, showing "general" tab of create or update term weight set page. This page is included from term_weight_set.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler"/>

  <h3>
    <fmt:message key="term_weight.general.basics"/>
    <span class="ea"><tags:ea key="embedded_assistant.term_weight_set_general.title" /></span>
  </h3>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="termNameAlert">
            <span class="required"><fmt:message key="project_general.required_field"/></span>
          </span>
          <label for="termName">
            <fmt:message key="term_weight.general.name"/>
          </label>
        </td>
        <td>
          <%-- Term weight set name --%>
          <d:input type="text" id="termName" name="termName" iclass="textField"
                   bean="ManageTermWeightSetFormHandler.termName"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <label for="termDescription">
            <span id="termDescriptionAlert"></span>
            <fmt:message key="term_weight.general.description"/>
          </label>
        </td>
        <td>
          <%-- Term weight set description --%>
          <d:input type="text"
                   id="termDescription" iclass="textField"
                   name="termDescription"
                   bean="ManageTermWeightSetFormHandler.termDescription"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="term_weight_sets.language"/>
        </td>
        <td>
          <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                        var="allLanguages"/>
          <d:select id="language" iclass="small" bean="ManageTermWeightSetFormHandler.language" name="language">
            <c:forEach items="${allLanguages}" var="currentLanguage">
              <admin-ui:option value="${currentLanguage}">
                <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
                ${localizedLanguage}
              </admin-ui:option>
            </c:forEach>
          </d:select>
        </td>
      </tr>

    </tbody>
  </table>

  <h3>
    <fmt:message key="term_weight.general.thresholds"/>
  </h3>

  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" var="activeProjectId"/>

  <admin-beans:getTPOSetByProjectId var="tpoSet" activeProjectId="${activeProjectId}"/>
  <admin-beans:getOptionValues var="stopWordThreshold" textProcessingOptionsSet="${tpoSet}"
                               optionName="indexStopThresh"/>
  <c:set var="stopWordThreshold" value="${stopWordThreshold[0]}"/>
  <admin-beans:getOptionValues var="termRetrievalThreshold" textProcessingOptionsSet="${tpoSet}"
                               optionName="indexTermThresh"/>
  <c:set var="termRetrievalThreshold" value="${termRetrievalThreshold[0]}"/>

  <c:choose>
    <c:when test="${activeProjectId == null || activeProjectId == ''}">
      <fmt:message key="term_weight.general.thresholds.no_active_project" var="tpoName"/>
    </c:when>
    <c:when test="${tpoSet == null}">
      <fmt:message key="content.create.indexing_options.tpo.default" var="tpoName"/>
    </c:when>
    <c:otherwise>
      <c:url value="${tpoPath}/tpo_index_edit_set.jsp" var="tpoUrl">
        <c:param name="tpoSetId" value="${tpoSet.id}"/>
      </c:url>
      <c:set var="tpoName">
        <a href="${tpoUrl}" title="<c:out value="${tpoSet.name}"/>" onclick="return loadRightPanel(this.href);">
          <c:out value="${tpoSet.name}"/>
        </a>
      </c:set>
    </c:otherwise>
  </c:choose>

  <%-- Getting Stop Word Threshold and Term Retrieval Threshold value --%>
  <script type="text/javascript">
    var stopWordThresholdVar = <c:out value="${stopWordThreshold}"/>;
    var termRetrievalThresholdVar = <c:out value="${termRetrievalThreshold}"/>;
  </script>

  <p>
    <c:choose>
      <c:when test="${empty activeProjectId}">
        <fmt:message key="term_weight.general.thresholds.head"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="term_weight.general.thresholds.head.active"/>
      </c:otherwise>
    </c:choose>
  </p>
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <fmt:message key="term_weight.general.thresholds.tpo_set"/>
        </td>
        <td>
          <div class="tpoName">
            <c:out value="${tpoName}" escapeXml="false"/>
          </div>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="term_weight.general.thresholds.stop_word"/>
        </td>
        <td>
          <div class="stopWordThresholdValue">
            <c:out value="${stopWordThreshold}"/>
          </div>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="term_weight.general.thresholds.term_retrieval"/>
        </td>
        <td>
          <div class="termRetrievalThresholdValue">
            <c:out value="${termRetrievalThreshold}"/>
          </div>
        </td>
      </tr>
    </tbody>
  </table>

  <d:include src="term_weight_set_thresholds_table.jsp">
    <d:param name="stopWordThreshold" value="${stopWordThreshold}"/>
    <d:param name="termRetrievalThreshold" value="${termRetrievalThreshold}"/>
  </d:include>

  <d:include src="term_weight_set_weights.jsp">
    <d:param name="stopWordThreshold" value="${stopWordThreshold}"/>
    <d:param name="termRetrievalThreshold" value="${termRetrievalThreshold}"/>
  </d:include>

  <d:input bean="ManageTermWeightSetFormHandler.refresh" type="submit" id="refreshButton" 
           style="display:none" iclass="formsubmitter"/>
  <script type="text/javascript">
    function refreshRightPane(obj) {
      document.getElementById("refreshButton").click();
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_general.jsp#2 $$Change: 651448 $--%>
