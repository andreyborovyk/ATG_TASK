<%--
Shows TPOs and language of the current active project
Used by Term Lookup and Text Processing of Content tabs

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/active_project_tpo.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="handlerName" var="handlerName"/>
  <d:getvalueof param="hoverKey" var="hoverKey"/>
  <d:importbean var="handler" bean="${handlerName}"/>

  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <fmt:message key="dictionary_inspection.active_project_tpo.project_tpo_set"/>
        </td>
        <td>
          <c:set var="activeProjectTPOSet" value="${handler.activeSearchProject.activeProjectTPOSet}" />
          <c:choose>
            <c:when test="${not empty activeProjectTPOSet}">
              <c:out value="${activeProjectTPOSet.name}" />
            </c:when>
            <c:otherwise>
              <fmt:message key="content.create.indexing_options.tpo.default" />
            </c:otherwise>
          </c:choose>
          <span class="ea"><tags:ea key="${hoverKey}"/></span>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="dictionary_inspection.active_project_tpo.content_tpo_set"/>
        </td>
        <td>
          <c:if test="${empty handler.contentTPOSets}">
            <fmt:message key="dictionary_inspection.active_project_tpo.no_tpos"/>
          </c:if>
          <c:if test="${not empty handler.contentTPOSets}">
            <d:select id="selectedContentTPOId" iclass="small" bean="${handlerName}.selectedContentTPOId"
                name="selectedContentTPOId" onchange="document.getElementById('contentTPOChangeButton').click()">
              <d:option><fmt:message key="dictionary_inspection.active_project_tpo.no_tpos"/></d:option>
              <c:forEach items="${handler.contentTPOSets}" var="contentTPOSet">
                <d:option value="${contentTPOSet.id}">
                  ${contentTPOSet.name}
                </d:option>
              </c:forEach>
            </d:select>
            <d:input bean="${handlerName}.contentTPOChange" name="contentTPOChange" id="contentTPOChangeButton"
                     type="submit" style="display:none" iclass="formsubmitter"/>
          </c:if>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="dictionary_inspection.active_project_tpo.search_language"/>
        </td>
        <td>
          <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${handler.language}"/>
          ${localizedLanguage}
          <d:input type="hidden" bean="${handlerName}.language" id="languageInput" name="language" />
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/active_project_tpo.jsp#2 $$Change: 651448 $--%>
