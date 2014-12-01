<%--
Jsp provides popup for exporting term from inspection to custom term dictionary

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/term_export_popup.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <c:set var="termName"><tags:i18GetParam paramName="termName"/></c:set>
  <d:getvalueof param="termPos" var="termPos" scope="page"/>
  <d:getvalueof param="termLanguage" var="termLanguage" scope="page"/>

  <admin-beans:getDictionaryByLanguageCaseIns language="${termLanguage}" var="dictionaries"/>

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler"/>
  <admin-ui:initializeFormHandler handler="${handler}" />

  <fmt:message key="active_project.ok.button" var="button_ok"/>
  <fmt:message key="active_project.ok.tooltip" var="tooltip_ok"/>

  <c:choose>
    <c:when test="${empty dictionaries}">

      <div class="content">
        <p>
          <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${termLanguage}"/>
          <fmt:message key="confirm_message.no.custom.dictionaries.defined">
            <fmt:param value="${localizedLanguage}"/>
          </fmt:message>
        </p>
      </div>
      <div class="footer" id="popupFooter">
        <input type="button" value="${button_ok}" onclick="closePopUp()" name="cancel" title="${tooltip_ok}"/>
      </div>

    </c:when>

    <c:otherwise>

      <d:form action="term_export_popup.jsp" method="POST">

        <div class="content">
          <p>
            <fmt:message key="dictionary_inspection.export_term_popup.dictionary.label">
              <fmt:param value="${termName}"/>
            </fmt:message>
          </p>
          <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termDictionaryComparator" var="comparator"/>
          <admin-ui:sort var="sortedDictionaries" items="${dictionaries}" comparator="${comparator}" sortMode="${sortValue}"/>
          <table cellpadding="0" cellspacing="0">
            <c:forEach items="${sortedDictionaries}" var="dict" varStatus="status">
              <tr>
                <td width="30"></td>
                <td>
                  <d:input type="radio" name="parentId" bean="TermDictionaryInspectionTermExportFormHandler.parentId" value="${dict.id}" id="parentId_${dict.id}" checked="${status.first}"/>
                  <label for="parentId_${dict.id}"><c:out value="${dict.name}"/></label>
                </td>
              </tr>
            </c:forEach>
          </table>
          <p>
            <fmt:message key="dictionary_inspection.export_term_popup.pos.label">
              <fmt:param value="${termName}"/>
            </fmt:message>
          </p>
          <table cellpadding="0" cellspacing="0">
            <c:forTokens items="${termPos}" delims="," var="pos" varStatus="status">
              <tr>
                <td width="30"></td>
                <td>
                  <d:input type="radio" name="partSpeech" bean="TermDictionaryInspectionTermExportFormHandler.partSpeech" value="${pos}" id="partSpeech_${pos}" checked="${status.first}"/>
                  <label for="partSpeech_${pos}"><c:out value="${pos}"/></label>
                </td>
              </tr>
            </c:forTokens>
          </table>
        </div>

        <div class="footer" id="popupFooter">
          <c:url var="errorURL" value="${dictionaryPath}/inspection/term_export_popup.jsp"/>
          <c:url var="successURL" value="${dictionaryPath}/term_create.jsp"/>

          <d:input type="hidden" value="${errorURL}" name="errorURL"
                   bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler.errorURL" />
          <d:input type="hidden" value="${successURL}" name="successURL"
                   bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler.successURL" />
          <d:input type="hidden" value="${termName}" name="termName"
                   bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler.term" />
          <d:input type="hidden" value="${termLanguage}" name="language"
                   bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermExportFormHandler.language"/>

          <d:input type="submit" bean="TermDictionaryInspectionTermExportFormHandler.exportTerm" name="exportTerm"
                   value="${button_ok}" title="${tooltip_ok}" iclass="formsubmitter"
                   id="exportTermButton"/>
          <input type="button" value="<fmt:message key='active_project.cancel.button'/>"
                 onclick="closePopUp()" name="cancel"
                 title="<fmt:message key='active_project.cancel.tooltip'/>"/>
        </div>
      </d:form>

    </c:otherwise>
  </c:choose>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/term_export_popup.jsp#1 $$Change: 651360 $--%>
